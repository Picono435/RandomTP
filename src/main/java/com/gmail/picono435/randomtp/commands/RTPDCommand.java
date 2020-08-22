package com.gmail.picono435.randomtp.commands;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Random;

import com.gmail.picono435.randomtp.MainMod;
import com.gmail.picono435.randomtp.config.Config;
import com.gmail.picono435.randomtp.config.Messages;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.DimensionArgument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.server.permission.PermissionAPI;

public class RTPDCommand {
	
	private static HashMap<String, Long> cooldowns = new HashMap<String, Long>();
	
	private static Block[] dangerBlockArray = { Blocks.LAVA, Blocks.WATER, Blocks.AIR };
	
	public static void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(Commands.literal("rtpd").requires(source -> hasPermission(source))
				.then(
						Commands.argument("dimension", DimensionArgument.getDimension())
						.executes(context -> 
						runCommand(context.getSource().asPlayer(), DimensionArgument.getDimensionArgument(context, "dimension"))
						)
				));
		dispatcher.register(Commands.literal("randomteleportdimension").requires(source -> hasPermission(source))
				.then(
						Commands.argument("dimension", DimensionArgument.getDimension())
						.executes(context -> 
						runCommand(context.getSource().asPlayer(), DimensionArgument.getDimensionArgument(context, "dimension"))
						)
				));
		dispatcher.register(Commands.literal("randomtpd").requires(source -> hasPermission(source))
				.then(
						Commands.argument("dimension", DimensionArgument.getDimension())
						.executes(context ->
						runCommand(context.getSource().asPlayer(), DimensionArgument.getDimensionArgument(context, "dimension"))
						)
				));
	}
	
	private static int runCommand(PlayerEntity p, DimensionType dim) {
		World world = p.getEntityWorld();
	    WorldBorder border = world.getWorldBorder();
	    MinecraftServer server = MainMod.server;
	    StringTextComponent succefull = new StringTextComponent(Messages.succefully.get().replaceAll("\\{playerName\\}", p.getName().getString()).replaceAll("\\{blockZ\\}", "" + p.getPositionVector().z).replaceAll("\\{blockX\\}", "" + p.getPositionVector().x).replaceAll("&", "§"));
	    if(!checkCooldown(p)) {
	    	long secondsLeft = getCooldownLeft(p);
	    	StringTextComponent cooldownmes = new StringTextComponent(Messages.cooldown.get().replaceAll("\\{secondsLeft\\}", Long.toString(secondsLeft)).replaceAll("\\{playerName\\}", p.getName().getString()).replaceAll("&", "§"));
	        p.sendMessage(cooldownmes);
	        return 1;
	    } else {
	    	double cal = border.getDiameter()/2;
	    	BigDecimal num = new BigDecimal(cal);
	    	String maxDistance = num.toPlainString();
	    	String dimensionId = dim.getRegistryName().toString();
	    	p.setPortal(p.getPosition());
	    	if(!inWhitelist(dimensionId)) {
	    		p.sendMessage(new StringTextComponent(Messages.dimensionNotAllowed.get().replaceAll("\\{playerName\\}", p.getName().getString()).replaceAll("\\{dimensionId\\}", dimensionId + "").replace('&', '§')));
	    		return 1;
	    	}
	    	p.changeDimension(dim);
	    	if(Config.useOriginal.get()) {
	    		randomTeleport(p);
	    		cooldowns.put(p.getName().getString(), System.currentTimeMillis());
	    		return 1;
	    	}
	        if(Config.max_distance.get() == 0) {
	        	server.getCommandManager().handleCommand(server.getCommandSource(), "spreadplayers " + border.getCenterX() + " " + border.getCenterZ() + " " + Config.min_distance.get() + " " + maxDistance + " false " + p.getName().getString().toLowerCase());
	        	p.sendMessage(succefull);
	        } else {
	    		server.getCommandManager().handleCommand(server.getCommandSource(), "spreadplayers " + border.getCenterX() + " " + border.getCenterZ() + " " + Config.min_distance.get() + " " + Config.max_distance.get() + " false " + p.getName().getString().toLowerCase());
	    		p.sendMessage(succefull);
	        }
	        cooldowns.put(p.getName().getString(), System.currentTimeMillis());
	    }
		return 1;
	}
	
	private static boolean checkCooldown(PlayerEntity p) {
		  int cooldownTime = Config.cooldown.get();
		  if(cooldowns.containsKey(p.getName().getString())) {
			  long secondsLeft = ((cooldowns.get(p.getName().getString())/1000)+cooldownTime) - (System.currentTimeMillis()/1000);
		      if(secondsLeft > 0) {
		    	  return false;
		      } else {
		    	  cooldowns.remove(p.getName().getString());
		    	  return true;
		      }
		  } else {
			  return true;
		  }
	  }
	  
	  private static long getCooldownLeft(PlayerEntity p) {
		  int cooldownTime = Config.cooldown.get();
		  long secondsLeft = ((cooldowns.get(p.getName().getString())/1000)+cooldownTime) - (System.currentTimeMillis()/1000);
		  return secondsLeft;
	  }
	
	  public static boolean inWhitelist(String dimension) {
		  //WHITELIST
		  if(Config.useWhitelist.get()) {
			  return Config.allowedDimensions.get().contains(dimension);
		  //BLACKLIST
		  } else {
			  return !Config.allowedDimensions.get().contains(dimension); 
		  }
	  }
	  
	private static boolean hasPermission(CommandSource source) {
		try {
			return PermissionAPI.hasPermission(source.asPlayer(), "randomtp.command.basic");
		} catch (CommandSyntaxException e) {
			return false;
		}
	}
	
	private static void randomTeleport(PlayerEntity p) {
		try  {
			Random r = new Random();
			  int low = Config.min_distance.get();
			  int high = Config.max_distance.get();
			  if(high == 0) {
				  high = (int) (p.world.getWorldBorder().getDiameter() / 2);
			  }
			  int x = r.nextInt(high-low) + low;
			  int y = 50;
			  int z = r.nextInt(high-low) + low;
			  int maxTries = Config.maxTries.get();
			  while (!isSafe(p, x, y, z) && (maxTries == -1 || maxTries > 0)) {
				  y++;
				  if(y >= 120) {
					  x = r.nextInt(high-low) + low;
					  y = 50;
					  z = r.nextInt(high-low) + low;
					  continue;
				  }
				  if(maxTries > 0){
					  maxTries--;
				  }
				  if(maxTries == 0) {
					  StringTextComponent msg = new StringTextComponent("Error, please try again.");
					  p.sendMessage(msg);
					  return;
				  }
			  }
			  
			  p.setPositionAndUpdate(x, y, z);
			  StringTextComponent succefull = new StringTextComponent(Messages.succefully.get().replaceAll("\\{playerName\\}", p.getName().getString()).replaceAll("\\{blockZ\\}", "" + p.getPositionVec().z).replaceAll("\\{blockX\\}", "" + p.getPositionVec().x).replaceAll("&", "§"));
			  p.sendMessage(succefull);
		} catch(Exception ex) {
			MainMod.logger.info("Error executing command.");
			ex.printStackTrace();
		}
	  }
	  
	  private static boolean isSafe(PlayerEntity player, int newX, int newY, int newZ) {
		if ((isEmpty(player.world, newX, newY, newZ)) && 
				(!isDangerBlock(player.world, newX, newY - 1, newZ))) {
			return true;
		}
		return false;
	  }
		  
	  private static boolean isEmpty(World world, int newX, int newY, int newZ) {
		if ((world.isAirBlock(new BlockPos(newX, newY, newZ))) && (world.isAirBlock(new BlockPos(newX, newY + 1, newZ))) && 
				(world.isAirBlock(new BlockPos(newX + 1, newY, newZ))) && (world.isAirBlock(new BlockPos(newX - 1, newY, newZ))) && 
				(world.isAirBlock(new BlockPos(newX, newY, newZ + 1))) && (world.isAirBlock(new BlockPos(newX, newY, newZ - 1)))) {
			return true;
		}
		return false;
	  }
		  
	  private static boolean isDangerBlock(World world, int newX, int newY, int newZ) {
		for (Block block : dangerBlockArray) {
			if (block.equals(world.getBlockState(new BlockPos(newX, newY, newZ)).getBlock())) {
				return true;
			}
		}
		return false;
	  }
}
