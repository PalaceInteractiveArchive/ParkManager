package us.mcmagic.magicassistant.magicband;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.mcmagic.magicassistant.handlers.HotelRoom;
import us.mcmagic.magicassistant.utils.BandUtil;
import us.mcmagic.magicassistant.utils.HotelUtil;
import us.mcmagic.magicassistant.utils.InventoryType;
import us.mcmagic.magicassistant.utils.InventoryUtil;
import us.mcmagic.mcmagiccore.coins.Coins;

/**
 * Created by Greenlock28 on 2/11/2015.
 */
public class HotelCheckoutMenuClick {

    public static void handle(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        if (item.equals(BandUtil.getBackItem())) {
            InventoryUtil.openInventory(player, InventoryType.HOTELSANDRESORTS);
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        String name = ChatColor.stripColor(meta.getDisplayName()).substring(13);
        HotelRoom room = HotelUtil.getRoom(name);
        room.setCurrentOccupant(null);
        room.setOccupationCooldown(0);
        HotelUtil.updateRoom(room);
        HotelUtil.refreshRooms();
        HotelUtil.updateRooms();
        player.closeInventory();
        player.sendMessage(ChatColor.GREEN + "You have checked out of your room.  Have a wonderful rest of your visit!");
        return;
    }
}
