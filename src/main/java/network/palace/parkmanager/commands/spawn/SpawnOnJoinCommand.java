package network.palace.parkmanager.commands.spawn;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import org.bukkit.ChatColor;

@CommandMeta(description = "Set the spawn-on-join setting")
public class SpawnOnJoinCommand extends CoreCommand {

    public SpawnOnJoinCommand() {
        super("spawnonjoin");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "/spawn spawnonjoin [true/false]");
            return;
        }
        ParkManager.getConfigUtil().setSpawnOnJoin(Boolean.parseBoolean(args[0]));
        if (ParkManager.getConfigUtil().isSpawnOnJoin()) {
            player.sendMessage(ChatColor.GREEN + "Enabled spawn-on-join!");
        } else {
            player.sendMessage(ChatColor.RED + "Disabled spawn-on-join!");
        }
    }
}
