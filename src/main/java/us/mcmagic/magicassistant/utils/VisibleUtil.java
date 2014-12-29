package us.mcmagic.magicassistant.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import us.mcmagic.magicassistant.MagicAssistant;

import java.util.ArrayList;
import java.util.UUID;

public class VisibleUtil implements Listener {
    public static ArrayList<UUID> hideall = new ArrayList<>();
    public static MagicAssistant pl;

    public VisibleUtil(MagicAssistant instance) {
        pl = instance;
    }

    @SuppressWarnings("deprecation")
    public static void addToHideAll(final Player player) {
        hideall.add(player.getUniqueId());
        for (Player tp : Bukkit.getOnlinePlayers()) {
            if (!tp.getName().equals(player.getName())) {
                if (!tp.hasPermission("band.stayvisible")) {
                    player.hidePlayer(tp);
                }
            }
        }
    }

    public static void hideForHideAll(Player player) {
        for (UUID uuid : hideall) {
            Bukkit.getPlayer(uuid).hidePlayer(player);
        }
    }

    @SuppressWarnings("deprecation")
    public static void removeFromHideAll(final Player player) {
        hideall.remove(player.getUniqueId());
        for (Player tp : Bukkit.getOnlinePlayers()) {
            if (!tp.getName().equals(player.getName())) {
                if (!tp.hasPermission("band.stayvisible")) {
                    player.showPlayer(tp);
                }
            }
        }
    }
}