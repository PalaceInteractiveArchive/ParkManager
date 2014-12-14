package us.mcmagic.magicassistant.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
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
        player.sendMessage(pi.getHeldItemSlot() + "");
        if (pi.getHeldItemSlot() != 8) {
            return;
        }
        ItemStack mb = new ItemStack(BandUtil.getBandMaterial(data.getBandColor()));
        ItemMeta mbm = mb.getItemMeta();
        mbm.setDisplayName(data.getBandName() + "MagicBand");
        mbm.setLore(Arrays.asList(ChatColor.GREEN + "Click me to open",
                ChatColor.GREEN + "the MagicBand menu!"));
        mb.setItemMeta(mbm);
        player.sendMessage(pi.getItemInHand() + " " + ChatColor.RESET + mb);
        if (pi.getItemInHand().equals(mb)) {
            player.sendMessage("Opening Main Menu");
            InventoryUtil.openInventory(player, InventoryType.MAINMENU);
        }
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        Block b = event.getBlock();
        if ((b.getType().equals(Material.SIGN))
                || (b.getType().equals(Material.SIGN_POST))
                || (b.getType().equals(Material.WALL_SIGN))) {
            Sign s = (Sign) b.getState();
            for (int i = 0; i < 4; i++) {
                String line = event.getLine(i);
                event.setLine(i,
                        ChatColor.translateAlternateColorCodes('&', line));
                s.update();
            }
        }
    }
}