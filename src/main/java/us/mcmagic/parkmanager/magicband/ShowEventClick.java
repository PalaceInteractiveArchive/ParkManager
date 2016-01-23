package us.mcmagic.parkmanager.magicband;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.mcmagic.parkmanager.ParkManager;
import us.mcmagic.parkmanager.handlers.InventoryType;
import us.mcmagic.parkmanager.utils.BandUtil;

/**
 * Created by Marc on 12/22/14
 */
public class ShowEventClick {

    @SuppressWarnings("deprecation")
    public static void handle(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null || item.getItemMeta() == null) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        if (item.equals(BandUtil.getBackItem())) {
            ParkManager.inventoryUtil.openInventory(player, InventoryType.MAINMENU);
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta.getDisplayName() == null) {
            return;
        }
        String name = ChatColor.stripColor(meta.getDisplayName());
        switch (name) {
            case "Show Timetable":
                ParkManager.inventoryUtil.openInventory(player, InventoryType.SHOWTIMES);
                return;
            case "Fantasmic!":
                player.performCommand("warp fantasmic");
                return;
            case "IROE":
                player.performCommand("warp iroe");
                return;
            case "Wishes!":
                player.performCommand("warp wishes");
                return;
            case "Main Street Electrical Parade":
                player.performCommand("warp mainstreet");
                return;
            case "Finding Nemo the Musical":
                player.performCommand("warp fntm");
        }
    }
}
