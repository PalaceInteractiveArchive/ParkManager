package us.mcmagic.magicassistant.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.itemcreator.ItemCreator;
import us.mcmagic.mcmagiccore.permissions.Rank;
import us.mcmagic.mcmagiccore.player.PlayerUtil;
import us.mcmagic.mcmagiccore.player.User;

import java.util.ArrayList;

/**
 * Created by Jacob on 1/20/15.
 */
public class Commandautograph implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this!");
            return true;
        }
        Player player = (Player) sender;
        if (args.length != 1) {
            helpMenu(player);
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "deny":
                MagicAssistant.autographManager.acceptTrade(player);
                return true;
            case "remove":
                MagicAssistant.autographManager.denyTrade(player);
                return true;
        }
        Player tp = PlayerUtil.findPlayer(args[0]);
        if (tp == null) {
            player.sendMessage(ChatColor.RED + "Player not found!");
            return true;
        }
        if (tp.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You can't sign your own book!");
            return true;

        }
        MagicAssistant.autographManager.addAutograph(player, tp);
        return true;
    }


    private void helpMenu(Player player) {
        player.sendMessage(ChatColor.GREEN + "Autograph Book Commands:");
        player.sendMessage(ChatColor.GREEN + "/autograph <user> " + ChatColor.AQUA + "- Requests signature from Guest");
        player.sendMessage(ChatColor.GREEN + "/autograph accept " + ChatColor.AQUA + "- Accepts Autograph Book from Guest");
        player.sendMessage(ChatColor.GREEN + "/autograph deny " + ChatColor.AQUA + "- Denies Autograph Book from Guest");
        player.sendMessage(ChatColor.GREEN + "/autograph remove <user>" + ChatColor.AQUA + "- Removes users signature from your book");

    }
}