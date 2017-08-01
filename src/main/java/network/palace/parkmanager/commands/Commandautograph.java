package network.palace.parkmanager.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.parkmanager.ParkManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

/**
 * Created by Jacob on 1/20/15.
 */
@CommandMeta(description = "Request to sign a player's book", aliases = {"a", "auto"})
public class Commandautograph extends CoreCommand {

    public Commandautograph() {
        super("autograph");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length != 1) {
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("remove")) {
                    Integer pageNum;
                    try {
                        pageNum = Integer.parseInt(args[1]);
                        ParkManager.getInstance().getAutographManager().removeAutograph(player, pageNum);
                        return;
                    } catch (NumberFormatException ignored) {
                    }
                }
                helpMenu(player);
                return;
            }
            helpMenu(player);
            return;
        }
        switch (args[0].toLowerCase()) {
            case "accept":
                ParkManager.getInstance().getAutographManager().acceptRequest(player);
                return;
            case "deny":
                ParkManager.getInstance().getAutographManager().denyRequest(player);
                return;
        }
        CPlayer cp = Core.getPlayerManager().getPlayer(player.getUniqueId());
        if (cp.getRank().getRankId() <= Rank.HONORABLE.getRankId()) {
            helpMenu(player);
            return;
        }
        CPlayer tp = Core.getPlayerManager().getPlayer(Bukkit.getPlayer(args[0]));
        if (tp == null) {
            player.sendMessage(ChatColor.RED + "Player not found!");
            return;
        }
        if (tp.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You can't sign your own book!");
            return;
        }
        ParkManager.getInstance().getAutographManager().requestToSign(player, tp);
    }


    private void helpMenu(CPlayer player) {
        player.sendMessage(ChatColor.GREEN + "Autograph Book Commands:");
        CPlayer cp = Core.getPlayerManager().getPlayer(player.getUniqueId());
        if (cp.getRank().getRankId() >= Rank.SPECIALGUEST.getRankId()) {
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