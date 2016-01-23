package us.mcmagic.parkmanager.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        event.getPlayer().sendMessage(ChatColor.RED + "There is currently a problem with chat. (Error Code 109)");
        event.setCancelled(true);
    }
}