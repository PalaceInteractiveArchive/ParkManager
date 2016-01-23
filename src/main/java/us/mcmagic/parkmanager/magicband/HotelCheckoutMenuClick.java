package us.mcmagic.parkmanager.magicband;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.mcmagic.parkmanager.ParkManager;
import us.mcmagic.parkmanager.handlers.HotelRoom;
import us.mcmagic.parkmanager.handlers.InventoryType;
import us.mcmagic.parkmanager.hotels.HotelManager;
import us.mcmagic.parkmanager.utils.BandUtil;

/**
 * Created by Greenlock28 on 2/11/2015.
 */
public class HotelCheckoutMenuClick {

    public static void handle(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        if (item.equals(BandUtil.getBackItem())) {
            ParkManager.inventoryUtil.openInventory(player, InventoryType.HOTELSANDRESORTS);
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        HotelManager manager = ParkManager.hotelManager;
        String name = ChatColor.stripColor(meta.getDisplayName()).substring(13);
        HotelRoom room = manager.getRoom(name);
        manager.checkout(room, false);
    }
}
