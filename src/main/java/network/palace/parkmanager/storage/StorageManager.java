package network.palace.parkmanager.storage;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.client.FindIterable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import network.palace.core.Core;
import network.palace.core.events.IncomingMessageEvent;
import network.palace.core.menu.Menu;
import network.palace.core.menu.MenuButton;
import network.palace.core.messagequeue.ConnectionType;
import network.palace.core.messagequeue.MessageClient;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.core.utils.ItemUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.Resort;
import network.palace.parkmanager.handlers.storage.ResortInventory;
import network.palace.parkmanager.handlers.storage.StorageData;
import network.palace.parkmanager.handlers.storage.StorageSize;
import network.palace.parkmanager.message.ParkStorageLockPacket;
import network.palace.parkmanager.utils.HashUtil;
import network.palace.parkmanager.utils.InventoryUtil;
import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.logging.Level;

public class StorageManager {
    private static final List<Material> bannedItems = Arrays.asList(Material.MINECART, Material.SNOW_BALL, Material.ARROW);
    // Map used to store players that need their inventory set after joining
    // Boolean represents build mode
    private final HashMap<UUID, Boolean> joinList = new HashMap<>();
    // List of players waiting for previous server to finish uploading inventory
    // If this takes more than 5 seconds, request player to disconnect and reconnect
    private final HashMap<UUID, Long> waitingForInventory = new HashMap<>();
    private final ItemStack backpack;

    public StorageManager() {
        if (ParkManager.getResort().equals(Resort.WDW)) {
            backpack = ItemUtil.create(Material.DIAMOND_HOE, ChatColor.GREEN + "Backpack " + ChatColor.GRAY + "(Right-Click)", 47);
            ItemMeta meta = backpack.getItemMeta();
            meta.setUnbreakable(true);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
            backpack.setItemMeta(meta);
        } else {
            backpack = ItemUtil.create(Material.CHEST, ChatColor.GREEN + "Backpack " + ChatColor.GRAY + "(Right-Click)");
        }
    }

    public void initialize() {
        Core.runTaskTimer(ParkManager.getInstance(), () -> Core.getPlayerManager().getOnlinePlayers().forEach(this::updateCachedInventory), 0L, 1200L);
        Core.runTaskTimer(ParkManager.getInstance(), () -> {
            if (!joinList.isEmpty()) {
                HashMap<UUID, Boolean> map = new HashMap<>(joinList);
                joinList.clear();
                map.forEach((uuid, build) -> {
                    CPlayer player = Core.getPlayerManager().getPlayer(uuid);
                    if (player == null) return;
                    ParkManager.getInventoryUtil().handleJoin(player, build ? InventoryUtil.InventoryState.BUILD : InventoryUtil.InventoryState.GUEST);
                });
            }
            if (!waitingForInventory.isEmpty()) {
                List<UUID> toRemove = new ArrayList<>();
                List<UUID> toUpdate = new ArrayList<>();
                for (Map.Entry<UUID, Long> entry : waitingForInventory.entrySet()) {
                    if (System.currentTimeMillis() - entry.getValue() >= 5000) {
                        // If it's taking more than 5 seconds to load their inventory, suggest they reconnect
                        CPlayer player = Core.getPlayerManager().getPlayer(entry.getKey());
                        if (player == null) {
                            toRemove.add(entry.getKey());
                        } else {
                            player.sendMessage(" ");
                            player.sendMessage(" ");
                            player.sendMessage(ChatColor.YELLOW + "We're having some trouble loading your inventory items.\n" +
                                    ChatColor.LIGHT_PURPLE + "Please disconnect and reconnect to Palace Network to fix this.\n" +
                                    ChatColor.AQUA + "We apologize for the inconvenience. If you encounter any further issues, reach out to a staff member with " + ChatColor.GREEN + "/helpme.");
                            player.sendMessage(" ");
                            player.sendMessage(" ");
                            toUpdate.add(entry.getKey());
                        }
                    }
                }
                toRemove.forEach(waitingForInventory::remove);
                toUpdate.forEach(uuid -> waitingForInventory.put(uuid, System.currentTimeMillis() + 5000));
            }
        }, 0L, 10L);
        try {
            MessageClient all_parks = new MessageClient(ConnectionType.PUBLISHING, "all_parks", "fanout");
            Core.getMessageHandler().permanentClients.put("all_parks", all_parks);
            Core.getMessageHandler().registerConsumer("all_parks", "fanout", "", (consumerTag, delivery) -> {
                try {
                    JsonObject object = Core.getMessageHandler().parseDelivery(delivery);
                    Core.debugLog(object.toString());
                    int id = object.get("id").getAsInt();
                    try {
                        new IncomingMessageEvent(id, object).call();
                    } catch (Exception e) {
                        Core.logMessage("MessageHandler", "Error processing IncomingMessageEvent for incoming packet " + object.toString());
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    Core.getMessageHandler().handleError(consumerTag, delivery, e);
                }
            }, t -> {
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handle inventory setting when a player joins
     *
     * @param player    the player
     * @param buildMode whether the player should join in build mode
     */
    public void handleJoin(CPlayer player, boolean buildMode) {
        Object o = Core.getMongoHandler().getOnlineDataValue(player.getUniqueId(), "parkStorageLock");
        String parkStorageLock = o == null ? "" : (String) o;
        if (parkStorageLock.isEmpty() || parkStorageLock.equals(Core.getInstanceName())) {
            // Player is either not coming from another park server, or that park server has finished saving the player's inventory
            Core.getMongoHandler().setOnlineDataValue(player.getUniqueId(), "parkStorageLock", Core.getInstanceName());
            StorageData data = getStorageDataFromDatabase(player.getUniqueId());
            player.getRegistry().addEntry("storageData", data);
            joinList.put(player.getUniqueId(), buildMode);
        } else {
            // Player is coming from another park server which is still saving the player's inventory
            player.getRegistry().addEntry("waitingForInventory", true);
            player.getRegistry().addEntry("waitingForInventory_BuildSetting", buildMode);
            waitingForInventory.put(player.getUniqueId(), System.currentTimeMillis() + 5000);
        }
    }

    public void joinLate(UUID uuid) {
        waitingForInventory.remove(uuid);
        CPlayer player = Core.getPlayerManager().getPlayer(uuid);
        if (player == null || !player.getRegistry().hasEntry("waitingForInventory")) return;
        Core.getMongoHandler().setOnlineDataValue(player.getUniqueId(), "parkStorageLock", Core.getInstanceName());
        StorageData data = getStorageDataFromDatabase(player.getUniqueId());
        player.getRegistry().addEntry("storageData", data);
        player.getRegistry().removeEntry("waitingForInventory");
        boolean build = (boolean) player.getRegistry().removeEntry("waitingForInventory_BuildSetting");
        joinList.put(player.getUniqueId(), build);
    }

    public StorageData getStorageDataFromDatabase(UUID uuid) {
        Document storage = Core.getMongoHandler().getParkData(uuid, ParkManager.getResort().name().toLowerCase() + "_storage");

        ResortInventory resortInventory;
        if (storage == null) {
            FindIterable<Document> oldStorageDocuments = Core.getMongoHandler().getOldStorageDocuments(uuid);
            Document doc = oldStorageDocuments.first();
            if (doc == null || !doc.containsKey(ParkManager.getResort().name().toLowerCase())) {
                resortInventory = new ResortInventory(ParkManager.getResort(), "", "", "", StorageSize.SMALL.getSize(), "", "", "", StorageSize.SMALL.getSize(), "", "", "", "", "", "");
            } else {
                resortInventory = ParkManager.getInventoryUtil().getResortInventoryFromDocument(uuid, doc.get(ParkManager.getResort().name().toLowerCase(), Document.class), ParkManager.getResort());
            }
        } else {
            resortInventory = ParkManager.getInventoryUtil().getResortInventoryFromDocument(uuid, storage, ParkManager.getResort());
        }

        return resortInventory.toStorageData();
    }

    /**
     * Update the player's inventory stored in the database
     *
     * @param player the player
     * @see #updateCachedInventory(CPlayer, boolean)
     */
    public void updateCachedInventory(CPlayer player) {
        updateCachedInventory(player, false);
    }

    /**
     * Update the player's inventory stored in the database
     *
     * @param player     the player
     * @param disconnect if this update is being sent because the player just disconnected
     * @implNote if disconnect is true, typical the 5-minute delay between updates is ignored and the update is sent anyway
     */
    public void updateCachedInventory(CPlayer player, boolean disconnect) {
        if (!player.getRegistry().hasEntry("storageData")) return;
        StorageData data = (StorageData) player.getRegistry().getEntry("storageData");

        if (!disconnect && System.currentTimeMillis() - data.getLastInventoryUpdate() < (5 * 60 * 1000)) return;
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
            String backpackJson = ItemUtil.getJsonFromInventoryNew(backpackInventory).toString();
            String backpackHash = HashUtil.generateHash(backpackJson);
            int backpackSize = data.getBackpackSize().getSize();

            String lockerJson = ItemUtil.getJsonFromInventoryNew(data.getLocker()).toString();
            String lockerHash = HashUtil.generateHash(lockerJson);
            int lockerSize = data.getLockerSize().getSize();

            String baseJson = ItemUtil.getJsonFromArrayNew(base).toString();
            String baseHash = HashUtil.generateHash(baseJson);

            String buildJson = ItemUtil.getJsonFromArrayNew(build).toString();
            String buildHash = HashUtil.generateHash(buildJson);

            data.setLastInventoryUpdate(System.currentTimeMillis());

            if (!disconnect && backpackHash.isEmpty() && lockerHash.isEmpty() && baseHash.isEmpty() && buildHash.isEmpty() && backpackSize == -1 && lockerSize == -1) {
                Core.logInfo("Skipped updating " + player.getName() + "'s inventory, no change!");
                return;
            }

            UpdateData updateData = getDataFromJson(backpackJson, backpackSize, lockerJson, lockerSize, baseJson, buildJson);

            Document doc = new Document("backpack", updateData.getPack()).append("backpacksize", updateData.getPackSize())
                    .append("locker", updateData.getLocker()).append("lockersize", updateData.getLockerSize())
                    .append("base", updateData.getBase()).append("build", updateData.getBuild())
                    .append("last-updated", System.currentTimeMillis());
            Core.getMongoHandler().setParkStorage(player.getUniqueId(), ParkManager.getResort().name().toLowerCase() + "_storage", doc);
            if (disconnect) {
                Core.getMongoHandler().setOnlineDataValueConcurrentSafe(player.getUniqueId(), "parkStorageLock", null, Core.getInstanceName());
                try {
                    Core.getMessageHandler().sendMessage(new ParkStorageLockPacket(player.getUniqueId(), Core.getInstanceName(), false), Core.getMessageHandler().permanentClients.get("all_parks"));
                } catch (Exception e) {
                    Core.getInstance().getLogger().log(Level.SEVERE, "Error while sending ParkStorageLockPacket to all_parks", e);
                }
            }

            Core.logInfo("Inventory packet for " + player.getName() + " generated and sent in " +
                    (System.currentTimeMillis() - currentTime) + "ms");
        });
    }

    /**
     * Update the player's physical inventory depending on their build state
     * This method makes sure the player's inventory matches what's stored in storageData
     *
     * @param player the player
     * @see #updateInventory(CPlayer, boolean)
     */
    public void updateInventory(CPlayer player) {
        updateInventory(player, false);
    }

    /**
     * Update the player's physical inventory depending on their build state
     * This method makes sure the player's inventory matches what's stored in storageData
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
                compass, null, null, null, null, backpack,
                ItemUtil.create(Material.WATCH, ChatColor.GREEN + "Watch " + ChatColor.GRAY + "(Right-Click)",
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
            }
        }
        inv.setContents(contents);
        ParkManager.getWardrobeManager().setOutfitItems(player);
        Core.runTaskAsynchronously(ParkManager.getInstance(), () -> {
            ParkManager.getAutographManager().updateAutographs(player);
            ParkManager.getAutographManager().giveBook(player);
        });

        if (player.getRank().getRankId() >= Rank.TRAINEE.getRankId())
            Core.runTask(ParkManager.getInstance(), () -> player.setAllowFlight(true));
    }

    /**
     * Process a player logging out
     * Force-update the player's full inventory to the database
     *
     * @param uuid the uuid
     */
    public void logout(UUID uuid) {
        waitingForInventory.remove(uuid);
        CPlayer player = Core.getPlayerManager().getPlayer(uuid);
        if (player == null) return;
        updateCachedInventory(player, true);
    }

    /**
     * Remove banned items from an array
     *
     * @param items an array of items
     */
    public static void filterItems(ItemStack[] items) {
        for (int i = 0; i < items.length; i++) {
            if (bannedItems.contains(items[i].getType())) {
                items[i] = null;
            }
        }
    }

    /**
     * Fill an inventory with the provided items, up to the StorageSize limit
     *
     * @param inventory the inventory
     * @param size      the inventory's StorageSize
     * @param items     the items
     */
    public static void fillInventory(Inventory inventory, StorageSize size, ItemStack[] items) {
        if (items.length == size.getSlots()) {
            inventory.setContents(items);
        } else {
            ItemStack[] arr = new ItemStack[size.getSlots()];

            System.arraycopy(items, 0, arr, 0, Math.min(items.length, size.getSlots()));

            inventory.setContents(arr);
        }
    }

    public void buyUpgrade(CPlayer player, Material type) {
        boolean backpack = type.equals(Material.CHEST);
        if (!backpack && !type.equals(Material.ENDER_CHEST)) return;
        new Menu(27, ChatColor.BLUE + "Confirm Upgrade", player, Arrays.asList(
                new MenuButton(4,
                        ItemUtil.create(Material.CHEST, ChatColor.GREEN + "Storage Upgrade", Arrays.asList(
                                ChatColor.YELLOW + "3 rows ➠ 6 rows", ChatColor.GRAY + "Purchase a " + (backpack ? "backpack" : "locker"),
                                ChatColor.GRAY + "upgrade for " + ChatColor.GREEN + "$250"
                        ))
                ),
                new MenuButton(11,
                        ItemUtil.create(Material.STAINED_CLAY, ChatColor.GREEN + "Decline", 14),
                        ImmutableMap.of(ClickType.LEFT, p -> {
                            p.sendMessage(ChatColor.RED + "Cancelled transaction!");
                            p.closeInventory();
                        })
                ),
                new MenuButton(15,
                        ItemUtil.create(Material.STAINED_CLAY, ChatColor.GREEN + "Confirm", 13),
                        ImmutableMap.of(ClickType.LEFT, p -> {
                            p.sendMessage(ChatColor.GREEN + "Processing transaction...");
                            if (p.getBalance() < 250) {
                                p.sendMessage(ChatColor.RED + "You cannot afford this upgrade!");
                                p.closeInventory();
                            } else {
                                Core.runTaskAsynchronously(ParkManager.getInstance(), () -> {
                                    p.addBalance(-250, (backpack ? "Backpack" : "Locker") + " Upgrade");
                                    StorageData data = (StorageData) p.getRegistry().getEntry("storageData");
                                    if (backpack) {
                                        data.setBackpackSize(StorageSize.LARGE);
                                    } else {
                                        data.setLockerSize(StorageSize.LARGE);
                                    }
                                    Core.runTask(ParkManager.getInstance(), () -> {
                                        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                                        p.closeInventory();
                                        p.sendMessage(ChatColor.GREEN + "Successfully processed storage upgrade!");
                                        Inventory newInv = Bukkit.createInventory(null, StorageSize.LARGE.getSlots(), ChatColor.BLUE + "Your " + (backpack ? "Backpack" : "Locker"));
                                        newInv.setContents((backpack ? data.getBackpack() : data.getLocker()).getContents());
                                        StorageData newData = new StorageData(
                                                backpack ? newInv : data.getBackpack(),
                                                backpack ? StorageSize.LARGE : data.getBackpackSize(),
                                                data.getBackpackHash(),
                                                data.getDbBackpackSize(),
                                                backpack ? data.getLocker() : newInv,
                                                backpack ? data.getLockerSize() : StorageSize.LARGE,
                                                data.getLockerHash(),
                                                data.getDbLockerSize(),
                                                data.getBase(),
                                                data.getBaseHash(),
                                                data.getBuild(),
                                                data.getBuildHash()
                                        );
                                        p.getRegistry().addEntry("storageData", newData);
                                    });
                                });
                            }
                        })
                )
        )).open();
    }

    /**
     * Create an UpdateData object based on the provided inventory JSON
     *
     * @param backpackJSON the backpack JSON
     * @param backpackSize the backpack size
     * @param lockerJSON   the locker JSON
     * @param lockerSize   the locker size
     * @param baseJSON     the base JSON
     * @param buildJSON    the build JSON
     * @return an UpdateData object based on the provided inventory JSON
     */
    public static UpdateData getDataFromJson(String backpackJSON, int backpackSize, String lockerJSON, int lockerSize, String baseJSON, String buildJSON) {
        BsonArray pack = jsonToArray(backpackJSON);
        BsonArray locker = jsonToArray(lockerJSON);
        BsonArray base = jsonToArray(baseJSON);
        BsonArray build = jsonToArray(buildJSON);

        return new UpdateData(pack, backpackSize, locker, lockerSize, base, build);
    }

    /**
     * Create a BsonArray from the provided JSON string
     *
     * @param json the JSON string
     * @return the BsonArray
     */
    public static BsonArray jsonToArray(String json) {
        BsonArray array = new BsonArray();
        if (json == null || json.isEmpty()) return array;
        JsonElement element = new JsonParser().parse(json);
        if (element.isJsonArray()) {
            JsonArray baseArray = element.getAsJsonArray();

            int i = 0;
            for (JsonElement e2 : baseArray) {
                JsonObject o = e2.getAsJsonObject();
                BsonDocument item = InventoryUtil.getBsonFromJson(o.toString());
                array.add(item);
                i++;
            }
        }
        return array;
    }

    @Getter
    @AllArgsConstructor
    public static class UpdateData {
        private final BsonArray pack;
        private final int packSize;
        private final BsonArray locker;
        private final int lockerSize;
        private final BsonArray base;
        private final BsonArray build;
    }
}
