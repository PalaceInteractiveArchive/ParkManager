package network.palace.parkmanager.commands.food;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.MiscUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.food.FoodLocation;
import org.bukkit.ChatColor;

@CommandMeta(description = "Remove an existing food location")
public class RemoveCommand extends CoreCommand {

    public RemoveCommand() {
        super("remove");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "/food remove [id]");
            player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "Get the food location id from /food list!");
            return;
        }
        if (!MiscUtil.checkIfInt(args[0])) {
            player.sendMessage(ChatColor.RED + args[0] + " is not an integer!");
            return;
        }
        int id = Integer.parseInt(args[0]);
        FoodLocation food = ParkManager.getFoodManager().getFoodLocation(id);
        if (food == null) {
            player.sendMessage(ChatColor.RED + "Could not find a food location by id " + id + "!");
            return;
        }
        if (ParkManager.getFoodManager().removeFoodLocation(id)) {
            player.sendMessage(ChatColor.GREEN + "Successfully removed " + food.getName() + "!");
        } else {
            player.sendMessage(ChatColor.RED + "There was an error removing " + food.getName() + "!");
        }
    }
}
