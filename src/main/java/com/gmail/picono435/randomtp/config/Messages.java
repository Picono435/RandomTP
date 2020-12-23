package com.gmail.picono435.randomtp.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class Messages {
	
    public static ForgeConfigSpec.ConfigValue<String> succefully;
    public static ForgeConfigSpec.ConfigValue<String> cooldown;
    public static ForgeConfigSpec.ConfigValue<String> dimensionNotAllowed;
    public static ForgeConfigSpec.ConfigValue<String> maxTries;

    public static void initConfig(ForgeConfigSpec.Builder config) {
    	config.comment("Configuration file");
    	
    	succefully = config
    			.comment("Message that you want to appier when the command is succefully made. [ placeholders: {playerName} {blockZ} {blockY}, color codes: & + letter (example: &c) ] [default: &aSuccefully teleported you to a random location.]")
    			.define("command.succefully", "&aYou have been teleported to the coordinates &e{blockX}, {blockY}, {blockZ}&a.");
    	
    	cooldown = config
    			.comment("Message that you want to appier when the command is on cooldown. [ placeholders: {playerName}, color codes: & + letter (example: &c) ] [default: &cWait more {secondsLeft} seconds for execute the command again.]")
    			.define("command.cooldown", "&cWait more {secondsLeft} seconds for execute the command again.");
    	
    	dimensionNotAllowed = config
    			.comment("Message that you want to appier when you execute /rtpd with a dimension that is in the blacklist [ placeholders: {playerName} {dimensionId}, color codes: & + letter (example: &c) ]. [default: &cYou cannot random teleport to that dimension!]")
    			.define("command.dimensionNotAllowed", "&cYou cannot random teleport to that dimension!");
    
    	maxTries = config
    			.comment("Message that you want to appier when the max tries of finding a safe location is reached [ placeholders: {playerName}, color codes: & + letter (example: &c) ].")
    			.define("command.max-tries", "&cTimed out trying to find a safe location to warp to.");
    }
}