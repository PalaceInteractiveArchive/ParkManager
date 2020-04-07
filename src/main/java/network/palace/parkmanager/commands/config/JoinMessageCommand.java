package network.palace.parkmanager.commands.config;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.parkmanager.ParkManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@CommandMeta(description = "Set the message players are sent when they join")
public class JoinMessageCommand extends CoreCommand {

    public JoinMessageCommand() {
        super("joinmessage");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            String message = ParkManager.getConfigUtil().getJoinMessage();
            if (message.equals("none")) {
                sender.sendMessage(ChatColor.GREEN + "This server has no join message!");
            } else {
                sender.sendMessage(ChatColor.GREEN + "Current join message:");
                sender.sendMessage(message);
            }
            return;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("none")) {
            ParkManager.getConfigUtil().setJoinMessage("none");
            sender.sendMessage(ChatColor.RED + "Disable the join message for this server!");
            return;
        }
        StringBuilder msg = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            msg.append(args[i]);
            if (i < (args.length - 1)) msg.append(" ");
        }
        String message = ChatColor.translateAlternateColorCodes('&', msg.toString());
        ParkManager.getConfigUtil().setJoinMessage(message);
        sender.sendMessage(ChatColor.GREEN + "Set the join message to:");
        sender.sendMessage(message);
    }
}
