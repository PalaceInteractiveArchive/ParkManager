package network.palace.parkmanager.commands.vqueue;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.chat.ComponentSerializer;
import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.messagequeue.packets.BroadcastComponentPacket;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.queues.virtual.VirtualQueue;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.logging.Level;

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
        if (!queue.isHost()) {
            sender.sendMessage(ChatColor.RED + "You can only do that on the server hosting the queue (" +
                    ChatColor.GREEN + queue.getServer() + ChatColor.RED + ")!");
            return;
        }
        String senderName;
        if (!(sender instanceof Player)) {
            if (sender instanceof BlockCommandSender) {
                Location loc = ((BlockCommandSender) sender).getBlock().getLocation();
                senderName = "" + Core.getInstanceName() + ", CMDBLK @ " + loc.getWorld().getName().toLowerCase() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
            } else {
                senderName = "Console on " + Core.getInstanceName();
            }
        } else {
            senderName = sender.getName();
        }
        StringBuilder s = new StringBuilder(ChatColor.GREEN + "");
        for (int i = 1; i < args.length; i++) {
            s.append(args[i]).append(" ");
        }
        BaseComponent[] components = new ComponentBuilder("")
                .append(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', s.toString().trim())))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        TextComponent.fromLegacyText(ChatColor.GREEN + "Click to join the virtual queue " + queue.getName() + "!")))
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/vqjoin " + queue.getId()))
                .create();
        BroadcastComponentPacket packet = new BroadcastComponentPacket(senderName, ComponentSerializer.toString(components));
        try {
            Core.getMessageHandler().sendMessage(packet, Core.getMessageHandler().ALL_PROXIES);
        } catch (IOException e) {
            Core.getInstance().getLogger().log(Level.SEVERE, "Error sending queue announcement", e);
            sender.sendMessage(ChatColor.RED + "An error occurred while sending that virtual queue announcement, check console for details.");
        }
        sender.sendMessage(ChatColor.GREEN + "Your announcement has been sent!");
    }
}
