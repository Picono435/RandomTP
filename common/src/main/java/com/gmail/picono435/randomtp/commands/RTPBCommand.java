package com.gmail.picono435.randomtp.commands;

import com.gmail.picono435.randomtp.api.RandomTPAPI;
import com.gmail.picono435.randomtp.config.Config;
import com.gmail.picono435.randomtp.config.Messages;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceOrTagArgument;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.biome.Biome;

import java.util.HashMap;
import java.util.Map;

public class RTPBCommand {

	private static final DynamicCommandExceptionType ERROR_BIOME_INVALID = new DynamicCommandExceptionType((object) -> {
		return Component.translatable("commands.locate.biome.invalid", new Object[]{object});
	});
	private static Map<String, Long> cooldowns = new HashMap<String, Long>();

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandBuildContext) {
		dispatcher.register(Commands.literal("rtpb").requires(source -> RandomTPAPI.hasPermission(source, "randomtp.command.interbiome"))
				.then(
						Commands.argument("biome", ResourceOrTagArgument.resourceOrTag(commandBuildContext, Registries.BIOME))
								.executes(context ->
										runCommand(context.getSource().getPlayerOrException(), ResourceOrTagArgument.getResourceOrTag(context, "biome", Registries.BIOME))
								)
				));
		dispatcher.register(Commands.literal("biomertp").requires(source -> RandomTPAPI.hasPermission(source, "randomtp.command.interbiome"))
				.then(
						Commands.argument("biome", ResourceOrTagArgument.resourceOrTag(commandBuildContext, Registries.BIOME))
								.executes(context ->
										runCommand(context.getSource().getPlayerOrException(), ResourceOrTagArgument.getResourceOrTag(context, "biome", Registries.BIOME))
								)
				));
	}
	
	private static int runCommand(ServerPlayer p, ResourceOrTagArgument.Result<Biome> biome) {
		try {
			if(!RandomTPAPI.checkCooldown(p, cooldowns) && !RandomTPAPI.hasPermission(p, "randomtp.cooldown.exempt")) {
				long secondsLeft = RandomTPAPI.getCooldownLeft(p, cooldowns);
				Component cooldownmes = Component.literal(Messages.getCooldown().replaceAll("\\{secondsLeft\\}", Long.toString(secondsLeft)).replaceAll("\\{playerName\\}", p.getName().getString()).replaceAll("&", "§"));
				p.sendSystemMessage(cooldownmes, false);
				return 1;
			} else {
				cooldowns.remove(p.getName().getString());
				ResourceKey<Biome> biomeKey = biome.unwrap().left().get().key();
				ResourceLocation biomeLocation = biomeKey.location();
				if(!inWhitelist(biomeLocation.toString())) {
					p.sendSystemMessage(Component.literal(Messages.getBiomeNotAllowed().replaceAll("\\{playerName\\}", p.getName().getString()).replaceAll("\\{biomeId\\}", biomeLocation.toString()).replace('&', '§')), false);
					return 1;
				}
				if(Config.useOriginal()) {
					Component finding = Component.literal(Messages.getFinding().replaceAll("\\{playerName\\}", p.getName().getString()).replaceAll("\\{blockX\\}", "" + (int)p.position().x).replaceAll("\\{blockY\\}", "" + (int)p.position().y).replaceAll("\\{blockZ\\}", "" + (int)p.position().z).replaceAll("&", "§"));
					p.sendSystemMessage(finding, false);
					RandomTPAPI.randomTeleport(p, p.serverLevel(), biomeKey);
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
