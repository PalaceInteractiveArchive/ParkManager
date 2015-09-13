package us.mcmagic.magicassistant.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import us.mcmagic.magicassistant.MagicAssistant;

/**
 * Created by Marc on 9/6/15
 */
public class PlayerMove implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        MagicAssistant.vanishUtil.move(event);
    }
}