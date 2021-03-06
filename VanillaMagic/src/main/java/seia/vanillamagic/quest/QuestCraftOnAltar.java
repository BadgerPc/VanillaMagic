package seia.vanillamagic.quest;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCauldron;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import seia.vanillamagic.api.event.EventPlayerUseCauldron;
import seia.vanillamagic.api.magic.IWand;
import seia.vanillamagic.magic.wand.WandRegistry;
import seia.vanillamagic.util.AltarUtil;
import seia.vanillamagic.util.CauldronUtil;
import seia.vanillamagic.util.ItemStackUtil;

public class QuestCraftOnAltar extends Quest
{
	/*
	 * Each ItemStack is a dif ferent Item
	 * Instead of doing 1x Coal + 1x Coal, do 2x Coal -> 2 will be stackSize
	 */
	protected ItemStack[] ingredients;
	protected ItemStack[] result;
	protected int requiredAltarTier;
	protected IWand requiredMinimalWand;
	
	public void readData(JsonObject jo)
	{
		this.ingredients = ItemStackUtil.getItemStackArrayFromJSON(jo, "ingredients");
		this.result = ItemStackUtil.getItemStackArrayFromJSON(jo, "result");
		this.icon = result[0].copy();
		this.requiredAltarTier = jo.get("requiredAltarTier").getAsInt();
		this.requiredMinimalWand = WandRegistry.getWandByTier(jo.get("wandTier").getAsInt());
		super.readData(jo);
	}
	
	public ItemStack[] getIngredients()
	{
		return ingredients;
	}
	
	public ItemStack[] getResult()
	{
		return result;
	}
	
	public int getRequiredAltarTier()
	{
		return requiredAltarTier;
	}
	
	public IWand getRequiredWand()
	{
		return requiredMinimalWand;
	}
	
	public int getIngredientsStackSize()
	{
		int stackSize = 0;
		for (int i = 0; i < ingredients.length; ++i) stackSize += ItemStackUtil.getStackSize(ingredients[i]);
		return stackSize;
	}
	
	public int getIngredientsInCauldronStackSize(List<EntityItem> entitiesInCauldron)
	{
		int stackSize = 0;
		for (int i = 0; i < entitiesInCauldron.size(); ++i) stackSize += ItemStackUtil.getStackSize(entitiesInCauldron.get(i).getItem());
		return stackSize;
	}
	
	@SubscribeEvent
	public void craftOnAltar(RightClickBlock event)
	{
		EntityPlayer player = event.getEntityPlayer();
		BlockPos cauldronPos = event.getPos();
		// player has got required wand in hand
		if (WandRegistry.isWandInMainHandRight(player, requiredMinimalWand.getWandID()))
		{
			World world = player.world;
			// is right-clicking on Cauldron
			if (world.getBlockState(cauldronPos).getBlock() instanceof BlockCauldron)
			{
				// is altair build correct
				if (AltarUtil.checkAltarTier(world, cauldronPos, requiredAltarTier))
				{
					List<EntityItem> entitiesInCauldron = CauldronUtil.getItemsInCauldron(world, cauldronPos);
					// there is a right amount of items in Cauldron but are they the right items ?
					int ingredientsStackSize = getIngredientsStackSize();
					int ingredientsInCauldronStackSize = getIngredientsInCauldronStackSize(entitiesInCauldron);
					if (ingredientsStackSize == ingredientsInCauldronStackSize)
					{
						List<EntityItem> alreadyCheckedEntityItems = new ArrayList<EntityItem>(); // items to be deleted later
						for (int i = 0; i < ingredients.length; ++i)
						{
							ItemStack currentlyCheckedIngredient = ingredients[i];
							for (int j = 0; j < entitiesInCauldron.size(); ++j)
							{
								EntityItem currentlyCheckedEntityItem = entitiesInCauldron.get(j);
								if (ItemStack.areItemStacksEqual(currentlyCheckedIngredient, currentlyCheckedEntityItem.getItem()))
								{
									alreadyCheckedEntityItems.add(currentlyCheckedEntityItem);
									break;
								}
							}
						}
						// the amount of items in Cauldron was right and the items themselfs were right
						if (ingredients.length == alreadyCheckedEntityItems.size())
						{
							checkQuestProgress(player);
							
							if (hasQuest(player))
							{
								if (!MinecraftForge.EVENT_BUS.post(new EventPlayerUseCauldron.CraftOnAltar(
										player, world, cauldronPos, alreadyCheckedEntityItems, result)))
								{
									for (int i = 0; i < alreadyCheckedEntityItems.size(); ++i) world.removeEntity(alreadyCheckedEntityItems.get(i));
									BlockPos newItemPos = new BlockPos(cauldronPos.getX(), cauldronPos.getY() + 1, cauldronPos.getZ());
									// Spawn all the results
									for (int i = 0; i < result.length; ++i) Block.spawnAsEntity(world, newItemPos, result[i].copy());
									world.updateEntities();
								}
							}
						}
					}
				}
			}
		}
	}
}