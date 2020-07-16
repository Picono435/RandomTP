package com.gmail.picono435.randomtp;

import java.io.File;

import org.apache.logging.log4j.Logger;

import com.gmail.picono435.randomtp.commands.RTPCommand;
import com.gmail.picono435.randomtp.commands.RTPDCommand;
import com.gmail.picono435.randomtp.config.Config;
import com.gmail.picono435.randomtp.config.Messages;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;
 
@Mod(modid = MainMod.MODID, name = MainMod.NAME, version = MainMod.VERSION, acceptableRemoteVersions = "*")
public class MainMod {

      public static final String MODID = "randomtp";
      public static final String NAME = "Random Teleport Mod";
      public static final String VERSION = "1.3";

      public static final String NEW_LINE;
      
      public static Logger logger;
      public static Configuration config;
      public static Configuration messages;
      
	  public static boolean check;

      static {
    	  NEW_LINE = System.getProperty("line.separator");
      }
      
      
      @EventHandler
      
      
      public void preInit(FMLPreInitializationEvent event)
      {
          logger = event.getModLog();
      }

      @EventHandler
      
      public void init(FMLServerStartingEvent event)
      {
        logger.info("Initalized Random Teleport Mod.");
        event.registerServerCommand(new RTPCommand());
        if(Config.dim) {
        	event.registerServerCommand(new RTPDCommand());
        }
        
        config = new Configuration(new File("config/RandomTP/config.cfg"));
        Config.readConfig();
        
        messages = new Configuration(new File("config/RandomTP/messages.cfg"));
        Messages.readConfig();
        
        File f = new File("config/randomtp.cfg");
        if(f.exists()) {
        	if(f.delete()) {
        		logger.info("We detected that you used a old config file so we deleted it. The new config file is located in 'config/RandomTP/config.cfg', sorry for the changes.");
        	}
        }
        
        logger.info("Configs files loaded.");
        
        PermissionAPI.registerNode("randomtp.command.basic", DefaultPermissionLevel.OP, "The permission to execute the command /randomtp");
        PermissionAPI.registerNode("randomtp.command.interdim", DefaultPermissionLevel.OP, "The permission to execute the command /randomtp");
      }
      
      public void preInit(FMLPostInitializationEvent event)
      {
          if (config.hasChanged()) {
              config.save();
          }
      }
      
}
