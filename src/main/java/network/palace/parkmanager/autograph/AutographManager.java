package network.palace.parkmanager.autograph;

import network.palace.core.Core;
import network.palace.core.message.FormattedMessage;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.PlayerData;
import network.palace.parkmanager.listeners.BlockEdit;
import network.palace.parkmanager.utils.ReflectionUtils;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BookMeta;

import java.lang.reflect.Method;
import java.util.*;

public class AutographManager {
    public static final String BOOK_TITLE = ChatColor.DARK_AQUA + "Autograph Book";
    public static final String FIRST_PAGE = ChatColor.translateAlternateColorCodes('&', "&d&lPalace Network\n&9&lAutograph Book\n\n&aMeet &9Characters &aand Staff Members to get your book signed!\n&eEach book holds up to 49 autographs. &9Hold shift and click to switch books.\n&a&nThis book contains:\n");
    private HashMap<UUID, UUID> signerToPlayer = new HashMap<>();
    private HashMap<UUID, Integer> signerMap = new HashMap<>();
    private HashMap<UUID, Integer> receiverMap = new HashMap<>();
    private HashMap<UUID, UUID> activeSessions = new HashMap<>();
    private HashMap<UUID, ItemStack> books = new HashMap<>();

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

    public List<Signature> getSignatures(UUID uuid) {
        List<Signature> list = new ArrayList<>();
        for (Document doc : Core.getMongoHandler().getAutographs(uuid)) {
            list.add(new Signature(doc.getString("sender"), doc.getString("message"), doc.getLong("time")));
        }
        return list;
    }

    public List<Book> getBooks(CPlayer player) {
        PlayerData data = ParkManager.getInstance().getPlayerData(player.getUniqueId());
        List<Signature> autographs = data.getAutographs();
        autographs.sort((o1, o2) -> {
            if (o1.getSigner().equals(o2.getSigner())) {
                return (int) (o1.getTime() - o2.getTime());
            }
            return o1.getSigner().toLowerCase().compareTo(o2.getSigner().toLowerCase());
        });
        List<Book> books = new ArrayList<>();
        int start = 0;
        for (int i = 0; i < (int) Math.ceil((double) autographs.size() / 49); i++) {
            books.add(new Book(i + 1, player.getUniqueId(), player.getName(),
                    autographs.subList(start, autographs.size() - start > 49 ? start + 49 : autographs.size())));
            start += 49;
        }
        return books;
    }

    public void giveBook(CPlayer player) {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bm = (BookMeta) book.getItemMeta();
        bm.setTitle(BOOK_TITLE);
        bm.setAuthor(player.getName());
        book.setItemMeta(bm);
        if (BlockEdit.isInBuildMode(player.getUniqueId())) {
            return;
        }
        player.getInventory().setItem(7, book);
    }

    public void logout(CPlayer player) {
        if (player == null) {
            return;
        }
        if (signerToPlayer.containsKey(player.getUniqueId())) {
            UUID tuuid = signerToPlayer.remove(player.getUniqueId());
            cancelTimer(player.getUniqueId());
            cancelTimer(tuuid);
            signerToPlayer.remove(player.getUniqueId());
            return;
        }
        if (signerToPlayer.containsValue(player.getUniqueId())) {
            for (Map.Entry<UUID, UUID> entry : signerToPlayer.entrySet()) {
                if (entry.getValue().equals(player.getUniqueId())) {
                    cancelTimer(player.getUniqueId());
                    cancelTimer(entry.getKey());
                    signerToPlayer.remove(entry.getKey());
                    return;
                }
            }
        }
    }

    public void openMenu(CPlayer player, List<Signature> autographs, boolean sneaking) {
        autographs = new ArrayList<>(autographs);
        autographs.sort((o1, o2) -> {
            if (o1.getSigner().equals(o2.getSigner())) {
                return (int) (o1.getTime() - o2.getTime());
            }
            return o1.getSigner().toLowerCase().compareTo(o2.getSigner().toLowerCase());
        });
        if (autographs.size() < 50) {
            Book book = new Book(1, player.getUniqueId(), player.getName(), autographs);
            openBook(player, book.getBook());
            return;
        }
        ItemStack currentSlot = player.getInventory().getItem(7);
        if (!sneaking && currentSlot.getType().equals(Material.WRITTEN_BOOK) && (currentSlot.getItemMeta().getLore() != null && !currentSlot.getItemMeta().getLore().isEmpty())) {
            openBook(player, currentSlot);
            return;
        }
        List<Book> books = getBooks(player);
        int size = books.size() < 10 ? 9 : (books.size() < 19 ? 18 : (books.size() < 28 ? 27 : (books.size() < 37 ? 36 : (books.size() < 46 ? 45 : 54))));
        Inventory inv = Bukkit.createInventory(player.getBukkitPlayer(), size, ChatColor.BLUE + "Choose an Autograph Book");
        for (int i = 0; i < books.size(); i++) {
            inv.setItem(i, books.get(i).getBook());
        }
        player.openInventory(inv);
    }

    public void openBook(CPlayer player, ItemStack book) {
        if (!book.getType().equals(Material.WRITTEN_BOOK)) {
            return;
        }
        player.setHeldItemSlot(7);
        PlayerInventory inv = player.getInventory();
        if (!inv.getItemInMainHand().equals(book)) {
            inv.setItemInMainHand(book);
        }
        try {
            Object entityPlayer = getHandle.invoke(player.getBukkitPlayer());
            Class<?> enumHand = ReflectionUtils.PackageType.MINECRAFT_SERVER.getClass("EnumHand");
            Object[] enumArray = enumHand.getEnumConstants();
            openBook.invoke(entityPlayer, getItemStack(book), enumArray[0]);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    public static Object getItemStack(ItemStack item) {
        try {
            Method asNMSCopy = ReflectionUtils.getMethod(ReflectionUtils.PackageType.CRAFTBUKKIT_INVENTORY.getClass("CraftItemStack"),
                    "asNMSCopy", ItemStack.class);
            return asNMSCopy.invoke(ReflectionUtils.PackageType.CRAFTBUKKIT_INVENTORY.getClass("CraftItemStack"), item);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void sign(CPlayer player, String message) {
        CPlayer tp = null;
        for (Map.Entry<UUID, UUID> entry : activeSessions.entrySet()) {
            if (entry.getKey().equals(player.getUniqueId())) {
                tp = Core.getPlayerManager().getPlayer(entry.getValue());
                break;
            }
        }
        if (tp == null) {
            player.sendMessage(ChatColor.RED + "You're not signing anyone's book right now!");
            return;
        }
        String name = player.getName();
        Core.getMongoHandler().signBook(tp.getUniqueId(), name, message);
        ParkManager.getInstance().getPlayerData(tp.getUniqueId()).updateAutographs();
        giveBook(tp);
        tp.sendMessage(player.getRank().getTagColor() + player.getName() + ChatColor.GREEN +
                " has signed your Autograph Book!");
        tp.giveAchievement(1);
        player.sendMessage(ChatColor.GREEN + "You signed " + tp.getName() + "'s Autograph Book!");
        activeSessions.remove(player.getUniqueId());
    }

    public void requestToSign(CPlayer sender, CPlayer target) {
        if (signerToPlayer.containsValue(target.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "That player already has an autograph request!");
            return;
        }
        signerToPlayer.put(sender.getUniqueId(), target.getUniqueId());
        final String name = Core.getPlayerManager().getPlayer(sender.getUniqueId()).getRank().getTagColor() + sender.getName();
        final String name2 = Core.getPlayerManager().getPlayer(target.getUniqueId()).getRank().getTagColor() + target.getName();
        sender.sendMessage(ChatColor.GREEN + "Autograph Request sent to " + name2);
        FormattedMessage msg = new FormattedMessage(sender.getName()).color(Core.getPlayerManager().getPlayer(sender.getUniqueId())
                .getRank().getTagColor()).then(" has sent you an ").color(ChatColor.GREEN).then("Autograph Request. ")
                .color(ChatColor.DARK_AQUA).style(ChatColor.BOLD).then("Click here to Accept").color(ChatColor.YELLOW)
                .command("/autograph accept").tooltip(ChatColor.GREEN + "Click to Accept!").then(" - ")
                .color(ChatColor.GREEN).then("Click here to Deny").color(ChatColor.RED).command("/autograph deny")
                .tooltip(ChatColor.RED + "Click to Deny!");
        msg.send(target);
        signerMap.put(sender.getUniqueId(), Bukkit.getScheduler().runTaskLater(ParkManager.getInstance(), () -> {
            if (!signerToPlayer.containsKey(sender.getUniqueId())) {
                return;
            }
            signerToPlayer.remove(sender.getUniqueId());
            sender.sendMessage(ChatColor.RED + "Your Autograph Request to " + name2 + ChatColor.RED +
                    " has timed out!");
            target.sendMessage(name + "'s " + ChatColor.RED + "Autograph Request sent to you has timed out!");
        }, 400L).getTaskId());
        UUID uuid = sender.getUniqueId();
        receiverMap.put(sender.getUniqueId(), Bukkit.getScheduler().runTaskTimer(ParkManager.getInstance(), new Runnable() {
            int i = 20;

            @Override
            public void run() {
                if (target == null || sender == null) {
                    Bukkit.getScheduler().cancelTask(receiverMap.remove(uuid));
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
        }, 0, 20L).getTaskId());
    }

    public void acceptRequest(CPlayer player) {
        if (!signerToPlayer.containsValue(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You don't have any pending Autograph Requests!");
            return;
        }
        CPlayer tp = null;
        for (Map.Entry<UUID, UUID> entry : signerToPlayer.entrySet()) {
            if (entry.getValue().equals(player.getUniqueId())) {
                tp = Core.getPlayerManager().getPlayer(Bukkit.getPlayer(entry.getKey()));
                break;
            }
        }
        if (tp == null) {
            player.sendMessage(ChatColor.RED + "You don't have any pending Autograph Requests!");
            return;
        }
        signerToPlayer.remove(tp.getUniqueId());
        cancelTimer(tp.getUniqueId());
        final String name = tp.getRank().getTagColor() + tp.getName();
        final String name2 = player.getRank().getTagColor() + player.getName();
        player.getActionBar().show(ChatColor.GREEN + "You accepted " + name + "'s " + ChatColor.GREEN +
                "Autograph Request!");
        tp.getActionBar().show(name2 + ChatColor.GREEN + " accepted your Autograph Request!");
        player.sendMessage(ChatColor.GREEN + "You accepted " + name + "'s " + ChatColor.GREEN + "Autograph Request!");
        tp.sendMessage(name2 + ChatColor.GREEN + " accepted your Autograph Request!");
        activeSessions.put(tp.getUniqueId(), player.getUniqueId());
        player.getInventory().setItem(7, new ItemStack(Material.AIR));
    }

    public void denyRequest(CPlayer player) {
        if (!signerToPlayer.containsValue(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You don't have any pending Autograph Requests!");
            return;
        }
        CPlayer tp = null;
        for (Map.Entry<UUID, UUID> entry : signerToPlayer.entrySet()) {
            if (entry.getValue().equals(player.getUniqueId())) {
                tp = Core.getPlayerManager().getPlayer(Bukkit.getPlayer(entry.getKey()));
                break;
            }
        }
        if (tp == null) {
            player.sendMessage(ChatColor.RED + "You don't have any Autograph Requests!");
            return;
        }
        signerToPlayer.remove(tp.getUniqueId());
        cancelTimer(tp.getUniqueId());
        final String name = tp.getRank().getTagColor() + tp.getName();
        final String name2 = player.getRank().getTagColor() + player.getName();
        player.getActionBar().show(ChatColor.RED + "You denied " + name + "'s " + ChatColor.RED +
                "Autograph Request!");
        tp.getActionBar().show(name2 + ChatColor.RED + " denied your Autograph Request!");
        player.sendMessage(ChatColor.RED + "You denied " + name + "'s " + ChatColor.RED + "Autograph Request!");
        tp.sendMessage(name2 + ChatColor.RED + " denied your Autograph Request!");
    }

    private void cancelTimer(UUID uuid) {
        Integer taskID1 = signerMap.remove(uuid);
        Integer taskID2 = receiverMap.remove(uuid);
        if (taskID1 != null) {
            Bukkit.getScheduler().cancelTask(taskID1);
        }
        if (taskID2 != null) {
            Bukkit.getScheduler().cancelTask(taskID2);
        }

    }

    public void removeAutograph(CPlayer player, final Integer num) {
        if (num < 2 || num > 50) {
            player.sendMessage(ChatColor.RED + "You can't remove this page!");
            return;
        }
        ItemStack book = player.getInventory().getItem(7);
        if (BlockEdit.isInBuildMode(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You can't be in Build Mode while removing autographs!");
            return;
        }
        if (book == null || !book.getType().equals(Material.WRITTEN_BOOK)) {
            player.sendMessage(ChatColor.RED + "You must have a book selected to remove an autograph");
            openMenu(player, ParkManager.getInstance().getPlayerData(player.getUniqueId()).getAutographs(), true);
            return;
        }
        BookMeta meta = (BookMeta) book.getItemMeta();
        int bookNumber = Integer.parseInt(meta.getTitle().replaceAll(BOOK_TITLE + " #", ""));
        Bukkit.getScheduler().runTaskAsynchronously(ParkManager.getInstance(), () -> {
            int pageNumber = (49 * (bookNumber - 1)) + num;
            PlayerData data = ParkManager.getInstance().getPlayerData(player.getUniqueId());
            List<Signature> autos = data.getAutographs();
            if (pageNumber > (data.getAutographs().size() + 1)) {
                player.sendMessage(ChatColor.RED + "That page doesn't exist!");
                player.sendMessage(ChatColor.YELLOW + "Make sure you're using the page number in your " +
                        ChatColor.ITALIC + "current book!");
                return;
            }
            autos.sort((o1, o2) -> {
                if (o1.getSigner().equals(o2.getSigner())) {
                    return (int) (o1.getTime() - o2.getTime());
                }
                return o1.getSigner().toLowerCase().compareTo(o2.getSigner().toLowerCase());
            });
            if (getSignatures(player.getUniqueId()).size() != autos.size()) {
                data.updateAutographs();
                giveBook(player);
                player.sendMessage(ChatColor.RED + "Your autograph page numbers just changed, make sure you're removing the correct page!");
                return;
            }
            Signature remove = autos.get(pageNumber - 2);
            Core.getMongoHandler().deleteAutograph(player.getUniqueId(), remove.getSigner(), remove.getTime());
            data.updateAutographs();
            giveBook(player);
            player.sendMessage(ChatColor.GREEN + "You removed the Autograph from " + ChatColor.BLUE + remove.getSigner() +
                    ChatColor.GREEN + " from your Autograph Book");
        });
    }

    private String getTimerMessage(int i) {
        switch (i) {
            case 20:
                return ChatColor.DARK_GREEN + "▉▉▉▉▉▉▉▉▉▉";
            case 19:
                return ChatColor.DARK_GREEN + "▉▉▉▉▉▉▉▉▉" + ChatColor.GREEN + "▉";
            case 18:
                return ChatColor.DARK_GREEN + "▉▉▉▉▉▉▉▉▉" + ChatColor.RED + "▉";
            case 17:
                return ChatColor.DARK_GREEN + "▉▉▉▉▉▉▉▉" + ChatColor.GREEN + "▉" + ChatColor.RED + "▉";
            case 16:
                return ChatColor.DARK_GREEN + "▉▉▉▉▉▉▉▉" + ChatColor.RED + "▉▉";
            case 15:
                return ChatColor.DARK_GREEN + "▉▉▉▉▉▉▉" + ChatColor.GREEN + "▉" + ChatColor.RED + "▉▉";
            case 14:
                return ChatColor.DARK_GREEN + "▉▉▉▉▉▉▉" + ChatColor.RED + "▉▉▉";
            case 13:
                return ChatColor.DARK_GREEN + "▉▉▉▉▉▉" + ChatColor.GREEN + "▉" + ChatColor.RED + "▉▉▉";
            case 12:
                return ChatColor.DARK_GREEN + "▉▉▉▉▉▉" + ChatColor.RED + "▉▉▉▉";
            case 11:
                return ChatColor.DARK_GREEN + "▉▉▉▉▉" + ChatColor.GREEN + "▉" + ChatColor.RED + "▉▉▉▉";
            case 10:
                return ChatColor.DARK_GREEN + "▉▉▉▉▉" + ChatColor.RED + "▉▉▉▉▉";
            case 9:
                return ChatColor.DARK_GREEN + "▉▉▉▉" + ChatColor.GREEN + "▉" + ChatColor.RED + "▉▉▉▉▉";
            case 8:
                return ChatColor.DARK_GREEN + "▉▉▉▉" + ChatColor.RED + "▉▉▉▉▉▉";
            case 7:
                return ChatColor.DARK_GREEN + "▉▉▉" + ChatColor.GREEN + "▉" + ChatColor.RED + "▉▉▉▉▉▉";
            case 6:
                return ChatColor.DARK_GREEN + "▉▉▉" + ChatColor.RED + "▉▉▉▉▉▉▉";
            case 5:
                return ChatColor.DARK_GREEN + "▉▉" + ChatColor.GREEN + "▉" + ChatColor.RED + "▉▉▉▉▉▉▉";
            case 4:
                return ChatColor.DARK_GREEN + "▉▉" + ChatColor.RED + "▉▉▉▉▉▉▉▉";
            case 3:
                return ChatColor.DARK_GREEN + "▉" + ChatColor.GREEN + "▉" + ChatColor.RED + "▉▉▉▉▉▉▉▉";
            case 2:
                return ChatColor.DARK_GREEN + "▉" + ChatColor.RED + "▉▉▉▉▉▉▉▉▉";
            case 1:
                return ChatColor.GREEN + "▉" + ChatColor.RED + "▉▉▉▉▉▉▉▉▉";
            case 0:
                return ChatColor.RED + "▉▉▉▉▉▉▉▉▉▉";
            default:
                return "";
        }
    }
}