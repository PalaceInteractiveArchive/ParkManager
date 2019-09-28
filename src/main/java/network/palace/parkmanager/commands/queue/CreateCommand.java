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
        player.sendMessage(ChatColor.GREEN + "You've started creating a new queue! We're going to go step-by-step through this. (Exit at any time with /queue exit)");
        player.sendMessage(ChatColor.GREEN + "First, let's give your queue a name. Run " + ChatColor.YELLOW + "/queue create [name]");
        player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "This name supports color codes! For example, '&aExample &dQueue' becomes '"
                + ChatColor.GREEN + "Example " + ChatColor.LIGHT_PURPLE + "Queue" + ChatColor.DARK_AQUA + "'.");
    }
}
