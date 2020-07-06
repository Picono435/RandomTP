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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import net.minecraftforge.server.permission.PermissionAPI;

public class RTPCommand {
	
	private static HashMap<String, Long> cooldowns = new HashMap<String, Long>();
	
	public static void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(Commands.literal("rtp").requires(source -> hasPermission(source))
				.executes(context -> randomTeleport(context.getSource().asPlayer())
				));
		dispatcher.register(Commands.literal("randomtp").requires(source -> hasPermission(source))
				.executes(context -> randomTeleport(context.getSource().asPlayer())
				));
		dispatcher.register(Commands.literal("randomteleport").requires(source -> hasPermission(source))
				.executes(context -> randomTeleport(context.getSource().asPlayer())
				));
	}
	
	private static int randomTeleport(PlayerEntity p) {
		World world = p.getEntityWorld();
	    WorldBorder border = world.getWorldBorder();
	    MinecraftServer server = MainMod.server;
	    StringTextComponent succefull = new StringTextComponent(Messages.succefully.get().replaceAll("\\{playerName\\}", p.getName().getString()).replaceAll("\\{blockZ\\}", "" + p.getPositionVec().z).replaceAll("\\{blockX\\}", "" + p.getPositionVec().x).replaceAll("&", "§"));
	    if(!checkCooldown(p)) {
	    	long secondsLeft = getCooldownLeft(p);
	    	StringTextComponent cooldownmes = new StringTextComponent(Messages.cooldown.get().replaceAll("\\{secondsLeft\\}", Long.toString(secondsLeft)).replaceAll("\\{playerName\\}", p.getName().getString()).replaceAll("&", "§"));
	        p.sendMessage(cooldownmes, p.getUniqueID());
	        return 1;
	    } else {
	    	double cal = border.getDiameter()/2;
	    	BigDecimal num = new BigDecimal(cal);
	    	String maxDistance = num.toPlainString();
	        if(Config.max_distance.get() == 0) {
	        	server.getCommandManager().handleCommand(server.getCommandSource(), "spreadplayers " + border.func_230316_a_() + " " + border.func_230317_b_() + " " + Config.min_distance.get() + " " + maxDistance + " false " + p.getName().getString().toLowerCase());
	        	p.sendMessage(succefull, p.getUniqueID());
	        } else {
	    		server.getCommandManager().handleCommand(server.getCommandSource(), "spreadplayers " + border.func_230316_a_() + " " + border.func_230317_b_() + " " + Config.min_distance.get() + " " + Config.max_distance.get() + " false " + p.getName().getString().toLowerCase());
	    		p.sendMessage(succefull, p.getUniqueID());
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
	
	private static boolean hasPermission(CommandSource source) {
		try {
			return PermissionAPI.hasPermission(source.asPlayer(), "randomtp.command.basic");
		} catch (CommandSyntaxException e) {
			return false;
		}
	}
}
