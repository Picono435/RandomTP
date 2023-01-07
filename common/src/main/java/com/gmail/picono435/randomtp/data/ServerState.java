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
        // Putting the 'players' hashmap, into the 'nbt' which will be saved.
        CompoundTag playersNbtCompound = new CompoundTag();
        players.forEach((UUID, playerSate) -> {
            CompoundTag playerStateNbt = new CompoundTag();

            // ANYTIME YOU PUT NEW DATA IN THE PlayerState CLASS YOU NEED TO REFLECT THAT HERE!!!
            playerStateNbt.putBoolean("hasJoined", playerSate.hasJoined);

            playersNbtCompound.put(String.valueOf(UUID), playerStateNbt);
        });
        compoundTag.put("players", playersNbtCompound);
        return compoundTag;
    }

    public static ServerState createFromNbt(CompoundTag compoundTag) {
        ServerState serverState = new ServerState();

        // Here we are basically reversing what we did in ''save'' and putting the data inside the tag back to our hashmap
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
        // First we get the persistentStateManager for the OVERWORLD
        DimensionDataStorage persistentStateManager = server
                .getLevel(Level.OVERWORLD).getDataStorage();

        // Calling this reads the file from the disk if it exists, or creates a new one and saves it to the disk
        // You need to use a unique string as the key. You should already have a MODID variable defined by you somewhere in your code. Use that.
        ServerState serverState = persistentStateManager.computeIfAbsent(
                ServerState::createFromNbt,
                ServerState::new,
                RandomTPMod.MOD_ID);

        serverState.setDirty(); // YOU MUST DO THIS!!!! Or data wont be saved correctly.

        return serverState;
    }

    public static PlayerState getPlayerState(LivingEntity player) {
        ServerState serverState = getServerState(player.level.getServer());

        // Either get the player by the uuid, or we don't have data for him yet, make a new player state
        PlayerState playerState = serverState.players.computeIfAbsent(player.getUUID(), uuid -> new PlayerState());

        return playerState;
    }
}
