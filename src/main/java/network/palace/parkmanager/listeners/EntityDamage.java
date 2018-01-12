package network.palace.parkmanager.listeners;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.mural.Mural;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Marc on 4/12/15
 */
public class EntityDamage implements Listener {

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (entity.getType().equals(EntityType.PLAYER)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        EntityType type = entity.getType();
        if (type.equals(EntityType.MINECART)) {
            Entity damager = event.getDamager();
            if (damager.getType().equals(EntityType.PLAYER)) {
                Player player = (Player) damager;
                if (Core.getPlayerManager().getPlayer(player.getUniqueId()).getRank().getRankId() < Rank.MOD.getRankId()) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
        if (type.equals(EntityType.ITEM_FRAME) || type.equals(EntityType.PAINTING) ||
                type.equals(EntityType.ARMOR_STAND)) {
            Entity damager = event.getDamager();
            if (damager.getType().equals(EntityType.PLAYER)) {
                CPlayer player = Core.getPlayerManager().getPlayer(damager.getUniqueId());
                if (type.equals(EntityType.ITEM_FRAME)) {
                    ItemFrame frame = (ItemFrame) entity;
                    ItemStack stack = frame.getItem();
                    if (stack != null && stack.getType().equals(Material.MAP)) {
                        int id = stack.getDurability();
                        for (Mural m : ParkManager.getMuralUtil().getMurals()) {
                            if (m.getIntList().contains(id)) {
                                event.setCancelled(true);
                                return;
                            }
                        }
                    }
                }
                if (ParkManager.getInstance().getToyStoryMania() != null) {
                    if (ParkManager.getInstance().getToyStoryMania().isInGame(player)) {
                        event.setCancelled(true);
                        return;
                    }
                }
                CPlayer cplayer = Core.getPlayerManager().getPlayer(player.getUniqueId());
                if (cplayer.getRank().getRankId() >= Rank.MOD.getRankId()) {
                    if (!BlockEdit.isInBuildMode(player.getUniqueId())) {
                        event.setCancelled(true);
                        player.sendMessage(ChatColor.RED + "You must be in Build Mode to break entities!");
                    }
                } else {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "You are not allowed to break this!");
                }
            }
        }
    }
}
