package us.mcmagic.parkmanager.utils;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.io.InvalidClassException;
import java.util.Collection;
import java.util.UUID;

public class PlayerUtil {

    @SuppressWarnings("unchecked")
    public static Player[] onlinePlayers() {
        try {
            Object rawPlayerList = (Bukkit.class.getMethod("getOnlinePlayers", null).invoke(null, null));
            if (rawPlayerList instanceof Player[]) {
                return (Player[]) rawPlayerList;
            } else if (rawPlayerList instanceof Collection) {
                Collection<? extends Player> playerList = (Collection<? extends Player>) rawPlayerList;
                Player[] players = new Player[playerList.size()];
                int i = 0;
                for (Object p : playerList) {
                    players[i] = (Player) p;
                    i++;
                }
                return players;
            } else {
                throw new InvalidClassException("The return object type was neither Player[] nor Collection");
            }
        } catch (Exception ex) {
            Bukkit.getLogger().severe("Exception occured in ParkManager:PlayerUtil.onlinePlayers()");
            Bukkit.getLogger().severe(ex.getClass().getSimpleName() + ": " + ex.getMessage());
            ex.printStackTrace();
            return new Player[0];
        }
    }

    public static Player randomPlayer() {
        return Bukkit.getOnlinePlayers().iterator().next();
    }

    public static String getNameFromUUID(String uuid) {
        for (Player p : onlinePlayers()) {
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

    public static String getNameFromUUID(UUID uuid) {
        for (Player p : onlinePlayers()) {
            if (p.getUniqueId().equals(uuid)) {
                return p.getName();
            }
        }
        try {
            return Bukkit.getOfflinePlayer(uuid).getName();
        } catch (Exception ex) {
            return null;
        }
    }

    public static String getUUIDFromName(String name) {
        for (Player p : onlinePlayers()) {
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