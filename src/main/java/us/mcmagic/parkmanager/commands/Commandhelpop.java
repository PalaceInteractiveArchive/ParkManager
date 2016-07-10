package us.mcmagic.parkmanager.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.permissions.Rank;

public class Commandhelpop implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            String message = "";
            for (String arg : args) {
                message += arg + " ";
            }
            if (sender instanceof Player) {
                Player player = (Player) sender;
                for (Player tp : Bukkit.getOnlinePlayers()) {
                    if (MCMagicCore.getUser(tp.getUniqueId()).getRank().getRankId() >= Rank.EARNINGMYEARS.getRankId()) {
                        tp.sendMessage(ChatColor.DARK_RED + "[CM CHAT] " + ChatColor.GRAY + player.getName() + ": " +
                                ChatColor.WHITE + ChatColor.translateAlternateColorCodes('&', message));
                    }
                }
                return true;
            }
            for (Player tp : Bukkit.getOnlinePlayers()) {
                if (MCMagicCore.getUser(tp.getUniqueId()).getRank().getRankId() >= Rank.EARNINGMYEARS.getRankId()) {
                    tp.sendMessage(ChatColor.DARK_RED + "[CM CHAT] " + ChatColor.GRAY + "Console" + ": " +
                            ChatColor.WHITE + ChatColor.translateAlternateColorCodes('&', message));
                }
            }
            return true;
        }
        sender.sendMessage(ChatColor.RED + "/" + label + " [message]");
        return true;
    }
}