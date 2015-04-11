package us.mcmagic.magicassistant.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.magicassistant.utils.PlayerUtil;

public class Commandtp implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length == 2) {
                Player tp1 = PlayerUtil.findPlayer(args[0]);
                Player tp2 = PlayerUtil.findPlayer(args[1]);
                if (tp1 == null || tp2 == null) {
                    sender.sendMessage(ChatColor.RED + "Player not found!");
                    return true;
                }
                tp1.teleport(tp2);
                sender.sendMessage(ChatColor.GRAY + tp1.getName() + " has been teleported to " + tp2.getName());
                return true;
            }
            if (args.length == 4) {
                try {
                    Player tp = PlayerUtil.findPlayer(args[0]);
                    double x = args[1].startsWith("~") ? tp.getLocation().getX()
                            + Integer.parseInt(args[1].substring(1)) : Integer
                            .parseInt(args[1]);
                    double y = args[2].startsWith("~") ? tp.getLocation().getY()
                            + Integer.parseInt(args[2].substring(1)) : Integer
                            .parseInt(args[2]);
                    double z = args[3].startsWith("~") ? tp.getLocation().getZ()
                            + Integer.parseInt(args[3].substring(1)) : Integer
                            .parseInt(args[3]);
                    Location loc = new Location(tp.getWorld(), x, y, z, tp
                            .getLocation().getYaw(), tp.getLocation().getPitch());
                    tp.teleport(loc);
                    sender.sendMessage(ChatColor.GRAY + tp.getName() + " has been teleported to " + x + ", " + y + ", "
                            + z);
                    return true;
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Error with numbers!");
                    return true;
                }
            }
            sender.sendMessage(ChatColor.RED + "/tp [Player] <Target> or <x> <y> <z>");
            return true;
        }
        Player player = (Player) sender;
        if (args.length == 1) {
            Player tp = PlayerUtil.findPlayer(args[0]);
            if (tp == null) {
                player.sendMessage(ChatColor.RED + "Player not found!");
                return true;
            }
            player.teleport(tp);
            player.sendMessage(ChatColor.GRAY + "You teleported to " + tp.getName());
            return true;
        }
        if (args.length == 2) {
            Player tp1 = PlayerUtil.findPlayer(args[0]);
            Player tp2 = PlayerUtil.findPlayer(args[1]);
            if (tp1 == null || tp2 == null) {
                sender.sendMessage(ChatColor.RED + "Player not found!");
                return true;
            }
            tp1.teleport(tp2);
            player.sendMessage(ChatColor.GRAY + tp1.getName()
                    + " has been teleported to " + tp2.getName());
            return true;
        }
        if (args.length == 3) {
            try {
                double x = args[0].startsWith("~") ? player.getLocation().getX()
                        + Integer.parseInt(args[0].substring(1)) : Integer
                        .parseInt(args[0]);
                double y = args[1].startsWith("~") ? player.getLocation().getY()
                        + Integer.parseInt(args[1].substring(1)) : Integer
                        .parseInt(args[1]);
                double z = args[2].startsWith("~") ? player.getLocation().getZ()
                        + Integer.parseInt(args[2].substring(1)) : Integer
                        .parseInt(args[2]);
                Location loc = new Location(player.getWorld(), x, y, z, player
                        .getLocation().getYaw(), player.getLocation().getPitch());
                player.teleport(loc);
                player.sendMessage(ChatColor.GRAY + "You teleported to " + x + ", " + y + ", " + z);
                return true;
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Error with numbers!");
                return true;
            }
        }
        if (args.length == 4) {
            try {
                Player tp = PlayerUtil.findPlayer(args[0]);
                double x = args[0].startsWith("~") ? player.getLocation().getX()
                        + Integer.parseInt(args[0].substring(1)) : Integer
                        .parseInt(args[0]);
                double y = args[1].startsWith("~") ? player.getLocation().getY()
                        + Integer.parseInt(args[1].substring(1)) : Integer
                        .parseInt(args[1]);
                double z = args[2].startsWith("~") ? player.getLocation().getZ()
                        + Integer.parseInt(args[2].substring(1)) : Integer
                        .parseInt(args[2]);
                Location loc = new Location(tp.getWorld(), x, y, z, tp
                        .getLocation().getYaw(), tp.getLocation().getPitch());
                tp.teleport(loc);
                player.sendMessage(ChatColor.GRAY + tp.getName() + " has been teleported to " + x + ", " + y + ", " + z);
                return true;
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Error with numbers!");
                return true;
            }
        }
        player.sendMessage(ChatColor.RED + "/tp [Player] <Target> or <x> <y> <z>");
        return true;
    }
}
