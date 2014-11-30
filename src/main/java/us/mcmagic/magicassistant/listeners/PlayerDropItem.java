package us.mcmagic.magicassistant.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.mcmagic.magicassistant.MagicAssistant;

import java.util.Arrays;

public class PlayerDropItem implements Listener {
    public static MagicAssistant pl;

    public PlayerDropItem(MagicAssistant instance) {
        pl = instance;
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        ItemStack drop = event.getItemDrop().getItemStack();
        ItemStack mb = new ItemStack(Material.PAPER);
        ItemMeta mbm = mb.getItemMeta();
        mbm.setDisplayName(ChatColor.GOLD + "MagicBand");
        mbm.setLore(Arrays.asList(ChatColor.GREEN + "Click me to open",
                ChatColor.GREEN + "the MagicBand menu!"));
        mb.setItemMeta(mbm);
        if (drop.equals(mb)) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You don't want to lose your "
                    + ChatColor.GOLD + "MagicBand!");
        }
    }
}