package us.mcmagic.magicassistant.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
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
            Sign s = (Sign) b.getState();
            for (int i = 0; i < 4; i++) {
                s.setLine(
                        i,
                        ChatColor.translateAlternateColorCodes('&',
                                s.getLine(i)));
            }
            s.update();
            if (s.getLine(0).equalsIgnoreCase("[warp]")) {
                s.setLine(0, "[" + ChatColor.BLUE + "Warp"
                        + ChatColor.RESET + "]");
                s.update();
                return;
            }
            if (s.getLine(0).equalsIgnoreCase("[disposal]")) {
                s.setLine(0, "[" + ChatColor.BLUE + "Disposal" + ChatColor.RESET + "]");
                s.update();
            }
        }
    }
}