package network.palace.parkmanager.commands.vqueue;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.dashboard.packets.parks.queue.AnnounceQueuePacket;
import network.palace.parkmanager.queues.virtual.VirtualQueue;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@CommandMeta(description = "Announce a virtual queue hosted on this server")
public class AnnounceCommand extends CoreCommand {

    public AnnounceCommand() {
        super("announce");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "/vqueue announce [id] [Announcement Text]");
            sender.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "Get the queue id from /vqueue list!");
            sender.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "Clicking the Announcement Text will let players join the queue!");
            return;
        }
        VirtualQueue queue = ParkManager.getVirtualQueueManager().getQueueById(args[0]);
        if (queue == null) {
            sender.sendMessage(ChatColor.RED + "Could not find a queue by id " + args[0] + "!");
            return;
        }
        if (queue.cantEdit()) {
            sender.sendMessage(ChatColor.RED + "You can only do that on the server hosting the queue (" +
                    ChatColor.GREEN + queue.getServer() + ChatColor.RED + ")!");
            return;
        }
        StringBuilder s = new StringBuilder(ChatColor.GREEN + "");
        for (int i = 1; i < args.length; i++) {
            s.append(args[i]).append(" ");
        }
        Core.getDashboardConnection().send(new AnnounceQueuePacket(queue.getId(), ChatColor.translateAlternateColorCodes('&', s.toString().trim())));
        sender.sendMessage(ChatColor.GREEN + "Your announcement has been sent!");
    }
}
