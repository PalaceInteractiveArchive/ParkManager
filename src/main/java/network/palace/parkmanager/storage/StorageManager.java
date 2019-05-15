package network.palace.parkmanager.storage;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.core.utils.ItemUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.dashboard.packets.parks.PacketInventoryContent;
import network.palace.parkmanager.handlers.storage.StorageData;
import network.palace.parkmanager.handlers.storage.StorageSize;
import network.palace.parkmanager.utils.HashUtil;
import network.palace.parkmanager.utils.InventoryUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BookMeta;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class StorageManager {
    private static final List<Material> bannedItems = Arrays.asList(Material.MINECART, Material.SNOWBALL, Material.ARROW);
    private Cache<UUID, StorageData> savedStorageData = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).build();
    private int updaterTaskID = -1;

    public void initialize() {
        if (updaterTaskID != -1) {
            Core.cancelTask(updaterTaskID);
        }
        updaterTaskID = Core.runTaskTimer(() -> Core.getPlayerManager().getOnlinePlayers().forEach(this::updateCachedInventory), 0L, 1200L);
    }

    /**
     * Handle inventory setting when a player joins
     *
     * @param player the player
     */
    public void handleJoin(CPlayer player, boolean buildMode) {
        StorageData data = savedStorageData.getIfPresent(player.getUniqueId());
        if (data == null) {
            player.getRegistry().addEntry("waitingForInventory", true);
            return;
        }
        savedStorageData.invalidate(player.getUniqueId());
        player.getRegistry().addEntry("storageData", data);
        if (buildMode) {
            ParkManager.getBuildUtil().toggleBuildMode(player, true);
        } else {
            updateInventory(player, true);
        }
    }

    public void updateCachedInventory(CPlayer player) {
        updateCachedInventory(player, ParkManager.getBuildUtil().isInBuildMode(player));
    }

    public void updateCachedInventory(CPlayer player, boolean build) {
        updateCachedInventory(player, build, false);
    }

    public void updateCachedInventory(CPlayer player, boolean buildMode, boolean disconnect) {
        if (!player.getRegistry().hasEntry("storageData")) return;
        StorageData data = (StorageData) player.getRegistry().getEntry("storageData");

        if (System.currentTimeMillis() - data.getLastInventoryUpdate() < (5 * 60 * 1000) && !disconnect) return;
        long currentTime = System.currentTimeMillis();

        Inventory backpackInventory = data.getBackpack();
        ItemStack[] base = new ItemStack[36];
        ItemStack[] build = new ItemStack[34];

        PlayerInventory inv = player.getInventory();
        ItemStack[] invContents = inv.getStorageContents();
        if (buildMode) {
            //Store current inventory items into 'build' array
            if (invContents.length - 2 >= 0) System.arraycopy(invContents, 2, build, 0, invContents.length - 2);
            base = data.getBase();
        } else {
            //Store current inventory items (except reserved slots) into 'base' array
            for (int i = 0; i < invContents.length; i++) {
                if (InventoryUtil.isReservedSlot(i)) continue;
                base[i] = invContents[i];
            }
            build = data.getBuild();
        }

        String backpackJson = ItemUtil.getJsonFromInventory(backpackInventory).toString();
        String backpackHash = HashUtil.generateHash(backpackJson);
        int backpackSize;

        String lockerJson = ItemUtil.getJsonFromInventory(data.getLocker()).toString();
        String lockerHash = HashUtil.generateHash(lockerJson);
        int lockerSize;

        String baseJson = ItemUtil.getJsonFromArray(base).toString();
        String baseHash = HashUtil.generateHash(baseJson);

        String buildJson = ItemUtil.getJsonFromArray(build).toString();
        String buildHash = HashUtil.generateHash(buildJson);

        if (backpackHash.equals(data.getBackpackHash())) {
            backpackJson = "";
            backpackHash = "";
        } else {
            data.setBackpackHash(backpackHash);
        }

        if (data.getBackpackSize().getSize() == data.getDbBackpackSize()) {
            backpackSize = -1;
        } else {
            backpackSize = data.getBackpackSize().getSize();
            data.setDbBackpackSize(backpackSize);
        }

        if (lockerHash.equals(data.getLockerHash())) {
            lockerJson = "";
            lockerHash = "";
        } else {
            data.setLockerHash(lockerHash);
        }

        if (data.getLockerSize().getSize() == data.getDbLockerSize()) {
            lockerSize = -1;
        } else {
            lockerSize = data.getLockerSize().getSize();
            data.setDbLockerSize(lockerSize);
        }

        if (baseHash.equals(data.getBaseHash())) {
            baseJson = "";
            baseHash = "";
        } else {
            data.setBaseHash(baseHash);
        }

        if (buildHash.equals(data.getBuildHash())) {
            buildJson = "";
            buildHash = "";
        } else {
            data.setBuildHash(buildHash);
        }

        data.setLastInventoryUpdate(System.currentTimeMillis());

        if (backpackHash.isEmpty() && lockerHash.isEmpty() && baseHash.isEmpty() && buildHash.isEmpty() && backpackSize == -1 && lockerSize == -1 && !disconnect) {
            Core.logInfo("Skipped updating " + player.getName() + "'s inventory, no change!");
            return;
        }

        PacketInventoryContent packet = new PacketInventoryContent(player.getUniqueId(), ParkManager.getResort(),
                backpackJson, backpackHash, backpackSize,
                lockerJson, lockerHash, lockerSize,
                baseJson, baseHash,
                buildJson, buildHash);
        packet.setDisconnect(disconnect);
        Core.getDashboardConnection().send(packet);

        Core.logInfo("Inventory packet for " + player.getName() + " generated and sent in " +
                (System.currentTimeMillis() - currentTime) + "ms");
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
     * @implNote Only set storageCheck to true if you're certain the registry has an entry for storageData!
     * @implNote When player is in build mode, this method does nothing!
     */
    public void updateInventory(CPlayer player, boolean storageCheck) {
        if ((!storageCheck && !player.getRegistry().hasEntry("storageData")) || ParkManager.getBuildUtil().isInBuildMode(player))
            return;

        StorageData data = (StorageData) player.getRegistry().getEntry("storageData");

        PlayerInventory inv = player.getInventory();
        inv.clear();
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
            ItemStack[] base = data.getBase();
            for (int i = 0; i < base.length; i++) {
                if (InventoryUtil.isReservedSlot(i)) continue;
                inv.setItem(i, base[i]);
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
        ItemStack[] baseArray = ItemUtil.getInventoryFromJson(packet.getBaseJson());
        ItemStack[] buildArray = ItemUtil.getInventoryFromJson(packet.getBuildJson());

        StorageSize backpackSize = StorageSize.fromInt(packet.getBackpackSize());
        StorageSize lockerSize = StorageSize.fromInt(packet.getLockerSize());

        filterItems(backpackArray);
        filterItems(lockerArray);
        filterItems(baseArray);

        Inventory backpack = Bukkit.createInventory(null, backpackSize.getSlots(), ChatColor.BLUE + "Your Backpack");
        Inventory locker = Bukkit.createInventory(null, backpackSize.getSlots(), ChatColor.BLUE + "Your Locker");

        fillInventory(backpack, backpackSize, backpackArray);
        fillInventory(locker, lockerSize, lockerArray);

        StorageData data = new StorageData(backpack, backpackSize, packet.getBackpackHash(), packet.getBackpackSize(),
                locker, lockerSize, packet.getLockerHash(), packet.getLockerSize(),
                baseArray, packet.getBaseHash(), buildArray, packet.getBuildHash());

        CPlayer player = Core.getPlayerManager().getPlayer(packet.getUuid());
        if (player != null && player.getRegistry().hasEntry("waitingForInventory")) {
            player.getRegistry().removeEntry("waitingForInventory");
            player.getRegistry().addEntry("storageData", data);
            updateInventory(player, true);
        } else {
            savedStorageData.put(packet.getUuid(), data);
        }
    }

    public void logout(CPlayer player) {
        savedStorageData.invalidate(player.getUniqueId());
        updateCachedInventory(player, ParkManager.getBuildUtil().isInBuildMode(player), true);
    }

    private void filterItems(ItemStack[] items) {
        for (int i = 0; i < items.length; i++) {
            if (bannedItems.contains(items[i].getType())) {
                items[i] = null;
            }
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
