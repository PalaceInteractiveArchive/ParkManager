package network.palace.parkmanager.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.parkmanager.ParkManager;
import org.bukkit.ChatColor;

@CommandMeta(description = "Open the Pack settings menu")
public class PackCommand extends CoreCommand {

    public PackCommand() {
        super("pack");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 0 || player.getRank().getRankId() < Rank.MOD.getRankId()) {
            ParkManager.getPackManager().openMenu(player);
            return;
        }
        if (args.length < 2 || !args[0].equalsIgnoreCase("setpack")) {
            player.sendMessage(ChatColor.AQUA + "/pack setpack [pack] - Set the server's pack");
            return;
        }
        ParkManager.getPackManager().setServerPack(args[1]);
        player.sendMessage(ChatColor.GREEN + "Set this server's pack setting to " + ChatColor.YELLOW + args[1] + "!");
    }
}
