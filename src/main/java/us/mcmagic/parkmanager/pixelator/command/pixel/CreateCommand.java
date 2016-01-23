package us.mcmagic.parkmanager.pixelator.command.pixel;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import us.mcmagic.parkmanager.ParkManager;
import us.mcmagic.parkmanager.pixelator.command.CommandDetails;
import us.mcmagic.parkmanager.pixelator.command.ICommand;
import us.mcmagic.parkmanager.pixelator.renderer.SourceType;
import us.mcmagic.parkmanager.pixelator.renderer.types.MapImageRenderer;

import java.util.Arrays;

@CommandDetails(
        name = "create",
        usage = "/pixel create <source>",
        description = "Creates a new image map",
        executableAsConsole = true,
        permission = "Pixelator.create"
)
public class CreateCommand implements ICommand {

    public void execute(ParkManager plugin, CommandSender sender, String[] params) {
        String s = StringUtils.join(Arrays.copyOfRange(params, 0, params.length), " ");
        SourceType type = SourceType.determine(s);
        if (type == null) {
            sender.sendMessage("§3[§b§lPixelator§3]§r §cThe entered source path is invalid!");
        } else {
            MapImageRenderer i;
            try {
                i = new MapImageRenderer(plugin, s, type);
            } catch (Exception var8) {
                var8.printStackTrace();
                sender.sendMessage("§3[§b§lPixelator§3]§r §cThe entered source path doesn\'t contain a valid image!");
                return;
            }

            plugin.pixelator.rendererManager.register(i);
            sender.sendMessage("§3[§b§lPixelator§3]§r §aYou\'ve created a new image map with id §6" + i.getId() + "§a. §8(§c" + s + "§8)");
        }
    }
}