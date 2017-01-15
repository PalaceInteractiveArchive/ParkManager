package network.palace.parkmanager.watch;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import org.bukkit.ChatColor;

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
            CPlayer tp = Core.getPlayerManager().getPlayer(uuid);
            tp.getActionBar().show(msg);
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