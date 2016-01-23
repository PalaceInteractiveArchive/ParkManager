package us.mcmagic.parkmanager.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.mcmagiccore.player.PlayerUtil;

import java.util.HashSet;

public class Commandsmite implements CommandExecutor {

    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length == 1) {
                Player tp = PlayerUtil.findPlayer(args[0]);
                if (tp == null) {
                    sender.sendMessage(ChatColor.RED + "Player not found!");
                    return true;
                }
                strike(tp);
                return true;
            }
            sender.sendMessage(ChatColor.RED + "/smite [Username]");
            return true;
        }
        Player player = (Player) sender;
        if (args.length == 1) {
            Player tp = PlayerUtil.findPlayer(args[0]);
            if (tp == null) {
                player.sendMessage(ChatColor.RED + "Player not found!");
                return true;
            }
            player.sendMessage(ChatColor.GRAY + "Smiting " + tp.getName());
            strike(tp);
            return true;
        }
        player.getWorld().strikeLightning(player.getTargetBlock(new HashSet<Byte>(), 50).getLocation());
        return true;
    }

    private void strike(Player tp) {
        tp.getWorld().strikeLightning(tp.getLocation());
        tp.sendMessage(ChatColor.GRAY + "Thou hast been smitted!");
    }
}