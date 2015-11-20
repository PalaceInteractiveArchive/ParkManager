package us.mcmagic.magicassistant.pixelator.command.pixel;

import org.bukkit.command.CommandSender;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.pixelator.command.CommandDetails;
import us.mcmagic.magicassistant.pixelator.command.ICommand;
import us.mcmagic.magicassistant.pixelator.util.StringUtil;

@CommandDetails(
        name = "list",
        usage = "/pixel list",
        description = "Shows a list of available image maps",
        executableAsConsole = true,
        permission = "Pixelator.list"
)
public class ListCommand implements ICommand {

    public void execute(MagicAssistant plugin, CommandSender sender, String[] params) {
        if (plugin.pixelator.rendererManager.getRendererAmount() == 0) {
            sender.sendMessage("§3[§b§lPixelator§3]§r §cThere are no image maps!");
        } else {
            sender.sendMessage("§3[§b§lPixelator§3]§r §aAll available image maps:" +
                    StringUtil.toString(plugin.pixelator.rendererManager.getRenderers()));
        }
    }
}
