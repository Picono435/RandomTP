package com.gmail.picono435.randomtp.config;

import java.util.Arrays;
import java.util.List;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
	
	public static ForgeConfigSpec.ConfigValue<Integer> max_distance;
    public static ForgeConfigSpec.ConfigValue<Integer> min_distance;

    public static ForgeConfigSpec.ConfigValue<Integer> cooldown;
    public static ForgeConfigSpec.ConfigValue<Boolean> useOriginal;
    public static ForgeConfigSpec.ConfigValue<Integer> maxTries;
    
    public static ForgeConfigSpec.ConfigValue<Boolean> dim;
    public static ForgeConfigSpec.ConfigValue<Boolean> useWhitelist;
    public static ForgeConfigSpec.ConfigValue<List<String>> allowedDimensions;
	
	public static void initConfig(ForgeConfigSpec.Builder config) {
    	config.comment("Configuration file");
    	
    	//DISTANCE CATEGORY
    	max_distance = config
    			.comment(" Max distance that you want to a person be teleported. (0 = world border size / 2) [default: auto]")
    			.defineInRange("distance.max_distance", 0, 0, Integer.MAX_VALUE);
    	
    	min_distance = config
    			.comment("Minimum distance that you want to a person be teleported. [default: 1]")
    			.defineInRange("distance.min_distance", 1, 1, Integer.MAX_VALUE);
    	
    	//INTERDIM CATEGORY
    	dim = config
    			.comment("Do you want to the command /rtpd be allowed? (This commands adds a inter-dimension RTP) [default: true]")
    			.define("inter-dimensions-command.inter-dim", true);
    	
    	useWhitelist = config
    			.comment("Do you want to use the whitelist or blacklist dimension?  [default: false]")
    			.define("inter-dimensions-command.use-whitelist", false);
    	
    	String[] array = {"minecraft:the_end", "twilight:dimension"};
    	allowedDimensions = config
    			.comment("The dimensions whitelist (Works with namespaces:paths only, use-whitelist:true=whitelist use-whitelist:true=blacklist) [default: [minecraft:the_end], [twilight:dimension]]")
    			.define("inter-dimensions-command.whitelist-dimension", Arrays.asList(array));
    	
    	//OTHERS CATEGORY
    	cooldown = config
    			.comment("How much cooldown do you want for the command (put 0 for none) [range: 0 ~ 1000, default: 0]")
    			.defineInRange("others.cooldown", 0, 0, Integer.MAX_VALUE);
    	
    	useOriginal = config
    				.comment("If you want to use the original RTP system or the /spreadplayers system.")
    				.define("others.use-original", true);
    	
    	maxTries = config
				.comment("The amount of tries to find a safe location (original system) [-1 = infinite]")
				.define("others.max-tries", -1);
    }
}
