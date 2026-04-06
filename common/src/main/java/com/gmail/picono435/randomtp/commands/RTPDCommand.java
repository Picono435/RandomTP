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
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.portal.TeleportTransition;

public class RTPDCommand {
	
	private static Map<String, Long> cooldowns = new HashMap<String, Long>();

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("rtpd").requires(source -> RandomTPAPI.hasPermission(source, "randomtp.command.interdim"))
				.then(
						Commands.argument("dimension", DimensionArgument.dimension())
						.executes(context -> 
							runCommand(context.getSource().getPlayerOrException(), DimensionArgument.getDimension(context, "dimension"))
						)
				));
		dispatcher.register(Commands.literal("dimensionrtp").requires(source -> RandomTPAPI.hasPermission(source, "randomtp.command.interdim"))
				.then(
						Commands.argument("dimension", DimensionArgument.dimension())
								.executes(context ->
										runCommand(context.getSource().getPlayerOrException(), DimensionArgument.getDimension(context, "dimension"))
								)
				));
	}
	
	private static int runCommand(ServerPlayer p, ServerLevel dim) {
		try {
			if(!RandomTPAPI.checkCooldown(p, cooldowns) && !RandomTPAPI.hasPermission(p, "randomtp.cooldown.exempt")) {
				long secondsLeft = RandomTPAPI.getCooldownLeft(p, cooldowns);
				Component cooldownmes = Component.literal(Messages.getCooldown().replaceAll("\\{secondsLeft\\}", Long.toString(secondsLeft)).replaceAll("\\{playerName\\}", p.getName().getString()).replaceAll("&", "§"));
				p.sendSystemMessage(cooldownmes, false);
				return 1;
			} else {
				cooldowns.remove(p.getName().getString());
				String dimensionId = dim.dimension().identifier().getNamespace() + ":" + dim.dimension().identifier().getPath();
				if(!inWhitelist(dimensionId)) {
					p.sendSystemMessage(Component.literal(Messages.getDimensionNotAllowed().replaceAll("\\{playerName\\}", p.getName().getString()).replaceAll("\\{dimensionId\\}", dimensionId.toString()).replace('&', '§')), false);
					return 1;
				}
				if(Config.useOriginal()) {
					Component finding = Component.literal(Messages.getFinding().replaceAll("\\{playerName\\}", p.getName().getString()).replaceAll("\\{blockX\\}", "" + (int)p.position().x).replaceAll("\\{blockY\\}", "" + (int)p.position().y).replaceAll("\\{blockZ\\}", "" + (int)p.position().z).replaceAll("&", "§"));
					p.sendSystemMessage(finding, false);
					RandomTPAPI.randomTeleport(p, dim);
					cooldowns.put(p.getName().getString(), System.currentTimeMillis());
					return 1;
				}
				TeleportTransition teleportTransition = new TeleportTransition(dim, p.position(), p.getDeltaMovement(), 0, 0, TeleportTransition.DO_NOTHING);
				p.teleport(teleportTransition);
				double cal = p.level().getWorldBorder().getSize()/2;
				BigDecimal num = new BigDecimal(cal);
				String maxDistance = num.toPlainString();
				if(Config.getMaxDistance() == 0) {
					String command = "spreadplayers " + p.level().getWorldBorder().getCenterX() + " " + p.level().getWorldBorder().getCenterZ() + " " + Config.getMinDistance() + " " + maxDistance + " false " + p.getName().getString().toLowerCase();
					p.level().getServer().getCommands().performCommand(p.level().getServer().getCommands().getDispatcher().parse(command, p.level().getServer().createCommandSourceStack()), command);
					Component successful = Component.literal(Messages.getSuccessful().replaceAll("\\{playerName\\}", p.getName().getString()).replaceAll("\\{blockX\\}", "" + (int)p.position().x).replaceAll("\\{blockY\\}", "" + (int)p.position().y).replaceAll("\\{blockZ\\}", "" + (int)p.position().z).replaceAll("&", "§"));
					p.sendSystemMessage(successful, false);
				} else {
					String command = "spreadplayers " + p.level().getWorldBorder().getCenterX() + " " + p.level().getWorldBorder().getCenterZ() + " " + Config.getMinDistance() + " " + Config.getMaxDistance() + " false " + p.getName().getString().toLowerCase();
					p.level().getServer().getCommands().performCommand(p.level().getServer().getCommands().getDispatcher().parse(command, p.level().getServer().createCommandSourceStack()), command);
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
	
	  private static boolean inWhitelist(String dimension) {
		  //WHITELIST
		  if(Config.useWhitelist()) {
			  return Config.getAllowedDimensions().contains(dimension);
		  //BLACKLIST
		  } else {
			  return !Config.getAllowedDimensions().contains(dimension);
		  }
	  }
}
