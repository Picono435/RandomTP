package com.gmail.picono435.randomtp;

import com.gmail.picono435.randomtp.api.RandomTPAPI;
import com.gmail.picono435.randomtp.config.Config;
import com.gmail.picono435.randomtp.config.ConfigHandler;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RandomTPMod {

    public static final String MOD_ID = "randomtp";

    public static void init() {
        RandomTPMod.getLogger().info("Starting Random Teleport Mod, thanks for downloading it.");
    }

    public static void spawnTeleportPlayer(ServerPlayer player) {
        String rtpCommand = Config.getAutoTeleport();
        if(rtpCommand == null) return;
        switch(rtpCommand.split(" ")[0]) {
            case "rtp":
                RandomTPAPI.randomTeleport(player, player.serverLevel());
                return;
            case "rtpd":
                String dimension = rtpCommand.split(" ")[1];
                RandomTPAPI.randomTeleport(player, RandomTPAPI.getWorld(dimension, player.getServer()));
                return;
            case "rtpb":
                String biome = rtpCommand.split(" ")[1];
                ResourceLocation biomeLocation = ResourceLocation.tryParse(biome);
                ResourceKey<Biome> biomeKey = ResourceKey.create(Registries.BIOME, biomeLocation);
                RandomTPAPI.randomTeleport(player, player.serverLevel(), biomeKey);
                return;
            case "none": {}
        }
    }

    public static Logger getLogger() {
        return LogManager.getLogger();
    }
}
