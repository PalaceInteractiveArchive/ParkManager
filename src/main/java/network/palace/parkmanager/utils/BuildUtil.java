package network.palace.parkmanager.utils;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.core.utils.ItemUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.storage.StorageData;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.UUID;

public class BuildUtil {

    public boolean isInBuildMode(UUID uuid) {
        return isInBuildMode(Core.getPlayerManager().getPlayer(uuid));
    }

    public boolean isInBuildMode(CPlayer player) {
        if (player == null || !player.getRegistry().hasEntry("buildMode")) return false;
        return (boolean) player.getRegistry().getEntry("buildMode");
    }

    /**
     * Toggle the player's build mode status
     *
     * @param player the player
     * @return true if entering build mode, false if exiting it
     */
    public boolean toggleBuildMode(CPlayer player) {
        return toggleBuildMode(player, false);
    }

    /**
     * Toggle the player's build mode status
     *
     * @param player the player
     * @param join   true if this is setting the player to build mode as they're joining, false if setting after they've been online
     * @return true if entering build mode, false if exiting it
     * @implNote IMPORTANT: join should only be set to true when setting the player to build mode on join
     */
    public boolean toggleBuildMode(CPlayer player, boolean join) {
        if (player == null || !player.getRegistry().hasEntry("storageData")) return false;

        boolean newSetting = !isInBuildMode(player);
        player.getRegistry().addEntry("buildMode", newSetting);

        PlayerInventory inv = player.getInventory();
        StorageData data = (StorageData) player.getRegistry().getEntry("storageData");
        if (newSetting) {
            //Player is moving to build mode
            player.setGamemode(GameMode.CREATIVE);

            ItemStack[] invContents = inv.getStorageContents();

            if (!join) {
                ItemStack[] base = new ItemStack[36];
                //Store current inventory items (except reserved slots) into 'base' array
                for (int i = 0; i < invContents.length; i++) {
                    if (InventoryUtil.isReservedSlot(i)) continue;
                    base[i] = invContents[i];
                }
                data.setBase(base);
            }

            //Clear inventory and set basic build items
            inv.setContents(new ItemStack[]{ItemUtil.create(Material.COMPASS), ItemUtil.create(Material.WOODEN_AXE)});

            ItemStack[] buildContents = data.getBuild();
            //Copy 'buildContents' items into main inventory offset by 2 for compass and WorldEdit wand
            for (int i = 0; i < buildContents.length; i++) {
                inv.setItem(i + 2, buildContents[i]);
            }
        } else {
            //Player is leaving build mode
            player.setGamemode(player.getRank().getRankId() >= Rank.MOD.getRankId() ? GameMode.SURVIVAL : GameMode.ADVENTURE);

            ItemStack[] build = new ItemStack[34];
            ItemStack[] invContents = inv.getStorageContents();
            //Store current inventory items into 'build' array
            if (invContents.length - 2 >= 0) System.arraycopy(invContents, 2, build, 0, invContents.length - 2);
            data.setBuild(build);

            ParkManager.getStorageManager().updateInventory(player, true);
        }
        Core.runTaskAsynchronously(() -> Core.getMongoHandler().setBuildMode(player.getUniqueId(), newSetting));
        return newSetting;
    }

    public boolean canToggleBuildMode(CPlayer player) {
        return player.getRank().getRankId() >= Rank.TRAINEEBUILD.getRankId();
    }
}
