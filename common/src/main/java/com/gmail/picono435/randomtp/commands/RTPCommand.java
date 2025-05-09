package com.gmail.picono435.randomtp.commands;

import com.gmail.picono435.randomtp.config.Config;
import com.gmail.picono435.randomtp.config.Messages;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static com.gmail.picono435.randomtp.api.RandomTPAPI.*;

public class RTPCommand {
	
	private static final Map<String, Long> cooldowns = new HashMap<String, Long>();
	
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("rtp").requires(source -> hasPermission(source, "randomtp.command.basic"))
				.executes(context -> runCommand(context.getSource().getPlayerOrException())
				));
		dispatcher.register(Commands.literal("randomtp").requires(source -> hasPermission(source, "randomtp.command.basic"))
				.executes(context -> runCommand(context.getSource().getPlayerOrException())
				));
	}
	
	public static int runCommand(ServerPlayer p) {
		try {
			if(!checkCooldown(p, cooldowns) && !hasPermission(p, "randomtp.cooldown.exempt")) {
				long secondsLeft = getCooldownLeft(p, cooldowns);
				Component cooldownmes = Component.literal(Messages.getCooldown().replaceAll("\\{secondsLeft\\}", Long.toString(secondsLeft)).replaceAll("\\{playerName\\}", p.getName().getString()).replaceAll("&", "§"));
				p.sendSystemMessage(cooldownmes, false);
				return 1;
			} else {
				cooldowns.remove(p.getName().getString());
				if(Config.useOriginal()) {
					Component finding = Component.literal(Messages.getFinding().replaceAll("\\{playerName\\}", p.getName().getString()).replaceAll("\\{blockX\\}", "" + (int)p.position().x).replaceAll("\\{blockY\\}", "" + (int)p.position().y).replaceAll("\\{blockZ\\}", "" + (int)p.position().z).replaceAll("&", "§"));
					p.sendSystemMessage(finding, false);
					if(!Config.getDefaultWorld().equals("playerworld")) {
						randomTeleport(p, getWorld(Config.getDefaultWorld(), p.getServer()));
					} else {
						randomTeleport(p, p.serverLevel());
					}
					cooldowns.put(p.getName().getString(), System.currentTimeMillis());
					return 1;
				}
				double cal = p.serverLevel().getWorldBorder().getSize()/2;
				BigDecimal num = new BigDecimal(cal);
				String maxDistance = num.toPlainString();
				if(Config.getMaxDistance() == 0) {
					String command = "spreadplayers " + p.serverLevel().getWorldBorder().getCenterX() + " " + p.serverLevel().getWorldBorder().getCenterZ() + " " + Config.getMinDistance() + " " + maxDistance + " false " + p.getName().getString().toLowerCase();
					p.getServer().getCommands().performCommand(p.getServer().getCommands().getDispatcher().parse(command, p.getServer().createCommandSourceStack()), command);
					Component successful = Component.literal(Messages.getSuccessful().replaceAll("\\{playerName\\}", p.getName().getString()).replaceAll("\\{blockX\\}", "" + (int)p.position().x).replaceAll("\\{blockY\\}", "" + (int)p.position().y).replaceAll("\\{blockZ\\}", "" + (int)p.position().z).replaceAll("&", "§"));
					p.sendSystemMessage(successful, false);
				} else {
					String command = "spreadplayers " + p.serverLevel().getWorldBorder().getCenterX() + " " + p.serverLevel().getWorldBorder().getCenterZ() + " " + Config.getMinDistance() + " " + Config.getMaxDistance() + " false " + p.getName().getString().toLowerCase();
					p.getServer().getCommands().performCommand(p.getServer().getCommands().getDispatcher().parse(command, p.getServer().createCommandSourceStack()), command);
					Component successful = Component.literal(Messages.getSuccessful().replaceAll("\\{playerName\\}", p.getName().getString()).replaceAll("\\{blockX\\}", "" + (int)p.position().x).replaceAll("\\{blockY\\}", "" + (int)p.position().y).replaceAll("\\{blockZ\\}", "" + (int)p.position().z).replaceAll("&", "§"));
					p.sendSystemMessage(successful, false);
				}
				cooldowns.put(p.getName().getString(), System.currentTimeMillis());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return 1;
	}
}
