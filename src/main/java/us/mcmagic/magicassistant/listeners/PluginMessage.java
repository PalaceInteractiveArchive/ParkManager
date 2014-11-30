package us.mcmagic.magicassistant.listeners;

import net.minecraft.util.com.google.common.io.ByteArrayDataInput;
import net.minecraft.util.com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.utils.WarpUtil;

public class PluginMessage implements PluginMessageListener {
    public static MagicAssistant pl;

    public PluginMessage(MagicAssistant instance) {
        pl = instance;
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player,
                                        byte[] message) {
        if (!channel.equals("BungeeCord")) {
            return;
        }
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subchannel = in.readUTF();
        if (subchannel.equals("UpdateWarps")) {
            WarpUtil.refreshWarps();
            return;
        }
    }
}