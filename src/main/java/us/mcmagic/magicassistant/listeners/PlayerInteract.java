package us.mcmagic.magicassistant.listeners;

import java.util.Arrays;

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

import com.legobuilder0813.AK.AK;
import com.legobuilder0813.AK.InventoryType;
import com.legobuilder0813.AK.Utils.InventoryUtil;

public class PlayerInteract implements Listener {
	static AK pl;

	public PlayerInteract(AK instance) {
		pl = instance;
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (event.getAction().equals(Action.PHYSICAL)) {
			return;
		}
		PlayerInventory pi = player.getInventory();
		ItemStack mb = new ItemStack(Material.PAPER);
		ItemMeta mbm = mb.getItemMeta();
		mbm.setDisplayName(ChatColor.GOLD + "MagicBand");
		mbm.setLore(Arrays.asList(ChatColor.GREEN + "Click me to open",
				ChatColor.GREEN + "the MagicBand menu!"));
		mb.setItemMeta(mbm);
		if (pi.getItemInHand().equals(mb)) {
			InventoryUtil.openInventory(player, InventoryType.MAGICBAND);
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