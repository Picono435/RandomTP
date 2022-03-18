package com.gmail.picono435.randomtp.api.fabric;

import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.biome.Biome;

import java.util.HashMap;
import java.util.Map;

public class RandomTPAPIImpl {

	public static Map<String, Integer> registeredNodes = new HashMap<String, Integer>();

	public static boolean hasPermission(CommandSourceStack source, String permission) {
		return Permissions.check(source, permission, registeredNodes.get(permission));
	}

	public static boolean hasPermission(ServerPlayer player, String permission) {
		return Permissions.check(player, permission, registeredNodes.get(permission));
	}

    public static ResourceLocation getBiomeId(Biome biome) {
		return BuiltinRegistries.BIOME.getKey(biome);
    }

    public static ResourceKey<Biome> getBiomeResourceKey(Biome biome) {
		return BuiltinRegistries.BIOME.getResourceKey(biome).orElse(null);
    }

	public static Biome getBiomeFromKey(ResourceKey<Biome> biome) {
		return BuiltinRegistries.BIOME.get(biome);
	}

}
