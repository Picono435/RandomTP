package com.gmail.picono435.randomtp.api.fabric;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.commands.CommandSourceStack;

import java.util.HashMap;
import java.util.Map;

public class RandomTPAPIImpl {

	public static Map<String, Integer> registeredNodes = new HashMap<String, Integer>();

	public static boolean hasPermission(CommandSourceStack source, String permission) {
		return Permissions.check(source, permission, registeredNodes.get(permission));
	}

}
