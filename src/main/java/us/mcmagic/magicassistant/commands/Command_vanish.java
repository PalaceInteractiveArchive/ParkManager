package us.mcmagic.magicassistant.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.magicassistant.utils.PlayerUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Command_vanish {
    public static List<UUID> hidden = new ArrayList<>();

    @SuppressWarnings("deprecation")
    public static void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this!");
            return;
        }
        Player player = (Player) sender;
        if (args.length == 0) {
            if (hidden.contains(player.getUniqueId())) {
                hidden.remove(player.getUniqueId());
                for (Player tp : PlayerUtil.onlinePlayers()) {
                    tp.showPlayer(player);
                }
                player.sendMessage(ChatColor.DARK_AQUA
                        + "You have become visible.");
                for (Player tp : PlayerUtil.onlinePlayers()) {
                    if (tp.hasPermission("vanish.standard")
                            && !tp.equals(player)) {
                        tp.sendMessage(ChatColor.YELLOW + player.getName()
                                + " has become visible.");
                    }
                }
            } else {
                hidden.add(player.getUniqueId());
                for (Player tp : PlayerUtil.onlinePlayers()) {
                    if (!tp.hasPermission("vanish.standard")) {
                        tp.hidePlayer(player);
                    }
                }
                player.sendMessage(ChatColor.DARK_AQUA
                        + "You have vanished. Poof.");
                for (Player tp : PlayerUtil.onlinePlayers()) {
                    if (tp.hasPermission("vanish.standard")
                            && !tp.equals(player)) {
                        tp.sendMessage(ChatColor.YELLOW + player.getName()
                                + " has vanished. Poof.");
                    }
                }
            }
            return;
        }
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                StringBuilder list = new StringBuilder();
                for (Player tp : PlayerUtil.onlinePlayers()) {
                    if (hidden.contains(tp.getUniqueId())) {
                        if (list.length() > 0) {
                            list.append(ChatColor.DARK_AQUA);
                            list.append(", ");
                        }
                        list.append(ChatColor.AQUA);
                        list.append(tp.getName());
                    }
                }
                list.insert(0, "Vanished: ");
                list.insert(0, ChatColor.DARK_AQUA);
                player.sendMessage(list.toString());
                return;
            }
            if (args[0].equalsIgnoreCase("check")) {
                if (hidden.contains(player.getUniqueId())) {
                    player.sendMessage(ChatColor.DARK_AQUA
                            + "You are vanished.");
                } else {
                    player.sendMessage(ChatColor.DARK_AQUA
                            + "You are not vanished.");
                }
            }
        }
    }
}