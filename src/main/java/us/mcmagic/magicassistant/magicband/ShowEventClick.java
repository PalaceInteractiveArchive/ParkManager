package us.mcmagic.magicassistant.magicband;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.utils.BandUtil;
import us.mcmagic.magicassistant.handlers.InventoryType;

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
            MagicAssistant.inventoryUtil.openInventory(player, InventoryType.MAINMENU);
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta.getDisplayName() == null) {
            return;
        }
        if (meta.getDisplayName().contains("Party")) {
            if (item.getData().getData() == (byte) 14) {
                player.closeInventory();
                player.sendMessage(ChatColor.RED + "There is no Party right now, sorry!");
                return;
            } else if (item.getData().getData() == (byte) 5) {
                player.closeInventory();
                player.sendMessage(ChatColor.GREEN + "Joining Party...");
                MagicAssistant.bandUtil.joinParty(player);
                return;
            }
        }
        String name = ChatColor.stripColor(meta.getDisplayName());
        switch (name) {
            case "Show Timetable":
                MagicAssistant.inventoryUtil.openInventory(player, InventoryType.SHOWTIMES);
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
            case "Festival of Fantasy Parade":
                player.performCommand("warp mainstreet");
        }
    }
}
