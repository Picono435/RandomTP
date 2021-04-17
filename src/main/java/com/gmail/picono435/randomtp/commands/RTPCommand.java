package com.gmail.picono435.randomtp.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gmail.picono435.randomtp.MainMod;
import com.gmail.picono435.randomtp.api.RandomTPAPI;
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
import net.minecraftforge.server.permission.PermissionAPI;

public class RTPCommand extends CommandBase {

	private final List<String> aliases = Lists.newArrayList(MainMod.MODID, "randomtp", "rtp", "randomteleport", "rteleport", "rndtp", "rndteleport");
	  
	public Map<String, Long> cooldowns = new HashMap<String, Long>();
		
  @Override
  public void execute(MinecraftServer server, ICommandSender sender, String[] params) throws CommandException {
    EntityPlayer p = getCommandSenderAsPlayer(sender);
    if(!RandomTPAPI.checkCooldown(p, cooldowns) && !PermissionAPI.hasPermission(p, "randomtp.cooldown.exempt")) {
    	long secondsLeft = RandomTPAPI.getCooldownLeft(p, cooldowns);
    	TextComponentString cooldownmes = new TextComponentString(Messages.cooldown.replaceAll("\\{secondsLeft\\}", Long.toString(secondsLeft)).replaceAll("\\{playerName\\}", p.getName()).replaceAll("&", "§"));
        p.sendMessage(cooldownmes);
        return;
    } else {
    	cooldowns.remove(p.getName());
    	if(Config.useOriginal) {
    		if(!Config.default_world.equals("playerworld")) {
    			RandomTPAPI.randomTeleport(p, p.getServer().getWorld(Integer.parseInt(Config.default_world)));
    		} else {
    			RandomTPAPI.randomTeleport(p, p.world);
    		}
    		cooldowns.put(sender.getName(), System.currentTimeMillis());
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
  
  @Override
  public String getName() {
    return "randomtp";
  }

  @Override
  public String getUsage(ICommandSender sender) {
    return "/rtp - Teleports you randomly in the map.";
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
		return PermissionAPI.hasPermission(getCommandSenderAsPlayer(sender), "randomtp.command.basic");
	  } catch (PlayerNotFoundException e) {
		return false;
	  }
  }
}
