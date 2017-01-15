package network.palace.parkmanager.shooter;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.UUID;

/**
 * Created by Marc on 1/19/15
 */
public class MessageTimer {

    public static void start() {
        Bukkit.getScheduler().runTaskTimer(ParkManager.getInstance(), () -> {
            String msg = "";
            switch (ParkManager.shooter.game) {
                case "buzz":
                    msg = ChatColor.BLUE + "" + ChatColor.BOLD + "Buzz Points: " + ChatColor.GREEN + "" +
                            ChatColor.BOLD;
                    break;
                case "tsm":
                    msg = ChatColor.GOLD + "" + ChatColor.BOLD + "Toy Story Mania Points: " + ChatColor.GREEN + ""
                            + ChatColor.BOLD;
                    break;
                case "mm":
                    msg = ChatColor.RED + "" + ChatColor.BOLD + "Monstropolis Mayhem Points: " + ChatColor.YELLOW +
                            "" + ChatColor.BOLD;
                    break;
            }
            for (UUID uuid : ParkManager.shooter.getIngame()) {
                CPlayer player = Core.getPlayerManager().getPlayer(uuid);
                if (player == null) {
                    continue;
                }
                player.getActionBar().show(msg + player.getBukkitPlayer().getMetadata("shooter").get(0).asInt());
            }
        }, 0, 20L);
    }
}
