package us.mcmagic.parkmanager.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.permissions.Rank;
import us.mcmagic.mcmagiccore.player.User;

public class PlayerDropItem implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        User user = MCMagicCore.getUser(player.getUniqueId());
        if (user.getRank().getRankId() < Rank.CASTMEMBER.getRankId()) {
            event.setCancelled(true);
            return;
        }
        if (BlockEdit.isInBuildMode(player.getUniqueId())) {
            return;
        }
        if (player.getInventory().getHeldItemSlot() >= 4) {
            event.setCancelled(true);
        }
    }
}