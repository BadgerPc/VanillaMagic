package seia.vanillamagic.integration;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Level;

import net.minecraft.nbt.NBTTagCompound;
import seia.vanillamagic.core.VanillaMagic;

/**
 * Integration is always done at the end of each phase (PreInit, Init, PostInit).
 */
public class VanillaMagicIntegration
{
	public static final NBTTagCompound TAG = new NBTTagCompound();
	public static final List<IIntegration> INTEGRATIONS = new ArrayList<IIntegration>();
	
	private VanillaMagicIntegration()
	{
	}
	
	static
	{
		/*
		 * Comment any of the following to disable integration.
		 */
		INTEGRATIONS.add(new IntegrationVersionChecker());
		INTEGRATIONS.add(new IntegrationBetterAchievements());
		INTEGRATIONS.add(new IntegrationWTFExpedition());
		INTEGRATIONS.add(new IntegrationFilledOres());
		INTEGRATIONS.add(new IntegrationSuperOres());
		
		INTEGRATIONS.add(new IntegrationNetherMetals());
		INTEGRATIONS.add(new IntegrationEndMetals());
		INTEGRATIONS.add(new IntegrationDenseMetals());
	}
	
	public static void preInit()
	{
		for(IIntegration i : INTEGRATIONS)
		{
			try
			{
				if(i.preInit())
				{
					VanillaMagic.LOGGER.log(Level.INFO, "[PRE-INIT] " + i.getModName() + " integration - enabled");
				}
			}
			catch(Exception e)
			{
				VanillaMagic.LOGGER.log(Level.INFO, "[PRE-INIT] " + i.getModName() + " integration - failed");
			}
		}
	}
	
	public static void init()
	{
		for(IIntegration i : INTEGRATIONS)
		{
			try
			{
				if(i.init())
				{
					VanillaMagic.LOGGER.log(Level.INFO, "[INIT] " + i.getModName() + " integration - enabled");
				}
			}
			catch(Exception e)
			{
				VanillaMagic.LOGGER.log(Level.INFO, "[INIT] " + i.getModName() + " integration - failed");
			}
		}
	}
	
	public static void postInit()
	{
		for(IIntegration i : INTEGRATIONS)
		{
			try
			{
				if(i.postInit())
				{
					VanillaMagic.LOGGER.log(Level.INFO, "[POST-INIT] " + i.getModName() + " integration - enabled");
				}
			}
			catch(Exception e)
			{
				VanillaMagic.LOGGER.log(Level.INFO, "[POST-INIT] " + i.getModName() + " integration - failed");
			}
		}
	}
}