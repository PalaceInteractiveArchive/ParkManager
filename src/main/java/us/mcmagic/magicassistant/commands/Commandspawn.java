package us.mcmagic.magicassistant.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.mcmagiccore.player.PlayerUtil;

public class Commandspawn implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length == 1) {
                Player tp = PlayerUtil.findPlayer(args[0]);
                if (tp == null) {
                    sender.sendMessage(ChatColor.RED + "Player not found!");
                }
                tp.teleport(MagicAssistant.spawn);
                return true;
            }
            sender.sendMessage(ChatColor.RED + "/spawn [Username]");
            return true;
        }
        Player player = (Player) sender;
        if (args.length == 1) {
            if (player.hasPermission("spawn.other")) {
                Player tp = PlayerUtil.findPlayer(args[0]);
                if (tp == null) {
                    player.sendMessage(ChatColor.RED + "Player not found!");
                }
                tp.teleport(MagicAssistant.spawn);
            } else {
                player.teleport(MagicAssistant.spawn);
            }
            return true;
        }
        player.teleport(MagicAssistant.spawn);
        return true;
    }
}