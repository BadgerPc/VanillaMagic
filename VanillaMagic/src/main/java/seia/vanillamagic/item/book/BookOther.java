package seia.vanillamagic.item.book;

import static seia.vanillamagic.util.TextHelper.ENTER;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.fml.common.registry.GameRegistry;
import seia.vanillamagic.api.quest.IQuest;
import seia.vanillamagic.item.accelerationcrystal.QuestAccelerationCrystal;
import seia.vanillamagic.item.liquidsuppressioncrystal.QuestLiquidSuppressionCrystal;
import seia.vanillamagic.item.thecrystalofmothernature.QuestMotherNatureCrystal;
import seia.vanillamagic.quest.QuestBuildAltar;
import seia.vanillamagic.quest.QuestCraftOnAltar;
import seia.vanillamagic.quest.QuestList;
import seia.vanillamagic.quest.spell.QuestCastSpell;
import seia.vanillamagic.tileentity.chunkloader.QuestChunkLoader;
import seia.vanillamagic.tileentity.machine.autocrafting.QuestAutocrafting;
import seia.vanillamagic.tileentity.machine.quarry.QuestQuarry;
import seia.vanillamagic.util.TextHelper;

public class BookOther implements IBook
{
	public int getUID() 
	{
		return 4;
	}
	
	public void registerRecipe() 
	{
		GameRegistry.addRecipe(getItem(), new Object[]{
				"   ",
				"   ",
				"BBB",
				'B', Items.BOOK
		});
	}
	
	public ItemStack getItem() 
	{
		ItemStack infoBook = new ItemStack(BookRegistry.BOOK_ITEM);
		NBTTagCompound data = new NBTTagCompound();
		{
			// Constructing TagCompound
			NBTTagList pages = new NBTTagList();
			{
				// Pages
				pages.appendTag(new NBTTagString("\n\n\n\n" + BookRegistry.COLOR_TITLE + "==== " + TextHelper.translateToLocal("book.other.title") + " ====" + 
						TextHelper.getEnters(4) + "-" + BookRegistry.AUTHOR + " " + BookRegistry.YEAR));
				for(int i = 0; i < QuestList.size(); i++)
				{
					IQuest quest = QuestList.get(i);
					if(quest instanceof QuestChunkLoader)
					{
						pages.appendTag(new NBTTagString(
								BookRegistry.COLOR_HEADER + 
								TextHelper.translateToLocal("achievement." + quest.getUniqueName()) + 
								TextHelper.getEnters(2) + 
								"�0" +
								TextHelper.translateToLocal("achievement." + quest.getUniqueName() + ".desc") + TextHelper.getEnters(2) +
								"--->"
								));
						pages.appendTag(new NBTTagString(
								"Layer 1:" + ENTER +
								"  �6T " + ENTER +
								"�6T�0E�6T" + ENTER +
								"  �6T " + TextHelper.getEnters(2) +
								"�0Layer 2:" + ENTER +
								"  �5O " + ENTER +
								"�5OOO" + ENTER +
								"  �5O "
								));
					}
					else if(quest instanceof QuestQuarry)
					{
						pages.appendTag(new NBTTagString(
								BookRegistry.COLOR_HEADER + 
								TextHelper.translateToLocal("achievement." + quest.getUniqueName()) + 
								TextHelper.getEnters(2) + 
								"�0" +
								TextHelper.translateToLocal("achievement." + quest.getUniqueName() + ".desc"
								)));
						pages.appendTag(new NBTTagString(
								"Quarry from top:" + ENTER +
								"           �aN           " + ENTER +
								"" + ENTER +
								"            �cR" + ENTER +
								"�aW          �0C�9D          �aE" + ENTER +
								"" + ENTER +
								"           �aS"
								));
					}
					else if(quest instanceof QuestAccelerationCrystal)
					{
						pages.appendTag(new NBTTagString(
								BookRegistry.COLOR_HEADER + 
								TextHelper.translateToLocal("achievement." + quest.getUniqueName()) + 
								TextHelper.getEnters(2) + 
								"�0" +
								TextHelper.translateToLocal("achievement." + quest.getUniqueName() + ".desc") + TextHelper.getEnters(2) +
								"--->"
								));
						pages.appendTag(new NBTTagString(
								"Crafting:" + TextHelper.getEnters(2) +
								"[ ][�6B�0][ ]" + ENTER +
								"[�6B�0][�8NS�0][�6B�0]" + ENTER +
								"[ ][�6B�0][ ]"
								));
					}
					else if(quest instanceof QuestLiquidSuppressionCrystal)
					{
						pages.appendTag(new NBTTagString(
								BookRegistry.COLOR_HEADER + 
								TextHelper.translateToLocal("achievement." + quest.getUniqueName()) + 
								TextHelper.getEnters(2) + 
								"�0" +
								TextHelper.translateToLocal("achievement." + quest.getUniqueName() + ".desc") + TextHelper.getEnters(2) +
								"--->"
								));
						pages.appendTag(new NBTTagString(
								"Crafting:" + TextHelper.getEnters(2) +
								"[�7B�0][�7B�0][�7B�0]" + ENTER +
								"[�7B�0][�8NS�0][�7B�0]" + ENTER +
								"[�7B�0][�7B�0][�7B�0]"
								));
					}
					else if(quest instanceof QuestMotherNatureCrystal)
					{
						pages.appendTag(new NBTTagString(
								BookRegistry.COLOR_HEADER + 
								TextHelper.translateToLocal("achievement." + quest.getUniqueName()) + 
								TextHelper.getEnters(2) + 
								"�0" +
								TextHelper.translateToLocal("achievement." + quest.getUniqueName() + ".desc") + TextHelper.getEnters(2) +
								"--->"
								));
						pages.appendTag(new NBTTagString(
								"Crafting:" + TextHelper.getEnters(2) +
								"[�2M�0][�aS�0][�2M�0]" + ENTER +
								"[�aS�0][�8NS�0][�aS�0]" + ENTER +
								"[�6P�0][�aS�0][�6P�0]"
								));
					}
					else if(quest instanceof QuestAutocrafting)
					{
						pages.appendTag(new NBTTagString(
								BookRegistry.COLOR_HEADER + 
								TextHelper.translateToLocal("achievement." + quest.getUniqueName()) + 
								TextHelper.getEnters(2) + 
								"�0" +
								TextHelper.translateToLocal("achievement." + quest.getUniqueName() + ".desc") + TextHelper.getEnters(2) +
								"--->"
								));
						pages.appendTag(new NBTTagString(
								"�0Layer 1: (�7X-Empty space, �6C-Chest�0)" + ENTER +
								"�7XXXXX" + ENTER +
								"�7XXXXX" + ENTER +
								"�7XX�6C�7XX" + ENTER +
								"�7XXXXX" + ENTER +
								"�7XXXXX" + TextHelper.getEnters(2) +
								"�0Layer 2: (�7X-Empty space, �0C-Cauldron�0)" + ENTER +
								"�7XXXXX" + ENTER +
								"�7XXXXX" + ENTER +
								"�7XX�0C�7XX" + ENTER +
								"�7XXXXX" + ENTER +
								"�7XXXXX"
								));
						pages.appendTag(new NBTTagString(
								"�0Layer 3: (�7X-Empty space, �6C-CraftingTable�0)" + ENTER +
								"�7XXXXX" + ENTER +
								"�7XXXXX" + ENTER +
								"�7XX�6C�7XX" + ENTER +
								"�7XXXXX" + ENTER +
								"�7XXXXX" + TextHelper.getEnters(2) +
								"�0Layer 4: (�7X-Empty space, �0H-Hopper�0)" + ENTER +
								"�7XXXXX" + ENTER +
								"�7XXXXX" + ENTER +
								"�7XX�0H�7XX" + ENTER +
								"�7XXXXX" + ENTER +
								"�7XXXXX"
								));
						pages.appendTag(new NBTTagString(
								"�0Layer 5: (�7X-Empty space, �6C-Chest�0)" + ENTER +
								"�6C�7X�6C�7X�6C" + ENTER +
								"�7XXXXX" + ENTER +
								"�6C�7X�6C�7X�6C" + ENTER +
								"�7XXXXX" + ENTER +
								"�6C�7X�6C�7X�6C"
								));
					}
					// Others
					else if(!(quest instanceof QuestCraftOnAltar) &&
							!(quest instanceof QuestCastSpell) &&
							!(quest instanceof QuestBuildAltar))
					{
						pages.appendTag(new NBTTagString(
								BookRegistry.COLOR_HEADER + 
								TextHelper.translateToLocal("achievement." + quest.getUniqueName()) + 
								TextHelper.getEnters(2) + 
								"�0" +
								TextHelper.translateToLocal("achievement." + quest.getUniqueName() + ".desc"
										)));
					}
				}
			}
			data.setTag("pages", pages);
			data.setString("author", BookRegistry.AUTHOR);
			data.setString("title", BookRegistry.BOOK_NAME_OTHER);
			data.setInteger(BookRegistry.BOOK_NBT_UID, getUID());
		}
		infoBook.setTagCompound(data);
		infoBook.setStackDisplayName(BookRegistry.BOOK_NAME_OTHER);
		return infoBook;
	}
}