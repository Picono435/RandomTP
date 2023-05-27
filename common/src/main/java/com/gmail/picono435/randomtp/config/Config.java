package com.gmail.picono435.randomtp.config;

import io.leangen.geantyref.TypeToken;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.ArrayList;
import java.util.List;

public class Config {

    public static String getDefaultWorld() {
        return ConfigHandler.getConfig().node("distance").node("default_world").getString();
    }

    public static int getMaxDistance() {
        return ConfigHandler.getConfig().node("distance").node("max_distance").getInt();
    }

    public static int getMinDistance() {
        return ConfigHandler.getConfig().node("distance").node("min_distance").getInt();
    }

    public static int getCooldown() {
        return ConfigHandler.getConfig().node("others").node("cooldown").getInt();
    }

    public static boolean useOriginal() {
        return ConfigHandler.getConfig().node("others").node("use-original").getBoolean();
    }

    public static String getAutoTeleport() {
        return ConfigHandler.getConfig().node("others").node("auto-teleportation").getString();
    }

    public static boolean useBiomeWhitelist() {
        return ConfigHandler.getConfig().node("others").node("use-biome-whitelist").getBoolean();
    }

    public static List<String> getAllowedBiomes() {
        try {
            return ConfigHandler.getConfig().node("others").node("biome-whitelist").getList(TypeToken.get(String.class));
        } catch (SerializationException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static int getMaxTries() {
        return ConfigHandler.getConfig().node("others").node("max-tries").getInt();
    }

    public static boolean useDimension() {
        return ConfigHandler.getConfig().node("inter-dimensions-command").node("inter-dim").getBoolean();
    }

    public static boolean useWhitelist() {
        return ConfigHandler.getConfig().node("inter-dimensions-command").node("use-whitelist").getBoolean();
    }

    public static List<String> getAllowedDimensions() {
        try {
            return ConfigHandler.getConfig().node("inter-dimensions-command").node("whitelist-dimension").getList(TypeToken.get(String.class));
        } catch (SerializationException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static boolean useBiome() {
        return ConfigHandler.getConfig().node("inter-biomes-command").node("inter-biome").getBoolean();
    }

}
