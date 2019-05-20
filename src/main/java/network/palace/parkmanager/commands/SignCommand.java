package network.palace.parkmanager.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.parkmanager.ParkManager;
import org.bukkit.ChatColor;

@CommandMeta(description = "Sign an autograph book", aliases = "s", rank = Rank.SPECIALGUEST)
public class SignCommand extends CoreCommand {

    public SignCommand() {
        super("sign");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        StringBuilder msg = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            msg.append(args[i]);
            if (i < (args.length - 1)) {
                msg.append(" ");
            }
        }
        Core.runTaskAsynchronously(() -> {
            player.sendMessage(ChatColor.GREEN + "Signing book...");
            ParkManager.getAutographManager().sign(player, msg.toString());
        });
    }
}
