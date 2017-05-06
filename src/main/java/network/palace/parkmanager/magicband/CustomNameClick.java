package network.palace.parkmanager.magicband;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.InventoryType;
import network.palace.parkmanager.utils.BandUtil;
import org.bukkit.ChatColor;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Created by Marc on 12/21/14
 */
public class CustomNameClick {

    public static void handle(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null) {
            return;
        }
        CPlayer player = Core.getPlayerManager().getPlayer(event.getWhoClicked().getUniqueId());
        if (item.equals(BandUtil.getBackItem())) {
            ParkManager.inventoryUtil.openInventory(player, InventoryType.CUSTOMIZE);
            return;
        }
        if (item.getItemMeta() == null) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta.getDisplayName() == null) {
            return;
        }
        String name = ChatColor.stripColor(meta.getDisplayName());
        ChatColor color = ParkManager.bandUtil.getBandNameColor(name.toLowerCase().replaceAll(" ", ""));
        player.closeInventory();
        ParkManager.bandUtil.setBandName(player, color);
    }
}
