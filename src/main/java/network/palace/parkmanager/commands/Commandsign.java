package network.palace.parkmanager.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CommandPermission;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.parkmanager.ParkManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

/**
 * Created by Marc on 8/14/15
 */
@CommandMeta(description = "Sign an autograph book", aliases = "s")
@CommandPermission(rank = Rank.SPECIALGUEST)
public class Commandsign extends CoreCommand {

    public Commandsign() {
        super("sign");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (player.getRank().getRankId() <= Rank.DVCMEMBER.getRankId()) {
            player.sendMessage(ChatColor.RED + "You must be the " + Rank.SPECIALGUEST.getFormattedName() +
                    ChatColor.RED + " Rank or higher to do this!");
            return;
        }
        StringBuilder msg = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            msg.append(args[i]);
            if (i < (args.length - 1)) {
                msg.append(" ");
            }
        }
        String finalMsg = msg.toString();
        Bukkit.getScheduler().runTaskAsynchronously(ParkManager.getInstance(), () -> {
            player.sendMessage(ChatColor.GREEN + "Signing book...");
            ParkManager.getInstance().getAutographManager().sign(player, finalMsg);
        });
    }
}
