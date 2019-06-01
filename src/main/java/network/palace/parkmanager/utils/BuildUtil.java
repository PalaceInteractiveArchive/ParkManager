package network.palace.parkmanager.utils;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.parkmanager.ParkManager;
import org.bukkit.ChatColor;

import java.util.UUID;

public class BuildUtil {

    public boolean isInBuildMode(UUID uuid) {
        return isInBuildMode(Core.getPlayerManager().getPlayer(uuid));
    }

    public boolean isInBuildMode(CPlayer player) {
        return ParkManager.getInventoryUtil().getInventoryState(player).equals(InventoryUtil.InventoryState.BUILD);
    }

    /**
     * Toggle the player's build mode status
     *
     * @param player the player
     * @return true if the toggle was successful
     */
    public boolean toggleBuildMode(CPlayer player) {
        InventoryUtil.InventoryState state = ParkManager.getInventoryUtil().getInventoryState(player);
        if (state.equals(InventoryUtil.InventoryState.RIDE)) {
            player.sendMessage(ChatColor.RED + "You cannot toggle Build Mode while on a ride!");
            return false;
        }
        if (state.equals(InventoryUtil.InventoryState.BUILD)) {
            ParkManager.getInventoryUtil().switchToState(player, InventoryUtil.InventoryState.GUEST);
            player.sendMessage(ChatColor.YELLOW + "You have exited Build Mode");
        } else {
            ParkManager.getInventoryUtil().switchToState(player, InventoryUtil.InventoryState.BUILD);
            player.sendMessage(ChatColor.YELLOW + "You have entered Build Mode");
        }
        return true;
    }

    public boolean canToggleBuildMode(CPlayer player) {
        return player.getRank().getRankId() >= Rank.TRAINEEBUILD.getRankId();
    }
}
