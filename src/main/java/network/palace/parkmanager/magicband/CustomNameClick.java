package network.palace.parkmanager.magicband;

import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.InventoryType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import network.palace.parkmanager.utils.BandUtil;

/**
 * Created by Marc on 12/21/14
 */
public class CustomNameClick {

    public static void handle(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        if (item.equals(BandUtil.getBackItem())) {
            ParkManager.inventoryUtil.openInventory(player, InventoryType.CUSTOMIZE);
            return;
        }
        if (item.getItemMeta() == null) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta.getDisplayName() == null) {
            return;
        }
        String name = ChatColor.stripColor(meta.getDisplayName());
        ChatColor color = ParkManager.bandUtil.getBandNameColor(name.toLowerCase().replaceAll(" ", ""));
        player.closeInventory();
        ParkManager.bandUtil.setBandName(player, color);
    }
}
