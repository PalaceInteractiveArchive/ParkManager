package network.palace.parkmanager.listeners;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

/**
 * Created by Marc on 9/6/15
 */
public class FoodLevel implements Listener {

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        CPlayer player = Core.getPlayerManager().getPlayer(event.getEntity().getUniqueId());
        if (player != null && player.getRank().getRankId() >= Rank.CHARACTER.getRankId()) {
            event.setCancelled(true);
        }
    }
}
