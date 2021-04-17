package com.gmail.picono435.randomtp.config;

import com.gmail.picono435.randomtp.MainMod;

import net.minecraftforge.common.config.Configuration;

public class Config {
    private static final String CATEGORY_DISTANCE = "Distance";
    private static final String CATEGORY_INTERDIM = "Inter-Dimensions-Command";
    private static final String CATEGORY_OTHERS = "Others";
    
    public static String default_world = "playerworld";
    public static int max_distance = 10000;
    public static int min_distance = 1;
    
    public static int cooldown = 0;
    public static boolean useOriginal = true;
    public static int maxTries = -1;
    
    public static boolean dim = true;
    public static boolean useWhitelist = true;
    public static String[] allowedDimensions = {"1", "-1"};

    public static void readConfig() {
        Configuration cfg = MainMod.config;
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
        cfg.addCustomCategoryComment(CATEGORY_DISTANCE, "Distance configuration settings for RandomTP!");
        default_world = cfg.getString("default_world", CATEGORY_DISTANCE, default_world, "The default world that you are teleported when doing /rtp (Please remember that in 1.12.2 you need to put the dimension ID not the name) (Only supported when use-original is set to true) [playerworld = the world on where the player is]");
        max_distance = cfg.getInt("max_distance", CATEGORY_DISTANCE, max_distance, 0, Integer.MAX_VALUE, "Max distance that you want to a person be teleported. (auto = world border size / 2)");
        min_distance = cfg.getInt("min_distance", CATEGORY_DISTANCE, min_distance, 1, Integer.MAX_VALUE, "Minimum distance that you want to a person be teleported.");
        
        cfg.addCustomCategoryComment(CATEGORY_INTERDIM, "Configuration settings for the inter dimensions command for RandomTP");
        dim = cfg.getBoolean("inter-dim", CATEGORY_INTERDIM, dim, "Do you want to the command /rtpd be allowed? (This commands adds a inter-dimension RTP)");
        useWhitelist = cfg.getBoolean("use-whitelist", CATEGORY_INTERDIM, useWhitelist, "Do you want to use the whitelist or blacklist dimension? ");
        allowedDimensions = cfg.getStringList("whitelist-dimensions", CATEGORY_INTERDIM, allowedDimensions, "The dimensions whitelist (Works with IDs only, use-whitelist:true=whitelist use-whitelist:true=blacklist)");
        
        cfg.addCustomCategoryComment(CATEGORY_OTHERS, "Others configuration settings for RandomTP!");
        cooldown  = cfg.getInt("cooldown", CATEGORY_OTHERS, cooldown, 0, Integer.MAX_VALUE, "How much cooldown do you want for the command (put 0 for none)");
        useOriginal  = cfg.getBoolean("use-original", CATEGORY_OTHERS, useOriginal, "If you want to use the original RTP system or the /spreadplayers system.");
        maxTries  = cfg.getInt("max-tries", CATEGORY_OTHERS, maxTries, -1, Integer.MAX_VALUE, "The amount of tries to find a safe location (original system) [-1 = infinite]");
    }
}