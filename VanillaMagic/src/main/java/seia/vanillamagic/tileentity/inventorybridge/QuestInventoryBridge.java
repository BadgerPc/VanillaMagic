package seia.vanillamagic.tileentity.inventorybridge;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import seia.vanillamagic.api.exception.NotInventoryException;
import seia.vanillamagic.api.tileentity.ICustomTileEntity;
import seia.vanillamagic.handler.customtileentity.CustomTileEntityHandler;
import seia.vanillamagic.quest.Quest;
import seia.vanillamagic.spell.EnumWand;
import seia.vanillamagic.util.EntityHelper;
import seia.vanillamagic.util.WorldHelper;

public class QuestInventoryBridge extends Quest
{
	/**
	 * ItemStack required in left hand to create {@link TileInventoryBridge}
	 */
	public final ItemStack requiredLeftHand = new ItemStack(Blocks.STAINED_GLASS);
	
	@SubscribeEvent
	public void onRightClick(RightClickBlock event)
	{
		EntityPlayer player = event.getEntityPlayer();
		ItemStack rightHand = player.getHeldItemMainhand();
		if(rightHand == null)
		{
			return;
		}
		if(!EnumWand.areWandsEqual(EnumWand.BLAZE_ROD.wandItemStack, rightHand))
		{
			return;
		}
		ItemStack leftHand = player.getHeldItemOffhand();
		if(leftHand == null)
		{
			return;
		}
		if(!ItemStack.areItemsEqual(leftHand, requiredLeftHand))
		{
			return;
		}
		if(player.isSneaking())
		{
			World world = event.getWorld();
			BlockPos clickedPos = event.getPos();
			// RightClicked on IInventory
			TileEntity clickedInventory = world.getTileEntity(clickedPos);
			if(clickedInventory == null)
			{
				return;
			}
			if(!(clickedInventory instanceof IInventory))
			{
				return;
			}
			/*
			 * Player has got Blaze Rod in right hand and "requiredLeftHand" in left hand.
			 * We can now spawn TileEntity and let it do work.
			 */
			if(canPlayerGetAchievement(player))
			{
				player.addStat(achievement, 1);
			}
			if(player.hasAchievement(achievement))
			{
				TileInventoryBridge tile = new TileInventoryBridge();
				tile.init(player, clickedPos.offset(EnumFacing.UP));
				tile.setWorld(world);
				try
				{
					tile.setPositionFromSelector(player);
				}
				catch(NotInventoryException e)
				{
					e.printStackTrace();
					System.out.println(e.position.toString());
				}
				try 
				{
					tile.setOutputInventory(world, clickedPos);
				} 
				catch(NotInventoryException e)
				{
					e.printStackTrace();
				}
				if(CustomTileEntityHandler.INSTANCE.addCustomTileEntity(tile, player.dimension))
				{
					EntityHelper.addChatComponentMessage(player, tile.getClass().getSimpleName() + " added");
					leftHand.stackSize--;
					if(leftHand.stackSize == 0)
					{
						player.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, null);
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onBridgeDestroyed(BreakEvent event)
	{
		BlockPos inventoryPos = event.getPos();
		World world = event.getWorld();
		TileEntity inventoryTile = world.getTileEntity(inventoryPos);
		if(inventoryTile instanceof IInventory)
		{
			BlockPos customTilePos = inventoryPos.offset(EnumFacing.UP);
			ICustomTileEntity customTile = CustomTileEntityHandler.INSTANCE.getCustomTileEntity(customTilePos, WorldHelper.getDimensionID(world));
			if(customTile == null)
			{
				return;
			}
			if(CustomTileEntityHandler.INSTANCE.removeCustomTileEntityAtPos(world, customTilePos))
			{
				EntityHelper.addChatComponentMessage(event.getPlayer(), customTile.getClass().getSimpleName() + " removed");
			}
		}
	}
}