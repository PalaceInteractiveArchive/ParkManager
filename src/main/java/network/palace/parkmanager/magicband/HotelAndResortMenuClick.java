package network.palace.parkmanager.magicband;

import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.InventoryType;
import network.palace.parkmanager.utils.BandUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

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
            ParkManager.getInstance().getInventoryUtil().openInventory(player, InventoryType.MAINMENU);
            return;
        }
        Material itemType = item.getType();
        switch (itemType) {
            case BED:
                ParkManager.getInstance().getInventoryUtil().openInventory(player, InventoryType.MYHOTELROOMS);
                return;
            case EMERALD:
                ParkManager.getInstance().getInventoryUtil().openInventory(player, InventoryType.HOTELS);
        }
    }
}
