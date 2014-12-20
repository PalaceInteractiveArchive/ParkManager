package us.mcmagic.magicassistant.magicband;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.PlayerData;
import us.mcmagic.magicassistant.utils.BandUtil;
import us.mcmagic.magicassistant.utils.InventoryType;
import us.mcmagic.magicassistant.utils.InventoryUtil;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Marc on 12/19/14
 */
public class ProfileMenuClick {

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
                List<String> dvcmsgs = Arrays.asList(" ", " ", " ", " ", ChatColor.GREEN + "" + ChatColor.BOLD + "Store Link: " + ChatColor.AQUA + "" + ChatColor.BOLD + "http://store.mcmagic.us");
                for (String msg : dvcmsgs) {
                    player.sendMessage(msg);
                }
                player.closeInventory();
                return;
            case "Website":
                List<String> webmsgs = Arrays.asList(" ", " ", " ", " ", ChatColor.GREEN + "" + ChatColor.BOLD + "Website Link: " + ChatColor.AQUA + "" + ChatColor.BOLD + "http://MCMagic.us");
                for (String msg : webmsgs) {
                    player.sendMessage(msg);
                }
                player.closeInventory();
                return;
            case "Friends List":
                PlayerData data = MagicAssistant.getPlayerData(player.getUniqueId());
                InventoryUtil.openInventory(player, InventoryType.FRIENDLIST);
                return;
            case "Locker":
                player.openInventory(player.getEnderChest());
                return;
            case "Achievements":
                InventoryUtil.featureComingSoon(player);
                return;
            case "Mumble":
                player.closeInventory();
                player.chat("/mumble");
                return;
            case "Resource/Audio Packs":
                player.closeInventory();
                player.chat("/rp");
        }
    }
}
