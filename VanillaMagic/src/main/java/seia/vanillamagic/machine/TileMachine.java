package seia.vanillamagic.machine;

import java.util.List;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import seia.vanillamagic.utils.BlockPosHelper;
import seia.vanillamagic.utils.NBTHelper;
import seia.vanillamagic.utils.SmeltingHelper;

public abstract class TileMachine extends TileEntity implements IMachine
{
	public static final String REGISTRY_NAME = "TileEntityMachine";
	
	protected EntityPlayer player;
	protected BlockPos machinePos;
	protected BlockPos workingPos;
	protected BlockPos startPos;
	protected ItemStack shouldBeInLeftHand;
	protected ItemStack shouldBeInRightHand;
	protected int radius = 4;
	protected int oneOperationCost = 400;
	protected int ticks = 0;
	protected int maxTicks = 4000;
	protected boolean isActive = false;
	protected boolean finished = false;
	protected int delayInTicks = 0;
	
	private String NBT_MACHINE_POS_X = "NBT_MACHINE_POS_X";
	private String NBT_MACHINE_POS_Y = "NBT_MACHINE_POS_Y";
	private String NBT_MACHINE_POS_Z = "NBT_MACHINE_POS_Z";
	private String NBT_WORKING_POS_X = "NBT_WORKING_POS_X";
	private String NBT_WORKING_POS_Y = "NBT_WORKING_POS_Y";
	private String NBT_WORKING_POS_Z = "NBT_WORKING_POS_Z";
	private String NBT_RADIUS = "NBT_RADIUS";
	private String NBT_ONE_OPERATION_COST = "NBT_ONE_OPERATION_COST";
	private String NBT_TICKS = "NBT_TICKS";
	private String NBT_MAX_TICKS = "NBT_MAX_TICKS";
	private String NBT_IS_ACTIVE = "NBT_IS_ACTIVE";

	/**
	 * This should check if the Machine is build correctly.
	 */
	public abstract boolean checkSurroundings();
	
	/**
	 * This method is for a Machine work.
	 * Each Machine have to know when to decrease ticks.
	 * For instance: I don't want Quarry to decrease ticks if it hits Air or Bedrock. <br>
	 * In this method Machine should, for instance: move workingPos.
	 */
	public abstract void doWork();
	
	/**
	 * This method is used to check if the output inventory has space for more items.
	 */
	public abstract boolean inventoryOutputHasSpace();
	
	int delay = 0;
	public void update()
	{
		if(delay >= delayInTicks)
		{
			delay = 0;
			if(worldObj.getChunkFromBlockCoords(machinePos).isLoaded())
			{
				if(worldObj.getChunkFromBlockCoords(getWorkingPos()).isLoaded())
				{
					if(checkSurroundings())
					{
						showBoundingBox();
						checkFuel();
						if(isNextToOutput())
						{
							if(inventoryOutputHasSpace())
							{
								isActive = true;
								doWork();
								return;
							}
						}
						else
						{
							isActive = true;
							doWork();
							return;
						}
					}
				}
			}
			isActive = false;
		}
		else
		{
			delay++;
		}
	}
	
	public TileEntity getTileEntity()
	{
		return this;
	}
	
	/**
	 * Additional method for showing the box on which Machine is operating.
	 */
	public void showBoundingBox()
	{
	}
	
	public boolean finishedWork()
	{
		return finished;
	}

	public boolean isNextToOutput()
	{
		return getOutputInventory() != null ? true : false;
	}

	public EntityPlayer getPlayerWhoPlacedMachine() 
	{
		return player;
	}

	public BlockPos getMachinePos() 
	{
		return machinePos;
	}

	public void setMachinePos(BlockPos newPos) 
	{
		this.machinePos = newPos;
	}

	public BlockPos getWorkingPos() 
	{
		return workingPos;
	}

	public void setWorkingPos(BlockPos newPos) 
	{
		this.workingPos = newPos;
	}
	
	public BlockPos getStartPos()
	{
		return startPos;
	}
	
	public void setNewStartPos(BlockPos newStartPos)
	{
		this.startPos = newStartPos;
	}

	public int getWorkRadius() 
	{
		return radius;
	}
	
	public void setWorkRadius(int newRadius)
	{
		this.radius = newRadius;
	}

	public int getOneOperationCost() 
	{
		return oneOperationCost;
	}

	public int getCurrentTicks() 
	{
		return ticks;
	}

	public int getMaxTicks() 
	{
		return maxTicks;
	}

	public boolean isActive() 
	{
		return isActive;
	}
	
	/**
	 * Try to override serializeNBT instead of this method.
	 */
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
		try
		{
			super.writeToNBT(compound);
			compound = NBTHelper.writeToINBTSerializable(this, compound);
			return compound;
		}
		catch(Exception e)
		{
			System.out.println("Error while writing NBT from TileEntityMachine at:");
			BlockPosHelper.printCoords(machinePos);
		}
		return null;
    }
	
	public NBTTagCompound serializeNBT()
	{
		NBTTagCompound compound = new NBTTagCompound();
		compound.setInteger(NBT_MACHINE_POS_X, machinePos.getX());
		compound.setInteger(NBT_MACHINE_POS_Y, machinePos.getY());
		compound.setInteger(NBT_MACHINE_POS_Z, machinePos.getZ());
		compound.setInteger(NBT_WORKING_POS_X, workingPos.getX());
		compound.setInteger(NBT_WORKING_POS_Y, workingPos.getY());
		compound.setInteger(NBT_WORKING_POS_Z, workingPos.getZ());
		compound.setInteger(NBT_RADIUS, radius);
		compound.setInteger(NBT_ONE_OPERATION_COST, oneOperationCost);
		compound.setInteger(NBT_TICKS, ticks);
		compound.setInteger(NBT_MAX_TICKS, maxTicks);
		compound.setBoolean(NBT_IS_ACTIVE, isActive);
		return compound;
	}
	
	/**
	 * Try to override deserializeNBT instead of this method.
	 */
	public void readFromNBT(NBTTagCompound compound)
    {
		try
		{
			super.readFromNBT(compound);
			NBTHelper.readFromINBTSerializable(this, compound);
		}
		catch(Exception e)
		{
			System.out.println("Error while reading NBT to TileEntityMachine at:");
			BlockPosHelper.printCoords(machinePos);
		}
    }
	
	public void deserializeNBT(NBTTagCompound compound)
	{
		int machinePosX = compound.getInteger(NBT_MACHINE_POS_X);
		int machinePosY = compound.getInteger(NBT_MACHINE_POS_Y);
		int machinePosZ = compound.getInteger(NBT_MACHINE_POS_Z);
		BlockPos machinePos = new BlockPos(machinePosX, machinePosY, machinePosZ);
		this.machinePos = machinePos;
		int workingPosX = compound.getInteger(NBT_WORKING_POS_X);
		int workingPosY = compound.getInteger(NBT_WORKING_POS_Y);
		int workingPosZ = compound.getInteger(NBT_WORKING_POS_Z);
		BlockPos workingPos = new BlockPos(workingPosX, workingPosY, workingPosZ);
		this.workingPos = workingPos;
		this.radius = compound.getInteger(NBT_RADIUS);
		this.oneOperationCost = compound.getInteger(NBT_ONE_OPERATION_COST);
		this.ticks = compound.getInteger(NBT_TICKS);
		this.maxTicks = compound.getInteger(NBT_MAX_TICKS);
		this.isActive = compound.getBoolean(NBT_IS_ACTIVE);
	}
	
	public boolean hasInputInventory()
	{
		return getInputInventory() != null ? true : false;
	}
	
	public void decreaseTicks()
	{
		ticks -= oneOperationCost;
	}
	
	public void checkFuel()
	{
		try
		{
			if(isNextToOutput())
			{
				if(!inventoryOutputHasSpace())
				{
					return;
				}
			}
			if(ticks >= maxTicks)
			{
				return;
			}
			if(ticks >= oneOperationCost)
			{
				return;
			}
			
			if(hasInputInventory())
			{
				ItemStack fuelToAdd = SmeltingHelper.getFuelFromInventoryAndDelete(getInputInventory());
				ticks += SmeltingHelper.countTicks(fuelToAdd);
			}
			else if(!hasInputInventory())
			{
				List<EntityItem> fuelsInCauldron = SmeltingHelper.getFuelFromCauldron(worldObj, machinePos);
				if(fuelsInCauldron.size() == 0)
				{
					return;
				}
				for(EntityItem entityItem : fuelsInCauldron)
				{
					ItemStack stack = entityItem.getEntityItem();
					ticks += SmeltingHelper.countTicks(stack);
					if(ticks >= oneOperationCost)
					{
						worldObj.removeEntity(entityItem);
					}
				}
			}
		}
		catch(Exception e)
		{
		}
	}
	
	public void endWork()
	{
	}
	
	public ItemStack getActivationStackLeftHand() 
	{
		return shouldBeInLeftHand;
	}
	
	public void setActivationStackLeftHand(ItemStack stack) 
	{
		this.shouldBeInLeftHand = stack;
	}
	
	public ItemStack getActivationStackRightHand() 
	{
		return shouldBeInRightHand;
	}
	
	public void setActivationStackRightHand(ItemStack stack) 
	{
		this.shouldBeInRightHand = stack;
	}
}