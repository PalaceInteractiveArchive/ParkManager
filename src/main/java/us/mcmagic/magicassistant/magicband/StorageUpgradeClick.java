package us.mcmagic.magicassistant.magicband;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.handlers.PlayerData;
import us.mcmagic.magicassistant.storage.Backpack;
import us.mcmagic.magicassistant.storage.Locker;
import us.mcmagic.magicassistant.storage.StorageSize;
import us.mcmagic.magicassistant.utils.BandUtil;
import us.mcmagic.mcmagiccore.MCMagicCore;

/**
 * Created by Marc on 10/25/15
 */
public class StorageUpgradeClick {

    public static void handle(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        if (item.equals(BandUtil.getBackItem())) {
            MagicAssistant.shopManager.openMenu(player);
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        String name = ChatColor.stripColor(meta.getDisplayName());
        PlayerData data = MagicAssistant.getPlayerData(player.getUniqueId());
        switch (name) {
            case "Upgrade Backpack": {
                if (data.getBackpack().getSize().equals(StorageSize.LARGE)) {
                    player.sendMessage(ChatColor.RED + "You can't upgrade your Backpack any further!");
                    player.closeInventory();
                    return;
                }
                int bal = MCMagicCore.economy.getBalance(player.getUniqueId());
                if (bal < 500) {
                    player.sendMessage(ChatColor.RED + "You can't afford that!");
                    player.closeInventory();
                    return;
                }
                player.closeInventory();
                MCMagicCore.economy.addBalance(player.getUniqueId(), -500);
                ItemStack[] cont = data.getBackpack().getInventory().getContents();
                data.setBackpack(new Backpack(player, StorageSize.LARGE, cont));
                MagicAssistant.storageManager.setValue(player.getUniqueId(), "packsize", "large");
                break;
            }
            case "Upgrade Locker": {
                if (data.getLocker().getSize().equals(StorageSize.LARGE)) {
                    player.sendMessage(ChatColor.RED + "You can't upgrade your Locker any further!");
                    player.closeInventory();
                    return;
                }
                int bal = MCMagicCore.economy.getBalance(player.getUniqueId());
                if (bal < 500) {
                    player.sendMessage(ChatColor.RED + "You can't afford that!");
                    player.closeInventory();
                    return;
                }
                player.closeInventory();
                MCMagicCore.economy.addBalance(player.getUniqueId(), -500);
                ItemStack[] cont = data.getLocker().getInventory().getContents();
                data.setLocker(new Locker(player, StorageSize.LARGE, cont));
                MagicAssistant.storageManager.setValue(player.getUniqueId(), "lockersize", "large");
                break;
            }
        }
    }
}