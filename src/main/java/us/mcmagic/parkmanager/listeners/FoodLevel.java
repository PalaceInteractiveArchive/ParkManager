package us.mcmagic.parkmanager.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.permissions.Rank;
import us.mcmagic.mcmagiccore.player.User;

/**
 * Created by Marc on 9/6/15
 */
public class FoodLevel implements Listener {

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        Player player = (Player) event.getEntity();
        User user = MCMagicCore.getUser(player.getUniqueId());
        if (user.getRank().getRankId() >= Rank.CHARACTERGUEST.getRankId()) {
            event.setCancelled(true);
        }
    }
}