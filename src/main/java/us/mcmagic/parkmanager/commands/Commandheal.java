package us.mcmagic.parkmanager.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import us.mcmagic.mcmagiccore.player.PlayerUtil;

public class Commandheal implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length == 1) {
                if (args[0].equals("**")) {
                    for (Player tp : Bukkit.getOnlinePlayers()) {
                        healPlayer(tp);
                        tp.sendMessage(ChatColor.GRAY + "You have been healed.");
                    }
                    sender.sendMessage(ChatColor.GRAY + "Healed all players!");
                    return true;
                }
                Player tp = PlayerUtil.findPlayer(args[0]);
                if (tp == null) {
                    sender.sendMessage(ChatColor.RED + "Player not found!");
                    return true;
                }
                healPlayer(tp);
                tp.sendMessage(ChatColor.GRAY + "You have been healed.");
            }
            return true;
        }
        Player player = (Player) sender;
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("**")) {
                for (Player tp : Bukkit.getOnlinePlayers()) {
                    healPlayer(tp);
                    tp.sendMessage(ChatColor.GRAY + "You have been healed.");
                }
                player.sendMessage(ChatColor.GRAY + "Healed all players!");
                return true;
            }
            Player tp = PlayerUtil.findPlayer(args[0]);
            if (tp == null) {
                player.sendMessage(ChatColor.RED + "Player not found!");
                return true;
            }
            healPlayer(tp);
            player.sendMessage(ChatColor.GRAY + "You healed " + tp.getName());
            tp.sendMessage(ChatColor.GRAY + "You have been healed.");
            return true;
        }
        healPlayer(player);
        player.sendMessage(ChatColor.GRAY + "You have been healed.");
        return true;
    }

    public static void healPlayer(Player player) {
        player.setHealth(player.getHealthScale());
        player.setFoodLevel(20);
        player.setFireTicks(0);
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
    }
}