package us.mcmagic.magicassistant.magicband;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.mcmagic.magicassistant.utils.BandUtil;
import us.mcmagic.magicassistant.utils.InventoryType;
import us.mcmagic.magicassistant.utils.InventoryUtil;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Marc on 12/19/14
 */
public class ProfileMenuClick {
    public static List<String> dvcmsgs = Arrays.asList(" ", " ", " ", " ", ChatColor.GREEN + "" + ChatColor.BOLD + "Store Link: " + ChatColor.AQUA + "" + ChatColor.BOLD + "http://store.mcmagic.us");

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
        String name = ChatColor.stripColor(meta.getDisplayName());
        switch (name) {
            case "Become a DVC Member!":
                for (String msg : dvcmsgs) {
                    player.sendMessage(msg);
                }
                player.closeInventory();
                return;
            case "Website":
                player.closeInventory();
                sendPluginMessage(player, "website");
                return;
            case "Friends List":
                InventoryUtil.openFriendListPage(player, 1);
                return;
            case "Locker":
                player.openInventory(player.getEnderChest());
                return;
            case "Achievements":
                InventoryUtil.featureComingSoon(player);
                return;
            case "Mumble":
                player.closeInventory();
                sendPluginMessage(player, "mumble");
                return;
            case "Resource/Audio Packs":
                player.closeInventory();
                sendPluginMessage(player, "packs");
        }
    }

    public static void sendPluginMessage(Player player, String action) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("MagicBand");
            out.writeUTF(player.getUniqueId() + "");
            out.writeUTF(action);
            player.sendPluginMessage(
                    Bukkit.getPluginManager().getPlugin("MagicAssistant"),
                    "BungeeCord", b.toByteArray());
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED
                    + "Sorry! It looks like something went wrong! It's probably out fault. We will try to fix it as soon as possible!");
        }
    }
}