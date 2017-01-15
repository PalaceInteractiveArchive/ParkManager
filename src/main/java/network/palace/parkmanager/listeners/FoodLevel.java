package network.palace.parkmanager.listeners;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

/**
 * Created by Marc on 9/6/15
 */
public class FoodLevel implements Listener {

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        CPlayer player = Core.getPlayerManager().getPlayer((Player) event.getEntity());
        event.setCancelled(player.getRank().getRankId() >= Rank.CHARACTER.getRankId());
    }
}
