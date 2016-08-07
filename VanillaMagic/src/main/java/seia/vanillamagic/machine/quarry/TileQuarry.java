package seia.vanillamagic.machine.quarry;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCauldron;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import seia.vanillamagic.machine.TileEntityMachine;
import seia.vanillamagic.utils.InventoryHelper;

public class TileQuarry extends TileEntityMachine
{
	// It's (size)x(size) but (size-2)x(size-2) is for digging
	// ChunkNumber * BlocksInChunk + 2 blocks bounding
	public static final int BASIC_QUARRY_SIZE = 4 * 16 + 2;
	// Input side from the Quarry into IInventory (argument in methods)
	public static final EnumFacing INPUT_FACING = EnumFacing.NORTH;
	// The name for registry
	public static final String TILE_QUARRY_NAME = "tileQuarry";

	public ItemStack itemInHand; // should be cauldron
	public BlockCauldron cauldron;
	public BlockPos diamondBlockPos;
	public Block diamondBlock;
	public BlockPos redstoneBlockPos;
	public Block redstoneBlock;

	private Random rand = new Random();
	
	public TileQuarry(BlockPos machinePos, EntityPlayer whoPlacedQuarry, ItemStack itemInHand) 
			throws Exception
	{
		this.machinePos = machinePos;
		this.placedBy = whoPlacedQuarry;
		this.itemInHand = itemInHand;
		this.worldObj = placedBy.worldObj;
		this.cauldron = (BlockCauldron) worldObj.getBlockState(machinePos).getBlock();
		this.diamondBlockPos = new BlockPos(machinePos.getX() + 1, machinePos.getY(), machinePos.getZ());
		this.diamondBlock = worldObj.getBlockState(diamondBlockPos).getBlock();
		this.redstoneBlockPos = new BlockPos(machinePos.getX(), machinePos.getY(), machinePos.getZ() - 1);
		this.redstoneBlock = worldObj.getBlockState(redstoneBlockPos).getBlock();
		
		if(!Block.isEqualTo(diamondBlock, Blocks.DIAMOND_BLOCK))
		{
			throw new Exception("DiamondBlock is not in place.");
		}
		if(!Block.isEqualTo(redstoneBlock, Blocks.REDSTONE_BLOCK))
		{
			throw new Exception("RedstoneBlock is not in place.");
		}
		
		this.workingPos = new BlockPos(
				machinePos.getX() - 1, 
				machinePos.getY(), 
				machinePos.getZ() + 1);
	}

	/**
	 * @return - true if quarry is complete and right, otherwise false 
	 * (this should be achievable if the quarry isn't correctly build 
	 * - exception should be thrown when the object is creating)
	 */
	public boolean isComplete() 
	{
		return true;
	}
	
	public BlockPos getTopPos()
	{
		return new BlockPos(machinePos.getX(), machinePos.getY(), machinePos.getZ() + BASIC_QUARRY_SIZE - 1);
	}
	
	public BlockPos getLeftPos()
	{
		return new BlockPos(machinePos.getX() - BASIC_QUARRY_SIZE + 1, machinePos.getY(), machinePos.getZ());
	}
	
	public BlockPos getTopLeftPos()
	{
		return new BlockPos(machinePos.getX() - BASIC_QUARRY_SIZE + 1, machinePos.getY(), machinePos.getZ() + BASIC_QUARRY_SIZE - 1);
	}
	
	public BlockPos getworkingPos()
	{
		return new BlockPos(workingPos.getX(), workingPos.getY(), workingPos.getZ());
	}
	
	public BlockPos getInventoryOutputPos()
	{
		return new BlockPos(machinePos.getX() - 1, machinePos.getY(), machinePos.getZ());
	}
	
	public IInventory getOutputInventory()
	{
		TileEntity output = worldObj.getTileEntity(getInventoryOutputPos());
		if(output instanceof IInventory)
		{
			return (IInventory) output;
		}
		return null;
	}
	
	public BlockPos getInputFuelChestPos()
	{
		return new BlockPos(machinePos.getX(), machinePos.getY() + 1, machinePos.getZ());
	}
	
	public IInventory getInputInventory()
	{
		TileEntity input = worldObj.getTileEntity(getInputFuelChestPos());
		if(input instanceof IInventory)
		{
			return (IInventory) input;
		}
		return null;
	}
	
	/**
	 * Method for checking DiamondBlock and RedstoneBlock
	 */
	public boolean checkSurroundings()
	{
		if(!Block.isEqualTo(diamondBlock, worldObj.getBlockState(diamondBlockPos).getBlock()))
		{
			return false;
		}
		if(!Block.isEqualTo(redstoneBlock, worldObj.getBlockState(redstoneBlockPos).getBlock()))
		{
			return false;
		}
		return true;
	}
	
	/**
	 * will return if quarry can dig next block
	 */
	public boolean canDig()
	{
		if(ticks >= oneOperationCost)
		{
			return true;
		}
		return false;
	}

	/**
	 * TODO: fix bounding box
	 * showing the rendered particles around the digging area
	 */
	public void showBoundingBox()
	{
		/*
		int posX = machinePos.getX();
		int posZ = machinePos.getZ();
		for(int x = 0; x < BASIC_QUARRY_SIZE; x++)
		{
			for(int z = 0; z < BASIC_QUARRY_SIZE; z++)
			{
				if(((posX - x) == posX) || // bottom line
						((posZ + z) == posZ) || // right line
						((posX - x) == (posX - BASIC_QUARRY_SIZE - 1)) || // top line
						((posZ + z) == (posZ + BASIC_QUARRY_SIZE - 1))) // left line
				{
					world.spawnParticle(EnumParticleTypes.END_ROD,
							posX - x, 
							machinePos.getY(), 
							posZ + z, 
							rand.nextGaussian() * 0.005D, 
							rand.nextGaussian() * 0.005D, 
							rand.nextGaussian() * 0.005D, 
							new int[0]);
				}
			}
		}
		*/
	}
	
	public void endWork()
	{
		QuarryHandler.INSTANCE.killQuarry(this);
	}
	
	public boolean inventoryHasSpace()
	{
		return !InventoryHelper.isInventoryFull(getOutputInventory(), INPUT_FACING);
	}
	
	public void spawnDigged(ItemStack digged)
	{
		Block.spawnAsEntity(worldObj, machinePos.offset(EnumFacing.UP, 2), digged);
	}
	
	/**
	 * updating the quarry (dig block, place block in chest)
	 */
	public void doWork() // once a world tick
	{
		if(!canDig())
		{
			return;
		}
		
//		if(workingPos.getX() == getLeftPos().getX())
//		{
//			endWork();
//			return;
//		}
		
		if(worldObj.isAirBlock(workingPos)) 
		{
			while(!worldObj.isAirBlock(workingPos))
			{
				workingPos = workingPos.offset(EnumFacing.DOWN);
			}
		}
		else if(Block.isEqualTo(worldObj.getBlockState(workingPos).getBlock(), Blocks.BEDROCK))
		{
			int nextCoordX = workingPos.getX();
			int nextCoordZ = workingPos.getZ();
			nextCoordZ++;
			if(nextCoordZ >= getTopPos().getZ())
			{
				nextCoordX++;
				nextCoordZ = machinePos.getZ() + 1;
			}
			// kill quarry because it's outside the rectangle
//			if(nextCoordX <= getLeftPos().getX() - 1)
//			{
//				endWork();
//				return;
//			}
			workingPos = new BlockPos(nextCoordX, machinePos.getY(), nextCoordZ);
		}
		else // dig something like stone, iron-ore, etc.
		{
			decreaseTicks();
			boolean hasChest = isNextToOutput(); // chest or any other IInventory
			Block blockToDig = worldObj.getBlockState(workingPos).getBlock();
			List<ItemStack> drops = blockToDig.getDrops(worldObj, workingPos, worldObj.getBlockState(workingPos), 0);
			worldObj.setBlockToAir(workingPos);
			for(ItemStack stack : drops)
			{
				if(!hasChest)
				{
					spawnDigged(stack);
				}
				else if(hasChest)
				{
					ItemStack leftItems = InventoryHelper.putStackInInventoryAllSlots(getOutputInventory(), stack, INPUT_FACING);
					try
					{
						if(leftItems.stackSize > 0)
						{
							spawnDigged(leftItems);
						}
					}
					catch(Exception e)
					{
					}
				}
			}
		}
		// go down by 1 at the end of work in this tick
		workingPos = workingPos.offset(EnumFacing.DOWN);
	}
    
	public void deserializeNBT(NBTTagCompound compound)
	{
		if(machinePos != null)
		{
			QuarryHandler.INSTANCE.addNewQuarry(this);
		}
	}
	
	public ItemStack getActivationStackLeftHand() 
	{
		return null;
	}
	
	public ItemStack getActivationStackRightHand() 
	{
		return itemInHand;
	}
}