package us.mcmagic.magicassistant.commands;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import us.mcmagic.magicassistant.handlers.GlowType;
import us.mcmagic.mcmagiccore.itemcreator.ItemCreator;
import us.mcmagic.mcmagiccore.player.PlayerUtil;

import java.util.Arrays;

/**
 * Created by Marc on 3/10/15
 */
public class Commandgwts implements CommandExecutor {
    private ItemStack red;
    private ItemStack orange;
    private ItemStack yellow;
    private ItemStack green;
    private ItemStack aqua;
    private ItemStack blue;
    private ItemStack purple;
    private ItemStack pink;
    private ItemStack white;
    private ItemStack black;
    private ItemStack air;

    public Commandgwts() {
        red = new ItemCreator(Material.LEATHER_HELMET, ChatColor.DARK_RED + "Red Mickey Ears",
                Arrays.asList(ChatColor.LIGHT_PURPLE + "Part of the show, don't move this!"));
        orange = new ItemCreator(Material.LEATHER_HELMET, ChatColor.GOLD + "Orange Mickey Ears",
                Arrays.asList(ChatColor.LIGHT_PURPLE + "Part of the show, don't move this!"));
        yellow = new ItemCreator(Material.LEATHER_HELMET, ChatColor.YELLOW + "Yellow Mickey Ears",
                Arrays.asList(ChatColor.LIGHT_PURPLE + "Part of the show, don't move this!"));
        green = new ItemCreator(Material.LEATHER_HELMET, ChatColor.DARK_GREEN + "Green Mickey Ears",
                Arrays.asList(ChatColor.LIGHT_PURPLE + "Part of the show, don't move this!"));
        aqua = new ItemCreator(Material.LEATHER_HELMET, ChatColor.AQUA + "Aqua Mickey Ears",
                Arrays.asList(ChatColor.LIGHT_PURPLE + "Part of the show, don't move this!"));
        blue = new ItemCreator(Material.LEATHER_HELMET, ChatColor.BLUE + "Blue Mickey Ears",
                Arrays.asList(ChatColor.LIGHT_PURPLE + "Part of the show, don't move this!"));
        purple = new ItemCreator(Material.LEATHER_HELMET, ChatColor.DARK_PURPLE + "Purple Mickey Ears",
                Arrays.asList(ChatColor.LIGHT_PURPLE + "Part of the show, don't move this!"));
        pink = new ItemCreator(Material.LEATHER_HELMET, ChatColor.RED + "Pink Mickey Ears",
                Arrays.asList(ChatColor.LIGHT_PURPLE + "Part of the show, don't move this!"));
        white = new ItemCreator(Material.LEATHER_HELMET, ChatColor.GRAY + "White Mickey Ears",
                Arrays.asList(ChatColor.LIGHT_PURPLE + "Part of the show, don't move this!"));
        black = new ItemCreator(Material.LEATHER_HELMET, ChatColor.DARK_GRAY + "Black Mickey Ears",
                Arrays.asList(ChatColor.LIGHT_PURPLE + "Part of the show, don't move this!"));
        air = new ItemStack(Material.AIR);
        LeatherArmorMeta ro = (LeatherArmorMeta) red.getItemMeta();
        LeatherArmorMeta oo = (LeatherArmorMeta) orange.getItemMeta();
        LeatherArmorMeta yo = (LeatherArmorMeta) yellow.getItemMeta();
        LeatherArmorMeta go = (LeatherArmorMeta) green.getItemMeta();
        LeatherArmorMeta ao = (LeatherArmorMeta) aqua.getItemMeta();
        LeatherArmorMeta bo = (LeatherArmorMeta) blue.getItemMeta();
        LeatherArmorMeta po = (LeatherArmorMeta) purple.getItemMeta();
        LeatherArmorMeta pio = (LeatherArmorMeta) pink.getItemMeta();
        LeatherArmorMeta wo = (LeatherArmorMeta) white.getItemMeta();
        LeatherArmorMeta blo = (LeatherArmorMeta) black.getItemMeta();
        ro.setColor(Color.fromRGB(170, 0, 0));
        oo.setColor(Color.fromRGB(255, 102, 0));
        yo.setColor(Color.fromRGB(255, 222, 0));
        go.setColor(Color.fromRGB(0, 153, 0));
        ao.setColor(Color.fromRGB(0, 255, 255));
        bo.setColor(Color.fromRGB(51, 51, 255));
        po.setColor(Color.fromRGB(39, 31, 155));
        pio.setColor(Color.fromRGB(255, 0, 255));
        wo.setColor(Color.fromRGB(255, 255, 255));
        blo.setColor(Color.fromRGB(0, 0, 0));
        red.setItemMeta(ro);
        orange.setItemMeta(oo);
        yellow.setItemMeta(yo);
        green.setItemMeta(go);
        aqua.setItemMeta(ao);
        blue.setItemMeta(bo);
        purple.setItemMeta(po);
        pink.setItemMeta(pio);
        white.setItemMeta(wo);
        black.setItemMeta(blo);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("help")) {
                sender.sendMessage(ChatColor.RED +
                        "/gwts <red, orange, yellow, green, aqua, blue, purple, pink, white, black, done> <Player>");
                return true;
            }
        }
        if (!(sender instanceof Player)) {
            if (args.length == 2) {
                Player tp = PlayerUtil.findPlayer(args[1]);
                if (tp == null) {
                    sender.sendMessage(ChatColor.RED + "Player not found!");
                    return true;
                }
                GlowType type = GlowType.fromString(args[0]);
                if (type == null) {
                    sender.sendMessage(ChatColor.RED + "GlowType '" + ChatColor.GREEN + args[0] + ChatColor.RED +
                            "' not recognized!");
                    return true;
                }
                handleGlow(tp, type);
            }
            sender.sendMessage(ChatColor.RED + "/gwts [GlowType] [Player]");
            return true;
        }
        Player player = (Player) sender;
        if (args.length == 1) {
            GlowType type = GlowType.fromString(args[0]);
            if (type == null) {
                player.sendMessage(ChatColor.RED + "GlowType '" + ChatColor.GREEN + args[0] + ChatColor.RED +
                        "' not recognized!");
                return true;
            }
            handleGlow(player, type);
            return true;
        }
        if (args.length == 2) {
            Player tp = PlayerUtil.findPlayer(args[1]);
            if (tp == null) {
                sender.sendMessage(ChatColor.RED + "Player not found!");
                return true;
            }
            GlowType type = GlowType.fromString(args[0]);
            if (type == null) {
                sender.sendMessage(ChatColor.RED + "GlowType '" + ChatColor.GREEN + args[0] + ChatColor.RED +
                        "' not recognized!");
                return true;
            }
            handleGlow(tp, type);
            return true;
        }
        player.sendMessage(ChatColor.RED + "/gwts [GlowType] [Player]");
        return true;
    }

    private void handleGlow(Player player, GlowType glowType) {
        PlayerInventory inv = player.getInventory();
        switch (glowType) {
            case RED:
                inv.setHelmet(red);
                return;
            case ORANGE:
                inv.setHelmet(orange);
                return;
            case YELLOW:
                inv.setHelmet(yellow);
                return;
            case GREEN:
                inv.setHelmet(green);
                return;
            case AQUA:
                inv.setHelmet(aqua);
                return;
            case BLUE:
                inv.setHelmet(blue);
                return;
            case PURPLE:
                inv.setHelmet(purple);
                return;
            case PINK:
                inv.setHelmet(pink);
                return;
            case WHITE:
                inv.setHelmet(white);
                return;
            case BLACK:
                inv.setHelmet(black);
                return;
            case DONE:
                inv.setHelmet(air);
        }
    }
}
