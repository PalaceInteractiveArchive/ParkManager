package us.mcmagic.magicassistant.shooter;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.mcmagiccore.actionbar.ActionBarManager;

import java.util.UUID;

/**
 * Created by Marc on 1/19/15
 */
public class MessageTimer {

    public static void start(MagicAssistant pl) {
        Bukkit.getScheduler().runTaskTimer(pl, new Runnable() {
            @Override
            public void run() {
                if (Shooter.game == "buzz") {
                    for (UUID uuid : Shooter.ingame) {
                        Player player = Bukkit.getPlayer(uuid);
                        ActionBarManager.sendMessage(player, ChatColor.BLUE + "" + ChatColor.BOLD + "Buzz Points: "
                                + ChatColor.GREEN + "" + ChatColor.BOLD + player.getMetadata("shooter").get(0).asInt());
                    }
                } else {
                    for (UUID uuid : Shooter.ingame) {
                        Player player = Bukkit.getPlayer(uuid);
                        ActionBarManager.sendMessage(player, ChatColor.GOLD + "" + ChatColor.BOLD + "Toy Story Mania Points: "
                                + ChatColor.GREEN + "" + ChatColor.BOLD + player.getMetadata("shooter").get(0).asInt());
                    }
                }
            }
        }, 0, 20L);
    }
}
