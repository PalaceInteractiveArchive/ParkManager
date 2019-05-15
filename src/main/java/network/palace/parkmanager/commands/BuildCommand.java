package network.palace.parkmanager.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.parkmanager.ParkManager;
import org.bukkit.ChatColor;

@CommandMeta(description = "Toggle build mode", rank = Rank.TRAINEEBUILD)
public class BuildCommand extends CoreCommand {

    public BuildCommand() {
        super("build");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (player.getRegistry().hasEntry("buildModeTimeout") && (System.currentTimeMillis() - (long) player.getRegistry().getEntry("buildModeTimeout")) < 1000) {
            player.sendMessage(ChatColor.RED + "You must wait at least 1s before doing this again!");
            return;
        }
        if (!ParkManager.getBuildUtil().canToggleBuildMode(player)) return;
        player.getRegistry().addEntry("buildModeTimeout", System.currentTimeMillis());
        player.sendMessage(ChatColor.YELLOW + "You have " + (ParkManager.getBuildUtil().toggleBuildMode(player) ? "entered" : "exited") + " Build Mode");
    }
}
