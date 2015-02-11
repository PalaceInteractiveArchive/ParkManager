package us.mcmagic.magicassistant.commands;

import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import us.mcmagic.mcmagiccore.itemcreator.ItemCreator;

import java.util.ArrayList;

import java.util.List;
import java.util.ListIterator;

/**
 * Created by Jacob on 1/20/15.
 */


public class Command_autograph {
    private static ItemStack stack = new ItemCreator(Material.WRITTEN_BOOK, ChatColor.DARK_AQUA + "Autograph Book", new ArrayList<String>());
    private static String msg;

    public static void execute(CommandSender sender, String label, String[] args) {
        if (args.length == 0) {
            helpMenu("main", sender);
            return;
        }
        Player player = (Player) sender;
        ListIterator li = player.getInventory().iterator();

        switch (args[0].toLowerCase()) {
            case "book":
                helpMenu("book", sender);
                return;
            case "session":
                helpMenu("session", sender);
                if (args.length == 2) {
                    switch (args[1].toLowerCase()) {
                    /*    case "start":
                            if (sender.hasPermission("magicassistant.session"))
                                return;
                        case "stop":
                            if (sender.hasPermission("magicassistant.session"))
                                return;
                        case "info":
                            if (sender.hasPermission("magicassistant.session"))
                                return; */
                        case "newbook":

                            BookMeta bookmeta = ((BookMeta) stack.getItemMeta());
                            bookmeta.setAuthor(player.getName());
                            List<String> pages = new ArrayList<>();
                            pages.add("This is the autograph book of " + player.getDisplayName());
                            bookmeta.setPages(pages);
                            stack.setItemMeta(bookmeta);
                            if (!player.getInventory().contains(stack)) {
                                player.getInventory().addItem(stack);
                                player.sendMessage(ChatColor.WHITE + "[Autograph] "
                                        + ChatColor.GREEN + "You now have a new "
                                        + ChatColor.AQUA + " Autograph Book");
                            } else {
                                player.sendMessage(ChatColor.WHITE + "[Autograph] "
                                        + ChatColor.GREEN + "You now have a new "
                                        + ChatColor.AQUA + " Autograph Book");
                            }
                            return;
                        case "sign":
                            if (player.getItemInHand().getType().equals(Material.WRITTEN_BOOK)) {
                                bookmeta = ((BookMeta) player.getItemInHand().getItemMeta());
                                if ((bookmeta.hasTitle()) &&
                                        (bookmeta.getTitle().equalsIgnoreCase("Autograph Book"))) {
                                    player.sendMessage(ChatColor.WHITE + "[Autograph] "
                                            + ChatColor.GREEN + "You have signed the book Sucessfully!");
                                    bookmeta.getPages();
                                    msg = "";
                                }
                                bookmeta.addPage(new String[]{msg + "\n§0" + "-" + player.getDisplayName()});
                                player.getItemInHand().setItemMeta(bookmeta);
                            }
                            return;
                        case "rmpage":
                            if (player.getItemInHand().getType().equals(Material.WRITTEN_BOOK)) {
                                bookmeta = ((BookMeta) player.getItemInHand().getItemMeta());
                                if ((bookmeta.hasTitle()) &&
                                        (bookmeta.getTitle().equalsIgnoreCase("Autograph Book"))) {
                                    if (bookmeta.getAuthor().equals(player.getName())) {
                                        if ((bookmeta.getPageCount() == 1) || (args[0].equalsIgnoreCase("1"))) {
                                            player.sendMessage(ChatColor.WHITE + "[Autograph] "
                                                    + ChatColor.GREEN + "You may not delete the cover page.");
                                            return;
                                        }
                                        player.sendMessage(ChatColor.WHITE + "[Autograph] "
                                                + ChatColor.GREEN + "Modified your book.");
                                        ArrayList newpages = new ArrayList();
                                        for (int i = 1; i < bookmeta.getPageCount(); i++) {
                                            if (new Integer(i) != new Integer(args[0])) {
                                                newpages.add(bookmeta.getPage(i));
                                            }
                                        }
                                        bookmeta.setPages(newpages);
                                        player.getItemInHand().setItemMeta(bookmeta);
                                        return;
                                    }
                                    player.sendMessage(ChatColor.WHITE + "[Autograph] "
                                            + ChatColor.GREEN + "You are not allowed to remove signatures from other's books");
                                    String BookAuthor = bookmeta.getAuthor();
                                    return;
                                }
                            }
                        case "return":
                            if (player.getInventory().contains(Material.WRITTEN_BOOK)) {
                                int books = 0;
                                while (li.hasNext()) {
                                    ItemStack item = (ItemStack) li.next();
                                    BookMeta bm = (BookMeta) item.getItemMeta();
                                    if (!bm.getAuthor().equalsIgnoreCase(player.getName())) {
                                        Player owner = Bukkit.getPlayer(bm.getAuthor());
                                        player.getInventory().remove(item);
                                        owner.getInventory().addItem(new ItemStack[]{item});
                                        books++;
                                    }
                                }

                                player.sendMessage(ChatColor.WHITE + "[Autograph] "
                                        + ChatColor.GREEN + "You have returned all the books you have to their rightful owners.");
                                return;
                            }
                            player.sendMessage(ChatColor.WHITE + "[Autograph] "
                                    + ChatColor.GREEN + "You do not have anyone else's autograph book.");
                            return;
                        case "regain":
                            if (player.getInventory().contains(Material.WRITTEN_BOOK)) {
                                while (li.hasNext()) {
                                    ItemStack item = (ItemStack) li.next();
                                    BookMeta bm = (BookMeta) item.getItemMeta();
                                    if (bm.getAuthor().equalsIgnoreCase(player.getName())) {
                                        player.getInventory().remove(item);
                                        player.getInventory().addItem(new ItemStack[]{item});
                                        player.sendMessage(ChatColor.WHITE + "[Autograph] "
                                                + ChatColor.GREEN + player.getName() + " has taken back their Autograph Book.");
                                        player.sendMessage(ChatColor.WHITE + "[Autograph] "
                                                + ChatColor.GREEN + "Autograph book retrieved from " + player.getName());
                                    }


                                }

                            }

                    }

                }
        }
    }


    public static void helpMenu(String menu, CommandSender sender) {
        switch (menu) {
            case "main":
                sender.sendMessage(ChatColor.GREEN + "Autograph Commands:");
                sender.sendMessage(ChatColor.GREEN + "/autograph book " + ChatColor.AQUA + "- Autograph Book Features");
                sender.sendMessage(ChatColor.GREEN + "/autograph session " + ChatColor.AQUA + "- Autograph Session Features");
                break;
            case "book":
                sender.sendMessage(ChatColor.GREEN + "Autograph Book Commands:");
                sender.sendMessage(ChatColor.GREEN + "/autograph newbook " + ChatColor.AQUA + "- Get a new Autograph Book!");
                sender.sendMessage(ChatColor.GREEN + "/autograph sign " + ChatColor.AQUA + "- Add your signature");
                sender.sendMessage(ChatColor.GREEN + "/autograph rmpage " + ChatColor.AQUA + "- Remove a specific page");
                sender.sendMessage(ChatColor.GREEN + "/autograph return " + ChatColor.AQUA + "- Return all Autograph Books");
                sender.sendMessage(ChatColor.GREEN + "/autograph regain " + ChatColor.AQUA + "- Retrieve your Autograph Book");
                break;
            case "session":
                sender.sendMessage(ChatColor.GREEN + "Autograph Session Commands:");
                sender.sendMessage(ChatColor.GREEN + "/autograph session start " + ChatColor.AQUA + "- Create a new signing session");
                sender.sendMessage(ChatColor.GREEN + "/autograph session stop " + ChatColor.AQUA + "- Stops signing session");
                sender.sendMessage(ChatColor.GREEN + "/autograph session info " + ChatColor.AQUA + "- Infomation about signing sesssion");
                break;
        }


    }

}
