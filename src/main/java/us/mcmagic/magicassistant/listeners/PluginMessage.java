package us.mcmagic.magicassistant.listeners;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.utils.WarpUtil;

import java.util.UUID;

public class PluginMessage implements PluginMessageListener {

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("BungeeCord")) {
            return;
        }
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subchannel = in.readUTF();
        switch (subchannel) {
            case "UpdateWarps":
                WarpUtil.refreshWarps();
                return;
            case "UpdateHotelRooms":
                MagicAssistant.hotelManager.refreshRooms();
                return;
            case "Download":
                MagicAssistant.storageManager.downloadInventory(UUID.fromString(in.readUTF()));
        }
    }
}