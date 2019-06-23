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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class StorageManager {
    private static final List<Material> bannedItems = Arrays.asList(Material.MINECART, Material.SNOWBALL, Material.ARROW);
    private Cache<UUID, StorageData> savedStorageData = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).build();
    // Map used to store players that need their inventory set after joining
    // Boolean represents build mode
    private HashMap<UUID, Boolean> joinList = new HashMap<>();

    public void initialize() {
        Core.runTaskTimer(ParkManager.getInstance(), () -> Core.getPlayerManager().getOnlinePlayers().forEach(this::updateCachedInventory), 0L, 1200L);
        Core.runTaskTimer(ParkManager.getInstance(), () -> {
            if (joinList.isEmpty()) return;
            HashMap<UUID, Boolean> map = new HashMap<>(joinList);
            joinList.clear();
            map.forEach((uuid, build) -> {
                CPlayer player = Core.getPlayerManager().getPlayer(uuid);
                if (player == null) return;
                ParkManager.getInventoryUtil().handleJoin(player, build ? InventoryUtil.InventoryState.BUILD : InventoryUtil.InventoryState.GUEST);
            });
        }, 0L, 10L);
    }

    /**
     * Handle inventory setting when a player joins
     *
     * @param player    the player
     * @param buildMode whether the player should join in build mode
     */
    public void handleJoin(CPlayer player, boolean buildMode) {
        StorageData data = savedStorageData.getIfPresent(player.getUniqueId());
        if (data == null) {
            player.getRegistry().addEntry("waitingForInventory", true);
            return;
        }
        savedStorageData.invalidate(player.getUniqueId());
        player.getRegistry().addEntry("storageData", data);
        joinList.put(player.getUniqueId(), buildMode);
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
        ItemStack[] base;
        ItemStack[] build;

        PlayerInventory inv = player.getInventory();
        ItemStack[] invContents = inv.getStorageContents();

        switch (ParkManager.getInventoryUtil().getInventoryState(player)) {
            case GUEST: {
                base = new ItemStack[36];
                //Store current inventory items (except reserved slots) into 'base' array
                for (int i = 0; i < invContents.length; i++) {
                    if (InventoryUtil.isReservedSlot(i)) continue;
                    base[i] = invContents[i];
                }
                build = data.getBuild();
                break;
            }
            case BUILD: {
                build = new ItemStack[34];
                //Store current inventory items into 'build' array
                if (invContents.length - 2 >= 0) System.arraycopy(invContents, 2, build, 0, invContents.length - 2);
                base = data.getBase();
                break;
            }
            default: {
                base = data.getBase();
                build = data.getBuild();
                break;
            }
        }

        Core.runTaskAsynchronously(ParkManager.getInstance(), () -> {
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

            if (!disconnect && backpackHash.isEmpty() && lockerHash.isEmpty() && baseHash.isEmpty() && buildHash.isEmpty() && backpackSize == -1 && lockerSize == -1) {
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
        });
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
        long time = System.currentTimeMillis();
        if ((!storageCheck && !player.getRegistry().hasEntry("storageData")) || ParkManager.getBuildUtil().isInBuildMode(player))
            return;

        StorageData data = (StorageData) player.getRegistry().getEntry("storageData");

        PlayerInventory inv = player.getInventory();
        inv.clear();
        ItemStack compass = player.getRank().getRankId() >= Rank.MOD.getRankId() ? ItemUtil.create(Material.COMPASS) : null;
        ItemStack[] contents = new ItemStack[]{
                compass, null, null, null, null,
                ItemUtil.create(Material.CHEST, ChatColor.GREEN + "Backpack " + ChatColor.GRAY + "(Right-Click)"),
                ItemUtil.create(Material.CLOCK, ChatColor.GREEN + "Watch " + ChatColor.GRAY + "(Right-Click)",
                        Arrays.asList(ChatColor.GRAY + "Right-Click to open", ChatColor.GRAY + "the Show Timetable")),
                null, ParkManager.getMagicBandManager().getMagicBandItem(player),
                null, null, null, null, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null, null, null, null, null
        };

        if (data != null) {
            ItemStack[] base = data.getBase();
            for (int i = player.getRank().getRankId() >= Rank.MOD.getRankId() ? 1 : 0; i < base.length; i++) {
                if (InventoryUtil.isReservedSlot(i)) continue;
                contents[i] = base[i];
//                inv.setItem(i, base[i]);
            }
        }
        inv.setContents(contents);
        Core.runTaskAsynchronously(ParkManager.getInstance(), () -> {
            ParkManager.getAutographManager().updateAutographs(player);
            ParkManager.getAutographManager().giveBook(player);
        });

        if (player.getRank().getRankId() >= Rank.SPECIALGUEST.getRankId()) {
            Core.runTask(ParkManager.getInstance(), () -> player.setAllowFlight(true));
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
