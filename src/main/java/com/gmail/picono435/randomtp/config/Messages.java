package com.gmail.picono435.randomtp.config;

import com.gmail.picono435.randomtp.MainMod;

import net.minecraftforge.common.config.Configuration;

public class Messages {
    private static final String CATEGORY_COMMAND = "Command";

    public static String succefully = "&aSuccefully teleported you to a random location.";
    public static String cooldown = "&cWait more {secondsLeft} seconds for execute the command again.";
    public static String invalidArgs = "&cPlease use /rtpd (dimension).";
    public static String invalidDimension = "&cPlease put a valid dimension (e.g: -1).";
    public static String dimensionNotAllowed = "&cYou cannot random teleport to that dimension!";
    
    public static void readConfig() {
        Configuration cfg = MainMod.messages;
        try {
            cfg.load();
            initGeneralConfig(cfg);
        } catch (Exception e) {
            MainMod.logger.error("Problem loading config file!", e);
        } finally {
            if (cfg.hasChanged()) {
                cfg.save();
            }
        }
    }

    public static void initGeneralConfig(Configuration cfg) {
        cfg.addCustomCategoryComment(CATEGORY_COMMAND, "Command configuration messages for RandomTP!");
        succefully = cfg.getString("succefully", CATEGORY_COMMAND, succefully, "Message that you want to appier when the command is succefully made. [ placeholders: {playerName} {blockZ} {blockY}, color codes: & + letter (example: &c) ]");
        cooldown = cfg.getString("cooldown", CATEGORY_COMMAND, cooldown, "Message that you want to appier when the command is on cooldown. [ placeholders: {playerName}, color codes: & + letter (example: &c) ]");
        invalidArgs = cfg.getString("invalid-args", CATEGORY_COMMAND, invalidArgs, "Message that you want to appier when you execute /rtpd without args. [ placeholders: {playerName}, color codes: & + letter (example: &c) ]");
        invalidDimension = cfg.getString("invalid-dimension", CATEGORY_COMMAND, invalidDimension, "Message that you want to appier when you execute /rtpd with a invalid dimension [ placeholders: {playerName} {dimensionId}, color codes: & + letter (example: &c) ].");
        dimensionNotAllowed = cfg.getString("blacklist-dimension", CATEGORY_COMMAND, dimensionNotAllowed, "Message that you want to appier when you execute /rtpd with a dimension that is in the blacklist [ placeholders: {playerName} {dimensionId}, color codes: & + letter (example: &c) ].");
    }
}