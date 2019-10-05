package network.palace.parkmanager.commands.queue;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.utils.MiscUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.queues.Queue;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@CommandMeta(description = "Un-pause movement of the queue")
public class UnpauseCommand extends CoreCommand {

    public UnpauseCommand() {
        super("unpause");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "/queue unpause [id]");
            sender.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "Get the queue id from /queue list!");
            return;
        }
        if (!MiscUtil.checkIfInt(args[0])) {
            sender.sendMessage(ChatColor.RED + args[0] + " is not an integer!");
            return;
        }
        int id = Integer.parseInt(args[0]);
        Queue queue = ParkManager.getQueueManager().getQueue(id);
        if (queue == null) {
            sender.sendMessage(ChatColor.RED + "Could not find a queue by id " + id + "!");
            return;
        }
        queue.setPaused(false);
        sender.sendMessage(queue.getName() + ChatColor.YELLOW + " has been un-paused!");
    }
}
