package com.legobuilder0813.MagicAssistant.Utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerUtil {

	@SuppressWarnings("deprecation")
	public static Player findPlayer(String name) {
		for (Player tp : Bukkit.getOnlinePlayers()) {
			if (tp.getName().toLowerCase().contains(name.toLowerCase())) {
				return tp;
			}
		}
		return null;
	}
}