package network.palace.parkmanager.resourcepack;

import lombok.Getter;
import lombok.Setter;
import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.resource.ResourcePack;
import network.palace.core.utils.ItemUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Marc on 3/14/15
 */
public class PackManager {
    @Getter @Setter private String serverPack = "Blank";

    public PackManager() {
        initialize();
    }

    public void initialize() {
        setServerPack(ParkManager.getInstance().getConfig().getString("server-pack"));
    }

    public void handleClick(InventoryClickEvent event) {
        final CPlayer player = Core.getPlayerManager().getPlayer((Player) event.getWhoClicked());
        ItemStack item = event.getCurrentItem();
        if (item == null) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null || meta.getDisplayName() == null) {
            return;
        }
        String name = ChatColor.stripColor(meta.getDisplayName());
        event.setCancelled(true);
        PlayerData data = ParkManager.getInstance().getPlayerData(player.getUniqueId());
        String rp = "";
        if (name.equalsIgnoreCase("no")) {
            setResourcePack(player.getSqlId(), "no");
            data.setPack("no");
            player.sendMessage(ChatColor.GREEN + "You will not be sent a Resource Pack when you join a Park server.");
            player.closeInventory();
            return;
        } else if (name.equalsIgnoreCase("yes")) {
            setResourcePack(player.getSqlId(), "yes");
            data.setPack("yes");
            player.sendMessage(ChatColor.GREEN + "You will be sent the Park Resource Pack when you join a Park server.");
            player.closeInventory();
            rp = serverPack;
        } else if (name.equalsIgnoreCase("Blank")) {
            setResourcePack(player.getSqlId(), "blank");
            data.setPack("blank");
            player.sendMessage(ChatColor.GREEN + "You will be sent a Blank Resource Pack when you join a Park server.");
            player.closeInventory();
            rp = "Blank";
        }
        ResourcePack pack = Core.getResourceManager().getPack(rp);
        if (pack == null) {
            player.sendMessage(ChatColor.RED + "We couldn't find the Resource Pack, try again soon!");
            player.closeInventory();
            return;
        }
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 0);
        player.closeInventory();
        if (!player.getPack().equalsIgnoreCase(pack.getName())) {
            Core.getResourceManager().sendPack(player, pack);
        }
    }

    private void setResourcePack(int id, String pack) {
        Core.runTaskAsynchronously(new Runnable() {
            @Override
            public void run() {
                try (Connection connection = Core.getSqlUtil().getConnection()) {
                    PreparedStatement sql = connection.prepareStatement("UPDATE player_data SET pack=? WHERE id=?");
                    sql.setString(1, pack);
                    sql.setInt(2, id);
                    sql.execute();
                    sql.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void openMenu(CPlayer player) {
        Inventory inv = Bukkit.createInventory(player.getBukkitPlayer(), 27, ChatColor.BLUE + "Resource Pack");
        String selected = ParkManager.getInstance().getPlayerData(player.getUniqueId()).getPack();
        ItemStack yes = ItemUtil.create(Material.WOOL, 1, (byte) 5, ChatColor.GREEN + "Yes",
                Arrays.asList(ChatColor.GRAY + "Download all Park resource", ChatColor.GRAY + "packs when you connect",
                        ChatColor.GRAY + "to a Park server"));
        ItemStack no = ItemUtil.create(Material.WOOL, 1, (byte) 14, ChatColor.RED + "No",
                Arrays.asList(ChatColor.GRAY + "Don't send me any Park", ChatColor.GRAY + "resource packs (you will",
                        ChatColor.GRAY + "keep any pack from", ChatColor.GRAY + "other Palace servers"));
        ItemStack blank = ItemUtil.create(Material.BARRIER, ChatColor.RED + "Blank",
                Arrays.asList(ChatColor.GRAY + "You will be sent a blank", ChatColor.GRAY + "resource pack when you",
                        ChatColor.GRAY + "connect to a Park server"));
        switch (selected.toLowerCase()) {
            case "yes": {
                ItemMeta m = yes.getItemMeta();
                List<String> l = m.getLore();
                l.add(0, ChatColor.LIGHT_PURPLE + "(SELECTED)");
                m.setLore(l);
                yes.setItemMeta(m);
                break;
            }
            case "no": {
                ItemMeta m = no.getItemMeta();
                List<String> l = m.getLore();
                l.add(0, ChatColor.LIGHT_PURPLE + "(SELECTED)");
                m.setLore(l);
                no.setItemMeta(m);
                break;
            }
            case "blank": {
                ItemMeta m = blank.getItemMeta();
                List<String> l = m.getLore();
                l.add(0, ChatColor.LIGHT_PURPLE + "(SELECTED)");
                m.setLore(l);
                blank.setItemMeta(m);
                break;
            }
        }
        inv.setItem(8, blank);
        inv.setItem(12, yes);
        inv.setItem(14, no);
        player.openInventory(inv);
    }
}
