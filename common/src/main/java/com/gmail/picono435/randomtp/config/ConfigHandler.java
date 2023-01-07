package com.gmail.picono435.randomtp.config;

import com.gmail.picono435.randomtp.RandomTPMod;
import dev.architectury.injectables.annotations.ExpectPlatform;
import org.apache.commons.io.FileUtils;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;

public class ConfigHandler {

    private static ConfigurationNode config;
    private static YamlConfigurationLoader configLoader;
    private static ConfigurationNode messages;
    private static YamlConfigurationLoader messagesLoader;

    public static void loadConfiguration() throws IOException, URISyntaxException {
        URL input = ConfigHandler.class.getClassLoader().getResource("config.yml");
        File to = getConfigDirectory().resolve("RandomTP").resolve("config.yml").toFile();
        if(!to.exists()){
            FileUtils.copyURLToFile(input, to);
        }
        URL input2 = ConfigHandler.class.getClassLoader().getResource("messages.yml");
        File to2 = getConfigDirectory().resolve("RandomTP").resolve("messages.yml").toFile();
        if(!to2.exists()) {
            FileUtils.copyURLToFile(input2, to2);
        }

        configLoader = YamlConfigurationLoader.builder()
                .indent(2)
                .url(input)
                .path(getConfigDirectory().resolve("RandomTP").resolve("config.yml"))
                .build();
        config = configLoader.load();

        messagesLoader = YamlConfigurationLoader.builder()
                .indent(2)
                .url(input2)
                .path(getConfigDirectory().resolve("RandomTP").resolve("messages.yml"))
                .build();
        messages = messagesLoader.load();

        if(!messages.node("command", "dimensionNotAllowed").isNull()) {
            messages.node("command", "dimension-not-allowed").mergeFrom(messages.node("command", "dimensionNotAllowed"));
            messages.node("command", "dimensionNotAllowed").set(null);
            messagesLoader.save(messages);
            RandomTPMod.getLogger().warn("Migrated config key from dimensionNotAllowed to dimension-not-allowed");
        }
        if(!messages.node("command", "biomeNotAllowed").isNull()) {
            messages.node("command", "biome-not-allowed").mergeFrom(messages.node("command", "biomeNotAllowed"));
            messages.node("command", "biomeNotAllowed").set(null);
            messagesLoader.save(messages);
            RandomTPMod.getLogger().warn("Migrated config key from biomeNotAllowed to biome-not-allowed");
        }
    }

    public static ConfigurationNode getConfig() {
        return config;
    }

    public static ConfigurationNode getMessages() {
        return messages;
    }

    @ExpectPlatform
    public static Path getConfigDirectory() {
        throw new AssertionError();
    }
}
