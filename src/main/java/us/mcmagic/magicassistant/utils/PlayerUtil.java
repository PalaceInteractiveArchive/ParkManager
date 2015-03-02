package us.mcmagic.magicassistant.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


public class PlayerUtil {

    public static Player findPlayer(String name) {
        for (Player tp : Bukkit.getOnlinePlayers()) {
            if (tp.getName().toLowerCase().contains(name.toLowerCase())) {
                return tp;
            }
        }
        return null;
    }


    public static Player randomPlayer() {
        return Bukkit.getOnlinePlayers().iterator().next();
    }
}