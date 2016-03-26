package us.mcmagic.parkmanager.chairs;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import us.mcmagic.parkmanager.ParkManager;

import java.util.HashMap;
import java.util.UUID;

public class ChairManager {
    private HashMap<UUID, SitData> sitting = new HashMap<>();
    private HashMap<Block, UUID> chairs = new HashMap<>();

    public boolean sitPlayer(final Player player, Block block, Location location, boolean noMessage) {
        if (chairs.containsKey(block)) {
            player.sendMessage(ChatColor.GRAY + "That seat is occupied!");
            return false;
        }
        if (!noMessage) {
            player.sendMessage(ChatColor.GRAY + "Relaxing...");
        }
        SitData data = new SitData();
        final Entity arrow = ParkManager.chairFactory.spawnArrow(location.getBlock().getLocation().add(0.5, 0.0, 0.5));
        data.arrow = arrow;
        data.chairBlock = block;
        data.teleportLocation = player.getLocation();
        data.sittingTask = Bukkit.getScheduler().runTaskTimer(ParkManager.getInstance(), new Runnable() {
            public void run() {
                resetArrow(player);
            }
        }, 1000L, 1000L).getTaskId();
        player.teleport(location);
        arrow.setPassenger(player);
        sitting.put(player.getUniqueId(), data);
        chairs.put(block, player.getUniqueId());
        data.sitting = true;
        return true;
    }

    public boolean standPlayer(final Player player, boolean noMessage) {
        SitData data = sitting.get(player.getUniqueId());
        data.sitting = false;
        player.leaveVehicle();
        ((CraftEntity) data.arrow).getHandle().die();
        player.teleport(data.teleportLocation.clone());
        player.setSneaking(false);
        chairs.remove(data.chairBlock);
        Bukkit.getScheduler().cancelTask(data.sittingTask);
        sitting.remove(player.getUniqueId());
        if (!noMessage) player.sendMessage(ChatColor.GRAY + "Have a magical day!");
        return false;
    }

    public void resetArrow(final Player player) {
        SitData data = sitting.get(player.getUniqueId());
        data.sitting = false;
        Entity previousArrow = data.arrow;
        final Entity arrow = ParkManager.chairFactory.spawnArrow(previousArrow.getLocation());
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
        chairs.put(chair, player.getUniqueId());
        SitData data = sitting.get(player.getUniqueId());
        data.chairBlock = chair;
        data.sitting = false;
        Entity old = data.arrow;
        Location sitLocation = chair.getLocation().clone().add(0.5, 0, 0.5);
        sitLocation.setPitch(45);
        Entity arrow = ParkManager.chairFactory.spawnArrow(sitLocation);
        arrow.teleport(arrow.getLocation());
        arrow.setPassenger(player);
        data.arrow = arrow;
        ((CraftEntity) old).getHandle().die();
        data.sitting = true;
    }

    public boolean isSitting(Player player) {
        return player != null && sitting.get(player.getUniqueId()) instanceof SitData &&
                sitting.containsKey(player.getUniqueId()) && sitting.get(player.getUniqueId()).sitting;
    }

    public boolean isChairOccupied(Block block) {
        return chairs.containsKey(block);
    }

    public Player getSittingPlayer(Block chair) {
        return Bukkit.getPlayer(chairs.get(chair));
    }

    public void emptyAllData() {
        HashMap<UUID, SitData> clone = new HashMap<>(sitting);
        for (UUID uuid : clone.keySet()) {
            this.standPlayer(Bukkit.getPlayer(uuid), true);
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