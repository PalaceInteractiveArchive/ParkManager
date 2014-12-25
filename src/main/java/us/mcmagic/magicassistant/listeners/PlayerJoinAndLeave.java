package us.mcmagic.magicassistant.listeners;

import net.md_5.bungee.api.ChatColor;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.commands.Command_vanish;
import us.mcmagic.magicassistant.utils.BandUtil;
import us.mcmagic.magicassistant.utils.InventorySql;
import us.mcmagic.magicassistant.utils.VisibleUtil;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

public class PlayerJoinAndLeave implements Listener {
    public static MagicAssistant pl;

    public PlayerJoinAndLeave(MagicAssistant instance) {
        pl = instance;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        if (MagicAssistant.spawnOnJoin) {
            player.performCommand("spawn");
        }
        for (String msg : MagicAssistant.joinMessages) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
        }
        if (MagicAssistant.hubServer) {
            if (!player.hasPlayedBefore()) {
                for (String msg : MagicAssistant.newJoinMessage) {
                    String nmsg = msg.replaceAll("%pl%", player.getName());
                    Bukkit.broadcastMessage(nmsg.replaceAll("%total%", "" + (Bukkit.getOfflinePlayers().length + 100000)));
                }
                PlayerInventory pi = player.getInventory();
                for (Map.Entry<Integer, Integer> item : MagicAssistant.firstJoinItems.entrySet()) {
                    ItemStack i = new ItemStack(item.getKey(), item.getValue());
                    pi.addItem(i);
                }
            }
        }
        if (MagicAssistant.crossServerInv) {
            Bukkit.getScheduler().runTaskLaterAsynchronously(pl,
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
                            BandUtil.giveBandToPlayer(player);
                            player.sendMessage(ChatColor.GREEN + "Inventory Updated!");
                        }
                    }, 100L);
        }
        Bukkit.getScheduler().runTaskLaterAsynchronously(pl, new Runnable() {
            @Override
            public void run() {
                BandUtil.setupPlayerData(player);
                BandUtil.giveBandToPlayer(player);
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
            player.sendPluginMessage(pl, "BungeeCord",
                    b.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        if (MagicAssistant.crossServerInv) {
            Bukkit.getScheduler().runTaskAsynchronously(pl, new Runnable() {
                @Override
                public void run() {
                    InventorySql.updateInventory(player);
                    InventorySql.updateEndInventory(player);
                }
            });
        }
        BandUtil.removePlayerData(player);
        try {
            VisibleUtil.hideall.remove(player);
        } catch (Exception ignored) {
        }
        try {
            Command_vanish.hidden.remove(player);
        } catch (Exception ignored) {
        }
    }
}
