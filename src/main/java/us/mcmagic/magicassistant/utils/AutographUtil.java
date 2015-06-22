package us.mcmagic.magicassistant.utils;


import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import us.mcmagic.mcmagiccore.itemcreator.ItemCreator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AutographUtil {
    HashMap<UUID, String> nameMap = new HashMap<>();

    public void join(Player player) {
        ItemStack book = getBook(player);
        player.getInventory().setItem(7, book);
    }

    public ItemStack getBook(Player player) {

        try (Connection connection = SqlUtil.getConnection()) {
            PreparedStatement sql = connection.prepareStatement("SELECT * FROM autographs WHERE user=?");
            sql.setString(1, player.getUniqueId().toString());
            ResultSet result = sql.executeQuery();
            HashMap<UUID, String> map = new HashMap<>();
            while (result.next()) {
                map.put(UUID.fromString(result.getString("sender")), result.getString("message"));

            }
            result.close();
            sql.close();

            ItemStack book = new ItemCreator(Material.WRITTEN_BOOK, ChatColor.DARK_AQUA + "My Autograph Book");
            BookMeta bm = (BookMeta) book.getItemMeta();
            for (Map.Entry<UUID, String> entry : map.entrySet()) {
                String name;
                if (nameMap.containsKey(entry.getKey())) {
                    name = nameMap.get(entry.getKey());
                } else {
                    PreparedStatement n = connection.prepareStatement("SELECT * FROM player_data WHERE uuid=?");
                    n.setString(1, entry.getKey().toString());
                    ResultSet r = n.executeQuery();
                    if (!r.next()) {
                        continue;
                    }
                    name = r.getString("username");
                    nameMap.put(entry.getKey(), name);
                    r.close();
                    n.close();
                }
                bm.addPage(ChatColor.translateAlternateColorCodes('&', entry.getValue()) + "\n- " + name);

            }
            bm.setAuthor(player.getName());
            bm.setTitle(ChatColor.DARK_AQUA + player.getName() + "'s Autograph Book");
            book.setItemMeta(bm);
            return book;
        } catch (SQLException e) {

            e.printStackTrace();
        }
        return null;
    }


}