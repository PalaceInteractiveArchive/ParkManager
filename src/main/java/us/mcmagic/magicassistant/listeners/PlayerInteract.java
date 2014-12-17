package us.mcmagic.magicassistant.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.PlayerData;
import us.mcmagic.magicassistant.Warp;
import us.mcmagic.magicassistant.utils.BandUtil;
import us.mcmagic.magicassistant.utils.InventoryType;
import us.mcmagic.magicassistant.utils.InventoryUtil;
import us.mcmagic.magicassistant.utils.WarpUtil;

import java.util.Arrays;

public class PlayerInteract implements Listener {
    static MagicAssistant pl;

    public PlayerInteract(MagicAssistant instance) {
        pl = instance;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        PlayerData data = MagicAssistant.getPlayerData(player.getUniqueId());
        if (event.getAction().equals(Action.PHYSICAL)) {
            return;
        }
        PlayerInventory pi = player.getInventory();
        if (pi.getHeldItemSlot() != 8) {
            return;
        }
        ItemStack mb = new ItemStack(BandUtil.getBandMaterial(data.getBandColor()));
        ItemMeta mbm = mb.getItemMeta();
        mbm.setDisplayName(data.getBandName() + "MagicBand");
        mbm.setLore(Arrays.asList(ChatColor.GREEN + "Click me to open",
                ChatColor.GREEN + "the MagicBand menu!"));
        mb.setItemMeta(mbm);
        if (pi.getItemInHand().equals(mb)) {
            InventoryUtil.openInventory(player, InventoryType.MAINMENU);
            return;
        }
        Material type = event.getClickedBlock().getType();
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (type.equals(Material.SIGN) || type.equals(Material.SIGN_POST) || type.equals(Material.WALL_SIGN)) {
                Sign s = (Sign) event.getClickedBlock().getState();
                if (s.getLine(0).equals("[" + ChatColor.BLUE + "Disposal" + ChatColor.RESET + "]")) {
                    player.openInventory(Bukkit.createInventory(player, 54, ChatColor.BLUE + "Disposal"));
                    return;
                }
                if (s.getLine(0).equals("[" + ChatColor.BLUE + "Warp" + ChatColor.RESET + "]")) {
                    Warp warp = WarpUtil.findWarp(ChatColor.stripColor(s.getLine(1)));
                    if (warp == null) {
                        player.sendMessage(ChatColor.RED + "That warp does not exist, sorry!");
                        return;
                    }
                    player.teleport(warp.getLocation());
                    player.sendMessage(ChatColor.BLUE + "You have arrived at "
                            + ChatColor.WHITE + "[" + ChatColor.GREEN + warp.getName()
                            + ChatColor.WHITE + "]");
                    return;
                }
            }
        }
    }
}