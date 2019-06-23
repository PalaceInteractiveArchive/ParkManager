package network.palace.parkmanager.autograph;

import com.google.common.collect.ImmutableMap;
import network.palace.core.Core;
import network.palace.core.menu.Menu;
import network.palace.core.menu.MenuButton;
import network.palace.core.message.FormattedMessage;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.ItemUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.utils.ReflectionUtils;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.lang.reflect.Method;
import java.util.*;

public class AutographManager {
    public static final String BOOK_TITLE = ChatColor.DARK_AQUA + "Autograph Book";
    public static final int AUTOS_PER_BOOK = 49;
    public static final String FIRST_PAGE = ChatColor.translateAlternateColorCodes('&', "&d&lPalace Network\n&9&lAutograph Book\n\n&aMeet &9Characters &aand Staff Members to get your book signed!\n&eEach book holds up to " + AUTOS_PER_BOOK + " autographs. &9Hold shift and click to switch books.\n&a&nThis book contains:\n");
    //    private HashMap<UUID, UUID> signerToPlayer = new HashMap<>();

    //This contains the sender->target pair when sender is actively signing target's book
    private HashMap<UUID, UUID> activeSessions = new HashMap<>();

    //Contains the taskID of the sender countdown task
    private HashMap<UUID, Integer> signerMap = new HashMap<>();

    //Contains the taskID of the target countdown task
    private HashMap<UUID, Integer> receiverMap = new HashMap<>();

    private static Method getHandle;
    private static Method openBook;

    static {
        try {
            getHandle = ReflectionUtils.getMethod("CraftPlayer", ReflectionUtils.PackageType.CRAFTBUKKIT_ENTITY, "getHandle");
            openBook = ReflectionUtils.getMethod("EntityPlayer", ReflectionUtils.PackageType.MINECRAFT_SERVER,
                    "a", ReflectionUtils.PackageType.MINECRAFT_SERVER.getClass("ItemStack"),
                    ReflectionUtils.PackageType.MINECRAFT_SERVER.getClass("EnumHand"));
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get a list of all signatures a player has in their Autograph Book
     *
     * @param uuid the uuid
     * @return a list of all signatures
     * @implNote This returns all signatures in their "theoretical" book, i.e. all signatures, not a specific book
     */
    public List<Signature> getSignatures(UUID uuid) {
        List<Signature> list = new ArrayList<>();
        for (Object o : Core.getMongoHandler().getAutographs(uuid)) {
            Document doc = (Document) o;
            list.add(new Signature(doc.getString("author"), doc.getString("message"), doc.getLong("time")));
        }
        return list;
    }

    /**
     * Get a list of all signatures split into books
     *
     * @param player the player
     * @return all signatures sorted alphabetically into books
     */
    public List<Book> getBooks(CPlayer player) {
        List<Signature> autographs = (List<Signature>) player.getRegistry().getEntry("autographs");
        if (autographs.isEmpty())
            return Collections.singletonList(new Book(1, player.getUniqueId(), player.getName(), new ArrayList<>()));
        autographs.sort((o1, o2) -> {
            if (o1.getSigner().equals(o2.getSigner())) {
                return (int) (o1.getTime() - o2.getTime());
            }
            return o1.getSigner().toLowerCase().compareTo(o2.getSigner().toLowerCase());
        });
        List<Book> books = new ArrayList<>();
        int start = 0;
        for (int i = 0; i < (int) Math.ceil((double) autographs.size() / AUTOS_PER_BOOK); i++) {
            books.add(new Book(i + 1, player.getUniqueId(), player.getName(),
                    autographs.subList(start, autographs.size() - start > AUTOS_PER_BOOK ? start + AUTOS_PER_BOOK : autographs.size())));
            start += AUTOS_PER_BOOK;
        }
        return books;
    }

    /**
     * Gives the player a stand-in Autograph Book. When this book is opened, it will be replaced with Autograph Book #1.
     *
     * @param player the player
     */
    public void giveBook(CPlayer player) {
        if (ParkManager.getBuildUtil().isInBuildMode(player)) return;
        ItemStack book = ItemUtil.create(Material.WRITTEN_BOOK);
        BookMeta bm = (BookMeta) book.getItemMeta();
        bm.setTitle(BOOK_TITLE);
        bm.setAuthor(player.getName());
        book.setItemMeta(bm);
        player.setInventorySlot(7, book);
    }

    /**
     * Handle a player clicking in slot 7 when in Player Mode
     * This should open an Autograph Book, unless someone's currently signing theirs
     *
     * @param player the player
     */
    public void handleInteract(CPlayer player) {
        if (activeSessions.containsValue(player.getUniqueId())) {
            //Someone's signing their book, so do nothing
            return;
        }
        ItemStack item = player.getItemInMainHand();
        if (item == null || !item.getType().equals(Material.WRITTEN_BOOK)) {
            if (player.getHeldItemSlot() == 7) {
                giveBook(player);
            } else {
                return;
            }
        }
        if (player.isSneaking()) {
            //Player wants to select between their different books, so open the menu
            List<Book> books = getBooks(player);

            List<MenuButton> buttons = new ArrayList<>();
            for (int i = 0; i < books.size(); i++) {
                ItemStack bookItem = books.get(i).getBook();
                buttons.add(new MenuButton(i, bookItem, ImmutableMap.of(ClickType.LEFT, p -> openBook(p, bookItem))));
            }

            int size = books.size() < 10 ? 9 : (books.size() < 19 ? 18 : (books.size() < 28 ? 27 : (books.size() < 37 ? 36 : (books.size() < 46 ? 45 : 54))));
            new Menu(size, ChatColor.BLUE + "Choose an Autograph Book", player, buttons);
        } else if (item.getItemMeta().hasLore()) {
            //Player wants to open the book in their hand
            openBook(player, item);
        } else {
            //Player doesn't have a numbered book, so we need to give it to them
            List<Book> books = getBooks(player);
            openBook(player, getBooks(player).get(0).getBook());
        }
    }

    /**
     * Forces a client to open a book
     *
     * @param player the player
     * @param book   the book
     */
    public void openBook(CPlayer player, ItemStack book) {
        if (!book.getType().equals(Material.WRITTEN_BOOK)) return;
        player.setHeldItemSlot(7);
        if (!player.getItemInMainHand().equals(book)) player.getInventory().setItem(7, book);

        try {
            Object entityPlayer = getHandle.invoke(player.getBukkitPlayer());
            Class<?> enumHand = ReflectionUtils.PackageType.MINECRAFT_SERVER.getClass("EnumHand");
            Object[] enumArray = enumHand.getEnumConstants();
            openBook.invoke(entityPlayer, getItemStack(book), enumArray[0]);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Called when sender requests to sign target's autograph book
     *
     * @param sender the person sending a request
     * @param target the target of that request
     */
    public void requestToSign(CPlayer sender, CPlayer target) {
        if (target.getRegistry().hasEntry("autographRequestFrom") && target.getRegistry().getEntry("autographRequestFrom") != null) {
//        if (signerToPlayer.containsValue(target.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "That player already has an autograph request!");
            return;
        }
//        signerToPlayer.put(sender.getUniqueId(), target.getUniqueId());
        target.getRegistry().addEntry("autographRequestFrom", sender.getUniqueId());
        sender.getRegistry().addEntry("autographRequestTo", target.getUniqueId());

        String coloredName = target.getRank().getTagColor() + target.getName();
        sender.sendMessage(ChatColor.GREEN + "Autograph Request sent to " + coloredName);

        new FormattedMessage(sender.getName()).color(sender.getRank().getTagColor())
                .then(" has sent you an ").color(ChatColor.GREEN)
                .then("Autograph Request. ").color(ChatColor.DARK_AQUA).style(ChatColor.BOLD)
                .then("Click here to Accept").color(ChatColor.YELLOW).command("/autograph accept").tooltip(ChatColor.GREEN + "Click to Accept!")
                .then(" - ").color(ChatColor.GREEN)
                .then("Click here to Deny").color(ChatColor.RED).command("/autograph deny").tooltip(ChatColor.RED + "Click to Deny!")
                .send(target);

        signerMap.put(sender.getUniqueId(), Core.runTaskLater(ParkManager.getInstance(), () -> {
            if (!target.getRegistry().hasEntry("autographRequestFrom") || !target.getRegistry().getEntry("autographRequestFrom").equals(sender.getUniqueId()))
                //If target does not have a pending request or if the pending request is for someone else, do nothing
                return;
            //Otherwise, time out the request

            target.getRegistry().removeEntry("autographRequestFrom");
            sender.getRegistry().removeEntry("autographRequestTo");

            sender.sendMessage(ChatColor.RED + "Your Autograph Request to " + coloredName + ChatColor.RED + " has timed out!");
            target.sendMessage(sender.getRank().getTagColor() + sender.getName() + "'s " + ChatColor.RED + "Autograph Request sent to you has timed out!");
        }, 400L));
        UUID uuid = sender.getUniqueId();
        receiverMap.put(sender.getUniqueId(), Core.runTaskTimer(ParkManager.getInstance(), new Runnable() {
            int i = 20;

            @Override
            public void run() {
                if (target == null || sender == null) {
                    Core.cancelTask(receiverMap.remove(uuid));
                    return;
                }
                if (i <= 0) {
                    target.getActionBar().show(ChatColor.RED + sender.getName() + "'s Autograph Request Expired!");
                    sender.getActionBar().show(ChatColor.RED + "Your Autograph Request to " + target.getName() +
                            " Expired!");
                    cancelTimer(sender.getUniqueId());
                    return;
                }
                target.getActionBar().show(ChatColor.AQUA + sender.getName() + "'s Autograph Request: " +
                        getTimerMessage(i) + " " + ChatColor.AQUA + i + "s");
                sender.getActionBar().show(ChatColor.GREEN + "Your Autograph Request to " + ChatColor.AQUA +
                        target.getName() + ": " + getTimerMessage(i) + " " + ChatColor.AQUA + i + "s");
                i--;
            }
        }, 0, 20L));
    }

    /**
     * Called when a player responds to an autograph request
     *
     * @param target the target player
     * @param accept true if they accept, false if denied
     */
    public void requestResponse(CPlayer target, boolean accept) {
        if (!target.getRegistry().hasEntry("autographRequestFrom")) {
            target.sendMessage(ChatColor.RED + "You don't have any pending Autograph Requests!");
            return;
        }

        CPlayer sender = Core.getPlayerManager().getPlayer((UUID) target.getRegistry().getEntry("autographRequestFrom"));

        if (sender == null) {
            target.sendMessage(ChatColor.RED + "You don't have any pending Autograph Requests!");
            return;
        }

        target.getRegistry().removeEntry("autographRequestFrom");
        sender.getRegistry().removeEntry("autographRequestTo");

        cancelTimer(sender.getUniqueId());

        final String senderName = sender.getRank().getTagColor() + sender.getName();
        final String targetName = target.getRank().getTagColor() + target.getName();

        if (accept) {
            target.getActionBar().show(ChatColor.GREEN + "You accepted " + senderName + "'s " + ChatColor.GREEN + "Autograph Request!");
            target.sendMessage(ChatColor.GREEN + "You accepted " + senderName + "'s " + ChatColor.GREEN + "Autograph Request!");
            sender.getActionBar().show(targetName + ChatColor.GREEN + " accepted your Autograph Request!");
            sender.sendMessage(targetName + ChatColor.GREEN + " accepted your Autograph Request! Sign with /sign " + ChatColor.YELLOW + "[message]");

            activeSessions.put(sender.getUniqueId(), target.getUniqueId());
            target.getInventory().setItem(7, new ItemStack(Material.AIR));
        } else {
            target.getActionBar().show(ChatColor.RED + "You denied " + senderName + "'s " + ChatColor.RED + "Autograph Request!");
            target.sendMessage(ChatColor.RED + "You denied " + senderName + "'s " + ChatColor.RED + "Autograph Request!");
            sender.getActionBar().show(targetName + ChatColor.RED + " denied your Autograph Request!");
            sender.sendMessage(targetName + ChatColor.RED + " denied your Autograph Request!");
        }
    }

    /**
     * Called when a player signs their target's autograph book
     *
     * @param sender  the sender of the request
     * @param message the message they sign
     */
    public void sign(CPlayer sender, String message) {
        CPlayer tp = null;
        for (Map.Entry<UUID, UUID> entry : activeSessions.entrySet()) {
            if (entry.getKey().equals(sender.getUniqueId())) {
                tp = Core.getPlayerManager().getPlayer(entry.getValue());
                break;
            }
        }
        if (tp == null) {
            sender.sendMessage(ChatColor.RED + "You're not signing anyone's book right now!");
            return;
        }
        Core.getMongoHandler().signBook(tp.getUniqueId(), sender.getName(), message);
        updateAutographs(tp);
        giveBook(tp);
        tp.sendMessage(sender.getRank().getTagColor() + sender.getName() + ChatColor.GREEN + " has signed your Autograph Book!");
        tp.giveAchievement(1);
        sender.sendMessage(ChatColor.GREEN + "You signed " + tp.getName() + "'s Autograph Book!");
        activeSessions.remove(sender.getUniqueId());
    }

    /**
     * Called when a player wants to remove a page from their autograph book
     *
     * @param player the player
     * @param num    the page number in their current book
     */
    public void removeAutograph(CPlayer player, Integer num) {
        if (num < 2 || num > 50) {
            //Can't remove first page, and there can't be more than 50 pages
            player.sendMessage(ChatColor.RED + "You can't remove this page!");
            return;
        }
        if (ParkManager.getBuildUtil().isInBuildMode(player)) {
            player.sendMessage(ChatColor.RED + "You can't be in Build Mode while removing autographs!");
            return;
        }
        if (!player.getRegistry().hasEntry("autographs")) {
            player.sendMessage(ChatColor.RED + "There was an error removing the autograph!");
            return;
        }
        if (activeSessions.containsValue(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You can't remove an autograph when someone's signing your book!");
            return;
        }

        ItemStack book = player.getInventory().getItem(7);
        if (book == null || !book.getType().equals(Material.WRITTEN_BOOK) || !((BookMeta) book.getItemMeta()).getTitle().contains("#")) {
            player.sendMessage(ChatColor.RED + "There's no book in your inventory!");
            player.setHeldItemSlot(7);
            handleInteract(player);
            return;
        }
        BookMeta meta = (BookMeta) book.getItemMeta();
        int bookNumber = Integer.parseInt(meta.getTitle().replaceAll(BOOK_TITLE + " #", ""));

        Core.runTaskAsynchronously(ParkManager.getInstance(), () -> {
            int pageNumber = (AUTOS_PER_BOOK * (bookNumber - 1)) + num;
            List<Signature> autographs = (List<Signature>) player.getRegistry().getEntry("autographs");
            if (pageNumber > (autographs.size() + 1)) {
                player.sendMessage(ChatColor.RED + "That page doesn't exist!");
                player.sendMessage(ChatColor.YELLOW + "Make sure you're using a page number from your " + ChatColor.ITALIC + "current book!");
                return;
            }
            autographs.sort((o1, o2) -> {
                if (o1.getSigner().equals(o2.getSigner())) {
                    return (int) (o1.getTime() - o2.getTime());
                }
                return o1.getSigner().toLowerCase().compareTo(o2.getSigner().toLowerCase());
            });
            if (getSignatures(player.getUniqueId()).size() != autographs.size()) {
                updateAutographs(player);
                giveBook(player);
                player.sendMessage(ChatColor.RED + "Your autograph page numbers just changed, make sure you're removing the correct page!");
                return;
            }
            Signature remove = autographs.get(pageNumber - 2);
            Core.getMongoHandler().deleteAutograph(player.getUniqueId(), remove.getSigner(), remove.getTime());
            updateAutographs(player);
            giveBook(player);
            player.sendMessage(ChatColor.GREEN + "You removed an autograph from " + ChatColor.BLUE + remove.getSigner() + ChatColor.GREEN + " from your Autograph Book");
        });
    }

    /**
     * Update the locally cached list of autographs
     *
     * @param player the player
     */
    public void updateAutographs(CPlayer player) {
        if (player != null) player.getRegistry().addEntry("autographs", getSignatures(player.getUniqueId()));
    }

    /**
     * Handle autograph sessions when a player logs out
     *
     * @param player the player
     */
    public void logout(CPlayer player) {
        if (player == null) return;
        if (player.getRegistry().hasEntry("autographRequestFrom")) {
            //Player has a pending autograph request from another player
            CPlayer sender = Core.getPlayerManager().getPlayer((UUID) player.getRegistry().getEntry("autographRequestFrom"));
            if (sender != null) {
                sender.sendMessage(ChatColor.RED + player.getName() + " has disconnected, so your autograph request has timed out!");
                sender.getActionBar().show(ChatColor.RED + "Autograph Request Cancelled");
                cancelTimer(sender.getUniqueId());
            }
        } else if (player.getRegistry().hasEntry("autographRequestTo")) {
            //Player has a pending autograph request to another player
            CPlayer target = Core.getPlayerManager().getPlayer((UUID) player.getRegistry().getEntry("autographRequestTo"));
            if (target != null) {
                target.sendMessage(ChatColor.RED + player.getName() + " has disconnected, so their autograph request has timed out!");
                target.getActionBar().show(ChatColor.RED + "Autograph Request Cancelled");
                cancelTimer(target.getUniqueId());
            }
        } else if (activeSessions.containsKey(player.getUniqueId())) {
            //Player is currently signing another player's book, need to give the book back
            CPlayer target = Core.getPlayerManager().getPlayer(activeSessions.remove(player.getUniqueId()));
            if (target != null) {
                updateAutographs(target);
                giveBook(target);
                target.sendMessage(ChatColor.RED + player.getName() + " has disconnected, so they couldn't sign your Autograph Book!");
            }
        } else if (activeSessions.containsValue(player.getUniqueId())) {
            //Player is currently getting their book signed by another player, need to cancel request session but don't need to give book back
            CPlayer sender = null;
            for (Map.Entry<UUID, UUID> entry : activeSessions.entrySet()) {
                if (entry.getValue().equals(player.getUniqueId())) {
                    sender = Core.getPlayerManager().getPlayer(entry.getKey());
                    if (sender != null) {
                        activeSessions.remove(entry.getKey());
                        break;
                    }
                }
            }
            if (sender != null)
                sender.sendMessage(ChatColor.RED + player.getName() + " has disconnected, so you can't sign their Autograph Book!");
        }
        cancelTimer(player.getUniqueId());
    }

    /**
     * Cancel any pending timers stored under this UUID
     *
     * @param uuid the uuid
     */
    private void cancelTimer(UUID uuid) {
        Core.cancelTask(signerMap.remove(uuid));
        Core.cancelTask(receiverMap.remove(uuid));
    }

    /**
     * Get the NMS instance of a Bukkit ItemStack
     *
     * @param item the Bukkit item
     * @return the NMS item
     */
    private static Object getItemStack(ItemStack item) {
        try {
            Method asNMSCopy = ReflectionUtils.getMethod(ReflectionUtils.PackageType.CRAFTBUKKIT_INVENTORY.getClass("CraftItemStack"),
                    "asNMSCopy", ItemStack.class);
            return asNMSCopy.invoke(ReflectionUtils.PackageType.CRAFTBUKKIT_INVENTORY.getClass("CraftItemStack"), item);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get the appropriate timer message for a certain number of seconds
     *
     * @param time the time, must be a number from 0 to 20
     * @return the appropriately colored timer message
     */
    private String getTimerMessage(int time) {
        int darkGreenBlocks = Math.floorDiv(time, 2);
        int greenBlocks = time % 2 == 0 ? 0 : 1;
        int red = (10 - darkGreenBlocks) - greenBlocks;

        char block = '▉';

        StringBuilder s = new StringBuilder(ChatColor.DARK_GREEN + "");
        for (int i = 0; i < darkGreenBlocks; i++) {
            //dark green blocks
            s.append(block);
        }
        if (greenBlocks == 1) {
            s.append(ChatColor.GREEN).append(block);
        }
        s.append(ChatColor.RED);
        for (int i = 0; i < (10 - darkGreenBlocks) - greenBlocks; i++) {
            s.append(block);
        }
        return s.toString();
    }
}
