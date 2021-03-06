package seia.vanillamagic.magic.spell.spells.summon;

import java.util.Random;

import javax.annotation.Nullable;

import org.apache.logging.log4j.Level;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import seia.vanillamagic.api.event.EventSpell;
import seia.vanillamagic.api.magic.IWand;
import seia.vanillamagic.api.util.VectorWrapper.Vector3D;
import seia.vanillamagic.config.VMConfig;
import seia.vanillamagic.core.VanillaMagic;
import seia.vanillamagic.magic.spell.Spell;
import seia.vanillamagic.util.EntityUtil;

public abstract class SpellSummon extends Spell 
{
	public SpellSummon(int spellID, String spellName, String spellUniqueName, IWand wand, 
			ItemStack itemOffHand) 
	{
		super(spellID, spellName, spellUniqueName, wand, itemOffHand);
	}
	
	public boolean castSpell(EntityPlayer caster, BlockPos pos, EnumFacing face, Vector3D hitVec) 
	{
		if (pos != null)
		{
			World world = caster.world;
			BlockPos spawnPos = pos.offset(face);
			Entity entity = getEntity(world); // entity to spawn after spell being casted
			if (entity != null)
			{
				if (entity instanceof EntityAgeable) ((EntityAgeable) entity).setGrowingAge(1);
				entity.setLocationAndAngles(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D, caster.rotationYaw, 0.0F);
				if (getSpawnWithArmor()) EntityUtil.addRandomArmorToEntity(entity);
				if (MinecraftForge.EVENT_BUS.post(new EventSpell.Cast.SummonSpell(this, caster, world, entity))) return false;
				world.spawnEntity(entity);
				Entity horse = getHorse(world);
				if (horse != null)
				{
					int rand = new Random().nextInt(100);
					if (rand < VMConfig.PERCENT_FOR_SPAWN_HOSTILE_ON_HORSE) entity.startRiding(horse);
				}
				world.updateEntities();
				return true;
			}
			else
				VanillaMagic.LOGGER.log(Level.INFO, "Wrong spellID. (spellID = " + getSpellID() + ")");
		}
		return false;
	}
	
	public boolean getSpawnWithArmor()
	{
		return false;
	}
	
	@Nullable
	public Entity getHorse(World world)
	{
		return null;
	}
	
	public abstract Entity getEntity(World world);
}