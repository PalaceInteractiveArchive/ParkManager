package network.palace.parkmanager.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.core.utils.ItemUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.Resort;
import network.palace.parkmanager.handlers.magicband.MenuType;
import network.palace.parkmanager.handlers.storage.ResortInventory;
import network.palace.parkmanager.handlers.storage.StorageData;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonString;
import org.bson.Document;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;

public class InventoryUtil {

    public void openMenu(CPlayer player, MenuType type) {
        switch (type) {
            case BACKPACK: {
                if (!player.getRegistry().hasEntry("storageData")) return;
                StorageData data = (StorageData) player.getRegistry().getEntry("storageData");
                player.openInventory(data.getBackpack());
                break;
            }
            case LOCKER: {
                if (!player.getRegistry().hasEntry("storageData")) return;
                StorageData data = (StorageData) player.getRegistry().getEntry("storageData");
                player.openInventory(data.getLocker());
                break;
            }
        }
    }

    public InventoryState getInventoryState(CPlayer player) {
        if (!player.getRegistry().hasEntry("inventoryState")) return InventoryState.GUEST;
        return (InventoryState) player.getRegistry().getEntry("inventoryState");
    }

    /**
     * Change to the specified #InventoryState
     *
     * @param player    the player
     * @param nextState the target inventory state
     */
    public void switchToState(CPlayer player, InventoryState nextState) {
        InventoryState currentState = getInventoryState(player);
        if (currentState.equals(nextState)) return;
        exitState(player, currentState);
        enterState(player, nextState);
    }

    /**
     * Set the player's inventory (and potentially GameMode) to the appropriate content for the new state.
     * This method does not save any data regarding the player's previous inventory.
     *
     * @param player the player
     * @param state  the state the player is entering
     * @see #exitState(CPlayer, InventoryState)
     */
    private void enterState(CPlayer player, InventoryState state) {
        player.getRegistry().addEntry("inventoryState", state);
        PlayerInventory inv = player.getInventory();
        StorageData data = (StorageData) player.getRegistry().getEntry("storageData");
        switch (state) {
            case GUEST: {
                boolean flying = player.isFlying();
                player.setGamemode(player.getRank().getRankId() >= Rank.TRAINEEBUILD.getRankId() ? GameMode.SURVIVAL : GameMode.ADVENTURE);

                if (player.getRank().getRankId() >= Rank.TRAINEE.getRankId()) {
                    player.setAllowFlight(true);
                    player.setFlying(flying);
                }

                ParkManager.getStorageManager().updateInventory(player, true);
                if (player.getHeldItemSlot() == 6) ParkManager.getTimeUtil().selectWatch(player);
                if (player.getRank().getRankId() >= Rank.TRAINEEBUILD.getRankId())
                    Core.runTaskAsynchronously(ParkManager.getInstance(), () -> Core.getMongoHandler().setBuildMode(player.getUniqueId(), false));
                ParkManager.getWardrobeManager().setOutfitItems(player);
                break;
            }
            case RIDE:
                player.setGamemode(player.getRank().getRankId() >= Rank.TRAINEEBUILD.getRankId() ? GameMode.SURVIVAL : GameMode.ADVENTURE);
                player.getInventory().clear();
                break;
            case BUILD: {
                player.setGamemode(GameMode.CREATIVE);
                ParkManager.getTimeUtil().unselectWatch(player);

                //Clear inventory and set basic build items
                inv.setContents(new ItemStack[]{ItemUtil.create(Material.COMPASS), ItemUtil.create(Material.WOOD_AXE)});

                ItemStack[] buildContents = data.getBuild();
                //Copy 'buildContents' items into main inventory offset by 2 for compass and WorldEdit wand
                for (int i = 0; i < buildContents.length; i++) {
                    inv.setItem(i + 2, buildContents[i]);
                }
                if (player.getRank().getRankId() >= Rank.TRAINEEBUILD.getRankId())
                    Core.runTaskAsynchronously(ParkManager.getInstance(), () -> Core.getMongoHandler().setBuildMode(player.getUniqueId(), true));
                break;
            }
        }
    }

    /**
     * Store any necessary data about the inventory state in preparation to switch to another state.
     * For example, store Build inventory data when switching from the Build to Guest state.
     * This method does not make any actual changes to the player's inventory.
     *
     * @param player the player
     * @param state  the state the player is exiting
     */
    private void exitState(CPlayer player, InventoryState state) {
        PlayerInventory inv = player.getInventory();
        StorageData data = (StorageData) player.getRegistry().getEntry("storageData");
        switch (state) {
            case GUEST: {
                ParkManager.getTimeUtil().unselectWatch(player);
                ItemStack[] invContents = inv.getStorageContents();
                ItemStack[] base = new ItemStack[36];
                //Store current inventory items (except reserved slots) into 'base' array
                for (int i = 0; i < invContents.length; i++) {
                    if (InventoryUtil.isReservedSlot(i)) continue;
                    base[i] = invContents[i];
                }
                data.setBase(base);
                break;
            }
            case RIDE:
                break;
            case BUILD: {
                ItemStack[] build = new ItemStack[34];
                ItemStack[] invContents = inv.getStorageContents();
                //Store current inventory items into 'build' array
                if (invContents.length - 2 >= 0) System.arraycopy(invContents, 2, build, 0, invContents.length - 2);
                data.setBuild(build);
                break;
            }
        }
    }

    /**
     * Set the player to the provided state on join
     *
     * @param player the player
     * @param state  the target inventory state
     */
    public void handleJoin(CPlayer player, InventoryState state) {
        enterState(player, state);
    }

    /**
     * Determine whether the slot provided is reserved for static items in 'guest' mode
     *
     * @param slot the slot
     * @return true if slot is reserved for static items in 'guest' mode
     */
    public static boolean isReservedSlot(int slot) {
        return slot >= 5 && slot <= 8;
    }

    @SuppressWarnings("rawtypes")
    public ResortInventory getResortInventoryFromDocument(UUID uuid, Document inv, Resort resort) {
        StringBuilder backpack = new StringBuilder("[");
        ArrayList packcontents = inv.get("backpack", ArrayList.class);
        for (int i = 0; i < packcontents.size(); i++) {
            Document item = (Document) packcontents.get(i);
            if (!item.containsKey("amount") || !(item.get("amount") instanceof Integer)) {
                backpack.append("{}");
            } else {
                backpack.append("{type:'").append(item.getString("type"))
                        .append("',data:").append(item.getInteger("data"))
                        .append(",amount:").append(item.getInteger("amount"))
                        .append(",tag:'").append(item.getString("tag")).append("'}");
            }
            if (i < (packcontents.size() - 1)) {
                backpack.append(",");
            }
        }
        backpack.append("]");
        StringBuilder locker = new StringBuilder("[");
        ArrayList lockercontents = inv.get("locker", ArrayList.class);
        for (int i = 0; i < lockercontents.size(); i++) {
            Document item = (Document) lockercontents.get(i);
            if (!item.containsKey("amount") || !(item.get("amount") instanceof Integer)) {
                locker.append("{}");
            } else {
                locker.append("{type:'").append(item.getString("type"))
                        .append("',data:").append(item.getInteger("data"))
                        .append(",amount:").append(item.getInteger("amount"))
                        .append(",tag:'").append(item.getString("tag")).append("'}");
            }
            if (i < (lockercontents.size() - 1)) {
                locker.append(",");
            }
        }
        locker.append("]");
        StringBuilder base = new StringBuilder("[");
        ArrayList basecontents = inv.get("base", ArrayList.class);
        for (int i = 0; i < basecontents.size(); i++) {
            Document item = (Document) basecontents.get(i);
            if (!item.containsKey("amount") || !(item.get("amount") instanceof Integer)) {
                base.append("{}");
            } else {
                base.append("{type:'").append(item.getString("type"))
                        .append("',data:").append(item.getInteger("data"))
                        .append(",amount:").append(item.getInteger("amount"))
                        .append(",tag:'").append(item.getString("tag")).append("'}");
            }
            if (i < (basecontents.size() - 1)) {
                base.append(",");
            }
        }
        base.append("]");
        StringBuilder build = new StringBuilder("[");
        ArrayList buildcontents = inv.get("build", ArrayList.class);
        for (int i = 0; i < buildcontents.size(); i++) {
            Document item = (Document) buildcontents.get(i);
            if (!item.containsKey("amount") || !(item.get("amount") instanceof Integer)) {
                build.append("{}");
            } else {
                build.append("{type:'").append(item.getString("type"))
                        .append("',data:").append(item.getInteger("data"))
                        .append(",amount:").append(item.getInteger("amount"))
                        .append(",tag:'").append(item.getString("tag")).append("'}");
            }
            if (i < (buildcontents.size() - 1)) {
                build.append(",");
            }
        }
        build.append("]");
        int backpacksize = inv.getInteger("backpacksize");
        int lockersize = inv.getInteger("lockersize");
        return new ResortInventory(resort, backpack.toString(), generateHash(backpack.toString()), "", backpacksize,
                locker.toString(), generateHash(locker.toString()), "", lockersize,
                base.toString(), generateHash(base.toString()), "",
                build.toString(), generateHash(build.toString()), "");
    }

    /**
     * Generate hash for inventory JSON
     *
     * @param inventory the JSON
     * @return MD5 hash of inventory
     */
    private String generateHash(String inventory) {
        if (inventory == null) {
            inventory = "";
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(inventory.getBytes());
            return DatatypeConverter.printHexBinary(digest.digest()).toLowerCase();
        } catch (NoSuchAlgorithmException e) {
            Core.getInstance().getLogger().log(Level.SEVERE, "Error generaing inventory hash", e);
            return "null";
        }
    }

    /**
     * Convert a JSON string to a BsonDocument
     *
     * @param json the JSON string
     * @return a Bsondocument
     */
    public static BsonDocument getBsonFromJson(String json) {
        JsonObject o = new JsonParser().parse(json).getAsJsonObject();
        if (!o.has("type")) {
            return new BsonDocument();
        }
        BsonDocument doc;
        try {
            doc = new BsonDocument("type", new BsonString(o.get("type").getAsString()))
                    .append("data", new BsonInt32(o.get("data").getAsInt()))
                    .append("amount", new BsonInt32(o.get("amount").getAsInt()))
                    .append("tag", o.get("tag") == null ? new BsonString("") : new BsonString(o.get("tag").getAsString()));
        } catch (IllegalArgumentException e) {
            Core.getInstance().getLogger().log(Level.SEVERE, "Error converting Json to Bson", e);
            return null;
        }
        return doc;
    }

    public enum InventoryState {
        GUEST, RIDE, BUILD
    }
}
