package us.mcmagic.parkmanager.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.permissions.Rank;
import us.mcmagic.mcmagiccore.player.PlayerUtil;
import us.mcmagic.mcmagiccore.player.User;
import us.mcmagic.parkmanager.ParkManager;

/**
 * Created by Jacob on 1/20/15.
 */
public class Commandautograph implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this!");
            return true;
        }
        Player player = (Player) sender;
        if (args.length != 1) {
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("remove")) {
                    Integer pageNum;
                    try {
                        pageNum = Integer.parseInt(args[1]);
                        ParkManager.autographManager.removeAutograph(player, pageNum);
                        return true;
                    } catch (NumberFormatException ignored) {
                    }
                }
                helpMenu(player);
                return true;
            }
            helpMenu(player);
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "accept":
                ParkManager.autographManager.acceptRequest(player);
                return true;
            case "deny":
                ParkManager.autographManager.denyRequest(player);
                return true;
        }
        User user = MCMagicCore.getUser(player.getUniqueId());
        if (user.getRank().getRankId() <= Rank.SHAREHOLDER.getRankId()) {
            helpMenu(player);
            return true;
        }
        Player tp = PlayerUtil.findPlayer(args[0]);
        if (tp == null) {
            player.sendMessage(ChatColor.RED + "Player not found!");
            return true;
        }
        if (tp.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You can't sign your own book!");
            return true;
        }
        ParkManager.autographManager.requestToSign(player, tp);
        return true;
    }


    private void helpMenu(Player player) {
        player.sendMessage(ChatColor.GREEN + "Autograph Book Commands:");
        User user = MCMagicCore.getUser(player.getUniqueId());
        if (user.getRank().getRankId() > Rank.SHAREHOLDER.getRankId()) {
            player.sendMessage(ChatColor.GREEN + "/autograph [user] " + ChatColor.AQUA +
                    "- Request to sign a player's book");
        }
        player.sendMessage(ChatColor.GREEN + "/autograph accept " + ChatColor.AQUA +
                "- Accepts signing request from player");
        player.sendMessage(ChatColor.GREEN + "/autograph deny " + ChatColor.AQUA +
                "- Denies signing request from player");
        player.sendMessage(ChatColor.GREEN + "/autograph remove [Page Number] " + ChatColor.AQUA +
                "- Remove a signature from your book");

    }
}