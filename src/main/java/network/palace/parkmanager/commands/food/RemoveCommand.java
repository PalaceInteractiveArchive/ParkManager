package network.palace.parkmanager.commands.food;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
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
        FoodLocation food = ParkManager.getFoodManager().getFoodLocation(args[0]);
        if (food == null) {
            player.sendMessage(ChatColor.RED + "Could not find a food location by id " + args[0] + "!");
            return;
        }
        if (ParkManager.getFoodManager().removeFoodLocation(args[0])) {
            player.sendMessage(ChatColor.GREEN + "Successfully removed " + food.getName() + "!");
        } else {
            player.sendMessage(ChatColor.RED + "There was an error removing " + food.getName() + "!");
        }
    }
}
