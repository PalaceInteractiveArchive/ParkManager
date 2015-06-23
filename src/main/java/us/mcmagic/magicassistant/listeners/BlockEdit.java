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
import us.mcmagic.magicassistant.hotels.HotelManager;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.permissions.Rank;
import us.mcmagic.mcmagiccore.player.User;

public class BlockEdit implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        User user = MCMagicCore.getUser(player.getUniqueId());
        if (user.getRank().getRankId() < Rank.CASTMEMBER.getRankId()) {
            event.setCancelled(true);
            return;
        }
        if (event.getBlock().getType() == Material.SIGN_POST || event.getBlock().getType() == Material.WALL_SIGN) {
            Sign s = (Sign) event.getBlock().getState();
            if (ChatColor.stripColor(s.getLine(0)).equalsIgnoreCase("[hotel]")) {
                if (!player.getItemInHand().getType().equals(Material.STICK)) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "Please break " + ChatColor.BLUE + "[Hotel] " + ChatColor.RED +
                            "signs with a " + ChatColor.GREEN + "Stick.");
                    return;
                }
                String hotelName = ChatColor.stripColor(s.getLine(2));
                String fullRoomName = hotelName + " #" + ChatColor.stripColor(s.getLine(1));
                HotelManager manager = MagicAssistant.hotelManager;
                if (manager.getRoom(fullRoomName) != null) {
                    manager.removeRoom(manager.getRoom(fullRoomName));
                    manager.refreshRooms();
                    manager.updateRooms();
                }
            }
        }

    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        User user = MCMagicCore.getUser(player.getUniqueId());
        if (user.getRank().getRankId() < Rank.CASTMEMBER.getRankId()) {
            event.setCancelled(true);
        }
    }
}