package com.gmail.picono435.randomtp.commands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.gmail.picono435.randomtp.config.Config;
import com.gmail.picono435.randomtp.config.Messages;
import com.google.common.collect.Lists;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import net.minecraftforge.server.permission.PermissionAPI;

public class RTPDCommand extends CommandBase {

	private final List<String> aliases = Lists.newArrayList("randomtpd", "rtpd", "randomteleportd", "rteleportd", "rndtpd", "rndteleportd","randomtpdimension", "rtpdimension", "randomteleportdimension", "rteleportdimension", "rndtpdimension", "rndteleportdimension");
	  
	public HashMap<String, Long> cooldowns = new HashMap<String, Long>();
	  
  @Override
  public void execute(MinecraftServer server, ICommandSender sender, String[] params) throws CommandException {
    World world = getCommandSenderAsPlayer(sender).getEntityWorld();
    WorldBorder border = world.getWorldBorder();
    EntityPlayer p = getCommandSenderAsPlayer(sender);
    if(params.length < 1) {
    	p.sendMessage(new TextComponentString(Messages.invalidArgs.replaceAll("\\{playerName\\}", p.getName()).replace('&', '§')));
    	return;
    }
    TextComponentString succefull = new TextComponentString(Messages.succefully.replaceAll("\\{playerName\\}", p.getName()).replaceAll("\\{blockZ\\}", "" + p.getPositionVector().z).replaceAll("\\{blockX\\}", "" + p.getPositionVector().x).replace('&', '§'));
    if(!checkCooldown(p)) {
    	long secondsLeft = getCooldownLeft(p);
    	TextComponentString cooldownmes = new TextComponentString(Messages.cooldown.replaceAll("\\{secondsLeft\\}", Long.toString(secondsLeft)).replaceAll("\\{playerName\\}", p.getName()).replace('&', '§'));
        p.sendMessage(cooldownmes);
        return;
    } else {
    	p.setPortal(p.getPosition());
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
    	try {
    		p.changeDimension(dimension);
    	} catch(NullPointerException ex) {
    		p.sendMessage(new TextComponentString(Messages.invalidDimension.replaceAll("\\{playerName\\}", p.getName()).replaceAll("\\{dimensionId\\}", params[0]).replace('&', '§')));
    		return;
    	}
        if(Config.max_distance == 0) {
        	server.getCommandManager().executeCommand(server, "spreadplayers " + border.getCenterX() + " " + border.getCenterZ() + " " + Config.min_distance + " " + border.getDiameter()/2 + " false " + p.getDisplayNameString());
        	p.sendMessage(succefull);
        } else {
    		server.getCommandManager().executeCommand(server, "spreadplayers " + border.getCenterX() + " " + border.getCenterZ() + " " + Config.min_distance + " " + Config.max_distance + " false " + p.getDisplayNameString());
    		p.sendMessage(succefull);
        }
        cooldowns.put(sender.getName(), System.currentTimeMillis());
    }
  }
  
  public boolean checkCooldown(EntityPlayer p) {
	  int cooldownTime = Config.cooldown;
	  if(cooldowns.containsKey(p.getName())) {
		  long secondsLeft = ((cooldowns.get(p.getName())/1000)+cooldownTime) - (System.currentTimeMillis()/1000);
	      if(secondsLeft > 0) {
	    	  return false;
	      } else {
	    	  cooldowns.remove(p.getName());
	    	  return true;
	      }
	  } else {
		  return true;
	  }
  }
  
  public long getCooldownLeft(EntityPlayer p) {
	  int cooldownTime = Config.cooldown;
	  long secondsLeft = ((cooldowns.get(p.getName())/1000)+cooldownTime) - (System.currentTimeMillis()/1000);
	  return secondsLeft;
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
	  if(Config.only_op_dim) {
		  EntityPlayer p;
		  try {
			  p = getCommandSenderAsPlayer(sender);
		  } catch (PlayerNotFoundException e) {
			  return true;
		  }
		  return PermissionAPI.hasPermission(p, "randomtp.command.interdim");
	  } else {
		  return true;
	  }
  }


}
