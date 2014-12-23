package us.mcmagic.magicassistant.magicband;

import org.bukkit.ChatColor;
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
public class AttractionListClick {

    public static void handle(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        if (item.equals(BandUtil.getBackItem())) {
            InventoryUtil.openInventory(player, InventoryType.RIDESANDATTRACTIONS);
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta.getDisplayName() == null) {
            return;
        }
        String name = ChatColor.stripColor(meta.getDisplayName());
        if (meta.getDisplayName().equals(ChatColor.RED + "Uh oh!")) {
            player.closeInventory();
            player.sendMessage(ChatColor.RED + "Sorry, but there are no attraction setup on this server!");
            return;
        }
        String invName = ChatColor.stripColor(event.getInventory().getName());
        switch (name) {
            case "Next Page":
                InventoryUtil.openAttractionListPage(player, Integer.parseInt(invName.replaceAll("Attraction List Page ", "")) + 1);
                return;
            case "Last Page":
                InventoryUtil.openAttractionListPage(player, Integer.parseInt(invName.replaceAll("Attraction List Page ", "")) - 1);
                return;
        }
        Attraction attraction = MagicAssistant.getAttraction(name);
        if (attraction == null) {
            player.closeInventory();
            player.sendMessage(ChatColor.RED + "There was an error, please tell a Staff Member!");
            return;
        }
        player.closeInventory();
        player.performCommand("warp " + attraction.getWarp());
    }
}
