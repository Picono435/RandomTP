package com.gmail.picono435.randomtp.api;

import com.gmail.picono435.randomtp.config.ConfigHandler;

public enum Message {
    COMMAND_FINDING("command.finding"),
    COMMAND_SUCCESSFUL("command.successfully"),
    COMMAND_COOLDOWN("command.cooldown"),
    COMMAND_DIMENSIONNOTALLOWED("command.dimension-not-allowed"),
    COMMAND_BIOMENOTALLOWED("command.biome-not-allowed"),
    COMMAND_MAXTRIES("command.max-tries");

    private String configKey;

    Message(String configKey) {
        this.configKey = configKey;
    }

    public String getValue() {
        return ConfigHandler.getMessages().getString(configKey);
    }
}
