package us.mcmagic.magicassistant.queue;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.utils.DateUtil;

import java.io.IOException;
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
    private boolean frozen;
    private List<UUID> FPQueue;

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

    public void joinQueue(Player player) {
        MagicAssistant.queueManager.leaveAllQueues(player);
        if (queue.isEmpty() && canSpawn()) {
            player.sendMessage(ChatColor.GREEN + "The Queue was empty, so you're able to ride now!");
            player.teleport(station);
            spawn();
            return;
        }
        queue.add(player.getUniqueId());
        updateSigns();
        player.sendMessage(ChatColor.GREEN + "You have joined the Queue for " + ChatColor.BLUE + name + ChatColor.GREEN
                + "\nYou are in position #" + (getPosition(player.getUniqueId()) + 1));
    }

    public boolean canSpawn() {
        return ((System.currentTimeMillis() / 1000) - delay) > lastSpawn;
    }

    public void leaveQueue(Player player) {
        player.sendMessage(ChatColor.GREEN + "You have left the Queue for " + ChatColor.BLUE + name);
        leaveQueueSilent(player);
    }

    public boolean isQueued(UUID uuid) {
        return queue.contains(uuid);
    }

    public void moveToStation() {
        List<UUID> fullList = new ArrayList<>(queue);
        int place = 1;
        for (UUID uuid : getFPQueue()) {
            fullList.add(place, uuid);
            place += 2;
        }
        if (fullList.size() >= amountOfRiders) {
            for (int i = 0; i < amountOfRiders; i++) {
                Player tp = Bukkit.getPlayer(fullList.get(0));
                if (tp == null) {
                    i--;
                    continue;
                }
                tp.teleport(getStation());
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
            tp.teleport(getStation());
            tp.sendMessage(ChatColor.GREEN + "You are now ready to board " + ChatColor.BLUE + name);
            leaveQueueSilent(tp);
        }
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
    }

    public void spawn() {
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

    public void leaveQueueSilent(Player player) {
        int pos = getPosition(player.getUniqueId());
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

    public int getFastpassSize() {
        return fpqueue.size();
    }

    public boolean isFPQueued(UUID uuid) {
        return fpqueue.contains(uuid);
    }

    public void joinFPQueue(Player player) {
        MagicAssistant.queueManager.leaveAllQueues(player);
        if (queue.isEmpty() && canSpawn()) {
            player.sendMessage(ChatColor.GREEN + "The Queue was empty, so you're able to ride now!");
            player.teleport(station);
            spawn();
            return;
        }
        fpqueue.add(player.getUniqueId());
        updateFPSigns();
        player.sendMessage(ChatColor.GREEN + "You have joined the " + ChatColor.AQUA + "FastPass Queue" +
                ChatColor.GREEN + " for " + ChatColor.BLUE + name + ChatColor.GREEN + "\nYou are in position #" +
                (getPosition(player.getUniqueId()) + 1));
    }

    public void updateFPSigns() {
        for (Location loc : new ArrayList<>(fpsigns)) {
            Block b = loc.getBlock();
            if (!MagicAssistant.rideManager.isSign(loc)) {
                continue;
            }
            Sign s = (Sign) b.getState();
            s.setLine(3, getQueueSize() + " Players");
            s.update();
        }
    }

    public List<UUID> getFPQueue() {
        return new ArrayList<>(fpqueue);
    }
}