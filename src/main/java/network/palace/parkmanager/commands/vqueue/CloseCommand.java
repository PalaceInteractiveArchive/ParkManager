package network.palace.parkmanager.commands.vqueue;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.message.UpdateQueuePacket;
import network.palace.parkmanager.queues.virtual.VirtualQueue;
import org.bukkit.ChatColor;

import java.util.logging.Level;

@CommandMeta(description = "Close a virtual queue hosted on this server")
public class CloseCommand extends CoreCommand {

    public CloseCommand() {
        super("close");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "/vqueue close [id]");
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
        if (!queue.isOpen()) {
            player.sendMessage(ChatColor.RED + "This queue is already closed!");
            return;
        }
        queue.setOpen(false);
        try {
            Core.getMessageHandler().sendMessage(new UpdateQueuePacket(queue.getId(), queue.isOpen(), null), Core.getMessageHandler().permanentClients.get("all_parks"));
            Core.getMessageHandler().sendStaffMessage(ChatColor.GREEN + "A virtual queue (" + queue.getName() +
                    ChatColor.GREEN + ") has been " + (queue.isOpen() ? "opened" : "closed"));
        } catch (Exception e) {
            Core.getInstance().getLogger().log(Level.SEVERE, "Error removing virtual queue", e);
            player.sendMessage(ChatColor.RED + "An error occurred while closing that virtual queue, check console for details");
        }
    }
}
