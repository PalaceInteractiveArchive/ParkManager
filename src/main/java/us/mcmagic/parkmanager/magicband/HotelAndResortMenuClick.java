package us.mcmagic.parkmanager.magicband;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import us.mcmagic.parkmanager.ParkManager;
import us.mcmagic.parkmanager.handlers.InventoryType;
import us.mcmagic.parkmanager.utils.BandUtil;

/**
 * Created by Greenlock28 on 1/24/2015.
 */
public class HotelAndResortMenuClick {

    public static void handle(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null) {
            return;
        }
        if (item.getItemMeta() == null) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        if (item.equals(BandUtil.getBackItem())) {
            ParkManager.inventoryUtil.openInventory(player, InventoryType.MAINMENU);
            return;
        }
        Material itemType = item.getType();
        switch (itemType) {
            case BED:
                ParkManager.inventoryUtil.openInventory(player, InventoryType.MYHOTELROOMS);
                return;
            case EMERALD:
                ParkManager.inventoryUtil.openInventory(player, InventoryType.HOTELS);
        }
    }
}
