package us.mcmagic.magicassistant.utils;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerUtil {

    @SuppressWarnings("deprecation")
    public static Player findPlayer(String name) {
        for (Player tp : onlinePlayers()) {
            if (tp.getName().toLowerCase().contains(name.toLowerCase())) {
                return tp;
            }
        }
        return null;
    }

    public static Player[] onlinePlayers() {
        return Bukkit.getOnlinePlayers();
    }

    public static Player randomPlayer() {
        return onlinePlayers()[0];
    }

    public static String getNameFromUUID(String uuid) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getUniqueId().toString().equalsIgnoreCase(uuid)) {
                return p.getName();
            }
        }
        try {
            return Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName();
        } catch (Exception ex) {
            return null;
        }
    }

    public static String getUUIDFromName(String name) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getName().equalsIgnoreCase(name)) {
                return p.getUniqueId().toString();
            }
        }
        for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
            if (p.getName().equalsIgnoreCase(name)) {
                return p.getUniqueId().toString();
            }
        }
        return null;
    }
}