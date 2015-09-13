package us.mcmagic.magicassistant.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.commands.Commandvanish;
import us.mcmagic.magicassistant.handlers.PlayerData;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.permissions.Rank;
import us.mcmagic.mcmagiccore.player.User;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class VisibleUtil {
    private List<UUID> hideall = new ArrayList<>();
    private Map<UUID, Boolean> hidden = new ConcurrentHashMap<>();

    public VisibleUtil() {
        if (!MCMagicCore.getMCMagicConfig().serverName.equalsIgnoreCase("hub")) {
            return;
        }
        Bukkit.getScheduler().runTaskTimer(MagicAssistant.getInstance(), new Runnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!hidden.get(player.getUniqueId()) && player.getLocation().distance(MagicAssistant.spawn) <= 5) {
                        User user = MCMagicCore.getUser(player.getUniqueId());
                        if (user.getRank().getRankId() < Rank.SPECIALGUEST.getRankId()) {
                            hidden.put(player.getUniqueId(), true);
                            vanish(player, false);
                        } else {
                            hidden.put(player.getUniqueId(), false);
                            vanish(player, false);
                        }
                    } else if (hidden.get(player.getUniqueId()) && player.getLocation().distance(MagicAssistant.spawn) <= 5) {
                        vanish(player, false);
                    } else if (hidden.get(player.getUniqueId()) && player.getLocation().distance(MagicAssistant.spawn) > 5) {
                        hidden.remove(player.getUniqueId());
                        vanish(player, true);
                    }
                }
            }
        }, 0L, 20L);
    }

    private void vanish(Player player, boolean show) {
        if (!this.hidden.get(player.getUniqueId())) {
            return;
        }
        for (UUID id : hidden.keySet()) {
            Player eachPlayer = Bukkit.getPlayer(id);
            if (!show) {
                if (eachPlayer != null && !player.getUniqueId().equals(eachPlayer.getUniqueId()) && !hidden.get(id)) {
                    player.hidePlayer(Bukkit.getPlayer(id));
                }
            } else {
                if (eachPlayer != null) {
                    player.showPlayer(Bukkit.getPlayer(id));
                }
            }
        }
    }

    public void logout(Player player) {
        hidden.remove(player.getUniqueId());
    }

    public void addToHideAll(final Player player) {
        PlayerData data = MagicAssistant.getPlayerData(player.getUniqueId());
        List<UUID> friends = data.getFriendList();
        hideall.add(player.getUniqueId());
        for (User user : MCMagicCore.getUsers()) {
            Player tp = Bukkit.getPlayer(user.getUniqueId());
            if (tp == null) {
                continue;
            }
            if (friends.contains(user.getUniqueId()) && user.getRank().getRankId() < Rank.SPECIALGUEST.getRankId()) {
                player.showPlayer(tp);
                continue;
            }
            if (!tp.getUniqueId().equals(player.getUniqueId())) {
                if (user.getRank().getRankId() < Rank.SPECIALGUEST.getRankId()) {
                    player.hidePlayer(tp);
                }
            }
        }
    }

    public void removeFromHideAll(final Player player) {
        PlayerData data = MagicAssistant.getPlayerData(player.getUniqueId());
        List<UUID> friends = data.getFriendList();
        hideall.remove(player.getUniqueId());
        for (User user : MCMagicCore.getUsers()) {
            Player tp = Bukkit.getPlayer(user.getUniqueId());
            if (tp == null) {
                continue;
            }
            if (!tp.getUniqueId().equals(player.getUniqueId())) {
                if (user.getRank().getRankId() < Rank.SPECIALGUEST.getRankId()) {
                    player.showPlayer(tp);
                }
            }
        }
    }

    public boolean isInHideAll(UUID uuid) {
        return hideall.contains(uuid);
    }

    public void login(Player player) {
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