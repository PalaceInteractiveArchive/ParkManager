package network.palace.parkmanager.magicband;

import network.palace.core.Core;
import network.palace.core.dashboard.packets.dashboard.PacketSendToServer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.InventoryType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import network.palace.parkmanager.utils.BandUtil;

/**
 * Created by Marc on 5/10/15
 */
public class VisitHotelMenuClick {

    public static void handle(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        if (item.equals(BandUtil.getBackItem())) {
            ParkManager.inventoryUtil.openInventory(player, InventoryType.MAINMENU);
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        String name = ChatColor.stripColor(meta.getDisplayName());
        switch (name) {
            case "Yes":
                PacketSendToServer packet = new PacketSendToServer(player.getUniqueId(), "Resorts");
                Core.getDashboardConnection().send(packet);
                player.closeInventory();
                player.sendMessage(ChatColor.GREEN + "Now joining " + ChatColor.AQUA + "" + ChatColor.BOLD + "Resorts...");
                return;
            case "No":
                ParkManager.inventoryUtil.openInventory(player, InventoryType.MAINMENU);
        }
    }
}
