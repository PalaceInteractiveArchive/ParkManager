package us.mcmagic.magicassistant;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class StitchEscape implements Listener {
    static MagicAssistant pl;

    public StitchEscape(MagicAssistant instance) {
        pl = instance;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        if (pl.watching.containsKey(player)) {
            Location from = event.getFrom();
            Location to = event.getTo();
            if ((from.getX() != to.getX()) && (from.getZ() != to.getZ()) || (from.getX() != to.getX()) || (from.getZ() != to.getZ())) {
                event.setCancelled(true);
                int i = player.getMetadata("stitch-seat-number").get(0).asInt();
                double x = pl.getConfig().getDouble("stitch." + i + ".x");
                double y = pl.getConfig().getDouble("stitch." + i + ".y");
                double z = pl.getConfig().getDouble("stitch." + i + ".z");
                float yaw = pl.getConfig().getInt("stitch." + i + ".yaw");
                float pitch = pl.getConfig().getInt("stitch." + i + ".pitch");
                Location seat = new Location(Bukkit.getWorld(player.getWorld().getName()), x, y, z, yaw, pitch);
                player.teleport(seat);
                if (!pl.chattimeout.containsKey(player)) {
                    player.sendMessage(ChatColor.AQUA + "------------------------------------------------");
                    player.sendMessage(ChatColor.BLUE + "Please don'commands leave your seat during the show.");
                    player.sendMessage(ChatColor.BLUE + "If you wish to leave, type /stitch leave");
                    player.sendMessage(ChatColor.AQUA + "------------------------------------------------");
                    pl.chattimeout.put(player, null);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(pl, new Runnable() {
                        public void run() {
                            pl.chattimeout.remove(player);
                        }
                    }, 60L);
                }
            }
        }
    }

    public static void joinShow(Player player) {
        boolean locked = pl.getConfig().getBoolean("show-locked");
        if (!locked && !pl.watching.containsKey(player)) {
            int amount = pl.getConfig().getInt("stitch.amount");
            for (int i = 1; i <= amount; i++) {
                if (pl.getConfig().getBoolean("stitch." + i + ".inuse") == false) {
                    double x = pl.getConfig().getDouble("stitch." + i + ".x");
                    double y = pl.getConfig().getDouble("stitch." + i + ".y");
                    double z = pl.getConfig().getDouble("stitch." + i + ".z");
                    float yaw = pl.getConfig().getInt("stitch." + i + ".yaw");
                    float pitch = pl.getConfig().getInt("stitch." + i + ".pitch");
                    Location seat = new Location(Bukkit.getWorld(player.getWorld().getName()), x, y, z, yaw, pitch);
                    pl.getConfig().set("stitch." + i + ".inuse", true);
                    pl.saveConfig();
                    player.setMetadata("stitch-seat-number", new FixedMetadataValue(pl, i));
                    pl.watching.put(player, null);
                    player.teleport(seat);
                    return;
                }
            }
            if (!pl.watching.containsKey(player)) {
                player.sendMessage(ChatColor.RED + "We're sorry, but no open seats were found at this time.");
            }
        } else {
            player.sendMessage(ChatColor.RED + "The show is locked right now. Sorry!");
        }
    }

    public static void leaveShow(Player player) {
        if (pl.watching.containsKey(player)) {
            pl.watching.remove(player);
            player.performCommand("warp sge");
            player.sendMessage(ChatColor.BLUE + "You are no longer watching Stitch's Great Escape");
        } else {
            player.sendMessage(ChatColor.RED + "You are not watching the show right now! If you belive this is an error, contact a Cast Member.");
        }
    }

    public static void lockShow(Player player) {
        boolean locked = pl.getConfig().getBoolean("show-locked");
        if (!locked) {
            pl.getConfig().set("show-locked", true);
            pl.saveConfig();
            player.sendMessage(ChatColor.BLUE + "The show has been " + ChatColor.RED + "locked");
        } else {
            pl.getConfig().set("show-locked", false);
            pl.saveConfig();
            player.sendMessage(ChatColor.BLUE + "The show has been " + ChatColor.GOLD + "un-locked");
        }
    }

}