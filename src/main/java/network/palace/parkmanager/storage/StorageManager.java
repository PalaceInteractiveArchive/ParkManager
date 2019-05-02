package network.palace.parkmanager.storage;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.core.utils.ItemUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.dashboard.packets.parks.PacketInventoryContent;
import network.palace.parkmanager.handlers.storage.StorageData;
import network.palace.parkmanager.handlers.storage.StorageSize;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BookMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

public class StorageManager {
    private HashMap<UUID, StorageData> savedStorageData = new HashMap<>();

    /**
     * Handle inventory setting when a player joins
     *
     * @param player the player
     */
    public void handleJoin(CPlayer player) {
        StorageData data = savedStorageData.remove(player.getUniqueId());
        if (data == null) {
            player.getRegistry().addEntry("waitingForInventory", true);
            return;
        }
        player.getRegistry().addEntry("storageData", data);
        updateInventory(player, true);
    }

    /**
     * Update the player's inventory depending on their build state
     *
     * @param player the player
     */
    public void updateInventory(CPlayer player) {
        updateInventory(player, false);
    }

    /**
     * Update the player's inventory depending on their build state
     *
     * @param player       the player
     * @param storageCheck true if it's been verified the player's registry contains an entry for storageData
     * @implNote only set storageCheck to true if you're certain the registry has an entry for storageData!
     */
    public void updateInventory(CPlayer player, boolean storageCheck) {
        if (!storageCheck && !player.getRegistry().hasEntry("storageData")) return;

        StorageData data = (StorageData) player.getRegistry().getEntry("storageData");

        if (ParkManager.getBuildUtil().isInBuildMode(player)) {
            player.setGamemode(GameMode.CREATIVE);
            return;
        }
        player.setGamemode(player.getRank().getRankId() >= Rank.MOD.getRankId() ? GameMode.SURVIVAL : GameMode.ADVENTURE);

        PlayerInventory inv = player.getInventory();
        inv.setItem(5, ItemUtil.create(Material.CLOCK, ChatColor.GREEN + "Watch " + ChatColor.GRAY + "(Right-Click)",
                Arrays.asList(ChatColor.GRAY + "Right-Click to open the", ChatColor.GRAY + "Show Schedule Menu")));
        inv.setItem(6, ItemUtil.create(Material.CHEST, ChatColor.GREEN + "Backpack " + ChatColor.GRAY + "(Right-Click)"));
        Core.runTaskAsynchronously(() -> {
            ItemStack book = ItemUtil.create(Material.WRITTEN_BOOK);

            BookMeta meta = (BookMeta) book.getItemMeta();
            meta.setTitle(ChatColor.DARK_AQUA + "Autograph Book");
            meta.setAuthor(player.getName());
            book.setItemMeta(meta);

            inv.setItem(7, book);
        });
        inv.setItem(8, ParkManager.getMagicBandUtil().getMagicBandItem("red", "gold"));

        if (data != null) {
            ItemStack[] hotbar = data.getHotbar();
            int max = hotbar.length > 5 ? 5 : hotbar.length;
            for (int i = 0; i < max; i++) {
                inv.setItem(i, hotbar[i]);
            }
        }

        if (player.getRank().getRankId() >= Rank.MOD.getRankId()) {
            inv.setItem(0, ItemUtil.create(Material.COMPASS));
        }
    }

    /**
     * Process an incoming inventory content packet
     *
     * @param packet the packet
     * @implNote If the target player isn't online, store the inventory for when the player joins
     * @implNote If the target player is online, save the StorageData to the player's registry and update the player's inventory
     */
    public void processIncomingPacket(PacketInventoryContent packet) {
        ItemStack[] backpackArray = ItemUtil.getInventoryFromJson(packet.getBackpackJson());
        ItemStack[] lockerArray = ItemUtil.getInventoryFromJson(packet.getLockerJson());
        ItemStack[] hotbarArray = ItemUtil.getInventoryFromJson(packet.getHotbarJson());

        packet.setBackpack(backpackArray);
        packet.setLocker(lockerArray);
        packet.setHotbar(hotbarArray);

        StorageSize backpackSize = StorageSize.fromInt(packet.getBackpackSize());
        StorageSize lockerSize = StorageSize.fromInt(packet.getLockerSize());

        ItemStack[] packItems = ItemUtil.getInventoryFromJson(packet.getBackpackJson());
        ItemStack[] lockerItems = ItemUtil.getInventoryFromJson(packet.getLockerJson());
        ItemStack[] hotbar = ItemUtil.getInventoryFromJson(packet.getHotbarJson());

        Inventory backpack = Bukkit.createInventory(null, backpackSize.getSlots(), ChatColor.BLUE + "Your Backpack");
        Inventory locker = Bukkit.createInventory(null, backpackSize.getSlots(), ChatColor.BLUE + "Your Locker");

        fillInventory(backpack, backpackSize, packItems);
        fillInventory(locker, lockerSize, lockerItems);

        StorageData data = new StorageData(backpack, locker, hotbar);

        CPlayer player = Core.getPlayerManager().getPlayer(packet.getUuid());
        if (player != null && player.getRegistry().hasEntry("waitingForInventory")) {
            player.getRegistry().removeEntry("waitingForInventory");
            player.getRegistry().addEntry("storageData", data);
            updateInventory(player, true);
        } else {
            savedStorageData.put(packet.getUuid(), data);
        }
    }

    private void fillInventory(Inventory inventory, StorageSize size, ItemStack[] items) {
        if (items.length == size.getSlots()) {
            inventory.setContents(items);
        } else {
            ItemStack[] arr = new ItemStack[size.getSlots()];

            System.arraycopy(items, 0, arr, 0, Math.min(items.length, size.getSlots()));

            inventory.setContents(arr);
        }
    }
}
