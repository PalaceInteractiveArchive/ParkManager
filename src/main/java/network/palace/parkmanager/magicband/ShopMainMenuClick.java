package network.palace.parkmanager.magicband;

import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.InventoryType;
import network.palace.parkmanager.shop.Shop;
import network.palace.parkmanager.utils.BandUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
            ParkManager.getInstance().getInventoryUtil().openInventory(player, InventoryType.MAINMENU);
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
                    ParkManager.getInstance().getInventoryUtil().openInventory(player, InventoryType.FASTPASS);
                    break;
                case "Storage Shop":
                    ParkManager.getInstance().getInventoryUtil().openInventory(player, InventoryType.STORAGE);
                    break;
                case "Custom MagicBands":
                    ParkManager.getInstance().getInventoryUtil().openInventory(player, InventoryType.CUSTOMIZE);
                    break;
            }
            return;
        }
        Shop shop = ParkManager.getInstance().getShopManager().getShop(name);
        if (shop == null) {
            player.sendMessage(ChatColor.RED + "Error finding that Shop!");
            player.closeInventory();
            return;
        }
        player.performCommand("warp " + shop.getWarp());
        player.closeInventory();
    }
}
