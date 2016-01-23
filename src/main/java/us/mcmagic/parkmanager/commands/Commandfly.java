package us.mcmagic.parkmanager.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.permissions.Rank;
import us.mcmagic.mcmagiccore.player.PlayerUtil;

public class Commandfly implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length == 1) {
                Player player = PlayerUtil.findPlayer(args[0]);
                if (player == null) {
                    sender.sendMessage(ChatColor.RED
                            + "I can'commands find that player!");
                    return true;
                }
                if (player.getAllowFlight()) {
                    player.setAllowFlight(false);
                    player.setFlying(false);
                    player.sendMessage(ChatColor.RED + "You can't fly anymore!");
                    sender.sendMessage(player.getName() + " can't fly anymore!");
                    return true;
                }
                player.setAllowFlight(true);
                player.sendMessage(ChatColor.GREEN + "You can fly!");
                sender.sendMessage(player.getName() + " can now fly!");
                return true;
            }
            sender.sendMessage(ChatColor.RED + "/fly [Username]");
            return true;
        }
        Player player = (Player) sender;
        if (args.length == 1) {
            Rank rank = MCMagicCore.getUser(player.getUniqueId()).getRank();
            if (rank.getRankId() < Rank.CASTMEMBER.getRankId()) {
                if (player.getAllowFlight()) {
                    player.setAllowFlight(false);
                    player.setFlying(false);
                    player.sendMessage(ChatColor.RED + "You can't fly anymore!");
                    return true;
                }
                player.setAllowFlight(true);
                player.sendMessage(ChatColor.GREEN + "You can fly!");
                return true;
            }
            Player tp = PlayerUtil.findPlayer(args[0]);
            if (tp == null) {
                player.sendMessage(ChatColor.RED + "I can't find that player!");
                return true;
            }
            if (tp.getAllowFlight()) {
                tp.setAllowFlight(false);
                tp.setFlying(false);
                tp.sendMessage(ChatColor.RED + "You can't fly anymore!");
                player.sendMessage(ChatColor.RED + tp.getName() + " can't fly anymore!");
                return true;
            }
            tp.setAllowFlight(true);
            tp.sendMessage(ChatColor.GREEN + "You can fly!");
            player.sendMessage(ChatColor.GREEN + tp.getName() + " can now fly!");
            return true;
        }
        if (player.getAllowFlight()) {
            player.setAllowFlight(false);
            player.setFlying(false);
            player.sendMessage(ChatColor.RED + "You can't fly anymore!");
            return true;
        }
        player.setAllowFlight(true);
        player.sendMessage(ChatColor.GREEN + "You can fly!");
        return true;
    }
}