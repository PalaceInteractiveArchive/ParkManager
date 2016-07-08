package us.mcmagic.parkmanager.commands;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.chat.formattedmessage.FormattedMessage;
import us.mcmagic.mcmagiccore.permissions.Rank;
import us.mcmagic.mcmagiccore.player.User;
import us.mcmagic.parkmanager.ParkManager;
import us.mcmagic.parkmanager.handlers.Warp;

import java.util.HashMap;

public class Commandnearby implements CommandExecutor {
    public static final int DEFAULT_SEARCH_DISTANCE = 100;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;
        Location center = player.getLocation().clone();
        HashMap<Warp, Integer> nearby = new HashMap<>();
        for (Warp warp : ParkManager.warps) {
            if (!warp.getServer().equals(MCMagicCore.getMCMagicConfig().serverName)) {
                continue;
            }
            if (warp.getLocation() == null) {
                continue;
            }
            User user = MCMagicCore.getUser(player.getUniqueId());
            if (warp.getName().startsWith("dvc") && user.getRank().getRankId() < Rank.DVCMEMBER.getRankId()) {
                continue;
            }
            if (warp.getName().startsWith("char") && user.getRank().getRankId() < Rank.CHARACTERGUEST.getRankId()) {
                continue;
            }
            if (warp.getName().startsWith("staff") && user.getRank().getRankId() < Rank.EARNINGMYEARS.getRankId()) {
                continue;
            }
            int distance = (int) warp.getLocation().distance(center);
            if (distance <= DEFAULT_SEARCH_DISTANCE) {
                nearby.put(warp, distance);
            }
        }
        if (!nearby.isEmpty()) {
            FormattedMessageNoSpace message = new FormattedMessageNoSpace("Nearby locations: (Click to warp there)\n");
            message.color(ChatColor.GREEN);
            for (int i = 0; i < nearby.keySet().size(); i++) {
                Warp warp = (Warp) nearby.keySet().toArray()[i];
                message.then(" - ");
                message.color(ChatColor.GREEN);
                message.then(warp.getName()).color(ChatColor.AQUA).command("/warp " + warp.getName()).tooltip(ChatColor.GREEN + "Click to warp").color(ChatColor.GREEN).then(" (" + nearby.get(warp) + " blocks)").color(ChatColor.AQUA);
                if (i < nearby.keySet().size() - 1) message.then("\n");
            }
            message.send(player);
        } else {
            player.sendMessage(ChatColor.RED + "Could not find any warps within " + DEFAULT_SEARCH_DISTANCE + " blocks.");
        }
        return true;
    }

    private class FormattedMessageNoSpace extends FormattedMessage {

        public FormattedMessageNoSpace(String firstSection) {
            super(firstSection);
        }

        @Override
        public void send(Player player) {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a(toJSONString())));
        }
    }
}
