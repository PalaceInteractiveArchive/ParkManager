package network.palace.parkmanager.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import network.palace.core.utils.MiscUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandMeta(description = "Set the lines of a sign", rank = Rank.MOD)
public class SetSignCommand extends CoreCommand {

    public SetSignCommand() {
        super("setsign");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (args.length < 4) {
            sender.sendMessage(ChatColor.AQUA + "Set sign lines:");
            sender.sendMessage(ChatColor.AQUA + "/setsign [x] [y] [z] line 1;line 2;line 3;line 4");
            sender.sendMessage(ChatColor.AQUA + "You need to provide text for the first line, but the rest are optional");
            sender.sendMessage(ChatColor.AQUA + "For example, '/setsign 1 1 1 hey;there' will skip the last two lines");
            sender.sendMessage(ChatColor.AQUA + "You can make a line blank as well, like '/setsign 1 1 1 hey;;there' (line two is blank)");
            return;
        }
        int x, y, z;
        boolean relativeX = false, relativeY = false, relativeZ = false;
        // If the command sender is a player or a command block, check for relative coordinates
        if (sender instanceof BlockCommandSender || sender instanceof Player) {
            if (args[0].contains("~")) {
                if (args[0].length() == 1) args[0] += "0";
                if (MiscUtil.checkIfInt(args[0].substring(1))) {
                    args[0] = args[0].substring(1);
                    relativeX = true;
                }
            }
            if (args[1].contains("~")) {
                if (args[1].length() == 1) args[1] += "0";
                if (MiscUtil.checkIfInt(args[1].substring(1))) {
                    args[1] = args[1].substring(1);
                    relativeY = true;
                }
            }
            if (args[2].contains("~")) {
                if (args[2].length() == 1) args[2] += "0";
                if (MiscUtil.checkIfInt(args[2].substring(1))) {
                    args[2] = args[2].substring(1);
                    relativeZ = true;
                }
            }
        }
        if (!MiscUtil.checkIfInt(args[0])) {
            sender.sendMessage(ChatColor.RED + "The 'x' coordinate must be an integer!");
            return;
        }
        if (!MiscUtil.checkIfInt(args[1])) {
            sender.sendMessage(ChatColor.RED + "The 'y' coordinate must be an integer!");
            return;
        }
        if (!MiscUtil.checkIfInt(args[2])) {
            sender.sendMessage(ChatColor.RED + "The 'z' coordinate must be an integer!");
            return;
        }
        x = Integer.parseInt(args[0]);
        y = Integer.parseInt(args[1]);
        z = Integer.parseInt(args[2]);
        Location loc;
        if (sender instanceof BlockCommandSender) {
            loc = ((BlockCommandSender) sender).getBlock().getLocation();
        } else if (sender instanceof Player) {
            loc = ((Player) sender).getLocation();
        } else {
            sender.sendMessage(ChatColor.RED + "Only players and command blocks can use this command!");
            return;
        }
        if (relativeX || relativeY || relativeZ) {
            if (relativeX) x = loc.getBlockX() + x;
            if (relativeY) y = loc.getBlockY() + y;
            if (relativeZ) z = loc.getBlockZ() + z;
        }
        Block b = loc.getWorld().getBlockAt(x, y, z);
        if (!b.getType().equals(Material.SIGN) && !b.getType().equals(Material.SIGN_POST) && !b.getType().equals(Material.WALL_SIGN)) {
            sender.sendMessage(ChatColor.RED + "There is no sign at " + x + "," + y + "," + z);
            return;
        }
        Sign s = (Sign) b.getState();
        StringBuilder command = new StringBuilder();
        for (int i = 3; i < args.length; i++) {
            command.append(args[i]).append(" ");
        }
        String[] commandLines = command.toString().trim().replaceAll(";;", "; ;").split(";");
        int size = Math.min(commandLines.length, 4);
        sender.sendMessage(ChatColor.GREEN + "Updating " + size + " lines...");
        for (int i = 0; i < size; i++) {
            s.setLine(i, ChatColor.translateAlternateColorCodes('&', commandLines[i].trim()));
            sender.sendMessage(ChatColor.GREEN + "Set line " + (i + 1) + " to:" + ChatColor.RESET + s.getLine(i));
        }
        s.update();
        sender.sendMessage(ChatColor.GREEN + "Updated sign at " + x + "," + y + "," + z);
    }
}
