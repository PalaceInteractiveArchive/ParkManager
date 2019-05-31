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
