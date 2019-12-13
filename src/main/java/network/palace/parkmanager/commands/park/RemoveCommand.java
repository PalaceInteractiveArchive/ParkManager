package network.palace.parkmanager.commands.park;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.Park;
import org.bukkit.ChatColor;

@CommandMeta(description = "Remove an existing park")
public class RemoveCommand extends CoreCommand {

    public RemoveCommand() {
        super("remove");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "/park remove [id]");
            player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "Get the park id from /parkconfig park list!");
            return;
        }
        String id = args[0];
        Park park = ParkManager.getParkUtil().getPark(id);
        if (park == null) {
            player.sendMessage(ChatColor.RED + "Could not find a park by id " + id + "!");
            return;
        }
        if (ParkManager.getParkUtil().removePark(id)) {
            player.sendMessage(ChatColor.GREEN + "Successfully removed " + park.getId() + "!");
        } else {
            player.sendMessage(ChatColor.RED + "There was an error removing " + park.getId() + "!");
        }
    }
}
