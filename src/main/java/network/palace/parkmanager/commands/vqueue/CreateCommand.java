package network.palace.parkmanager.commands.vqueue;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.queues.virtual.VirtualQueueBuilder;
import org.bukkit.ChatColor;

@CommandMeta(description = "Create a new virtual queue hosted on this server")
public class CreateCommand extends CoreCommand {

    public CreateCommand() {
        super("create");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length == 1 && args[0].equalsIgnoreCase("exit")) {
            player.sendMessage(ChatColor.RED + "Exited the virtual queue builder!");
            player.getRegistry().removeEntry("vqueueBuilder");
            return;
        }
        if (player.getRegistry().hasEntry("vqueueBuilder")) {
            ((VirtualQueueBuilder) player.getRegistry().getEntry("vqueueBuilder")).nextStep(player, args);
            return;
        }
        VirtualQueueBuilder queue = new VirtualQueueBuilder();
        player.getRegistry().addEntry("vqueueBuilder", queue);
        player.sendMessage(ChatColor.GREEN + "You've started creating a new virtual queue! We're going to go step-by-step through this. " + ChatColor.YELLOW + "(Exit at any time with /vqueue create exit)");
        player.sendMessage(ChatColor.GREEN + "First, let's give your queue an id. This id is used to reference this queue in commands. Run " + ChatColor.YELLOW + "/vqueue create [id]");
    }
}
