package us.mcmagic.magicassistant.magicband;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.utils.BandUtil;
import us.mcmagic.magicassistant.utils.InventoryType;
import us.mcmagic.magicassistant.utils.InventoryUtil;

/**
 * Created by Greenlock28 on 1/24/2015.
 */
public class HotelAndResortMenuClick {

    public static void handle(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        if (item.equals(BandUtil.getBackItem())) {
            InventoryUtil.openInventory(player, InventoryType.MAINMENU);
            return;
        }
        Material itemType = item.getType();
        switch (itemType) {
            case GLOWSTONE_DUST:
                MagicAssistant.getInstance().sendToServer(player, "Resorts");
                return;
            case BOOK:
                InventoryUtil.openInventory(player, InventoryType.MYHOTELROOMS);
                return;
            case BED:
                InventoryUtil.openInventory(player, InventoryType.HOTELS);
        }
    }
}
