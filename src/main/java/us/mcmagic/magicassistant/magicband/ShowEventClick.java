package us.mcmagic.magicassistant.magicband;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.utils.BandUtil;
import us.mcmagic.magicassistant.utils.InventoryType;
import us.mcmagic.magicassistant.utils.InventoryUtil;

/**
 * Created by Marc on 12/22/14
 */
public class ShowEventClick {

    @SuppressWarnings("deprecation")
    public static void handle(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        if (item.equals(BandUtil.getBackItem())) {
            InventoryUtil.openInventory(player, InventoryType.MAINMENU);
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta.getDisplayName() == null) {
            return;
        }
        if (meta.getDisplayName().contains("Party")) {
            if (item.getData().getData() == (byte) 14) {
                player.closeInventory();
                player.sendMessage(ChatColor.RED + "There is no Party right now, sorry!");
                return;
            } else if (item.getData().getData() == (byte) 5) {
                player.closeInventory();
                player.sendMessage(ChatColor.GREEN + "Joining Party...");
                MagicAssistant.getInstance().bandUtil.joinParty(player);
                return;
            }
        }
        String name = ChatColor.stripColor(meta.getDisplayName());
        switch (name) {
            case "Fantasmic!":
                if (MagicAssistant.serverName.equalsIgnoreCase("HWS")) {
                    player.performCommand("warp fantasmic");
                } else {
                    MagicAssistant.getInstance().sendToServer(player, "HWS");
                }
                return;
            case "IROE":
                if (MagicAssistant.serverName.equalsIgnoreCase("Epcot")) {
                    player.performCommand("warp iroe");
                } else {
                    MagicAssistant.getInstance().sendToServer(player, "Epcot");
                }
                return;
            case "Wishes":
                if (MagicAssistant.serverName.equalsIgnoreCase("MK")) {
                    player.performCommand("warp castle");
                } else {
                    MagicAssistant.getInstance().sendToServer(player, "MK");
                }
                return;
            case "Main Street Electrical Parade":
                if (MagicAssistant.serverName.equalsIgnoreCase("MK")) {
                    player.performCommand("warp mainstreet");
                } else {
                    MagicAssistant.getInstance().sendToServer(player, "MK");
                }
                return;
            case "Festival of Fantasy Parade":
                if (MagicAssistant.serverName.equalsIgnoreCase("MK")) {
                    player.performCommand("warp mainstreet");
                } else {
                    MagicAssistant.getInstance().sendToServer(player, "MK");
                }
        }
    }
}
