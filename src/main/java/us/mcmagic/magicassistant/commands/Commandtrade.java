package us.mcmagic.magicassistant.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.permissions.Rank;
import us.mcmagic.mcmagiccore.player.PlayerUtil;

/**
 * Created by Marc on 5/29/15
 */
public class Commandtrade implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this!");
            return true;
        }
        Player player = (Player) sender;
        if (args.length != 1) {
            helpMenu(player);
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "accept":
                MagicAssistant.tradeManager.acceptTrade(player);
                return true;
            case "deny":
                MagicAssistant.tradeManager.denyTrade(player);
                return true;
        }
        Player tp = PlayerUtil.findPlayer(args[0]);
        if (tp == null) {
            player.sendMessage(ChatColor.RED + "Player not found!");
            return true;
        }
        if (tp.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You can't trade with yourself!");
            return true;
        }
        if (MCMagicCore.getUser(player.getUniqueId()).getRank().getRankId() < Rank.INTERN.getRankId()) {
            if (MCMagicCore.getUser(tp.getUniqueId()).getRank().getRankId() > Rank.SPECIALGUEST.getRankId()) {
                player.sendMessage(ChatColor.RED + "You can't send this player a Trade Request!");
                return true;
            }
        }
        MagicAssistant.tradeManager.addTrade(player, tp);
        return true;
    }

    private void helpMenu(Player player) {
        player.sendMessage(ChatColor.YELLOW + "Trade Commands:");
        player.sendMessage(ChatColor.GREEN + "/trade [Username] " + ChatColor.YELLOW + "- Ask a player to Trade with you.");
        player.sendMessage(ChatColor.GREEN + "/trade accept " + ChatColor.YELLOW + "- Accept a pending Trade Request");
        player.sendMessage(ChatColor.GREEN + "/trade deny " + ChatColor.YELLOW + "- Deny a pending Trade Request");
    }
}
