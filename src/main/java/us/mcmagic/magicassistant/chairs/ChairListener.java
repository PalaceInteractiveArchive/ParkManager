package us.mcmagic.magicassistant.chairs;

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
import us.mcmagic.magicassistant.MagicAssistant;

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
                MagicAssistant.chairManager.sitPlayer(player, block, sitLocation, false);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void teleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        if (MagicAssistant.chairManager.isSitting(player)) {
            MagicAssistant.chairManager.standPlayer(player, true);
        }
    }

    @EventHandler
    public void quit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (MagicAssistant.chairManager.isSitting(player)) {
            MagicAssistant.chairManager.standPlayer(player, true);
        }
    }

    @EventHandler
    public void death(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (MagicAssistant.chairManager.isSitting(player)) {
            MagicAssistant.chairManager.standPlayer(player, true);
        }
    }

    @EventHandler
    public void vehicleExit(VehicleExitEvent event) {
        if (event.getExited() instanceof Player) {
            Player player = (Player) event.getExited();
            if (MagicAssistant.chairManager.isSitting(player)) {
                if (!MagicAssistant.chairManager.standPlayer(player, false)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (MagicAssistant.chairManager.isChairOccupied(block)) {
            Player player = MagicAssistant.chairManager.getSittingPlayer(block);
            MagicAssistant.chairManager.standPlayer(player, false);
        }
    }

    @EventHandler
    public void explode(EntityExplodeEvent event) {
        for (Block block : new ArrayList<>(event.blockList())) {
            if (MagicAssistant.chairManager.isChairOccupied(block)) {
                event.blockList().remove(block);
            }
        }
    }

    @EventHandler
    public void pistonExtend(final BlockPistonExtendEvent event) {
        for (final Block b : event.getBlocks()) {
            if (MagicAssistant.chairManager.isChairOccupied(b)) {
                Bukkit.getScheduler().runTaskLater(MagicAssistant.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        MagicAssistant.chairManager.movePlayer(MagicAssistant.chairManager.getSittingPlayer(b), b, b.getRelative(event.getDirection()));
                    }
                }, 3L);
                break;
            }
        }
    }

    @EventHandler
    public void pistonRetract(final BlockPistonRetractEvent event) {
        for (final Block b : event.getBlocks()) {
            if (MagicAssistant.chairManager.isChairOccupied(b)) {
                Bukkit.getScheduler().runTaskLater(MagicAssistant.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        MagicAssistant.chairManager.movePlayer(MagicAssistant.chairManager.getSittingPlayer(b), b, b.getRelative(event.getDirection()));
                    }
                }, 3L);
                break;
            }
        }
    }

    private boolean canSit(Player player, Block block) {
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
            if (MagicAssistant.chairManager.isSitting(player)) {
                return false;
            }
            if (MagicAssistant.chairManager.isChairOccupied(block)) {
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