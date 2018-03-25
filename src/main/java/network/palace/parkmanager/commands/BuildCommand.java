package network.palace.parkmanager.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CommandPermission;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.PlayerData;
import network.palace.parkmanager.listeners.BlockEdit;
import network.palace.parkmanager.watch.WatchTask;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * Created by Marc on 10/11/15
 */
@CommandMeta(description = "Toggle build mode")
@CommandPermission(rank = Rank.MOD)
public class BuildCommand extends CoreCommand {

    public BuildCommand() {
        super("build");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        ParkManager parkManager = ParkManager.getInstance();
        PlayerData data = parkManager.getPlayerData(player.getUniqueId());
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
            for (ItemStack i : parkManager.getPlayerData(player.getUniqueId()).getBackpack().getInventory().getContents()) {
                if (i == null || i.getType().equals(Material.AIR) || i.getType().equals(Material.WOOD_AXE) ||
                        i.getType().equals(Material.COMPASS)) {
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
            parkManager.getStorageManager().removeHotbar(player.getUniqueId());
            parkManager.getStorageManager().addHotbar(player.getUniqueId(), h);
        } else {
            player.sendMessage(ChatColor.GREEN + "You have exited " + ChatColor.YELLOW + "" + ChatColor.BOLD +
                    "Build Mode!");
            boolean isFlying = player.getBukkitPlayer().isFlying();
            player.setGamemode(GameMode.SURVIVAL);
            player.getBukkitPlayer().setAllowFlight(true);
            player.getBukkitPlayer().setFlying(isFlying);
            final PlayerInventory inv = player.getInventory();
            Inventory pack = parkManager.getPlayerData(player.getUniqueId()).getBackpack().getInventory();
            pack.clear();
            for (ItemStack i : inv.getContents()) {
                if (i == null || i.getType().equals(Material.AIR) || i.getType().equals(Material.WOOD_AXE) ||
                        i.getType().equals(Material.COMPASS)) {
                    continue;
                }
                pack.addItem(i);
            }
            inv.clear();
            parkManager.getPlayerJoinAndLeave().setInventory(player, true);
            if (Core.getPlayerManager().getPlayer(player.getUniqueId()).getRank().getRankId() > Rank.TRAINEE.getRankId()) {
                if (inv.getItem(0) == null || inv.getItem(0).getType().equals(Material.AIR)) {
                    inv.setItem(0, new ItemStack(Material.COMPASS));
                }
            }
            if (!parkManager.getStorageManager().getBuildModeHotbars().containsKey(player.getUniqueId())) {
                return;
            }
            for (ItemStack i : parkManager.getStorageManager().removeHotbar(player.getUniqueId())) {
                if (i == null || i.getType().equals(Material.AIR) || i.getType().equals(Material.WOOD_AXE) ||
                        i.getType().equals(Material.COMPASS)) {
                    continue;
                }
                inv.addItem(i);
            }
        }
        player.closeInventory();

        Core.getMongoHandler().setBuildMode(player.getUniqueId(), BlockEdit.isInBuildMode(player.getUniqueId()));
    }
}
