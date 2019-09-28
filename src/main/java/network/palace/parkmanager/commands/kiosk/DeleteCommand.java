package network.palace.parkmanager.commands.kiosk;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;

@CommandMeta(description = "Delete the kiosk closest to you (at most 3 blocks away)")
public class DeleteCommand extends CoreCommand {

    public DeleteCommand() {
        super("delete");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        Location loc = player.getLocation();
        ArmorStand closest = null;
        double distance = -1;
        for (ArmorStand stand : player.getWorld().getEntitiesByClass(ArmorStand.class)) {
            if (!ParkManager.getFastPassKioskManager().isKiosk(stand)) continue;
            Location standLoc = stand.getLocation();
            loc.setY(standLoc.getY());
            double dist = standLoc.distance(loc);
            if (dist < 3 && (dist < distance || distance == -1)) {
                closest = stand;
                distance = dist;
            }
        }
        if (closest == null) {
            player.sendMessage(ChatColor.RED + "Couldn't find a kiosk within 3 blocks from you, move closer to the one you're trying to remove!");
            return;
        }
        closest.remove();
        player.sendMessage(ChatColor.GREEN + "Deleted the closest kiosk to you!");
    }
}
