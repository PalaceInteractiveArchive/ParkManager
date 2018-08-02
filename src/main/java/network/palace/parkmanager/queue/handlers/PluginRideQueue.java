package network.palace.parkmanager.queue.handlers;

import lombok.Getter;
import lombok.Setter;
import network.palace.core.Core;
import network.palace.core.economy.CurrencyType;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.RideCategory;
import network.palace.ridemanager.RideManager;
import network.palace.ridemanager.handlers.ride.Ride;
import network.palace.ridemanager.handlers.ride.RideType;
import network.palace.ridemanager.handlers.ride.file.FileRide;
import network.palace.ridemanager.handlers.ride.flat.AerialCarouselRide;
import network.palace.ridemanager.handlers.ride.flat.CarouselRide;
import network.palace.ridemanager.handlers.ride.flat.TeacupsRide;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Marc
 * @since 8/25/17
 */
public class PluginRideQueue extends AbstractQueueRide {
    @Getter private final String name;
    @Getter private final String shortName;
    @Getter private final Ride ride;
    @Getter private Location station;
    @Getter private final int delay;
    @Getter private final int amount;
    @Getter private final String warp;
    @Getter private final RideType type;
    @Getter private boolean frozen = false;
    @Getter @Setter private int timeToNextRide = 0;
    private List<UUID> queue = new ArrayList<>();
    private List<UUID> fpqueue = new ArrayList<>();
    @Getter private boolean loaded = false;
    private List<CPlayer> riders = new ArrayList<>();

    public PluginRideQueue(String shortName, String name, Location station, Location exit, int delay, int amount,
                           String warp, RideCategory category, RideType type, YamlConfiguration config,
                           CurrencyType currencyType, int currencyAmount, int honorAmount, int achievementId) {
        this.shortName = shortName;
        this.name = name;
        this.station = station;
        this.amount = amount;
        this.warp = warp;
        this.category = category;
        this.type = type;
        this.delay = delay + 10;
        switch (type) {
            case CAROUSEL: {
                Location center = RideManager.parseLocation(config.getConfigurationSection("ride." + shortName + ".queue.center"));
                ride = new CarouselRide(shortName, name, delay, exit, center, currencyType, currencyAmount, honorAmount, achievementId);
                flat = true;
                break;
            }
            case TEACUPS: {
                Location center = RideManager.parseLocation(config.getConfigurationSection("ride." + shortName + ".queue.center"));
                ride = new TeacupsRide(shortName, name, delay, exit, center, currencyType, currencyAmount, honorAmount, achievementId);
                flat = true;
                break;
            }
            case AERIAL_CAROUSEL: {
                Location center = RideManager.parseLocation(config.getConfigurationSection("ride." + shortName + ".queue.center"));
                double aerialRadius = config.getDouble("ride." + shortName + ".queue.aerialRadius");
                double supportRadius = config.getDouble("ride." + shortName + ".queue.supportRadius");
                boolean small = config.getBoolean("ride." + shortName + ".queue.small");
                double angle = config.getDouble("ride." + shortName + ".queue.angle");
                double height = config.getDouble("ride." + shortName + ".queue.height");
                double movein = config.getDouble("ride." + shortName + ".queue.movein");
                ride = new AerialCarouselRide(shortName, name, delay, exit, center, currencyType, currencyAmount,
                        honorAmount, achievementId, aerialRadius, supportRadius, small, angle, height, movein);
                flat = true;
                break;
            }
            case FILE: {
                String file = config.getString("ride." + shortName + ".queue.file");
                ride = new FileRide(shortName, name, amount, delay, exit, file, currencyType, currencyAmount, honorAmount, achievementId);
                break;
            }
            default:
                ride = null;
        }
        if (ride != null) RideManager.getMovementUtil().addRide(ride);
    }

    @Override
    public String approximateWaitTime() {
        return ParkManager.getInstance().getQueueManager().getWaitString(queue, fpqueue, delay, amount, timeToNextRide);
    }

    @Override
    public List<UUID> getQueue() {
        return new ArrayList<>(queue);
    }

    @Override
    public List<UUID> getFPQueue() {
        return new ArrayList<>(fpqueue);
    }

    @Override
    public int getQueueSize() {
        return queue.size();
    }

    @Override
    public int getFPQueueSize() {
        return fpqueue.size();
    }

    @Override
    public boolean canStart() {
        return (getTime() - delay) >= lastSpawn && (getQueueSize() != 0 || getFPQueueSize() != 0) && !isLoaded();
    }

    public boolean isLoadPeriodOver(boolean b) {
        if (b) {
            return (getTime() - delay - 10) >= lastSpawn;
        } else {
            return (getTime() - delay - 11) >= lastSpawn;
//            boolean loadPeriodOver = (getTime() - delay - 11) >= lastSpawn;
//            boolean ridersEmpty = riders.isEmpty();
//            return !ridersEmpty && loadPeriodOver;
        }
    }

    public int getLoadTime() {
        return 10 - (int) (getTime() - (delay + lastSpawn));
    }

    @Override
    public int getPosition(UUID uuid) {
        if (fpqueue.contains(uuid)) {
            return fpqueue.indexOf(uuid);
        } else if (queue.contains(uuid)) {
            return queue.indexOf(uuid);
        }
        return 0;
    }

    @Override
    public void updateSigns() {
        ParkManager.getInstance().getQueueManager().updateSigns(signs, getQueueSize());
        ParkManager.getInstance().getQueueManager().updateSigns(fpsigns, getFPQueueSize());
    }

    @Override
    public String getWaitFor(UUID uuid) {
        return ParkManager.getInstance().getQueueManager().getWaitStringFor(uuid, this);
    }

    public void loadPeriod() {
        if (!loaded) return;
        if (isLoadPeriodOver(true)) {
            for (CPlayer player : new ArrayList<>(riders)) {
                if (Core.getPlayerManager().getPlayer(player.getUniqueId()) == null) {
                    riders.remove(player);
                }
            }
            ride.start(riders);
            timeToNextRide = delay;
            loaded = false;
            lastSpawn = System.currentTimeMillis() / 1000;
        } else if (!isLoadPeriodOver(false)) {
            int loadTime = getLoadTime();
            for (CPlayer p : riders) {
                if (p != null)
                    p.getActionBar().show(ChatColor.GREEN + "Ride starting in " + loadTime + " seconds!");
            }
        }
    }

    public void start() {
        if (ride instanceof TeacupsRide) {
            if (((TeacupsRide) ride).isStarted()) {
                return;
            }
        } else if (ride instanceof CarouselRide) {
            if (((CarouselRide) ride).isStarted()) {
                return;
            }
        } else if (ride instanceof AerialCarouselRide) {
            if (((AerialCarouselRide) ride).isStarted()) {
                return;
            }
        }
        if (frozen) {
            return;
        }
        if (!loaded) {
            List<UUID> fullList = getQueue();
            List<UUID> fps = getFPQueue();
            if (fps.size() > fullList.size()) {
                int place = 1;
                for (int i = 0; i < fullList.size(); i++) {
                    if (place > i) {
                        break;
                    }
                    fullList.add(place, fps.remove(i));
                    place += 2;
                }
                fullList.addAll(fps);
            } else {
                int place = 1;
                if (fullList.isEmpty()) {
                    fullList = fps;
                    fps.clear();
                } else {
                    for (UUID uuid : fps) {
                        fullList.add(place, uuid);
                        place += 2;
                    }
                }
            }
            riders = new ArrayList<>();
            if (fullList.size() >= amount) {
                for (int i = 0; i < amount; i++) {
                    CPlayer tp = Core.getPlayerManager().getPlayer(fullList.get(0));
                    if (tp == null) {
                        i--;
                        continue;
                    }
                    if (fps.contains(tp.getUniqueId())) {
                        chargeFastpass(ParkManager.getInstance().getPlayerData(tp.getUniqueId()));
                        tp.sendMessage(ChatColor.GREEN + "You were charged " + ChatColor.YELLOW + "1 " +
                                getCategory().getName() + " FastPass!");
                    }
                    tp.teleport(getStation());
                    tp.sendMessage(ChatColor.GREEN + "You are now ready to board " + ChatColor.BLUE + name);
                    leaveQueueSilent(tp);
                    fullList.remove(tp.getUniqueId());
                    riders.add(tp);
                }
                updateSigns();
                loaded = true;
            } else {
                for (UUID uuid : new ArrayList<>(fullList)) {
                    CPlayer tp = Core.getPlayerManager().getPlayer(uuid);
                    if (tp == null) {
                        continue;
                    }
                    if (fps.contains(tp.getUniqueId())) {
                        chargeFastpass(ParkManager.getInstance().getPlayerData(tp.getUniqueId()));
                        tp.sendMessage(ChatColor.GREEN + "You were charged " + ChatColor.YELLOW + "1 " +
                                getCategory().getName() + " FastPass!");
                    }
                    tp.teleport(getStation());
                    tp.sendMessage(ChatColor.GREEN + "You are now ready to board " + ChatColor.BLUE + name);
                    leaveQueueSilent(tp);
                    fullList.remove(tp.getUniqueId());
                    riders.add(tp);
                }
            }
            updateSigns();
            loaded = true;
            if (ride instanceof FileRide) {
                ((FileRide) ride).spawn(10000);
            }
        }
    }

    @Override
    public int getFastpassSize() {
        return fpqueue.size();
    }

    @Override
    public void addSign(Location loc, boolean updateFile) {
        signs.add(loc);
        if (updateFile) {
            try {
                ParkManager.getInstance().getQueueManager().addSign(this, loc);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void addFPSign(Location loc, boolean updateFile) {
        fpsigns.add(loc);
        if (updateFile) {
            try {
                ParkManager.getInstance().getQueueManager().addFPSign(this, loc);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void removeSign(Location loc) {
        signs.remove(loc);
    }

    @Override
    public void removeFPSign(Location loc) {
        fpsigns.remove(loc);
    }

    @Override
    public boolean isQueued(UUID uuid) {
        return queue.contains(uuid);
    }

    @Override
    public boolean isFPQueued(UUID uuid) {
        return fpqueue.contains(uuid);
    }

    @Override
    public void leaveQueue(CPlayer player) {
        player.sendMessage(ChatColor.GREEN + "You have left the Queue for " + ChatColor.BLUE + name);
        leaveQueueSilent(player);
    }

    @Override
    public void leaveFPQueue(CPlayer player) {
        player.sendMessage(ChatColor.GREEN + "You have left the FastPass Queue for " + ChatColor.BLUE + name);
        leaveQueueSilent(player);
    }

    @Override
    public void joinQueue(CPlayer player) {
        ParkManager.getInstance().getQueueManager().leaveAllQueues(player);
        if (queue.isEmpty() && timeToNextRide <= 0 && !loaded) {
            lastSpawn = getTime() - (delay - 10);
            timeToNextRide = 10;
            queue.add(player.getUniqueId());
            return;
        }
        queue.add(player.getUniqueId());
        player.sendMessage(ChatColor.GREEN + "You have joined the Queue for " + ChatColor.BLUE + name + ChatColor.GREEN
                + "\nYou are in position #" + (getPosition(player.getUniqueId()) + 1));
    }

    @Override
    public void joinFPQueue(CPlayer player) {
        if (fpoff) {
            player.sendMessage(ChatColor.RED + "The FastPass line for " + name + ChatColor.RED + " is closed, right now!");
            return;
        }
        if (queue.isEmpty()) {
            player.sendMessage(ChatColor.GREEN + "The queue is empty, you can't use a FastPass!");
            return;
        }
        ParkManager.getInstance().getQueueManager().leaveAllQueues(player);
        fpqueue.add(player.getUniqueId());
        player.sendMessage(ChatColor.GREEN + "You have joined the " + ChatColor.AQUA + "FastPass Queue" +
                ChatColor.GREEN + " for " + ChatColor.BLUE + name + ChatColor.GREEN + "\nYou are in position #" +
                (getPosition(player.getUniqueId()) + 1) + ". You will be charged one FastPass when you board the ride.");
    }

    @Override
    public void leaveQueueSilent(CPlayer player) {
        int pos = getPosition(player.getUniqueId());
        if (fpqueue.contains(player.getUniqueId())) {
            if (pos < fpqueue.size() - 1) {
                for (UUID uuid : new ArrayList<>(fpqueue.subList(pos + 1, fpqueue.size()))) {
                    CPlayer tp = Core.getPlayerManager().getPlayer(uuid);
                    if (tp == null) {
                        fpqueue.remove(uuid);
                        continue;
                    }
                    int tppos = getPosition(uuid);
                    tp.sendMessage(ChatColor.GREEN + "You have moved in the " + ChatColor.AQUA + "FastPass " +
                            ChatColor.GREEN + "queue from #" + (tppos + 1) + " to #" + (tppos));
                }
            }
            fpqueue.remove(player.getUniqueId());
            updateSigns();
        } else {
            if (pos < queue.size() - 1) {
                for (UUID uuid : new ArrayList<>(queue.subList(pos + 1, queue.size()))) {
                    CPlayer tp = Core.getPlayerManager().getPlayer(uuid);
                    if (tp == null) {
                        queue.remove(uuid);
                        continue;
                    }
                    int tppos = getPosition(uuid);
                    tp.sendMessage(ChatColor.GREEN + "You have moved in the queue from #" + (tppos + 1) + " to #" + (tppos));
                }
            }
            queue.remove(player.getUniqueId());
            updateSigns();
        }
    }

    @Override
    public void ejectQueue() {
        for (UUID uuid : getQueue()) {
            queue.remove(uuid);
            CPlayer tp = Core.getPlayerManager().getPlayer(uuid);
            if (tp != null) {
                tp.sendMessage(ChatColor.GREEN + "You have been ejected from " + getName() +
                        ChatColor.GREEN + "'s Queue!");
            }
        }
        for (UUID uuid : getFPQueue()) {
            fpqueue.remove(uuid);
            CPlayer tp = Core.getPlayerManager().getPlayer(uuid);
            if (tp != null) {
                tp.sendMessage(ChatColor.GREEN + "You have been ejected from " + getName() +
                        ChatColor.GREEN + "'s FastPass Queue! You didn't use a FastPass yet.");
            }
        }
    }

    @Override
    public boolean toggleFastpass(CommandSender sender) {
        fpoff = !fpoff;
        if (fpoff) {
            for (UUID uuid : getFPQueue()) {
                CPlayer tp = Core.getPlayerManager().getPlayer(uuid);
                if (tp == null) {
                    continue;
                }
                tp.sendMessage(getName() + ChatColor.GREEN +
                        "'s FastPass Queue has been closed. You may stay in line until you reach the ride, " +
                        "but if you leave your place in line will be lost.");
            }
            sender.sendMessage(ChatColor.RED + "The FastPass line has been closed!");
        } else {
            sender.sendMessage(ChatColor.GREEN + "The FastPass line has been opened!");
        }
        return fpoff;
    }

    @Override
    public boolean toggleFreeze() {
        frozen = !frozen;
        return frozen;
    }

    @Override
    public void setStation(Location loc) throws IOException {
        this.station = loc;
        ParkManager.getInstance().getQueueManager().setStation(this, loc);
    }
}
