package us.mcmagic.magicassistant.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.player.User;

/**
 * Created by Marc on 3/20/15
 */
public class PlayerCloseInventory implements Listener {

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        String title = event.getInventory().getTitle().toLowerCase();
        if (title.endsWith("'s magicband")) {
            MagicAssistant.bandUtil.cancelLoadPlayerData(player.getUniqueId());
            return;
        }
        if (title.endsWith("resource pack menu")) {
            User user = MCMagicCore.getUser(player.getUniqueId());
            if (user.getPreferredPack().equals("none")) {
                player.sendMessage(ChatColor.RED + "You haven't chosen any Resource Pack Setting!");
            }
            return;
        }
        if (title.endsWith("confirm")) {
            MagicAssistant.shopManager.cancelPurchase(player);
        }
    }
}