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
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.PlayerData;
import us.mcmagic.magicassistant.utils.BandUtil;

import java.util.Arrays;

public class PlayerDropItem implements Listener {
    public static MagicAssistant pl;

    public PlayerDropItem(MagicAssistant instance) {
        pl = instance;
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        PlayerData data = MagicAssistant.getPlayerData(player.getUniqueId());
        ItemStack drop = event.getItemDrop().getItemStack();
        ItemStack mb = new ItemStack(Material.FIREWORK_CHARGE);
        FireworkEffectMeta mbm = (FireworkEffectMeta) mb.getItemMeta();
        mbm.setEffect(FireworkEffect.builder().withColor(BandUtil.getBandColor(data.getBandColor())).build());
        mbm.setDisplayName(data.getBandName() + "MagicBand");
        mbm.setLore(Arrays.asList(ChatColor.GREEN + "Click me to open",
                ChatColor.GREEN + "the MagicBand menu!"));
        mb.setItemMeta(mbm);
        if (drop.equals(mb)) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You don't want to lose your "
                    + data.getBandName() + "MagicBand!");
        }
    }
}