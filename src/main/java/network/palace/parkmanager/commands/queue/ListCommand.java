package network.palace.parkmanager.commands.queue;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.queues.Queue;
import org.bukkit.ChatColor;

@CommandMeta(description = "List all queues")
public class ListCommand extends CoreCommand {

    public ListCommand() {
        super("list");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        player.sendMessage(ChatColor.GREEN + "Queues:");
        for (Queue queue : ParkManager.getQueueManager().getQueues()) {
            player.sendMessage(ChatColor.AQUA + "- [" + queue.getId() + "] " + ChatColor.YELLOW + queue.getName() + ChatColor.GREEN + " at " + ChatColor.YELLOW + "/warp " + queue.getWarp());
        }
    }
}
