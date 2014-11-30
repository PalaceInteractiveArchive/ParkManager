package us.mcmagic.magicassistant.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.magicassistant.utils.NumberUtil;
import us.mcmagic.magicassistant.utils.PlayerUtil;

public class Command_ptime {

    public static void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length == 2) {
                Player tp = PlayerUtil.findPlayer(args[1]);
                if (tp == null) {
                    sender.sendMessage(ChatColor.RED + "Player not found!");
                    return;
                }
                args[0] = args[0].toLowerCase().replaceAll("ticks", "");
                switch (args[0]) {
                    case "day":
                        tp.setPlayerTime(1000, true);
                        tp.sendMessage(ChatColor.GREEN
                                + "Your time has been set to "
                                + ChatColor.DARK_AQUA + tp.getPlayerTime()
                                + ChatColor.GREEN + "!");
                        sender.sendMessage(ChatColor.DARK_AQUA + tp.getName()
                                + "'s " + ChatColor.GREEN + "time has been set to "
                                + ChatColor.DARK_AQUA + "1000" + ChatColor.GREEN
                                + "!");
                        break;
                    case "noon":
                        tp.setPlayerTime(6000, true);
                        tp.sendMessage(ChatColor.GREEN
                                + "Your time has been set to "
                                + ChatColor.DARK_AQUA + tp.getPlayerTime()
                                + ChatColor.GREEN + "!");
                        sender.sendMessage(ChatColor.DARK_AQUA + tp.getName()
                                + "'s " + ChatColor.GREEN + "time has been set to "
                                + ChatColor.DARK_AQUA + "6000" + ChatColor.GREEN
                                + "!");
                        break;
                    case "night":
                        tp.setPlayerTime(16000, true);
                        tp.sendMessage(ChatColor.GREEN
                                + "Your time has been set to "
                                + ChatColor.DARK_AQUA + tp.getPlayerTime()
                                + ChatColor.GREEN + "!");
                        sender.sendMessage(ChatColor.DARK_AQUA + tp.getName()
                                + "'s " + ChatColor.GREEN + "time has been set to "
                                + ChatColor.DARK_AQUA + "16000" + ChatColor.GREEN
                                + "!");
                        break;
                    case "reset":
                        tp.resetPlayerTime();
                        tp.sendMessage(ChatColor.GREEN
                                + "Your time now matches the server.");
                        sender.sendMessage(ChatColor.DARK_AQUA + tp.getName()
                                + "'s " + ChatColor.GREEN
                                + "time now matches the server.");
                        break;
                    default:
                        if (NumberUtil.isInt(args[0])) {
                            tp.setPlayerTime(Integer.parseInt(args[0]), true);
                            tp.sendMessage(ChatColor.GREEN
                                    + "Your time has been set to "
                                    + ChatColor.DARK_AQUA + tp.getPlayerTime()
                                    + ChatColor.GREEN + "!");
                            sender.sendMessage(ChatColor.DARK_AQUA + tp.getName()
                                    + "'s " + ChatColor.GREEN
                                    + "time has been set to " + ChatColor.DARK_AQUA
                                    + Integer.parseInt(args[0]) + ChatColor.GREEN
                                    + "!");
                        } else {
                            sender.sendMessage(ChatColor.RED
                                    + "/ptime [day/noon/night/1000/reset] [Username]");
                        }
                        break;
                }
            }
            sender.sendMessage(ChatColor.RED
                    + "/ptime [day/noon/night/1000/reset] [Username]");
            return;
        }
        Player player = (Player) sender;
        if (args.length == 1) {
            args[0] = args[0].toLowerCase().replaceAll("ticks", "");
            switch (args[0]) {
                case "day":
                    player.setPlayerTime(1000, true);
                    player.sendMessage(ChatColor.DARK_AQUA + player.getName()
                            + "'s " + ChatColor.GREEN + "time has been set to "
                            + ChatColor.DARK_AQUA + "1000" + ChatColor.GREEN + "!");
                    break;
                case "noon":
                    player.setPlayerTime(6000, true);
                    player.sendMessage(ChatColor.DARK_AQUA + player.getName()
                            + "'s " + ChatColor.GREEN + "time has been set to "
                            + ChatColor.DARK_AQUA + "6000" + ChatColor.GREEN + "!");
                    break;
                case "night":
                    player.setPlayerTime(16000, true);
                    player.sendMessage(ChatColor.DARK_AQUA + player.getName()
                            + "'s " + ChatColor.GREEN + "time has been set to "
                            + ChatColor.DARK_AQUA + "16000" + ChatColor.GREEN + "!");
                    break;
                case "reset":
                    player.resetPlayerTime();
                    player.sendMessage(ChatColor.GREEN
                            + "Your time now matches the server.");
                    break;
                default:
                    if (NumberUtil.isInt(args[0])) {
                        player.setPlayerTime(Integer.parseInt(args[0]), true);
                        player.sendMessage(ChatColor.DARK_AQUA + player.getName()
                                + "'s " + ChatColor.GREEN + "time has been set to "
                                + ChatColor.DARK_AQUA + Integer.parseInt(args[0])
                                + ChatColor.GREEN + "!");
                    } else {
                        player.sendMessage(ChatColor.RED
                                + "/ptime [day/noon/night/1000/reset] [Username]");
                    }
                    break;
            }
            return;
        }
        if (args.length == 2) {
            Player tp = PlayerUtil.findPlayer(args[1]);
            if (tp == null) {
                player.sendMessage(ChatColor.RED + "Player not found!");
                return;
            }
            args[0] = args[0].toLowerCase().replaceAll("ticks", "");
            switch (args[0]) {
                case "day":
                    tp.setPlayerTime(1000, true);
                    tp.sendMessage(ChatColor.GREEN + "Your time has been set to "
                            + ChatColor.DARK_AQUA + tp.getPlayerTime()
                            + ChatColor.GREEN + "!");
                    player.sendMessage(ChatColor.DARK_AQUA + tp.getName() + "'s "
                            + ChatColor.GREEN + "time has been set to "
                            + ChatColor.DARK_AQUA + "1000" + ChatColor.GREEN + "!");
                    break;
                case "noon":
                    tp.setPlayerTime(6000, true);
                    tp.sendMessage(ChatColor.GREEN + "Your time has been set to "
                            + ChatColor.DARK_AQUA + tp.getPlayerTime()
                            + ChatColor.GREEN + "!");
                    player.sendMessage(ChatColor.DARK_AQUA + tp.getName() + "'s "
                            + ChatColor.GREEN + "time has been set to "
                            + ChatColor.DARK_AQUA + "6000" + ChatColor.GREEN + "!");
                    break;
                case "night":
                    tp.setPlayerTime(16000, true);
                    tp.sendMessage(ChatColor.GREEN + "Your time has been set to "
                            + ChatColor.DARK_AQUA + tp.getPlayerTime()
                            + ChatColor.GREEN + "!");
                    player.sendMessage(ChatColor.DARK_AQUA + tp.getName() + "'s "
                            + ChatColor.GREEN + "time has been set to "
                            + ChatColor.DARK_AQUA + "16000" + ChatColor.GREEN + "!");
                    break;
                case "reset":
                    tp.resetPlayerTime();
                    tp.sendMessage(ChatColor.GREEN
                            + "Your time now matches the server.");
                    player.sendMessage(ChatColor.DARK_AQUA + tp.getName() + "'s "
                            + ChatColor.GREEN + "time now matches the server.");
                default:
                    if (NumberUtil.isInt(args[0])) {
                        tp.setPlayerTime(Integer.parseInt(args[0]), true);
                        tp.sendMessage(ChatColor.GREEN
                                + "Your time has been set to "
                                + ChatColor.DARK_AQUA + tp.getPlayerTime()
                                + ChatColor.GREEN + "!");
                        player.sendMessage(ChatColor.DARK_AQUA + tp.getName()
                                + "'s " + ChatColor.GREEN + "time has been set to "
                                + ChatColor.DARK_AQUA + Integer.parseInt(args[0])
                                + ChatColor.GREEN + "!");
                    } else {
                        player.sendMessage(ChatColor.RED
                                + "/ptime [day/noon/night/1000/reset] [Username]");
                    }
                    break;
            }
        }
        player.sendMessage(ChatColor.RED
                + "/ptime [day/noon/night/1000/reset] [Username]");
    }
}