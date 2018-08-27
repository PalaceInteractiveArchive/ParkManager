package network.palace.parkmanager.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.outline.OutlineSession;
import network.palace.parkmanager.outline.Point;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.List;

/**
 * @author Marc
 * @since 10/20/17
 */
@CommandMeta(description = "Outline command", aliases = "out", rank = Rank.MOD)
public class OutlineCommand extends CoreCommand {

    public OutlineCommand() {
        super("outline");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length == 0) {
            helpMenu("main", player);
            return;
        }
        OutlineSession session = ParkManager.getInstance().getOutlineManager().getSession(player.getUniqueId());
        String s1 = args[0];
        if (args.length == 1) {
            if (s1.equalsIgnoreCase("undo")) {
                if (session.undo()) {
                    player.sendMessage(ChatColor.GREEN + "Undo successful!");
                } else {
                    player.sendMessage(ChatColor.RED + "Error undoing! (Maybe you have nothing to undo?)");
                }
                return;
            }
            helpMenu(s1, player);
            return;
        }
        String s2 = args[1];
        if (args.length == 2) {
            switch (s1.toLowerCase()) {
                case "point": {
                    if (s2.equalsIgnoreCase("list")) {
                        List<Point> points = ParkManager.getInstance().getOutlineManager().getPoints();
                        if (points.isEmpty()) {
                            player.sendMessage(ChatColor.RED + "No points exist on this server!");
                            return;
                        }
                        player.sendMessage(ChatColor.GREEN + "Outline Points:");
                        for (Point p : points) {
                            player.sendMessage(ChatColor.AQUA + "- " + p.getName() + " x: " + p.getX() + ", z: " + p.getZ());
                        }
                        return;
                    }
                    Point p = ParkManager.getInstance().getOutlineManager().getPoint(s2);
                    if (p == null) {
                        helpMenu("point", player);
                        return;
                    }
                    session.setSessionPoint(p);
                    player.sendMessage(ChatColor.GREEN + "Your session point is now " + ChatColor.AQUA + p.getName());
                    return;
                }
                case "setblock": {
                    Material type = Material.valueOf(s2);
                    if (type == null) {
                        player.sendMessage(ChatColor.RED + "No block type '" + s2 + "'!");
                        return;
                    }
                    session.setType(type);
                    player.sendMessage(ChatColor.GREEN + "Set your block type to " + ChatColor.AQUA + type);
                    return;
                }
                default: {
                    try {
                        double length = Double.parseDouble(s1);
                        double heading = Double.parseDouble(s2);
                        Location loc = session.outline(length, heading);
                        if (loc == null) {
                            player.sendMessage(ChatColor.RED + "There was an error creating that outline! Do you have a starting point selected!");
                            return;
                        }
                        player.sendMessage(ChatColor.GREEN + "Placed a block at " + loc.getBlockX() + "," +
                                loc.getBlockY() + "," + loc.getBlockZ());
                    } catch (NumberFormatException e) {
                        helpMenu("main", player);
                    }
                    return;
                }
            }
        }
        String s3 = args[2];
        if (args.length == 3) {
            if (!s1.equalsIgnoreCase("point") || !s2.equalsIgnoreCase("remove")) {
                helpMenu("main", player);
                return;
            }
            if (ParkManager.getInstance().getOutlineManager().removePoint(s3)) {
                player.sendMessage(ChatColor.GREEN + "Point removed successfully!");
            } else {
                player.sendMessage(ChatColor.RED + "No point exists with the name '" + s3 + "'!");
            }
            return;
        }
        String s4 = args[3];
        if (args.length == 4) {
            if (!s1.equalsIgnoreCase("point") || !s2.equalsIgnoreCase("create")) {
                helpMenu("main", player);
                return;
            }
            try {
                String[] list = s4.split(",");
                int x = Integer.parseInt(list[0]);
                int z = Integer.parseInt(list[1]);
                Point p = new Point(s3, x, z);
                ParkManager.getInstance().getOutlineManager().addPoint(p);
                player.sendMessage(ChatColor.GREEN + "Point added successfully!");
            } catch (Exception e) {
                e.printStackTrace();
                player.sendMessage(ChatColor.RED + "There was an error while creating that point!");
            }
            return;
        }
        helpMenu("main", player);
    }

    public static void helpMenu(String menu, CPlayer player) {
        switch (menu) {
            case "point": {
                player.sendMessage(ChatColor.GREEN + "Point Commands:");
                player.sendMessage(ChatColor.GREEN + "/outline point list " + ChatColor.AQUA +
                        "- List all configured outline points");
                player.sendMessage(ChatColor.GREEN + "/outline point create [name] [x,z] " + ChatColor.AQUA +
                        "- Save a new outline point");
                player.sendMessage(ChatColor.GREEN + "/outline point remove [name] " + ChatColor.AQUA +
                        "- Remove a saved point");
                player.sendMessage(ChatColor.GREEN + "/outline point [name] " + ChatColor.AQUA +
                        "- Select a starting point for your session");
                break;
            }
            default: {
                player.sendMessage(ChatColor.GREEN + "Outline Commands:");
                player.sendMessage(ChatColor.GREEN + "/outline point " + ChatColor.AQUA +
                        "- Commands for managing starting points");
                player.sendMessage(ChatColor.GREEN + "/outline [length] [heading]" + ChatColor.AQUA +
                        "- Place a block at this outline location");
                player.sendMessage(ChatColor.GREEN + "/outline undo" + ChatColor.AQUA +
                        "- Undo your latest outline");
                player.sendMessage(ChatColor.GREEN + "/outline setblock [type] " + ChatColor.AQUA +
                        "- Set the block type for your session (optional, default is gold block)");
                break;
            }
        }
    }
}
