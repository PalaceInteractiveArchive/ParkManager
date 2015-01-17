package us.mcmagic.magicassistant.stitch;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.handlers.StitchSeat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class SGE implements Listener {
    static MagicAssistant pl;
    private static List<UUID> watching = new ArrayList<>();
    private static List<UUID> msgTimeout = new ArrayList<>();
    private static HashMap<Integer, StitchSeat> seats = new HashMap<>();
    private static boolean showLocked = false;

    public SGE(MagicAssistant instance) {
        pl = instance;
        FileConfiguration config = instance.getConfig();
        int amount = config.getInt("stitch.amount");
        for (int i = 1; i <= amount; i++) {
            double x = config.getDouble("stitch." + i + ".x");
            double y = config.getDouble("stitch." + i + ".y");
            double z = config.getDouble("stitch." + i + ".z");
            float yaw = config.getInt("stitch." + i + ".yaw");
            float pitch = config.getInt("stitch." + i + ".pitch");
            Location seat = new Location(Bukkit.getWorlds().get(0), x, y, z, yaw, pitch);
            seats.put(i, new StitchSeat(i, seat));
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        if (watching.contains(player.getUniqueId())) {
            Location from = event.getFrom();
            Location to = event.getTo();
            if ((from.getX() != to.getX()) && (from.getZ() != to.getZ()) || (from.getX() != to.getX()) || (from.getZ() != to.getZ())) {
                event.setCancelled(true);
                int i = player.getMetadata("stitch-seat-number").get(0).asInt();
                StitchSeat seat = seats.get(i);
                player.teleport(seat.getLocation());
                if (!msgTimeout.contains(player.getUniqueId())) {
                    player.sendMessage(ChatColor.AQUA + "------------------------------------------------");
                    player.sendMessage(ChatColor.BLUE + "Please don't leave your seat during the show.");
                    player.sendMessage(ChatColor.BLUE + "If you wish to leave, type /stitch leave");
                    player.sendMessage(ChatColor.AQUA + "------------------------------------------------");
                    msgTimeout.add(player.getUniqueId());
                    Bukkit.getScheduler().scheduleSyncDelayedTask(pl, new Runnable() {
                        public void run() {
                            msgTimeout.remove(player.getUniqueId());
                        }
                    }, 60L);
                }
            }
        }
    }

    public static void toggleLock(CommandSender sender) {
        showLocked = !showLocked;
        if (showLocked) {
            sender.sendMessage(ChatColor.RED + "SGE has been locked!");
        } else {
            sender.sendMessage(ChatColor.RED + "SGE has been unlocked!");
        }
    }

    public static void joinShow(Player player) {
        if (showLocked) {
            player.sendMessage(ChatColor.RED + "You can't get in right now, sorry!");
        }
        if (watching.contains(player.getUniqueId())) {
            int amount = pl.getConfig().getInt("stitch.amount");
            for (int i = 1; i <= amount; i++) {
                if (!pl.getConfig().getBoolean("stitch." + i + ".inuse")) {
                    double x = pl.getConfig().getDouble("stitch." + i + ".x");
                    double y = pl.getConfig().getDouble("stitch." + i + ".y");
                    double z = pl.getConfig().getDouble("stitch." + i + ".z");
                    float yaw = pl.getConfig().getInt("stitch." + i + ".yaw");
                    float pitch = pl.getConfig().getInt("stitch." + i + ".pitch");
                    Location seat = new Location(Bukkit.getWorld(player.getWorld().getName()), x, y, z, yaw, pitch);
                    pl.getConfig().set("stitch." + i + ".inuse", true);
                    pl.saveConfig();
                    player.setMetadata("stitch-seat-number", new FixedMetadataValue(pl, i));
                    watching.add(player.getUniqueId());
                    player.teleport(seat);
                    return;
                }
            }
            if (watching.contains(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "We're sorry, but no open seats were found at this time.");
            }
        }
    }

    public static void leaveShow(Player player) {
        if (watching.contains(player.getUniqueId())) {
            watching.remove(player.getUniqueId());
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