package us.mcmagic.magicassistant.listeners;

import org.bukkit.ChatColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.ItemMeta;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.handlers.PlayerData;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.permissions.Rank;
import us.mcmagic.mcmagiccore.player.User;

import java.util.Arrays;

public class PlayerDropItem implements Listener {

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        User user = MCMagicCore.getUser(player.getUniqueId());
        if (user.getRank().getRankId() < Rank.CASTMEMBER.getRankId()) {
            event.setCancelled(true);
            return;
        }
        PlayerData data = MagicAssistant.getPlayerData(player.getUniqueId());
        ItemStack drop = event.getItemDrop().getItemStack();
        ItemStack mb;
        if (data.getSpecial()) {
            mb = new ItemStack(MagicAssistant.bandUtil.getBandMaterial(data.getBandColor()));
            ItemMeta mbm = mb.getItemMeta();
            mbm.setDisplayName(data.getBandName() + "MagicBand");
            mbm.setLore(Arrays.asList(ChatColor.GREEN + "Click me to open",
                    ChatColor.GREEN + "the MagicBand menu!"));
            mb.setItemMeta(mbm);
        } else {
            mb = new ItemStack(Material.FIREWORK_CHARGE);
            FireworkEffectMeta mbm = (FireworkEffectMeta) mb.getItemMeta();
            mbm.setEffect(FireworkEffect.builder().withColor(MagicAssistant.bandUtil.getBandColor(
                    data.getBandColor())).build());
            mbm.setDisplayName(data.getBandName() + "MagicBand");
            mbm.setLore(Arrays.asList(ChatColor.GREEN + "Click me to open",
                    ChatColor.GREEN + "the MagicBand menu!"));
            mb.setItemMeta(mbm);
        }
        if (drop.equals(mb)) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You don't want to lose your " + data.getBandName() + "MagicBand!");
            return;
        }
        if (drop.getType().equals(Material.WRITTEN_BOOK)) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You don't want to lose your " + ChatColor.DARK_AQUA + "Autograph Book!");
        }
    }
}