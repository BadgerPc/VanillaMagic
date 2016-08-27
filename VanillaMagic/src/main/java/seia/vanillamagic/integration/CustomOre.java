package seia.vanillamagic.integration;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

public class CustomOre 
{
	public static final CustomOre INSTANCE = new CustomOre();
	
	public final List<Block> customOreRedstone = new ArrayList<Block>();
	public final List<Block> customOreDiamond = new ArrayList<Block>();
	public final List<Block> customObsidian = new ArrayList<Block>();
	
	private CustomOre()
	{
		getCustomRedstoneOre();
		getCustomDiamondOre();
	}
	
	private void getCustomRedstoneOre()
	{
		customOreRedstone.add(Blocks.REDSTONE_ORE);
		customOreRedstone.add(Blocks.LIT_REDSTONE_ORE); // OreDictionary don't add this as oreRedstone
	}
	
	private void getCustomDiamondOre()
	{
		customOreDiamond.add(Blocks.DIAMOND_ORE);
	}
	
	private void getCustomObsidian()
	{
		customObsidian.add(Blocks.OBSIDIAN);
	}
}