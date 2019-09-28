package network.palace.parkmanager.commands.attractions;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.MiscUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.attractions.Attraction;
import org.bukkit.ChatColor;

@CommandMeta(description = "Remove an existing attraction")
public class RemoveCommand extends CoreCommand {

    public RemoveCommand() {
        super("remove");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "/attraction remove [id]");
            player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "Get the attraction id from /attraction list!");
            return;
        }
        if (!MiscUtil.checkIfInt(args[0])) {
            player.sendMessage(ChatColor.RED + args[0] + " is not an integer!");
            return;
        }
        int id = Integer.parseInt(args[0]);
        Attraction attraction = ParkManager.getAttractionManager().getAttraction(id);
        if (attraction == null) {
            player.sendMessage(ChatColor.RED + "Could not find an attraction by id " + id + "!");
            return;
        }
        if (ParkManager.getAttractionManager().removeAttraction(id)) {
            player.sendMessage(ChatColor.GREEN + "Successfully removed " + attraction.getName() + "!");
        } else {
            player.sendMessage(ChatColor.RED + "There was an error removing " + attraction.getName() + "!");
        }
    }
}
