package network.palace.parkmanager.commands.queue;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.MiscUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.queues.Queue;
import org.bukkit.ChatColor;

@CommandMeta(description = "Empty a queue")
public class EmptyCommand extends CoreCommand {

    public EmptyCommand() {
        super("empty");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "/queue empty [id]");
            player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "Get the queue id from /queue list!");
            return;
        }
        if (!MiscUtil.checkIfInt(args[0])) {
            player.sendMessage(ChatColor.RED + args[0] + " is not an integer!");
            return;
        }
        int id = Integer.parseInt(args[0]);
        Queue queue = ParkManager.getQueueManager().getQueue(id);
        if (queue == null) {
            player.sendMessage(ChatColor.RED + "Could not find a queue by id " + id + "!");
            return;
        }
        queue.emptyQueue();
        player.sendMessage(ChatColor.GREEN + "The queue for " + queue.getName() + ChatColor.GREEN + " has been emptied!");
    }
}
