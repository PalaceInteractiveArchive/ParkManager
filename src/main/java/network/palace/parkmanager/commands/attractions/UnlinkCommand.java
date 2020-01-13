package network.palace.parkmanager.commands.attractions;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.attractions.Attraction;
import network.palace.parkmanager.handlers.Park;
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
        Park park = ParkManager.getParkUtil().getPark(player.getLocation());
        if (park == null) {
            player.sendMessage(ChatColor.RED + "You must be inside a park when running this command!");
            return;
        }
        Attraction attraction = ParkManager.getAttractionManager().getAttraction(args[0], park.getId());
        if (attraction == null) {
            player.sendMessage(ChatColor.RED + "Could not find an attraction by id " + args[0] + "!");
            return;
        }
        attraction.setLinkedQueue(null);
        ParkManager.getAttractionManager().saveToFile();
        player.sendMessage(ChatColor.GREEN + "Successfully unlinked " + attraction.getName() + "'s " + ChatColor.GREEN + "queue!");
    }
}
