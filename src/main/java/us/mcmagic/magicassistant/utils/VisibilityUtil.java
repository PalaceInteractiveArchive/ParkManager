package us.mcmagic.magicassistant.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.commands.Commandvanish;
import us.mcmagic.magicassistant.handlers.PlayerData;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.permissions.Rank;
import us.mcmagic.mcmagiccore.player.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class VisibilityUtil {
    private List<UUID> hideall = new ArrayList<>();
    private List<UUID> spawnHide = new ArrayList<>();

    public VisibilityUtil() {
        if (!MagicAssistant.hubServer) {
            return;
        }
        Bukkit.getScheduler().runTaskTimer(MagicAssistant.getInstance(), new Runnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!getSpawnHide().contains(player.getUniqueId()) && player.getLocation().distance(MagicAssistant.spawn) <= 5) {
                        User user = MCMagicCore.getUser(player.getUniqueId());
                        if (user.getRank().getRankId() < Rank.SPECIALGUEST.getRankId()) {
                            spawnHide.add(player.getUniqueId());
                            hidePlayerForOthers(player);
                        }
                        continue;
                    }
                    if (getSpawnHide().contains(player.getUniqueId()) && player.getLocation().distance(MagicAssistant.spawn) > 5) {
                        spawnHide.remove(player.getUniqueId());
                        showPlayerForOthers(player);
                    }
                }
            }
        }, 0L, 20L);
    }

    private void hidePlayerForOthers(Player player) {
        for (Player tp : Bukkit.getOnlinePlayers()) {
            if (tp.getUniqueId().equals(player.getUniqueId())) {
                continue;
            }
            tp.hidePlayer(player);
        }
    }

    private void showPlayerForOthers(Player player) {
        for (Player tp : Bukkit.getOnlinePlayers()) {
            if (tp.getUniqueId().equals(player.getUniqueId())) {
                continue;
            }
            if (hideall.contains(tp.getUniqueId())) {
                continue;
            }
            PlayerData data = MagicAssistant.getPlayerData(tp.getUniqueId());
            if (data.getVisibility()) {
                tp.showPlayer(player);
            }
        }
    }

    public void logout(Player player) {
        hideall.remove(player.getUniqueId());
        spawnHide.remove(player.getUniqueId());
    }

    public void addToHideAll(final Player player) {
        PlayerData data = MagicAssistant.getPlayerData(player.getUniqueId());
        List<UUID> friends = data.getFriendList();
        hideall.add(player.getUniqueId());
        for (Player tp : Bukkit.getOnlinePlayers()) {
            User user = MCMagicCore.getUser(tp.getUniqueId());
            if (friends.contains(tp.getUniqueId()) && user.getRank().getRankId() < Rank.SPECIALGUEST.getRankId() ||
                    tp.getUniqueId().equals(player.getUniqueId())) {
                continue;
            }
            if (user.getRank().getRankId() < Rank.SPECIALGUEST.getRankId()) {
                player.hidePlayer(tp);
            }
        }
    }

    public void removeFromHideAll(final Player player) {
        PlayerData data = MagicAssistant.getPlayerData(player.getUniqueId());
        List<UUID> friends = data.getFriendList();
        hideall.remove(player.getUniqueId());
        for (Player tp : Bukkit.getOnlinePlayers()) {
            User user = MCMagicCore.getUser(tp.getUniqueId());
            if (getSpawnHide().contains(tp.getUniqueId())) {
                continue;
            }
            if (tp.getUniqueId().equals(player.getUniqueId())) {
                continue;
            }
            if (user.getRank().getRankId() < Rank.SPECIALGUEST.getRankId()) {
                player.showPlayer(tp);
            }
        }
    }

    public boolean isInHideAll(UUID uuid) {
        return hideall.contains(uuid);
    }

    public void login(Player player) {
        for (UUID uuid : getSpawnHide()) {
            Player tp = Bukkit.getPlayer(uuid);
            if (tp != null) {
                player.hidePlayer(tp);
            }
        }
        if (MCMagicCore.getUser(player.getUniqueId()).getRank().getRankId() < Rank.SPECIALGUEST.getRankId()) {
            PlayerData data = MagicAssistant.getPlayerData(player.getUniqueId());
            List<UUID> friends = data.getFriendList();
            for (UUID uuid : hideall) {
                if (friends.contains(uuid)) {
                    continue;
                }
                Bukkit.getPlayer(uuid).hidePlayer(player);
            }
            for (UUID uuid : Commandvanish.getHidden()) {
                player.hidePlayer(Bukkit.getPlayer(uuid));
            }
        }
    }

    public List<UUID> getSpawnHide() {
        return new ArrayList<>(spawnHide);
    }
}
