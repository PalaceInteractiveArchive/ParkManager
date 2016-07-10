package us.mcmagic.parkmanager.commands;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.mcmagiccore.player.PlayerUtil;
import us.mcmagic.parkmanager.ParkManager;

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
                if (tp1.isInsideVehicle()) {
                    sender.sendMessage(ChatColor.RED + tp1.getName() + " is on a ride, you can't teleport them!");
                    return true;
                }
                if (tp2.isInsideVehicle() && !tp2.getGameMode().equals(GameMode.SPECTATOR)) {
                    sender.sendMessage(ChatColor.RED + tp2.getName() + " is on a ride, you can't teleport to them! " +
                            "They must be in Spectator Mode to teleport to players on rides.");
                    return true;
                }
                ParkManager.teleportUtil.log(tp1, tp1.getLocation());
                tp1.teleport(tp2);
                sender.sendMessage(ChatColor.GRAY + tp1.getName() + " has been teleported to " + tp2.getName());
                return true;
            }
            if (args.length == 4) {
                try {
                    Player tp = PlayerUtil.findPlayer(args[0]);
                    double x = args[1].startsWith("~") ? tp.getLocation().getX() + num(args[1].substring(1)) : num(args[1]);
                    double y = args[2].startsWith("~") ? tp.getLocation().getY() + num(args[2].substring(1)) : num(args[2]);
                    double z = args[3].startsWith("~") ? tp.getLocation().getZ() + num(args[3].substring(1)) : num(args[3]);
                    Location loc = new Location(tp.getWorld(), x, y, z, tp
                            .getLocation().getYaw(), tp.getLocation().getPitch());
                    if (tp.isInsideVehicle()) {
                        sender.sendMessage(ChatColor.RED + tp.getName() + " is on a ride, you can't teleport them!");
                        return true;
                    }
                    ParkManager.teleportUtil.log(tp, tp.getLocation());
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
            if (tp.isInsideVehicle() && !player.getUniqueId().equals(tp.getUniqueId()) && !player.getGameMode().equals(GameMode.SPECTATOR)) {
                sender.sendMessage(ChatColor.RED + tp.getName() + " is on a ride, you can't teleport to them! " +
                        "You must be in Spectator Mode to teleport to players on rides.");
                return true;
            }
            ParkManager.teleportUtil.log(player, player.getLocation());
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
            if (tp1.isInsideVehicle()) {
                sender.sendMessage(ChatColor.RED + tp1.getName() + " is on a ride, you can't teleport them!");
                return true;
            }
            if (tp2.isInsideVehicle() && !tp2.getGameMode().equals(GameMode.SPECTATOR)) {
                sender.sendMessage(ChatColor.RED + tp2.getName() + " is on a ride, you can't teleport to them!");
                return true;
            }
            ParkManager.teleportUtil.log(tp1, tp1.getLocation());
            tp1.teleport(tp2);
            player.sendMessage(ChatColor.GRAY + tp1.getName()
                    + " has been teleported to " + tp2.getName());
            return true;
        }
        if (args.length == 3) {
            try {
                double x = args[0].startsWith("~") ? player.getLocation().getX() + num(args[0].substring(1)) : num(args[0]);
                double y = args[1].startsWith("~") ? player.getLocation().getY() + num(args[1].substring(1)) : num(args[1]);
                double z = args[2].startsWith("~") ? player.getLocation().getZ() + num(args[2].substring(1)) : num(args[2]);
                Location loc = new Location(player.getWorld(), x, y, z, player.getLocation().getYaw(), player.getLocation().getPitch());
                ParkManager.teleportUtil.log(player, player.getLocation());
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
                double x = args[1].startsWith("~") ? player.getLocation().getX() + num(args[1].substring(1)) : num(args[1]);
                double y = args[2].startsWith("~") ? player.getLocation().getY() + num(args[2].substring(1)) : num(args[2]);
                double z = args[3].startsWith("~") ? player.getLocation().getZ() + num(args[3].substring(1)) : num(args[3]);
                Location loc = new Location(tp.getWorld(), x, y, z, player.getLocation().getYaw(), player.getLocation().getPitch());
                if (tp.isInsideVehicle()) {
                    sender.sendMessage(ChatColor.RED + tp.getName() + " is on a ride, you can't teleport to them!");
                    return true;
                }
                ParkManager.teleportUtil.log(tp, tp.getLocation());
                tp.teleport(loc);
                player.sendMessage(ChatColor.GRAY + tp.getName() + " has been teleported to " + x + ", " + y + ", " + z);
                return true;
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Error with numbers!");
                return true;
            }
        }
        player.sendMessage(ChatColor.RED + "/tp [Player] <Target> or /tp <x> <y> <z> or /tp [Player] <x> <y> <z>");
        return true;
    }

    private double num(String s) {
        if (s == null) {
            return 0;
        }
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }
}