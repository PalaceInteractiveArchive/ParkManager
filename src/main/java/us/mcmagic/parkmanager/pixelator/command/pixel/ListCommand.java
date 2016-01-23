package us.mcmagic.parkmanager.pixelator.command.pixel;

import org.bukkit.command.CommandSender;
import us.mcmagic.parkmanager.ParkManager;
import us.mcmagic.parkmanager.pixelator.command.CommandDetails;
import us.mcmagic.parkmanager.pixelator.command.ICommand;
import us.mcmagic.parkmanager.pixelator.util.StringUtil;

@CommandDetails(
        name = "list",
        usage = "/pixel list",
        description = "Shows a list of available image maps",
        executableAsConsole = true,
        permission = "Pixelator.list"
)
public class ListCommand implements ICommand {

    public void execute(ParkManager plugin, CommandSender sender, String[] params) {
        if (plugin.pixelator.rendererManager.getRendererAmount() == 0) {
            sender.sendMessage("§3[§b§lPixelator§3]§r §cThere are no image maps!");
        } else {
            sender.sendMessage("§3[§b§lPixelator§3]§r §aAll available image maps:" +
                    StringUtil.toString(plugin.pixelator.rendererManager.getRenderers()));
        }
    }
}
