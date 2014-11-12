package com.legobuilder0813.MagicAssistant.Listeners;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;

import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.BanList.Type;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;

import com.legobuilder0813.MagicAssistant.MagicAssistant;
import com.legobuilder0813.MagicAssistant.Commands.Command_vanish;
import com.legobuilder0813.MagicAssistant.Utils.InventorySql;

public class PlayerJoinAndLeave implements Listener {
	public static MagicAssistant pl;

	public PlayerJoinAndLeave(MagicAssistant instance) {
		pl = instance;
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		if (!player.hasPermission("arcade.staff")) {
			Bukkit.getScheduler().runTaskAsynchronously(pl, new Runnable() {
				public void run() {
					for (Player tp : Bukkit.getOnlinePlayers()) {
						if (Command_vanish.hidden.containsKey(tp)) {
							player.hidePlayer(tp);
						}
					}
				}
			});
			// } else {
			// Bukkit.getScheduler().runTaskAsynchronously(pl, new Runnable() {
			// public void run() {
			// for (Player tp : Bukkit.getOnlinePlayers()) {
			// if (Command_vanish.hidden.containsKey(tp)) {
			// if (!tp.hasPermission("vanish.standard")) {
			// player.hidePlayer(tp);
			// }
			// }
			// }
			// }
			// });
		}
		Bukkit.getScheduler().runTaskLaterAsynchronously(
				Bukkit.getPluginManager().getPlugin("MagicAssistant"),
				new Runnable() {
					public void run() {
						if (!InventorySql.playerDataContainsPlayer(player)) {
							player.performCommand("spawn");
							InventorySql.setupData(player);
						} else {
							player.getInventory().setContents(
									InventorySql.invContents(player));
							player.getInventory().setArmorContents(
									InventorySql.armorContents(player));
						}
						if (!InventorySql.endPlayerDataContainsPlayer(player)) {
							InventorySql.setupEndData(player);
						} else {
							player.getEnderChest().setContents(
									InventorySql.endInvContents(player));
						}
					}
				}, 20L);
	}

	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event) {
		Player player = event.getPlayer();
		if (event.getResult().equals(Result.KICK_BANNED)) {
			BanList list = Bukkit.getBanList(Type.NAME);
			BanEntry entry = list.getBanEntry(player.getName());
			String reason = entry.getReason();
			banPlayer(player, reason, new Date(System.currentTimeMillis()),
					true);
			// BanUtil.banPlayer(player.getUniqueId() + "", reason,
			// permanent,
			// entry.getExpiration(), "Console");
			return;
		}
	}

	@SuppressWarnings("deprecation")
	public void banPlayer(Player player, String reason, Date expiration,
			boolean permanent) {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		try {
			out.writeUTF("MagicBan");
			out.writeUTF(player.getName());
			out.writeUTF(reason);
			out.writeBoolean(permanent);
			out.writeLong(expiration.getTime());
			Bukkit.getOnlinePlayers()[0].sendPluginMessage(pl, "BungeeCord",
					b.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		final Player player = event.getPlayer();
		Bukkit.getScheduler().runTaskAsynchronously(pl, new Runnable() {
			public void run() {
				if (InventorySql.playerDataContainsPlayer(player)) {
					InventorySql.updateInventory(player);
				}
				if (InventorySql.endPlayerDataContainsPlayer(player)) {
					InventorySql.updateEndInventory(player);
				}
			}
		});
	}

	public void banPluginMessage(Player player, String reason,
			boolean permanent, long release) {
	}
}