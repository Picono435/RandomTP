package com.gmail.picono435.randomtp.config.fabric;

import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class ConfigHandlerImpl {

	public static Path getConfigDirectory() {
		return FabricLoader.getInstance().getConfigDir();
	}

}