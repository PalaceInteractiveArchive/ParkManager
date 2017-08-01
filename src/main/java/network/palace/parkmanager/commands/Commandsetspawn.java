package network.palace.parkmanager.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CommandPermission;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.utils.FileUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;

@CommandMeta(description = "Set spawn location")
@CommandPermission(rank = Rank.WIZARD)
public class Commandsetspawn extends CoreCommand {

    public Commandsetspawn() {
        super("setspawn");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        Location loc = player.getLocation();
        FileUtil.setSpawn(loc);
        ParkManager.getInstance().setSpawn(loc);
        player.getLocation().getWorld().setSpawnLocation(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        player.sendMessage(ChatColor.GRAY + "Spawn Set!");
    }
}