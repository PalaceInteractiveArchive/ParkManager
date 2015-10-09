package us.mcmagic.magicassistant.chairs;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import us.mcmagic.magicassistant.MagicAssistant;

import java.util.HashMap;

public class ChairManager {

    private MagicAssistant plugin;
    private HashMap<Player, SitData> sitting = new HashMap<>();
    private HashMap<Block, Player> chairs = new HashMap<>();

    public ChairManager(MagicAssistant plugin) {
        this.plugin = plugin;
    }

    public boolean sitPlayer(final Player player, Block block, Location location, boolean noMessage) {
        if(!noMessage) player.sendMessage(ChatColor.GRAY + "Relaxing...");
        SitData data = new SitData();
        final Entity arrow = plugin.chairFactory.spawnArrow(location.getBlock().getLocation().add(0.5, 0.0, 0.5));
        data.arrow = arrow;
        data.chairBlock = block;
        data.teleportLocation = player.getLocation();
        data.sittingTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            public void run() {
                resetArrow(player);
            }
        }, 1000L, 1000L);
        player.teleport(location);
        arrow.setPassenger(player);
        sitting.put(player, data);
        chairs.put(block, player);
        data.sitting = true;
        return true;
    }

    public boolean standPlayer(final Player player, boolean noMessage) {
        SitData data = sitting.get(player);
        data.sitting = false;
        player.leaveVehicle();
        ((CraftEntity) data.arrow).getHandle().die();
        player.teleport(data.teleportLocation.clone());
        player.setSneaking(false);
        chairs.remove(data.chairBlock);
        Bukkit.getScheduler().cancelTask(data.sittingTask);
        sitting.remove(player);
        if (!noMessage) player.sendMessage(ChatColor.GRAY + "Have a magical day!");
        return false;
    }

    public void resetArrow(final Player player) {
        SitData data = sitting.get(player);
        data.sitting = false;
        Entity previousArrow = data.arrow;
        final Entity arrow = plugin.chairFactory.spawnArrow(previousArrow.getLocation());
        arrow.setPassenger(player);
        data.arrow = arrow;
        previousArrow.remove();
        data.sitting = true;
    }

    public void movePlayer(final Player player, Block remove, Block chair) {
        if (!isSitting(player)) {
            return;
        }
        chairs.remove(remove);
        chairs.put(chair, player);
        SitData data = sitting.get(player);
        data.chairBlock = chair;
        data.sitting = false;
        Entity old = data.arrow;
        Location sitLocation = chair.getLocation().clone().add(0.5, 0, 0.5);
        sitLocation.setPitch(45);
        Entity arrow = plugin.chairFactory.spawnArrow(sitLocation);
        arrow.teleport(arrow.getLocation());
        arrow.setPassenger(player);
        data.arrow = arrow;
        ((CraftEntity) old).getHandle().die();
        data.sitting = true;
    }

    public boolean isSitting(Player player) {
        return sitting.containsKey(player) && sitting.get(player).sitting;
    }

    public boolean isChairOccupied(Block block) {
        return chairs.containsKey(block);
    }

    public Player getSittingPlayer(Block chair) {
        return chairs.get(chair);
    }

    public void emptyAllData() {
        HashMap<Player, SitData> clone = new HashMap<>(sitting);
        for (Player player : clone.keySet()) {
            this.standPlayer(player, true);
        }
        sitting.clear();
        chairs.clear();
    }

    private class SitData {
        private boolean sitting;
        private Entity arrow;
        private Location teleportLocation;
        private int sittingTask;
        private Block chairBlock;
    }
}
