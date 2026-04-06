package com.gmail.picono435.randomtp.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class PlayerState {

    public static final Codec<PlayerState> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.BOOL.fieldOf("hasJoined").forGetter(PlayerState::hasJoined)
            ).apply(instance, PlayerState::new)
    );

    private boolean hasJoined;

    public PlayerState(boolean hasJoined) {
        this.hasJoined = hasJoined;
    }

    public boolean hasJoined() {
        return this.hasJoined;
    }

    public void setHasJoined(boolean hasJoined) {
        this.hasJoined = hasJoined;
    }
}