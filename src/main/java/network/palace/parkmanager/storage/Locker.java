package network.palace.parkmanager.storage;

import network.palace.core.player.CPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * Created by Marc on 10/11/15
 */
public class Locker {
    private UUID uuid;
    private StorageSize size;
    private Inventory inv;

    public Locker(CPlayer player, StorageSize size, ItemStack[] contents) {
        this.uuid = player.getUniqueId();
        this.size = size;
        inv = Bukkit.createInventory(player.getBukkitPlayer(), size.getRows() * 9, ChatColor.BLUE + "Your Locker");
        if (contents != null) {
            for (int i = 0; i < contents.length - 1; i++) {
                if (i > 27) break;
                inv.addItem(contents[i]);
            }
        }
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public StorageSize getSize() {
        return size;
    }

    public Inventory getInventory() {
        return inv;
    }
}