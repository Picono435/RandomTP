package com.gmail.picono435.randomtp.neoforge;

import com.gmail.picono435.randomtp.RandomTPMod;

import com.gmail.picono435.randomtp.forge.EventBuses;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod(RandomTPMod.MOD_ID)
public class RandomTPModNeoForge {

    public RandomTPModNeoForge() {
        RandomTPMod.init();

        EventBuses eventBuses = new EventBuses();

        EventBuses.modInit();

        NeoForge.EVENT_BUS.register(eventBuses);
    }
}