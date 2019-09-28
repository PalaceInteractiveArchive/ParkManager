package network.palace.parkmanager.commands.attractions;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.MiscUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.attractions.Attraction;
import org.bukkit.ChatColor;

@CommandMeta(description = "Unlink a queue from an attraction")
public class UnlinkCommand extends CoreCommand {

    public UnlinkCommand() {
        super("unlink");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "/attraction unlink [attraction-id]");
            return;
        }
        if (!MiscUtil.checkIfInt(args[0])) {
            player.sendMessage(ChatColor.RED + args[0] + " is not an integer!");
            return;
        }
        int attractionID = Integer.parseInt(args[0]);
        Attraction attraction = ParkManager.getAttractionManager().getAttraction(attractionID);
        if (attraction == null) {
            player.sendMessage(ChatColor.RED + "Could not find an attraction by id " + attractionID + "!");
            return;
        }
        attraction.setLinkedQueue(null);
        ParkManager.getAttractionManager().saveToFile();
        player.sendMessage(ChatColor.GREEN + "Successfully unlinked " + attraction.getName() + "'s " + ChatColor.GREEN + "queue!");
    }
}
