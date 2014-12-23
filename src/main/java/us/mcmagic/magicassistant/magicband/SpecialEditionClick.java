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
import us.mcmagic.mcmagiccore.coins.Coins;

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
        Material color = item.getType();
        if (color.equals(BandUtil.getBandMaterial(MagicAssistant.getPlayerData(player.getUniqueId()).getBandColor()))) {
            player.closeInventory();
            player.sendMessage(ChatColor.RED + "You already have that MagicBand color!");
            return;
        }
        int coins = Coins.getSqlCoins(player);
        if (coins < 500) {
            player.closeInventory();
            player.sendMessage(ChatColor.RED + "You need at least " + ChatColor.GREEN + "500 Coins" + ChatColor.RED + " to get this!");
            return;
        }
        if (!player.hasPermission("band.change")) {
            Coins.minusSqlCoins(player, 500);
        }
        player.closeInventory();
        BandUtil.setBandColor(player, color);
    }
}
