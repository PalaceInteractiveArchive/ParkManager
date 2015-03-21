package us.mcmagic.magicassistant.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.magicassistant.MagicAssistant;

/**
 * Created by Marc on 3/20/15
 */
public class Command_pack implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        MagicAssistant.getInstance().packManager.openMenu((Player) sender);
        return true;
    }
}
