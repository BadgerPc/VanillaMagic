package seia.vanillamagic.tileentity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.registry.GameRegistry;
import seia.vanillamagic.item.liquidsuppressioncrystal.TileLiquidSuppression;
import seia.vanillamagic.tileentity.blockabsorber.TileBlockAbsorber;
import seia.vanillamagic.tileentity.chunkloader.TileChunkLoader;
import seia.vanillamagic.tileentity.inventorybridge.TileInventoryBridge;
import seia.vanillamagic.tileentity.machine.autocrafting.TileAutocrafting;
import seia.vanillamagic.tileentity.machine.farm.TileFarm;
import seia.vanillamagic.tileentity.machine.quarry.TileQuarry;
import seia.vanillamagic.tileentity.machine.speedy.TileSpeedy;

public class TileEntityRegistry 
{
	public static final List<Class<? extends TileEntity>> REGISTERED_CLASSES = new ArrayList<Class<? extends TileEntity>>();
	
	private TileEntityRegistry()
	{
	}
	
	public static void preInit()
	{
		// Register TileEntities
		register(TileQuarry.class, TileQuarry.REGISTRY_NAME);
		register(TileChunkLoader.class, TileChunkLoader.REGISTRY_NAME);
		register(TileFarm.class, TileFarm.REGISTRY_NAME);
		register(TileAutocrafting.class, TileAutocrafting.REGISTRY_NAME);
		register(TileSpeedy.class, TileSpeedy.REGISTRY_NAME);
		register(TileLiquidSuppression.class, TileLiquidSuppression.REGISTRY_NAME);
		register(TileBlockAbsorber.class, TileBlockAbsorber.REGISTRY_NAME);
		register(TileInventoryBridge.class, TileInventoryBridge.REGISTRY_NAME);
	}
	
	private static void register(Class<? extends TileEntity> tileEntityClass, String id)
	{
		GameRegistry.registerTileEntity(tileEntityClass, id);
		REGISTERED_CLASSES.add(tileEntityClass);
	}
}