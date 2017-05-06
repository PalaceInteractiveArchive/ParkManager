package network.palace.parkmanager.magicband;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.InventoryType;
import network.palace.parkmanager.utils.BandUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Created by Marc on 12/21/14
 */
public class CustomizeMenuClick {

    public static void handle(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null) {
            return;
        }
        CPlayer player = Core.getPlayerManager().getPlayer(event.getWhoClicked().getUniqueId());
        if (item.equals(BandUtil.getBackItem())) {
            ParkManager.shopManager.openMenu(player);
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        String name = ChatColor.stripColor(meta.getDisplayName());
        switch (name) {
            case "Change MagicBand Color":
                ParkManager.inventoryUtil.openInventory(player, InventoryType.CUSTOMCOLOR);
                return;
            case "Change Name Color":
                ParkManager.inventoryUtil.openInventory(player, InventoryType.CUSTOMNAME);
        }
    }
}
