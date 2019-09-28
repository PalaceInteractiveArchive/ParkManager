package network.palace.parkmanager.listeners;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.core.utils.ItemUtil;
import network.palace.parkmanager.ParkManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class EntityDamage implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageEvent event) {
        boolean cancel = false;
        if (event.getEntityType().equals(EntityType.PLAYER)) {
            // Prevent all player damage
            cancel = true;
        } else if (event.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
            // Prevent damage caused by projectiles
            cancel = true;
        }
        if (cancel) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        switch (event.getEntityType()) {
            case MINECART:
            case MINECART_CHEST:
            case MINECART_COMMAND:
            case MINECART_FURNACE:
            case MINECART_HOPPER:
            case MINECART_MOB_SPAWNER:
            case MINECART_TNT: {
                if (damager.getType().equals(EntityType.PLAYER)) {
                    CPlayer player = Core.getPlayerManager().getPlayer(damager.getUniqueId());
                    if (player.getRank().getRankId() < Rank.TRAINEEBUILD.getRankId()) {
                        // Non-staff can't destroy Minecarts
                        event.setCancelled(true);
                        return;
                    }
                } else if (damager.getType().equals(EntityType.ARROW)) {
                    // Cancel all damage done by arrows
                    event.setCancelled(true);
                    return;
                }
                break;
            }
            case ITEM_FRAME: {
                if (onDamage(event.getEntity(), damager)) event.setCancelled(true);
                break;
            }
            case ARMOR_STAND: {
                if (!damager.getType().equals(EntityType.PLAYER)) return;
                CPlayer player = Core.getPlayerManager().getPlayer(damager.getUniqueId());
                if (player.getRank().getRankId() >= Rank.TRAINEEBUILD.getRankId()) {
                    if (!ParkManager.getBuildUtil().isInBuildMode(player)) {
                        // Staff can only edit entities when in Build mode
                        event.setCancelled(true);
                        player.sendMessage(ChatColor.RED + "You must be in Build Mode to break entities!");
                    }
                } else {
                    // Non-staff can't edit entities
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "You are not allowed to break this!");
                }
                break;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        switch (event.getRightClicked().getType()) {
            case ITEM_FRAME:
            case PAINTING: {
                CPlayer player = Core.getPlayerManager().getPlayer(event.getPlayer().getUniqueId());
                if (player.getRank().getRankId() < Rank.TRAINEEBUILD.getRankId()) {
                    // Non-staff can't edit entities
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "You are not allowed to edit this!");
                } else if (!ParkManager.getBuildUtil().isInBuildMode(player)) {
                    // Staff can only edit entities when in Build mode
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "You must be in Build Mode to edit entities!");
                }
                break;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onHangingBreak(HangingBreakByEntityEvent event) {
        switch (event.getEntity().getType()) {
            case ITEM_FRAME:
            case PAINTING: {
                if (onDamage(event.getEntity(), event.getRemover())) event.setCancelled(true);
                break;
            }
        }
    }

    public boolean onDamage(Entity entity, Entity damager) {
        if (!damager.getType().equals(EntityType.PLAYER)) return false;
        CPlayer player = Core.getPlayerManager().getPlayer(damager.getUniqueId());
        if (player.getRank().getRankId() >= Rank.TRAINEEBUILD.getRankId()) {
            if (!ParkManager.getBuildUtil().isInBuildMode(player)) {
                // Staff can only edit entities when in Build mode
                player.sendMessage(ChatColor.RED + "You must be in Build Mode to break entities!");
                return true;
            } else {
                if (entity.getType().equals(EntityType.ITEM_FRAME)) {
                    ItemFrame frame = (ItemFrame) entity;
                    if (frame.getItem() != null) {
                        // We need to do this for now because of https://bugs.mojang.com/browse/MC-130558
                        frame.setItem(ItemUtil.create(Material.AIR));
                    }
                    return false;
                }
            }
        } else {
            // Non-staff can't edit entities
            player.sendMessage(ChatColor.RED + "You are not allowed to break this!");
            return true;
        }
        return false;
    }
}
