package us.mcmagic.parkmanager.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.permissions.Rank;
import us.mcmagic.parkmanager.ParkManager;
import us.mcmagic.parkmanager.handlers.PlayerData;
import us.mcmagic.parkmanager.listeners.BlockEdit;
import us.mcmagic.parkmanager.utils.SqlUtil;
import us.mcmagic.parkmanager.watch.WatchTask;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Marc on 10/11/15
 */
public class Commandbuild implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only Players can do this!");
            return true;
        }
        final Player player = (Player) sender;
        PlayerData data = ParkManager.getPlayerData(player.getUniqueId());
        WatchTask.removeFromMessage(player.getUniqueId());
        if (data == null || data.getBackpack() == null || data.getBackpack().getInventory() == null
                || data.getBackpack().getInventory().getContents() == null) {
            return true;
        }
        if (BlockEdit.toggleBuildMode(player.getUniqueId())) {
            player.sendMessage(ChatColor.GREEN + "You are now in " + ChatColor.YELLOW + "" + ChatColor.BOLD +
                    "Build Mode!");
            player.setGameMode(GameMode.CREATIVE);
            PlayerInventory inv = player.getInventory();
            ItemStack[] hotbar = new ItemStack[4];
            ItemStack[] cont = player.getInventory().getContents();
            for (int i = 0; i < 4; i++) {
                hotbar[i] = cont[i];
            }
            inv.clear();
            inv.setItem(0, new ItemStack(Material.COMPASS));
            inv.setItem(1, new ItemStack(Material.WOOD_AXE));
            for (ItemStack i : ParkManager.getPlayerData(player.getUniqueId()).getBackpack().getInventory().getContents()) {
                if (i == null || i.getType().equals(Material.AIR)) {
                    continue;
                }
                inv.addItem(i);
            }
            ItemStack[] h = new ItemStack[4];
            int in = 0;
            for (ItemStack i : hotbar) {
                if (i == null || i.getType().equals(Material.AIR) || i.getType().equals(Material.WOOD_AXE) ||
                        i.getType().equals(Material.COMPASS)) {
                    continue;
                }
                h[in] = i;
                in++;
            }
            ParkManager.storageManager.removeHotbar(player.getUniqueId());
            ParkManager.storageManager.addHotbar(player.getUniqueId(), h);
        } else {
            player.sendMessage(ChatColor.GREEN + "You have exited " + ChatColor.YELLOW + "" + ChatColor.BOLD +
                    "Build Mode!");
            boolean isFlying = player.isFlying();
            player.setGameMode(GameMode.SURVIVAL);
            player.setAllowFlight(true);
            player.setFlying(isFlying);
            final PlayerInventory inv = player.getInventory();
            Inventory pack = ParkManager.getPlayerData(player.getUniqueId()).getBackpack().getInventory();
            pack.clear();
            for (ItemStack i : inv.getContents()) {
                if (i == null || i.getType().equals(Material.AIR) || i.getType().equals(Material.WOOD_AXE) ||
                        i.getType().equals(Material.COMPASS)) {
                    continue;
                }
                pack.addItem(i);
            }
            inv.clear();
            Bukkit.getScheduler().runTaskAsynchronously(ParkManager.getInstance(), () -> {
                ParkManager.autographManager.setBook(player.getUniqueId());
                ParkManager.playerJoinAndLeave.setInventory(player, true);
                if (MCMagicCore.getUser(player.getUniqueId()).getRank().getRankId() > Rank.EARNINGMYEARS.getRankId()) {
                    if (inv.getItem(0) == null || inv.getItem(0).getType().equals(Material.AIR)) {
                        inv.setItem(0, new ItemStack(Material.COMPASS));
                    }
                }
                if (!ParkManager.storageManager.getBuildModeHotbars().containsKey(player.getUniqueId())) {
                    return;
                }
                for (ItemStack i : ParkManager.storageManager.removeHotbar(player.getUniqueId())) {
                    if (i == null || i.getType().equals(Material.AIR) || i.getType().equals(Material.WOOD_AXE) ||
                            i.getType().equals(Material.COMPASS)) {
                        continue;
                    }
                    inv.addItem(i);
                }
            });
        }
        player.closeInventory();
        Bukkit.getScheduler().runTaskAsynchronously(ParkManager.getInstance(), () -> {
            try (Connection connection = SqlUtil.getConnection()) {
                PreparedStatement sql = connection.prepareStatement("UPDATE player_data SET buildmode=? WHERE uuid=?");
                sql.setInt(1, BlockEdit.isInBuildMode(player.getUniqueId()) ? 1 : 0);
                sql.setString(2, player.getUniqueId().toString());
                sql.execute();
                sql.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        return true;
    }
}