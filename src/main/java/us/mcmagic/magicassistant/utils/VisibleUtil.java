package us.mcmagic.magicassistant.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.commands.Commandvanish;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.player.User;

import java.util.ArrayList;
import java.util.UUID;

public class VisibleUtil implements Listener {
    public static ArrayList<UUID> hideall = new ArrayList<>();
    public static MagicAssistant pl;

    public VisibleUtil(MagicAssistant instance) {
        pl = instance;
    }

    public static void addToHideAll(final Player player) {
        hideall.add(player.getUniqueId());
        for (User user : MCMagicCore.getUsers()) {
            Player tp = Bukkit.getPlayer(user.getUniqueId());
            if (tp == null) {
                continue;
            }
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
        for (UUID uuid : Commandvanish.hidden) {
            player.hidePlayer(Bukkit.getPlayer(uuid));
        }
    }

    public static void removeFromHideAll(final Player player) {
        hideall.remove(player.getUniqueId());
        for (User user : MCMagicCore.getUsers()) {
            Player tp = Bukkit.getPlayer(user.getUniqueId());
            if (!tp.getName().equals(player.getName())) {
                if (!tp.hasPermission("band.stayvisible")) {
                    player.showPlayer(tp);
                }
            }
        }
    }
}