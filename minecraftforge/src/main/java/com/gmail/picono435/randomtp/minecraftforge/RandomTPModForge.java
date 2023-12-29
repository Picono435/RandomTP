package com.gmail.picono435.randomtp.minecraftforge;

import com.gmail.picono435.randomtp.RandomTPMod;

import com.gmail.picono435.randomtp.forge.EventBuses;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod(RandomTPMod.MOD_ID)
public class RandomTPModForge {

    public RandomTPModForge() {
        RandomTPMod.init();

        EventBuses eventBuses = new EventBuses();

        EventBuses.modInit();

        MinecraftForge.EVENT_BUS.register(eventBuses);
    }

}