package network.palace.parkmanager.autograph;

import network.palace.core.Core;
import network.palace.core.inventory.InventoryClick;
import network.palace.core.inventory.impl.Inventory;
import network.palace.core.inventory.impl.InventoryButton;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.parkmanager.ParkManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Innectic
 * @since 5/25/2017
 */
public class AutographInventory {

    private Inventory inventory;
    private CPlayer player;

    /**
     * Create a new autograph inventory for the player
     *
     * @param player the player to create the inventory for
     */
    public AutographInventory(CPlayer player) {
        this.player = player;
        // Get the amount that we need
        int amount = getAmountOfBooks();
        int inventorySize = getNextMultiple(amount);
        if (inventorySize > 54) inventorySize = 54;
        // Create the inventory
        inventory = new Inventory(inventorySize, ChatColor.AQUA + "Choose an Autograph book");
        ItemStack item = new ItemStack(Material.BOOK);
        // Put all the books in the inventory
        for (int i = 0; i < amount; i++) {
            if (i > 54) break;
            int x = i; // @HACK: The lambda hates `i` because it changes. I don't care that it doesn't like it.
            ItemMeta meta = item.getItemMeta();
            // Set the item name to the id of the book
            meta.setDisplayName(ChatColor.AQUA + "" + (i + 1));
            item.setItemMeta(meta);
            // Add the book to the inventory
            InventoryClick click = (clicker, clickAction) -> click(clicker, x);
            inventory.addButton(new InventoryButton(item, click), i);
        }
    }

    /**
     * Open the autographs with the index of 1 - 50 * id.
     *
     * @param player the player who clicked
     * @param id     the offset of the autographs
     */
    private void click(CPlayer player, int id) {
        ItemStack book = createBook(player, id);
        player.setInventorySlot(7, book);
        player.sendMessage(ChatColor.YELLOW + "Selected " + (id + 1) + "!");
        player.closeInventory();
    }

    /**
     * Get the amount of books that needs to be displayed
     *
     * @return the amount
     */
    private int getAmountOfBooks() {
        List signatures = getSignatures(player, Optional.empty());
        double amount = signatures.size();
        if (amount % 50 == 0) return (int) amount / 50;
        else return (int) (amount / 50) + 1;
    }

    /**
     * Get all the signatures that the player has
     *
     * @param player the player to get the signatures for
     * @return the signatures the player has
     */
    private List<Signature> getSignatures(CPlayer player, Optional<Integer> offset) {
        List<Signature> list = new ArrayList<>();
        try (Connection connection = Core.getSqlUtil().getConnection()) {
            PreparedStatement sql = connection.prepareStatement("SELECT * FROM autographs WHERE user=?");
            sql.setString(1, player.getUuid().toString());
            ResultSet result = sql.executeQuery();
            int position = 1;
            while (result.next()) {
                if (offset.isPresent() && position < offset.get()) {
                    position++;
                    continue;
                }
                list.add(new Signature(result.getInt("id"), result.getString("sender"),
                        result.getString("message")));
            }
            result.close();
            sql.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Get the next multiple of 9 from a number
     *
     * @param starting the starting point
     * @return the next multiple
     */
    private int getNextMultiple(int starting) {
        int current = starting;
        if (current % 9 == 0) return current;
        for (int i = 0; i < 9; i++) {
            if (current % 9 == 0) break;
            current += 1;
        }
        return current;
    }

    /**
     * Create a new autograph book
     *
     * @param player the player to get the autographs for
     * @param id     the id of the book clicked
     * @return the fulfilled autograph book
     */
    private ItemStack createBook(CPlayer player, int id) {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();
        meta.addPage(
                "This is your Palace Network Autograph Book! Find Characters and staff, and they'll sign it for you! Each book is limited to 50 autographs, use /autobook to move between books");
        List<Signature> signatures = getSignatures(player, Optional.of(id * 50));
        signatures.forEach(signature -> {
            String displayName = "";
            Optional<UUID> uuid = Optional.empty();
            if (signature.getSigner().length() > 16) uuid = Optional.of(UUID.fromString(signature.getSigner()));
            else displayName = ChatColor.BLUE + signature.getSigner();
            if (uuid.isPresent()) {
                if (ParkManager.userCache.containsKey(uuid.get())) displayName = ParkManager.userCache.get(uuid.get());
                else {
                    try (Connection connection = Core.getSqlUtil().getConnection()) {
                        PreparedStatement statement = connection.prepareStatement("SELECT rank,username FROM player_data WHERE uuid=?");
                        statement.setString(1, uuid.get().toString());
                        ResultSet results = statement.executeQuery();
                        if (!results.next()) {
                            results.close();
                            statement.close();
                            return;
                        }
                        Rank rank = Rank.fromString(results.getString("rank"));
                        displayName = rank.getFormattedName() + results.getString("username");
                        ParkManager.userCache.put(uuid.get(), displayName);
                        results.close();
                        connection.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
            meta.addPage(ChatColor.translateAlternateColorCodes('&', signature.getMessage()) + ChatColor.DARK_GREEN + "\n- " + displayName);
        });
        meta.setTitle(ChatColor.AQUA + "Autograph book #" + id);
        book.setItemMeta(meta);
        return book;
    }

    public void open() {
        inventory.open(player);
    }
}
