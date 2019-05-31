package network.palace.parkmanager.commands.spawn;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import org.bukkit.ChatColor;

@CommandMeta(description = "Set the warp-on-join setting")
public class WarpOnJoinCommand extends CoreCommand {

    public WarpOnJoinCommand() {
        super("warponjoin");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "/spawn warponjoin [true/false]");
            return;
        }
        ParkManager.getConfigUtil().setWarpOnJoin(Boolean.parseBoolean(args[0]));
        if (ParkManager.getConfigUtil().isWarpOnJoin()) {
            player.sendMessage(ChatColor.GREEN + "Enabled warp-on-join!");
        } else {
            player.sendMessage(ChatColor.RED + "Disabled warp-on-join!");
        }
    }
}
