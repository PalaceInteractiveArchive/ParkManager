package network.palace.parkmanager.magicband;

import network.palace.core.Core;
import network.palace.core.economy.CurrencyType;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.PlayerData;
import network.palace.parkmanager.storage.Backpack;
import network.palace.parkmanager.storage.Locker;
import network.palace.parkmanager.storage.StorageSize;
import network.palace.parkmanager.utils.BandUtil;
import org.bukkit.ChatColor;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Created by Marc on 10/25/15
 */
public class StorageUpgradeClick {

    public static void handle(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null) {
            return;
        }
        CPlayer player = Core.getPlayerManager().getPlayer(event.getWhoClicked().getUniqueId());
        if (item.equals(BandUtil.getBackItem())) {
            ParkManager.getInstance().getShopManager().openMenu(player);
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        String name = ChatColor.stripColor(meta.getDisplayName());
        PlayerData data = ParkManager.getInstance().getPlayerData(player.getUniqueId());
        switch (name) {
            case "Upgrade Backpack": {
                if (data.getBackpack().getSize().equals(StorageSize.LARGE)) {
                    player.sendMessage(ChatColor.RED + "You can't upgrade your Backpack any further!");
                    player.closeInventory();
                    return;
                }

                int bal = Core.getMongoHandler().getCurrency(player.getUniqueId(), CurrencyType.BALANCE);
                if (bal < 500) {
                    player.sendMessage(ChatColor.RED + "You can't afford that!");
                    player.closeInventory();
                    return;
                }
                player.closeInventory();
                Core.getMongoHandler().changeAmount(player.getUniqueId(), -500, "backpack upgrade", CurrencyType.BALANCE, false);
                ItemStack[] cont = data.getBackpack().getInventory().getContents();
                data.setBackpack(new Backpack(player, StorageSize.LARGE, cont));
                ParkManager.getInstance().getStorageManager().setValue(player.getUniqueId(), "packsize", 1);
                break;
            }
            case "Upgrade Locker": {
                if (data.getLocker().getSize().equals(StorageSize.LARGE)) {
                    player.sendMessage(ChatColor.RED + "You can't upgrade your Locker any further!");
                    player.closeInventory();
                    return;
                }
                int bal = Core.getMongoHandler().getCurrency(player.getUniqueId(), CurrencyType.BALANCE);
                if (bal < 500) {
                    player.sendMessage(ChatColor.RED + "You can't afford that!");
                    player.closeInventory();
                    return;
                }
                player.closeInventory();
                Core.getMongoHandler().changeAmount(player.getUniqueId(), -500, "locker upgrade", CurrencyType.BALANCE, false);
                ItemStack[] cont = data.getLocker().getInventory().getContents();
                data.setLocker(new Locker(player, StorageSize.LARGE, cont));
                ParkManager.getInstance().getStorageManager().setValue(player.getUniqueId(), "lockersize", 1);
                break;
            }
        }
    }
}