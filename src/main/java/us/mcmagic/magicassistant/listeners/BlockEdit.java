package us.mcmagic.magicassistant.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.utils.HotelUtil;

public class BlockEdit implements Listener {
    public static MagicAssistant pl;

    public BlockEdit(MagicAssistant instance) {
        pl = instance;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission("block.break")) {
            event.setCancelled(true);
        }
        if (event.getBlock().getType() == Material.SIGN_POST ||
                event.getBlock().getType() == Material.WALL_SIGN) {
            Sign s = (Sign) event.getBlock().getState();
            if (ChatColor.stripColor(s.getLine(0)).equalsIgnoreCase("[hotel]")) {
                String hotelName = ChatColor.stripColor(s.getLine(2));
                String fullRoomName = hotelName + " #" + ChatColor.stripColor(s.getLine(1));
                if (HotelUtil.getRoom(fullRoomName) != null) {
                    HotelUtil.removeRoom(HotelUtil.getRoom(fullRoomName));
                    HotelUtil.refreshRooms();
                    HotelUtil.updateRooms();
                }
            }
        }

    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission("block.place")) {
            event.setCancelled(true);
        }
    }
}