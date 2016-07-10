package us.mcmagic.parkmanager.queue;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import us.mcmagic.parkmanager.ParkManager;
import us.mcmagic.parkmanager.handlers.FastPassData;
import us.mcmagic.parkmanager.handlers.PlayerData;
import us.mcmagic.parkmanager.handlers.RideCategory;
import us.mcmagic.parkmanager.queue.tasks.QueueTask;
import us.mcmagic.parkmanager.queue.tasks.SpawnBlockSetTask;
import us.mcmagic.parkmanager.utils.DateUtil;
import us.mcmagic.parkmanager.utils.SqlUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by Marc on 6/24/15
 */
public class QueueRide {
    protected List<UUID> queue = new ArrayList<>();
    private List<UUID> fpqueue = new ArrayList<>();
    protected final String name;
    private Location station;
    private Location spawner;
    protected final int delay;
    protected final int amountOfRiders;
    private String warp;
    protected long lastSpawn = 0;
    private List<Location> signs = new ArrayList<>();
    private List<Location> fpsigns = new ArrayList<>();
    protected boolean frozen = false;
    private boolean fpoff = false;
    protected boolean paused = false;
    private Integer timerID;
    private final Block spawnerBlock;
    public int timeToNextRide = 0;
    private RideCategory category;
    private String shortName;

    public QueueRide(String name, Location station, Location spawner, int delay, int amountOfRiders, String warp,
                     RideCategory category, String shortName) {
        this.name = name;
        this.station = station;
        this.spawner = spawner;
        this.delay = delay;
        this.amountOfRiders = amountOfRiders;
        this.warp = warp;
        this.category = category;
        this.shortName = shortName;
        this.spawnerBlock = spawner.getBlock();
    }

    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }

    public Location getStation() {
        return station;
    }

    public Location getSpawner() {
        return spawner;
    }

    public int getDelay() {
        return delay;
    }

    public int getAmountOfRiders() {
        return amountOfRiders;
    }

    public String getWarp() {
        return warp;
    }

    public int getPosition(UUID uuid) {
        if (fpqueue.contains(uuid)) {
            return fpqueue.indexOf(uuid);
        } else if (queue.contains(uuid)) {
            return queue.indexOf(uuid);
        }
        return 0;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public void joinQueue(final Player player) {
        ParkManager.queueManager.leaveAllQueues(player);
        if (queue.isEmpty() && timeToNextRide <= 0) {
            lastSpawn = getTime() - (delay - 10);
            timeToNextRide = 10;
            player.sendMessage(ChatColor.GREEN + "The Queue is empty so we're going to wait " + ChatColor.AQUA + "" +
                    ChatColor.BOLD + "10" + ChatColor.GREEN + " seconds for anyone else to join the Queue.");
            queue.add(player.getUniqueId());
            return;
        }
        queue.add(player.getUniqueId());
        player.sendMessage(ChatColor.GREEN + "You have joined the Queue for " + ChatColor.BLUE + name + ChatColor.GREEN
                + "\nYou are in position #" + (getPosition(player.getUniqueId()) + 1));
    }

    public void joinFPQueue(Player player) {
        if (fpoff) {
            player.sendMessage(ChatColor.RED + "The FastPass line for " + name + ChatColor.RED + " is closed, right now!");
            return;
        }
        if (queue.isEmpty()) {
            player.sendMessage(ChatColor.GREEN + "The queue is empty, don't use a FastPass!");
            return;
        }
        ParkManager.queueManager.leaveAllQueues(player);
        fpqueue.add(player.getUniqueId());
        player.sendMessage(ChatColor.GREEN + "You have joined the " + ChatColor.AQUA + "FastPass Queue" +
                ChatColor.GREEN + " for " + ChatColor.BLUE + name + ChatColor.GREEN + "\nYou are in position #" +
                (getPosition(player.getUniqueId()) + 1) + ". You will be charged one FastPass when you board the ride.");
    }

    public boolean canSpawn() {
        return (getTime() - delay) >= lastSpawn && !paused;
    }

    public void leaveQueue(Player player) {
        player.sendMessage(ChatColor.GREEN + "You have left the Queue for " + ChatColor.BLUE + name);
        leaveQueueSilent(player);
    }

    public void leaveFPQueue(Player player) {
        player.sendMessage(ChatColor.GREEN + "You have left the FastPass Queue for " + ChatColor.BLUE + name);
        leaveQueueSilent(player);
    }

    public boolean isQueued(UUID uuid) {
        return queue.contains(uuid);
    }

    public void moveToStation() {
        if (frozen) {
            return;
        }
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
            for (UUID uuid : fps) {
                fullList.add(uuid);
            }
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
        if (fullList.size() >= amountOfRiders) {
            for (int i = 0; i < amountOfRiders; i++) {
                Player tp = Bukkit.getPlayer(fullList.get(0));
                if (tp == null) {
                    i--;
                    continue;
                }
                if (fps.contains(tp.getUniqueId())) {
                    chargeFastpass(ParkManager.getPlayerData(tp.getUniqueId()));
                    tp.sendMessage(ChatColor.GREEN + "You were charged " + ChatColor.YELLOW + "1 " +
                            getCategory().getName() + " FastPass!");
                }
                tp.teleport(getStation());
                tp.sendMessage(ChatColor.GREEN + "You are now ready to board " + ChatColor.BLUE + name);
                leaveQueueSilent(tp);
                fullList.remove(tp.getUniqueId());
            }
            updateSigns();
            return;
        }
        for (UUID uuid : new ArrayList<>(fullList)) {
            Player tp = Bukkit.getPlayer(uuid);
            if (tp == null) {
                continue;
            }
            if (fps.contains(tp.getUniqueId())) {
                chargeFastpass(ParkManager.getPlayerData(tp.getUniqueId()));
                tp.sendMessage(ChatColor.GREEN + "You were charged " + ChatColor.YELLOW + "1 " +
                        getCategory().getName() + " FastPass!");
            }
            tp.teleport(getStation());
            tp.sendMessage(ChatColor.GREEN + "You are now ready to board " + ChatColor.BLUE + name);
            leaveQueueSilent(tp);
            fullList.remove(tp.getUniqueId());
        }
    }

    protected void chargeFastpass(final PlayerData data) {
        final FastPassData fpdata = data.getFastPassData();
        fpdata.setPass(category, fpdata.getPass(category) - 1);
        Bukkit.getScheduler().runTaskAsynchronously(ParkManager.getInstance(), () -> {
            try (Connection connection = SqlUtil.getConnection()) {
                PreparedStatement sql = connection.prepareStatement("UPDATE player_data SET " +
                        category.getSqlName() + "=? WHERE uuid=?");
                sql.setInt(1, fpdata.getPass(category));
                sql.setString(2, data.getUniqueId().toString());
                sql.execute();
                sql.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public int getQueueSize() {
        return queue.size();
    }

    public void updateSigns() {
        for (Location loc : new ArrayList<>(signs)) {
            Block b = loc.getBlock();
            if (!ParkManager.rideManager.isSign(loc)) {
                continue;
            }
            Sign s = (Sign) b.getState();
            s.setLine(3, getQueueSize() + " Players");
            s.update();
        }
        for (Location loc : new ArrayList<>(fpsigns)) {
            Block b = loc.getBlock();
            if (!ParkManager.rideManager.isSign(loc)) {
                continue;
            }
            Sign s = (Sign) b.getState();
            s.setLine(3, getFastpassSize() + " Players");
            s.update();
        }
    }

    public void spawn() {
        if (frozen) {
            return;
        }
        lastSpawn = getTime();
        timeToNextRide = delay;
        addTask(new SpawnBlockSetTask(this, System.currentTimeMillis() + 1000, Material.REDSTONE_BLOCK));
        addTask(new SpawnBlockSetTask(this, System.currentTimeMillis() + 5000, Material.AIR));
    }

    protected void addTask(QueueTask task) {
        ParkManager.queueManager.addTask(task);
    }

    protected long getTime() {
        return System.currentTimeMillis() / 1000;
    }

    public List<Location> getSigns() {
        return new ArrayList<>(signs);
    }

    public List<Location> getFPsigns() {
        return new ArrayList<>(fpsigns);
    }

    public void leaveQueueSilent(Player player) {
        int pos = getPosition(player.getUniqueId());
        if (fpqueue.contains(player.getUniqueId())) {
            if (pos < fpqueue.size() - 1) {
                for (UUID uuid : new ArrayList<>(fpqueue.subList(pos + 1, fpqueue.size()))) {
                    Player tp = Bukkit.getPlayer(uuid);
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
                    Player tp = Bukkit.getPlayer(uuid);
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

    public List<UUID> getQueue() {
        return new ArrayList<>(queue);
    }

    public void ejectQueue() {
        for (UUID uuid : getQueue()) {
            queue.remove(uuid);
            Player tp = Bukkit.getPlayer(uuid);
            if (tp != null) {
                //tp.performCommand("warp " + warp);
                tp.sendMessage(ChatColor.GREEN + "You have been ejected from " + getName() +
                        ChatColor.GREEN + "'s Queue!");
            }
        }
        for (UUID uuid : getFPQueue()) {
            fpqueue.remove(uuid);
            Player tp = Bukkit.getPlayer(uuid);
            if (tp != null) {
                //tp.performCommand("warp " + warp);
                tp.sendMessage(ChatColor.GREEN + "You have been ejected from " + getName() +
                        ChatColor.GREEN + "'s FastPass Queue! You still have your FastPasses.");
            }
        }
    }

    public void addSign(Location loc, boolean setFile) {
        try {
            if (setFile) {
                ParkManager.queueManager.addSign(this, loc);
            }
            signs.add(loc);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addFPSign(Location loc, boolean setFile) {
        try {
            if (setFile) {
                ParkManager.queueManager.addFPSign(this, loc);
            }
            fpsigns.add(loc);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeSign(Location loc) {
        signs.remove(loc);
    }

    public void removeFPSign(Location loc) {
        fpsigns.remove(loc);
    }

    public void setStation(Location loc) throws IOException {
        station = loc;
        ParkManager.queueManager.setStation(this, loc);
    }

    public void setSpawner(Location loc) throws IOException {
        spawner = loc;
        ParkManager.queueManager.setSpawner(this, loc);
    }

    public boolean isFrozen() {
        return frozen;
    }

    public boolean isFastpassOff() {
        return fpoff;
    }

    public boolean toggleFreeze() {
        frozen = !frozen;
        for (UUID uuid : getQueue()) {
            Player tp = Bukkit.getPlayer(uuid);
            if (tp == null) {
                continue;
            }
            tp.sendMessage(getName() + ChatColor.GREEN + "'s Queue has been " + (frozen ? "frozen" : "unfrozen") + "!");
            if (frozen) {
                tp.sendMessage(ChatColor.YELLOW + "Players can no longer join the Queue. You may stay until the Queue " +
                        "is unfrozen, but if you leave your place in line will be lost.");
            }
        }
        return frozen;
    }

    public boolean toggleFastpass() {
        fpoff = !fpoff;
        if (fpoff) {
            for (UUID uuid : getFPQueue()) {
                Player tp = Bukkit.getPlayer(uuid);
                if (tp == null) {
                    continue;
                }
                tp.sendMessage(getName() + ChatColor.GREEN +
                        "'s FastPass Queue has been closed. You may stay in line until you reach the ride, " +
                        "but if you leave your place in line will be lost.");
            }
        }
        return fpoff;
    }

    public int getFastpassSize() {
        return fpqueue.size();
    }

    public boolean isFPQueued(UUID uuid) {
        return fpqueue.contains(uuid);
    }

    public List<UUID> getFPQueue() {
        return new ArrayList<>(fpqueue);
    }

    public String appxWaitTime() {
        if (queue.isEmpty() && fpqueue.isEmpty()) {
            return "No Wait";
        }
        int groups = (int) Math.ceil((float) (queue.size() + fpqueue.size()) / amountOfRiders);
        double seconds = (delay * (groups - 1)) + timeToNextRide;
        Calendar to = new GregorianCalendar();
        to.setTimeInMillis((long) (System.currentTimeMillis() + (seconds * 1000)));
        String msg = DateUtil.formatDateDiff(new GregorianCalendar(), to);
        return msg.equalsIgnoreCase("now") ? "No Wait" : msg;
    }

    public String getWaitFor(UUID uuid) {
        int groups = (int) Math.ceil((float) (queue.size() + fpqueue.size()) / amountOfRiders);
        if (groups < 2) {
            Calendar to = new GregorianCalendar();
            to.setTimeInMillis(System.currentTimeMillis() + (timeToNextRide * 1000));
            String msg = DateUtil.formatDateDiff(new GregorianCalendar(), to);
            return msg.equalsIgnoreCase("now") ? "No Wait" : msg;
        }
        int group = (int) Math.ceil((float) (getPosition(uuid) / amountOfRiders));
        double seconds = (delay * group) + timeToNextRide;
        Calendar to = new GregorianCalendar();
        to.setTimeInMillis((long) (System.currentTimeMillis() + (seconds * 1000)));
        String msg = DateUtil.formatDateDiff(new GregorianCalendar(), to);
        return msg.equalsIgnoreCase("now") ? "No Wait" : msg;
    }

    public Block getSpawnerBlock() {
        return spawnerBlock;
    }

    public RideCategory getCategory() {
        return category;
    }
}