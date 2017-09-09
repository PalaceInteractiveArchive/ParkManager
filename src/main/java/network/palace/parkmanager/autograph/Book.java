package network.palace.parkmanager.autograph;

import lombok.AllArgsConstructor;
import lombok.Getter;
import network.palace.parkmanager.ParkManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * @author Marc
 * @since 9/8/17
 */
@Getter
@AllArgsConstructor
public class Book {
    private int id;
    private UUID uuid;
    private String name;
    private List<Signature> signatures;

    public String getFirstName() {
        return signatures.isEmpty() ? "null" : signatures.get(0).getSigner();
    }

    public String getLastName() {
        return signatures.isEmpty() ? "null" : signatures.get(signatures.size() - 1).getSigner();
    }

    public ItemStack getBook() {
        AutographManager manager = ParkManager.getInstance().getAutographManager();
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK, 1);
        BookMeta meta = (BookMeta) book.getItemMeta();
        meta.setTitle(manager.BOOK_TITLE + " #" + id);
        String extra = "";
        if (signatures.isEmpty()) {
            extra = "\n" + ChatColor.RED + "" + ChatColor.ITALIC + "No Autographs";
        } else if (signatures.size() == 1) {
            extra = ChatColor.BLUE + "" + ChatColor.ITALIC + getFirstName();
        } else {
            extra = ChatColor.BLUE + "" + ChatColor.ITALIC + getFirstName() + ChatColor.GREEN +
                    " to " + ChatColor.BLUE + "" + ChatColor.ITALIC + getLastName();
        }
        meta.addPage(manager.FIRST_PAGE + extra);
        meta.setAuthor(name);
        meta.setLore(Arrays.asList(ChatColor.GREEN + "Contains:", "", extra.replaceAll("\n", "")));
        for (Signature signature : signatures) {
            meta.addPage(ChatColor.translateAlternateColorCodes('&', signature.getMessage()) +
                    ChatColor.YELLOW + "\n- " + ChatColor.BLUE + "" + ChatColor.ITALIC + "" + signature.getSigner());
        }
        book.setItemMeta(meta);
        return book;
    }
}
