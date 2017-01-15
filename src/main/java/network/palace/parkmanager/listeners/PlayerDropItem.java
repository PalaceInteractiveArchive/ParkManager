package network.palace.parkmanager.listeners;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class PlayerDropItem implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        CPlayer cplayer = Core.getPlayerManager().getPlayer(player.getUniqueId());
        if (cplayer.getRank().getRankId() < Rank.KNIGHT.getRankId()) {
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