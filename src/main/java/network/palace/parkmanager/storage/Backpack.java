package network.palace.parkmanager.storage;

import network.palace.core.player.CPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * Created by Marc on 10/10/15
 */
public class Backpack {
    private UUID uuid;
    private StorageSize size;
    private Inventory inv;

    public Backpack(CPlayer player, StorageSize size, ItemStack[] contents) {
        this.uuid = player.getUniqueId();
        this.size = size;
        inv = Bukkit.createInventory(player.getBukkitPlayer(), size.getRows() * 9, ChatColor.BLUE + "Your Backpack");
        if (contents != null) {
            inv.setContents(contents);
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