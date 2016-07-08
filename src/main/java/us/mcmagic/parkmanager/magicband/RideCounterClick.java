package us.mcmagic.parkmanager.magicband;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.mcmagic.parkmanager.ParkManager;
import us.mcmagic.parkmanager.handlers.InventoryType;
import us.mcmagic.parkmanager.utils.BandUtil;

/**
 * Created by Marc on 9/1/15
 */
public class RideCounterClick {

    public static void handle(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        if (item.equals(BandUtil.getBackItem())) {
            ParkManager.inventoryUtil.openInventory(player, InventoryType.MYPROFILE);
            return;
        }
        if (item.getItemMeta() == null) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta.getDisplayName() == null) {
            return;
        }
        String inv = ChatColor.stripColor(event.getInventory().getName());
        int page = Integer.parseInt(inv.replaceAll("Ride Counter Page ", ""));
        String name = ChatColor.stripColor(meta.getDisplayName());
        if (name.equals("Next Page")) {
            ParkManager.inventoryUtil.openRideCounterPage(player, page + 1);
        }
        if (name.equals("Last Page")) {
            ParkManager.inventoryUtil.openRideCounterPage(player, page - 1);
        }
    }
}