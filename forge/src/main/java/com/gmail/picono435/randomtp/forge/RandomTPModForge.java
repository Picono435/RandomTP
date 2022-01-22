package com.gmail.picono435.randomtp.forge;

import com.gmail.picono435.randomtp.RandomTP;
import com.gmail.picono435.randomtp.RandomTPMod;

import com.gmail.picono435.randomtp.commands.RTPCommand;
import com.gmail.picono435.randomtp.commands.RTPDCommand;
import com.gmail.picono435.randomtp.config.Config;
import com.gmail.picono435.randomtp.config.ConfigHandler;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.events.PermissionGatherEvent;
import net.minecraftforge.server.permission.nodes.PermissionDynamicContext;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import net.minecraftforge.server.permission.nodes.PermissionTypes;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@Mod(RandomTPMod.MOD_ID)
public class RandomTPModForge {

    public static PermissionNode<Boolean> BASIC_COMMAND_PERM = new PermissionNode<>(
            RandomTPMod.MOD_ID,
            "command.basic",
            PermissionTypes.BOOLEAN,
            (arg, uUID, permissionDynamicContexts) -> true);
    public static PermissionNode<Boolean> INTERDIM_COMMAND_PERM = new PermissionNode<>(
            RandomTPMod.MOD_ID,
            "command.interdim",
            PermissionTypes.BOOLEAN,
            (arg, uUID, permissionDynamicContexts) -> true);
    public static PermissionNode<Boolean> COOLDOWN_EXEMPT_PERM = new PermissionNode<>(
            RandomTPMod.MOD_ID,
            "cooldown.exempt",
            PermissionTypes.BOOLEAN,
            (arg, uUID, permissionDynamicContexts) -> false);

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
    public void init(ServerStartingEvent event) {
        RandomTP.getLogger().info("RandomTP successfully loaded.");
    }

    @SubscribeEvent
    public void command(RegisterCommandsEvent event) {
        RTPCommand.register(event.getDispatcher());
        if(Config.useDimension()) {
            RTPDCommand.register(event.getDispatcher());
        }
    }

    @SubscribeEvent
    public void permission(PermissionGatherEvent.Nodes event) {
        RandomTP.getLogger().info("Registering permission nodes...");
        event.addNodes(BASIC_COMMAND_PERM, INTERDIM_COMMAND_PERM, COOLDOWN_EXEMPT_PERM);
    }
}