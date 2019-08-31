package network.palace.parkmanager.commands.outfits;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.ItemUtil;
import network.palace.parkmanager.ParkManager;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

@CommandMeta(description = "Create a new outfit")
public class CreateCommand extends CoreCommand {

    public CreateCommand() {
        super("create");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "/outfit create [name]");
            player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "Also, put on the outfit items before running this command!");
            return;
        }

        StringBuilder name = new StringBuilder();
        for (String arg : args) {
            name.append(arg).append(" ");
        }
        String displayName = ChatColor.AQUA + ChatColor.translateAlternateColorCodes('&', name.toString().trim());

        PlayerInventory inv = player.getInventory();

        ItemStack head = inv.getHelmet().clone();
        ItemStack shirt = inv.getChestplate().clone();
        ItemStack pants = inv.getLeggings().clone();
        ItemStack boots = inv.getBoots().clone();

        ItemMeta meta = head.getItemMeta();
        meta.setDisplayName(displayName + " Head");
        head.setItemMeta(meta);

        meta = shirt.getItemMeta();
        meta.setDisplayName(displayName + " Shirt");
        shirt.setItemMeta(meta);

        meta = pants.getItemMeta();
        meta.setDisplayName(displayName + " Pants");
        pants.setItemMeta(meta);

        meta = boots.getItemMeta();
        meta.setDisplayName(displayName + " Boots");
        boots.setItemMeta(meta);

        Core.runTaskAsynchronously(ParkManager.getInstance(), () -> {
            Core.getMongoHandler().createOutfitNew(name.toString().trim(),
                    ItemUtil.getJsonFromItemNew(head).toString(),
                    ItemUtil.getJsonFromItemNew(shirt).toString(),
                    ItemUtil.getJsonFromItemNew(pants).toString(),
                    ItemUtil.getJsonFromItemNew(boots).toString(),
                    ParkManager.getResort().getId());
            player.sendMessage(ChatColor.GREEN + "Created new outfit in the database! Reload with '/outfit reload' to view it on this server.");
        });
    }
}
