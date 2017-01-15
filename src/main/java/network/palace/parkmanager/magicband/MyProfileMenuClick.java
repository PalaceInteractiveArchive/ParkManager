package network.palace.parkmanager.magicband;

import network.palace.core.Core;
import network.palace.core.message.FormattedMessage;
import network.palace.core.player.CPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.InventoryType;
import network.palace.parkmanager.utils.BandUtil;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Marc on 12/19/14
 */
public class MyProfileMenuClick {
    public static List<String> donatemsgs = Arrays.asList(" ", " ", " ", " ", ChatColor.GREEN + "" + ChatColor.BOLD +
            "Store Link: " + ChatColor.AQUA + "" + ChatColor.BOLD + "http://store.palace.network");
    private static FormattedMessage mumble = new FormattedMessage("Click here to download Mumble").color(ChatColor.YELLOW)
            .style(ChatColor.BOLD).link("https://palace.network/mumble").tooltip(ChatColor.GREEN +
                    "Click to visit https://palace.network/mumble");
    private static FormattedMessage website = new FormattedMessage("Click here to visit our Website").color(ChatColor.YELLOW)
            .style(ChatColor.BOLD).link("https://palace.network").tooltip(ChatColor.GREEN +
                    "Click to visit https://palace.network");

    public static void handle(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        if (item.equals(BandUtil.getBackItem())) {
            ParkManager.inventoryUtil.openInventory(player, InventoryType.MAINMENU);
            return;
        }
        if (item.getItemMeta() == null) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta.getDisplayName() == null) {
            return;
        }
        CPlayer cplayer = Core.getPlayerManager().getPlayer(player);
        String name = ChatColor.stripColor(meta.getDisplayName());
        switch (name) {
            case "Make a Donation!":
                donatemsgs.forEach(player::sendMessage);
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
                ParkManager.inventoryUtil.openAchievementPage(player, 1);
                return;
            case "Ride Counter":
                ParkManager.inventoryUtil.openRideCounterPage(player, 1);
                return;
            case "Resource/Audio Packs":
                ParkManager.packManager.openMenu(cplayer);
                return;
            case "Mumble":
                player.closeInventory();
                player.sendMessage(" ");
                mumble.send(player);
                player.sendMessage(" ");
                return;
            case "Player Settings":
                ParkManager.inventoryUtil.openInventory(player, InventoryType.PLAYERSETTINGS);
        }
    }
}