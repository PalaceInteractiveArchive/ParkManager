package us.mcmagic.magicassistant.magicband;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.handlers.InventoryType;
import us.mcmagic.magicassistant.handlers.PlayerData;
import us.mcmagic.magicassistant.utils.BandUtil;
import us.mcmagic.magicassistant.utils.SqlUtil;
import us.mcmagic.mcmagiccore.MCMagicCore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

/**
 * Created by Marc on 6/24/15
 */
public class FastPassMenuClick {

    public static void handle(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null) {
            return;
        }
        if (item.getItemMeta() == null) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        if (item.equals(BandUtil.getBackItem())) {
            MagicAssistant.inventoryUtil.openInventory(player, InventoryType.MAINMENU);
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta.getDisplayName() == null) {
            return;
        }
        String name = ChatColor.stripColor(meta.getDisplayName());
        switch (name) {
            case "Yes":
                PlayerData data = MagicAssistant.getPlayerData(player.getUniqueId());
                data.setFastpass(data.getFastpass() + 1);
                player.closeInventory();
                MCMagicCore.economy.addBalance(player.getUniqueId(), -50);
                player.sendMessage(ChatColor.GREEN + "You purchased one FastPass!");
                setFastpassValue(player.getUniqueId(), data.getFastpass());
                return;
            case "No":
                MagicAssistant.inventoryUtil.openInventory(player, InventoryType.MAINMENU);
        }
    }

    private static void setFastpassValue(UUID uuid, int value) {
        try (Connection connection = SqlUtil.getConnection()) {
            PreparedStatement sql = connection.prepareStatement("UPDATE player_data SET fastpass=? WHERE uuid=?");
            sql.setInt(1, value);
            sql.setString(2, uuid.toString());
            sql.execute();
            sql.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}