package us.mcmagic.magicassistant.queue;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.listeners.PlayerInteract;
import us.mcmagic.mcmagiccore.actionbar.ActionBarManager;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by Marc on 6/23/15
 */
public class QueueManager {
    private Map<String, QueueRide> rides = new TreeMap<>();

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
        File file = new File("plugins/MagicAssistant/queue.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        List<String> qs = config.getStringList("queues");
        Collections.sort(qs);
        for (String s : qs) {
            Location station = new Location(Bukkit.getWorlds().get(0), config.getDouble("queue." + s + ".station.x"),
                    config.getDouble("queue." + s + ".station.y"), config.getDouble("queue." + s + ".station.z"),
                    config.getInt("queue." + s + ".station.yaw"), config.getInt("queue." + s + ".station.pitch"));
            Location spawner = new Location(Bukkit.getWorlds().get(0), config.getInt("queue." + s + ".spawner.x"),
                    config.getInt("queue." + s + ".spawner.y"), config.getInt("queue." + s + ".spawner.z"));
            QueueRide ride = new QueueRide(ChatColor.translateAlternateColorCodes('&', config.getString("queue." + s +
                    ".name")), station, spawner, config.getInt("queue." + s + ".delay"), config.getInt("queue." + s +
                    ".amount"), config.getString("queue." + s + ".warp"));
            for (int i = 1; i <= config.getInt("queue." + s + ".sign-amount"); i++) {
                ride.addSign(new Location(Bukkit.getWorlds().get(0), config.getInt("queue." + s + ".sign." + i + ".x"),
                        config.getInt("queue." + s + ".sign." + i + ".y"), config.getInt("queue." + s + ".sign." + i +
                        ".z")), false);
            }
            for (int i = 1; i <= config.getInt("queue." + s + ".fpsign-amount"); i++) {
                ride.addFPSign(new Location(Bukkit.getWorlds().get(0), config.getInt("queue." + s + ".fpsign." + i + ".x"),
                        config.getInt("queue." + s + ".fpsign." + i + ".y"), config.getInt("queue." + s + ".fpsign." + i +
                        ".z")), false);
            }
            rides.put(s, ride);
        }
    }

    public void createSign(SignChangeEvent event) {
        QueueRide ride = MagicAssistant.queueManager.getRide(event.getLine(1));
        if (ride != null) {
            boolean fp = event.getLine(2).equalsIgnoreCase("fp");
            if (fp) {
                event.setLine(0, PlayerInteract.fastpass);
                event.setLine(1, ChatColor.BLUE + "Use 1 Fastpass");
                event.setLine(2, ride.getName());
                event.setLine(3, ride.getFastpassSize() + " Players");
                ride.addFPSign(event.getBlock().getLocation(), true);
            } else {
                event.setLine(0, PlayerInteract.queue);
                event.setLine(1, ChatColor.BLUE + "Join Queue For");
                event.setLine(2, ride.getName());
                event.setLine(3, ride.getQueueSize() + " Players");
                ride.addSign(event.getBlock().getLocation(), true);
            }
        }
    }

    public void deleteSign(Location loc) throws IOException {
        QueueRide ride = null;
        for (QueueRide r : getRides()) {
            r.removeSign(loc);
            ride = r;
        }
        if (ride == null) {
            return;
        }
        String sn = null;
        for (Map.Entry<String, QueueRide> entry : rides.entrySet()) {
            if (entry.getValue().getName().equals(ride.getName())) {
                sn = entry.getKey();
                break;
            }
        }
        if (sn == null) {
            return;
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File("plugins/MagicAssistant/queue.yml"));
        int amount = config.getInt("sign-amount");
        config.set("queue." + sn + ".sign", null);
        List<Location> signs = ride.getSigns();
        for (int i = 1; i <= signs.size(); i++) {
            Location l = signs.get(i - 1);
            config.set("queue." + sn + ".sign." + i + ".x", l.getBlockX());
            config.set("queue." + sn + ".sign." + i + ".y", l.getBlockY());
            config.set("queue." + sn + ".sign." + i + ".z", l.getBlockZ());
        }
        config.set("queue." + sn + ".sign-amount", signs.size());
        config.save(new File("plugins/MagicAssistant/queue.yml"));
    }

    public void deleteFPSign(Location loc) throws IOException {
        QueueRide ride = null;
        for (QueueRide r : getRides()) {
            r.removeSign(loc);
            ride = r;
        }
        if (ride == null) {
            return;
        }
        String sn = null;
        for (Map.Entry<String, QueueRide> entry : rides.entrySet()) {
            if (entry.getValue().getName().equals(ride.getName())) {
                sn = entry.getKey();
                break;
            }
        }
        if (sn == null) {
            return;
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File("plugins/MagicAssistant/queue.yml"));
        int amount = config.getInt("fpsign-amount");
        config.set("queue." + sn + ".sign", null);
        List<Location> signs = ride.getSigns();
        for (int i = 1; i <= signs.size(); i++) {
            Location l = signs.get(i - 1);
            config.set("queue." + sn + ".fpsign." + i + ".x", l.getBlockX());
            config.set("queue." + sn + ".fpsign." + i + ".y", l.getBlockY());
            config.set("queue." + sn + ".fpsign." + i + ".z", l.getBlockZ());
        }
        config.set("queue." + sn + ".fpsign-amount", signs.size());
        config.save(new File("plugins/MagicAssistant/queue.yml"));

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
        if (ride.isFPQueued(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You can't leave a Fastpass queue!");
            return;
        }
        if (ride.isQueued(player.getUniqueId())) {
            ride.leaveQueue(player);
            return;
        }
        if (ride.isFrozen()) {
            player.sendMessage(ride.getName() + ChatColor.GREEN +
                    "'s Queue is frozen right now, check back in a few moments!");
            return;
        }
        if (s.getLine(0).equals(PlayerInteract.fastpass)) {
            ride.joinFPQueue(player);
        } else {
            ride.joinQueue(player);
        }
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

    public void setStation(QueueRide ride, Location loc) throws IOException {
        String sname = null;
        for (Map.Entry<String, QueueRide> entry : rides.entrySet()) {
            if (entry.getValue().getName().equals(ride.getName())) {
                sname = entry.getKey();
                break;
            }
        }
        if (sname == null) {
            return;
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File("plugins/MagicAssistant/queue.yml"));
        config.set("queue." + sname + ".station.x", loc.getX());
        config.set("queue." + sname + ".station.y", loc.getY());
        config.set("queue." + sname + ".station.z", loc.getZ());
        config.set("queue." + sname + ".station.yaw", loc.getYaw());
        config.set("queue." + sname + ".station.pitch", loc.getPitch());
        config.save(new File("plugins/MagicAssistant/queue.yml"));
    }

    public void setSpawner(QueueRide ride, Location loc) throws IOException {
        String sname = null;
        for (Map.Entry<String, QueueRide> entry : rides.entrySet()) {
            if (entry.getValue().getName().equals(ride.getName())) {
                sname = entry.getKey();
                break;
            }
        }
        if (sname == null) {
            return;
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File("plugins/MagicAssistant/queue.yml"));
        config.set("queue." + sname + ".spawner.x", loc.getBlockX());
        config.set("queue." + sname + ".spawner.y", loc.getBlockY());
        config.set("queue." + sname + ".spawner.z", loc.getBlockZ());
        config.save(new File("plugins/MagicAssistant/queue.yml"));
    }

    public void addSign(QueueRide ride, Location loc) throws IOException {
        String sname = null;
        for (Map.Entry<String, QueueRide> entry : rides.entrySet()) {
            if (entry.getValue().getName().equals(ride.getName())) {
                sname = entry.getKey();
                break;
            }
        }
        if (sname == null) {
            return;
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File("plugins/MagicAssistant/queue.yml"));
        int amount = config.getInt("queue." + sname + ".sign-amount");
        int num = amount + 1;
        config.set("queue." + sname + ".sign." + num + ".x", loc.getBlockX());
        config.set("queue." + sname + ".sign." + num + ".y", loc.getBlockY());
        config.set("queue." + sname + ".sign." + num + ".z", loc.getBlockZ());
        config.set("queue." + sname + ".sign-amount", num);
        config.save(new File("plugins/MagicAssistant/queue.yml"));
    }

    public void addFPSign(QueueRide ride, Location loc) throws IOException {
        String sname = null;
        for (Map.Entry<String, QueueRide> entry : rides.entrySet()) {
            if (entry.getValue().getName().equals(ride.getName())) {
                sname = entry.getKey();
                break;
            }
        }
        if (sname == null) {
            return;
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File("plugins/MagicAssistant/queue.yml"));
        int amount = config.getInt("queue." + sname + ".fpsign-amount");
        int num = amount + 1;
        config.set("queue." + sname + ".fpsign." + num + ".x", loc.getBlockX());
        config.set("queue." + sname + ".fpsign." + num + ".y", loc.getBlockY());
        config.set("queue." + sname + ".fpsign." + num + ".z", loc.getBlockZ());
        config.set("queue." + sname + ".fpsign-amount", num);
        config.save(new File("plugins/MagicAssistant/queue.yml"));
    }
}