package us.mcmagic.magicassistant.queue;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import us.mcmagic.magicassistant.MagicAssistant;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Marc on 6/24/15
 */
public class QueueRide {
    private List<UUID> queue = new ArrayList<>();
    private List<UUID> fpqueue = new ArrayList<>();
    private final String name;
    private final Location station;
    private final Location spawner;
    private final int delay;
    private final int amountOfRiders;
    private String warp;
    private long lastSpawn = 0;
    private List<Location> signs = new ArrayList<>();

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
        return queue.indexOf(uuid);
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
        if (queue.size() >= amountOfRiders) {
            for (int i = 0; i < amountOfRiders; i++) {
                Player tp = Bukkit.getPlayer(queue.get(0));
                if (tp == null) {
                    i--;
                    continue;
                }
                tp.teleport(getStation());
                leaveQueueSilent(tp);
            }
            updateSigns();
            return;
        }
        for (UUID uuid : new ArrayList<>(queue)) {
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

    public double appxWaitTime() {
        int groups = (int) Math.floor((float) queue.size() / amountOfRiders);
        double wait = (delay * (groups)) / (float) 60;
        NumberFormat format = NumberFormat.getInstance();
        format.setRoundingMode(RoundingMode.DOWN);
        format.setMaximumFractionDigits(2);
        double waitt = Double.parseDouble(format.format(wait));
        return waitt < 0 ? 0 : waitt;
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

    public void addSign(Location loc) {
        signs.add(loc);
    }

    public void removeSign(Location loc) {
        signs.remove(loc);
    }
}