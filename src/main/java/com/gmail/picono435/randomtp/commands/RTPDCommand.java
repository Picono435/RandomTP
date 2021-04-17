package com.gmail.picono435.randomtp.commands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.gmail.picono435.randomtp.api.RandomTPAPI;
import com.gmail.picono435.randomtp.config.Config;
import com.gmail.picono435.randomtp.config.Messages;
import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.server.permission.PermissionAPI;

public class RTPDCommand extends CommandBase {

	private final List<String> aliases = Lists.newArrayList("randomtpd", "rtpd", "randomteleportd", "rteleportd", "rndtpd", "rndteleportd","randomtpdimension", "rtpdimension", "randomteleportdimension", "rteleportdimension", "rndtpdimension", "rndteleportdimension");
	  
	public HashMap<String, Long> cooldowns = new HashMap<String, Long>();
	
	public static Block[] dangerBlockArray = { Blocks.LAVA, Blocks.FLOWING_LAVA, Blocks.WATER, Blocks.FLOWING_WATER, Blocks.AIR };
	  
  @Override
  public void execute(MinecraftServer server, ICommandSender sender, String[] params) throws CommandException {
    EntityPlayer p = getCommandSenderAsPlayer(sender);
    if(params.length < 1) {
    	p.sendMessage(new TextComponentString(Messages.invalidArgs.replaceAll("\\{playerName\\}", p.getName()).replace('&', '§')));
    	return;
    }
    if(!RandomTPAPI.checkCooldown(p, cooldowns) && !PermissionAPI.hasPermission(p, "randomtp.cooldown.exempt")) {
    	long secondsLeft = RandomTPAPI.getCooldownLeft(p, cooldowns);
    	TextComponentString cooldownmes = new TextComponentString(Messages.cooldown.replaceAll("\\{secondsLeft\\}", Long.toString(secondsLeft)).replaceAll("\\{playerName\\}", p.getName()).replace('&', '§'));
        p.sendMessage(cooldownmes);
        return;
    } else {
    	cooldowns.remove(p.getName());
    	int dimension = 0;
    	try {
    		dimension = Integer.parseInt(params[0]);
    	} catch(NumberFormatException ex) {
    		p.sendMessage(new TextComponentString(Messages.invalidDimension.replaceAll("\\{playerName\\}", p.getName()).replaceAll("\\{dimensionId\\}", params[0]).replace('&', '§')));
    		return;
    	}
    	if(!inWhitelist(params[0])) {
    		p.sendMessage(new TextComponentString(Messages.dimensionNotAllowed.replaceAll("\\{playerName\\}", p.getName()).replaceAll("\\{dimensionId\\}", params[0]).replace('&', '§')));
    		return;
    	}
    	if(Config.useOriginal) {
    		RandomTPAPI.randomTeleport(p, p.getServer().getWorld(dimension));
    		cooldowns.put(p.getName(), System.currentTimeMillis());
    		return;
    	}
    	
    	try {
    		p.changeDimension(dimension);
    	} catch(NullPointerException ex) {
    		p.sendMessage(new TextComponentString(Messages.invalidDimension.replaceAll("\\{playerName\\}", p.getName()).replaceAll("\\{dimensionId\\}", params[0]).replace('&', '§')));
    		return;
    	}
    	if(Config.max_distance == 0) {
        	server.getCommandManager().executeCommand(server, "spreadplayers " + p.world.getWorldBorder().getCenterX() + " " + p.world.getWorldBorder().getCenterZ() + " " + Config.min_distance + " " + p.world.getWorldBorder().getDiameter()/2 + " false " + p.getDisplayNameString());
        	TextComponentString succefull = new TextComponentString(Messages.succefully.replaceAll("\\{playerName\\}", p.getName()).replaceAll("\\{blockX\\}", "" + (int)p.getPositionVector().x).replaceAll("\\{blockY\\}", "" + (int)p.getPositionVector().y).replaceAll("\\{blockZ\\}", "" + (int)p.getPositionVector().z).replaceAll("&", "§"));
        	p.sendMessage(succefull);
        } else {
        	server.getCommandManager().executeCommand(server, "spreadplayers " + p.world.getWorldBorder().getCenterX() + " " + p.world.getWorldBorder().getCenterZ() + " " + Config.min_distance + " " + Config.max_distance + " false " + p.getDisplayNameString());
        	TextComponentString succefull = new TextComponentString(Messages.succefully.replaceAll("\\{playerName\\}", p.getName()).replaceAll("\\{blockX\\}", "" + (int)p.getPositionVector().x).replaceAll("\\{blockY\\}", "" + (int)p.getPositionVector().y).replaceAll("\\{blockZ\\}", "" + (int)p.getPositionVector().z).replaceAll("&", "§"));
        	p.sendMessage(succefull);
        }
        cooldowns.put(sender.getName(), System.currentTimeMillis());
    }
  }
  
  public boolean inWhitelist(String dimension) {
	  //WHITELIST
	  if(Config.useWhitelist) {
		  return Arrays.asList(Config.allowedDimensions).contains(dimension);
	  //BLACKLIST
	  } else {
		  return !Arrays.asList(Config.allowedDimensions).contains(dimension); 
	  }
  }
  
  @Override
  public String getName() {
    return "randomtp";
  }

  @Override
  public String getUsage(ICommandSender sender) {
    return "/rtpd - Teleports you randomly in the selected dimension.";
  }

  @Override
  public List<String> getAliases() 
  {
  	return aliases;
  }

  @Override
  public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
	  if(!(sender instanceof EntityPlayer)) return true;
	  try {
		return PermissionAPI.hasPermission(getCommandSenderAsPlayer(sender), "randomtp.command.interdim");
	  } catch (PlayerNotFoundException e) {
		return false;
	  }
  }

}
