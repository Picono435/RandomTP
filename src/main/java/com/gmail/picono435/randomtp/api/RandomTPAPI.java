package com.gmail.picono435.randomtp.api;

import java.util.Map;
import java.util.Random;

import com.gmail.picono435.randomtp.MainMod;
import com.gmail.picono435.randomtp.config.Config;
import com.gmail.picono435.randomtp.config.Messages;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.server.permission.PermissionAPI;

public class RandomTPAPI {
	
	public static Block[] dangerBlockArray = { Blocks.LAVA, Blocks.WATER, Blocks.AIR };
	
	public static void randomTeleport(PlayerEntity p, ServerWorld world) {
		try  {
			Random r = new Random();
			int lowX = ((int)Math.round(Math.abs(p.getPosX())) + Config.min_distance.get()) * -1;
			int highX = Math.abs((int)Math.round(p.getPosX()) + Config.max_distance.get());
			int lowZ = ((int)Math.round(Math.abs(p.getPosZ())) + Config.min_distance.get()) * -1;
			int highZ = Math.abs((int)Math.round(p.getPosZ()) + Config.max_distance.get());
			if(Config.max_distance.get() == 0) {
				highX = (int) (world.getWorldBorder().getDiameter() / 2);
				highZ = (int) (world.getWorldBorder().getDiameter() / 2);
			}
			int x = r.nextInt(highX-lowX) + lowX;
			int y = 50;
			int z = r.nextInt(highZ-lowZ) + lowZ;
			int maxTries = Config.maxTries.get();
			while (!isSafe(world, x, y, z) && (maxTries == -1 || maxTries > 0)) {
				y++;
				if(y >= 120) {
					  x = r.nextInt(highX-lowX) + lowX;
					  y = 50;
					  z = r.nextInt(highZ-lowZ) + lowZ;
					  continue;
				}
				if(maxTries > 0){
					  maxTries--;
				}
				if(maxTries == 0) {
					  StringTextComponent msg = new StringTextComponent(Messages.maxTries.get().replaceAll("\\{playerName\\}", p.getName().getString()).replaceAll("&", "§"));
					  p.sendMessage(msg, p.getUniqueID());
					  return;
				}
			}
			
			((ServerPlayerEntity) p).teleport(world, x, y, z, p.rotationPitch, p.rotationYaw);
			StringTextComponent succefull = new StringTextComponent(Messages.succefully.get().replaceAll("\\{playerName\\}", p.getName().getString()).replaceAll("\\{blockX\\}", "" + (int)p.getPositionVec().x).replaceAll("\\{blockY\\}", "" + (int)p.getPositionVec().y).replaceAll("\\{blockZ\\}", "" + (int)p.getPositionVec().z).replaceAll("&", "§"));
			p.sendMessage(succefull, p.getUniqueID());
		} catch(Exception ex) {
			MainMod.logger.info("Error executing command.");
			ex.printStackTrace();
		}
	  }
	
	public static ServerWorld getWorld(String world, MinecraftServer server) {
    	try {
    		ResourceLocation resourcelocation = ResourceLocation.tryCreate(world);
	    	RegistryKey<World> registrykey = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, resourcelocation);
	    	ServerWorld worldTo = server.getWorld(registrykey);
	    	return worldTo;
    	} catch(Exception ex) {
    		ex.printStackTrace();
    		return null;
    	}
	}
	
	public static boolean checkCooldown(PlayerEntity p, Map<String, Long> cooldowns) {
		  int cooldownTime = Config.cooldown.get();
		  if(cooldowns.containsKey(p.getName().getString())) {
			  long secondsLeft = ((cooldowns.get(p.getName().getString())/1000)+cooldownTime) - (System.currentTimeMillis()/1000);
		      if(secondsLeft > 0) {
		    	  return false;
		      } else {
		    	  return true;
		      }
		  } else {
			  return true;
		  }
	  }
	  
	  public static long getCooldownLeft(PlayerEntity p, Map<String, Long> cooldowns) {
		  int cooldownTime = Config.cooldown.get();
		  long secondsLeft = ((cooldowns.get(p.getName().getString())/1000)+cooldownTime) - (System.currentTimeMillis()/1000);
		  return secondsLeft;
	  }
	
	public static boolean hasPermission(CommandSource source, String permission) {
		try {
			return PermissionAPI.hasPermission(source.asPlayer(), permission);
		} catch (CommandSyntaxException e) {
			return false;
		}
	}
	  
	  public static boolean isSafe(ServerWorld world, int newX, int newY, int newZ) {
		if(newX >= world.getWorldBorder().maxX() || newZ >= world.getWorldBorder().maxZ()) return false;
		if ((isEmpty(world, newX, newY, newZ)) && 
				(!isDangerBlock(world, newX, newY - 1, newZ))) {
			return true;
		}
		return false;
	  }
		  
	  public static boolean isEmpty(World world, int newX, int newY, int newZ) {
		if ((world.isAirBlock(new BlockPos(newX, newY, newZ))) && (world.isAirBlock(new BlockPos(newX, newY + 1, newZ))) && 
				(world.isAirBlock(new BlockPos(newX + 1, newY, newZ))) && (world.isAirBlock(new BlockPos(newX - 1, newY, newZ))) && 
				(world.isAirBlock(new BlockPos(newX, newY, newZ + 1))) && (world.isAirBlock(new BlockPos(newX, newY, newZ - 1)))) {
			return true;
		}
		return false;
	  }
		  
	  public static boolean isDangerBlock(World world, int newX, int newY, int newZ) {
		for (Block block : dangerBlockArray) {
			if (block.equals(world.getBlockState(new BlockPos(newX, newY, newZ)).getBlock())) {
				return true;
			}
		}
		return false;
	  }
}
