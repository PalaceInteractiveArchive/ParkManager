package network.palace.parkmanager.magicband;

import network.palace.core.Core;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.HotelRoom;
import network.palace.parkmanager.handlers.InventoryType;
import network.palace.parkmanager.utils.BandUtil;
import network.palace.parkwarp.handlers.Warp;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
            ParkManager.getInstance().getInventoryUtil().openInventory(player, InventoryType.HOTELSANDRESORTS);
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        String name = ChatColor.stripColor(meta.getDisplayName());
        HotelRoom room = ParkManager.getInstance().getHotelManager().getRoom(name);
        if (room != null) {
            Warp warp = room.getWarp();
            if (warp != null) {
                if (warp.getServer().equals(Core.getInstanceName())) {
                    if (player.isInsideVehicle()) {
                        player.getVehicle().eject();
                    }
                    Chunk c = warp.getLocation().getChunk();
                    if (!c.isLoaded()) {
                        c.load();
                    }
                    player.teleport(warp.getLocation());
                } else {
                    Core.getPlayerManager().getPlayer(player).sendToServer(warp.getServer());
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
