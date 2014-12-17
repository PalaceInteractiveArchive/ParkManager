package us.mcmagic.magicassistant.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import us.mcmagic.magicassistant.MagicAssistant;

public class SignChange implements Listener {
    public MagicAssistant pl;

    public SignChange(MagicAssistant instance) {
        pl = instance;
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        Block b = event.getBlock();
        if (b.getType().equals(Material.SIGN)
                || b.getType().equals(Material.SIGN_POST)
                || b.getType().equals(Material.WALL_SIGN)) {
            for (int i = 0; i < 4; i++) {
                event.setLine(i, ChatColor.translateAlternateColorCodes('&', event.getLine(i)));
            }
            if (event.getLine(0).equalsIgnoreCase("[warp]")) {
                event.setLine(0, "[" + ChatColor.BLUE + "Warp"
                        + ChatColor.RESET + "]");
                return;
            }
            if (event.getLine(0).equalsIgnoreCase("[disposal]")) {
                event.setLine(0, "[" + ChatColor.BLUE + "Disposal" + ChatColor.RESET + "]");
            }
        }
    }
}