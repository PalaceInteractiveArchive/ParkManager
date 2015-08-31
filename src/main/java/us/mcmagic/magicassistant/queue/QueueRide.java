package us.mcmagic.magicassistant.queue;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.handlers.PlayerData;
import us.mcmagic.magicassistant.utils.DateUtil;
import us.mcmagic.magicassistant.utils.SqlUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by Marc on 6/24/15
 */
public class QueueRide {
    private List<UUID> queue = new ArrayList<>();
    private List<UUID> fpqueue = new ArrayList<>();
    private final String name;
    private Location station;
    private Location spawner;
    private final int delay;
    private final int amountOfRiders;
    private String warp;
    private long lastSpawn = 0;
    private List<Location> signs = new ArrayList<>();
    private List<Location> fpsigns = new ArrayList<>();
    private boolean frozen = false;
    private boolean fpoff = false;
    private boolean paused = false;
    private List<UUID> FPQueue;
    private Integer timerID;

    public QueueRide(String name, Location station, Location spawner, int delay, int amountOfRiders, String warp) {
        this.name = name;
        this.station = station;
        this.spawner = spawner;
        this.delay = delay;
        this.amountOfRiders = amountOfRiders;
        this.warp = warp;
    }

    public String getName() {
        return name;
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
        } else {
            return queue.indexOf(uuid);
        }
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public void joinQueue(Player player) {
        MagicAssistant.queueManager.leaveAllQueues(player);
        if (amountOfRiders == 1 && queue.isEmpty() && fpqueue.isEmpty() && canSpawn()) {
            queue.add(player.getUniqueId());
            moveToStation();
            spawn();
            return;
        }
        if (queue.isEmpty() && canSpawn()) {
            player.sendMessage(ChatColor.GREEN + "The Queue is empty so we're going to wait " + ChatColor.AQUA + "" +
                    ChatColor.BOLD + "10" + ChatColor.GREEN + " seconds for anyone else to join the Queue.");
            queue.add(player.getUniqueId());
            updateSigns();
            if (timerID == null) {
                timerID = Bukkit.getScheduler().runTaskLater(MagicAssistant.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        if (!queue.isEmpty() || !fpqueue.isEmpty()) {
                            moveToStation();
                            spawn();
                        }
                        timerID = null;
                    }
                }, 200L).getTaskId();
            }
            return;
        }
        queue.add(player.getUniqueId());
        updateSigns();
        player.sendMessage(ChatColor.GREEN + "You have joined the Queue for " + ChatColor.BLUE + name + ChatColor.GREEN
                + "\nYou are in position #" + (getPosition(player.getUniqueId()) + 1));
    }

    public void joinFPQueue(Player player) {
        if (fpoff) {
            player.sendMessage(ChatColor.RED + "The FastPass line for " + name + ChatColor.RED + " is closed, right now!");
            return;
        }
        if (queue.isEmpty() && fpqueue.isEmpty() && canSpawn()) {
            player.sendMessage(ChatColor.GREEN + "The queue is empty, don't use a FastPass!");
            return;
        }
        MagicAssistant.queueManager.leaveAllQueues(player);
        fpqueue.add(player.getUniqueId());
        updateSigns();
        player.sendMessage(ChatColor.GREEN + "You have joined the " + ChatColor.AQUA + "FastPass Queue" +
                ChatColor.GREEN + " for " + ChatColor.BLUE + name + ChatColor.GREEN + "\nYou are in position #" +
                (getPosition(player.getUniqueId()) + 1) + ". You will be charged one FastPass when you board the ride.");
    }

    public boolean canSpawn() {
        return ((System.currentTimeMillis() / 1000) - delay) > lastSpawn && !paused;
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
        List<UUID> fullList = new ArrayList<>(queue);
        List<UUID> fps = getFPQueue();
        if (fps.size() > fullList.size()) {
            int place = 1;
            for (int i = 0; i < fullList.size(); i++) {
                fullList.add(place, fps.get(i));
                fps.remove(fps.get(i));
                place += 2;
            }
            for (UUID uuid : fps) {
                fullList.add(uuid);
            }
        } else {
            int place = 1;
            if (fullList.isEmpty()) {
                fullList = fps;
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
                    chargeFastpass(MagicAssistant.getPlayerData(tp.getUniqueId()));
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
                chargeFastpass(MagicAssistant.getPlayerData(tp.getUniqueId()));
            }
            tp.teleport(getStation());
            tp.sendMessage(ChatColor.GREEN + "You are now ready to board " + ChatColor.BLUE + name);
            leaveQueueSilent(tp);
            fullList.remove(tp.getUniqueId());
        }
        updateSigns();
    }

    private void chargeFastpass(final PlayerData data) {
        data.setFastpass(data.getFastpass() - 1);
        Bukkit.getScheduler().runTaskAsynchronously(MagicAssistant.getInstance(), new Runnable() {
            @Override
            public void run() {
                try (Connection connection = SqlUtil.getConnection()) {
                    PreparedStatement sql = connection.prepareStatement("UPDATE player_data SET fastpass=? WHERE uuid=?");
                    sql.setInt(1, data.getFastpass());
                    sql.setString(2, data.getUniqueId().toString());
                    sql.execute();
                    sql.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public int getQueueSize() {
        return queue.size();
    }

    public String appxWaitTime() {
        int groups = (int) Math.ceil((float) (queue.size() + fpqueue.size()) / amountOfRiders);
        double seconds = delay * (groups);
        Calendar to = new GregorianCalendar();
        to.setTimeInMillis((long) (System.currentTimeMillis() + (seconds * 1000)));
        String msg = DateUtil.formatDateDiff(new GregorianCalendar(), to);
        return msg.equalsIgnoreCase("now") ? "No Wait" : msg;
    }

    public void updateSigns() {
        for (Location loc : new ArrayList<>(signs)) {
            Block b = loc.getBlock();
            if (!MagicAssistant.rideManager.isSign(loc)) {
                continue;
            }
            Sign s = (Sign) b.getState();
            s.setLine(3, getQueueSize() + " Players");
            s.update();
        }
        for (Location loc : new ArrayList<>(fpsigns)) {
            Block b = loc.getBlock();
            if (!MagicAssistant.rideManager.isSign(loc)) {
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
        lastSpawn = System.currentTimeMillis() / 1000;
        final Block b = spawner.getBlock();
        b.setType(Material.REDSTONE_BLOCK);
        Bukkit.getScheduler().runTaskLater(MagicAssistant.getInstance(), new Runnable() {
            @Override
            public void run() {
                b.setType(Material.AIR);
            }
        }, 10L);
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
        if (timerID != null && queue.isEmpty() && fpqueue.isEmpty()) {
            Bukkit.getScheduler().cancelTask(timerID);
            timerID = null;
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
        updateSigns();
    }

    public void addSign(Location loc, boolean setFile) {
        try {
            if (setFile) {
                MagicAssistant.queueManager.addSign(this, loc);
            }
            signs.add(loc);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addFPSign(Location loc, boolean setFile) {
        try {
            if (setFile) {
                MagicAssistant.queueManager.addFPSign(this, loc);
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
        MagicAssistant.queueManager.setStation(this, loc);
    }

    public void setSpawner(Location loc) throws IOException {
        spawner = loc;
        MagicAssistant.queueManager.setSpawner(this, loc);
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
        if ((!queue.isEmpty() || !fpqueue.isEmpty()) && canSpawn() && !frozen) {
            moveToStation();
            spawn();
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
}