package us.mcmagic.magicassistant.magicband;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.handlers.InventoryType;
import us.mcmagic.magicassistant.queue.QueueRide;
import us.mcmagic.magicassistant.utils.BandUtil;

/**
 * Created by Marc on 6/24/15
 */
public class WaitTimeClick {

    public static void handle(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        if (item.equals(BandUtil.getBackItem())) {
            MagicAssistant.inventoryUtil.openInventory(player, InventoryType.RIDESANDATTRACTIONS);
            return;
        }
        if (item.getItemMeta() == null) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta.getDisplayName() == null) {
            return;
        }
        String name = meta.getDisplayName();
        if (name.equals(ChatColor.RED + "Uh oh!")) {
            player.closeInventory();
            player.sendMessage(ChatColor.RED + "Sorry, but there are no rides setup on this server!");
        }
        QueueRide ride = MagicAssistant.queueManager.getRide2(name);
        player.performCommand("warp " + ride.getWarp());
    }
}