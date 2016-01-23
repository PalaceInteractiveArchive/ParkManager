package us.mcmagic.parkmanager.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import us.mcmagic.parkmanager.ParkManager;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.player.User;

/**
 * Created by Marc on 3/20/15
 */
public class PlayerCloseInventory implements Listener {

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Inventory inv = event.getInventory();
        String title = inv.getTitle().toLowerCase();
        if (title.endsWith("'s magicband")) {
            ParkManager.bandUtil.cancelLoadPlayerData(player.getUniqueId());
            return;
        }
        if (title.endsWith("resource pack menu")) {
            User user = MCMagicCore.getUser(player.getUniqueId());
            if (user == null) {
                return;
            }
            if (user.getPreferredPack().equals("none")) {
                player.sendMessage(ChatColor.RED + "You haven't chosen any Resource Pack Setting!");
            }
            return;
        }
        if (title.endsWith("confirm")) {
            ParkManager.shopManager.cancelPurchase(player);
        }
    }
}