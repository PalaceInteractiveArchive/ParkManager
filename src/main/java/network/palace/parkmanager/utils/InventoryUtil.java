package network.palace.parkmanager.utils;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.core.utils.ItemUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.magicband.MenuType;
import network.palace.parkmanager.handlers.storage.StorageData;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

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

                if (player.getRank().getRankId() >= Rank.SPECIALGUEST.getRankId()) {
                    player.setAllowFlight(true);
                    player.setFlying(flying);
                }

                ParkManager.getStorageManager().updateInventory(player, true);
                if (player.getHeldItemSlot() == 6) ParkManager.getTimeUtil().selectWatch(player);
                if (player.getRank().getRankId() >= Rank.TRAINEEBUILD.getRankId())
                    Core.runTaskAsynchronously(() -> Core.getMongoHandler().setBuildMode(player.getUniqueId(), false));
                break;
            }
            case RIDE:
                player.setGamemode(player.getRank().getRankId() >= Rank.TRAINEEBUILD.getRankId() ? GameMode.SURVIVAL : GameMode.ADVENTURE);
                player.getInventory().clear();
                break;
            case BUILD: {
                player.setGamemode(GameMode.CREATIVE);

                //Clear inventory and set basic build items
                inv.setContents(new ItemStack[]{ItemUtil.create(Material.COMPASS), ItemUtil.create(Material.WOODEN_AXE)});

                ItemStack[] buildContents = data.getBuild();
                //Copy 'buildContents' items into main inventory offset by 2 for compass and WorldEdit wand
                for (int i = 0; i < buildContents.length; i++) {
                    inv.setItem(i + 2, buildContents[i]);
                }
                if (player.getRank().getRankId() >= Rank.TRAINEEBUILD.getRankId())
                    Core.runTaskAsynchronously(() -> Core.getMongoHandler().setBuildMode(player.getUniqueId(), true));
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

    public enum InventoryState {
        GUEST, RIDE, BUILD
    }
}
