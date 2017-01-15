package network.palace.parkmanager.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CommandPermission;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.listeners.BlockEdit;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import network.palace.parkmanager.handlers.PlayerData;
import network.palace.parkmanager.watch.WatchTask;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Marc on 10/11/15
 */
@CommandMeta(description = "Toggle build mode")
@CommandPermission(rank = Rank.KNIGHT)
public class Commandbuild extends CoreCommand {

    public Commandbuild() {
        super("build");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        PlayerData data = ParkManager.getPlayerData(player.getUniqueId());
        WatchTask.removeFromMessage(player.getUniqueId());
        if (data == null || data.getBackpack() == null || data.getBackpack().getInventory() == null
                || data.getBackpack().getInventory().getContents() == null) {
            return;
        }
        if (BlockEdit.toggleBuildMode(player.getUniqueId())) {
            player.sendMessage(ChatColor.GREEN + "You are now in " + ChatColor.YELLOW + "" + ChatColor.BOLD +
                    "Build Mode!");
            player.setGamemode(GameMode.CREATIVE);
            PlayerInventory inv = player.getInventory();
            ItemStack[] hotbar = new ItemStack[4];
            ItemStack[] cont = player.getInventory().getContents();
            System.arraycopy(cont, 0, hotbar, 0, 4);
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
            boolean isFlying = player.getBukkitPlayer().isFlying();
            player.setGamemode(GameMode.SURVIVAL);
            player.getBukkitPlayer().setAllowFlight(true);
            player.getBukkitPlayer().setFlying(isFlying);
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
                ParkManager.playerJoinAndLeave.setInventory(player.getBukkitPlayer(), true);
                if (Core.getPlayerManager().getPlayer(player.getUniqueId()).getRank().getRankId() > Rank.SQUIRE.getRankId()) {
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
            try (Connection connection = Core.getSqlUtil().getConnection()) {
                PreparedStatement sql = connection.prepareStatement("UPDATE player_data SET buildmode=? WHERE uuid=?");
                sql.setInt(1, BlockEdit.isInBuildMode(player.getUniqueId()) ? 1 : 0);
                sql.setString(2, player.getUniqueId().toString());
                sql.execute();
                sql.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
