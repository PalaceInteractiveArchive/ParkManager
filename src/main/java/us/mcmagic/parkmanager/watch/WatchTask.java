package us.mcmagic.parkmanager.watch;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.mcmagic.parkmanager.ParkManager;
import us.mcmagic.mcmagiccore.actionbar.ActionBarManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Marc on 10/11/15
 */
public class WatchTask implements Runnable {
    private static List<UUID> message = new ArrayList<>();

    @Override
    public void run() {
        String msg = ChatColor.YELLOW + "" + ChatColor.BOLD + "Current time in EST: " + ChatColor.GREEN +
                ParkManager.bandUtil.currentTime();
        for (UUID uuid : new ArrayList<>(message)) {
            Player tp = Bukkit.getPlayer(uuid);
            ActionBarManager.sendMessage(tp, msg);
        }
    }

    public static void addToMessage(UUID uuid) {
        removeFromMessage(uuid);
        message.add(uuid);
    }

    public static void removeFromMessage(UUID uuid) {
        message.remove(uuid);
    }
}