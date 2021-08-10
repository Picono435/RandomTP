package com.gmail.picono435.randomtp.api.forge;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.permission.PermissionAPI;

public class RandomTPAPIImpl {

	public static boolean hasPermission(CommandSourceStack source, String permission) {
		try {
			return PermissionAPI.hasPermission(source.getPlayerOrException(), permission);
		} catch (CommandSyntaxException e) {
			return false;
		}
	}

	public static boolean hasPermission(ServerPlayer player, String permission) {
		return PermissionAPI.hasPermission(player, permission);
	}

}
