package us.mcmagic.magicassistant.magicband;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.handlers.HotelRoom;
import us.mcmagic.magicassistant.handlers.InventoryType;
import us.mcmagic.magicassistant.hotels.HotelManager;
import us.mcmagic.magicassistant.utils.BandUtil;
import us.mcmagic.mcmagiccore.MCMagicCore;

/**
 * Created by Greenlock28 on 1/25/2015.
 */
public class HotelRoomMenuClick {

    public static void handle(InventoryClickEvent event) {
        final ItemStack item = event.getCurrentItem();
        if (item == null) {
            return;
        }
        final Player player = (Player) event.getWhoClicked();
        if (item.equals(BandUtil.getBackItem())) {
            MagicAssistant.inventoryUtil.openInventory(player, InventoryType.HOTELS);
            return;
        }
        boolean playerOwnsRooms = false;
        HotelManager manager = MagicAssistant.hotelManager;
        for (HotelRoom room : manager.getRooms()) {
            if (room.isOccupied() && room.getCurrentOccupant().equals(player.getUniqueId())) {
                playerOwnsRooms = true;
                break;
            }
        }
        if (playerOwnsRooms) {
            player.closeInventory();
            player.sendMessage(ChatColor.RED + "You cannot book more than one room at a time! You need to wait for your current reservation to lapse or check out by right-clicking the booked room's sign.");
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        String name = ChatColor.stripColor(meta.getDisplayName());
        HotelRoom room = manager.getRoom(name);
        if (MCMagicCore.economy.getBalance(player.getUniqueId()) >= room.getCost()) {
            MCMagicCore.economy.addBalance(player.getUniqueId(), -room.getCost());
            manager.rentRoom(room, player);
        } else {
            player.closeInventory();
            player.sendMessage(ChatColor.RED + "You don't have enough money to book this room!");
        }
    }
}
