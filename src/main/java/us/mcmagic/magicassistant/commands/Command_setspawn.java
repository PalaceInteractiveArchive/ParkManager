package us.mcmagic.magicassistant.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.utils.FileUtil;

public class Command_setspawn implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only Players can use this command!");
            return true;
        }
        Player player = (Player) sender;
        FileUtil.setSpawn(player.getLocation());
        MagicAssistant.spawn = player.getLocation();
        player.sendMessage(ChatColor.GRAY + "Spawn Set!");
        return true;
    }
}