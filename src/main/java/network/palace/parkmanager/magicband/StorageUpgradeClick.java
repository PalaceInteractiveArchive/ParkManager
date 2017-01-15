package network.palace.parkmanager.magicband;

import network.palace.core.Core;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.PlayerData;
import network.palace.parkmanager.storage.Backpack;
import network.palace.parkmanager.storage.Locker;
import network.palace.parkmanager.storage.StorageSize;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import network.palace.parkmanager.utils.BandUtil;

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
            ParkManager.shopManager.openMenu(player);
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        String name = ChatColor.stripColor(meta.getDisplayName());
        PlayerData data = ParkManager.getPlayerData(player.getUniqueId());
        switch (name) {
            case "Upgrade Backpack": {
                if (data.getBackpack().getSize().equals(StorageSize.LARGE)) {
                    player.sendMessage(ChatColor.RED + "You can't upgrade your Backpack any further!");
                    player.closeInventory();
                    return;
                }
                int bal = Core.getEconomy().getBalance(player.getUniqueId());
                if (bal < 500) {
                    player.sendMessage(ChatColor.RED + "You can't afford that!");
                    player.closeInventory();
                    return;
                }
                player.closeInventory();
                Core.getEconomy().addBalance(player.getUniqueId(), -500);
                ItemStack[] cont = data.getBackpack().getInventory().getContents();
                data.setBackpack(new Backpack(player, StorageSize.LARGE, cont));
                ParkManager.storageManager.setValue(player.getUniqueId(), "packsize", "large");
                break;
            }
            case "Upgrade Locker": {
                if (data.getLocker().getSize().equals(StorageSize.LARGE)) {
                    player.sendMessage(ChatColor.RED + "You can't upgrade your Locker any further!");
                    player.closeInventory();
                    return;
                }
                int bal = Core.getEconomy().getBalance(player.getUniqueId());
                if (bal < 500) {
                    player.sendMessage(ChatColor.RED + "You can't afford that!");
                    player.closeInventory();
                    return;
                }
                player.closeInventory();
                Core.getEconomy().addBalance(player.getUniqueId(), -500);
                ItemStack[] cont = data.getLocker().getInventory().getContents();
                data.setLocker(new Locker(player, StorageSize.LARGE, cont));
                ParkManager.storageManager.setValue(player.getUniqueId(), "lockersize", "large");
                break;
            }
        }
    }
}