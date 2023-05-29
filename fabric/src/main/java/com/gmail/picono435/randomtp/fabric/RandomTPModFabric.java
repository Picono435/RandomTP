package com.gmail.picono435.randomtp.fabric;

import com.gmail.picono435.randomtp.RandomTPMod;
import com.gmail.picono435.randomtp.api.fabric.RandomTPAPIImpl;
import com.gmail.picono435.randomtp.commands.RTPBCommand;
import com.gmail.picono435.randomtp.commands.RTPCommand;
import com.gmail.picono435.randomtp.commands.RTPDCommand;
import com.gmail.picono435.randomtp.config.Config;
import com.gmail.picono435.randomtp.config.ConfigHandler;
import com.gmail.picono435.randomtp.data.PlayerState;
import com.gmail.picono435.randomtp.data.ServerState;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;

public class RandomTPModFabric implements ModInitializer {

    public static MinecraftServer minecraftServer;

    @Override
    public void onInitialize() {
        RandomTPMod.init();

        RandomTPMod.getLogger().info("Loading config files...");

        try {
            ConfigHandler.loadConfiguration();
        } catch (Exception e) {
            e.printStackTrace();
            RandomTPMod.getLogger().info("An error occuried while loading configuration.");
        }

        RandomTPMod.getLogger().info("Config files loaded.");

        ServerLifecycleEvents.SERVER_STARTING.register((server) -> {
            RandomTPMod.getLogger().info("Registering permission nodes...");

            RandomTPAPIImpl.registeredNodes.put("randomtp.command.basic", 0);
            RandomTPAPIImpl.registeredNodes.put("randomtp.command.interdim", 0);
            RandomTPAPIImpl.registeredNodes.put("randomtp.command.interbiome", 0);
            RandomTPAPIImpl.registeredNodes.put("randomtp.cooldown.exempt", 1);

            minecraftServer = server;

            RandomTPMod.getLogger().info("RandomTP successfully loaded.");
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            PlayerState playerState = ServerState.getPlayerState(handler.player);
            if(!playerState.hasJoined) {
                RandomTPMod.spawnTeleportPlayer(handler.getPlayer());
                playerState.hasJoined = true;
            }
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            RTPCommand.register(dispatcher);
            if(Config.useDimension()) {
                RTPDCommand.register(dispatcher);
            }
            if(Config.useBiome()) {
                RTPBCommand.register(dispatcher, registryAccess);
            }
        });
    }

}
