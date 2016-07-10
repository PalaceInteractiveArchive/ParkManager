package us.mcmagic.parkmanager.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.permissions.Rank;
import us.mcmagic.mcmagiccore.player.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Commandvanish implements CommandExecutor {
    private static List<UUID> hidden = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this!");
            return true;
        }
        Player player = (Player) sender;
        if (args.length == 0) {
            if (hidden.contains(player.getUniqueId())) {
                hidden.remove(player.getUniqueId());
                for (Player tp : Bukkit.getOnlinePlayers()) {
                    tp.showPlayer(player);
                }
                player.sendMessage(ChatColor.DARK_AQUA + "You have become visible.");
                for (Player tp : Bukkit.getOnlinePlayers()) {
                    User user = MCMagicCore.getUser(tp.getUniqueId());
                    if (user.getRank().getRankId() > Rank.SPECIALGUEST.getRankId() &&
                            !tp.getUniqueId().equals(player.getUniqueId())) {
                        tp.sendMessage(ChatColor.YELLOW + player.getName() + " has become visible.");
                    }
                }
            } else {
                hidden.add(player.getUniqueId());
                for (Player tp : Bukkit.getOnlinePlayers()) {
                    User user = MCMagicCore.getUser(tp.getUniqueId());
                    if (user.getRank().getRankId() < Rank.SPECIALGUEST.getRankId()) {
                        tp.hidePlayer(player);
                    }
                }
                player.sendMessage(ChatColor.DARK_AQUA + "You have vanished. Poof.");
                for (Player tp : Bukkit.getOnlinePlayers()) {
                    User user = MCMagicCore.getUser(tp.getUniqueId());
                    if (user.getRank().getRankId() > Rank.SPECIALGUEST.getRankId() &&
                            !tp.getUniqueId().equals(player.getUniqueId())) {
                        tp.sendMessage(ChatColor.YELLOW + player.getName() + " has vanished. Poof.");
                    }
                }
            }
            return true;
        }
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                StringBuilder list = new StringBuilder();
                Bukkit.getOnlinePlayers().stream().filter(tp -> hidden.contains(tp.getUniqueId())).forEach(tp -> {
                    if (list.length() > 0) {
                        list.append(ChatColor.DARK_AQUA);
                        list.append(", ");
                    }
                    list.append(ChatColor.AQUA);
                    list.append(tp.getName());
                });
                list.insert(0, "Vanished: ");
                list.insert(0, ChatColor.DARK_AQUA);
                player.sendMessage(list.toString());
                return true;
            }
            if (args[0].equalsIgnoreCase("check")) {
                if (hidden.contains(player.getUniqueId())) {
                    player.sendMessage(ChatColor.DARK_AQUA + "You are vanished.");
                } else {
                    player.sendMessage(ChatColor.DARK_AQUA + "You are visible.");
                }
            }
        }
        return true;
    }

    public static void vanish(Player player) {
        if (!hidden.contains(player.getUniqueId())) {
            hidden.add(player.getUniqueId());
        }
        for (Player tp : Bukkit.getOnlinePlayers()) {
            User user = MCMagicCore.getUser(tp.getUniqueId());
            if (user.getRank().getRankId() < Rank.SPECIALGUEST.getRankId()) {
                tp.hidePlayer(player);
            }
        }
        player.sendMessage(ChatColor.DARK_AQUA + "You have vanished. Poof.");
        for (Player tp : Bukkit.getOnlinePlayers()) {
            User user = MCMagicCore.getUser(tp.getUniqueId());
            if (user.getRank().getRankId() > Rank.SPECIALGUEST.getRankId() &&
                    !tp.getUniqueId().equals(player.getUniqueId())) {
                tp.sendMessage(ChatColor.YELLOW + player.getName() + " has vanished. Poof.");
            }
        }
    }

    public static List<UUID> getHidden() {
        return new ArrayList<>(hidden);
    }

    public static void unvanish(UUID uuid) {
        hidden.remove(uuid);
    }
}