package network.palace.parkmanager.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;

@CommandMeta(description = "Teleport to spawn")
public class SpawnCommand extends CoreCommand {

    public SpawnCommand() {
        super("spawn");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        Location spawn = ParkManager.getConfigUtil().getSpawn();
        if (spawn == null) {
            player.sendMessage(ChatColor.RED + "A spawn location hasn't been configured yet!");
            return;
        }
        player.sendMessage(ChatColor.GRAY + "Teleporting you to spawn...");
        player.teleport(spawn);
    }
}
