package com.gmail.picono435.randomtp.commands;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.gmail.picono435.randomtp.api.RandomTPAPI;
import com.gmail.picono435.randomtp.config.Config;
import com.gmail.picono435.randomtp.config.Messages;
import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;

public class RTPCommand {
	
	private static Map<String, Long> cooldowns = new HashMap<String, Long>();
	
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("rtp").requires(source -> RandomTPAPI.hasPermission(source, "randomtp.command.basic"))
				.executes(context -> runCommand(context.getSource().getPlayerOrException())
				));
		dispatcher.register(Commands.literal("randomtp").requires(source -> RandomTPAPI.hasPermission(source, "randomtp.command.basic"))
				.executes(context -> runCommand(context.getSource().getPlayerOrException())
				));
	}
	
	private static int runCommand(ServerPlayer p) {
		try {
			if(!RandomTPAPI.checkCooldown(p, cooldowns) && !RandomTPAPI.hasPermission(p, "randomtp.cooldown.exempt")) {
				long secondsLeft = RandomTPAPI.getCooldownLeft(p, cooldowns);
				TextComponent cooldownmes = new TextComponent(Messages.getCooldown().replaceAll("\\{secondsLeft\\}", Long.toString(secondsLeft)).replaceAll("\\{playerName\\}", p.getName().getString()).replaceAll("&", "§"));
				p.sendMessage(cooldownmes, p.getUUID());
				return 1;
			} else {
				cooldowns.remove(p.getName().getString());
				if(Config.useOriginal()) {
					TextComponent finding = new TextComponent(Messages.getFinding().replaceAll("\\{playerName\\}", p.getName().getString()).replaceAll("\\{blockX\\}", "" + (int)p.position().x).replaceAll("\\{blockY\\}", "" + (int)p.position().y).replaceAll("\\{blockZ\\}", "" + (int)p.position().z).replaceAll("&", "§"));
					p.sendMessage(finding, p.getUUID());
					new Thread(() -> {
						if(!Config.getDefaultWorld().equals("playerworld")) {
							RandomTPAPI.randomTeleport(p, RandomTPAPI.getWorld(Config.getDefaultWorld(), p.getServer()));
						} else {
							RandomTPAPI.randomTeleport(p, p.getLevel());
						}
					}).start();
					cooldowns.put(p.getName().getString(), System.currentTimeMillis());
					return 1;
				}
				double cal = p.getLevel().getWorldBorder().getSize()/2;
				BigDecimal num = new BigDecimal(cal);
				String maxDistance = num.toPlainString();
				if(Config.getMaxDistance() == 0) {
					p.getServer().getCommands().performCommand(p.getServer().createCommandSourceStack(), "spreadplayers " + p.getLevel().getWorldBorder().getCenterX() + " " + p.getLevel().getWorldBorder().getCenterZ() + " " + Config.getMinDistance() + " " + maxDistance + " false " + p.getName().getString().toLowerCase());
					TextComponent successful = new TextComponent(Messages.getSuccessful().replaceAll("\\{playerName\\}", p.getName().getString()).replaceAll("\\{blockX\\}", "" + (int)p.position().x).replaceAll("\\{blockY\\}", "" + (int)p.position().y).replaceAll("\\{blockZ\\}", "" + (int)p.position().z).replaceAll("&", "§"));
					p.sendMessage(successful, p.getUUID());
				} else {
					p.getServer().getCommands().performCommand(p.getServer().createCommandSourceStack(), "spreadplayers " + p.getLevel().getWorldBorder().getCenterX() + " " + p.getLevel().getWorldBorder().getCenterZ() + " " + Config.getMinDistance() + " " + Config.getMaxDistance() + " false " + p.getName().getString().toLowerCase());
					TextComponent successful = new TextComponent(Messages.getSuccessful().replaceAll("\\{playerName\\}", p.getName().getString()).replaceAll("\\{blockX\\}", "" + (int)p.position().x).replaceAll("\\{blockY\\}", "" + (int)p.position().y).replaceAll("\\{blockZ\\}", "" + (int)p.position().z).replaceAll("&", "§"));
					p.sendMessage(successful, p.getUUID());
				}
				cooldowns.put(p.getName().getString(), System.currentTimeMillis());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return 1;
	}
}
