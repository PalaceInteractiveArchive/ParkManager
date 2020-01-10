package network.palace.parkmanager.commands.food;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.food.FoodLocation;
import network.palace.parkmanager.handlers.Park;
import org.bukkit.ChatColor;

@CommandMeta(description = "List all food locations")
public class ListCommand extends CoreCommand {

    public ListCommand() {
        super("list");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        Park park = ParkManager.getParkUtil().getPark(player.getLocation());
        if (park == null) {
            player.sendMessage(ChatColor.RED + "You must be inside a park when running this command!");
            return;
        }
        player.sendMessage(ChatColor.GREEN + "Food locations:");
        for (FoodLocation food : ParkManager.getFoodManager().getFoodLocations(park.getId())) {
            player.sendMessage(ChatColor.AQUA + "- [" + food.getId() + "] " + ChatColor.YELLOW + food.getName() + ChatColor.GREEN + " at " + ChatColor.YELLOW + "/warp " + food.getWarp());
        }
    }
}
