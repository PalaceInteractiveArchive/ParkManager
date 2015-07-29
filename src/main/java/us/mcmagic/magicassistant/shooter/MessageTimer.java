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

    public static void start() {
        Bukkit.getScheduler().runTaskTimer(MagicAssistant.getInstance(), new Runnable() {
            @Override
            public void run() {
                switch (MagicAssistant.shooter.game) {
                    case "buzz":
                        for (UUID uuid : MagicAssistant.shooter.ingame) {
                            Player player = Bukkit.getPlayer(uuid);
                            ActionBarManager.sendMessage(player, ChatColor.BLUE + "" + ChatColor.BOLD + "Buzz Points: "
                                    + ChatColor.GREEN + "" + ChatColor.BOLD + player.getMetadata("shooter").get(0).asInt());
                        }
                        break;
                    case "tsm":
                        for (UUID uuid : MagicAssistant.shooter.ingame) {
                            Player player = Bukkit.getPlayer(uuid);
                            ActionBarManager.sendMessage(player, ChatColor.GOLD + "" + ChatColor.BOLD + "Toy Story Mania Points: "
                                    + ChatColor.GREEN + "" + ChatColor.BOLD + player.getMetadata("shooter").get(0).asInt());
                        }
                        break;
                    case "mm":
                        for (UUID uuid : MagicAssistant.shooter.ingame) {
                            Player player = Bukkit.getPlayer(uuid);
                            ActionBarManager.sendMessage(player, ChatColor.RED + "" + ChatColor.BOLD + "Monstropolis Mayhem Points: "
                                    + ChatColor.YELLOW + "" + ChatColor.BOLD + player.getMetadata("shooter").get(0).asInt());
                        }
                        break;
                }
            }
        }, 0, 20L);
    }
}