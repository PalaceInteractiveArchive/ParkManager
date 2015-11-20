package us.mcmagic.magicassistant.pixelator.command;

import org.bukkit.command.CommandSender;
import us.mcmagic.magicassistant.MagicAssistant;

public interface ICommand {

    void execute(MagicAssistant var1, CommandSender var2, String[] var3);
}
