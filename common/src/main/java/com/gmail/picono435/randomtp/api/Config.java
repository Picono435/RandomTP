package com.gmail.picono435.randomtp.api;

import com.gmail.picono435.randomtp.config.ConfigHandler;

public enum Config {
    DISTANCE_DEFAULTWORLD("distance.default_world"),
    DISTANCE_MAXDISTANCE("distance.max_distance"),
    DISTANCE_MINDISTANCE("distance.min_distance"),
    OTHERS_COOLDOWN("others.cooldown"),
    OTHERS_USEORIGINAL("others.use-original"),
    OTHERS_USEBIOMEWHITELIST("others.use-biome-whitelist"),
    OTHERS_BIOMEWHITELIST("others.biome-whitelist"),
    OTHERS_MAXTRIES("others.max-tries"),
    INTERDIM_ENABLE("inter-dimensions-command.inter-dim"),
    INTERDIM_USEWHITELIST("inter-dimensions-command.use-whitelist"),
    INTERDIM_DIMENSIONWHITELIST("inter-dimensions-command.whitelist-dimension"),
    INTERBIOME_ENABLE("inter-biomes-command.enable");

    private String configKey;

    Config(String configKey) {
        this.configKey = configKey;
    }

    public String getValue() {
        return ConfigHandler.getConfig().getString(configKey);
    }
}
