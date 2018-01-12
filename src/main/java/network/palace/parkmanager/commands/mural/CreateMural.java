package network.palace.parkmanager.commands.mural;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.mural.Mural;
import org.bukkit.ChatColor;

@CommandMeta(description = "Create a new mural")
public class CreateMural extends CoreCommand {

    public CreateMural() {
        super("create");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 7) {
            player.sendMessage(ChatColor.RED + "/mural create name x1 y1 z1 x2 y2 z2");
            return;
        }
        String name = args[0];
        int minX = parseInt(args[1]);
        int minY = parseInt(args[2]);
        int minZ = parseInt(args[3]);
        int maxX = parseInt(args[4]);
        int maxY = parseInt(args[5]);
        int maxZ = parseInt(args[6]);
        Mural mural = ParkManager.getMuralUtil().createMural(name, Math.min(minX, maxX), Math.max(minX, maxX),
                Math.min(minY, maxY), Math.max(minY, maxY), Math.min(minZ, maxZ), Math.max(minZ, maxZ));
        player.sendMessage(ChatColor.GREEN + "New mural created!");
    }

    private static int parseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static long parseLong(String s) {
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
