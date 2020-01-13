package network.palace.parkmanager.commands.queue;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.attractions.Attraction;
import network.palace.parkmanager.handlers.Park;
import network.palace.parkmanager.queues.Queue;
import org.bukkit.ChatColor;

@CommandMeta(description = "Open a queue")
public class OpenCommand extends CoreCommand {

    public OpenCommand() {
        super("open");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "/queue open [id]");
            player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "Get the queue id from /queue list!");
            return;
        }
        Park park = ParkManager.getParkUtil().getPark(player.getLocation());
        if (park == null) {
            player.sendMessage(ChatColor.RED + "You must be inside a park when running this command!");
            return;
        }
        Queue queue = ParkManager.getQueueManager().getQueueById(args[0]);
        if (queue == null) {
            player.sendMessage(ChatColor.RED + "Could not find a queue by id " + args[0] + "!");
            return;
        }
        queue.setOpen(true);
        boolean attractionUpdate = false;
        for (Attraction attraction : ParkManager.getAttractionManager().getAttractions(park.getId())) {
            if (attraction.getLinkedQueue() == null || !attraction.getLinkedQueue().equals(queue.getUuid())) continue;
            attraction.setOpen(true);
            attractionUpdate = true;
        }
        if (attractionUpdate) ParkManager.getAttractionManager().saveToFile();
        ParkManager.getQueueManager().saveToFile();
        player.sendMessage(queue.getName() + ChatColor.GREEN + " has been opened!");
    }
}
