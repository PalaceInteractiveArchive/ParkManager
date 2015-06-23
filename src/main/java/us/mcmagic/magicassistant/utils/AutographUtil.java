package us.mcmagic.magicassistant.utils;


//import net.minecraft.server.v1_8_R3.*;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.mcmagiccore.permissions.Rank;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AutographUtil {
    HashMap<UUID, String> nameMap = new HashMap<>();

    public void join(final Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(MagicAssistant.getInstance(), new Runnable() {
            @Override
            public void run() {
                setBook(player);
            }
        });
    }

    public void setBook(Player player) {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK, 1);
        BookMeta bm = (BookMeta) book.getItemMeta();
        bm.addPage("This is your Autograph Book! Find Characters and they will sign it for you!");
        HashMap<UUID, String> map = new HashMap<>();
        try (Connection connection = SqlUtil.getConnection()) {
            PreparedStatement sql = connection.prepareStatement("SELECT * FROM autographs WHERE user=?");
            sql.setString(1, player.getUniqueId().toString());
            ResultSet result = sql.executeQuery();
            while (result.next()) {
                map.put(UUID.fromString(result.getString("sender")), result.getString("message"));
            }
            result.close();
            sql.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (!map.isEmpty()) {
            for (Map.Entry<UUID, String> entry : map.entrySet()) {
                String name = "unknown";
                if (nameMap.containsKey(entry.getKey())) {
                    name = nameMap.get(entry.getKey());
                } else {
                    try (Connection connection = SqlUtil.getConnection()) {
                        PreparedStatement n = connection.prepareStatement("SELECT * FROM player_data WHERE uuid=?");
                        n.setString(1, entry.getKey().toString());
                        ResultSet r = n.executeQuery();
                        if (!r.next()) {
                            continue;
                        }
                        Rank rank = Rank.fromString(r.getString("rank"));
                        name = rank.getChatColor() + r.getString("username");
                        nameMap.put(entry.getKey(), name);
                        r.close();
                        n.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                bm.addPage(ChatColor.translateAlternateColorCodes('&', entry.getValue()) + ChatColor.DARK_GREEN + "\n- "
                        + name);
            }
        }
        bm.setAuthor(player.getName());
        bm.setTitle(ChatColor.DARK_AQUA + "Autograph Book");
        book.setItemMeta(bm);
        player.getInventory().setItem(7, book);
    }
}