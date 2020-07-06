package com.gmail.picono435.randomtp.commands;

import java.math.BigDecimal;
import java.util.HashMap;

import com.gmail.picono435.randomtp.MainMod;
import com.gmail.picono435.randomtp.config.Config;
import com.gmail.picono435.randomtp.config.Messages;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.DimensionArgument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.server.permission.PermissionAPI;

public class RTPDCommand {
	
	private static HashMap<String, Long> cooldowns = new HashMap<String, Long>();
	
	public static void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(Commands.literal("rtpd").requires(source -> hasPermission(source))
				.then(
						Commands.argument("dimension", DimensionArgument.getDimension())
						.executes(context -> 
						randomTeleport(context.getSource().asPlayer(), DimensionArgument.func_212592_a(context, "dimension"))
						)
				));
		dispatcher.register(Commands.literal("randomteleportdimension").requires(source -> hasPermission(source))
				.then(
						Commands.argument("dimension", DimensionArgument.getDimension())
						.executes(context -> 
						randomTeleport(context.getSource().asPlayer(), DimensionArgument.func_212592_a(context, "dimension"))
						)
				));
		dispatcher.register(Commands.literal("randomtpd").requires(source -> hasPermission(source))
				.then(
						Commands.argument("dimension", DimensionArgument.getDimension())
						.executes(context ->
						randomTeleport(context.getSource().asPlayer(), DimensionArgument.func_212592_a(context, "dimension"))
						)
				));
	}
	
	private static int randomTeleport(PlayerEntity p, DimensionType dim) {
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
	    	int dimensionId = dim.getId();
	    	p.setPortal(p.getPosition());
	    	if(!inWhitelist(dimensionId)) {
	    		p.sendMessage(new StringTextComponent(Messages.dimensionNotAllowed.get().replaceAll("\\{playerName\\}", p.getName().getString()).replaceAll("\\{dimensionId\\}", dim.getId() + "").replace('&', '§')));
	    		return 1;
	    	}
	    	p.changeDimension(dim);
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
	
	  public static boolean inWhitelist(int dimension) {
		  //WHITELIST
		  if(Config.useWhitelist.get()) {
			  return Config.allowedDimensions.get().contains(dimension + "");
		  //BLACKLIST
		  } else {
			  return !Config.allowedDimensions.get().contains(dimension + ""); 
		  }
	  }
	  
	private static boolean hasPermission(CommandSource source) {
		try {
			return PermissionAPI.hasPermission(source.asPlayer(), "randomtp.command.basic");
		} catch (CommandSyntaxException e) {
			return false;
		}
	}
}
