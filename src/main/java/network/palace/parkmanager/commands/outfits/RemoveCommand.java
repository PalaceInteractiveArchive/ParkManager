package network.palace.parkmanager.commands.outfits;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.MiscUtil;
import network.palace.parkmanager.ParkManager;
import org.bukkit.ChatColor;

@CommandMeta(description = "Remove an outfit")
public class RemoveCommand extends CoreCommand {

    public RemoveCommand() {
        super("remove");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "/outfit remove [id]");
            player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "Get the outfit id from /outfit list!");
            return;
        }
        if (!MiscUtil.checkIfInt(args[0])) {
            player.sendMessage(ChatColor.RED + args[0] + " is not an integer!");
            return;
        }
        int id = Integer.parseInt(args[0]);
        Core.runTaskAsynchronously(ParkManager.getInstance(), () -> Core.getMongoHandler().deleteOutfit(id));
        player.sendMessage(ChatColor.GREEN + "Successfully removed that outfit! Reload with '/outfit reload' to update this server.");
    }
}
