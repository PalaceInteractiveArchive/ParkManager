package network.palace.parkmanager.commands.config;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import org.bukkit.ChatColor;

@CommandMeta(description = "Set the spawn location")
public class SpawnCommand extends CoreCommand {

    public SpawnCommand() {
        super("setspawn");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        ParkManager.getConfigUtil().setSpawn(player.getLocation());
        player.sendMessage(ChatColor.GRAY + "Set server spawn to where you're standing!");
    }
}
