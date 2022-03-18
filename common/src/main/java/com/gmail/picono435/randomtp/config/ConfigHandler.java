package com.gmail.picono435.randomtp.config;

import com.gmail.picono435.randomtp.RandomTP;
import com.google.common.io.Files;
import dev.architectury.injectables.annotations.ExpectPlatform;
import org.apache.commons.io.FileUtils;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;

public class ConfigHandler {

    private static ConfigurationNode config;
    private static ConfigurationNode messages;

    public static void loadConfiguration() throws IOException, URISyntaxException {
        migrateFiles();

        URL input = ConfigHandler.class.getClassLoader().getResource("config.yml");
        File to = getConfigDirectory().resolve("RandomTP").resolve("config.yml").toFile();
        if(!to.exists()){
            FileUtils.copyURLToFile(input, to);
        }
        URL input2 = ConfigHandler.class.getClassLoader().getResource("messages.yml");
        /*File to2 = getConfigDirectory().resolve("RandomTP").resolve("messages.yml").toFile();
        if(!to2.exists()) {
            FileUtils.copyURLToFile(input2, to2);
        }*/

        config = YamlConfigurationLoader.builder()
                .indent(2)
                .path(getConfigDirectory().resolve("RandomTP").resolve("config.yml"))
                .build().load();

        messages = YamlConfigurationLoader.builder()
                .indent(2)
                .url(input2)
                .path(getConfigDirectory().resolve("RandomTP").resolve("messages.yml"))
                .build().load();
    }

    public static void migrateFiles() {
        File configToml = getConfigDirectory().resolve("RandomTP").resolve("config.toml").toFile();
        if(configToml.exists()) {
            RandomTP.getLogger().error("You are using an old configuration format, because of the recent transformation of the mod this format is no longer supported. Be free to convert using an online website (https://www.convertsimple.com/convert-toml-to-yaml/). This will probably be automated in a future update.");
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
