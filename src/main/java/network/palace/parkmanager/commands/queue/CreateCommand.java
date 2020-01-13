package network.palace.parkmanager.commands.queue;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.queues.QueueBuilder;
import org.bukkit.ChatColor;

@CommandMeta(description = "Create a new queue")
public class CreateCommand extends CoreCommand {

    public CreateCommand() {
        super("create");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length == 1 && args[0].equalsIgnoreCase("exit")) {
            player.sendMessage(ChatColor.RED + "Exited the queue builder!");
            player.getRegistry().removeEntry("queueBuilder");
            return;
        }
        if (player.getRegistry().hasEntry("queueBuilder")) {
            ((QueueBuilder) player.getRegistry().getEntry("queueBuilder")).nextStep(player, args);
            return;
        }
        QueueBuilder queue = new QueueBuilder();
        player.getRegistry().addEntry("queueBuilder", queue);
        player.sendMessage(ChatColor.GREEN + "You've started creating a new queue! We're going to go step-by-step through this. " + ChatColor.YELLOW + "(Exit at any time with /queue create exit)");
        player.sendMessage(ChatColor.GREEN + "First, let's give your queue an id. This id is used to reference this queue in commands. Run " + ChatColor.YELLOW + "/queue create [id]");
    }
}
