package network.palace.parkmanager.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.parkmanager.ParkManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.List;
import java.util.UUID;

public class VisibilityUtil {

    /**
     * Update whether this player can see other players or not
     *
     * @param player the player
     */
    private void updatePlayerVisibility(CPlayer player) {
        for (CPlayer tp : Core.getPlayerManager().getOnlinePlayers()) {
            if (shouldSee(player, tp)) {
                player.showPlayer(ParkManager.getInstance(), tp);
            } else {
                player.hidePlayer(ParkManager.getInstance(), tp);
            }
        }
    }

    /**
     * Check if player1 should be able to see player2
     *
     * @param player1 the first player
     * @param player2 the second player
     * @return if player1 should be able to see player2
     */
    private boolean shouldSee(CPlayer player1, CPlayer player2) {
        if (player1.getUniqueId().equals(player2.getUniqueId())) return false;
        Setting setting = getSetting(player1);
        switch (setting) {
            case ONLY_STAFF_AND_FRIENDS:
                if (player2.getRank().getRankId() >= Rank.SPECIALGUEST.getRankId()) return true;
            case ONLY_FRIENDS:
                return isFriend(player1, player2);
            case ALL_HIDDEN:
                return true;
        }
        return true;
    }

    /**
     * Check if player1 and player2 are friends
     *
     * @param player1 the first player
     * @param player2 the second player
     * @return if the two players are friends
     */
    private boolean isFriend(CPlayer player1, CPlayer player2) {
        return ((List<UUID>) player1.getRegistry().getEntry("friends")).contains(player2.getUniqueId());
    }

    public boolean toggleVisibility(CPlayer player) {
        if (player.getRegistry().hasEntry("visibilityDelay") && (System.currentTimeMillis() < ((long) player.getRegistry().getEntry("visibilityDelay")))) {
            player.sendMessage(ChatColor.RED + "You must wait 5s between changing visibility settings!");
            return false;
        }
        player.getRegistry().addEntry("visibilityDelay", System.currentTimeMillis() + 5000);
        Setting currentSetting = getSetting(player);
        if (currentSetting.equals(Setting.ALL_HIDDEN)) {
            setSetting(player, Setting.ALL_VISIBLE, true);
        } else {
            setSetting(player, Setting.ALL_HIDDEN, true);
        }
        return true;
    }

    public Setting getSetting(CPlayer player) {
        if (!player.getRegistry().hasEntry("visibilitySetting"))
            player.getRegistry().addEntry("visibilitySetting", Setting.ALL_VISIBLE);
        return (Setting) player.getRegistry().getEntry("visibilitySetting");
    }

    public boolean setSetting(CPlayer player, Setting setting, boolean delayBypass) {
        if (!delayBypass) {
            if (player.getRegistry().hasEntry("visibilityDelay") && (System.currentTimeMillis() < ((long) player.getRegistry().getEntry("visibilityDelay")))) {
                player.sendMessage(ChatColor.RED + "You must wait 5s between changing visibility settings!");
                return false;
            }
            player.getRegistry().addEntry("visibilityDelay", System.currentTimeMillis() + 5000);
        }
        player.getRegistry().addEntry("visibilitySetting", setting);
        updatePlayerVisibility(player);
        return true;
    }

    public void handleJoin(CPlayer player, String visibility) {
        setSetting(player, Setting.fromString(visibility), false);
        Core.getPlayerManager().getOnlinePlayers().stream().filter(p -> !shouldSee(p, player)).forEach(p -> p.hidePlayer(player));
    }

    @Getter
    @AllArgsConstructor
    public enum Setting {
        ALL_VISIBLE("Visible", ChatColor.GREEN, Material.GREEN_TERRACOTTA),
        ONLY_STAFF_AND_FRIENDS("Staff & Friends", ChatColor.YELLOW, Material.YELLOW_TERRACOTTA),
        ONLY_FRIENDS("Friends", ChatColor.GOLD, Material.ORANGE_TERRACOTTA),
        ALL_HIDDEN("Hidden", ChatColor.RED, Material.RED_TERRACOTTA);

        String text;
        ChatColor color;
        Material block;

        public String toString() {
            switch (this) {
                case ALL_VISIBLE:
                    return "all";
                case ONLY_STAFF_AND_FRIENDS:
                    return "staff_friends";
                case ONLY_FRIENDS:
                    return "friends";
            }
            return "none";
        }

        public static Setting fromString(String visibility) {
            if (visibility == null) return ALL_VISIBLE;
            switch (visibility.toLowerCase()) {
                case "staff_friends":
                    return ONLY_STAFF_AND_FRIENDS;
                case "friends":
                    return ONLY_FRIENDS;
                case "none":
                    return ALL_HIDDEN;
                default:
                    return ALL_VISIBLE;
            }
        }
    }
}
