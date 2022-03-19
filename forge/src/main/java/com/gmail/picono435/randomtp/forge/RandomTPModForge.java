package com.gmail.picono435.randomtp.forge;

import com.gmail.picono435.randomtp.RandomTP;
import com.gmail.picono435.randomtp.RandomTPMod;

import com.gmail.picono435.randomtp.commands.RTPBCommand;
import com.gmail.picono435.randomtp.commands.RTPCommand;
import com.gmail.picono435.randomtp.commands.RTPDCommand;
import com.gmail.picono435.randomtp.config.Config;
import com.gmail.picono435.randomtp.config.ConfigHandler;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fmlserverevents.FMLServerStartingEvent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

@Mod(RandomTPMod.MOD_ID)
public class RandomTPModForge {

    public RandomTPModForge() {
        RandomTPMod.init();

        RandomTP.getLogger().info("Loading config files...");

        //ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigHandlerImpl.config, "RandomTP" + File.separatorChar + "config.toml");
        //ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigHandlerImpl.messages, "RandomTP" + File.separatorChar + "messages.toml");

        //ConfigHandlerImpl.loadConfig(ConfigHandlerImpl.config, FMLPaths.CONFIGDIR.get().resolve("RandomTP" + File.separatorChar + "config.toml").toString());
        //ConfigHandlerImpl.loadConfig(ConfigHandlerImpl.messages, FMLPaths.CONFIGDIR.get().resolve("RandomTP" + File.separatorChar + "messages.toml").toString());

        try {
            ConfigHandler.loadConfiguration();
        } catch (Exception e) {
            e.printStackTrace();
            RandomTP.getLogger().info("An error occuried while loading configuration.");
        }

        RandomTP.getLogger().info("Config files loaded.");

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void init(FMLServerStartingEvent event) {
        RandomTP.getLogger().info("Registering permission nodes...");

        PermissionAPI.registerNode("randomtp.command.basic", DefaultPermissionLevel.ALL, "The permission to execute the command /rtp");
        PermissionAPI.registerNode("randomtp.command.interdim", DefaultPermissionLevel.ALL, "The permission to execute the command /rtpd");
        PermissionAPI.registerNode("randomtp.command.interbiome", DefaultPermissionLevel.ALL, "The permission to execute the command /rtpb");
        PermissionAPI.registerNode("randomtp.cooldown.exempt", DefaultPermissionLevel.OP, "The permission used to be exempt from the cooldown");

        RandomTP.getLogger().info("RandomTP successfully loaded.");
    }

    @SubscribeEvent
    public void command(RegisterCommandsEvent event) {
        RTPCommand.register(event.getDispatcher());
        if(Config.useDimension()) {
            RTPDCommand.register(event.getDispatcher());
        }
        if(Config.useBiome()) {
            RTPBCommand.register(event.getDispatcher());
        }
    }
}