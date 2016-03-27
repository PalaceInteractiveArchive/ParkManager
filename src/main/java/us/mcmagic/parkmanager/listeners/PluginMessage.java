package us.mcmagic.parkmanager.listeners;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import us.mcmagic.mcmagiccore.scoreboard.ScoreboardUtility;
import us.mcmagic.parkmanager.ParkManager;
import us.mcmagic.parkmanager.utils.WarpUtil;

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
                ParkManager.hotelManager.refreshRooms();
                return;
            case "Download":
                ParkManager.storageManager.downloadInventory(UUID.fromString(in.readUTF()));
                return;
            case "MagicVote":
                UUID uuid = UUID.fromString(in.readUTF());
                Player tp = Bukkit.getPlayer(uuid);
                if (tp != null) {
                    ParkManager.fpKioskManager.updateKioskData(uuid);
                    ScoreboardUtility.update(uuid);
                }
        }
    }
}