package us.mcmagic.magicassistant.magicband;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.utils.BandUtil;
import us.mcmagic.magicassistant.utils.InventoryType;
import us.mcmagic.magicassistant.utils.InventoryUtil;

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
            InventoryUtil.openInventory(player, InventoryType.CUSTOMIZE);
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta.getDisplayName() == null) {
            return;
        }
        String name = ChatColor.stripColor(meta.getDisplayName());
        if (name.equals("Last Page")) {
            InventoryUtil.openInventory(player, InventoryType.CUSTOMCOLOR);
            return;
        }
        Material color = BandUtil.getBandMaterial(BandColor.valueOf(name.toLowerCase()));
        if (color.equals(MagicAssistant.getPlayerData(player.getUniqueId()).getBandColor())) {
            player.closeInventory();
            player.sendMessage(ChatColor.RED + "You already have that MagicBand color!");
            return;
        }
        player.closeInventory();
        BandUtil.setBandColor(player, color);
    }
}
