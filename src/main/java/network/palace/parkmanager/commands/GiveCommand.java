package network.palace.parkmanager.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CommandPermission;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import network.palace.core.utils.ItemUtil;
import network.palace.parkmanager.utils.NumberUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@CommandMeta(description = "Give a player an item")
@CommandPermission(rank = Rank.TRAINEEBUILD)
public class GiveCommand extends CoreCommand {

    public GiveCommand() {
        super("give");
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "/give [Player] [Numeric ID] [Amount]");
            return;
        }
        Player tp = Bukkit.getPlayer(args[0]);
        if (args.length == 2) {
            if (!NumberUtil.isInt(args[1])) {
                try {
                    if (args[1].contains(":")) {
                        String[] list = args[1].split(":");
                        int id;
                        byte data;
                        if (list.length > 1) {
                            id = Integer.parseInt(list[0]);
                            data = Byte.parseByte(list[1]);
                        } else {
                            id = Integer.parseInt(args[1]);
                            data = (byte) 0;
                        }
                        ItemStack item = ItemUtil.create(Material.getMaterial(id), 64, data);
                        tp.getInventory().addItem(item);
                        sender.sendMessage(ChatColor.GRAY + "Giving 64 of " +
                                item.getType().name().toLowerCase().replaceAll("_", " ") + " to " + tp.getName());
                        return;
                    }
                    Material mat = Material.getMaterial(args[1].toUpperCase());
                    if (mat != null) {
                        ItemStack item = new ItemStack(mat, 64);
                        tp.getInventory().addItem(item);
                        sender.sendMessage(ChatColor.GRAY + "Giving 64 of " +
                                item.getType().name().toLowerCase().replaceAll("_", " ") + " to " + tp.getName());
                        return;
                    }
                } catch (Exception e) {
                    sender.sendMessage(ChatColor.RED + "/give [Player] [Numeric ID] [Amount]");
                    return;
                }
                sender.sendMessage(ChatColor.RED + "/give [Player] [Numeric ID] [Amount]");
                return;
            }
            String[] list = args[1].split(":");
            try {
                int id;
                byte data;
                if (list.length > 1) {
                    id = Integer.parseInt(list[0]);
                    data = Byte.parseByte(list[1]);
                } else {
                    id = Integer.parseInt(args[1]);
                    data = (byte) 0;
                }
                ItemStack item = ItemUtil.create(Material.getMaterial(id), 64, data);
                tp.getInventory().addItem(item);
                sender.sendMessage(ChatColor.GRAY + "Giving 64 of " +
                        item.getType().name().toLowerCase().replaceAll("_", " ") + " to " + tp.getName());
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "There was an error, sorry!");
            }
            return;
        }
        if (args.length == 3) {
            if (!NumberUtil.isInt(args[2])) {
                sender.sendMessage(ChatColor.RED + "/give [Numeric ID] [Amount]");
                return;
            }
            int amount = Integer.parseInt(args[2]);
            if (!NumberUtil.isInt(args[1])) {
                try {
                    if (args[1].contains(":")) {
                        String[] list = args[1].split(":");
                        int id;
                        byte data;
                        if (list.length > 1) {
                            id = Integer.parseInt(list[0]);
                            data = Byte.parseByte(list[1]);
                        } else {
                            id = Integer.parseInt(args[1]);
                            data = (byte) 0;
                        }
                        ItemStack item = ItemUtil.create(Material.getMaterial(id), amount, data);
                        tp.getInventory().addItem(item);
                        sender.sendMessage(ChatColor.GRAY + "Giving " + amount + " of " +
                                item.getType().name().toLowerCase().replaceAll("_", " ") + " to " + tp.getName());
                        return;
                    }
                    Material mat = Material.getMaterial(args[1].toUpperCase());
                    if (mat != null) {
                        ItemStack item = new ItemStack(mat, amount);
                        tp.getInventory().addItem(item);
                        sender.sendMessage(ChatColor.GRAY + "Giving " + amount + " of " +
                                item.getType().name().toLowerCase().replaceAll("_", " ") + " to " + tp.getName());
                        return;
                    }
                } catch (Exception e) {
                    sender.sendMessage(ChatColor.RED + "/give [Numeric ID] [Amount]");
                    return;
                }
                sender.sendMessage(ChatColor.RED + "/give [Numeric ID] [Amount]");
                return;
            }
            String[] list = args[1].split(":");
            try {
                int id;
                byte data;
                if (list.length > 1) {
                    id = Integer.parseInt(list[0]);
                    data = Byte.parseByte(list[1]);
                } else {
                    id = Integer.parseInt(args[1]);
                    data = (byte) 0;
                }
                ItemStack item = ItemUtil.create(Material.getMaterial(id), amount, data);
                tp.getInventory().addItem(item);
                sender.sendMessage(ChatColor.GRAY + "Giving " + amount + " of " +
                        item.getType().name().toLowerCase().replaceAll("_", " ") + " to " + tp.getName());
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "There was an error, sorry!");
            }
            return;
        }
        sender.sendMessage(ChatColor.RED + "/give [Player] [Numeric ID] [Amount]");
    }
}
