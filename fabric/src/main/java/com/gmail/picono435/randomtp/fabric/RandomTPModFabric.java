package com.gmail.picono435.randomtp.fabric;

import com.gmail.picono435.randomtp.RandomTP;
import com.gmail.picono435.randomtp.RandomTPMod;
import com.gmail.picono435.randomtp.api.fabric.RandomTPAPIImpl;
import com.gmail.picono435.randomtp.commands.RTPCommand;
import com.gmail.picono435.randomtp.commands.RTPDCommand;
import com.gmail.picono435.randomtp.config.Config;
import com.gmail.picono435.randomtp.config.ConfigHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class RandomTPModFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        RandomTPMod.init();

        RandomTP.getLogger().info("Loading config files...");

        try {
            ConfigHandler.loadConfiguration();
        } catch (Exception e) {
            e.printStackTrace();
            RandomTP.getLogger().info("An error occuried while loading configuration.");
        }

        RandomTP.getLogger().info("Config files loaded.");

        ServerLifecycleEvents.SERVER_STARTING.register((server) -> {
            RandomTP.getLogger().info("Registering permission nodes...");

            RandomTPAPIImpl.registeredNodes.put("randomtp.command.basic", 0);
            RandomTPAPIImpl.registeredNodes.put("randomtp.command.interdim", 0);
            RandomTPAPIImpl.registeredNodes.put("randomtp.cooldown.exempt", 1);

            RandomTP.getLogger().info("RandomTP successfully loaded.");
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            RTPCommand.register(dispatcher);
            if(Config.useDimension()) {
                RTPDCommand.register(dispatcher);
            }
        });
    }

}
