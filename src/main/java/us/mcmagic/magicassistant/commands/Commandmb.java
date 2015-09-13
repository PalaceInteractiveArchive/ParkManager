package us.mcmagic.magicassistant.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.magicassistant.MagicAssistant;

public class Commandmb implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED
                    + "Only In-Game Players can use this Command!");
            return true;
        }
        Player player = (Player) sender;
        if (MagicAssistant.bandUtil.isLoading(player)) {
            player.sendMessage(ChatColor.GRAY + "Your MagicBand is currently initializing!");
            return true;
        }
        MagicAssistant.bandUtil.giveBandToPlayer(player);
        player.sendMessage(ChatColor.GRAY + "MagicBand has been restored!");
        return true;
    }
}