package us.mcmagic.magicassistant.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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
}