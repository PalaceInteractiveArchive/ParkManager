package us.mcmagic.parkmanager.magicband;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.mcmagic.parkmanager.ParkManager;
import us.mcmagic.parkmanager.handlers.HotelRoom;
import us.mcmagic.parkmanager.handlers.InventoryType;
import us.mcmagic.parkmanager.handlers.Warp;
import us.mcmagic.parkmanager.utils.BandUtil;
import us.mcmagic.parkmanager.utils.WarpUtil;
import us.mcmagic.mcmagiccore.MCMagicCore;

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
            ParkManager.inventoryUtil.openInventory(player, InventoryType.HOTELSANDRESORTS);
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        String name = ChatColor.stripColor(meta.getDisplayName());
        HotelRoom room = ParkManager.hotelManager.getRoom(name);
        if (room != null) {
            Warp warp = room.getWarp();
            if (warp != null) {
                if (warp.getServer().equals(MCMagicCore.getMCMagicConfig().serverName)) {
                    if (player.isInsideVehicle()) {
                        player.getVehicle().eject();
                    }
                    Chunk c = warp.getLocation().getChunk();
                    if (!c.isLoaded()) {
                        c.load();
                    }
                    player.teleport(warp.getLocation());
                } else {
                    WarpUtil.crossServerWarp(player.getUniqueId(), warp.getName(), warp.getServer());
                }
            } else {
                player.sendMessage(ChatColor.RED + "This room does not have a warp set!");
                player.closeInventory();
            }
        } else {
            player.closeInventory();
        }
    }
}
