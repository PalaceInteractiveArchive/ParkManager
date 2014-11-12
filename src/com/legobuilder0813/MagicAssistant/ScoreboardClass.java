package com.legobuilder0813.MagicAssistant;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class ScoreboardClass implements Listener {
	static MagicAssistant pl;

	public ScoreboardClass(MagicAssistant instance) {
		pl = instance;
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();
		Team admin = sb.registerNewTeam("admin");
		Team cm = sb.registerNewTeam("cm");
		Team sg = sb.registerNewTeam("sg");
		Team intern = sb.registerNewTeam("intern");
		Team character = sb.registerNewTeam("character");
		Team donor = sb.registerNewTeam("donor");
		Team guest = sb.registerNewTeam("guest");
		admin.setPrefix(ChatColor.GOLD + "");
		cm.setPrefix(ChatColor.GREEN + "");
		sg.setPrefix(ChatColor.DARK_PURPLE + "");
		intern.setPrefix(ChatColor.DARK_GREEN + "");
		character.setPrefix(ChatColor.BLUE + "");
		donor.setPrefix(ChatColor.AQUA + "");
		guest.setPrefix(ChatColor.GRAY + "");
		player.setScoreboard(sb);
		for (Player tp : Bukkit.getOnlinePlayers()) {
			setScoreboardColorForOtherPlayer(tp, player);
			if (isInPermGroup(tp, "mayor") || isInPermGroup(tp, "technician")
					|| isInPermGroup(tp, "manager")) {
				admin.addPlayer(tp);
			} else if (isInPermGroup(tp, "moderator")) {
				cm.addPlayer(tp);
			} else if (isInPermGroup(tp, "specialguest")) {
				sg.addPlayer(tp);
			} else if (isInPermGroup(tp, "character")
					|| isInPermGroup(tp, "characterguest")) {
				character.addPlayer(tp);
			} else if (isInPermGroup(tp, "intern")) {
				intern.addPlayer(tp);
			} else if (isInPermGroup(tp, "donor")) {
				donor.addPlayer(tp);
			} else {
				guest.addPlayer(tp);
			}
		}
	}

	public static void setScoreboardColorForOtherPlayer(Player player, Player tp) {
		Scoreboard sb = player.getScoreboard();
		if (isInPermGroup(tp, "mayor") || isInPermGroup(tp, "technician")
				|| isInPermGroup(tp, "manager")) {
			sb.getTeam("admin").addPlayer(tp);
		} else if (isInPermGroup(tp, "moderator")) {
			sb.getTeam("cm").addPlayer(tp);
		} else if (isInPermGroup(tp, "specialguest")) {
			sb.getTeam("sg").addPlayer(tp);
		} else if (isInPermGroup(tp, "character")
				|| isInPermGroup(tp, "characterguest")) {
			sb.getTeam("character").addPlayer(tp);
		} else if (isInPermGroup(tp, "intern")) {
			sb.getTeam("intern").addPlayer(tp);
		} else if (isInPermGroup(tp, "donor")) {
			sb.getTeam("donor").addPlayer(tp);
		} else {
			sb.getTeam("guest").addPlayer(tp);
		}
	}

	public static boolean isInPermGroup(Player player, String group) {
		String[] groups = WorldGuardPlugin.inst().getGroups(player);
		for (int i = 0; i < groups.length; i++) {
			if (groups[i].toLowerCase().equals(group.toLowerCase())) {
				return true;
			}
		}
		return false;
	}
}