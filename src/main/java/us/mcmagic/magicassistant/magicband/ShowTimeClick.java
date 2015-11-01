package us.mcmagic.magicassistant.magicband;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.handlers.InventoryType;
import us.mcmagic.magicassistant.utils.BandUtil;

/**
 * Created by Marc on 6/25/15
 */
public class ShowTimeClick {


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
            MagicAssistant.inventoryUtil.openInventory(player, InventoryType.SHOWSANDEVENTS);
        }
    }
}
