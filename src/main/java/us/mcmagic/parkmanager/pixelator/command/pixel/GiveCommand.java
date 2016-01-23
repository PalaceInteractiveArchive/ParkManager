package us.mcmagic.parkmanager.pixelator.command.pixel;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import us.mcmagic.parkmanager.ParkManager;
import us.mcmagic.parkmanager.pixelator.command.CommandDetails;
import us.mcmagic.parkmanager.pixelator.command.ICommand;
import us.mcmagic.parkmanager.pixelator.renderer.types.MapImageRenderer;
import us.mcmagic.parkmanager.pixelator.util.PlayerUtil;

@CommandDetails(
        name = "give",
        usage = "/pixel give <id>",
        description = "Gives you the desired image map",
        executableAsConsole = false,
        permission = "Pixelator.give"
)
public class GiveCommand implements ICommand {

    public void execute(ParkManager plugin, CommandSender sender, String[] params) {
        Player p = (Player) sender;
        short id;
        try {
            id = Short.parseShort(params[0]);
        } catch (Exception var8) {
            p.sendMessage("§3[§b§lPixelator§3]§r §6" + params[0] + " §cisn\'t numeric!");
            return;
        }
        MapImageRenderer m = plugin.pixelator.rendererManager.getRenderer(id);
        if (m == null) {
            p.sendMessage("§3[§b§lPixelator§3]§r §cThere\'s no image map with this id!");
        } else {
            ItemStack i;
            i = m.createMap();
            if (!PlayerUtil.hasEnoughSpace(p, i)) {
                p.sendMessage("§3[§b§lPixelator§3]§r §cYou don\'t have enough space!");
                return;
            }
            p.getInventory().addItem(i);
            p.sendMessage("§3[§b§lPixelator§3]§r §aYou\'ve been given the image map with id §6" + id + "!");
        }
    }
}
