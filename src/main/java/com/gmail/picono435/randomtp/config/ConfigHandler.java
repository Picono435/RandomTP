package com.gmail.picono435.randomtp.config;

import java.io.File;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod.EventBusSubscriber
public class ConfigHandler {
    
    private static final ForgeConfigSpec.Builder builder_config = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec config;
    private static final ForgeConfigSpec.Builder messages_config = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec messages;
    
    static {
    	Config.initConfig(builder_config);
    	Messages.initConfig(messages_config);
    	
    	config = builder_config.build();
    	messages = messages_config.build();
    }
    
    public static void loadConfig(ForgeConfigSpec config, String path) {
    	final File folder = new File(FMLPaths.CONFIGDIR.get().resolve("RandomTP").toString());
    	if(!folder.exists()) {
    		folder.mkdir();
    	}
    	final CommentedFileConfig file = CommentedFileConfig.builder(new File(path)).sync().autosave().writingMode(WritingMode.REPLACE).build();
    	file.load();
    	config.setConfig(file);
    }
}