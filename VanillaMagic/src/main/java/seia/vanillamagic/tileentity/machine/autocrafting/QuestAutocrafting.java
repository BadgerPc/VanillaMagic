package seia.vanillamagic.tileentity.machine.autocrafting;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.BlockWorkbench;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.IHopper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import seia.vanillamagic.api.tileentity.ICustomTileEntity;
import seia.vanillamagic.api.tileentity.machine.IAutocrafting;
import seia.vanillamagic.handler.CustomTileEntityHandler;
import seia.vanillamagic.magic.wand.WandRegistry;
import seia.vanillamagic.quest.Quest;
import seia.vanillamagic.util.EntityUtil;
import seia.vanillamagic.util.ItemStackUtil;
import seia.vanillamagic.util.WorldUtil;

public class QuestAutocrafting extends Quest
{
	/**
	 * How many blocks lower than the Cauldron are the IInventories.
	 */
	private static int _IINVDOWN = 3;
	
	int count = 0; // simple counter
	@SubscribeEvent
	public void changeSlot(RightClickBlock event)
	{
		EntityPlayer player = event.getEntityPlayer();
		World world = player.world;
		if (world.isRemote) return;
		
		ItemStack rightHand = player.getHeldItemMainhand();
		if (!ItemStackUtil.isNullStack(rightHand)) return; // quit if  NOT empty right hand
		
		BlockPos tilePos = event.getPos();
		ICustomTileEntity tile = CustomTileEntityHandler.getCustomTileEntity(tilePos, world);
		if (tile == null) return;
		
		if (tile instanceof IAutocrafting)
		{
			if (count == 0) count++;
			else
			{
				count = 0;
				return;
			}
			
			IAutocrafting auto = (IAutocrafting) tile;
			if (player.isSneaking()) // sneaking -> change slot
			{
				if (auto.getCurrentCraftingSlot() == auto.getDefaultMaxCraftingSlot()) 
					auto.setCurrentCraftingSlot(auto.getDefaultCraftingSlot());
				else
					auto.setCurrentCraftingSlot(auto.getCurrentCraftingSlot() + 1);
				EntityUtil.addChatComponentMessageNoSpam(player, 
						"Current crafting slot set to: " + auto.getCurrentCraftingSlot());
			}
			else // player is not sneaking -> show current crafting slot
			{
				EntityUtil.addChatComponentMessageNoSpam(player, 
						"Current crafting slot: " + auto.getCurrentCraftingSlot());
			}
		}
	}
	
	@SubscribeEvent
	public void addAutocrafting(RightClickBlock event)
	{
		EntityPlayer player = event.getEntityPlayer();
		World world = player.world;
		if (WandRegistry.areWandsEqual(WandRegistry.WAND_BLAZE_ROD.getWandStack(), player.getHeldItemMainhand()))
		{
			if (player.isSneaking())
			{
				BlockPos workbenchPos = event.getPos();
				BlockPos cauldronPos = workbenchPos.up();
				if (world.getBlockState(cauldronPos).getBlock() instanceof BlockCauldron)
				{
					if (isConstructionComplete(world, cauldronPos))
					{
						if (canAddStat(player)) addStat(player);
						
						if (hasQuest(player))
						{
							if (!ItemStackUtil.isNullStack(player.getHeldItemOffhand())) return;
							
							TileAutocrafting tile = new TileAutocrafting();
							tile.init(player.world, cauldronPos);
							if (CustomTileEntityHandler.addCustomTileEntity(tile, WorldUtil.getDimensionID(world)))
								EntityUtil.addChatComponentMessageNoSpam(player, tile.getClass().getSimpleName() + " added");
						}
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public void deleteAutocrafting(BreakEvent event)
	{
		EntityPlayer player = event.getPlayer();
		World world = player.world;
		BlockPos cauldronPos = event.getPos();
		Block cauldron = world.getBlockState(cauldronPos).getBlock();
		if (cauldron instanceof BlockCauldron)
		{
			BlockPos workbenchPos = cauldronPos.down();
			Block workbench = world.getBlockState(workbenchPos).getBlock();
			if (workbench instanceof BlockWorkbench)
				CustomTileEntityHandler.removeCustomTileEntityAndSendInfoToPlayer(world, cauldronPos, player);
		}
	}
	
	public static ItemStack[][] buildStackMatrix(IInventory[][] inventoryMatrix, int slot) 
	{
		int size = inventoryMatrix.length;
		ItemStack[][] stackMat = new ItemStack[size][size];
		for (int i = 0; i < size; ++i)
			for (int j = 0; j < size; ++j)
				stackMat[i][j] = inventoryMatrix[i][j].getStackInSlot(slot);
		return stackMat;
	}

	public static IInventory[][] buildIInventoryMatrix(World world, BlockPos[][] inventoryPosMatrix) 
	{
		int size = inventoryPosMatrix.length;
		IInventory[][] invMat = new IInventory[size][size];
		for (int i = 0; i < size; ++i)
		{
			for (int j = 0; j < size; ++j)
			{
				TileEntity tile = world.getTileEntity(inventoryPosMatrix[i][j]);
				if (tile instanceof IInventory)
					invMat[i][j] = (IInventory) tile;
			}
		}
		return invMat;
	}

	public static BlockPos[][] buildInventoryMatrix(BlockPos cauldronPos) 
	{
		BlockPos leftTop = new BlockPos(cauldronPos.getX() - 2, cauldronPos.getY() - _IINVDOWN, cauldronPos.getZ() + 2);
		BlockPos top = new BlockPos(cauldronPos.getX(), cauldronPos.getY() - _IINVDOWN, cauldronPos.getZ() + 2);
		BlockPos rightTop = new BlockPos(cauldronPos.getX() + 2, cauldronPos.getY() - _IINVDOWN, cauldronPos.getZ() + 2);
		BlockPos right = new BlockPos(cauldronPos.getX() + 2, cauldronPos.getY() - _IINVDOWN, cauldronPos.getZ());
		BlockPos rightBottom = new BlockPos(cauldronPos.getX() + 2, cauldronPos.getY() - _IINVDOWN, cauldronPos.getZ() - 2);
		BlockPos bottom = new BlockPos(cauldronPos.getX(), cauldronPos.getY() - _IINVDOWN, cauldronPos.getZ() - 2);
		BlockPos leftBottom = new BlockPos(cauldronPos.getX() - 2, cauldronPos.getY() - _IINVDOWN, cauldronPos.getZ() - 2);
		BlockPos left = new BlockPos(cauldronPos.getX() - 2, cauldronPos.getY() - _IINVDOWN, cauldronPos.getZ());
		BlockPos middle = cauldronPos.offset(EnumFacing.DOWN, _IINVDOWN);
		
		return new BlockPos[][]{
			new BlockPos[]{ leftTop, top, rightTop },
			new BlockPos[]{ left, middle, right },
			new BlockPos[]{ leftBottom, bottom, rightBottom }
		};
	}
	
	public static boolean isConstructionComplete(World world, BlockPos cauldronPos)
	{
		boolean checkBasics = false;
		if (world.getTileEntity(cauldronPos.offset(EnumFacing.DOWN, _IINVDOWN)) instanceof IInventory)
			if (world.getTileEntity(cauldronPos.offset(EnumFacing.DOWN, 2)) instanceof IHopper)
				if (world.getBlockState(cauldronPos.offset(EnumFacing.DOWN)).getBlock() instanceof BlockWorkbench)
					checkBasics = true;
		
		if (checkBasics)
		{
			BlockPos leftTop = new BlockPos(cauldronPos.getX() - 2, cauldronPos.getY() - _IINVDOWN, cauldronPos.getZ() + 2);
			BlockPos top = new BlockPos(cauldronPos.getX(), cauldronPos.getY() - _IINVDOWN, cauldronPos.getZ() + 2);
			BlockPos rightTop = new BlockPos(cauldronPos.getX() + 2, cauldronPos.getY() - _IINVDOWN, cauldronPos.getZ() + 2);
			BlockPos right = new BlockPos(cauldronPos.getX() + 2, cauldronPos.getY() - _IINVDOWN, cauldronPos.getZ());
			BlockPos rightBottom = new BlockPos(cauldronPos.getX() + 2, cauldronPos.getY() - _IINVDOWN, cauldronPos.getZ() - 2);
			BlockPos bottom = new BlockPos(cauldronPos.getX(), cauldronPos.getY() - _IINVDOWN, cauldronPos.getZ() - 2);
			BlockPos leftBottom = new BlockPos(cauldronPos.getX() - 2, cauldronPos.getY() - _IINVDOWN, cauldronPos.getZ() - 2);
			BlockPos left = new BlockPos(cauldronPos.getX() - 2, cauldronPos.getY() - _IINVDOWN, cauldronPos.getZ());
			
			if (world.getTileEntity(leftTop) instanceof IInventory)
				if (world.getTileEntity(top) instanceof IInventory)
					if (world.getTileEntity(rightTop) instanceof IInventory)
						if (world.getTileEntity(right) instanceof IInventory)
							if (world.getTileEntity(rightBottom) instanceof IInventory)
								if (world.getTileEntity(bottom) instanceof IInventory)
									if (world.getTileEntity(leftBottom) instanceof IInventory)
										if (world.getTileEntity(left) instanceof IInventory)
											return true;
		}
		return false;
	}
}