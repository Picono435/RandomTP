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
import net.minecraft.commands.arguments.DimensionTypeArgument;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.dimension.DimensionType;

public class RTPDCommand {
	
	private static Map<String, Long> cooldowns = new HashMap<String, Long>();

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("rtpd").requires(source -> RandomTPAPI.hasPermission(source, "randomtp.command.interdim"))
				.then(
						Commands.argument("dimension", DimensionTypeArgument.dimension())
						.executes(context -> 
							runCommand(context.getSource().getPlayerOrException(), DimensionTypeArgument.getDimension(context, "dimension"))
						)
				));
		dispatcher.register(Commands.literal("randomteleportdimension").requires(source -> RandomTPAPI.hasPermission(source, "randomtp.command.interdim"))
				.then(
						Commands.argument("dimension", DimensionTypeArgument.dimension())
						.executes(context ->
								runCommand(context.getSource().getPlayerOrException(), DimensionTypeArgument.getDimension(context, "dimension"))
						)
				));
		dispatcher.register(Commands.literal("randomtpd").requires(source -> RandomTPAPI.hasPermission(source, "randomtp.command.interdim"))
				.then(
						Commands.argument("dimension", DimensionTypeArgument.dimension())
						.executes(context ->
								runCommand(context.getSource().getPlayerOrException(), DimensionTypeArgument.getDimension(context, "dimension"))
						)
				));
	}

	private static int runCommand(ServerPlayer p, DimensionType dim) {
		try {
			if(!RandomTPAPI.checkCooldown(p, cooldowns) && !RandomTPAPI.hasPermission(p, "randomtp.cooldown.exempt")) {
				long secondsLeft = RandomTPAPI.getCooldownLeft(p, cooldowns);
				TextComponent cooldownmes = new TextComponent(Messages.getCooldown().replaceAll("\\{secondsLeft\\}", Long.toString(secondsLeft)).replaceAll("\\{playerName\\}", p.getName().getString()).replaceAll("&", "§"));
				p.sendMessage(cooldownmes);
				return 1;
			} else {
				cooldowns.remove(p.getName().getString());
				String dimensionId = Registry.DIMENSION_TYPE.getKey(dim).getNamespace() + ":" + Registry.DIMENSION_TYPE.getKey(dim).getPath();
				if(!inWhitelist(dimensionId)) {
					p.sendMessage(new TextComponent(Messages.getDimensionNotAllowed().replaceAll("\\{playerName\\}", p.getName().getString()).replaceAll("\\{dimensionId\\}", dimensionId + "").replace('&', '§')));
					return 1;
				}
				if(Config.useOriginal()) {
					RandomTPAPI.randomTeleport(p, p.getServer().getLevel(dim));
					cooldowns.put(p.getName().getString(), System.currentTimeMillis());
					return 1;
				}
				p.handleInsidePortal(p.getCommandSenderBlockPosition());
				p.changeDimension(dim);
				double cal = p.getLevel().getWorldBorder().getSize()/2;
				BigDecimal num = new BigDecimal(cal);
				String maxDistance = num.toPlainString();
				if(Config.getMaxDistance() == 0) {
					p.getServer().getCommands().performCommand(p.getServer().createCommandSourceStack(), "spreadplayers " + p.getLevel().getWorldBorder().getCenterX() + " " + p.getLevel().getWorldBorder().getCenterZ() + " " + Config.getMinDistance() + " " + maxDistance + " false " + p.getName().getString().toLowerCase());
					TextComponent successful = new TextComponent(Messages.getSuccessful().replaceAll("\\{playerName\\}", p.getName().getString()).replaceAll("\\{blockX\\}", "" + (int)p.position().x).replaceAll("\\{blockY\\}", "" + (int)p.position().y).replaceAll("\\{blockZ\\}", "" + (int)p.position().z).replaceAll("&", "§"));
					p.sendMessage(successful);
				} else {
					p.getServer().getCommands().performCommand(p.getServer().createCommandSourceStack(), "spreadplayers " + p.getLevel().getWorldBorder().getCenterX() + " " + p.getLevel().getWorldBorder().getCenterZ() + " " + Config.getMinDistance() + " " + Config.getMaxDistance() + " false " + p.getName().getString().toLowerCase());
					TextComponent successful = new TextComponent(Messages.getSuccessful().replaceAll("\\{playerName\\}", p.getName().getString()).replaceAll("\\{blockX\\}", "" + (int)p.position().x).replaceAll("\\{blockY\\}", "" + (int)p.position().y).replaceAll("\\{blockZ\\}", "" + (int)p.position().z).replaceAll("&", "§"));
					p.sendMessage(successful);
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
