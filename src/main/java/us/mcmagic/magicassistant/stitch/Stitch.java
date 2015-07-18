package us.mcmagic.magicassistant.stitch;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.handlers.StitchSeat;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Stitch implements Listener {
    private List<UUID> watching = new ArrayList<>();
    private List<UUID> msgTimeout = new ArrayList<>();
    private HashMap<Integer, StitchSeat> seats = new HashMap<>();
    private boolean showLocked = false;
    private String prefix = ChatColor.WHITE + "[" + ChatColor.BLUE + "SGE" + ChatColor.WHITE + "] ";

    public Stitch() {
        initialize();
    }

    public void initialize() {
        seats.clear();
        watching.clear();
        msgTimeout.clear();
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File("plugins/MagicAssistant/config.yml"));
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

    public int getSeatNumber(UUID uuid) {
        for (StitchSeat seat : seats.values()) {
            if (seat.getOccupant().equals(uuid)) {
                return seat.getId();
            }
        }
        return 0;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        if (watching.contains(player.getUniqueId())) {
            Location from = event.getFrom();
            Location to = event.getTo();
            if (from.getX() != to.getX() || from.getZ() != to.getZ()) {
                int i = getSeatNumber(player.getUniqueId());
                StitchSeat seat = seats.get(i);
                player.teleport(seat.getLocation());
                if (!msgTimeout.contains(player.getUniqueId())) {
                    player.sendMessage(ChatColor.RED + "------------------------------------------------");
                    player.sendMessage(ChatColor.BLUE + "Please don't leave your seat during the show.");
                    player.sendMessage(ChatColor.BLUE + "If you wish to leave, type /warp sge");
                    player.sendMessage(ChatColor.RED + "------------------------------------------------");
                    msgTimeout.add(player.getUniqueId());
                    Bukkit.getScheduler().runTaskLater(MagicAssistant.getInstance(), new Runnable() {
                        public void run() {
                            msgTimeout.remove(player.getUniqueId());
                        }
                    }, 60L);
                }
            }
        }
    }

    public void toggleLock(CommandSender sender) {
        showLocked = !showLocked;
        if (showLocked) {
            sender.sendMessage(prefix + ChatColor.RED + "SGE has been locked!");
        } else {
            sender.sendMessage(prefix + ChatColor.RED + "SGE has been unlocked!");
        }
    }

    public void ejectAll() {
        for (StitchSeat seat : seats.values()) {
            if (!seat.inUse()) {
                continue;
            }
            Bukkit.getPlayer(seat.getOccupant()).sendMessage(prefix + ChatColor.BLUE +
                    "The show has ended, we hope you enjoyed it!");
            seat.clearOccupant();
        }
    }

    public void joinShow(Player player) {
        if (showLocked) {
            player.sendMessage(prefix + ChatColor.RED + "You can't get in right now, sorry!");
            return;
        }
        for (StitchSeat seat : seats.values()) {
            if (seat.inUse()) {
                continue;
            }
            watching.add(player.getUniqueId());
            seat.setOccupant(player.getUniqueId());
            player.teleport(seat.getLocation());
            player.sendMessage(prefix + ChatColor.BLUE + "You are in your seat! The show will begin shortly.");
            return;
        }
        player.sendMessage(prefix + ChatColor.RED + "The show is full right now, sorry!");
    }

    public void leaveShow(Player player) {
        watching.remove(player.getUniqueId());
        player.sendMessage(prefix + ChatColor.RED + "You are no longer watching Stitch's Great Escape");
    }

    public boolean isWatching(UUID uuid) {
        return watching.contains(uuid);
    }
}