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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.server.permission.PermissionAPI;

public class RTPCommand {
	
	private static Map<String, Long> cooldowns = new HashMap<String, Long>();
	
	public static void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(Commands.literal("rtp").requires(source -> RandomTPAPI.hasPermission(source, "randomtp.command.basic"))
				.executes(context -> runCommand(context.getSource().asPlayer())
				));
		dispatcher.register(Commands.literal("randomtp").requires(source -> RandomTPAPI.hasPermission(source, "randomtp.command.basic"))
				.executes(context -> runCommand(context.getSource().asPlayer())
				));
		dispatcher.register(Commands.literal("randomteleport").requires(source -> RandomTPAPI.hasPermission(source, "randomtp.command.basic"))
				.executes(context -> runCommand(context.getSource().asPlayer())
				));
	}
	
	private static int runCommand(PlayerEntity p) {
	    if(!RandomTPAPI.checkCooldown(p, cooldowns) && !PermissionAPI.hasPermission(p, "randomtp.cooldown.exempt")) {
	    	long secondsLeft = RandomTPAPI.getCooldownLeft(p, cooldowns);
	    	StringTextComponent cooldownmes = new StringTextComponent(Messages.cooldown.get().replaceAll("\\{secondsLeft\\}", Long.toString(secondsLeft)).replaceAll("\\{playerName\\}", p.getName().getString()).replaceAll("&", "§"));
	        p.sendMessage(cooldownmes, p.getUniqueID());
	        return 1;
	    } else {
	    	cooldowns.remove(p.getName().getString());
	    	if(Config.useOriginal.get()) {
		    	if(!Config.default_world.get().equals("playerworld")) {
		    		RandomTPAPI.randomTeleport(p, RandomTPAPI.getWorld(Config.default_world.get(), p.getServer()));
		    	} else {
		    		RandomTPAPI.randomTeleport(p, (ServerWorld)p.world);
		    	}
	    		cooldowns.put(p.getName().getString(), System.currentTimeMillis());
	    		return 1;
	    	}
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
}
