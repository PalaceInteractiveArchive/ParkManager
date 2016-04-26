package us.mcmagic.parkmanager.magicband;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.mcmagic.parkmanager.ParkManager;
import us.mcmagic.parkmanager.handlers.InventoryType;
import us.mcmagic.parkmanager.utils.BandUtil;

/**
 * Created by Marc on 4/26/16
 */
public class PlayerTimeClick {

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
        String name = ChatColor.stripColor(meta.getDisplayName());
        long time = getTime(name);
        if (time == -1) {
            //Reset
            player.sendMessage(ChatColor.GREEN + "You " + ChatColor.AQUA + "Reset " + ChatColor.GREEN + "your Player Time!");
            player.resetPlayerTime();
            player.playSound(player.getLocation(), Sound.NOTE_PLING, 100, 2);
            ParkManager.inventoryUtil.openInventory(player, InventoryType.PLAYERTIME);
            return;
        }
        player.setPlayerTime(time, false);
        player.sendMessage(ChatColor.GREEN + "Your Player Time has been set to " + ChatColor.AQUA + name);
        player.playSound(player.getLocation(), Sound.NOTE_PLING, 100, 2);
        ParkManager.inventoryUtil.openInventory(player, InventoryType.PLAYERTIME);
    }

    private static long getTime(String s) {
        switch (s) {
            case "6AM":
                return 0;
            case "9AM":
                return 3000;
            case "12PM":
                return 6000;
            case "3PM":
                return 9000;
            case "6PM":
                return 12000;
            case "9PM":
                return 15000;
            case "12AM":
                return 18000;
            case "3AM":
                return 21000;
        }
        return -1;
    }
}