package us.mcmagic.magicassistant.listeners;

import net.minecraft.util.com.google.common.io.ByteArrayDataInput;
import net.minecraft.util.com.google.common.io.ByteStreams;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.legobuilder0813.AK.AK;
import com.legobuilder0813.AK.Utils.WarpUtil;

public class PluginMessage implements PluginMessageListener {
	public static AK pl;

	public PluginMessage(AK instance) {
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