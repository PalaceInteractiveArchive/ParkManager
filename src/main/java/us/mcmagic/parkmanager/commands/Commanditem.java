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

/**
 * Created by Marc on 3/10/15
 */
public class Commanditem implements CommandExecutor {

    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this! Try /give instead.");
            return true;
        }
        Player player = (Player) sender;
        if (args.length == 1) {
            if (!NumberUtil.isInt(args[0])) {
                try {
                    if (args[0].contains(":")) {
                        String[] list = args[0].split(":");
                        int id;
                        byte data;
                        if (list.length > 1) {
                            id = Integer.parseInt(list[0]);
                            data = Byte.parseByte(list[1]);
                        } else {
                            id = Integer.parseInt(args[0]);
                            data = (byte) 0;
                        }
                        ItemStack item = new ItemCreator(Material.getMaterial(id), 64, data);
                        player.getInventory().addItem(item);
                        player.sendMessage(ChatColor.GRAY + "Giving 64 of " +
                                item.getType().name().toLowerCase().replaceAll("_", " "));
                        return true;
                    }
                    Material mat = Material.getMaterial(args[0].toUpperCase());
                    if (mat != null) {
                        ItemStack item = new ItemStack(mat, 64);
                        player.getInventory().addItem(item);
                        player.sendMessage(ChatColor.GRAY + "Giving 64 of " +
                                item.getType().name().toLowerCase().replaceAll("_", " "));
                        return true;
                    }
                } catch (Exception e) {
                    player.sendMessage(ChatColor.RED + "/" + label + " [Numeric ID] [Amount]");
                    return true;
                }
                player.sendMessage(ChatColor.RED + "/" + label + " [Numeric ID] [Amount]");
                return true;
            }
            String[] list = args[0].split(":");
            try {
                int id;
                byte data;
                if (list.length > 1) {
                    id = Integer.parseInt(list[0]);
                    data = Byte.parseByte(list[1]);
                } else {
                    id = Integer.parseInt(args[0]);
                    data = (byte) 0;
                }
                ItemStack item = new ItemCreator(Material.getMaterial(id), 64, data);
                player.getInventory().addItem(item);
                player.sendMessage(ChatColor.GRAY + "Giving 64 of " +
                        item.getType().name().toLowerCase().replaceAll("_", " "));
            } catch (Exception e) {
                player.sendMessage(ChatColor.RED + "There was an error, sorry!");
            }
            return true;
        }
        if (args.length == 2) {
            if (!NumberUtil.isInt(args[1])) {
                player.sendMessage(ChatColor.RED + "/" + label + " [Numeric ID] [Amount]");
                return true;
            }
            int amount = Integer.parseInt(args[1]);
            if (!NumberUtil.isInt(args[0])) {
                try {
                    if (args[0].contains(":")) {
                        String[] list = args[0].split(":");
                        int id;
                        byte data;
                        if (list.length > 1) {
                            id = Integer.parseInt(list[0]);
                            data = Byte.parseByte(list[1]);
                        } else {
                            id = Integer.parseInt(args[0]);
                            data = (byte) 0;
                        }
                        ItemStack item = new ItemCreator(Material.getMaterial(id), amount, data);
                        player.getInventory().addItem(item);
                        player.sendMessage(ChatColor.GRAY + "Giving " + amount + " of " +
                                item.getType().name().toLowerCase().replaceAll("_", " "));
                        return true;
                    }
                    Material mat = Material.getMaterial(args[0].toUpperCase());
                    if (mat != null) {
                        ItemStack item = new ItemStack(mat, amount);
                        player.getInventory().addItem(item);
                        player.sendMessage(ChatColor.GRAY + "Giving " + amount + " of " +
                                item.getType().name().toLowerCase().replaceAll("_", " "));
                        return true;
                    }
                } catch (Exception e) {
                    player.sendMessage(ChatColor.RED + "/" + label + " [Numeric ID] [Amount]");
                    return true;
                }
                player.sendMessage(ChatColor.RED + "/" + label + " [Numeric ID] [Amount]");
                return true;
            }
            String[] list = args[0].split(":");
            try {
                int id;
                byte data;
                if (list.length > 1) {
                    id = Integer.parseInt(list[0]);
                    data = Byte.parseByte(list[1]);
                } else {
                    id = Integer.parseInt(args[0]);
                    data = (byte) 0;
                }
                ItemStack item = new ItemCreator(Material.getMaterial(id), amount, data);
                player.getInventory().addItem(item);
                player.sendMessage(ChatColor.GRAY + "Giving " + amount + " of " +
                        item.getType().name().toLowerCase().replaceAll("_", " "));
            } catch (Exception e) {
                player.sendMessage(ChatColor.RED + "There was an error, sorry!");
            }
            return true;
        }
        player.sendMessage(ChatColor.RED + "/" + label + " [Numeric ID] [Amount]");
        return true;
    }
}
