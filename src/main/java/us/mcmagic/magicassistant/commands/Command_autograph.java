package us.mcmagic.magicassistant.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
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


public class Command_autograph implements CommandExecutor {
    private static ItemStack stack = new ItemCreator(Material.WRITTEN_BOOK, ChatColor.DARK_AQUA + "Autograph Book", new ArrayList<String>());
    private static String buffer;


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            helpMenu("main", sender);
            return true;
        }
        Player player = (Player) sender;
        ListIterator<ItemStack> li = player.getInventory().iterator();


        switch (args[0]) {
            case "book":
                helpMenu("book", sender);
                return true;
            case "newbook":
                BookMeta bookmeta = ((BookMeta) stack.getItemMeta());
                bookmeta.setAuthor("<uuid>" + player.getUniqueId() + "</uuid>");
                List<String> pages = new ArrayList<>();
                pages.add("This is the autograph book of <uuid>" + player.getUniqueId() + "</uuid>");
                bookmeta.setPages(pages);
                stack.setItemMeta(bookmeta);
                if (!player.getInventory().contains(stack)) {
                    player.getInventory().addItem(stack);
                    player.sendMessage(ChatColor.WHITE + "[Autograph] "
                            + ChatColor.GREEN + "You now have a new"
                            + ChatColor.AQUA + " Autograph Book");
                }
                if (player.getInventory().contains(stack)) {
                    player.sendMessage(ChatColor.WHITE + "[Autograph] "
                            + ChatColor.GREEN + "You now have an"
                            + ChatColor.AQUA + " Autograph Book");
                }

                return true;
            case "sign":

            {
                if (player.getItemInHand().getType().equals(Material.WRITTEN_BOOK)) {
                    if (args.length < 2) {
                        player.sendMessage(ChatColor.GREEN + "You forgot to add message! Please type /signing for help");
                        return true;
                    }
                    bookmeta = ((BookMeta) player.getItemInHand().getItemMeta());
                    if ((bookmeta.hasTitle()) &&
                            (bookmeta.getTitle().equalsIgnoreCase("Autograph Book"))) {
                        player.sendMessage(ChatColor.GREEN + "You have signed the book Sucessfully! Please type /return to return the book!");
                        bookmeta.getPages();
                        buffer = "";
                    }
                    bookmeta.addPage(buffer + "-" + player.getDisplayName());
                    player.getItemInHand().setItemMeta(bookmeta);
                    return true;
                }
            }

               /* if (player.getItemInHand().getType().equals(Material.WRITTEN_BOOK)) {
                    bookmeta = ((BookMeta) player.getItemInHand().getItemMeta());
                    if ((bookmeta.hasTitle()) &&
                            (bookmeta.getTitle().equalsIgnoreCase("Autograph Book"))) {
                        player.sendMessage(ChatColor.WHITE + "[Autograph] "
                                + ChatColor.GREEN + "You have signed the book Sucessfully!");
                        bookmeta.getPages();
                        msg = "";
                    }
                    bookmeta.addPage(new String[]{msg + "\n§0" + "- <uuid>" + player.getUniqueId() + "</uuid>"});
                    player.getItemInHand().setItemMeta(bookmeta);
                }
              */
            case "csign": {
                if (!player.hasPermission("mcmagic.csign")) {
                    player.sendMessage(ChatColor.RED + "Only Characters may use this command!");
                    return true;
                }
                if (args.length < 3) {
                    player.sendMessage(ChatColor.RED + "You forgot to add message! Please type /signing for help");
                    return true;
                }
                if (player.getItemInHand().getType().equals(Material.WRITTEN_BOOK)) {
                    bookmeta = ((BookMeta) player.getItemInHand().getItemMeta());
                    if ((bookmeta.hasTitle()) &&
                            (bookmeta.getTitle().equalsIgnoreCase("Autograph Book"))) {
                        bookmeta.getPages();
                        buffer = "";
                        String character = args[2];
                        args[2] = "";
                        player.sendMessage(ChatColor.AQUA + "You are signing this book as: " + character);


                        buffer = (buffer + " ");
                    }
                    player.getItemInHand().setItemMeta(bookmeta);
                    return true;
                }
                return true;
            }

            case "rmpage":
                if (player.getItemInHand().getType().equals(Material.WRITTEN_BOOK)) {
                    bookmeta = ((BookMeta) player.getItemInHand().getItemMeta());
                    if ((bookmeta.hasTitle()) &&
                            (bookmeta.getTitle().equalsIgnoreCase("Autograph Book"))) {
                        if (bookmeta.getAuthor().equals("<uuid>" + player.getUniqueId() + "</uuid>")) {
                            if ((bookmeta.getPageCount() == 1) || (args[0].equalsIgnoreCase("1"))) {
                                player.sendMessage(ChatColor.WHITE + "[Autograph] "
                                        + ChatColor.GREEN + "You may not delete the cover page.");
                                return true;
                            }
                            player.sendMessage(ChatColor.WHITE + "[Autograph] "
                                    + ChatColor.GREEN + "Modified your book.");
                            ArrayList<String> newpages = new ArrayList<>();
                            for (int i = 1; i < bookmeta.getPageCount(); i++) {
                                if (i != new Integer(args[0])) {
                                    newpages.add(bookmeta.getPage(i));
                                }
                            }
                            bookmeta.setPages(newpages);
                            player.getItemInHand().setItemMeta(bookmeta);
                            return true;
                        }
                        player.sendMessage(ChatColor.WHITE + "[Autograph] "
                                + ChatColor.GREEN + "You are not allowed to remove signatures from other's books");

                        return true;
                    }
                }
            case "return": {
                if (player.getInventory().contains(Material.WRITTEN_BOOK)) {
                    while (li.hasNext()) {
                        ItemStack item = li.next();
                        try {
                            BookMeta bm = (BookMeta) item.getItemMeta();
                            if ((!bm.getAuthor().equalsIgnoreCase("<uuid>" + player.getUniqueId() + "</uuid>")) && (bm.getTitle().equalsIgnoreCase("Autograph Book"))) {
                                Player owner = Bukkit.getPlayer(bm.getAuthor());
                                player.getInventory().remove(item);
                                owner.getInventory().addItem(item);
                            }
                        } catch (ClassCastException | NullPointerException ignored) {
                        }

                    }
                    player.sendMessage(ChatColor.WHITE + "[Autograph] "
                            + ChatColor.GREEN + "You have returned all the books you have to their rightful owners.");
                    return true;
                }
                player.sendMessage(ChatColor.WHITE + "[Autograph] "
                        + ChatColor.GREEN + "You do not have anyone else's autograph book.");
                return true;
            }
               /* if (player.getInventory().contains(Material.WRITTEN_BOOK)) {
                    int books = 0;
                    while (li.hasNext()) {
                        ItemStack item = (ItemStack) li.next();
                        if (item == null || item.getType() != Material.WRITTEN_BOOK) continue;
                        BookMeta bm = (BookMeta) item.getItemMeta();
                        if (!bm.getAuthor().equals("<uuid>" + player.getUniqueId() + "</uuid>")) {
                            Player owner = Bukkit.getPlayer(bm.getAuthor());
                            player.getInventory().remove(item);
                            owner.getInventory().addItem(new ItemStack[]{item});
                            books++;
                        }
                    }

                    player.sendMessage(ChatColor.WHITE + "[Autograph] "
                            + ChatColor.GREEN + "You have returned all the books you have to their rightful owners.");
                    return true;
                }
                player.sendMessage(ChatColor.WHITE + "[Autograph] "
                        + ChatColor.GREEN + "You do not have anyone else's autograph book.");
                return true; */

            case "regain":
                if (player.getInventory().contains(Material.WRITTEN_BOOK)) {
                    while (li.hasNext()) {
                        ItemStack item = li.next();
                        if (item == null || item.getType() != Material.WRITTEN_BOOK) continue;
                        BookMeta bm = (BookMeta) item.getItemMeta();
                        if (bm.getAuthor().equals("<uuid>" + player.getUniqueId() + "</uuid>")) {
                            player.getInventory().remove(item);
                            player.getInventory().addItem(item);
                            player.sendMessage(ChatColor.WHITE + "[Autograph] "
                                    + ChatColor.GREEN + player.getName() + " has taken back their Autograph Book.");
                            player.sendMessage(ChatColor.WHITE + "[Autograph] "
                                    + ChatColor.GREEN + "Autograph book retrieved from " + player.getName());
                        }


                    }

                }

        }
        return true;
    }

    public static void helpMenu(String menu, CommandSender sender) {
        switch (menu) {
            case "main":
                sender.sendMessage(ChatColor.GREEN + "Autograph Commands:");
                sender.sendMessage(ChatColor.GREEN + "/autograph book " + ChatColor.AQUA + "- Autograph Book Features");
                break;
            case "book":
                sender.sendMessage(ChatColor.GREEN + "Autograph Book Commands:");
                sender.sendMessage(ChatColor.GREEN + "/autograph newbook " + ChatColor.AQUA + "- Get a new Autograph Book!");
                sender.sendMessage(ChatColor.GREEN + "/autograph sign " + ChatColor.AQUA + "- Add your signature");
                sender.sendMessage(ChatColor.GREEN + "/autograph rmpage " + ChatColor.AQUA + "- Remove a specific page");
                sender.sendMessage(ChatColor.GREEN + "/autograph return " + ChatColor.AQUA + "- Return all Autograph Books");
                sender.sendMessage(ChatColor.GREEN + "/autograph regain " + ChatColor.AQUA + "- Retrieve your Autograph Book");
                break;
        }


    }
}
