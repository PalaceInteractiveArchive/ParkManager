package us.mcmagic.magicassistant.magicband;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.handlers.InventoryType;
import us.mcmagic.magicassistant.utils.BandUtil;

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
            MagicAssistant.inventoryUtil.openInventory(player, InventoryType.MAINMENU);
            return;
        }
        Material itemType = item.getType();
        switch (itemType) {
            case BED:
                MagicAssistant.inventoryUtil.openInventory(player, InventoryType.MYHOTELROOMS);
                return;
            case EMERALD:
                MagicAssistant.inventoryUtil.openInventory(player, InventoryType.HOTELS);
        }
    }
}
