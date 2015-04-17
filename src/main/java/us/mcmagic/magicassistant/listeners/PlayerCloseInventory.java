package us.mcmagic.magicassistant.listeners;

import org.bukkit.ChatColor;
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
        String title = event.getInventory().getTitle().toLowerCase();
        if (title.endsWith("'s MagicBand")) {
            MagicAssistant.bandUtil.cancelLoadPlayerData(event.getPlayer().getUniqueId());
            return;
        }
        if (title.endsWith("resource pack menu")) {
            User user = MCMagicCore.getUser(event.getPlayer().getUniqueId());
            if (user.getCurrentPack().equals("none")) {
                event.getPlayer().sendMessage(ChatColor.RED + "You haven't chosen any Resource Pack Setting!");
            }
        }
    }
}
