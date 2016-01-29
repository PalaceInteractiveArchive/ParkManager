package us.mcmagic.parkmanager.chairs;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.material.Stairs;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.parkmanager.ParkManager;

import java.util.ArrayList;

public class ChairListener implements Listener {
    public static final double MAX_SIT_DISTANCE = 3.0D;

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            Player player = event.getPlayer();
            Block block = event.getClickedBlock();
            if (canSit(player, block)) {
                Location sitLocation = ChairUtil.sitLocation(block, player.getLocation().getYaw());
                ParkManager.chairManager.sitPlayer(player, block, sitLocation, false);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void teleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        if (ParkManager.chairManager.isSitting(player)) {
            ParkManager.chairManager.standPlayer(player, true);
        }
    }

    @EventHandler
    public void quit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (ParkManager.chairManager.isSitting(player)) {
            ParkManager.chairManager.standPlayer(player, true);
        }
    }

    @EventHandler
    public void death(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (ParkManager.chairManager.isSitting(player)) {
            ParkManager.chairManager.standPlayer(player, true);
        }
    }

    @EventHandler
    public void vehicleExit(VehicleExitEvent event) {
        if (event.getExited() instanceof Player) {
            Player player = (Player) event.getExited();
            if (ParkManager.chairManager.isSitting(player)) {
                if (MCMagicCore.getMCMagicConfig().serverName.equals("MK")) {
                    if (ParkManager.stitch.isWatching(player.getUniqueId())) {
                        event.setCancelled(true);
                        return;
                    }
                }
                if (!ParkManager.chairManager.standPlayer(player, false)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (ParkManager.chairManager.isChairOccupied(block)) {
            Player player = ParkManager.chairManager.getSittingPlayer(block);
            ParkManager.chairManager.standPlayer(player, false);
        }
    }

    @EventHandler
    public void explode(EntityExplodeEvent event) {
        for (Block block : new ArrayList<>(event.blockList())) {
            if (ParkManager.chairManager.isChairOccupied(block)) {
                event.blockList().remove(block);
            }
        }
    }

    @EventHandler
    public void pistonExtend(final BlockPistonExtendEvent event) {
        for (final Block b : event.getBlocks()) {
            if (ParkManager.chairManager.isChairOccupied(b)) {
                Bukkit.getScheduler().runTaskLater(ParkManager.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        ParkManager.chairManager.movePlayer(ParkManager.chairManager.getSittingPlayer(b), b, b.getRelative(event.getDirection()));
                    }
                }, 3L);
                break;
            }
        }
    }

    @EventHandler
    public void pistonRetract(final BlockPistonRetractEvent event) {
        for (final Block b : event.getBlocks()) {
            if (ParkManager.chairManager.isChairOccupied(b)) {
                Bukkit.getScheduler().runTaskLater(ParkManager.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        ParkManager.chairManager.movePlayer(ParkManager.chairManager.getSittingPlayer(b), b, b.getRelative(event.getDirection()));
                    }
                }, 3L);
                break;
            }
        }
    }

    public static boolean canSit(Player player, Block block) {
        if (ChairUtil.isSuitableChair(block)) {
            if (player.getItemInHand().getType() != Material.AIR) {
                return false;
            }
            if (player.getLocation().distance(block.getLocation()) >= MAX_SIT_DISTANCE) {
                return false;
            }
            if (player.isSneaking()) {
                return false;
            }
            if (player.isInsideVehicle()) {
                return false;
            }
            if (ParkManager.chairManager.isSitting(player)) {
                return false;
            }
            if (ParkManager.chairManager.isChairOccupied(block)) {
                player.sendMessage(ChatColor.GRAY + "That seat is occupied!");
                return false;
            }
            if (block.getRelative(BlockFace.DOWN).isLiquid() || block.getRelative(BlockFace.UP).isLiquid()) {
                return false;
            }
            if (block.getRelative(BlockFace.DOWN).isEmpty() || !block.getRelative(BlockFace.UP).isEmpty()) {
                return false;
            }
            if (!block.getRelative(BlockFace.DOWN).getType().isSolid() || block.getRelative(BlockFace.UP).getType().isSolid()) {
                return false;
            }
            if (block.getState().getData() instanceof Stairs) {
                Stairs stair = (Stairs) block.getState().getData();
                if (stair.isInverted()) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}