package com.gmail.picono435.randomtp.config;

import java.util.Arrays;
import java.util.List;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
	
	public static ForgeConfigSpec.ConfigValue<Integer> max_distance;
    public static ForgeConfigSpec.ConfigValue<Integer> min_distance;
    
    public static ForgeConfigSpec.ConfigValue<Boolean> only_op_basic;
    public static ForgeConfigSpec.ConfigValue<Boolean> only_op_dim;
    
    public static ForgeConfigSpec.ConfigValue<Integer> cooldown;
    
    public static ForgeConfigSpec.ConfigValue<Boolean> dim;
    public static ForgeConfigSpec.ConfigValue<Boolean> useWhitelist;
    public static ForgeConfigSpec.ConfigValue<List<String>> allowedDimensions;
	
	public static void initConfig(ForgeConfigSpec.Builder config) {
    	config.comment("Configuration file");
    	
    	//DISTANCE CATEGORY
    	max_distance = config
    			.comment(" Max distance that you want to a person be teleported. (auto = world border size / 2) [default: auto]")
    			.defineInRange("distance.max_distance", 0, 0, Integer.MAX_VALUE);
    	
    	min_distance = config
    			.comment("Minimum distance that you want to a person be teleported. [default: 1]")
    			.defineInRange("distance.min_distance", 1, 1, Integer.MAX_VALUE);
    	
    	//INTERDIM CATEGORY
    	dim = config
    			.comment("Do you want to the command /rtpd be allowed? (This commands adds a inter-dimension RTP) [default: true]")
    			.define("inter-dimensions-command.inter-dim", true);
    	
    	useWhitelist = config
    			.comment("Do you want to use the whitelist or blacklist dimension?  [default: true]")
    			.define("inter-dimensions-command.use-whitelist", true);
    	
    	String[] array = {"1", "-1"};
    	allowedDimensions = config
    			.comment("The dimensions whitelist (Works with IDs only, use-whitelist:true=whitelist use-whitelist:true=blacklist) [default: [1], [2]]")
    			.define("inter-dimensions.allowed-dimensions", Arrays.asList(array));
    	
    	//OTHERS CATEGORY
    	cooldown = config
    			.comment("How much cooldown do you want for the command (put 0 for none) [range: 0 ~ 1000, default: 0]")
    			.defineInRange("others.cooldown", 0, 0, Integer.MAX_VALUE);
    	
    	//PERMISSION CATEGORY
    	only_op_basic = config
    			.comment("If you want only op players or with the required permission node to execute the basic /rtp command. (Permission node: randomtp.command.basic) [default: true]")
    			.define("permission.only_op_basic", true);
    	
    	only_op_dim = config
    			.comment("If you want only op players or with the required permission node to execute the inter dimension /rtpd command. (Permission node: randomtp.command.interdim) [default: true])")
    			.define("permission.only_op_dim", true);
    }
}
