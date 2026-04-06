package com.gmail.picono435.randomtp.neoforge;

import com.gmail.picono435.randomtp.RandomTPMod;
import com.gmail.picono435.randomtp.commands.RTPBCommand;
import com.gmail.picono435.randomtp.commands.RTPCommand;
import com.gmail.picono435.randomtp.commands.RTPDCommand;
import com.gmail.picono435.randomtp.config.Config;
import com.gmail.picono435.randomtp.config.ConfigHandler;
import com.gmail.picono435.randomtp.data.PlayerState;
import com.gmail.picono435.randomtp.data.ServerState;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.server.permission.events.PermissionGatherEvent;
import net.neoforged.neoforge.server.permission.nodes.PermissionNode;
import net.neoforged.neoforge.server.permission.nodes.PermissionTypes;

public class EventBuses {

    public static PermissionNode<Boolean> BASIC_COMMAND_PERM = new PermissionNode<>(
            RandomTPMod.MOD_ID,
            "command.basic",
            PermissionTypes.BOOLEAN,
            (player, uuid, permissionDynamicContexts) -> true);
    public static PermissionNode<Boolean> INTERDIM_COMMAND_PERM = new PermissionNode<>(
            RandomTPMod.MOD_ID,
            "command.interdim",
            PermissionTypes.BOOLEAN,
            (player, uuid, permissionDynamicContexts) -> true);
    public static PermissionNode<Boolean> INTERBIOME_COMMAND_PERM = new PermissionNode<>(
            RandomTPMod.MOD_ID,
            "command.interbiome",
            PermissionTypes.BOOLEAN,
            (player, uuid, permissionDynamicContexts) -> true);
    public static PermissionNode<Boolean> COOLDOWN_EXEMPT_PERM = new PermissionNode<>(
            RandomTPMod.MOD_ID,
            "cooldown.exempt",
            PermissionTypes.BOOLEAN,
            (player, uuid, permissionDynamicContexts) -> {
                if(player == null) {
                    return false;
                } else {
                    return player.permissions().hasPermission(Permissions.COMMANDS_ADMIN);
                }
            });

    public static void modInit() {
        RandomTPMod.init();

        RandomTPMod.getLogger().info("Loading config files...");

        try {
            ConfigHandler.loadConfiguration();
        } catch (Exception e) {
            e.printStackTrace();
            RandomTPMod.getLogger().info("An error occuried while loading configuration.");
        }

        RandomTPMod.getLogger().info("Config files loaded.");
    }

    @SubscribeEvent
    public void init(ServerStartingEvent event) {
        RandomTPMod.getLogger().info("RandomTP successfully loaded.");
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if(event.getEntity().level().isClientSide()) return;
        PlayerState playerState = ServerState.getPlayerState(event.getEntity());
        if(!playerState.hasJoined()) {
            RandomTPMod.spawnTeleportPlayer((ServerPlayer) event.getEntity());
            ServerState.get(event.getEntity().level().getServer()).setHasJoined(event.getEntity().getUUID(), true);
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
