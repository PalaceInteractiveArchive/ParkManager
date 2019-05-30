package network.palace.parkmanager.commands.attractions;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.MiscUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.attractions.Attraction;
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
        if (!MiscUtil.checkIfInt(args[1])) {
            player.sendMessage(ChatColor.RED + args[1] + " is not an integer!");
            return;
        }
        int queueID = Integer.parseInt(args[0]);
        Queue queue = ParkManager.getQueueManager().getQueue(queueID);
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
