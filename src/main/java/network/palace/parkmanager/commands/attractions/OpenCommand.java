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

@CommandMeta(description = "Open an attraction")
public class OpenCommand extends CoreCommand {

    public OpenCommand() {
        super("open");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "/attraction open [id]");
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
        attraction.setOpen(true);
        boolean queueUpdated = false;
        if (attraction.getLinkedQueue() != null) {
            Queue queue = ParkManager.getQueueManager().getQueue(attraction.getLinkedQueue(), park.getId());
            if (queue != null) {
                queue.setOpen(true);
                queueUpdated = true;
            }
        }
        if (queueUpdated) ParkManager.getQueueManager().saveToFile();
        ParkManager.getAttractionManager().saveToFile();
        player.sendMessage(attraction.getName() + ChatColor.GREEN + " has been opened!");
    }
}
