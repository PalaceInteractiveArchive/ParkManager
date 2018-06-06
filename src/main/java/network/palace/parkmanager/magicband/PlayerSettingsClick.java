package network.palace.parkmanager.magicband;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.InventoryType;
import network.palace.parkmanager.handlers.PlayerData;
import network.palace.parkmanager.utils.BandUtil;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Created by Marc on 4/21/15
 */
public class PlayerSettingsClick {

    public static void handle(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null) {
            return;
        }
        if (item.getItemMeta() == null) {
            return;
        }
        CPlayer player = Core.getPlayerManager().getPlayer((Player) event.getWhoClicked());
        if (item.equals(BandUtil.getBackItem())) {
            ParkManager.getInstance().getInventoryUtil().openInventory(player, InventoryType.MYPROFILE);
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta.getDisplayName() == null) {
            return;
        }
        String name = ChatColor.stripColor(meta.getDisplayName());
        PlayerData data = ParkManager.getInstance().getPlayerData(player.getUniqueId());
        switch (name) {
            case "Flash Effects":
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 100, 2);
                data.setFlash(!data.isFlash());
                ParkManager.getInstance().getInventoryUtil().openInventory(player, InventoryType.PLAYERSETTINGS);
                ParkManager.getInstance().getBandUtil().setSetting(player, "flash", data.isFlash());
                return;
            case "Player Visibility":
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 100, 2);
                data.setVisibility(!data.isVisibility());
                if (data.isVisibility()) {
                    ParkManager.getInstance().getVisibilityUtil().removeFromHideAll(player);
                } else {
                    ParkManager.getInstance().getVisibilityUtil().addToHideAll(player);
                }
                ParkManager.getInstance().getInventoryUtil().openInventory(player, InventoryType.PLAYERSETTINGS);
                ParkManager.getInstance().getBandUtil().setSetting(player, "visibility", data.isVisibility());
                return;
            case "Friends Access Hotel Room":
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 100, 2);
                data.setHotel(!data.isHotel());
                ParkManager.getInstance().getInventoryUtil().openInventory(player, InventoryType.PLAYERSETTINGS);
                ParkManager.getInstance().getBandUtil().setSetting(player, "hotel", data.isHotel());
        }
    }
}
