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

@CommandMeta(description = "Close an attraction")
public class CloseCommand extends CoreCommand {

    public CloseCommand() {
        super("close");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "/attraction close [id]");
            player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "Get the attraction id from /attraction list!");
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
        attraction.setOpen(false);
        boolean queueUpdated = false;
        if (attraction.getLinkedQueue() != null) {
            Queue queue = ParkManager.getQueueManager().getQueue(attraction.getLinkedQueue());
            if (queue != null) {
                queue.setOpen(false);
                queueUpdated = true;
            }
        }
        if (queueUpdated) ParkManager.getQueueManager().saveToFile();
        ParkManager.getAttractionManager().saveToFile();
        player.sendMessage(attraction.getName() + ChatColor.RED + " has been closed!");
    }
}
