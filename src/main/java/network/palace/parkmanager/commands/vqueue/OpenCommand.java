package network.palace.parkmanager.commands.vqueue;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.dashboard.packets.parks.queue.UpdateQueuePacket;
import network.palace.parkmanager.queues.virtual.VirtualQueue;
import org.bukkit.ChatColor;

@CommandMeta(description = "Open a virtual queue hosted on this server")
public class OpenCommand extends CoreCommand {

    public OpenCommand() {
        super("open");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "/vqueue open [id]");
            player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "Get the queue id from /vqueue list!");
            return;
        }
        VirtualQueue queue = ParkManager.getVirtualQueueManager().getQueueById(args[0]);
        if (queue == null) {
            player.sendMessage(ChatColor.RED + "Could not find a queue by id " + args[0] + "!");
            return;
        }
        if (queue.cantEdit()) {
            player.sendMessage(ChatColor.RED + "You can only do that on the server hosting the queue (" +
                    ChatColor.GREEN + queue.getServer() + ChatColor.RED + ")!");
            return;
        }
        if (queue.isOpen()) {
            player.sendMessage(ChatColor.RED + "This queue is already open!");
            return;
        }
        queue.setOpen(true);
        Core.getDashboardConnection().send(new UpdateQueuePacket(queue.getId(), queue.isOpen(), null));
        player.sendMessage(queue.getName() + ChatColor.GREEN + " has been opened!");
    }
}
