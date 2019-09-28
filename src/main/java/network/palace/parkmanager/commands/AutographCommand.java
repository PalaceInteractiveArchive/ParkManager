package network.palace.parkmanager.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.parkmanager.ParkManager;
import org.bukkit.ChatColor;

@CommandMeta(description = "Request to sign a player's book", aliases = {"a", "auto"})
public class AutographCommand extends CoreCommand {

    public AutographCommand() {
        super("autograph");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 1) {
            helpMenu(player);
            return;
        }
        switch (args[0].toLowerCase()) {
            case "accept": {
                ParkManager.getAutographManager().requestResponse(player, true);
                return;
            }
            case "deny": {
                ParkManager.getAutographManager().requestResponse(player, false);
                return;
            }
            case "remove": {
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "/autograph remove [Page Number]");
                    return;
                }
                Integer pageNum;
                try {
                    pageNum = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.RED + args[1] + " is not a number!");
                    return;
                }
                ParkManager.getAutographManager().removeAutograph(player, pageNum);
                return;
            }
            default: {
                if (player.getRank().getRankId() < Rank.SPECIALGUEST.getRankId()) {
                    helpMenu(player);
                    return;
                }
                CPlayer tp = Core.getPlayerManager().getPlayer(args[0]);
                if (tp == null) {
                    player.sendMessage(ChatColor.RED + "Player not found!");
                    return;
                }
                if (tp.getUniqueId().equals(player.getUniqueId())) {
                    player.sendMessage(ChatColor.RED + "You can't sign your own book!");
                    return;
                }
                ParkManager.getAutographManager().requestToSign(player, tp);
            }
        }
    }


    private void helpMenu(CPlayer player) {
        player.sendMessage(ChatColor.GREEN + "Autograph Book Commands:");
        CPlayer cp = Core.getPlayerManager().getPlayer(player.getUniqueId());
        if (cp.getRank().getRankId() >= Rank.SPECIALGUEST.getRankId()) {
            player.sendMessage(ChatColor.GREEN + "/autograph [user] " + ChatColor.AQUA +
                    "- Request to sign a player's book");
        }
        player.sendMessage(ChatColor.GREEN + "/autograph accept " + ChatColor.AQUA +
                "- Accepts signing request from a player");
        player.sendMessage(ChatColor.GREEN + "/autograph deny " + ChatColor.AQUA +
                "- Denies signing request from a player");
        player.sendMessage(ChatColor.GREEN + "/autograph remove [Page Number] " + ChatColor.AQUA +
                "- Remove a signature from your book");

    }
}