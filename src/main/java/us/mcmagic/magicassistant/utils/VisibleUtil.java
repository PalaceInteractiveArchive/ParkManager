package us.mcmagic.magicassistant.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.commands.Commandvanish;
import us.mcmagic.magicassistant.handlers.PlayerData;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.permissions.Rank;
import us.mcmagic.mcmagiccore.player.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class VisibleUtil {
    private List<UUID> hideall = new ArrayList<>();
    private List<UUID> spawnHide = new ArrayList<>();
    private boolean hub;
    private List<UUID> hide = new ArrayList<>();
    private List<UUID> show = new ArrayList<>();

    public VisibleUtil() {
        hub = MCMagicCore.getMCMagicConfig().serverName.equalsIgnoreCase("hub");
        Bukkit.getScheduler().runTaskTimer(MagicAssistant.getInstance(), new Runnable() {
            @Override
            public void run() {
                for (Player tp : Bukkit.getOnlinePlayers()) {
                    for (UUID uuid : hide) {
                        hide.remove(uuid);
                        if (tp.getUniqueId().equals(uuid)) {
                            continue;
                        }
                        tp.hidePlayer(Bukkit.getPlayer(uuid));
                    }
                    for (UUID uuid : show) {
                        show.remove(uuid);
                        if (tp.getUniqueId().equals(uuid)) {
                            continue;
                        }
                        if (Commandvanish.getHidden().contains(uuid) &&
                                MCMagicCore.getUser(tp.getUniqueId()).getRank().getRankId()
                                        < Rank.SPECIALGUEST.getRankId()) {
                            continue;
                        }
                        tp.showPlayer(Bukkit.getPlayer(uuid));
                    }
                }
            }
        }, 0L, 20L);
    }

    public void move(PlayerMoveEvent event) {
        if (!hub) {
            return;
        }
        Player player = event.getPlayer();
        Location to = event.getTo();
        boolean should = shouldBeHidden(to);
        if (!spawnHide.contains(player.getUniqueId()) && should) {
            spawnHide.add(player.getUniqueId());
            hide.add(player.getUniqueId());
        } else if (spawnHide.contains(player.getUniqueId()) && !should) {
            spawnHide.remove(player.getUniqueId());
            show.add(player.getUniqueId());
        }
    }

    private boolean shouldBeHidden(Location loc) {
        return loc.distance(MagicAssistant.spawn) <= 5;
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

    public void logout(UUID uuid) {
        hideall.remove(uuid);
    }
}