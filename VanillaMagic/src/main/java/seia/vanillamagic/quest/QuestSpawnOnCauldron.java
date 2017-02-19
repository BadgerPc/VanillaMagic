package seia.vanillamagic.quest;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.BlockCauldron;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import seia.vanillamagic.magic.wand.WandRegistry;
import seia.vanillamagic.util.CauldronHelper;
import seia.vanillamagic.util.ItemStackHelper;

public abstract class QuestSpawnOnCauldron extends Quest
{
	int times = 0;
	@SubscribeEvent
	public void craftUpgrade(RightClickBlock event)
	{
		EntityPlayer player = event.getEntityPlayer();
		World world = event.getWorld();
//		if(world.isRemote)
//		{
//			return;
//		}
		
		if(times == 0)
		{
			times++;
		}
		else
		{
			times = 0;
			return;
		}
		
		BlockPos clickedPos = event.getPos();
		ItemStack rightHand = player.getHeldItemMainhand();
		if(ItemStackHelper.isNullStack(rightHand))
		{
			return;
		}
		if(WandRegistry.areWandsEqual(WandRegistry.WAND_BLAZE_ROD.getWandStack(), rightHand))
		{
			IBlockState clickedBlock = world.getBlockState(clickedPos);
			if(clickedBlock.getBlock() instanceof BlockCauldron)
			{
				List<EntityItem> inCauldron = CauldronHelper.getItemsInCauldron(world, clickedPos);
				EntityItem base = getBaseStack(inCauldron);
				if(base == null)
				{
					return;
				}
				if(ItemStackHelper.isNullStack(base.getEntityItem()))
				{
					return;
				}
				if(!canGetUpgrade(base.getEntityItem()))
				{
					return;
				}
				List<EntityItem> ingredients = getIngredients(base, inCauldron);
				if(ingredients == null)
				{
					return;
				}
				if(ingredients.size() <= 0)
				{
					return;
				}
				ItemStack craftingResult = getResult(base, ingredients);
				if(ItemStackHelper.isNullStack(craftingResult))
				{
					return;
				}
				if(!player.hasAchievement(achievement))
				{
					player.addStat(achievement, 1);
				}
				if(player.hasAchievement(achievement))
				{
					// Remove necessary stacks
					world.removeEntity(base);
					for(EntityItem ei : ingredients)
					{
						world.removeEntity(ei);
					}
					
					EntityItem craftingResultEntity = new EntityItem(world, clickedPos.getX(), clickedPos.getY() + 1, clickedPos.getZ(), craftingResult);
					world.spawnEntity(craftingResultEntity);
					// Particle + sound
					{
						world.playSound((double)clickedPos.getX() + 0.5D, (double)clickedPos.getY(), (double)clickedPos.getZ() + 0.5D, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
						Random rand = new Random();
						double particleX = (double)clickedPos.getX() + 0.5D;
						double particleY = (double)clickedPos.getY() + rand.nextDouble() * 6.0D / 16.0D;
						double particleZ = (double)clickedPos.getZ() + 0.5D;
						double randomPos = rand.nextDouble() * 0.6D - 0.3D;
						world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, particleX - 0.52D, particleY, particleZ + randomPos, 0.0D, 0.0D, 0.0D, new int[0]);
						world.spawnParticle(EnumParticleTypes.FLAME, particleX - 0.52D, particleY, particleZ + randomPos, 0.0D, 0.0D, 0.0D, new int[0]);
						world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, particleX + 0.52D, particleY, particleZ + randomPos, 0.0D, 0.0D, 0.0D, new int[0]);
						world.spawnParticle(EnumParticleTypes.FLAME, particleX + 0.52D, particleY, particleZ + randomPos, 0.0D, 0.0D, 0.0D, new int[0]);
						world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, particleX + randomPos, particleY, particleZ - 0.52D, 0.0D, 0.0D, 0.0D, new int[0]);
						world.spawnParticle(EnumParticleTypes.FLAME, particleX + randomPos, particleY, particleZ - 0.52D, 0.0D, 0.0D, 0.0D, new int[0]);
						world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, particleX + randomPos, particleY, particleZ + 0.52D, 0.0D, 0.0D, 0.0D, new int[0]);
						world.spawnParticle(EnumParticleTypes.FLAME, particleX + randomPos, particleY, particleZ + 0.52D, 0.0D, 0.0D, 0.0D, new int[0]);
					}
				}
			}
		}
	}
	
	@Nullable
	public EntityItem getBaseStack(List<EntityItem> inCauldron) 
	{
		for(EntityItem ei : inCauldron)
		{
			if(isBaseItem(ei))
			{
				return ei;
			}
		}
		return null;
	}
	
	public List<EntityItem> getIngredients(EntityItem baseItem, List<EntityItem> inCauldron)
	{
		List<EntityItem> list = new ArrayList<>();
		for(EntityItem ei : inCauldron)
		{
			if(!isBaseItem(ei))
			{
				list.add(ei);
			}
		}
		return list;
	}
	
	@Nullable
	public ItemStack getResult(EntityItem base, List<EntityItem> ingredients)
	{
		for(int i = 0; i < ingredients.size(); ++i)
		{
			ItemStack result = getResultSingle(base, ingredients.get(i));
			if(result != null)
			{
				return result;
			}
		}
		return null;
	}
	
	public abstract boolean isBaseItem(EntityItem entityItem);
	
	public abstract boolean canGetUpgrade(ItemStack base);
	
	public abstract ItemStack getResultSingle(EntityItem base, EntityItem ingredient);
}