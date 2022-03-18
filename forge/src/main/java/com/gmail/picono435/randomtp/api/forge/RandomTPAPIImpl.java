package com.gmail.picono435.randomtp.api.forge;

import com.gmail.picono435.randomtp.forge.RandomTPModForge;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.server.permission.PermissionAPI;

public class RandomTPAPIImpl {
	
	public static boolean hasPermission(CommandSourceStack source, String permission) {
		try {
			if(permission.equalsIgnoreCase("randomtp.command.basic")) {
				return PermissionAPI.getPermission(source.getPlayerOrException(), RandomTPModForge.BASIC_COMMAND_PERM);
			} else if(permission.equalsIgnoreCase("randomtp.command.interdim")) {
				return PermissionAPI.getPermission(source.getPlayerOrException(), RandomTPModForge.INTERDIM_COMMAND_PERM);
			} else if(permission.equalsIgnoreCase("randomtp.cooldown.exempt")) {
				return PermissionAPI.getPermission(source.getPlayerOrException(), RandomTPModForge.COOLDOWN_EXEMPT_PERM);
			} else {
				return true;
			}
		} catch (CommandSyntaxException e) {
			return false;
		}
	}

	public static boolean hasPermission(ServerPlayer player, String permission) {
		if(permission.equalsIgnoreCase("randomtp.command.basic")) {
			return PermissionAPI.getPermission(player, RandomTPModForge.BASIC_COMMAND_PERM);
		} else if(permission.equalsIgnoreCase("randomtp.command.interdim")) {
			return PermissionAPI.getPermission(player, RandomTPModForge.INTERDIM_COMMAND_PERM);
		} else if(permission.equalsIgnoreCase("randomtp.cooldown.exempt")) {
			return PermissionAPI.getPermission(player, RandomTPModForge.COOLDOWN_EXEMPT_PERM);
		} else {
			return true;
		}
	}

    public static ResourceLocation getBiomeId(Biome biome) {
		return ForgeRegistries.BIOMES.getKey(biome);
    }

	public static ResourceKey<Biome> getBiomeResourceKey(Biome biome) {
		return ForgeRegistries.BIOMES.getResourceKey(biome).orElse(null);
	}

	public static Biome getBiomeFromKey(ResourceKey<Biome> biome) {
		return ForgeRegistries.BIOMES.getHolder(biome).get().value();
	}


}
