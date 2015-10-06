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
import us.mcmagic.magicassistant.handlers.PlayerData;
import us.mcmagic.magicassistant.listeners.PlayerInteract;
import us.mcmagic.magicassistant.queue.tasks.NextRidersTask;
import us.mcmagic.magicassistant.queue.tasks.QueueTask;
import us.mcmagic.magicassistant.utils.FileUtil;
import us.mcmagic.mcmagiccore.actionbar.ActionBarManager;
import us.mcmagic.mcmagiccore.particles.ParticleEffect;
import us.mcmagic.mcmagiccore.particles.ParticleUtil;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by Marc on 6/23/15
 */
public class QueueManager {
    private Map<String, QueueRide> rides = new TreeMap<>();
    private List<QueueTask> tasks = new ArrayList<>();

    public QueueManager() {
        initialize();
        Bukkit.getScheduler().runTaskTimer(MagicAssistant.getInstance(), new Runnable() {
            @Override
            public void run() {
                for (QueueRide ride : getRides()) {
                    ride.updateSigns();
                    List<UUID> q = ride.getQueue();
                    List<UUID> fp = ride.getFPQueue();
                    if (ride.canSpawn() && (!q.isEmpty() || !fp.isEmpty())) {
                        addTask(new NextRidersTask(ride, System.currentTimeMillis()));
                    } else {
                        ride.timeToNextRide = ride.timeToNextRide - 1;
                    }
                    for (UUID uuid : q) {
                        ActionBarManager.sendMessage(Bukkit.getPlayer(uuid), ChatColor.GREEN + "You're #" +
                                (ride.getPosition(uuid) + 1) + " in queue for " + ride.getName() + " " +
                                ChatColor.LIGHT_PURPLE + "Wait: " + ride.getWaitFor(uuid));
                    }
                    for (UUID uuid : fp) {
                        ActionBarManager.sendMessage(Bukkit.getPlayer(uuid), ChatColor.GREEN + "You're #" +
                                (ride.getPosition(uuid) + 1) + " in queue for " + ride.getName() + " " +
                                ChatColor.LIGHT_PURPLE + "Wait: " + ride.getWaitFor(uuid));
                    }
                }
            }
        }, 20L, 20L);
        Bukkit.getScheduler().runTaskTimer(MagicAssistant.getInstance(), new Runnable() {
            @Override
            public void run() {
                int i = 0;
                for (QueueTask task : new ArrayList<>(tasks)) {
                    if (System.currentTimeMillis() <= task.getTime()) {
                        i++;
                        continue;
                    }
                    task.execute();
                    tasks.remove(i);
                    i++;
                }
            }
        }, 0L, 1L);
    }

    public void addTask(QueueTask task) {
        tasks.add(task);
    }

    public void initialize() {
        for (QueueRide ride : getRides()) {
            ride.ejectQueue();
        }
        rides.clear();
        File file = FileUtil.queueFile();
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        YamlConfiguration config = FileUtil.queueYaml();
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
            boolean wait = event.getLine(2).equalsIgnoreCase("wait");
            if (fp) {
                event.setLine(0, PlayerInteract.fastpass);
                event.setLine(1, ChatColor.BLUE + "Use 1 Fastpass");
                event.setLine(2, ride.getName());
                event.setLine(3, ride.getFastpassSize() + " Players");
                ride.addFPSign(event.getBlock().getLocation(), true);
            } else if (wait) {
                event.setLine(0, PlayerInteract.wait);
                event.setLine(1, ChatColor.DARK_AQUA + "Click for the");
                event.setLine(2, ChatColor.DARK_AQUA + "wait times for");
                event.setLine(3, ride.getName());
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
        YamlConfiguration config = FileUtil.queueYaml();
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
        config.save(FileUtil.queueFile());
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
        YamlConfiguration config = FileUtil.queueYaml();
        int amount = config.getInt("fpsign-amount");
        config.set("queue." + sn + ".fpsign", null);
        List<Location> signs = ride.getFPsigns();
        for (int i = 1; i <= signs.size(); i++) {
            Location l = signs.get(i - 1);
            config.set("queue." + sn + ".fpsign." + i + ".x", l.getBlockX());
            config.set("queue." + sn + ".fpsign." + i + ".y", l.getBlockY());
            config.set("queue." + sn + ".fpsign." + i + ".z", l.getBlockZ());
        }
        config.set("queue." + sn + ".fpsign-amount", signs.size());
        config.save(FileUtil.queueFile());
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
        if (s.getLine(0).equals(PlayerInteract.wait)) {
            QueueRide ride = getRide2(s.getLine(3));
            if (ride == null) {
                return;
            }
            String wait = ride.appxWaitTime();
            particle(player, s.getLocation());
            player.sendMessage(ChatColor.GREEN + "The approximate Wait Time for " + ride.getName() + ChatColor.GREEN +
                    " is:\n" + ChatColor.AQUA + wait);
            return;
        }
        String rideName = s.getLine(2);
        QueueRide ride = getRide2(rideName);
        if (ride == null) {
            return;
        }
        if (ride.isFPQueued(player.getUniqueId())) {
            ride.leaveFPQueue(player);
            return;
        }
        if (ride.isQueued(player.getUniqueId())) {
            ride.leaveQueue(player);
            return;
        }
        if (ride.isFrozen()) {
            if (ride.getName().substring(ride.getName().length() - 1).equalsIgnoreCase("s")) {
                player.sendMessage(ride.getName() + "'s" + ChatColor.GREEN + " Queue is frozen right now, check back soon!");
            } else {
                player.sendMessage(ride.getName() + "'" + ChatColor.GREEN + " Queue is frozen right now, check back soon!");
            }
            return;
        }
        if (s.getLine(0).equals(PlayerInteract.fastpass)) {
            PlayerData data = MagicAssistant.getPlayerData(player.getUniqueId());
            if (data.getFastpass() <= 0) {
                player.sendMessage(ChatColor.RED + "You do not have any FastPasses! Purchase them in your MagicBand.");
                return;
            }
            ride.joinFPQueue(player);
            particle(player, s.getLocation());
        } else {
            ride.joinQueue(player);
            particle(player, s.getLocation());
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
        YamlConfiguration config = FileUtil.queueYaml();
        config.set("queue." + sname + ".station.x", loc.getX());
        config.set("queue." + sname + ".station.y", loc.getY());
        config.set("queue." + sname + ".station.z", loc.getZ());
        config.set("queue." + sname + ".station.yaw", loc.getYaw());
        config.set("queue." + sname + ".station.pitch", loc.getPitch());
        config.save(FileUtil.queueFile());
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
        YamlConfiguration config = FileUtil.queueYaml();
        config.set("queue." + sname + ".spawner.x", loc.getBlockX());
        config.set("queue." + sname + ".spawner.y", loc.getBlockY());
        config.set("queue." + sname + ".spawner.z", loc.getBlockZ());
        config.save(FileUtil.queueFile());
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
        YamlConfiguration config = FileUtil.queueYaml();
        int amount = config.getInt("queue." + sname + ".sign-amount");
        int num = amount + 1;
        config.set("queue." + sname + ".sign." + num + ".x", loc.getBlockX());
        config.set("queue." + sname + ".sign." + num + ".y", loc.getBlockY());
        config.set("queue." + sname + ".sign." + num + ".z", loc.getBlockZ());
        config.set("queue." + sname + ".sign-amount", num);
        config.save(FileUtil.queueFile());
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
        YamlConfiguration config = FileUtil.queueYaml();
        int amount = config.getInt("queue." + sname + ".fpsign-amount");
        int num = amount + 1;
        config.set("queue." + sname + ".fpsign." + num + ".x", loc.getBlockX());
        config.set("queue." + sname + ".fpsign." + num + ".y", loc.getBlockY());
        config.set("queue." + sname + ".fpsign." + num + ".z", loc.getBlockZ());
        config.set("queue." + sname + ".fpsign-amount", num);
        config.save(FileUtil.queueFile());
    }

    public void particle(Player player, Location loc) {
        ParticleUtil.spawnParticleForPlayer(ParticleEffect.WITCH_MAGIC, loc.add(0.5, 0.5, 0.5), 0.2f, 0.2f, 0.2f, 1, 25,
                player);
    }
}