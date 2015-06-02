package us.mcmagic.magicassistant.magicband;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.handlers.InventoryType;
import us.mcmagic.magicassistant.utils.BandUtil;
import us.mcmagic.mcmagiccore.chat.formattedmessage.FormattedMessage;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Marc on 12/19/14
 */
public class MyProfileMenuClick {
    public static List<String> donatemsgs = Arrays.asList(" ", " ", " ", " ", ChatColor.GREEN + "" + ChatColor.BOLD + "Store Link: " + ChatColor.AQUA + "" + ChatColor.BOLD + "http://store.mcmagic.us");
    private static FormattedMessage mumble = new FormattedMessage("Click here to download Mumble").color(ChatColor.YELLOW)
            .style(ChatColor.BOLD).link("http://mcmagic.us/mumble").tooltip(ChatColor.GREEN +
                    "Click to visit http://mcmagic.us/mumble");
    private static FormattedMessage website = new FormattedMessage("Click here to visit our Website").color(ChatColor.YELLOW)
            .style(ChatColor.BOLD).link("http://mcmagic.us").tooltip(ChatColor.GREEN +
                    "Click to visit http://mcmagic.us");

    public static void handle(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        if (item.equals(BandUtil.getBackItem())) {
            MagicAssistant.inventoryUtil.openInventory(player, InventoryType.MAINMENU);
            return;
        }
        if (item.getItemMeta() == null) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta.getDisplayName() == null) {
            return;
        }
        String name = ChatColor.stripColor(meta.getDisplayName());
        switch (name) {
            case "Make a Donation!":
                for (String msg : donatemsgs) {
                    player.sendMessage(msg);
                }
                player.closeInventory();
                return;
            case "Website":
                player.closeInventory();
                player.sendMessage(" ");
                website.send(player);
                player.sendMessage(" ");
                return;
            case "Locker":
                player.openInventory(player.getEnderChest());
                return;
            case "Achievements":
                MagicAssistant.inventoryUtil.featureComingSoon(player);
                return;
            case "Mumble":
                player.closeInventory();
                player.sendMessage(" ");
                mumble.send(player);
                player.sendMessage(" ");
                return;
            case "Player Settings":
                MagicAssistant.inventoryUtil.openInventory(player, InventoryType.PLAYERSETTINGS);
        }
    }
}