package us.mcmagic.magicassistant.magicband;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.handlers.InventoryType;
import us.mcmagic.magicassistant.shop.Shop;
import us.mcmagic.magicassistant.utils.BandUtil;

/**
 * Created by Marc on 5/29/15
 */
public class ShopMainMenuClick {

    public static void handle(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        if (item.equals(BandUtil.getBackItem())) {
            MagicAssistant.inventoryUtil.openInventory(player, InventoryType.MAINMENU);
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        String name = meta.getDisplayName();
        Shop shop = MagicAssistant.shopManager.getShop(name);
        if (shop == null) {
            player.sendMessage(ChatColor.RED + "Error finding that Shop!");
            player.closeInventory();
            return;
        }
        player.performCommand("warp " + shop.getWarp());
        player.closeInventory();
    }
}
