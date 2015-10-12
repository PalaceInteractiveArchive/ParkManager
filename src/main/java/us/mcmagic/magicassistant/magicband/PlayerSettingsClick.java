package us.mcmagic.magicassistant.magicband;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.handlers.InventoryType;
import us.mcmagic.magicassistant.handlers.PlayerData;
import us.mcmagic.magicassistant.utils.BandUtil;

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
            MagicAssistant.inventoryUtil.openInventory(player, InventoryType.MYPROFILE);
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta.getDisplayName() == null) {
            return;
        }
        String name = ChatColor.stripColor(meta.getDisplayName());
        PlayerData data = MagicAssistant.getPlayerData(player.getUniqueId());
        switch (name) {
            case "Flash Effects":
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 100, 2);
                data.setFlash(!data.getFlash());
                MagicAssistant.inventoryUtil.openInventory(player, InventoryType.PLAYERSETTINGS);
                MagicAssistant.bandUtil.setSetting(player.getUniqueId(), "flash", data.getFlash());
                return;
            case "Player Visibility":
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 100, 2);
                data.setVisibility(!data.getVisibility());
                if (data.getVisibility()) {
                    MagicAssistant.vanishUtil.removeFromHideAll(player);
                } else {
                    MagicAssistant.vanishUtil.addToHideAll(player);
                }
                MagicAssistant.inventoryUtil.openInventory(player, InventoryType.PLAYERSETTINGS);
                MagicAssistant.bandUtil.setSetting(player.getUniqueId(), "visibility", data.getVisibility());
                return;
            case "Fountains":
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 100, 2);
                data.setFountain(!data.getFountain());
                MagicAssistant.inventoryUtil.openInventory(player, InventoryType.PLAYERSETTINGS);
                MagicAssistant.bandUtil.setSetting(player.getUniqueId(), "fountain", data.getFountain());
                return;
            case "Friends Access Hotel Room":
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 100, 2);
                data.setHotel(!data.getHotel());
                MagicAssistant.inventoryUtil.openInventory(player, InventoryType.PLAYERSETTINGS);
                MagicAssistant.bandUtil.setSetting(player.getUniqueId(), "hotel", data.getHotel());
        }
    }
}
