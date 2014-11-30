package us.mcmagic.magicassistant.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import us.mcmagic.magicassistant.MagicAssistant;

import java.util.ArrayList;

public class VisibleUtil implements Listener {
    public static ArrayList<Player> hideall = new ArrayList<>();
    public static MagicAssistant pl;

    public VisibleUtil(MagicAssistant instance) {
        pl = instance;
    }

    @SuppressWarnings("deprecation")
    public static void addToHideAll(final Player player) {
        hideall.add(player);
        Bukkit.getScheduler().runTask(pl, new Runnable() {
            public void run() {
                for (Player tp : Bukkit.getOnlinePlayers()) {
                    if (tp.getName() != player.getName()) {
                        if (!tp.hasPermission("band.stayvisible")) {
                            player.hidePlayer(tp);
                        }
                    }
                }
            }
        });
    }

    @SuppressWarnings("deprecation")
    public static void removeFromHideAll(final Player player) {
        hideall.remove(player);
        Bukkit.getScheduler().runTask(pl, new Runnable() {
            public void run() {
                for (Player tp : Bukkit.getOnlinePlayers()) {
                    if (tp.getName() != player.getName()) {
                        if (!tp.hasPermission("band.stayvisible")) {
                            player.showPlayer(tp);
                        }
                    }
                }
            }
        });
    }
}