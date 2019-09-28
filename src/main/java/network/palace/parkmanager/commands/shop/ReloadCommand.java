package network.palace.parkmanager.commands.shop;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import org.bukkit.ChatColor;

@CommandMeta(description = "Reload shops from filesystem")
public class ReloadCommand extends CoreCommand {

    public ReloadCommand() {
        super("reload");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        player.sendMessage(ChatColor.GREEN + "Reloading shops from filesystem...");
        ParkManager.getShopManager().initialize();
        player.sendMessage(ChatColor.GREEN + "Finished reloading shops!");
    }
}
