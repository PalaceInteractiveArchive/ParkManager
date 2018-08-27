package network.palace.parkmanager.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import network.palace.core.utils.ItemUtil;
import network.palace.parkmanager.utils.NumberUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Marc on 3/10/15
 */
@CommandMeta(description = "Give player an item", aliases = "i", rank = Rank.TRAINEEBUILD)
public class ItemCommand extends CoreCommand {

    public ItemCommand() {
        super("item");
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this! Try /give instead.");
            return;
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
                        ItemStack item = ItemUtil.create(Material.getMaterial(id), 64, data);
                        player.getInventory().addItem(item);
                        player.sendMessage(ChatColor.GRAY + "Giving 64 of " +
                                item.getType().name().toLowerCase().replaceAll("_", " "));
                        return;
                    }
                    Material mat = Material.getMaterial(args[0].toUpperCase());
                    if (mat != null) {
                        ItemStack item = new ItemStack(mat, 64);
                        player.getInventory().addItem(item);
                        player.sendMessage(ChatColor.GRAY + "Giving 64 of " +
                                item.getType().name().toLowerCase().replaceAll("_", " "));
                        return;
                    }
                } catch (Exception e) {
                    player.sendMessage(ChatColor.RED + "/i [Numeric ID] [Amount]");
                    return;
                }
                player.sendMessage(ChatColor.RED + "/i [Numeric ID] [Amount]");
                return;
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
                ItemStack item = ItemUtil.create(Material.getMaterial(id), 64, data);
                player.getInventory().addItem(item);
                player.sendMessage(ChatColor.GRAY + "Giving 64 of " +
                        item.getType().name().toLowerCase().replaceAll("_", " "));
            } catch (Exception e) {
                player.sendMessage(ChatColor.RED + "There was an error, sorry!");
            }
            return;
        }
        if (args.length == 2) {
            if (!NumberUtil.isInt(args[1])) {
                player.sendMessage(ChatColor.RED + "/i [Numeric ID] [Amount]");
                return;
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
                        ItemStack item = ItemUtil.create(Material.getMaterial(id), amount, data);
                        player.getInventory().addItem(item);
                        player.sendMessage(ChatColor.GRAY + "Giving " + amount + " of " +
                                item.getType().name().toLowerCase().replaceAll("_", " "));
                        return;
                    }
                    Material mat = Material.getMaterial(args[0].toUpperCase());
                    if (mat != null) {
                        ItemStack item = new ItemStack(mat, amount);
                        player.getInventory().addItem(item);
                        player.sendMessage(ChatColor.GRAY + "Giving " + amount + " of " +
                                item.getType().name().toLowerCase().replaceAll("_", " "));
                        return;
                    }
                } catch (Exception e) {
                    player.sendMessage(ChatColor.RED + "/i [Numeric ID] [Amount]");
                    return;
                }
                player.sendMessage(ChatColor.RED + "/i [Numeric ID] [Amount]");
                return;
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
                ItemStack item = ItemUtil.create(Material.getMaterial(id), amount, data);
                player.getInventory().addItem(item);
                player.sendMessage(ChatColor.GRAY + "Giving " + amount + " of " +
                        item.getType().name().toLowerCase().replaceAll("_", " "));
            } catch (Exception e) {
                player.sendMessage(ChatColor.RED + "There was an error, sorry!");
            }
            return;
        }
        player.sendMessage(ChatColor.RED + "/i [Numeric ID] [Amount]");
    }
}
