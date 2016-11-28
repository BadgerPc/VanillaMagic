package seia.vanillamagic.integration;

import net.minecraftforge.fml.common.event.FMLInterModComms;
import seia.vanillamagic.core.VanillaMagic;

public class IntegrationVersionChecker implements IIntegration
{
	public String getModName()
	{
		return "VersionChecker";
	}
	
	public boolean init() throws Exception
	{
		VanillaMagicIntegration.TAG.setString("curseProjectName", "vanilla-magic");
		VanillaMagicIntegration.TAG.setString("curseFilenameParser", VanillaMagic.MODID + "-[].jar");
		FMLInterModComms.sendRuntimeMessage(VanillaMagic.MODID, 
				"VersionChecker", "addCurseCheck", VanillaMagicIntegration.TAG);
		return true;
	}
}