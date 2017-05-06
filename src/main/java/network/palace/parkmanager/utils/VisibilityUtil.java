package network.palace.parkmanager.utils;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.PlayerData;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Marc on 1/14/17.
 */
public class VisibilityUtil {
    private List<UUID> hideall = new ArrayList<>();

    private void hidePlayerForOthers(CPlayer player) {
        for (CPlayer tp : Core.getPlayerManager().getOnlinePlayers()) {
            if (tp.getUniqueId().equals(player.getUniqueId())) {
                continue;
            }
            tp.hidePlayer(player);
        }
    }

    private void showPlayerForOthers(CPlayer player) {
        for (CPlayer tp : Core.getPlayerManager().getOnlinePlayers()) {
            if (tp.getUniqueId().equals(player.getUniqueId())) {
                continue;
            }
            if (hideall.contains(tp.getUniqueId())) {
                continue;
            }
            if (ParkManager.getPlayerData(tp.getUniqueId()).getVisibility()) {
                tp.showPlayer(player);
            }
        }
    }

    public void logout(CPlayer player) {
        hideall.remove(player.getUniqueId());
    }

    public void addToHideAll(CPlayer player) {
        PlayerData data = ParkManager.getPlayerData(player.getUniqueId());
        List<UUID> friends = data.getFriendList();
        hideall.add(player.getUniqueId());
        for (CPlayer tp : Core.getPlayerManager().getOnlinePlayers()) {
            if (friends.contains(tp.getUniqueId()) && tp.getRank().getRankId() < Rank.SPECIALGUEST.getRankId() ||
                    tp.getUniqueId().equals(player.getUniqueId())) {
                continue;
            }
            if (tp.getRank().getRankId() < Rank.SPECIALGUEST.getRankId()) {
                player.hidePlayer(tp);
            }
        }
    }

    public void removeFromHideAll(CPlayer player) {
        PlayerData data = ParkManager.getPlayerData(player.getUniqueId());
        List<UUID> friends = data.getFriendList();
        hideall.remove(player.getUniqueId());
        for (CPlayer tp : Core.getPlayerManager().getOnlinePlayers()) {
            if (tp.getUniqueId().equals(player.getUniqueId())) {
                continue;
            }
            if (tp.getRank().getRankId() < Rank.SPECIALGUEST.getRankId()) {
                player.showPlayer(tp);
            }
        }
    }

    public boolean isInHideAll(UUID uuid) {
        return hideall.contains(uuid);
    }

    public void login(CPlayer player) {
        if (player.getRank().getRankId() < Rank.SPECIALGUEST.getRankId()) {
            PlayerData data = ParkManager.getPlayerData(player.getUniqueId());
            List<UUID> friends = data.getFriendList();
            for (UUID uuid : hideall) {
                if (friends.contains(uuid)) {
                    continue;
                }
                Core.getPlayerManager().getPlayer(Bukkit.getPlayer(uuid)).hidePlayer(player);
            }
        }
    }
}