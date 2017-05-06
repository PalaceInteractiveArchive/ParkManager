package us.mcmagic.parkmanager.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.parkmanager.ParkManager;
import us.mcmagic.parkmanager.listeners.BlockEdit;

/**
 * Created by Marc on 4/13/17.
 */
public class Commanduso implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            helpMenu("main", sender);
            return true;
        }
        if (args[0].equalsIgnoreCase("reload")) {
            ParkManager.ripRideRockit.initialize();
            sender.sendMessage(ChatColor.GREEN + "Reloaded!");
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "rrr": {
                if (args.length > 2) {
                    switch (args[1].toLowerCase()) {
                        case "choose": {
                            Player p = Bukkit.getPlayer(args[2]);
                            ParkManager.ripRideRockit.chooseSong(p);
                            return true;
                        }
                        case "start": {
                            Player p = Bukkit.getPlayer(args[2]);
                            ParkManager.ripRideRockit.startSong(p);
                            return true;
                        }
                    }
                }
                helpMenu("rrr", sender);
                return true;
            }
            case "mib": {
                if (args.length > 2) {
                    switch (args[1].toLowerCase()) {
                        case "add": {
                            Player p = Bukkit.getPlayer(args[2]);
                            if (BlockEdit.isInBuildMode(p.getUniqueId())) {
                                p.performCommand("build");
                                Bukkit.getScheduler().runTaskLater(ParkManager.getInstance(), () -> ParkManager.menInBlack.join(p), 20L);
                                return true;
                            }
                            ParkManager.menInBlack.join(p);
                            return true;
                        }
                        case "remove": {
                            Player p = Bukkit.getPlayer(args[2]);
                            ParkManager.menInBlack.done(p);
                            return true;
                        }
                    }
                }
                helpMenu("mib", sender);
                return true;
            }
        }
        helpMenu("main", sender);
        return true;
    }

    private static void helpMenu(String menu, CommandSender sender) {
        switch (menu) {
            case "main":
                sender.sendMessage(ChatColor.GREEN + "USO Commands:");
                sender.sendMessage(ChatColor.GREEN + "/uso rrr " + ChatColor.AQUA + "- Rip Ride Rockit");
                sender.sendMessage(ChatColor.GREEN + "/uso mib " + ChatColor.AQUA + "- Men In Black");
                break;
            case "rrr":
                sender.sendMessage(ChatColor.GREEN + "Rip Ride Rockit Commands:");
                sender.sendMessage(ChatColor.GREEN + "/uso rrr choose [Name] " + ChatColor.AQUA + "- Open music selection for player");
                sender.sendMessage(ChatColor.GREEN + "/uso rrr start [Name] " + ChatColor.AQUA + "- Start playing music for player");
                break;
            case "mib":
                sender.sendMessage(ChatColor.GREEN + "Men In Black Commands:");
                sender.sendMessage(ChatColor.GREEN + "/uso mib add [Name] " + ChatColor.AQUA + "- Add player to game");
                sender.sendMessage(ChatColor.GREEN + "/uso mib remove [Name] " + ChatColor.AQUA + "- Remove player from game");
        }
    }
}
