package us.mcmagic.parkmanager.magicband;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.parkmanager.ParkManager;
import us.mcmagic.parkmanager.handlers.HotelRoom;
import us.mcmagic.parkmanager.handlers.InventoryType;
import us.mcmagic.parkmanager.hotels.HotelManager;
import us.mcmagic.parkmanager.utils.BandUtil;

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
            ParkManager.inventoryUtil.openInventory(player, InventoryType.HOTELS);
            return;
        }
        boolean playerOwnsRooms = false;
        HotelManager manager = ParkManager.hotelManager;
        for (HotelRoom room : manager.getHotelRooms()) {
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
        if (room == null) {
            return;
        }
        if (MCMagicCore.economy.getBalance(player.getUniqueId()) >= room.getCost()) {
            MCMagicCore.economy.addBalance(player.getUniqueId(), -room.getCost(), room.getHotelName() + " #" +
                    room.getRoomNumber());
            manager.rentRoom(room, player);
        } else {
            player.closeInventory();
            player.sendMessage(ChatColor.RED + "You don't have enough money to book this room!");
        }
    }
}
