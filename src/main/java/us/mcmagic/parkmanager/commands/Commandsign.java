package us.mcmagic.parkmanager.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.parkmanager.ParkManager;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.permissions.Rank;
import us.mcmagic.mcmagiccore.player.User;

/**
 * Created by Marc on 8/14/15
 */
public class Commandsign implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        final Player player = (Player) sender;
        User user = MCMagicCore.getUser(player.getUniqueId());
        if (user.getRank().getRankId() <= Rank.DVCMEMBER.getRankId()) {
            player.sendMessage(ChatColor.RED + "You must be the " + Rank.SPECIALGUEST.getNameWithBrackets() +
                    ChatColor.RED + " Rank or higher to do this!");
            return true;
        }
        String msg = "";
        for (int i = 0; i < args.length; i++) {
            msg += args[i];
            if (i < (args.length - 1)) {
                msg += " ";
            }
        }
        final String finalMsg = msg;
        Bukkit.getScheduler().runTaskAsynchronously(ParkManager.getInstance(), new Runnable() {
            @Override
            public void run() {
                player.sendMessage(ChatColor.GREEN + "Signing book...");
                ParkManager.autographManager.sign(player, finalMsg);
            }
        });
        return true;
    }
}
