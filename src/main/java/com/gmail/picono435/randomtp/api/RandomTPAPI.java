package com.gmail.picono435.randomtp.api;

import java.util.Map;
import java.util.Random;

import com.gmail.picono435.randomtp.config.Config;
import com.gmail.picono435.randomtp.config.Messages;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ITeleporter;

public class RandomTPAPI {
	
	public static Block[] dangerBlockArray = { Blocks.LAVA, Blocks.WATER, Blocks.AIR };
	
	public static void randomTeleport(EntityPlayer p, World world) {
		  Random r = new Random();
		  int lowX = ((int)Math.round(Math.abs(p.getPosition().getX())) + Config.min_distance) * -1;
		  int highX = Math.abs((int)Math.round(p.getPosition().getX()) + Config.max_distance);
		  int lowZ = ((int)Math.round(Math.abs(p.getPosition().getZ())) + Config.min_distance) * -1;
		  int highZ = Math.abs((int)Math.round(p.getPosition().getZ()) + Config.max_distance);
		  if(Config.max_distance == 0) {
				highX = (int) (world.getWorldBorder().getDiameter() / 2);
				highZ = (int) (world.getWorldBorder().getDiameter() / 2);
		  }
		  int x = r.nextInt(highX-lowX) + lowX;
		  int y = 50;
		  int z = r.nextInt(highZ-lowZ) + lowZ;
		  int maxTries = Config.maxTries;
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
				  TextComponentString msg = new TextComponentString(Messages.maxTries.replaceAll("\\{playerName\\}", p.getName()).replaceAll("&", "§"));
				  p.sendMessage(msg);
				  return;
			  }
		  }
		  
		  final int xClas = x;
		  final int yClas = y;
		  final int zClas = z;
		  p.changeDimension(world.provider.getDimension(), new ITeleporter() {
				@Override
				public void placeEntity(World world, Entity entity, float yaw) {
					entity.setWorld(world);
					entity.setPositionAndUpdate(xClas, yClas, zClas);
				}
	      });
		  TextComponentString succefull = new TextComponentString(Messages.succefully.replaceAll("\\{playerName\\}", p.getName()).replaceAll("\\{blockX\\}", "" + (int)p.getPositionVector().x).replaceAll("\\{blockY\\}", "" + (int)p.getPositionVector().y).replaceAll("\\{blockZ\\}", "" + (int)p.getPositionVector().z).replaceAll("&", "§"));
		  p.sendMessage(succefull);
	  }
	
	public static boolean checkCooldown(EntityPlayer p, Map<String, Long> cooldowns) {
		  int cooldownTime = Config.cooldown;
		  if(cooldowns.containsKey(p.getName())) {
			  long secondsLeft = ((cooldowns.get(p.getName())/1000)+cooldownTime) - (System.currentTimeMillis()/1000);
		      if(secondsLeft > 0) {
		    	  return false;
		      } else {
		    	  return true;
		      }
		  } else {
			  return true;
		  }
	  }
	  
	  public static long getCooldownLeft(EntityPlayer p, Map<String, Long> cooldowns) {
		  int cooldownTime = Config.cooldown;
		  long secondsLeft = ((cooldowns.get(p.getName())/1000)+cooldownTime) - (System.currentTimeMillis()/1000);
		  return secondsLeft;
	  }
	  
	  public static boolean isSafe(World world, int newX, int newY, int newZ) {
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
