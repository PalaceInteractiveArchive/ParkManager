package us.mcmagic.magicassistant.queue;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.listeners.PlayerInteract;
import us.mcmagic.mcmagiccore.actionbar.ActionBarManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by Marc on 6/23/15
 */
public class QueueManager {
    private HashMap<String, QueueRide> rides = new HashMap<>();

    public QueueManager() {
        initialize();
        Bukkit.getScheduler().runTaskTimer(MagicAssistant.getInstance(), new Runnable() {
            @Override
            public void run() {
                for (QueueRide ride : getRides()) {
                    for (UUID uuid : ride.getQueue()) {
                        ActionBarManager.sendMessage(Bukkit.getPlayer(uuid), ChatColor.GREEN + "You're #" +
                                (ride.getPosition(uuid) + 1) + " in queue for " + ride.getName());
                    }
                }
            }
        }, 20L, 20L);
    }

    public void initialize() {
        for (QueueRide ride : getRides()) {
            ride.ejectQueue();
        }
        rides.clear();
        File file = new File("plugins/MagicAssistant/queues.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        for (String s : config.getStringList("queues")) {
            Location station = new Location(Bukkit.getWorlds().get(0), config.getDouble("queue." + s + ".station.x"),
                    config.getDouble("queue." + s + ".station.y"), config.getDouble("queue." + s + ".station.z"),
                    config.getInt("queue." + s + ".station.yaw"), config.getInt("queue." + s + ".station.pitch"));
            Location spawner = new Location(Bukkit.getWorlds().get(0), config.getDouble("queue." + s + ".spawner.x"),
                    config.getDouble("queue." + s + ".spawner.y"), config.getDouble("queue." + s + ".spawner.z"),
                    config.getInt("queue." + s + ".spawner.yaw"), config.getInt("queue." + s + ".spawner.pitch"));
            QueueRide ride = new QueueRide(config.getString("queue." + s + ".name"), station, null,
                    config.getInt("queue." + s + ".delay"), config.getInt("queue." + s + ".amount"),
                    config.getString("queue." + s + ".warp"));
            for (int i = 0; i < config.getInt("queue." + s + ".sign-amount"); i++) {
                ride.addSign(new Location(Bukkit.getWorlds().get(0), config.getInt("queue." + s + ".sign." + i + ".x"),
                        config.getInt("queue." + s + ".sign." + i + ".y"), config.getInt("queue." + s + ".sign." + i +
                        ".z")));
            }
        }
    }

    public void createSign(Sign s) {
        QueueRide ride = MagicAssistant.queueManager.getRide(s.getLine(1));
        if (ride != null) {
            s.setLine(0, PlayerInteract.queue);
            s.setLine(1, ChatColor.BLUE + "Join Queue For");
            s.setLine(2, ride.getName());
            s.setLine(3, ride.getQueueSize() + " Players");
            s.update();
            ride.addSign(s.getLocation());
        }
    }

    public void deleteSign(Location loc) {
        for (QueueRide ride : getRides()) {
            ride.removeSign(loc);
        }
    }

    public QueueRide getRide(String line) {
        return rides.get(line);
    }

    public QueueRide getRide2(String name) {
        for (QueueRide ride : getRides()) {
            if (ride.getName().equalsIgnoreCase(name)) {
                return ride;
            }
        }
        return null;
    }

    public void handle(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Sign s = (Sign) event.getClickedBlock().getState();
        String rideName = s.getLine(2);
        QueueRide ride = getRide2(rideName);
        if (ride == null) {
            return;
        }
        if (ride.isQueued(player.getUniqueId())) {
            ride.leaveQueue(player);
            return;
        }
        ride.joinQueue(player);
    }

    public List<QueueRide> getRides() {
        return new ArrayList<>(rides.values());
    }

    public void leaveAllQueues(Player player) {
        for (QueueRide ride : getRides()) {
            if (ride.isQueued(player.getUniqueId())) {
                ride.leaveQueue(player);
            }
        }
    }

    public void silentLeaveAllQueues(Player player) {
        for (QueueRide ride : getRides()) {
            if (ride.isQueued(player.getUniqueId())) {
                ride.leaveQueueSilent(player);
            }
        }
    }
}