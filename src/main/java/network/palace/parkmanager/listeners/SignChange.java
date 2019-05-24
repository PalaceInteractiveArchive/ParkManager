package network.palace.parkmanager.listeners;

import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.ServerSign;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class SignChange implements Listener {

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        Player player = event.getPlayer();
        Block b = event.getBlock();

        for (int i = 0; i < 4; i++) {
            event.setLine(i, ChatColor.translateAlternateColorCodes('&', event.getLine(i)));
        }
        String line1 = event.getLine(0);

        ServerSign sign = null;
        for (ServerSign type : ServerSign.values()) {
            if (type.getSignHeader().equalsIgnoreCase(line1)) {
                sign = type;
                break;
            }
        }

        if (sign != null) {
            event.setLine(0, ChatColor.BLUE + sign.getSignHeader());
            switch (sign) {
                case DISPOSAL:
                    event.setLine(1, "");
                    event.setLine(2, ChatColor.BLACK + "" + ChatColor.BOLD + "Trash");
                    event.setLine(3, "");
                    break;
                case RIDE_LEADERBOARD:
                    if (!ParkManager.getLeaderboardManager().registerLeaderboardSign(event.getLines(), event.getBlock())) {
                        player.sendMessage(ChatColor.RED + "There was a problem creating that leaderboard sign! This usually happens when a leaderboard sign already exists for this ride.");
                        return;
                    }
                    event.setLine(1, "");
                    event.setLine(2, ChatColor.AQUA + "Updating...");
                    event.setLine(3, "");
                    break;
                case SERVER:
                    String name = event.getLine(1);
                    event.setLine(1, "Click to join");
                    event.setLine(2, name);
                    event.setLine(3, "");
                    break;
            }
        }

        /*if (b.getType().equals(Material.SIGN)
                || b.getType().equals(Material.SIGN_POST)
                || b.getType().equals(Material.WALL_SIGN)) {
            for (int i = 0; i < 4; i++) {
                event.setLine(i, ChatColor.translateAlternateColorCodes('&', event.getLine(i)));
            }
            String l1 = event.getLine(0);
            if (l1.equalsIgnoreCase("[shop]")) {
                event.setLine(0, PlayerInteract.shop);
                event.setLine(1, ChatColor.DARK_GREEN + event.getLine(1));
                return;
            }
            if (l1.equalsIgnoreCase("[queue]")) {
                ParkManager.getInstance().getQueueManager().createSign(event);
                return;
            }
            if (l1.equalsIgnoreCase("[mural]")) {
                if (event.getLine(1).equalsIgnoreCase("return")) {
                    event.setLine(0, PlayerInteract.mural);
                    event.setLine(1, "Return your");
                    event.setLine(2, "paintbrush here!");
                } else if (event.getLine(1).equalsIgnoreCase("claim")) {
                    event.setLine(0, PlayerInteract.mural);
                    event.setLine(1, "Claim your");
                    event.setLine(2, "paintbrush here!");
                }
                return;
            }
            if (Core.getInstanceName().contains("Epcot")) {
                if (l1.equalsIgnoreCase("[design station]")) {
                    if (event.getLine(1).equalsIgnoreCase("stats")) {
                        event.setLine(0, PlayerInteract.designStation);
                        event.setLine(1, ChatColor.GOLD + "Stats");
                        event.setLine(2, ChatColor.DARK_GREEN + "Test Track");
                    } else {
                        event.setLine(0, PlayerInteract.designStation);
                        event.setLine(1, ChatColor.GOLD + event.getLine(1));
                        event.setLine(2, ChatColor.DARK_GREEN + "Test Track");
                    }
                }
            }
        }*/
    }
}
