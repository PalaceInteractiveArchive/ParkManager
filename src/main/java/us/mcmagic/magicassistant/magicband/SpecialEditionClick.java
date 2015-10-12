package us.mcmagic.magicassistant.magicband;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.handlers.InventoryType;
import us.mcmagic.magicassistant.utils.BandUtil;

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
            event.setCancelled(true);
            MagicAssistant.inventoryUtil.openInventory(player, InventoryType.CUSTOMIZE);
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
            MagicAssistant.inventoryUtil.openInventory(player, InventoryType.CUSTOMCOLOR);
            return;
        }
        Material type = item.getType();
        if (type.equals(Material.REDSTONE_BLOCK)) {
            return;
        }
        if (type.equals(MagicAssistant.bandUtil.getBandMaterial(MagicAssistant.getPlayerData(
                player.getUniqueId()).getBandColor()))) {
            player.closeInventory();
            player.sendMessage(ChatColor.RED + "You already have that MagicBand color!");
            return;
        }
        player.closeInventory();
        MagicAssistant.bandUtil.setBandColor(player, type);
    }
}
