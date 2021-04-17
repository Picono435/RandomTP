package com.gmail.picono435.randomtp.commands;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.gmail.picono435.randomtp.api.RandomTPAPI;
import com.gmail.picono435.randomtp.config.Config;
import com.gmail.picono435.randomtp.config.Messages;
import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.DimensionArgument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.server.permission.PermissionAPI;

public class RTPDCommand {
	
	private static Map<String, Long> cooldowns = new HashMap<String, Long>();
		
	public static void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(Commands.literal("rtpd").requires(source -> RandomTPAPI.hasPermission(source, "randomtp.command.interdim"))
				.then(
						Commands.argument("dimension", DimensionArgument.getDimension())
						.executes(context -> 
						runCommand(context.getSource().asPlayer(), DimensionArgument.getDimensionArgument(context, "dimension"))
						)
				));
		dispatcher.register(Commands.literal("randomteleportdimension").requires(source -> RandomTPAPI.hasPermission(source, "randomtp.command.interdim"))
				.then(
						Commands.argument("dimension", DimensionArgument.getDimension())
						.executes(context -> 
						runCommand(context.getSource().asPlayer(), DimensionArgument.getDimensionArgument(context, "dimension"))
						)
				));
		dispatcher.register(Commands.literal("randomtpd").requires(source -> RandomTPAPI.hasPermission(source, "randomtp.command.interdim"))
				.then(
						Commands.argument("dimension", DimensionArgument.getDimension())
						.executes(context ->
						runCommand(context.getSource().asPlayer(), DimensionArgument.getDimensionArgument(context, "dimension"))
						)
				));
	}
	
	private static int runCommand(PlayerEntity p, ServerWorld dim) {
	    if(!RandomTPAPI.checkCooldown(p, cooldowns) && !PermissionAPI.hasPermission(p, "randomtp.cooldown.exempt")) {
	    	long secondsLeft = RandomTPAPI.getCooldownLeft(p, cooldowns);
	    	StringTextComponent cooldownmes = new StringTextComponent(Messages.cooldown.get().replaceAll("\\{secondsLeft\\}", Long.toString(secondsLeft)).replaceAll("\\{playerName\\}", p.getName().getString()).replaceAll("&", "§"));
	        p.sendMessage(cooldownmes, p.getUniqueID());
	        return 1;
	    } else {
	    	cooldowns.remove(p.getName().getString());
	    	String dimensionId = dim.getDimensionKey().getLocation().getNamespace() + ":" + dim.getDimensionKey().getLocation().getPath();
	    	if(!inWhitelist(dimensionId)) {
	    		p.sendMessage(new StringTextComponent(Messages.dimensionNotAllowed.get().replaceAll("\\{playerName\\}", p.getName().getString()).replaceAll("\\{dimensionId\\}", dimensionId + "").replace('&', '§')), p.getUniqueID());
	    		return 1;
	    	}
	    	if(Config.useOriginal.get()) {
	    		RandomTPAPI.randomTeleport(p, dim);
	    		cooldowns.put(p.getName().getString(), System.currentTimeMillis());
	    		return 1;
	    	}
	    	p.setPortal(p.getPosition());
	    	p.changeDimension(dim);
	    	double cal = p.world.getWorldBorder().getDiameter()/2;
	    	BigDecimal num = new BigDecimal(cal);
	    	String maxDistance = num.toPlainString();
	        if(Config.max_distance.get() == 0) {
	        	p.getServer().getCommandManager().handleCommand(p.getServer().getCommandSource(), "spreadplayers " + p.world.getWorldBorder().getCenterX() + " " + p.world.getWorldBorder().getCenterZ() + " " + Config.min_distance.get() + " " + maxDistance + " false " + p.getName().getString().toLowerCase());
	        	StringTextComponent succefull = new StringTextComponent(Messages.succefully.get().replaceAll("\\{playerName\\}", p.getName().getString()).replaceAll("\\{blockX\\}", "" + (int)p.getPositionVec().x).replaceAll("\\{blockY\\}", "" + (int)p.getPositionVec().y).replaceAll("\\{blockZ\\}", "" + (int)p.getPositionVec().z).replaceAll("&", "§"));
	        	p.sendMessage(succefull, p.getUniqueID());
	        } else {
	    		p.getServer().getCommandManager().handleCommand(p.getServer().getCommandSource(), "spreadplayers " + p.world.getWorldBorder().getCenterX() + " " + p.world.getWorldBorder().getCenterZ() + " " + Config.min_distance.get() + " " + Config.max_distance.get() + " false " + p.getName().getString().toLowerCase());
	    		StringTextComponent succefull = new StringTextComponent(Messages.succefully.get().replaceAll("\\{playerName\\}", p.getName().getString()).replaceAll("\\{blockX\\}", "" + (int)p.getPositionVec().x).replaceAll("\\{blockY\\}", "" + (int)p.getPositionVec().y).replaceAll("\\{blockZ\\}", "" + (int)p.getPositionVec().z).replaceAll("&", "§"));
	    		p.sendMessage(succefull, p.getUniqueID());
	        }
	        cooldowns.put(p.getName().getString(), System.currentTimeMillis());
	    }
		return 1;
	}
	
	  private static boolean inWhitelist(String dimension) {
		  //WHITELIST
		  if(Config.useWhitelist.get()) {
			  return Config.allowedDimensions.get().contains(dimension);
		  //BLACKLIST
		  } else {
			  return !Config.allowedDimensions.get().contains(dimension); 
		  }
	  }
}
