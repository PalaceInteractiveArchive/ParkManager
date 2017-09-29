package network.palace.parkmanager.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CommandPermission;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

/**
 * Created by Marc on 5/5/17.
 */
@CommandMeta(description = "Temporary sign update command")
@CommandPermission(rank = Rank.DEVELOPER)
public class Commandupdate extends CoreCommand {

    public Commandupdate() {
        super("update");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        Location loc = player.getLocation();
        int size = 20;
        if (args.length > 0) {
            size = Integer.parseInt(args[0]);
        }
        int count = 0;
        int countUpdated = 0;
        for (int x = loc.getBlockX() - size; x < loc.getBlockX() + size; x++) {
            for (int y = loc.getBlockY() - size; y < loc.getBlockY() + size; y++) {
                for (int z = loc.getBlockZ() - size; z < loc.getBlockZ() + size; z++) {
                    Block b = loc.getWorld().getBlockAt(x, y, z);
                    if (!b.getType().equals(Material.SIGN) && !b.getType().equals(Material.SIGN_POST) &&
                            !b.getType().equals(Material.WALL_SIGN)) {
                        continue;
                    }
                    Sign s = (Sign) b.getState();
                    String[] lines = s.getLines();
                    int i = 0;
                    boolean update = false;
                    for (String line : lines) {
                        if (line.equals("\"\"")) {
                            s.setLine(i, "");
                            update = true;
                        }
                        i++;
                    }
                    if (update) {
                        s.update();
                        countUpdated++;
                    }
                    count++;
                }
            }
        }
        player.sendMessage("Found " + count + " signs, updated " + countUpdated + " of them.");
    }
}
