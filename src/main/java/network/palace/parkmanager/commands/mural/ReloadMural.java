package network.palace.parkmanager.commands.mural;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.parkmanager.ParkManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@CommandMeta(description = "Reload murals from configs")
public class ReloadMural extends CoreCommand {

    public ReloadMural() {
        super("reload");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        sender.sendMessage(ChatColor.BLUE + "Reloading murals...");
        ParkManager.getMuralUtil().reload();
        sender.sendMessage(ChatColor.BLUE + "Murals reloaded!");
    }
}
