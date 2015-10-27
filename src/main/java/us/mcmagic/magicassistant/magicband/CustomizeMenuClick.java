package us.mcmagic.magicassistant.magicband;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.handlers.InventoryType;
import us.mcmagic.magicassistant.utils.BandUtil;

/**
 * Created by Marc on 12/21/14
 */
public class CustomizeMenuClick {

    public static void handle(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        if (item.equals(BandUtil.getBackItem())) {
            MagicAssistant.shopManager.openMenu(player);
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        String name = ChatColor.stripColor(meta.getDisplayName());
        switch (name) {
            case "Change MagicBand Color":
                MagicAssistant.inventoryUtil.openInventory(player, InventoryType.CUSTOMCOLOR);
                return;
            case "Change Name Color":
                MagicAssistant.inventoryUtil.openInventory(player, InventoryType.CUSTOMNAME);
        }
    }
}
