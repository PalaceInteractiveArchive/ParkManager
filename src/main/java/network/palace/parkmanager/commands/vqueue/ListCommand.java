package network.palace.parkmanager.commands.vqueue;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.queues.virtual.VirtualQueue;
import org.bukkit.ChatColor;

@CommandMeta(description = "List all virtual queues")
public class ListCommand extends CoreCommand {

    public ListCommand() {
        super("list");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        player.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "Virtual" + ChatColor.GREEN + " Queues:");
        for (VirtualQueue queue : ParkManager.getVirtualQueueManager().getQueues()) {
            player.sendMessage(ChatColor.AQUA + "- [" + queue.getId() + "] " + ChatColor.YELLOW + queue.getName() +
                    ChatColor.GREEN + " on " + ChatColor.YELLOW + queue.getServer());
        }
    }
}
