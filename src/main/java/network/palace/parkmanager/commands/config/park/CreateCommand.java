package network.palace.parkmanager.commands.config.park;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.Park;
import network.palace.parkmanager.handlers.ParkType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;

@CommandMeta(description = "Create a new park")
public class CreateCommand extends CoreCommand {

    public CreateCommand() {
        super("create");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "/parkconfig park create [type] [world] [region]");
            player.sendMessage(ChatColor.AQUA + "Types: " + ParkType.listIDs());
            return;
        }
        ParkType type = ParkType.fromString(args[0].toUpperCase());
        if (type == null) {
            player.sendMessage(ChatColor.RED + args[0] + " isn't a valid park id!");
            return;
        }
        if (ParkManager.getParkUtil().getPark(type) != null) {
            player.sendMessage(ChatColor.RED + "A park already exists on this server with the id " + args[0] + "!");
            return;
        }
        World world = Bukkit.getWorld(args[1]);
        if (world == null) {
            player.sendMessage(ChatColor.RED + args[1] + " isn't a valid world for this server!");
            return;
        }
        ProtectedRegion region;
        try {
            region = WorldGuardPlugin.inst()
                    .getRegionManager(world)
                    .getRegion(args[2]);
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "Invalid region " + args[2] + " in world " + args[1]);
            return;
        }
        ParkManager.getParkUtil().addPark(new Park(type, world, region));
        player.sendMessage(ChatColor.GREEN + "Created a new park " + type.getTitle() + " with the id " + type.name() +
                " in region " + region.getId() + " on world " + world.getName());
    }
}
