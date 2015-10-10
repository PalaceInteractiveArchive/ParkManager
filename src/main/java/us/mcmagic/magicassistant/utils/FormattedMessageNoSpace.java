package us.mcmagic.magicassistant.utils;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import us.mcmagic.mcmagiccore.chat.formattedmessage.FormattedMessage;

public class FormattedMessageNoSpace extends FormattedMessage {

    public FormattedMessageNoSpace(String firstSection) {
        super(firstSection);
    }

    @Override
    public void send(Player player) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a(toJSONString())));
    }
}
