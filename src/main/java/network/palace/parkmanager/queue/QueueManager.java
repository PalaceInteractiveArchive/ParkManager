package network.palace.parkmanager.queue;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.PlayerData;
import network.palace.parkmanager.handlers.Ride;
import network.palace.parkmanager.handlers.RideCategory;
import network.palace.parkmanager.listeners.PlayerInteract;
import network.palace.parkmanager.queue.handlers.AbstractQueueRide;
import network.palace.parkmanager.queue.handlers.PluginRideQueue;
import network.palace.parkmanager.queue.handlers.QueueRide;
import network.palace.parkmanager.queue.tasks.NextRidersTask;
import network.palace.parkmanager.queue.tasks.QueueTask;
import network.palace.parkmanager.queue.tot.TowerPreShow;
import network.palace.parkmanager.queue.tot.TowerStation;
import network.palace.parkmanager.utils.DateUtil;
import network.palace.parkmanager.utils.FileUtil;
import network.palace.ridemanager.events.RideManagerStatusEvent;
import network.palace.ridemanager.handlers.CarouselRide;
import network.palace.ridemanager.handlers.FlatState;
import network.palace.ridemanager.handlers.RideType;
import network.palace.ridemanager.handlers.TeacupsRide;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Marc on 6/23/15
 */
public class QueueManager implements Listener {
    private List<QueueTask> tasks = new ArrayList<>();

    public QueueManager() {
        Bukkit.getScheduler().runTaskTimer(ParkManager.getInstance(), () -> {
            try {
                for (AbstractQueueRide ride : getRides()) {
                    ride.updateSigns();
                    List<UUID> q = ride.getQueue();
                    List<UUID> fp = ride.getFPQueue();
                    if (ride instanceof QueueRide) {
                        QueueRide qride = (QueueRide) ride;
                        if (qride.canStart() && (!q.isEmpty() || !fp.isEmpty())) {
                            addTask(new NextRidersTask(qride, System.currentTimeMillis()));
                        } else {
                            if (qride.timeToNextRide > 0) {
                                qride.timeToNextRide -= 1;
                            }
                        }
                    } else if (ride instanceof PluginRideQueue) {
                        PluginRideQueue pride = (PluginRideQueue) ride;
                        if (pride.isFlat()) {
                            FlatState state = FlatState.LOADING;
                            if (pride.getRide() instanceof TeacupsRide) {
                                TeacupsRide r = (TeacupsRide) pride.getRide();
                                state = r.getState();
                            } else if (pride.getRide() instanceof CarouselRide) {
                                CarouselRide r = (CarouselRide) pride.getRide();
                                state = r.getState();
                            }
                            if (state.equals(FlatState.LOADING)) {
                                if (pride.canStart()) {
                                    pride.start();
                                } else {
                                    pride.loadPeriod();
                                }
                            }
                        }
                        if (pride.getTimeToNextRide() > 0) {
                            pride.setTimeToNextRide(pride.getTimeToNextRide() - 1);
                        }
                    }
                    for (UUID uuid : q) {
                        Core.getPlayerManager().getPlayer(uuid).getActionBar().show(ChatColor.GREEN + "You're #" +
                                (ride.getPosition(uuid) + 1) + " in queue for " + ride.getName() + " " +
                                ChatColor.LIGHT_PURPLE + "Wait: " + ride.getWaitFor(uuid));
                    }
                    for (UUID uuid : fp) {
                        Core.getPlayerManager().getPlayer(uuid).getActionBar().show(ChatColor.GREEN + "You're #" +
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

    @EventHandler
    public void onRideManagerStatus(RideManagerStatusEvent event) {
        switch (event.getStatus()) {
            case STARTING:
                ParkManager.getInstance().setupRides();
                break;
            case STOPPING:
                ParkManager.getInstance().removeRides();
                break;
        }
    }

    public void addTask(QueueTask task) {
        tasks.add(task);
    }

    public AbstractQueueRide createQueue(String s, YamlConfiguration config) {
        String name = config.getString("ride." + s + ".queue.name");
        AbstractQueueRide ride = null;
        if (config.contains("ride." + s + ".queue.type")) {
            RideType type = RideType.fromString(config.getString("ride." + s + ".queue.type"));
            Location station = new Location(Bukkit.getWorlds().get(0), config.getDouble("ride." + s + ".queue.station.x"),
                    config.getDouble("ride." + s + ".queue.station.y"), config.getDouble("ride." + s + ".queue.station.z"),
                    config.getInt("ride." + s + ".queue.station.yaw"), config.getInt("ride." + s + ".queue.station.pitch"));
            Location exit = new Location(Bukkit.getWorlds().get(0), config.getDouble("ride." + s + ".queue.exit.x"),
                    config.getDouble("ride." + s + ".queue.exit.y"), config.getDouble("ride." + s + ".queue.exit.z"),
                    config.getInt("ride." + s + ".queue.exit.yaw"), config.getInt("ride." + s + ".queue.exit.pitch"));
            int delay = config.getInt("ride." + s + ".queue.delay");
            int amount = config.getInt("ride." + s + ".queue.amount");
            String warp = config.getString("ride." + s + ".warp");
            RideCategory category = RideCategory.fromString(config.getString("ride." + s + ".category"));
            ride = new PluginRideQueue(s, ChatColor.translateAlternateColorCodes('&', name), station, exit,
                    delay, amount, warp, category, type, config);
        } else {
            if (Core.getInstanceName().equalsIgnoreCase("dhs")) {
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
        AbstractQueueRide ride = getRide(event.getLine(1));
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
        AbstractQueueRide ride = null;
        for (AbstractQueueRide r : getRides()) {
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
        AbstractQueueRide ride = null;
        for (AbstractQueueRide r : getRides()) {
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
        List<Location> signs = ride.getFpsigns();
        for (int i = 1; i <= signs.size(); i++) {
            Location l = signs.get(i - 1);
            config.set("ride." + s + ".queue.fpsign." + i + ".x", l.getBlockX());
            config.set("ride." + s + ".queue.fpsign." + i + ".y", l.getBlockY());
            config.set("ride." + s + ".queue.fpsign." + i + ".z", l.getBlockZ());
        }
        config.set("ride." + s + ".queue.fpsign-amount", signs.size());
        config.save(FileUtil.menuFile());
    }

    public AbstractQueueRide getRide(String shortName) {
        AbstractQueueRide ride = null;
        for (AbstractQueueRide r : getRides()) {
            if (r.getShortName().equalsIgnoreCase(shortName)) {
                ride = r;
                break;
            }
        }
        return ride;
    }

    public AbstractQueueRide getRide2(String name) {
        for (AbstractQueueRide ride : getRides()) {
            if (ride.getName().equalsIgnoreCase(name)) {
                return ride;
            }
        }
        return null;
    }

    public void handle(PlayerInteractEvent event) {
        CPlayer player = Core.getPlayerManager().getPlayer(event.getPlayer());
        Sign s = (Sign) event.getClickedBlock().getState();
        if (s.getLine(0).equals(PlayerInteract.wait)) {
            AbstractQueueRide ride = getRide2(s.getLine(3));
            if (ride == null) {
                return;
            }
            String wait = ride.approximateWaitTime();
            particle(player, s.getLocation());
            player.sendMessage(ChatColor.GREEN + "The approximate Wait Time for " + ride.getName() + ChatColor.GREEN +
                    " is:\n" + ChatColor.AQUA + wait);
            return;
        }
        String rideName = s.getLine(2);
        AbstractQueueRide ride = getRide2(rideName);
        Ride rideObject = ParkManager.getInstance().getRide(ride.getShortName());
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
            PlayerData data = ParkManager.getInstance().getPlayerData(player.getUniqueId());
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

    public List<AbstractQueueRide> getRides() {
        ParkManager parkManager = ParkManager.getInstance();
        List<Ride> rides = parkManager.getRides();
        List<Ride> attractions = parkManager.getAttractions();
        List<Ride> mngs = parkManager.getMeetAndGreets();
        List<Ride> finalList = rides.stream().collect(Collectors.toList());
        finalList.addAll(attractions.stream().collect(Collectors.toList()));
        finalList.addAll(mngs.stream().collect(Collectors.toList()));
        return finalList.stream().filter(ride -> ride.getQueue() != null).map(Ride::getQueue)
                .collect(Collectors.toList());
    }

    public void leaveAllQueues(CPlayer player) {
        getRides().stream().filter(ride -> ride.isQueued(player.getUniqueId())).forEach(ride -> ride.leaveQueue(player));
    }

    public void silentLeaveAllQueues(CPlayer player) {
        for (AbstractQueueRide ride : getRides()) {
            if (ride.isQueued(player.getUniqueId())) {
                ride.leaveQueueSilent(player);
            }
            if (ride.isFPQueued(player.getUniqueId())) {
                ride.leaveQueueSilent(player);
            }
        }
    }

    public void setStation(AbstractQueueRide ride, Location loc) throws IOException {
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

    public void addSign(AbstractQueueRide ride, Location loc) throws IOException {
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

    public void addFPSign(AbstractQueueRide ride, Location loc) throws IOException {
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

    public void particle(CPlayer player, Location loc) {
        player.getParticles().send(loc.add(0.5, 0.5, 0.5), Particle.SPELL_WITCH, 25, 0.2f,
                0.2f, 0.2f, 1);
    }

    public String getWaitString(List<UUID> queue, List<UUID> fpqueue, int delay, int amount, int timeToNextRide) {
        if (queue.isEmpty() && fpqueue.isEmpty()) {
            return "No Wait";
        }
        int groups = (int) Math.ceil((float) (queue.size() + fpqueue.size()) / amount);
        double seconds = (delay * (groups - 1)) + timeToNextRide;
        Calendar to = new GregorianCalendar();
        to.setTimeInMillis((long) (System.currentTimeMillis() + (seconds * 1000)));
        String msg = DateUtil.formatDateDiff(new GregorianCalendar(), to);
        return msg.equalsIgnoreCase("now") ? "No Wait" : msg;
    }

    public String getWaitStringFor(UUID uuid, AbstractQueueRide ride) {
        int groups = (int) Math.ceil((float) (ride.getQueueSize() + ride.getFPQueueSize()) / ride.getAmount());
        if (groups < 2) {
            Calendar to = new GregorianCalendar();
            to.setTimeInMillis(System.currentTimeMillis() + (ride.getTimeToNextRide() * 1000));
            String msg = DateUtil.formatDateDiff(new GregorianCalendar(), to);
            return msg.equalsIgnoreCase("now") ? "No Wait" : msg;
        }
        int group = (int) Math.ceil((float) (ride.getPosition(uuid) / ride.getAmount()));
        double seconds = (ride.getDelay() * group) + ride.getTimeToNextRide();
        Calendar to = new GregorianCalendar();
        to.setTimeInMillis((long) (System.currentTimeMillis() + (seconds * 1000)));
        String msg = DateUtil.formatDateDiff(new GregorianCalendar(), to);
        return msg.equalsIgnoreCase("now") ? "No Wait" : msg;
    }

    public void updateSigns(List<Location> signs, int amount) {
        for (Location loc : new ArrayList<>(signs)) {
            Block b = loc.getBlock();
            if (!ParkManager.getInstance().isSign(loc)) {
                continue;
            }
            Sign s = (Sign) b.getState();
            s.setLine(3, amount + " Players");
            s.update();
        }
    }
}