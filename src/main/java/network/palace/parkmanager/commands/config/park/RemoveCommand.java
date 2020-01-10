package network.palace.parkmanager.commands.config.park;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.Park;
import network.palace.parkmanager.handlers.ParkType;
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
        ParkType type = ParkType.fromString(id);
        if (type == null) {
            player.sendMessage(ChatColor.RED + "That isn't a valid park id!");
            return;
        }
        Park park = ParkManager.getParkUtil().getPark(type);
        if (park == null) {
            player.sendMessage(ChatColor.RED + "A park doesn't exist on this server with the id " + id + "!");
            return;
        }
        if (ParkManager.getParkUtil().removePark(type)) {
            player.sendMessage(ChatColor.GREEN + "Successfully removed " + park.getId() + "!");
        } else {
            player.sendMessage(ChatColor.RED + "There was an error removing " + park.getId() + "!");
        }
    }
}
