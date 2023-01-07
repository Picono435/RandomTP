package com.gmail.picono435.randomtp.forge;

import com.gmail.picono435.randomtp.RandomTPMod;

import com.gmail.picono435.randomtp.commands.RTPBCommand;
import com.gmail.picono435.randomtp.commands.RTPCommand;
import com.gmail.picono435.randomtp.commands.RTPDCommand;
import com.gmail.picono435.randomtp.config.Config;
import com.gmail.picono435.randomtp.config.ConfigHandler;

import com.gmail.picono435.randomtp.data.PlayerState;
import com.gmail.picono435.randomtp.data.ServerState;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.permission.events.PermissionGatherEvent;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import net.minecraftforge.server.permission.nodes.PermissionTypes;

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
    public static PermissionNode<Boolean> INTERBIOME_COMMAND_PERM = new PermissionNode<>(
            RandomTPMod.MOD_ID,
            "command.interbiome",
            PermissionTypes.BOOLEAN,
            (arg, uUID, permissionDynamicContexts) -> true);
    public static PermissionNode<Boolean> COOLDOWN_EXEMPT_PERM = new PermissionNode<>(
            RandomTPMod.MOD_ID,
            "cooldown.exempt",
            PermissionTypes.BOOLEAN,
            (arg, uUID, permissionDynamicContexts) -> false);

    public RandomTPModForge() {
        RandomTPMod.init();

        RandomTPMod.getLogger().info("Loading config files...");

        try {
            ConfigHandler.loadConfiguration();
        } catch (Exception e) {
            e.printStackTrace();
            RandomTPMod.getLogger().info("An error occuried while loading configuration.");
        }

        RandomTPMod.getLogger().info("Config files loaded.");

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void init(ServerStartingEvent event) {
        RandomTPMod.getLogger().info("RandomTP successfully loaded.");
    }

    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if(event.getEntity().level.isClientSide) return;
        PlayerState playerState = ServerState.getPlayerState(event.getEntity());
        if(!playerState.hasJoined) {
            RandomTPMod.spawnTeleportPlayer((ServerPlayer) event.getEntity());
            playerState.hasJoined = true;
        }
    }

    @SubscribeEvent
    public void command(RegisterCommandsEvent event) {
        RTPCommand.register(event.getDispatcher());
        if(Config.useDimension()) {
            RTPDCommand.register(event.getDispatcher());
        }
        if(Config.useBiome()) {
            RTPBCommand.register(event.getDispatcher(), event.getBuildContext());
        }
    }

    @SubscribeEvent
    public void permission(PermissionGatherEvent.Nodes event) {
        RandomTPMod.getLogger().info("Registering permission nodes...");
        event.addNodes(BASIC_COMMAND_PERM, INTERDIM_COMMAND_PERM, INTERBIOME_COMMAND_PERM, COOLDOWN_EXEMPT_PERM);
    }
}