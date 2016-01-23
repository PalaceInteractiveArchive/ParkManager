package us.mcmagic.parkmanager.magicband;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.mcmagic.parkmanager.ParkManager;
import us.mcmagic.parkmanager.handlers.InventoryType;
import us.mcmagic.parkmanager.utils.BandUtil;

/**
 * Created by Marc on 12/23/14
 */
public class SpecialEditionClick {

    public static void handle(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        if (item.equals(BandUtil.getBackItem())) {
            ParkManager.inventoryUtil.openInventory(player, InventoryType.CUSTOMIZE);
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        if (meta.getDisplayName() == null) {
            return;
        }
        String name = ChatColor.stripColor(meta.getDisplayName());
        if (name.equals("Last Page")) {
            ParkManager.inventoryUtil.openInventory(player, InventoryType.CUSTOMCOLOR);
            return;
        }
        Material type = item.getType();
        if (type.equals(Material.REDSTONE_BLOCK)) {
            return;
        }
        if (type.equals(ParkManager.bandUtil.getBandMaterial(ParkManager.getPlayerData(player.getUniqueId())
                .getBandColor()))) {
            player.closeInventory();
            player.sendMessage(ChatColor.RED + "You already have that MagicBand color!");
            return;
        }
        player.closeInventory();
        ParkManager.bandUtil.setBandColor(player, type);
    }
}
