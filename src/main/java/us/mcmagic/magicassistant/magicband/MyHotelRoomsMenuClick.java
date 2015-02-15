package us.mcmagic.magicassistant.magicband;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.handlers.HotelRoom;
import us.mcmagic.magicassistant.handlers.Warp;
import us.mcmagic.magicassistant.utils.*;

/**
 * Created by Greenlock28 on 1/25/2015.
 */
public class MyHotelRoomsMenuClick {

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
        String name = ChatColor.stripColor(meta.getDisplayName());
        HotelRoom room = HotelUtil.getRoom(name);
        if (room != null) {
            Warp warp = room.getWarp();
            if (warp != null) {
                if (warp.getServer().equals(MagicAssistant.serverName)) {
                    if (player.isInsideVehicle()) {
                        player.getVehicle().eject();
                    }
                    player.teleport(warp.getLocation());
                } else {
                    WarpUtil.crossServerWarp(player.getUniqueId().toString(), warp.getName(), warp.getServer());
                }
            } else {
                player.sendMessage(ChatColor.RED + "This room does not have a warp set!");
                player.closeInventory();
                return;
            }
        } else {
            player.closeInventory();
        }
    }
}
