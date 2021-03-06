package seia.vanillamagic.tileentity.speedy;

import net.minecraft.block.BlockCauldron;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import seia.vanillamagic.handler.CustomTileEntityHandler;
import seia.vanillamagic.item.VanillaMagicItems;
import seia.vanillamagic.magic.wand.WandRegistry;
import seia.vanillamagic.quest.Quest;
import seia.vanillamagic.util.EntityUtil;
import seia.vanillamagic.util.WorldUtil;

public class QuestSpeedy extends Quest
{
	@SubscribeEvent
	public void placeSpeedy(RightClickBlock event)
	{
		EntityPlayer player = event.getEntityPlayer();
		World world = event.getWorld();
		ItemStack leftHand = player.getHeldItemOffhand();
		BlockPos clickedPos = event.getPos();
		if (world.getBlockState(clickedPos).getBlock() instanceof BlockCauldron)
		{
			if (VanillaMagicItems.isCustomItem(leftHand, VanillaMagicItems.ACCELERATION_CRYSTAL))
			{
				ItemStack rightHand = player.getHeldItemMainhand();
				if (WandRegistry.areWandsEqual(rightHand, WandRegistry.WAND_BLAZE_ROD.getWandStack()))
				{
					if (player.isSneaking())
					{
						if (canAddStat(player)) addStat(player);
						
						if (hasQuest(player))
						{
							TileSpeedy speedy = new TileSpeedy();
							speedy.init(player.world, clickedPos);
							if (speedy.containsCrystal())
							{
								if (CustomTileEntityHandler.addCustomTileEntity(speedy, WorldUtil.getDimensionID(world)))
									EntityUtil.addChatComponentMessageNoSpam(player, speedy.getClass().getSimpleName() + " added");
							}
						}
					}
				}
			}
		}
	}
}