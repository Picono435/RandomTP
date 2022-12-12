package com.gmail.picono435.randomtp.commands;

import com.gmail.picono435.randomtp.api.RandomTPAPI;
import com.gmail.picono435.randomtp.config.Config;
import com.gmail.picono435.randomtp.config.Messages;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.ResourceOrTagLocationArgument;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.ChatType;
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

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("rtpb").requires(source -> RandomTPAPI.hasPermission(source, "randomtp.command.interbiome"))
				.then(
						Commands.argument("biome", ResourceOrTagLocationArgument.resourceOrTag(Registry.BIOME_REGISTRY))
								.executes(context ->
										runCommand(context.getSource().getPlayerOrException(), ResourceOrTagLocationArgument.getRegistryType(context, "biome", Registry.BIOME_REGISTRY, ERROR_BIOME_INVALID))
								)
				));
		dispatcher.register(Commands.literal("biomertp").requires(source -> RandomTPAPI.hasPermission(source, "randomtp.command.interbiome"))
				.then(
						Commands.argument("biome", ResourceOrTagLocationArgument.resourceOrTag(Registry.BIOME_REGISTRY))
								.executes(context ->
										runCommand(context.getSource().getPlayerOrException(), ResourceOrTagLocationArgument.getRegistryType(context, "biome", Registry.BIOME_REGISTRY, ERROR_BIOME_INVALID))
								)
				));
	}
	
	private static int runCommand(ServerPlayer p, ResourceOrTagLocationArgument.Result<Biome> biome) {
		try {
			if(!RandomTPAPI.checkCooldown(p, cooldowns) && !RandomTPAPI.hasPermission(p, "randomtp.cooldown.exempt")) {
				long secondsLeft = RandomTPAPI.getCooldownLeft(p, cooldowns);
				Component cooldownmes = Component.literal(Messages.getCooldown().replaceAll("\\{secondsLeft\\}", Long.toString(secondsLeft)).replaceAll("\\{playerName\\}", p.getName().getString()).replaceAll("&", "§"));
				p.sendSystemMessage(cooldownmes, false);
				return 1;
			} else {
				cooldowns.remove(p.getName().getString());
				ResourceKey<Biome> biomeKey = biome.unwrap().left().get();
				ResourceLocation biomeLocation = biomeKey.location();
				String biomeId = biomeLocation.getNamespace() + ":" + biomeLocation.getPath();
				if(!inWhitelist(biomeId)) {
					p.sendSystemMessage(Component.literal(Messages.getBiomeNotAllowed().replaceAll("\\{playerName\\}", p.getName().getString()).replaceAll("\\{biomeId\\}", biomeId.toString()).replace('&', '§')), true);
					return 1;
				}
				if(Config.useOriginal()) {
					Component finding = Component.literal(Messages.getFinding().replaceAll("\\{playerName\\}", p.getName().getString()).replaceAll("\\{blockX\\}", "" + (int)p.position().x).replaceAll("\\{blockY\\}", "" + (int)p.position().y).replaceAll("\\{blockZ\\}", "" + (int)p.position().z).replaceAll("&", "§"));
					p.sendSystemMessage(finding, false);
					new Thread(() -> {
						RandomTPAPI.randomTeleport(p, p.getLevel(), RandomTPAPI.getBiomeFromKey(biomeKey));
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
	
	  private static boolean inWhitelist(String biome) {
		  //WHITELIST
		  if(Config.useBiomeWhitelist()) {
			  return Config.getAllowedBiomes().contains(biome);
		  //BLACKLIST
		  } else {
			  return !Config.getAllowedBiomes().contains(biome);
		  }
	  }
}
