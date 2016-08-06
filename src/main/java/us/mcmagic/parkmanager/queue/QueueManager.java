package us.mcmagic.parkmanager.queue;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.actionbar.ActionBarManager;
import us.mcmagic.mcmagiccore.particles.ParticleEffect;
import us.mcmagic.mcmagiccore.particles.ParticleUtil;
import us.mcmagic.parkmanager.ParkManager;
import us.mcmagic.parkmanager.handlers.PlayerData;
import us.mcmagic.parkmanager.handlers.Ride;
import us.mcmagic.parkmanager.handlers.RideCategory;
import us.mcmagic.parkmanager.listeners.PlayerInteract;
import us.mcmagic.parkmanager.queue.tasks.NextRidersTask;
import us.mcmagic.parkmanager.queue.tasks.QueueTask;
import us.mcmagic.parkmanager.queue.tot.TowerPreShow;
import us.mcmagic.parkmanager.queue.tot.TowerStation;
import us.mcmagic.parkmanager.utils.FileUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by Marc on 6/23/15
 */
public class QueueManager {
    private List<QueueTask> tasks = new ArrayList<>();

    public QueueManager() {
        Bukkit.getScheduler().runTaskTimer(ParkManager.getInstance(), () -> {
            try {
                for (QueueRide ride : getRides()) {
                    ride.updateSigns();
                    List<UUID> q = ride.getQueue();
                    List<UUID> fp = ride.getFPQueue();
                    if (ride.canSpawn() && (!q.isEmpty() || !fp.isEmpty())) {
                        addTask(new NextRidersTask(ride, System.currentTimeMillis()));
                    } else {
                        if (ride.timeToNextRide > 0) {
                            ride.timeToNextRide -= 1;
                        }
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 20L, 20L);
        Bukkit.getScheduler().runTaskTimer(ParkManager.getInstance(), () -> {
            try {
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0L, 1L);
    }

    public void addTask(QueueTask task) {
        tasks.add(task);
    }

    public QueueRide createQueue(String s, YamlConfiguration config) {
        String name = config.getString("ride." + s + ".queue.name");
        QueueRide ride = null;
        if (MCMagicCore.getMCMagicConfig().serverName.equalsIgnoreCase("dhs")) {
            switch (s) {
                case "totpre": {
                    Location station1 = new Location(Bukkit.getWorlds().get(0), config.getDouble("ride." + s + ".queue.station1.x"),
                            config.getDouble("ride." + s + ".queue.station1.y"), config.getDouble("ride." + s + ".queue.station1.z"),
                            config.getInt("ride." + s + ".queue.station1.yaw"), config.getInt("ride." + s + ".queue.station1.pitch"));
                    Location station2 = new Location(Bukkit.getWorlds().get(0), config.getDouble("ride." + s + ".queue.station2.x"),
                            config.getDouble("ride." + s + ".queue.station2.y"), config.getDouble("ride." + s + ".queue.station2.z"),
                            config.getInt("ride." + s + ".queue.station2.yaw"), config.getInt("ride." + s + ".queue.station2.pitch"));
                    Location spawner1 = new Location(Bukkit.getWorlds().get(0), config.getInt("ride." + s + ".queue.spawner1.x"),
                            config.getInt("ride." + s + ".queue.spawner1.y"), config.getInt("ride." + s + ".queue.spawner1.z"));
                    Location spawner2 = new Location(Bukkit.getWorlds().get(0), config.getInt("ride." + s + ".queue.spawner2.x"),
                            config.getInt("ride." + s + ".queue.spawner2.y"), config.getInt("ride." + s + ".queue.spawner2.z"));
                    ride = new TowerPreShow(ChatColor.translateAlternateColorCodes('&', name), station1, station2,
                            spawner1, spawner2, config.getInt("ride." + s + ".queue.delay"), config.getInt("ride." + s + ".queue.amount"),
                            config.getString("ride." + s + ".queue.warp"));
                    break;
                }
                case "totstation": {
                    Location station1 = new Location(Bukkit.getWorlds().get(0), config.getDouble("ride." + s + ".queue.station1.x"),
                            config.getDouble("ride." + s + ".queue.station1.y"), config.getDouble("ride." + s + ".queue.station1.z"),
                            config.getInt("ride." + s + ".queue.station1.yaw"), config.getInt("ride." + s + ".queue.station1.pitch"));
                    Location station2 = new Location(Bukkit.getWorlds().get(0), config.getDouble("ride." + s + ".queue.station2.x"),
                            config.getDouble("ride." + s + ".queue.station2.y"), config.getDouble("ride." + s + ".queue.station2.z"),
                            config.getInt("ride." + s + ".queue.station2.yaw"), config.getInt("ride." + s + ".queue.station2.pitch"));
                    Location station3 = new Location(Bukkit.getWorlds().get(0), config.getDouble("ride." + s + ".queue.station3.x"),
                            config.getDouble("ride." + s + ".queue.station3.y"), config.getDouble("ride." + s + ".queue.station3.z"),
                            config.getInt("ride." + s + ".queue.station3.yaw"), config.getInt("ride." + s + ".queue.station3.pitch"));
                    Location station4 = new Location(Bukkit.getWorlds().get(0), config.getDouble("ride." + s + ".queue.station4.x"),
                            config.getDouble("ride." + s + ".queue.station4.y"), config.getDouble("ride." + s + ".queue.station4.z"),
                            config.getInt("ride." + s + ".queue.station4.yaw"), config.getInt("ride." + s + ".queue.station4.pitch"));
                    Location spawner1 = new Location(Bukkit.getWorlds().get(0), config.getInt("ride." + s + ".queue.spawner1.x"),
                            config.getInt("ride." + s + ".queue.spawner1.y"), config.getInt("ride." + s + ".queue.spawner1.z"));
                    Location spawner2 = new Location(Bukkit.getWorlds().get(0), config.getInt("ride." + s + ".queue.spawner2.x"),
                            config.getInt("ride." + s + ".queue.spawner2.y"), config.getInt("ride." + s + ".queue.spawner2.z"));
                    Location spawner3 = new Location(Bukkit.getWorlds().get(0), config.getInt("ride." + s + ".queue.spawner3.x"),
                            config.getInt("ride." + s + ".queue.spawner3.y"), config.getInt("ride." + s + ".queue.spawner3.z"));
                    Location spawner4 = new Location(Bukkit.getWorlds().get(0), config.getInt("ride." + s + ".queue.spawner4.x"),
                            config.getInt("ride." + s + ".queue.spawner4.y"), config.getInt("ride." + s + ".queue.spawner4.z"));
                    ride = new TowerStation(ChatColor.translateAlternateColorCodes('&', name), station1, station2,
                            station3, station4, spawner1, spawner2, spawner3, spawner4, config.getInt("ride." + s +
                            ".queue.delay"), config.getInt("ride." + s + ".queue.amount"), config.getString("ride."
                            + s + ".queue.warp"));
                    break;
                }
            }
        }
        if (ride == null) {
            Location station = new Location(Bukkit.getWorlds().get(0), config.getDouble("ride." + s + ".queue.station.x"),
                    config.getDouble("ride." + s + ".queue.station.y"), config.getDouble("ride." + s + ".queue.station.z"),
                    config.getInt("ride." + s + ".queue.station.yaw"), config.getInt("ride." + s + ".queue.station.pitch"));
            Location spawner = new Location(Bukkit.getWorlds().get(0), config.getInt("ride." + s + ".queue.spawner.x"),
                    config.getInt("ride." + s + ".queue.spawner.y"), config.getInt("ride." + s + ".queue.spawner.z"));
            ride = new QueueRide(ChatColor.translateAlternateColorCodes('&', name), station, spawner,
                    config.getInt("ride." + s + ".queue.delay"), config.getInt("ride." + s + ".queue..amount"),
                    config.getString("ride." + s + ".queue.warp"), RideCategory.fromString(config.getString("ride." +
                    s + ".category")), s);
        }
        for (int i = 1; i <= config.getInt("ride." + s + ".queue.sign-amount"); i++) {
            ride.addSign(new Location(Bukkit.getWorlds().get(0), config.getInt("ride." + s + ".queue.sign." + i + ".x"),
                    config.getInt("ride." + s + ".queue.sign." + i + ".y"), config.getInt("ride." + s + ".queue.sign." + i +
                    ".z")), false);
        }
        for (int i = 1; i <= config.getInt("ride." + s + ".queue.fpsign-amount"); i++) {
            ride.addFPSign(new Location(Bukkit.getWorlds().get(0), config.getInt("ride." + s + ".queue.fpsign." + i + ".x"),
                    config.getInt("ride." + s + ".queue.fpsign." + i + ".y"), config.getInt("ride." + s + ".queue.fpsign." + i +
                    ".z")), false);
        }
        return ride;
    }

    public void createSign(SignChangeEvent event) {
        QueueRide ride = ParkManager.queueManager.getRide(event.getLine(1));
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
            if (!r.getSigns().contains(loc)) {
                continue;
            }
            r.removeSign(loc);
            ride = r;
            break;
        }
        if (ride == null) {
            return;
        }
        String s = ride.getShortName();
        if (s == null) {
            return;
        }
        YamlConfiguration config = FileUtil.menuYaml();
        int amount = config.getInt("sign-amount");
        config.set("ride." + s + ".queue.sign", null);
        List<Location> signs = ride.getSigns();
        for (int i = 1; i <= signs.size(); i++) {
            Location l = signs.get(i - 1);
            config.set("ride." + s + ".queue.sign." + i + ".x", l.getBlockX());
            config.set("ride." + s + ".queue.sign." + i + ".y", l.getBlockY());
            config.set("ride." + s + ".queue.sign." + i + ".z", l.getBlockZ());
        }
        config.set("ride." + s + ".queue.sign-amount", signs.size());
        config.save(FileUtil.menuFile());
    }

    public void deleteFPSign(Location loc) throws IOException {
        QueueRide ride = null;
        for (QueueRide r : getRides()) {
            if (!r.getSigns().contains(loc)) {
                continue;
            }
            r.removeFPSign(loc);
            ride = r;
            break;
        }
        if (ride == null) {
            return;
        }
        String s = ride.getShortName();
        if (s == null) {
            return;
        }
        YamlConfiguration config = FileUtil.menuYaml();
        int amount = config.getInt("fpsign-amount");
        config.set("ride." + s + ".queue.fpsign", null);
        List<Location> signs = ride.getFPsigns();
        for (int i = 1; i <= signs.size(); i++) {
            Location l = signs.get(i - 1);
            config.set("ride." + s + ".queue.fpsign." + i + ".x", l.getBlockX());
            config.set("ride." + s + ".queue.fpsign." + i + ".y", l.getBlockY());
            config.set("ride." + s + ".queue.fpsign." + i + ".z", l.getBlockZ());
        }
        config.set("ride." + s + ".queue.fpsign-amount", signs.size());
        config.save(FileUtil.menuFile());
    }

    public QueueRide getRide(String shortName) {
        QueueRide ride = null;
        for (QueueRide r : getRides()) {
            if (r.getShortName().equalsIgnoreCase(shortName)) {
                ride = r;
                break;
            }
        }
        return ride;
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
        Ride rideObject = ParkManager.getRide(ride.getShortName());
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
                player.sendMessage(ride.getName() + "'" + ChatColor.GREEN + " Queue is frozen right now, check back soon!");
            } else {
                player.sendMessage(ride.getName() + "'s" + ChatColor.GREEN + " Queue is frozen right now, check back soon!");
            }
            return;
        }
        if (s.getLine(0).equals(PlayerInteract.fastpass)) {
            PlayerData data = ParkManager.getPlayerData(player.getUniqueId());
            if (data.getFastPassData().getPass(ride.getCategory()) <= 0) {
                player.sendMessage(ChatColor.RED + "You do not have any " + ChatColor.YELLOW +
                        ride.getCategory().getName() + " FastPasses! " + ChatColor.RED + "You can claim one per day at FastPass Kiosks.");
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
        List<Ride> rides = ParkManager.getRides();
        List<Ride> attractions = ParkManager.getAttractions();
        List<Ride> mngs = ParkManager.getMeetAndGreets();
        List<Ride> finalList = rides.stream().collect(Collectors.toList());
        finalList.addAll(attractions.stream().collect(Collectors.toList()));
        finalList.addAll(mngs.stream().collect(Collectors.toList()));
        List<QueueRide> list = finalList.stream().filter(ride -> ride.getQueue() != null).map(Ride::getQueue)
                .collect(Collectors.toList());
        return list;
    }

    public void leaveAllQueues(Player player) {
        getRides().stream().filter(ride -> ride.isQueued(player.getUniqueId())).forEach(ride -> ride.leaveQueue(player));
    }

    public void silentLeaveAllQueues(Player player) {
        for (QueueRide ride : getRides()) {
            if (ride.isQueued(player.getUniqueId())) {
                ride.leaveQueueSilent(player);
            }
            if (ride.isFPQueued(player.getUniqueId())) {
                ride.leaveQueueSilent(player);
            }
        }
    }

    public void setStation(QueueRide ride, Location loc) throws IOException {
        String s = ride.getShortName();
        if (s == null) {
            return;
        }
        YamlConfiguration config = FileUtil.menuYaml();
        config.set("ride." + s + ".queue.station.x", loc.getX());
        config.set("ride." + s + ".queue.station.y", loc.getY());
        config.set("ride." + s + ".queue.station.z", loc.getZ());
        config.set("ride." + s + ".queue.station.yaw", loc.getYaw());
        config.set("ride." + s + ".queue.station.pitch", loc.getPitch());
        config.save(FileUtil.menuFile());
    }

    public void setSpawner(QueueRide ride, Location loc) throws IOException {
        String s = ride.getShortName();
        if (s == null) {
            return;
        }
        YamlConfiguration config = FileUtil.menuYaml();
        config.set("ride." + s + ".queue.spawner.x", loc.getBlockX());
        config.set("ride." + s + ".queue.spawner.y", loc.getBlockY());
        config.set("ride." + s + ".queue.spawner.z", loc.getBlockZ());
        config.save(FileUtil.menuFile());
    }

    public void addSign(QueueRide ride, Location loc) throws IOException {
        String s = ride.getShortName();
        if (s == null) {
            return;
        }
        YamlConfiguration config = FileUtil.menuYaml();
        int amount = config.getInt("ride." + s + ".queue.sign-amount");
        int num = amount + 1;
        config.set("ride." + s + ".queue.sign." + num + ".x", loc.getBlockX());
        config.set("ride." + s + ".queue.sign." + num + ".y", loc.getBlockY());
        config.set("ride." + s + ".queue.sign." + num + ".z", loc.getBlockZ());
        config.set("ride." + s + ".queue.sign-amount", num);
        config.save(FileUtil.menuFile());
    }

    public void addFPSign(QueueRide ride, Location loc) throws IOException {
        String s = ride.getShortName();
        if (s == null) {
            return;
        }
        YamlConfiguration config = FileUtil.menuYaml();
        int amount = config.getInt("ride." + s + ".queue.fpsign-amount");
        int num = amount + 1;
        config.set("ride." + s + ".queue.fpsign." + num + ".x", loc.getBlockX());
        config.set("ride." + s + ".queue.fpsign." + num + ".y", loc.getBlockY());
        config.set("ride." + s + ".queue.fpsign." + num + ".z", loc.getBlockZ());
        config.set("ride." + s + ".queue.fpsign-amount", num);
        config.save(FileUtil.menuFile());
    }

    public void particle(Player player, Location loc) {
        ParticleUtil.spawnParticleForPlayer(ParticleEffect.WITCH_MAGIC, loc.add(0.5, 0.5, 0.5), 0.2f, 0.2f, 0.2f, 1, 25,
                player);
    }
}