package us.mcmagic.parkmanager.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import us.mcmagic.mcmagiccore.itemcreator.ItemCreator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Marc on 10/19/15
 */
public class ItemUtil {
    private HashMap<Integer, ItemStack> items = new HashMap<>();

    public ItemUtil() {
        initialize();
    }

    @SuppressWarnings("deprecation")
    public void initialize() {
        items.clear();
        try (Connection connection = SqlUtil.getConnection()) {
            PreparedStatement sql = connection.prepareStatement("SELECT * FROM items");
            ResultSet result = sql.executeQuery();
            while (result.next()) {
                List<String> lore = new ArrayList<>();
                String l = result.getString("lore");
                if (!l.equals("")) {
                    for (String s : l.split(";")) {
                        lore.add(ChatColor.translateAlternateColorCodes('&', s));
                    }
                }
                ItemStack item = new ItemCreator(Material.getMaterial(result.getInt("type")), 1,
                        (byte) result.getInt("data"), ChatColor.translateAlternateColorCodes('&',
                        result.getString("name")), lore);
                items.put(result.getInt("id"), item);
            }
            result.close();
            sql.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public HashMap<Integer, ItemStack> getItems() {
        return new HashMap<>(items);
    }

    public ItemStack getItem(int id) {
        return items.get(id);
    }
}