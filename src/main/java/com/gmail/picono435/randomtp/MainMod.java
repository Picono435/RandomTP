package com.gmail.picono435.randomtp;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.gmail.picono435.randomtp.commands.RTPCommand;
import com.gmail.picono435.randomtp.commands.RTPDCommand;
import com.gmail.picono435.randomtp.config.ConfigHandler;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;
 
@Mod("randomtp")
public class MainMod {
	
      public static final String NEW_LINE;
      
      public static Logger logger;
      public static MinecraftServer server;
      
	  public static boolean check;

      static {
    	  NEW_LINE = System.getProperty("line.separator");
      }
      
      public MainMod() {
    	  
    	  ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigHandler.config, "RandomTP" + File.separatorChar + "config.toml");
    	  ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigHandler.messages, "RandomTP" + File.separatorChar + "messages.toml");
    	  
    	  FMLJavaModLoadingContext.get().getModEventBus().addListener(this::preInit);
    	  
    	  ConfigHandler.loadConfig(ConfigHandler.config, FMLPaths.CONFIGDIR.get().resolve("RandomTP" + File.separatorChar + "config.toml").toString());
    	  ConfigHandler.loadConfig(ConfigHandler.messages, FMLPaths.CONFIGDIR.get().resolve("RandomTP" + File.separatorChar + "messages.toml").toString());
    	  
    	  MinecraftForge.EVENT_BUS.register(this);
      }
      
      @SubscribeEvent
      public void preInit(FMLCommonSetupEvent event) {
    	  logger = LogManager.getLogger();
      }

      @SubscribeEvent
      public void init(FMLServerStartingEvent event) {
        logger.info("Initalized Random Teleport Mod.");
        server = event.getServer();
        
        logger.info("Configs files loaded.");
        
        PermissionAPI.registerNode("randomtp.command.basic", DefaultPermissionLevel.ALL, "The permission to execute the command /randomtp");
        PermissionAPI.registerNode("randomtp.command.interdim", DefaultPermissionLevel.ALL, "The permission to execute the command /randomtpdimension");
        PermissionAPI.registerNode("randomtp.cooldown.exempt", DefaultPermissionLevel.OP, "The permission used to be exempt from the cooldown");
      }  
      
      @SubscribeEvent
      public void command(RegisterCommandsEvent event) {
    	  RTPCommand.register(event.getDispatcher());
    	  RTPDCommand.register(event.getDispatcher());
      }
}
