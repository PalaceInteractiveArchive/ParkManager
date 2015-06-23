package us.mcmagic.magicassistant.magicband;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.handlers.HotelRoom;
import us.mcmagic.magicassistant.handlers.InventoryType;
import us.mcmagic.magicassistant.handlers.Warp;
import us.mcmagic.magicassistant.utils.BandUtil;
import us.mcmagic.magicassistant.utils.WarpUtil;
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
            MagicAssistant.inventoryUtil.openInventory(player, InventoryType.HOTELSANDRESORTS);
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        String name = ChatColor.stripColor(meta.getDisplayName());
        HotelRoom room = MagicAssistant.hotelManager.getRoom(name);
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
