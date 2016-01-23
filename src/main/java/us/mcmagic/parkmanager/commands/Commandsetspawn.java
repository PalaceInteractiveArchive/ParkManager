package us.mcmagic.parkmanager.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.parkmanager.ParkManager;
import us.mcmagic.parkmanager.utils.FileUtil;

public class Commandsetspawn implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only Players can use this command!");
            return true;
        }
        Player player = (Player) sender;
        FileUtil.setSpawn(player.getLocation());
        ParkManager.spawn = player.getLocation();
        Location l = player.getLocation();
        player.getWorld().setSpawnLocation(l.getBlockX(), l.getBlockY(), l.getBlockZ());
        player.sendMessage(ChatColor.GRAY + "Spawn Set!");
        return true;
    }
}