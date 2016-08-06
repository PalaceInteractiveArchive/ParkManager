package us.mcmagic.parkmanager.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.mcmagiccore.player.PlayerUtil;
import us.mcmagic.parkmanager.ParkManager;
import us.mcmagic.parkmanager.handlers.PlayerData;

public class Commandinvsee implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
            return true;
        }
        Player player = (Player) sender;
        if (args.length == 2) {
            Player tp = PlayerUtil.findPlayer(args[0]);
            if (tp == null) {
                player.sendMessage(ChatColor.RED + "Player not found!");
                return true;
            }
            String type = args[1];
            PlayerData data = ParkManager.getPlayerData(player.getUniqueId());
            switch (type.toLowerCase()) {
                case "backpack": {
                    player.sendMessage(ChatColor.GREEN + "Now looking in "
                            + tp.getName() + "'s Backpack!");
                    player.openInventory(data.getBackpack().getInventory());
                    return true;
                }
                case "locker": {
                    player.sendMessage(ChatColor.GREEN + "Now looking in "
                            + tp.getName() + "'s Locker!");
                    player.openInventory(data.getLocker().getInventory());
                    return true;
                }
            }
            player.sendMessage(ChatColor.GREEN + "Now looking in "
                    + tp.getName() + "'s Main Inventory!");
            player.openInventory(tp.getInventory());
            return true;
        }
        player.sendMessage(ChatColor.RED + "/invsee [Username] [Main/Backpack/Locker]");
        return true;
    }
}