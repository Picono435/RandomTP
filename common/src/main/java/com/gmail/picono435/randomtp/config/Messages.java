package com.gmail.picono435.randomtp.config;

public class Messages {

    public static String getFinding() {
        return ConfigHandler.getMessages().node("command").node("finding").getString();
    }

    public static String getSuccessful() {
        return ConfigHandler.getMessages().node("command").node("successfully").getString();
    }

    public static String getCooldown() {
        return ConfigHandler.getMessages().node("command").node("cooldown").getString();
    }

    public static String getDimensionNotAllowed() {
        return ConfigHandler.getMessages().node("command").node("dimension-not-allowed").getString();
    }

    public static String getBiomeNotAllowed() {
        return ConfigHandler.getMessages().node("command").node("biome-not-allowed").getString();
    }

    public static String getMaxTries() {
        return ConfigHandler.getMessages().node("command").node("max-tries").getString();
    }

}
