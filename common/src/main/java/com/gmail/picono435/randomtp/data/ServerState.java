package com.gmail.picono435.randomtp.data;

import com.gmail.picono435.randomtp.RandomTPMod;
import com.mojang.serialization.Codec;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ServerState extends SavedData {

    public static final Codec<ServerState> CODEC =
            Codec.unboundedMap(UUIDUtil.STRING_CODEC, PlayerState.CODEC)
                    .xmap(ServerState::new, ServerState::getPlayerStates);

    private static final SavedDataType<ServerState> TYPE = new SavedDataType<>(
            RandomTPMod.MOD_ID + "_server_state",
            ServerState::new,
            CODEC,
            null
    );

    private final HashMap<UUID, PlayerState> players = new HashMap<>();

    public ServerState() {
    }

    public ServerState(Map<UUID, PlayerState> players) {
        this.players.putAll(players);
    }

    public Map<UUID, PlayerState> getPlayerStates() {
        return this.players;
    }

    public PlayerState getPlayerState(UUID uuid) {
        return this.players.get(uuid);
    }

    public PlayerState getOrCreatePlayerState(UUID uuid) {
        return this.players.computeIfAbsent(uuid, ignored -> {
            setDirty();
            return new PlayerState(false);
        });
    }

    public void setHasJoined(UUID uuid, boolean hasJoined) {
        this.getOrCreatePlayerState(uuid).setHasJoined(hasJoined);
        setDirty();
    }

    public boolean hasPlayer(UUID uuid) {
        return this.players.containsKey(uuid);
    }

    public static ServerState get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(TYPE);
    }

    public static PlayerState getPlayerState(LivingEntity player) {
        ServerState serverState = get(player.level().getServer());

        return serverState.getOrCreatePlayerState(player.getUUID());
    }
}