package com.gmail.picono435.randomtp.data;

import com.gmail.picono435.randomtp.RandomTPMod;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.HashMap;
import java.util.UUID;

public class ServerState extends SavedData {

    public HashMap<UUID, PlayerState> players = new HashMap<>();

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        CompoundTag playersNbtCompound = new CompoundTag();
        players.forEach((UUID, playerSate) -> {
            CompoundTag playerStateNbt = new CompoundTag();

            playerStateNbt.putBoolean("hasJoined", playerSate.hasJoined);

            playersNbtCompound.put(String.valueOf(UUID), playerStateNbt);
        });
        compoundTag.put("players", playersNbtCompound);
        return compoundTag;
    }

    public static ServerState createFromNbt(CompoundTag compoundTag) {
        ServerState serverState = new ServerState();

        CompoundTag playersTag = compoundTag.getCompound("players");
        playersTag.getAllKeys().forEach(key -> {
            PlayerState playerState = new PlayerState();

            playerState.hasJoined = playersTag.getCompound(key).getBoolean("hasJoined");

            UUID uuid = UUID.fromString(key);
            serverState.players.put(uuid, playerState);
        });

        return serverState;
    }

    public static ServerState getServerState(MinecraftServer server) {
        DimensionDataStorage persistentStateManager = server
                .getLevel(Level.OVERWORLD).getDataStorage();

        ServerState serverState = persistentStateManager.computeIfAbsent(
                ServerState::createFromNbt,
                ServerState::new,
                RandomTPMod.MOD_ID);

        serverState.setDirty();

        return serverState;
    }

    public static PlayerState getPlayerState(LivingEntity player) {
        ServerState serverState = getServerState(player.level.getServer());

        PlayerState playerState = serverState.players.computeIfAbsent(player.getUUID(), uuid -> new PlayerState());

        return playerState;
    }
}
