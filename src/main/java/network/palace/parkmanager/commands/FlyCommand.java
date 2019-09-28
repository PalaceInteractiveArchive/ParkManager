package network.palace.parkmanager.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bukkit.ChatColor;

@CommandMeta(description = "Toggle player flight", rank = Rank.SPECIALGUEST)
public class FlyCommand extends CoreCommand {

    public FlyCommand() {
        super("fly");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 1 || player.getRank().getRankId() < Rank.TRAINEE.getRankId()) {
            toggleFlight(player, player);
            return;
        }
        CPlayer target = Core.getPlayerManager().getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(ChatColor.RED + "Player not found!");
            return;
        }
        toggleFlight(player, target);
    }

    private void toggleFlight(CPlayer sender, CPlayer target) {
        if (target.getAllowFlight()) {
            target.setAllowFlight(false);
            target.sendMessage(ChatColor.RED + "You can no longer fly!");
            if (!sender.getUniqueId().equals(target.getUniqueId()))
                sender.sendMessage(ChatColor.RED + target.getName() + " can no longer fly!");
        } else {
            target.setAllowFlight(true);
            target.sendMessage(ChatColor.GREEN + "You can now fly!");
            if (!sender.getUniqueId().equals(target.getUniqueId()))
                sender.sendMessage(ChatColor.GREEN + target.getName() + " can now fly!");
        }
    }
}
