package us.mcmagic.magicassistant.autograph;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.utils.SqlUtil;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.actionbar.ActionBarManager;
import us.mcmagic.mcmagiccore.chat.formattedmessage.FormattedMessage;
import us.mcmagic.mcmagiccore.permissions.Rank;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by Jacob on 7/19/15.
 */
public class Autographs {
    private HashMap<UUID, UUID> map = new HashMap<>();
    private HashMap<UUID, Integer> map2 = new HashMap<>();
    private HashMap<UUID, Integer> map3 = new HashMap<>();
    private HashMap<UUID, UUID> active = new HashMap<>();
    private HashMap<UUID, String> nameMap = new HashMap<>();
    private HashMap<UUID, ItemStack> books = new HashMap<>();

    public void setBook(UUID uuid) {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK, 1);
        BookMeta bm = (BookMeta) book.getItemMeta();
        bm.addPage("This is your Autograph Book! Find Characters and they will sign it for you!");
        List<Signature> list = new ArrayList<>();
        try (Connection connection = SqlUtil.getConnection()) {
            PreparedStatement sql = connection.prepareStatement("SELECT * FROM autographs WHERE user=?");
            sql.setString(1, uuid.toString());
            ResultSet result = sql.executeQuery();
            while (result.next()) {
                list.add(new Signature(UUID.fromString(result.getString("sender")), result.getString("message")));
            }
            result.close();
            sql.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for (Signature sign : list) {
            String name = "Unknown";
            if (nameMap.containsKey(sign.getSigner())) {
                name = nameMap.get(sign.getSigner());
            } else {
                try (Connection connection = SqlUtil.getConnection()) {
                    PreparedStatement n = connection.prepareStatement("SELECT * FROM player_data WHERE uuid=?");
                    n.setString(1, sign.getSigner().toString());
                    ResultSet r = n.executeQuery();
                    if (!r.next()) {
                        r.close();
                        n.close();
                        continue;
                    }
                    Rank rank = Rank.fromString(r.getString("rank"));
                    name = rank.getTagColor() + r.getString("username");
                    nameMap.put(sign.getSigner(), name);
                    r.close();
                    n.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            bm.addPage(ChatColor.translateAlternateColorCodes('&', sign.getMessage()) + ChatColor.DARK_GREEN + "\n- "
                    + name);
        }
        bm.setTitle(ChatColor.DARK_AQUA + "Autograph Book");
        book.setItemMeta(bm);
        books.put(uuid, book);
    }

    public void giveBook(Player player) {
        ItemStack book = books.remove(player.getUniqueId());
        if (book == null) {
            return;
        }
        BookMeta bm = (BookMeta) book.getItemMeta();
        bm.setAuthor(player.getName());
        book.setItemMeta(bm);
        player.getInventory().setItem(7, book);
    }

    public void logout(Player player) {
        if (map.containsKey(player.getUniqueId())) {
            UUID tuuid = map.remove(player.getUniqueId());
            cancelTimer(player.getUniqueId());
            cancelTimer(tuuid);
            map.remove(player.getUniqueId());
            return;
        }
        if (map.containsValue(player.getUniqueId())) {
            for (Map.Entry<UUID, UUID> entry : map.entrySet()) {
                if (entry.getValue().equals(player.getUniqueId())) {
                    cancelTimer(player.getUniqueId());
                    cancelTimer(entry.getKey());
                    map.remove(entry.getKey());
                    return;
                }
            }
        }
    }

    public void sign(Player player, String message) {
        Player tp = null;
        for (Map.Entry<UUID, UUID> entry : active.entrySet()) {
            if (entry.getKey().equals(player.getUniqueId())) {
                tp = Bukkit.getPlayer(entry.getValue());
                break;
            }
        }
        if (tp == null) {
            player.sendMessage(ChatColor.RED + "You're not signing anyone's book right now!");
            return;
        }
        try (Connection connection = SqlUtil.getConnection()) {
            PreparedStatement sql = connection.prepareStatement("INSERT INTO autographs (user, sender, message) VALUES (?,?,?)");
            sql.setString(1, tp.getUniqueId().toString());
            sql.setString(2, player.getUniqueId().toString());
            sql.setString(3, message);
            sql.execute();
            sql.close();
        } catch (SQLException e) {
            e.printStackTrace();
            player.sendMessage(ChatColor.RED + "There was an error signing this book!");
            return;
        }
        setBook(tp.getUniqueId());
        giveBook(tp);
        tp.sendMessage(MCMagicCore.getUser(player.getUniqueId()).getRank().getTagColor() + player.getName() +
                ChatColor.GREEN + " has signed your Autograph Book!");
        player.sendMessage(ChatColor.GREEN + "You signed " + tp.getName() + "'s Autograph Book!");
        active.remove(player.getUniqueId());
    }

    public void requestToSign(final Player sender, final Player target) {
        if (map.containsValue(target.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "That player already has an autograph request!");
            return;
        }
        map.put(sender.getUniqueId(), target.getUniqueId());
        final String name = MCMagicCore.getUser(sender.getUniqueId()).getRank().getTagColor() + sender.getName();
        final String name2 = MCMagicCore.getUser(target.getUniqueId()).getRank().getTagColor() + target.getName();
        sender.sendMessage(ChatColor.GREEN + "Autograph Request sent to " + name2);
        FormattedMessage msg = new FormattedMessage(sender.getName()).color(MCMagicCore.getUser(sender.getUniqueId())
                .getRank().getTagColor()).then(" has sent you an ").color(ChatColor.GREEN).then("Autograph Request. ")
                .color(ChatColor.DARK_AQUA).style(ChatColor.BOLD).then("Click here to Accept").color(ChatColor.YELLOW)
                .command("/autograph accept").then(" - ").color(ChatColor.GREEN).then("Click here to Deny")
                .color(ChatColor.RED).command("/autograph deny");
        msg.send(target);
        map2.put(sender.getUniqueId(), Bukkit.getScheduler().runTaskLater(MagicAssistant.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (!map.containsKey(sender.getUniqueId())) {
                    return;
                }
                map.remove(sender.getUniqueId());
                sender.sendMessage(ChatColor.RED + "Your Autograph Request to " + name2 + ChatColor.RED +
                        " has timed out!");
                target.sendMessage(name + "'s " + ChatColor.RED + "Autograph Request sent to you has timed out!");
            }
        }, 400L).getTaskId());
        map3.put(sender.getUniqueId(), Bukkit.getScheduler().runTaskTimer(MagicAssistant.getInstance(), new Runnable() {
            int i = 20;

            @Override
            public void run() {
                if (i <= 0) {
                    ActionBarManager.sendMessage(target, ChatColor.RED + sender.getName() +
                            "'s Autograph Request Expired!");
                    ActionBarManager.sendMessage(sender, ChatColor.RED + "Your Autograph Request to " + target.getName()
                            + " Expired!");
                    cancelTimer(sender.getUniqueId());
                    return;
                }
                ActionBarManager.sendMessage(target, ChatColor.AQUA + sender.getName() + "'s Autograph Request: " +
                        getTimerMessage(i) + " " + ChatColor.AQUA + i + "s");
                ActionBarManager.sendMessage(sender, ChatColor.GREEN + "Your Autograph Request to " + ChatColor.AQUA +
                        target.getName() + ": " + getTimerMessage(i) + " " + ChatColor.AQUA + i + "s");
                i--;
            }
        }, 0, 20L).getTaskId());
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

    public void acceptRequest(Player player) {
        if (!map.containsValue(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You don't have any pending Autograph Requests!");
            return;
        }
        Player tp = null;
        for (Map.Entry<UUID, UUID> entry : map.entrySet()) {
            if (entry.getValue().equals(player.getUniqueId())) {
                tp = Bukkit.getPlayer(entry.getKey());
                break;
            }
        }
        if (tp == null) {
            player.sendMessage(ChatColor.RED + "You don't have any pending Autograph Requests!");
            return;
        }
        map.remove(tp.getUniqueId());
        cancelTimer(tp.getUniqueId());
        final String name = MCMagicCore.getUser(tp.getUniqueId()).getRank().getTagColor() + tp.getName();
        final String name2 = MCMagicCore.getUser(player.getUniqueId()).getRank().getTagColor() + player.getName();
        ActionBarManager.sendMessage(player, ChatColor.GREEN + "You accepted " + name + "'s " + ChatColor.GREEN +
                "Autograph Request!");
        ActionBarManager.sendMessage(tp, name2 + ChatColor.GREEN + " accepted your Autograph Request!");
        player.sendMessage(ChatColor.GREEN + "You accepted " + name + "'s " + ChatColor.GREEN + "Autograph Request!");
        tp.sendMessage(name2 + ChatColor.GREEN + " accepted your Autograph Request!");
        active.put(tp.getUniqueId(), player.getUniqueId());
        player.getInventory().setItem(7, new ItemStack(Material.AIR));
    }

    public void denyRequest(Player player) {
        if (!map.containsValue(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You don't have any pending Autograph Requests!");
            return;
        }
        Player tp = null;
        for (Map.Entry<UUID, UUID> entry : map.entrySet()) {
            if (entry.getValue().equals(player.getUniqueId())) {
                tp = Bukkit.getPlayer(entry.getKey());
                break;
            }
        }
        if (tp == null) {
            player.sendMessage(ChatColor.RED + "You don't have any Autograph Requests!");
            return;
        }
        map.remove(tp.getUniqueId());
        cancelTimer(tp.getUniqueId());
        final String name = MCMagicCore.getUser(tp.getUniqueId()).getRank().getTagColor() + tp.getName();
        final String name2 = MCMagicCore.getUser(player.getUniqueId()).getRank().getTagColor() + player.getName();
        ActionBarManager.sendMessage(player, ChatColor.RED + "You denied " + name + "'s " + ChatColor.RED +
                "Autograph Request!");
        ActionBarManager.sendMessage(tp, name2 + ChatColor.RED + " denied your Autograph Request!");
        player.sendMessage(ChatColor.RED + "You denied " + name + "'s " + ChatColor.RED + "Autograph Request!");
        tp.sendMessage(name2 + ChatColor.RED + " denied your Autograph Request!");
    }

    private void cancelTimer(UUID uuid) {
        Integer taskID1 = map2.remove(uuid);
        Integer taskID2 = map3.remove(uuid);
        if (taskID1 != null) {
            Bukkit.getScheduler().cancelTask(taskID1);
        }
        if (taskID2 != null) {
            Bukkit.getScheduler().cancelTask(taskID2);
        }

    }

    public void removeAutograph(Player player, Integer pageNum) {
    }
}