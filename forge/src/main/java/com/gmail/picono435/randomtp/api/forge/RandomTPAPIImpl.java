package com.gmail.picono435.randomtp.api.forge;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.server.permission.PermissionAPI;

public class RandomTPAPIImpl {
	
	public static boolean hasPermission(CommandSourceStack source, String permission) {
		try {
			return PermissionAPI.hasPermission(source.getPlayerOrException(), permission);
		} catch (CommandSyntaxException e) {
			return false;
		}
	}

}
