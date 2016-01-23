package us.mcmagic.parkmanager.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import us.mcmagic.parkmanager.utils.NumberUtil;
import us.mcmagic.mcmagiccore.itemcreator.ItemCreator;
import us.mcmagic.mcmagiccore.player.PlayerUtil;

public class Commandgive implements CommandExecutor {

    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "/" + label + " [Player] [Numeric ID] [Amount]");
            return true;
        }
        Player tp = PlayerUtil.findPlayer(args[0]);
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
                        ItemStack item = new ItemCreator(Material.getMaterial(id), 64, data);
                        tp.getInventory().addItem(item);
                        sender.sendMessage(ChatColor.GRAY + "Giving 64 of " +
                                item.getType().name().toLowerCase().replaceAll("_", " ") + " to " + tp.getName());
                        return true;
                    }
                    Material mat = Material.getMaterial(args[1].toUpperCase());
                    if (mat != null) {
                        ItemStack item = new ItemStack(mat, 64);
                        tp.getInventory().addItem(item);
                        sender.sendMessage(ChatColor.GRAY + "Giving 64 of " +
                                item.getType().name().toLowerCase().replaceAll("_", " ") + " to " + tp.getName());
                        return true;
                    }
                } catch (Exception e) {
                    sender.sendMessage(ChatColor.RED + "/" + label + " [Player] [Numeric ID] [Amount]");
                    return true;
                }
                sender.sendMessage(ChatColor.RED + "/" + label + " [Player] [Numeric ID] [Amount]");
                return true;
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
                ItemStack item = new ItemCreator(Material.getMaterial(id), 64, data);
                tp.getInventory().addItem(item);
                sender.sendMessage(ChatColor.GRAY + "Giving 64 of " +
                        item.getType().name().toLowerCase().replaceAll("_", " ") + " to " + tp.getName());
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "There was an error, sorry!");
            }
            return true;
        }
        if (args.length == 3) {
            if (!NumberUtil.isInt(args[2])) {
                sender.sendMessage(ChatColor.RED + "/" + label + " [Numeric ID] [Amount]");
                return true;
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
                        ItemStack item = new ItemCreator(Material.getMaterial(id), amount, data);
                        tp.getInventory().addItem(item);
                        sender.sendMessage(ChatColor.GRAY + "Giving " + amount + " of " +
                                item.getType().name().toLowerCase().replaceAll("_", " ") + " to " + tp.getName());
                        return true;
                    }
                    Material mat = Material.getMaterial(args[1].toUpperCase());
                    if (mat != null) {
                        ItemStack item = new ItemStack(mat, amount);
                        tp.getInventory().addItem(item);
                        sender.sendMessage(ChatColor.GRAY + "Giving " + amount + " of " +
                                item.getType().name().toLowerCase().replaceAll("_", " ") + " to " + tp.getName());
                        return true;
                    }
                } catch (Exception e) {
                    sender.sendMessage(ChatColor.RED + "/" + label + " [Numeric ID] [Amount]");
                    return true;
                }
                sender.sendMessage(ChatColor.RED + "/" + label + " [Numeric ID] [Amount]");
                return true;
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
                ItemStack item = new ItemCreator(Material.getMaterial(id), amount, data);
                tp.getInventory().addItem(item);
                sender.sendMessage(ChatColor.GRAY + "Giving " + amount + " of " +
                        item.getType().name().toLowerCase().replaceAll("_", " ") + " to " + tp.getName());
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "There was an error, sorry!");
            }
            return true;
        }
        sender.sendMessage(ChatColor.RED + "/" + label + " [Player] [Numeric ID] [Amount]");
        return true;
    }
}