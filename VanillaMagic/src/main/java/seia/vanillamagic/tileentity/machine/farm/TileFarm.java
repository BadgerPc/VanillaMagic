package seia.vanillamagic.tileentity.machine.farm;

import java.util.UUID;

import javax.annotation.Nonnull;

import org.apache.logging.log4j.Level;

import com.mojang.authlib.GameProfile;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import seia.vanillamagic.VanillaMagic;
import seia.vanillamagic.api.exception.NotInventoryException;
import seia.vanillamagic.api.inventory.InventoryWrapper;
import seia.vanillamagic.fake.FakePlayerVM;
import seia.vanillamagic.inventory.InventoryHelper;
import seia.vanillamagic.tileentity.machine.TileMachine;
import seia.vanillamagic.util.BlockPosHelper;

public class TileFarm extends TileMachine
{
	public static final String REGISTRY_NAME = TileFarm.class.getSimpleName();
	public static final GameProfile FARMER_PROFILE = new GameProfile(UUID.fromString("c1ddfd7f-120a-4000-8b64-38660d3ec62d"), "[VanillaMagicFarmer]");
	
	public int radius;
	public BlockPos chestPosInput;
	public BlockPos chestPosOutput;
//	public EntityPlayerMP farmer;
	
	public void init(World world, BlockPos machinePos)
	{
		super.init(world, machinePos);
		this.startPos = new BlockPos(machinePos.getX() + radius, machinePos.getY(), machinePos.getZ() + radius);
		this.workingPos = null;//BlockPosHelper.copyPos(startPos);
		//this.radius = radius; //this.farmSize = (2 * radius) + 1;
		this.chestPosInput = this.pos.offset(EnumFacing.UP);
		this.chestPosOutput = this.pos.offset(EnumFacing.DOWN);
		try
		{
			this.inventoryInput = new InventoryWrapper(worldObj, this.chestPosInput);
			this.inventoryOutput = new InventoryWrapper(worldObj, this.chestPosOutput);
		}
		catch(NotInventoryException e)
		{
			VanillaMagic.LOGGER.log(Level.ERROR, this.getClass().getSimpleName() + 
					" - error when converting to IInventory at position: " + 
					e.position.toString());
		}
	}
	
//	public BlockPos getInputPos()
//	{
//		return this.getMachinePos().offset(EnumFacing.UP);
//	}
	
//	public BlockPos getOutputPos()
//	{
//		return this.getMachinePos().offset(EnumFacing.DOWN);
//	}
	
	public boolean checkSurroundings() 
	{
		return (this.worldObj.getTileEntity(chestPosInput) instanceof IInventory) &&
				(this.worldObj.getTileEntity(chestPosOutput) instanceof IInventory);
	}
	
	boolean shouldDecreaseTicks = false;
	public void doWork() 
	{
		shouldDecreaseTicks = false;
		doTick();
		if(shouldDecreaseTicks)
		{
			decreaseTicks();
		}
		/*
		try
		{
			workingPos = getNextCoord();
			IBlockState state = this.worldObj.getBlockState(workingPos);
			Block block = state.getBlock();
			Farmers.INSTANCE.prepareBlock(this, workingPos, block, state);
			state = this.worldObj.getBlockState(workingPos);
			block = state.getBlock();
			IHarvestResult harvest = Farmers.INSTANCE.harvestBlock(this, workingPos, block, state);
			if(harvest != null && harvest.getDrops() != null) 
			{
				for (EntityItem ei : harvest.getDrops()) 
				{
					if(ei != null) 
					{
						insertHarvestDrop(ei, getOutputPos());
						if(!ei.isDead) 
						{
							worldObj.spawnEntityInWorld(ei);
						}
					}
				}
			}
		}
		catch(Exception e)
		{
		}
		*/
	}
	
//	public int getMinSupplySlot()
//	{
//		return 0;
//	}
//	
//	public int getMaxSupplySlot()
//	{
//		return getInputInventory().getInventory().getSizeInventory();
//	}
	
	public boolean tillBlock(BlockPos plantingLocation) 
	{
		BlockPos dirtLoc = plantingLocation.offset(EnumFacing.DOWN);
		Block dirtBlock = getBlock(dirtLoc);
		if((dirtBlock == Blocks.DIRT || dirtBlock == Blocks.GRASS)) 
		{
			if(!hasHoe()) 
			{
				return false;
			}
			damageHoe(1, dirtLoc);
			worldObj.setBlockState(dirtLoc, Blocks.FARMLAND.getDefaultState());
			worldObj.playSound(dirtLoc.getX() + 0.5F, dirtLoc.getY() + 0.5F, dirtLoc.getZ() + 0.5F, 
					SoundEvents.BLOCK_GRASS_STEP, SoundCategory.BLOCKS,
					(Blocks.FARMLAND.getSoundType().getVolume() + 1.0F) / 2.0F, Blocks.FARMLAND.getSoundType().getPitch() * 0.8F, false);
			return true;
		} 
		else if(dirtBlock == Blocks.FARMLAND) 
		{
			return true;
		}
		return false;
	}

	public int getMaxLootingValue() 
	{
		int result = 0;
		IInventory inv = getInputInventory().getInventory();
		for(int i = 0; i < inv.getSizeInventory(); i++)
		{
			ItemStack stack = inv.getStackInSlot(i);
			if(stack != null)
			{
				int level = getLooting(stack);
				if(level > result)
				{
					result = level;
				}
			}
		}
		return result;
	}

	public boolean hasHoe() 
	{
		return hasTool(ToolType.HOE);
	}

	public boolean hasAxe() 
	{
		return hasTool(ToolType.AXE);
	}

	public boolean hasShears() 
	{
		return hasTool(ToolType.SHEARS);
	}

	public int getAxeLootingValue() 
	{
		ItemStack tool = getTool(ToolType.AXE);
		if(tool == null) 
		{
			return 0;
		}
		return getLooting(tool);
	}

	public void damageAxe(Block blk, BlockPos pos) 
	{
		damageTool(ToolType.AXE, blk, pos, 1);
	}

	public void damageHoe(int i, BlockPos pos) 
	{
		damageTool(ToolType.HOE, null, pos, i);
	}

	public void damageShears(Block blk, BlockPos pos) 
	{
		damageTool(ToolType.SHEARS, blk, pos, 1);
	}

	public boolean hasTool(ToolType type)
	{
		return getTool(type) != null;
	}

	public ItemStack getTool(ToolType type) 
	{
		int result = 0;
		IInventory inv = getInputInventory().getInventory();
		for(int i = 0; i < inv.getSizeInventory(); i++)
		{
			ItemStack stack = inv.getStackInSlot(i);
			if(stack != null)
			{
				if(type.itemMatches(stack) && stack.stackSize > 0 /*&& stack.getItemDamage() > 1*/)
				{
					return stack;
				}
			}
//			if(ToolType.isBrokenTinkerTool(stack))
//			{
//				markDirty();
//			}
//			if(type.itemMatches(stack) && stack.stackSize > 0 && stack.getItemDamage() > 1)
//			{
//				return stack;
//			}
		}
		return null;
	}

	public void damageTool(ToolType type, Block blk, BlockPos pos, int damage) 
	{
		ItemStack tool = getTool(type);
		if(tool == null) 
		{
			return;
		}
		IBlockState bs = getBlockState(pos);
		boolean canDamage = canDamage(tool);
		if(type == ToolType.AXE) 
		{
//			tool.getItem().onBlockDestroyed(tool, worldObj, bs, pos, farmer);
			tool.damageItem(1, null);
			//tool.setItemDamage(tool.getItemDamage() + 1);
		} 
		else if(type == ToolType.HOE) 
		{
//			int origDamage = tool.getItemDamage();
//			tool.getItem().onItemUse(tool, farmer, worldObj, pos, EnumHand.MAIN_HAND, EnumFacing.UP, 0.5f, 0.5f, 0.5f);
//			if(origDamage == tool.getItemDamage() && canDamage) 
//			{
//				tool.damageItem(1, farmer);
//			}
			{
				IBlockState iblockstate = worldObj.getBlockState(pos);
	            Block block = iblockstate.getBlock();
	            if(worldObj.isAirBlock(pos.up()))
	            {
	                if(block == Blocks.GRASS || block == Blocks.GRASS_PATH)
	                {
	                    if(!worldObj.isRemote)
	                    {
	                    	worldObj.setBlockState(pos, Blocks.FARMLAND.getDefaultState(), 11);
//	                    	tool.damageItem(1, null);
	                    	tool.setItemDamage(tool.getItemDamage() + 1);
	                    }
	                }
	                if(block == Blocks.DIRT)
	                {
	                    switch((BlockDirt.DirtType)iblockstate.getValue(BlockDirt.VARIANT))
	                    {
	                        case DIRT:
	                            if(!worldObj.isRemote)
	                            {
	                            	worldObj.setBlockState(pos, Blocks.FARMLAND.getDefaultState(), 11);
//	                            	tool.damageItem(1, null);
	                            	tool.setItemDamage(tool.getItemDamage() + 1);
	                            }
	                        case COARSE_DIRT:
	                            if(!worldObj.isRemote)
	                            {
	                            	worldObj.setBlockState(pos, Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.DIRT), 11);
//	                            	tool.damageItem(1, null);
	                            	tool.setItemDamage(tool.getItemDamage() + 1);
	                            }
	                    }
	                }
	            }
			}
		} 
		else if(canDamage) 
		{
//			tool.damageItem(1, farmer);
//			tool.damageItem(1, null);
			tool.setItemDamage(tool.getItemDamage() + 1);
		}
		
		if(tool.stackSize == 0 || (canDamage && tool.getItemDamage() >= tool.getMaxDamage())) 
		{
			destroyTool(type);
		}
	}

	private boolean canDamage(ItemStack stack) 
	{
		return stack != null && stack.isItemStackDamageable() && stack.getItem().isDamageable();
	}

	private void destroyTool(ToolType type) 
	{
		IInventory inv = getInputInventory().getInventory();
		for(int i = 0; i < inv.getSizeInventory(); i++)
		{
			ItemStack stack = inv.getStackInSlot(i);
			if(type.itemMatches(stack) && stack.stackSize == 0)
			{
				inv.setInventorySlotContents(i, null);
				markDirty();
				return;
			}
		}
	}

	private int getLooting(ItemStack stack) 
	{
		return Math.max(
				EnchantmentHelper.getEnchantmentLevel(Enchantments.LOOTING, stack),
				EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack));
	}

//	public EntityPlayerMP getFarmer() 
//	{
//		return farmer;
//	}

	public Block getBlock(int x, int y, int z) 
	{
		return getBlock(new BlockPos(x, y, z));
	}
	  
	public Block getBlock(BlockPos posIn) 
	{
		return getBlockState(posIn).getBlock();
	}
	  
	public IBlockState getBlockState(BlockPos posIn) 
	{
		return worldObj.getBlockState(posIn);
	}

	public boolean isOpen(BlockPos pos)
	{
		Block block = getBlock(pos);
		IBlockState bs = getBlockState(pos);
		return block.isAir(bs, worldObj, pos) || block.isReplaceable(worldObj, pos);
	}
	  
//	protected boolean isMachineItemValidForSlot(int slot, ItemStack stack) 
//	{
//		if(stack == null) 
//		{
//			return false;
//		}
//		return true;
//	}
	  
	protected boolean checkProgress(boolean redstoneChecksPassed) 
	{
		if(redstoneChecksPassed) 
		{
			doTick();
		}
		return false;
	}

	protected void doTick()
	{
//		int infiniteLoop = 20;
//		while(workingPos == null || workingPos.equals(getPos()) || !worldObj.isBlockLoaded(workingPos)) 
//		{
//			if(infiniteLoop-- <= 0) 
//			{
//				return;
//			}
//			workingPos = getNextCoord();
//		}
		workingPos = getNextCoord();
		IBlockState bs = getBlockState(workingPos);
		Block block = bs.getBlock();
//		if(farmer == null) 
//		{
//			//farmer = new FakeFarmer(FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(WorldHelper.getDimensionID(this.worldObj)));
//			farmer = new FakePlayerVM(worldObj, chestPosInput, FARMER_PROFILE);
//			VanillaMagic.LOGGER.log(Level.INFO, "Added Custom Farmer");
//		}
		if(isOpen(workingPos)) 
		{
			boolean prepared = FarmersRegistry.INSTANCE.prepareBlock(this, workingPos, block, bs);
			if(prepared)
			{
				shouldDecreaseTicks = true;
			}
			bs = getBlockState(workingPos);
			block = bs.getBlock();
		}
		if(isOutputFull())
		{
			return;
		}
		if(!isOpen(workingPos))
		{
			IHarvestResult harvest = FarmersRegistry.INSTANCE.harvestBlock(this, workingPos, block, bs);
			if(harvest != null && harvest.getDrops() != null) 
			{
				for(EntityItem ei : harvest.getDrops()) 
				{
					if(ei != null) 
					{
						insertHarvestDrop(ei, workingPos);
						//TODO: TileFarm -> Fix if output chest is full. Stop working.
//						if(!ei.isDead) 
//						{
//							worldObj.spawnEntityInWorld(ei);
//						}
					}
				}
				shouldDecreaseTicks = true;
				return;
			}
		}
//		if(hasBonemeal() && bonemealCooldown-- <= 0) 
//		{
//			IInventory inv = getInputInventory().getInventory();
//			for(int i = 0; i < inv.getSizeInventory(); i++)
//			{
//				ItemStack stack = inv.getStackInSlot(i);
//				Fertilizer fertilizer = Fertilizer.getInstance(stack);
//				if((fertilizer.applyOnPlant() != isOpen(workingPos)) || (fertilizer.applyOnAir() == worldObj.isAirBlock(workingPos))) 
//				{
//					farmer.inventory.mainInventory[0] = stack;
//					farmer.inventory.currentItem = 0;
//					if(fertilizer.apply(stack, farmer, worldObj, new BlockPos(workingPos))) 
//					{
//						//stack = farmer.inventory.mainInventory[0];
//						inv.setInventorySlotContents(i, farmer.inventory.mainInventory[0]);
//						if(inv.getStackInSlot(i) != null && inv.getStackInSlot(i).stackSize == 0)//if(stack != null && stack.stackSize == 0) 
//						{
//							inv.setInventorySlotContents(i, null);
//						}
//						bonemealCooldown = 20;
//					} 
//					else 
//					{
//						bonemealCooldown = 5;
//					}
//					farmer.inventory.mainInventory[0] = null;
//				}
//			}
//		}
	}

//	private int bonemealCooldown = 5; // no need to persist this
	  
//	private boolean hasBonemeal() 
//	{
//		IInventory inv = getInputInventory().getInventory();
//		for(int i = 0; i < inv.getSizeInventory(); i++)
//		{
//			ItemStack stack = inv.getStackInSlot(i);
//			if(Fertilizer.getInstance(stack) == Fertilizer.BONEMEAL)
//			{
//				return true;
//			}
//			if(Fertilizer.getInstance(inv.getStackInSlot(i)) != Fertilizer.NONE)
//			{
//				return true;
//			}
//		}
//		return false;
//	}

	private boolean isOutputFull() 
	{
		return !this.inventoryOutputHasSpace();
	}

	public boolean hasSeed(ItemStack seeds, BlockPos pos) 
	{
//		int slot = getSupplySlotForCoord(pos);
//		ItemStack inv = getInputInventory().getInventory().getStackInSlot(slot);
//		return inv != null && inv.stackSize > 1 && inv.isItemEqual(seeds);
		
		IInventory inv = getInputInventory().getInventory();
		for(int i = 0; i < inv.getSizeInventory(); i++)
		{
			ItemStack stack = inv.getStackInSlot(i);
			if((stack != null) && (stack.stackSize > 1) && (stack.isItemEqual(seeds)))
			{
				return true;
			}
		}
		return false;
	}

	int farmSaplingReserveAmount = 32;
	/*
	 * Returns a fuzzy boolean:
	 * 
	 * <=0 - break no leaves for saplings
	 *  50 - break half the leaves for saplings
	 *  90 - break 90% of the leaves for saplings
	 */
	public int isLowOnSaplings(BlockPos pos) 
	{
//		int slot = getSupplySlotForCoord(pos);
//		ItemStack inv = getInputInventory().getInventory().getStackInSlot(slot);
//		return 90 * (farmSaplingReserveAmount - (inv == null ? 0 : inv.stackSize)) / farmSaplingReserveAmount;
		
		IInventory inv = getInputInventory().getInventory();
		for(int i = 0; i < inv.getSizeInventory(); i++)
		{
			ItemStack stack = inv.getStackInSlot(i);
			if(stack != null)
			{
				Block blockSapling = Block.getBlockFromItem(stack.getItem());
				if(blockSapling != null)
				{
					if(blockSapling instanceof BlockSapling)
					{
						return 90 * (farmSaplingReserveAmount - (stack == null ? 0 : stack.stackSize)) / farmSaplingReserveAmount;
					}
				}
			}
		}
		return 0;
	}

	public ItemStack takeSeedFromSupplies(ItemStack stack, BlockPos forBlock) 
	{
		return takeSeedFromSupplies(stack, forBlock, true);
	}

	public ItemStack takeSeedFromSupplies(ItemStack stack, BlockPos forBlock, boolean matchMetadata) 
	{
		if(stack == null || forBlock == null) 
		{
			return null;
		}
		
		IInventory inv = getInputInventory().getInventory();
		for(int i = 0; i < inv.getSizeInventory(); i++)
		{
			ItemStack invStack = inv.getStackInSlot(i);
			if(invStack != null)
			{
				if(matchMetadata ? invStack.isItemEqual(stack) : invStack.getItem() == stack.getItem())
				{
					if(stack.stackSize > 1)
					{
						ItemStack result = stack.copy();
						result.stackSize = 1;
						stack = stack.copy();
						stack.stackSize--;
						if(stack.stackSize == 0)
						{
							stack = null;
						}
						getInputInventory().getInventory().setInventorySlotContents(i, stack);
						return result;
					}
				}
			}
		}
		return null;
		
//		int slot = getSupplySlotForCoord(forBlock);
//		ItemStack inv = getInputInventory().getInventory().getStackInSlot(slot);
//		if(inv != null) 
//		{
//			if(matchMetadata ? inv.isItemEqual(stack) : inv.getItem() == stack.getItem()) 
//			{
//				if(inv.stackSize <= 1) 
//				{
//					return null;
//				}
//				ItemStack result = inv.copy();
//				result.stackSize = 1;
//				inv = inv.copy();
//				inv.stackSize--;
//				if(inv.stackSize == 0) 
//				{
//					inv = null;
//				}
//				getInputInventory().getInventory().setInventorySlotContents(slot, inv);
//				return result;
//			}
//		}
//		return null;
	}

//	public ItemStack takeSeedFromSupplies(BlockPos pos) 
//	{
//		return takeSeedFromSupplies(getSeedTypeInSuppliesFor(pos), pos);
//	}
//
//	public ItemStack getSeedTypeInSuppliesFor(BlockPos pos) 
//	{
//		int slot = getSupplySlotForCoord(pos);
//		return getSeedTypeInSuppliesFor(slot);
//	}
//
//	public ItemStack getSeedTypeInSuppliesFor(int slot) 
//	{
//		ItemStack inv = getInputInventory().getInventory().getStackInSlot(slot);
//		if(inv != null && (inv.stackSize > 1)) 
//		{
//			return inv.copy();
//		}
//		return null;
//	}

//	public int getSupplySlotForCoord(BlockPos forBlock) // TODO: Fix TileFarm -> getSupplySlotForCoord
//	{
//		//return 0;
//		int xCoord = getPos().getX();
//		int zCoord = getPos().getZ();
//		if(forBlock.getX() <= xCoord && forBlock.getZ() > zCoord) 
//		{
//			System.out.println(0);
//			return 0;//getMinSupplySlot();
//		} 
//		else if(forBlock.getX() > xCoord && forBlock.getZ() > zCoord - 1) 
//		{
//			System.out.println(1);
//			return 1;//getMinSupplySlot() + 1;
//		} 
//		else if(forBlock.getX() < xCoord && forBlock.getZ() <= zCoord) 
//		{
//			System.out.println(2);
//			return 2;//getMinSupplySlot() + 2;
//		}
//		System.out.println(3);
//		return 3;//getMinSupplySlot() + 3;
//	}

	private void insertHarvestDrop(Entity entity, BlockPos pos) 
	{
		if(!worldObj.isRemote) 
		{
			if(entity instanceof EntityItem && !entity.isDead) 
			{
				EntityItem item = (EntityItem) entity;
				ItemStack stack = item.getEntityItem().copy();
				int numInserted = insertResult(stack, pos);
				stack.stackSize -= numInserted;
				item.setEntityItemStack(stack);
				if(stack.stackSize == 0) 
				{
					item.setDead();
				}
			}
		}
	}

	private int insertResult(ItemStack stack, BlockPos pos) 
	{
		ItemStack left = InventoryHelper.putStackInInventoryAllSlots(getOutputInventory().getInventory(), stack, EnumFacing.DOWN);
		if(left == null)
		{
			return 0;
		}
		return left.stackSize;
		/*
		int slot = pos != null ? getminSupplySlotForCoord(pos) : minSupplySlot;
		int[] slots = new int[NUM_SUPPLY_SLOTS];
		int k = 0;
		for(int j = slot; j <= maxSupplySlot; j++)
		{
			slots[k++] = j;
		}
		for(int j = minSupplySlot; j < slot; j++) 
		{
			slots[k++] = j;
		}	    
		int origSize = stack.stackSize;
		stack = stack.copy();
		int inserted = 0;
		for(int j = 0; j < slots.length && inserted < stack.stackSize; j++) 
		{
			int i = slots[j];
			ItemStack curStack = inventory[i];
			int inventoryStackLimit = getInventoryStackLimit(i);
			if(isItemValidForSlot(i, stack) && (curStack == null || curStack.stackSize < inventoryStackLimit)) 
			{
				if(curStack == null) 
				{
					if (stack.stackSize < inventoryStackLimit) 
					{
						inventory[i] = stack.copy();
						inserted = stack.stackSize;
					} 
					else 
					{
						inventory[i] = stack.copy();
						inserted = inventoryStackLimit;
						inventory[i].stackSize = inserted;
					}
				} 
				else if(curStack.isItemEqual(stack)) 
				{
					inserted = Math.min(inventoryStackLimit - curStack.stackSize, stack.stackSize);
					inventory[i].stackSize += inserted;
				}
			}
		}
		stack.stackSize -= inserted;
		if(inserted >= origSize) 
		{
			return origSize;
		}
		ResultStack[] in = new ResultStack[] { new ResultStack(stack) };
		mergeResults(in);
		return origSize - (in[0].item == null ? 0 : in[0].item.stackSize);
		*/
	}

	@Nonnull
	private BlockPos getNextCoord() 
	{
		int size = radius;
		BlockPos loc = getPos();
		if(workingPos == null) 
		{
			return workingPos = new BlockPos(loc.getX() - size, loc.getY(), loc.getZ() - size);
		}
		int nextX = workingPos.getX() + 1;
		int nextZ = workingPos.getZ();
		if(nextX > loc.getX() + size) 
		{
			nextX = loc.getX() - size;
			nextZ += 1;
			if(nextZ > loc.getZ() + size) 
			{
				nextX = loc.getX() - size;
				nextZ = loc.getZ() - size;
			}
		}
		return workingPos = new BlockPos(nextX, workingPos.getY(), nextZ);
	}
	
	public EnumFacing getOutputFacing() 
	{
		return EnumFacing.DOWN;
	}
}