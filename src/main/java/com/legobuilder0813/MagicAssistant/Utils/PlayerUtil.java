package com.legobuilder0813.MagicAssistant.Utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class PlayerUtil {

	@SuppressWarnings("deprecation")
	public static Player findPlayer(String name) {
		for (Player tp : onlinePlayers()) {
			if (tp.getName().toLowerCase().contains(name.toLowerCase())) {
				return tp;
			}
		}
		return null;
	}

	public static List<Player> onlinePlayers(){
		return Bukkit.getOnlinePlayers();
	}
}