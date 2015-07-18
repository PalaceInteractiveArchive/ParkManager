package us.mcmagic.magicassistant.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.permissions.Rank;

/**
 * Created by Marc on 7/18/15
 */
public class Commandfun implements CommandExecutor {
    private boolean active = false;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;
        if (!player.getName().equals("Legobuilder0813")) {
            player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Only Legobuilder0813 can have fun!");
            return true;
        }
        if (active) {
            for (Player tp : Bukkit.getOnlinePlayers()) {
                if (tp.getUniqueId().equals(player.getUniqueId())) {
                    continue;
                }
                if (tp.getSpectatorTarget().equals(player)) {
                    tp.setSpectatorTarget(null);
                    tp.setGameMode(MCMagicCore.getUser(tp.getUniqueId()).getRank().getRankId() <
                            Rank.CASTMEMBER.getRankId() ? GameMode.ADVENTURE : GameMode.CREATIVE);
                }
                tp.setSpectatorTarget(player);
                tp.sendMessage(ChatColor.RED + "You are no longer spectating " + player.getName() + "!");
            }
            player.sendMessage(ChatColor.RED + "No one is spectating you anymore!");
        } else {
            for (Player tp : Bukkit.getOnlinePlayers()) {
                if (tp.getUniqueId().equals(player.getUniqueId())) {
                    continue;
                }
                tp.setGameMode(GameMode.SPECTATOR);
                tp.setSpectatorTarget(player);
                tp.sendMessage(ChatColor.GREEN + "You are now spectating " + player.getName() + "!");
            }
            player.sendMessage(ChatColor.GREEN + "Everyone is spectating you!");
        }
        active = !active;
        return true;
    }
}
