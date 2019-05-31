package network.palace.parkmanager.commands;

import com.google.common.base.Joiner;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.parkmanager.ParkManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;

@CommandMeta(description = "Get an item", rank = Rank.TRAINEEBUILD, aliases = "i")
public class ItemCommand extends CoreCommand {
    private static final HashMap<String, Material> alternatives = new HashMap<>();

    static {
        alternatives.put("CMD", Material.COMMAND_BLOCK);
        alternatives.put("ENDERCHEST", Material.ENDER_CHEST);
        alternatives.put("ARMORSTAND", Material.ARMOR_STAND);
        alternatives.put("FIREWORK", Material.FIREWORK_ROCKET);
    }

    public ItemCommand() {
        super("item");
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "/i [item] <amount> <NBT Data>");
            player.sendMessage(ChatColor.RED + "Ex: /i cooked_porkchop 1 {display:{Name:\"\\\"Turkey Leg\\\"\"}}");
            return;
        }
        String itemString = args[0];
        Material type = getMaterial(itemString);
        if (type == null) {
            player.sendMessage(ChatColor.RED + "Unknown item '" + itemString + "'!");
            return;
        }
        int amount;
        if (args.length > 1) {
            try {
                amount = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                amount = type.getMaxStackSize();
            }
        } else {
            amount = type.getMaxStackSize();
        }
        if (amount < 1) amount = 1;
        ItemStack item = new ItemStack(type, amount);
        if (args.length > 2 && args[2].startsWith("{")) {
            try {
                ParkManager.getInstance().getServer().getUnsafe().modifyItemStack(item, Joiner.on(' ').join(Arrays.asList(args).subList(2, args.length)));
            } catch (NullPointerException npe) {
                player.sendMessage(ChatColor.RED + "The provided item meta is invalid: '" + args[2] + "'");
                return;
            }
        }
        player.getInventory().addItem(item);
        player.sendMessage(ChatColor.GRAY + "Given " + amount + " " + type.name().toLowerCase());
    }

    private Material getMaterial(String s) {
        if (alternatives.containsKey(s.toUpperCase())) {
            return alternatives.get(s.toUpperCase());
        } else {
            return Material.matchMaterial(s);
        }
    }
}
