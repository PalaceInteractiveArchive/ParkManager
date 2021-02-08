package network.palace.parkmanager.commands.vqueue;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.queues.virtual.VirtualQueue;
import org.bukkit.ChatColor;

import java.util.logging.Level;

@CommandMeta(description = "Advance players in line for a virtual queue hosted on this server")
public class AdvanceCommand extends CoreCommand {

    public AdvanceCommand() {
        super("advance");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "/vqueue advance [id]");
            player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "Get the queue id from /vqueue list!");
            return;
        }
        VirtualQueue queue = ParkManager.getVirtualQueueManager().getQueueById(args[0]);
        if (queue == null) {
            player.sendMessage(ChatColor.RED + "Could not find a queue by id " + args[0] + "!");
            return;
        }
        if (!queue.isHost()) {
            player.sendMessage(ChatColor.RED + "You can only do that on the server hosting the queue (" +
                    ChatColor.GREEN + queue.getServer() + ChatColor.RED + ")!");
            return;
        }
        if (System.currentTimeMillis() - queue.getLastAdvance() < 10000) {
            // If it's been less than 10 seconds since the last advance
            player.sendMessage(ChatColor.AQUA + "You must wait at least 10 seconds before advancing the queue!");
            return;
        }
        if (queue.getMembers().isEmpty()) {
            player.sendMessage(ChatColor.AQUA + "The queue is currently empty!");
            return;
        }
        queue.setLastAdvance(System.currentTimeMillis());

        try {
            queue.admit();
        } catch (Exception e) {
            Core.getInstance().getLogger().log(Level.SEVERE, "Error advancing virtual queue", e);
            player.sendMessage(ChatColor.RED + "An error occurred while advancing that virtual queue, check console for details");
        }

        if (queue.getMembers().isEmpty()) {
            player.sendMessage(queue.getName() + ChatColor.GREEN + " has been advanced! The queue is now empty.");
        } else {
            player.sendMessage(queue.getName() + ChatColor.GREEN + " has been advanced! There are now " +
                    queue.getMembers().size() + " players in queue.");
        }
    }
}
