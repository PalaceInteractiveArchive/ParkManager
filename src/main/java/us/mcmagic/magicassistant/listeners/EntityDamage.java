package us.mcmagic.magicassistant.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.permissions.Rank;
import us.mcmagic.mcmagiccore.player.User;

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

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        if (entity.getType().equals(EntityType.ITEM_FRAME) || entity.getType().equals(EntityType.PAINTING) ||
                entity.getType().equals(EntityType.ARMOR_STAND)) {
            event.setCancelled(true);
            Entity damager = event.getDamager();
            if (damager.getType().equals(EntityType.PLAYER)) {
                Player player = (Player) damager;
                User user = MCMagicCore.getUser(player.getUniqueId());
                if (user.getRank().getRankId() > Rank.INTERN.getRankId()) {
                    if (!player.getItemInHand().getType().equals(Material.GOLD_HOE)) {
                        player.sendMessage(ChatColor.RED + "To break this, please use a Golden Hoe");
                        return;
                    }
                    event.setCancelled(false);
                    return;
                }
                player.sendMessage(ChatColor.RED + "You are not allowed to break this!");
            }
        }
    }
}
