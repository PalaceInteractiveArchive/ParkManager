package network.palace.parkmanager.commands.vqueue;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.queues.virtual.VirtualQueue;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@CommandMeta(description = "List all virtual queues")
public class ListCommand extends CoreCommand {

    public ListCommand() {
        super("list");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        sender.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "Virtual" + ChatColor.GREEN + " Queues:");
        for (VirtualQueue queue : ParkManager.getVirtualQueueManager().getQueues()) {
            sender.sendMessage(ChatColor.AQUA + "- [" + queue.getId() + "] " + ChatColor.YELLOW + queue.getName() +
                    ChatColor.GREEN + " on " + ChatColor.YELLOW + queue.getServer() + ChatColor.GREEN + " is " +
                    (queue.isOpen() ? ChatColor.GREEN + "open" : ChatColor.RED + "closed") + ChatColor.GREEN +
                    " with " + ChatColor.YELLOW + queue.getMembers().size() + " players" + ChatColor.GREEN + " in queue");
        }
    }
}
