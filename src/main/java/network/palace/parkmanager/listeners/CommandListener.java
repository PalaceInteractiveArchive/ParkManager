package network.palace.parkmanager.listeners;

import org.bukkit.Bukkit;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerCommandEvent;

public class CommandListener implements Listener {

    @EventHandler
    public void onCommand(ServerCommandEvent event) {
        CommandSender sender = event.getSender();
        // only fix if command block
        if (!(sender instanceof BlockCommandSender)) return;
        CommandBlock block = (CommandBlock) ((BlockCommandSender) sender).getBlock().getState();
        String cmd = block.getCommand();
        if (cmd.toLowerCase().startsWith("magic rc add ")) {
            cmd = "rc " + cmd.substring(13);
            block.setCommand(cmd);
            block.update();
            Bukkit.dispatchCommand(sender, cmd);
        }
    }
}
