package us.mcmagic.magicassistant.commands;

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
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.listeners.BlockEdit;
import us.mcmagic.magicassistant.listeners.PlayerJoinAndLeave;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.permissions.Rank;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Marc on 10/11/15
 */
public class Commandbuild implements CommandExecutor {
    private static HashMap<UUID, ItemStack[]> hotbars = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only Players can do this!");
            return true;
        }
        final Player player = (Player) sender;
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
            for (ItemStack i : MagicAssistant.getPlayerData(player.getUniqueId()).getBackpack().getInventory().getContents()) {
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
            hotbars.remove(player.getUniqueId());
            hotbars.put(player.getUniqueId(), h);
        } else {
            player.sendMessage(ChatColor.GREEN + "You have exited " + ChatColor.YELLOW + "" + ChatColor.BOLD +
                    "Build Mode!");
            boolean isFlying = player.isFlying();
            player.setGameMode(GameMode.SURVIVAL);
            player.setAllowFlight(true);
            player.setFlying(isFlying);
            final PlayerInventory inv = player.getInventory();
            Inventory pack = MagicAssistant.getPlayerData(player.getUniqueId()).getBackpack().getInventory();
            pack.clear();
            for (ItemStack i : inv.getContents()) {
                if (i == null || i.getType().equals(Material.AIR) || i.getType().equals(Material.WOOD_AXE) ||
                        i.getType().equals(Material.COMPASS)) {
                    continue;
                }
                pack.addItem(i);
            }
            inv.clear();
            Bukkit.getScheduler().runTaskAsynchronously(MagicAssistant.getInstance(), new Runnable() {
                @Override
                public void run() {
                    MagicAssistant.autographManager.setBook(player.getUniqueId());
                    PlayerJoinAndLeave.setInventory(player, true);
                    if (MCMagicCore.getUser(player.getUniqueId()).getRank().getRankId() > Rank.INTERN.getRankId()) {
                        if (inv.getItem(0) == null || inv.getItem(0).getType().equals(Material.AIR)) {
                            inv.setItem(0, new ItemStack(Material.COMPASS));
                        }
                    }
                    for (ItemStack i : hotbars.get(player.getUniqueId())) {
                        if (i == null || i.getType().equals(Material.AIR) || i.getType().equals(Material.WOOD_AXE) ||
                                i.getType().equals(Material.COMPASS)) {
                            continue;
                        }
                        inv.addItem(i);
                    }
                    hotbars.remove(player.getUniqueId());
                }
            });
        }
        return true;
    }

    public static void logout(UUID uuid) {
        hotbars.remove(uuid);
    }
}