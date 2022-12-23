package com.gmail.picono435.randomtp.api.forge;

import com.gmail.picono435.randomtp.forge.RandomTPModForge;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.permission.PermissionAPI;

public class RandomTPAPIImpl {

	public static boolean hasPermission(CommandSourceStack source, String permission) {
		try {
			if(permission.equalsIgnoreCase("randomtp.command.basic")) {
				return PermissionAPI.getPermission(source.getPlayerOrException(), RandomTPModForge.BASIC_COMMAND_PERM);
			} else if(permission.equalsIgnoreCase("randomtp.command.interdim")) {
				return PermissionAPI.getPermission(source.getPlayerOrException(), RandomTPModForge.INTERDIM_COMMAND_PERM);
			} else if(permission.equalsIgnoreCase("randomtp.command.interbiome")) {
				return PermissionAPI.getPermission(source.getPlayerOrException(), RandomTPModForge.INTERBIOME_COMMAND_PERM);
			} else if(permission.equalsIgnoreCase("randomtp.cooldown.exempt")) {
				return PermissionAPI.getPermission(source.getPlayerOrException(), RandomTPModForge.COOLDOWN_EXEMPT_PERM);
			} else {
				return true;
			}
		} catch (CommandSyntaxException e) {
			return false;
		}
	}

	public static boolean hasPermission(ServerPlayer player, String permission) {
		if(permission.equalsIgnoreCase("randomtp.command.basic")) {
			return PermissionAPI.getPermission(player, RandomTPModForge.BASIC_COMMAND_PERM);
		} else if(permission.equalsIgnoreCase("randomtp.command.interdim")) {
			return PermissionAPI.getPermission(player, RandomTPModForge.INTERDIM_COMMAND_PERM);
		} else if(permission.equalsIgnoreCase("randomtp.cooldown.exempt")) {
			return PermissionAPI.getPermission(player, RandomTPModForge.COOLDOWN_EXEMPT_PERM);
		} else {
			return true;
		}
	}

}
