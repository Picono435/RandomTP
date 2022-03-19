package com.gmail.picono435.randomtp.commands;

import com.gmail.picono435.randomtp.api.RandomTPAPI;
import com.gmail.picono435.randomtp.config.Config;
import com.gmail.picono435.randomtp.config.Messages;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;

public class RTPBCommand {
	
	private static Map<String, Long> cooldowns = new HashMap<String, Long>();

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("rtpb").requires(source -> RandomTPAPI.hasPermission(source, "randomtp.command.interbiome"))
				.then(
						Commands.argument("biome", ResourceLocationArgument.id())
								.executes(context ->
										runCommand(context.getSource().getPlayerOrException(), context.getArgument("biome", ResourceLocation.class))
								)
				));
		dispatcher.register(Commands.literal("biomertp").requires(source -> RandomTPAPI.hasPermission(source, "randomtp.command.interbiome"))
				.then(
						Commands.argument("biome", ResourceLocationArgument.id())
								.executes(context ->
										runCommand(context.getSource().getPlayerOrException(), context.getArgument("biome", ResourceLocation.class))
								)
				));
	}
	
	private static int runCommand(ServerPlayer p, ResourceLocation biomeLocation) {
		try {
			if(!RandomTPAPI.checkCooldown(p, cooldowns) && !RandomTPAPI.hasPermission(p, "randomtp.cooldown.exempt")) {
				long secondsLeft = RandomTPAPI.getCooldownLeft(p, cooldowns);
				TextComponent cooldownmes = new TextComponent(Messages.getCooldown().replaceAll("\\{secondsLeft\\}", Long.toString(secondsLeft)).replaceAll("\\{playerName\\}", p.getName().getString()).replaceAll("&", "§"));
				p.sendMessage(cooldownmes);
				return 1;
			} else {
				cooldowns.remove(p.getName().getString());
				String biomeId = biomeLocation.getNamespace() + ":" + biomeLocation.getPath();
				if(!inWhitelist(biomeId)) {
					p.sendMessage(new TextComponent(Messages.getDimensionNotAllowed().replaceAll("\\{playerName\\}", p.getName().getString()).replaceAll("\\{biomeId\\}", biomeId.toString()).replace('&', '§')));
					return 1;
				}
				if(Config.useOriginal()) {
					TextComponent finding = new TextComponent(Messages.getFinding().replaceAll("\\{playerName\\}", p.getName().getString()).replaceAll("\\{blockX\\}", "" + (int)p.position().x).replaceAll("\\{blockY\\}", "" + (int)p.position().y).replaceAll("\\{blockZ\\}", "" + (int)p.position().z).replaceAll("&", "§"));
					p.sendMessage(finding);
					new Thread(() -> {
						RandomTPAPI.randomTeleport(p, p.getLevel(), RandomTPAPI.getBiomeFromKey(biomeLocation));
					}).start();
					cooldowns.put(p.getName().getString(), System.currentTimeMillis());
					return 1;
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
