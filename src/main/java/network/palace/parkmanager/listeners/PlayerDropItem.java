package network.palace.parkmanager.listeners;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.utils.InventoryUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class PlayerDropItem implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        CPlayer player = Core.getPlayerManager().getPlayer(event.getPlayer().getUniqueId());
        if (player.getRank().getRankId() < Rank.MOD.getRankId()) {
            // Non-mods can't drop items
            event.setCancelled(true);
            return;
        }
        if (ParkManager.getBuildUtil().isInBuildMode(player.getUniqueId())) return;

        if (InventoryUtil.isReservedSlot(player.getHeldItemSlot())) event.setCancelled(true);
    }
}