package us.mcmagic.magicassistant.listeners;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.utils.HotelUtil;
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
        if (subchannel.equals("UpdateHotelRooms")) {
            HotelUtil.refreshRooms();
            return;
        }
        if (subchannel.equals("MagicParty")) {
            MagicAssistant.party = in.readBoolean();
            MagicAssistant.partyServer.add(in.readUTF());
            if (MagicAssistant.party) {
                Bukkit.broadcast(ChatColor.GREEN + "Party Enabled, Server: " + MagicAssistant.partyServer, "arcade.bypass");
            } else {
                Bukkit.broadcast(ChatColor.RED + "No Party", "arcade.bypass");
            }
        }
        if (subchannel.equals("AddParty")) {
            MagicAssistant.party = true;
            MagicAssistant.partyServer.add(in.readUTF());
            return;
        }
        if (subchannel.equals("RemoveParty")) {
            MagicAssistant.party = false;
            MagicAssistant.partyServer.clear();
        }
    }
}