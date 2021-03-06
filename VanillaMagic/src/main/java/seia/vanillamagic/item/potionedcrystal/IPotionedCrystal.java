package seia.vanillamagic.item.potionedcrystal;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionType;
import seia.vanillamagic.api.item.ICustomItem;
import seia.vanillamagic.util.TextUtil;

/**
 * Interface used to dynamically create PotionedCrystals for each registered Potion.
 */
public interface IPotionedCrystal extends ICustomItem
{
	public static final String NBT_POTION_TYPE_NAME = "NBT_POTION_TYPE_NAME";
	
	/**
	 * @return Returns the type of this Potion.
	 */
	PotionType getPotionType();
	
	/**
	 * @return Returns the Crystal.
	 */
	default ItemStack getItem()
	{
		ItemStack stack = new ItemStack(Items.NETHER_STAR);
		stack.setStackDisplayName("Potioned Crystal: " + 
									TextUtil.translateToLocal(getPotionLocalizedName()));
		NBTTagCompound stackTag = stack.getTagCompound();
		stackTag.setString(NBT_UNIQUE_NAME, getUniqueNBTName());
		stackTag.setString(NBT_POTION_TYPE_NAME, getPotionUnlocalizedName());
		return stack;
	}
	
	/**
	 * For instance -> "water_breathing"
	 * 
	 * @return Returns the unlocalized name of the potion.
	 * 
	 * @see #getPotionLocalizedName()
	 */
	default String getPotionUnlocalizedName()
	{
		return PotionedCrystalHelper.getPotionTypeName(getPotionType());
	}
	
	/**
	 * For instance -> "Potion of Water Breathing"
	 * 
	 * @return Returns the localized name of the potion.
	 * 
	 * @see #getPotionUnlocalizedName()
	 */
	default String getPotionLocalizedName()
	{
		return getPotionType().getNamePrefixed("potion.effect.");
	}
	
	/**
	 * PotionedCrstals are created using outside technique (craft inside Cauldron).
	 */
	default void registerRecipe()
	{
	}
}