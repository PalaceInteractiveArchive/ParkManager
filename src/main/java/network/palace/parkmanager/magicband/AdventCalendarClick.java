package network.palace.parkmanager.magicband;

import network.palace.parkmanager.handlers.Warp;
import network.palace.parkmanager.utils.WarpUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Created by Marc on 12/1/15
 */
public class AdventCalendarClick {

    public static void handle(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null || item.getItemMeta() == null) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        ItemMeta meta = item.getItemMeta();
        if (meta.getDisplayName() == null) {
            return;
        }
        String name = ChatColor.stripColor(meta.getDisplayName());
        int day = Integer.parseInt(name.replace("December ", "").replace("st", "").replace("nd", "").replace("rd", "")
                .replace("th", ""));
        Warp warp = WarpUtil.findWarp("advent" + day);
        if (warp != null) {
            player.performCommand("warp " + warp.getName());
        } else {
            player.sendMessage(ChatColor.RED + "That Advent Area isn't open yet!");
        }
    }
}