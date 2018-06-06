package network.palace.parkmanager.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CommandPermission;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.listeners.BlockEdit;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Created by Marc on 4/13/17.
 */
@CommandMeta(description = "USO-related features")
@CommandPermission(rank = Rank.MOD)
public class USOCommand extends CoreCommand {

    public USOCommand() {
        super("uso");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        ParkManager parkManager = ParkManager.getInstance();
        if (args.length == 0) {
            helpMenu("main", sender);
            return;
        }
        if (args[0].equalsIgnoreCase("reload")) {
            parkManager.getRipRideRockit().initialize();
            sender.sendMessage(ChatColor.GREEN + "Reloaded!");
            return;
        }
        switch (args[0].toLowerCase()) {
            case "rrr": {
                if (args.length > 2) {
                    switch (args[1].toLowerCase()) {
                        case "choose": {
                            CPlayer p = Core.getPlayerManager().getPlayer(Bukkit.getPlayer(args[2]));
                            parkManager.getRipRideRockit().chooseSong(p);
                            return;
                        }
                        case "start": {
                            CPlayer p = Core.getPlayerManager().getPlayer(Bukkit.getPlayer(args[2]));
                            parkManager.getRipRideRockit().startSong(p);
                            return;
                        }
                    }
                }
                helpMenu("rrr", sender);
                return;
            }
            case "mib": {
                if (args.length > 2) {
                    switch (args[1].toLowerCase()) {
                        case "add": {
                            CPlayer p = Core.getPlayerManager().getPlayer(Bukkit.getPlayer(args[2]));
                            if (BlockEdit.isInBuildMode(p.getUniqueId())) {
                                p.performCommand("build");
                                Bukkit.getScheduler().runTaskLater(parkManager, () -> parkManager.getMenInBlack().join(p), 20L);
                                return;
                            }
                            parkManager.getMenInBlack().join(p);
                            return;
                        }
                        case "remove": {
                            CPlayer p = Core.getPlayerManager().getPlayer(Bukkit.getPlayer(args[2]));
                            parkManager.getMenInBlack().done(p);
                            return;
                        }
                    }
                }
                helpMenu("mib", sender);
                return;
            }
        }
        helpMenu("main", sender);
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
