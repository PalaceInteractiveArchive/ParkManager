package us.mcmagic.parkmanager.magicband;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.mcmagic.parkmanager.ParkManager;
import us.mcmagic.parkmanager.handlers.InventoryType;
import us.mcmagic.parkmanager.shop.Shop;
import us.mcmagic.parkmanager.utils.BandUtil;

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
            ParkManager.inventoryUtil.openInventory(player, InventoryType.MAINMENU);
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        String name = ChatColor.stripColor(meta.getDisplayName());
        if (event.getSlot() < 9) {
            switch (name) {
                case "FastPass Shop":
                    ParkManager.inventoryUtil.openInventory(player, InventoryType.FASTPASS);
                    break;
                case "Storage Shop":
                    ParkManager.inventoryUtil.openInventory(player, InventoryType.STORAGE);
                    break;
                case "Custom MagicBands":
                    ParkManager.inventoryUtil.openInventory(player, InventoryType.CUSTOMIZE);
                    break;
            }
            return;
        }
        Shop shop = ParkManager.shopManager.getShop(name);
        if (shop == null) {
            player.sendMessage(ChatColor.RED + "Error finding that Shop!");
            player.closeInventory();
            return;
        }
        player.performCommand("warp " + shop.getWarp());
        player.closeInventory();
    }
}
