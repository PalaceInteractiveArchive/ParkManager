package us.mcmagic.parkmanager.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.permissions.Rank;
import us.mcmagic.mcmagiccore.player.User;
import us.mcmagic.parkmanager.ParkManager;
import us.mcmagic.parkmanager.commands.Commandvanish;
import us.mcmagic.parkmanager.handlers.PlayerData;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class VisibilityUtil {
    private List<UUID> hideall = new ArrayList<>();
    private List<UUID> spawnHide = new ArrayList<>();

    public VisibilityUtil() {
        if (!ParkManager.ttcServer) {
            return;
        }
        Bukkit.getScheduler().runTaskTimer(ParkManager.getInstance(), () -> {
            final Location spawn = ParkManager.spawn;
            for (Player player : Bukkit.getOnlinePlayers()) {
                User user = MCMagicCore.getUser(player.getUniqueId());
                if (user.getRank().getRankId() >= Rank.SPECIALGUEST.getRankId()) {
                    continue;
                }
                final UUID uuid = player.getUniqueId();
                final Location loc = player.getLocation();
                boolean inRegion = loc.distance(spawn) <= 5;
                List<UUID> hidden = getSpawnHide();
                boolean contains = hidden.contains(uuid);
                if (!inRegion && contains) {
                    hidden.remove(uuid);
                    spawnHide.remove(uuid);
                    showPlayerForOthers(player);
                } else if (inRegion && !contains) {
                    hidden.add(uuid);
                    spawnHide.add(uuid);
                    hidePlayerForOthers(player);
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
            if (ParkManager.getPlayerData(tp.getUniqueId()).getVisibility()) {
                tp.showPlayer(player);
            }
        }
    }

    public void logout(Player player) {
        hideall.remove(player.getUniqueId());
        spawnHide.remove(player.getUniqueId());
    }

    public void addToHideAll(final Player player) {
        PlayerData data = ParkManager.getPlayerData(player.getUniqueId());
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
        PlayerData data = ParkManager.getPlayerData(player.getUniqueId());
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
            PlayerData data = ParkManager.getPlayerData(player.getUniqueId());
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
