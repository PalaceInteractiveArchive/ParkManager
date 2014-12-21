package us.mcmagic.magicassistant.magicband;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import us.mcmagic.magicassistant.utils.BandUtil;
import us.mcmagic.magicassistant.utils.InventoryType;
import us.mcmagic.magicassistant.utils.InventoryUtil;

/**
 * Created by Marc on 12/20/14
 */
public class FriendListClick {

    public static void handle(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        if (item.equals(BandUtil.getBackItem())) {
            InventoryUtil.openInventory(player, InventoryType.PLAYERINFO);
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta.getDisplayName() == null) {
            return;
        }
        String name = ChatColor.stripColor(meta.getDisplayName());
        if (item.getType().equals(Material.SKULL_ITEM)) {
            if (((SkullMeta) item.getItemMeta()).getOwner().equals("Herobrine")) {
                return;
            }
            BandUtil.friendTeleport(player, name);
            return;
        }
        String invName = ChatColor.stripColor(event.getInventory().getName());
        switch (name) {
            case "Next Page":
                InventoryUtil.openFriendListPage(player, Integer.parseInt(invName.replaceAll("Friend List Page ", "")) + 1);
                return;
            case "Last Page":
                InventoryUtil.openFriendListPage(player, Integer.parseInt(invName.replaceAll("Friend List Page ", "")) - 1);
        }
    }
}
