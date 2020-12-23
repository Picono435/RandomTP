package com.gmail.picono435.randomtp.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.gmail.picono435.randomtp.MainMod;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import net.minecraftforge.server.permission.PermissionAPI;

public class RTPCommand extends CommandBase {

	private final List<String> aliases = Lists.newArrayList(MainMod.MODID, "randomtp", "rtp", "randomteleport", "rteleport", "rndtp", "rndteleport");
	  
	public HashMap<String, Long> cooldowns = new HashMap<String, Long>();
	
	public static Block[] dangerBlockArray = { Blocks.LAVA, Blocks.FLOWING_LAVA, Blocks.WATER, Blocks.FLOWING_WATER, Blocks.AIR };
	
  @Override
  public void execute(MinecraftServer server, ICommandSender sender, String[] params) throws CommandException {
    World world = getCommandSenderAsPlayer(sender).getEntityWorld();
    WorldBorder border = world.getWorldBorder();
    EntityPlayer p = getCommandSenderAsPlayer(sender);
    if(!checkCooldown(p) && !PermissionAPI.hasPermission(p, "randomtp.cooldown.exempt")) {
    	long secondsLeft = getCooldownLeft(p);
    	TextComponentString cooldownmes = new TextComponentString(Messages.cooldown.replaceAll("\\{secondsLeft\\}", Long.toString(secondsLeft)).replaceAll("\\{playerName\\}", p.getName()).replaceAll("&", "§"));
        p.sendMessage(cooldownmes);
        return;
    } else {
    	if(Config.useOriginal) {
    		randomTeleport(p);
    		cooldowns.put(sender.getName(), System.currentTimeMillis());
    		return;
    	}
    	if(Config.max_distance == 0) {
            server.getCommandManager().executeCommand(server, "spreadplayers " + border.getCenterX() + " " + border.getCenterZ() + " " + Config.min_distance + " " + border.getDiameter()/2 + " false " + p.getDisplayNameString());
            TextComponentString succefull = new TextComponentString(Messages.succefully.replaceAll("\\{playerName\\}", p.getName()).replaceAll("\\{blockX\\}", "" + (int)p.getPositionVector().x).replaceAll("\\{blockY\\}", "" + (int)p.getPositionVector().y).replaceAll("\\{blockZ\\}", "" + (int)p.getPositionVector().z).replaceAll("&", "§"));
            p.sendMessage(succefull);
        } else {
        	server.getCommandManager().executeCommand(server, "spreadplayers " + border.getCenterX() + " " + border.getCenterZ() + " " + Config.min_distance + " " + Config.max_distance + " false " + p.getDisplayNameString());
        	TextComponentString succefull = new TextComponentString(Messages.succefully.replaceAll("\\{playerName\\}", p.getName()).replaceAll("\\{blockX\\}", "" + (int)p.getPositionVector().x).replaceAll("\\{blockY\\}", "" + (int)p.getPositionVector().y).replaceAll("\\{blockZ\\}", "" + (int)p.getPositionVector().z).replaceAll("&", "§"));
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


  private void randomTeleport(EntityPlayer p) {
	  Random r = new Random();
	  int low = Config.min_distance;
	  int high = Config.max_distance;
	  if(high == 0) {
		  high = (int) (p.world.getWorldBorder().getDiameter() / 2);
	  }
	  int x = r.nextInt(high-low) + low;
	  int y = 50;
	  int z = r.nextInt(high-low) + low;
	  int maxTries = Config.maxTries;
	  while (!isSafe(p, x, y, z) && (maxTries == -1 || maxTries > 0)) {
		  y++;
		  if(y >= 120) {
			  x = r.nextInt(high-low) + low;
			  y = 50;
			  z = r.nextInt(high-low) + low;
			  continue;
		  }
		  if(maxTries > 0){
			  maxTries--;
		  }
		  if(maxTries == 0) {
			  TextComponentString msg = new TextComponentString(Messages.maxTries.replaceAll("\\{playerName\\}", p.getName()).replaceAll("&", "§"));
			  p.sendMessage(msg);
			  return;
		  }
	  }
	  
	  p.setPositionAndUpdate(x, y, z);
	  TextComponentString succefull = new TextComponentString(Messages.succefully.replaceAll("\\{playerName\\}", p.getName()).replaceAll("\\{blockX\\}", "" + (int)p.getPositionVector().x).replaceAll("\\{blockY\\}", "" + (int)p.getPositionVector().y).replaceAll("\\{blockZ\\}", "" + (int)p.getPositionVector().z).replaceAll("&", "§"));
	  p.sendMessage(succefull);
  }
  
  private boolean isSafe(EntityPlayer player, int newX, int newY, int newZ) {
	if ((isEmpty(player.world, newX, newY, newZ)) && 
			(!isDangerBlock(player.world, newX, newY - 1, newZ))) {
		return true;
	}
	return false;
  }
	  
  private boolean isEmpty(World world, int newX, int newY, int newZ) {
	if ((world.isAirBlock(new BlockPos(newX, newY, newZ))) && (world.isAirBlock(new BlockPos(newX, newY + 1, newZ))) && 
			(world.isAirBlock(new BlockPos(newX + 1, newY, newZ))) && (world.isAirBlock(new BlockPos(newX - 1, newY, newZ))) && 
			(world.isAirBlock(new BlockPos(newX, newY, newZ + 1))) && (world.isAirBlock(new BlockPos(newX, newY, newZ - 1)))) {
		return true;
	}
	return false;
  }
	  
  private boolean isDangerBlock(World world, int newX, int newY, int newZ) {
	for (Block block : dangerBlockArray) {
		if (block.equals(world.getBlockState(new BlockPos(newX, newY, newZ)).getBlock())) {
			return true;
		}
	}
	return false;
  }
}
