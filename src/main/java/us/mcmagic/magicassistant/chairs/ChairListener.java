package us.mcmagic.magicassistant.chairs;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
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

    private ChairManager manager = MagicAssistant.chairManager;

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player player = event.getPlayer();
            Block block = event.getClickedBlock();
            if (canSit(player, block)) {
                Location sitLocation = ChairUtil.sitLocation(block, player.getLocation().getYaw());
                if (manager.sitPlayer(player, block, sitLocation, false)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void teleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        if (manager.isSitting(player)) {
            manager.standPlayer(player, true);
        }
    }

    @EventHandler
    public void quit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (manager.isSitting(player)) {
            manager.standPlayer(player, true);
        }
    }

    @EventHandler
    public void death(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (manager.isSitting(player)) {
            manager.standPlayer(player, true);
        }
    }

    @EventHandler
    public void vehicleExit(VehicleExitEvent event) {
        if (event.getExited() instanceof Player) {
            Player player = (Player) event.getExited();
            if (manager.isSitting(player)) {
                if (!manager.standPlayer(player, false)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (manager.isChairOccupied(block)) {
            Player player = manager.getSittingPlayer(block);
            manager.standPlayer(player, false);
        }
    }

    @EventHandler
    public void explode(EntityExplodeEvent event) {
        for (Block block : new ArrayList<Block>(event.blockList())) {
            if (manager.isChairOccupied(block)) {
                event.blockList().remove(block);
            }
        }
    }

    //FIXME
    @EventHandler
    public void pistonExtend(BlockPistonExtendEvent event) {
        Block updatedChair = null;
        Block oldChair = null;
        for (Block block : event.getBlocks()) {
            if (manager.isChairOccupied(block)) {
                oldChair = block;
                updatedChair = block.getRelative(event.getDirection());
                break;
            }
        }
        manager.updateChair(oldChair, updatedChair);
    }

    //FIXME
    @EventHandler
    public void pistonRetract(BlockPistonRetractEvent event) {
        Block updatedChair = null;
        Block oldChair = null;
        for (Block block : event.getBlocks()) {
            if (manager.isChairOccupied(block)) {
                oldChair = block;
                updatedChair = block.getRelative(event.getDirection());
                break;
            }
        }
        manager.updateChair(oldChair, updatedChair);
    }

    private boolean canSit(Player player, Block block) {
        if (manager.isSitting(player)) {
            return false;
        }
        if (player.getItemInHand().getType() != Material.AIR) {
            return false;
        }
        if (player.isSneaking()) {
            return false;
        }
        if (player.isInsideVehicle()) {
            return false;
        }
        if (manager.isChairOccupied(block)) {
            return false;
        }
        if (ChairUtil.isSuitableChair(block)) {
            Stairs stair = (Stairs) block.getState().getData();
            if (ChairUtil.isSuitableChair(block.getRelative(BlockFace.UP))) {
                return false;
            }
            if (block.getRelative(BlockFace.DOWN).isLiquid()) {
                return false;
            }
            if (block.getRelative(BlockFace.DOWN).isEmpty()) {
                return false;
            }
            if (!block.getRelative(BlockFace.DOWN).getType().isSolid()) {
                return false;
            }
            if (stair.isInverted()) {
                return false;
            }
            return true;
        }
        return false;
    }
}
