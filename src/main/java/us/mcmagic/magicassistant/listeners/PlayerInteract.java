package us.mcmagic.magicassistant.listeners;

import org.bukkit.ChatColor;
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
import us.mcmagic.magicassistant.utils.BandUtil;
import us.mcmagic.magicassistant.utils.InventoryType;
import us.mcmagic.magicassistant.utils.InventoryUtil;

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
        }
    }
}