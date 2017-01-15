package network.palace.parkmanager.listeners;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

/**
 * Created by Marc on 3/20/15
 */
public class PlayerCloseInventory implements Listener {

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        CPlayer player = Core.getPlayerManager().getPlayer((Player) event.getPlayer());
        Inventory inv = event.getInventory();
        String title = inv.getTitle().toLowerCase();
        if (title.endsWith("'s magicband")) {
            ParkManager.bandUtil.cancelLoadPlayerData(player.getUniqueId());
            return;
        }
        if (title.endsWith("resource pack menu")) {
            if (player == null) {
                return;
            }
            if (player.getPack().equals("none")) {
                player.sendMessage(ChatColor.RED + "You haven't chosen a Resource Pack setting!");
            }
            return;
        }
        if (title.endsWith("confirm")) {
            ParkManager.shopManager.cancelPurchase(player.getBukkitPlayer());
        }
    }
}