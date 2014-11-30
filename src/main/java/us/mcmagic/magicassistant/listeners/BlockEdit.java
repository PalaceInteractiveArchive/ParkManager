package us.mcmagic.magicassistant.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import us.mcmagic.magicassistant.MagicAssistant;

public class BlockEdit implements Listener {
    public static MagicAssistant pl;

    public BlockEdit(MagicAssistant instance) {
        pl = instance;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission("block.break")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission("block.place")) {
            event.setCancelled(true);
        }
    }
}