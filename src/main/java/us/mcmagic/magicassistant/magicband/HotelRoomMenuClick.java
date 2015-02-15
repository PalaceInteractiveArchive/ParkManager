package us.mcmagic.magicassistant.magicband;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.handlers.HotelRoom;
import us.mcmagic.magicassistant.utils.BandUtil;
import us.mcmagic.magicassistant.utils.HotelUtil;
import us.mcmagic.magicassistant.utils.InventoryType;
import us.mcmagic.magicassistant.utils.InventoryUtil;
import us.mcmagic.mcmagiccore.coins.Coins;

/**
 * Created by Greenlock28 on 1/25/2015.
 */
public class HotelRoomMenuClick {

    public static void handle(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        if (item.equals(BandUtil.getBackItem())) {
            InventoryUtil.openInventory(player, InventoryType.HOTELS);
            return;
        }

        boolean playerOwnsRooms = false;
        for (HotelRoom room : HotelUtil.getRooms()) {
            if (room.isOccupied() && room.getCurrentOccupant().equalsIgnoreCase(player.getUniqueId().toString())) {
                playerOwnsRooms = true;
                break;
            }
        }
        if (playerOwnsRooms) {
            player.closeInventory();
            player.sendMessage(ChatColor.RED + "You cannot book more than one room at a time!  You need to wait for your current reservation to lapse or check out by right-clicking the booked room's sign.");
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        String name = ChatColor.stripColor(meta.getDisplayName());
        HotelRoom room = HotelUtil.getRoom(name);
        if (Coins.getSqlCoins(player) >= room.getCost()) {
            Coins.minusSqlCoins(player, room.getCost());
            room.setCurrentOccupant(player.getUniqueId().toString());
            room.setOccupationCooldown(72);
            HotelUtil.updateRoom(room);
            HotelUtil.updateRooms();
            player.closeInventory();
            player.sendMessage(ChatColor.GREEN + "You have booked the " + room.getName() + " room for " + Integer.toString(room.getCost()) + " coins!");
            player.sendMessage(ChatColor.GREEN + "You can travel to your room using the My Hotel Rooms menu on your MagicBand.");
            return;
        } else {
            player.closeInventory();
            player.sendMessage(ChatColor.RED + "You don't have enough coins to book this room!");
            return;
        }
    }
}
