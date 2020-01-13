package network.palace.parkmanager.commands.attractions;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.attractions.Attraction;
import network.palace.parkmanager.handlers.Park;
import network.palace.parkmanager.queues.Queue;
import org.bukkit.ChatColor;

@CommandMeta(description = "Link a queue to an attraction")
public class LinkQueueCommand extends CoreCommand {

    public LinkQueueCommand() {
        super("linkqueue");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "/attraction linkqueue [attraction-id] [queue-id]");
            player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "Get attraction-id from /attraction list!");
            player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "Get queue-id from /queue list!");
            return;
        }
        Park park = ParkManager.getParkUtil().getPark(player.getLocation());
        if (park == null) {
            player.sendMessage(ChatColor.RED + "You must be inside a park when running this command!");
            return;
        }
        String attractionID = args[0];
        Attraction attraction = ParkManager.getAttractionManager().getAttraction(attractionID, park.getId());
        if (attraction == null) {
            player.sendMessage(ChatColor.RED + "Could not find an attraction by id " + attractionID + "!");
            return;
        }
        String queueID = args[1];
        Queue queue = ParkManager.getQueueManager().getQueueById(queueID);
        if (queue == null) {
            player.sendMessage(ChatColor.RED + "Could not find a queue by id " + queueID + "!");
            return;
        }
        attraction.setLinkedQueue(queue.getUuid());
        ParkManager.getAttractionManager().saveToFile();
        player.sendMessage(ChatColor.GREEN + "Successfully linked the attraction " + attraction.getName() +
                ChatColor.GREEN + " to the queue " + queue.getName() + "!");
    }
}
