package network.palace.parkmanager.magicband;

import network.palace.core.Core;
import network.palace.core.economy.CurrencyType;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.HotelRoom;
import network.palace.parkmanager.handlers.InventoryType;
import network.palace.parkmanager.hotels.HotelManager;
import network.palace.parkmanager.utils.BandUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
            ParkManager.getInstance().getInventoryUtil().openInventory(player, InventoryType.HOTELS);
            return;
        }
        ParkManager.getInstance().getInventoryUtil().openInventory(player, InventoryType.HOTELS);
        /*
        boolean playerOwnsRooms = false;
        HotelManager manager = ParkManager.getInstance().getHotelManager();
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
        if (Core.getMongoHandler().getCurrency(player.getUniqueId(), CurrencyType.BALANCE) >= room.getCost()) {
            Core.getMongoHandler().changeAmount(player.getUniqueId(), -room.getCost(), room.getHotelName() +
                    " #" + room.getRoomNumber(), CurrencyType.BALANCE, false);
            manager.rentRoom(room, player);
        } else {
            player.closeInventory();
            player.sendMessage(ChatColor.RED + "You don't have enough money to book this room!");
        }*/
    }
}
