package us.mcmagic.parkmanager.magicband;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.mcmagic.parkmanager.ParkManager;
import us.mcmagic.parkmanager.handlers.InventoryType;
import us.mcmagic.parkmanager.handlers.PlayerData;
import us.mcmagic.parkmanager.utils.BandUtil;

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
        Player player = (Player) event.getWhoClicked();
        if (item.equals(BandUtil.getBackItem())) {
            ParkManager.inventoryUtil.openInventory(player, InventoryType.MYPROFILE);
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta.getDisplayName() == null) {
            return;
        }
        String name = ChatColor.stripColor(meta.getDisplayName());
        PlayerData data = ParkManager.getPlayerData(player.getUniqueId());
        switch (name) {
            case "Flash Effects":
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 100, 2);
                data.setFlash(!data.getFlash());
                ParkManager.inventoryUtil.openInventory(player, InventoryType.PLAYERSETTINGS);
                ParkManager.bandUtil.setSetting(player.getUniqueId(), "flash", data.getFlash());
                return;
            case "Player Visibility":
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 100, 2);
                data.setVisibility(!data.getVisibility());
                if (data.getVisibility()) {
                    ParkManager.visibilityUtil.removeFromHideAll(player);
                } else {
                    ParkManager.visibilityUtil.addToHideAll(player);
                }
                ParkManager.inventoryUtil.openInventory(player, InventoryType.PLAYERSETTINGS);
                ParkManager.bandUtil.setSetting(player.getUniqueId(), "visibility", data.getVisibility());
                return;
            case "Park Loop Music":
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 100, 2);
                data.setLoop(!data.getLoop());
                ParkManager.inventoryUtil.openInventory(player, InventoryType.PLAYERSETTINGS);
                ParkManager.bandUtil.setSetting(player.getUniqueId(), "parkloop", data.getLoop());
                return;
            case "Friends Access Hotel Room":
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 100, 2);
                data.setHotel(!data.getHotel());
                ParkManager.inventoryUtil.openInventory(player, InventoryType.PLAYERSETTINGS);
                ParkManager.bandUtil.setSetting(player.getUniqueId(), "hotel", data.getHotel());
        }
    }
}
