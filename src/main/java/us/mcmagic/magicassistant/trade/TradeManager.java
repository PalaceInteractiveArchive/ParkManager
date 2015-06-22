package us.mcmagic.magicassistant.trade;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.actionbar.ActionBarManager;
import us.mcmagic.mcmagiccore.itemcreator.ItemCreator;
import us.mcmagic.mcmagiccore.player.PlayerUtil;

import java.util.*;

/**
 * Created by Marc on 5/29/15
 */
public class TradeManager {
    private HashMap<UUID, UUID> map = new HashMap<>();
    private HashMap<UUID, Integer> map2 = new HashMap<>();
    private HashMap<UUID, Integer> map3 = new HashMap<>();
    private HashMap<UUID, UUID> active = new HashMap<>();
    private ItemStack finalize = new ItemCreator(Material.STAINED_CLAY, 1, (byte) 5, ChatColor.GREEN + "Confirm Trade",
            new ArrayList<String>());

    public void logout(Player player) {
        if (map.containsKey(player.getUniqueId())) {
            UUID tuuid = map.remove(player.getUniqueId());
            cancelTimer(player.getUniqueId());
            cancelTimer(tuuid);
            map.remove(player.getUniqueId());
            closeMenu(player, Bukkit.getPlayer(tuuid));
            return;
        }
        if (map.containsValue(player.getUniqueId())) {
            for (Map.Entry<UUID, UUID> entry : map.entrySet()) {
                if (entry.getValue().equals(player.getUniqueId())) {
                    cancelTimer(player.getUniqueId());
                    cancelTimer(entry.getKey());
                    map.remove(entry.getKey());
                    closeMenu(player, Bukkit.getPlayer(entry.getKey()));
                    return;
                }
            }
        }
    }

    public void addTrade(final Player sender, final Player target) {
        if (map.containsValue(target.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "That player already has a pending trade request!");
            return;
        }
        map.put(sender.getUniqueId(), target.getUniqueId());
        final String name = MCMagicCore.getUser(sender.getUniqueId()).getRank().getTagColor() + sender.getName();
        final String name2 = MCMagicCore.getUser(target.getUniqueId()).getRank().getTagColor() + target.getName();
        sender.sendMessage(ChatColor.GREEN + "Trade Request sent to " + name2);
        target.sendMessage(name + ChatColor.GREEN + " has sent you a Trade Request. Type /trade accept to accept, and /trade deny to deny.");
        map2.put(sender.getUniqueId(), Bukkit.getScheduler().runTaskLater(MagicAssistant.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (!map.containsKey(sender.getUniqueId())) {
                    return;
                }
                map.remove(sender.getUniqueId());
                sender.sendMessage(ChatColor.RED + "Your Trade Request to " + name2 + ChatColor.RED +
                        " has timed out!");
                target.sendMessage(name + "'s " + ChatColor.RED + "Trade Request sent to you has timed out!");
            }
        }, 400L).getTaskId());
        map3.put(sender.getUniqueId(), Bukkit.getScheduler().runTaskTimer(MagicAssistant.getInstance(), new Runnable() {
            int i = 20;

            @Override
            public void run() {
                if (i <= 0) {
                    ActionBarManager.sendMessage(target, ChatColor.RED + sender.getName() + "'s Trade Expired!");
                    ActionBarManager.sendMessage(sender, ChatColor.RED + "Your Trade to " + target.getName() + " Expired!");
                    cancelTimer(sender.getUniqueId());
                    return;
                }
                ActionBarManager.sendMessage(target, ChatColor.AQUA + sender.getName() + "'s Trade: " + getTimerMessage(i)
                        + " " + ChatColor.AQUA + i + "s");
                ActionBarManager.sendMessage(sender, ChatColor.GREEN + "Your Trade to " + ChatColor.AQUA + target.getName()
                        + ": " + getTimerMessage(i) + " " + ChatColor.AQUA + i + "s");
                i--;
            }
        }, 0, 20L).getTaskId());
    }

    public void acceptTrade(Player player) {
        if (!map.containsValue(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You don't have any pending Trade Requests!");
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
            player.sendMessage(ChatColor.RED + "You don't have any pending Trade Requests!");
            return;
        }
        map.remove(tp.getUniqueId());
        cancelTimer(tp.getUniqueId());
        final String name = MCMagicCore.getUser(tp.getUniqueId()).getRank().getTagColor() + tp.getName();
        final String name2 = MCMagicCore.getUser(player.getUniqueId()).getRank().getTagColor() + player.getName();
        ActionBarManager.sendMessage(player, ChatColor.GREEN + "You accepted " + name + "'s " + ChatColor.GREEN +
                "Trade Request!");
        ActionBarManager.sendMessage(tp, name2 + ChatColor.GREEN + " accepted your Trade Request!");
        player.sendMessage(ChatColor.GREEN + "You accepted " + name + "'s " + ChatColor.GREEN + "Trade Request!");
        tp.sendMessage(name2 + ChatColor.GREEN + " accepted your Trade Request!");
        active.put(tp.getUniqueId(), player.getUniqueId());
        String t1 = ChatColor.BLUE + "Trade with " + tp.getName();
        String t2 = ChatColor.BLUE + "Trade with " + player.getName();
        Inventory pinv = Bukkit.createInventory(player, InventoryType.CHEST, t1.length() > 32 ? t1.substring(0, 32) : t1);
        Inventory tinv = Bukkit.createInventory(tp, InventoryType.CHEST, t2.length() > 32 ? t2.substring(0, 32) : t2);
        ItemStack drag = new ItemCreator(Material.STAINED_CLAY, 1, (byte) 9, ChatColor.GRAY + "Drag your item(s) under you head.",
                new ArrayList<String>());
        ItemStack p1head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta p1hs = (SkullMeta) p1head.getItemMeta();
        p1hs.setOwner(player.getName());
        p1hs.setDisplayName(MCMagicCore.getUser(player.getUniqueId()).getRank().getTagColor() + player.getName());
        p1head.setItemMeta(p1hs);
        ItemStack p2head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta p2hs = (SkullMeta) p1head.getItemMeta();
        p2hs.setOwner(tp.getName());
        p2hs.setDisplayName(MCMagicCore.getUser(tp.getUniqueId()).getRank().getTagColor() + tp.getName());
        p2head.setItemMeta(p2hs);
        ItemStack waiting = new ItemCreator(Material.STAINED_CLAY, 1, (byte) 4, ChatColor.GOLD + "Waiting...", new ArrayList<String>());
        ItemStack[] a1 = new ItemStack[]{drag, drag, p1head, drag, drag, drag, p2head, drag, drag, drag, null, null, null,
                drag, waiting, waiting, waiting, drag, drag, drag, drag, drag, drag, drag, drag, drag, drag};
        ItemStack[] a2 = new ItemStack[]{drag, drag, p1head, drag, drag, drag, p2head, drag, drag, drag, waiting, waiting,
                waiting, drag, null, null, null, drag, drag, drag, drag, drag, drag, drag, drag, drag, drag};
        pinv.setContents(a1);
        tinv.setContents(a2);
        player.openInventory(pinv);
        tp.openInventory(tinv);
    }

    public void denyTrade(Player player) {
        if (!map.containsValue(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You don't have any pending Trade Requests!");
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
            player.sendMessage(ChatColor.RED + "You don't have any pending Trade Requests!");
            return;
        }
        map.remove(tp.getUniqueId());
        cancelTimer(tp.getUniqueId());
        final String name = MCMagicCore.getUser(tp.getUniqueId()).getRank().getTagColor() + tp.getName();
        final String name2 = MCMagicCore.getUser(player.getUniqueId()).getRank().getTagColor() + player.getName();
        ActionBarManager.sendMessage(player, ChatColor.RED + "You denied " + name + "'s " + ChatColor.RED +
                "Trade Request!");
        ActionBarManager.sendMessage(tp, name2 + ChatColor.RED + " denied your Trade Request!");
        player.sendMessage(ChatColor.RED + "You denied " + name + "'s " + ChatColor.RED + "Trade Request!");
        tp.sendMessage(name2 + ChatColor.RED + " denied your Trade Request!");
    }

    public void closeMenu(Player player, String title) {
        if (!active.containsKey(player.getUniqueId()) && !active.containsValue(player.getUniqueId())) {
            return;
        }
        Player tp = PlayerUtil.findPlayer(title.replaceFirst("Trade with ", ""));
        if (tp == null) {
            return;
        }
        closeMenu(player, tp);
    }

    private void closeMenu(Player player, Player tp) {
        active.remove(player.getUniqueId());
        for (Map.Entry<UUID, UUID> entry : active.entrySet()) {
            if (entry.getValue().equals(player.getUniqueId())) {
                active.remove(entry.getKey());
            }
        }
        Inventory inv = tp.getOpenInventory().getTopInventory();
        boolean starter = active.containsKey(player.getUniqueId());
        int pleast;
        int pmost;
        int tpleast;
        int tpmost;
        if (starter) {
            pleast = 10;
            pmost = 12;
            tpleast = 14;
            tpmost = 16;
        } else {
            pleast = 14;
            pmost = 16;
            tpleast = 10;
            tpmost = 12;
        }
        List<ItemStack> pitems = getItems(inv, pleast, pmost);
        List<ItemStack> tpitems = getItems(inv, tpleast, tpmost);
        tp.closeInventory();
        tp.sendMessage(MCMagicCore.getUser(player.getUniqueId()).getRank().getTagColor() + player.getName() +
                ChatColor.RED + "" + ChatColor.BOLD + " cancelled the Trade!");
        player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "You cancelled the Trade!");
        for (ItemStack i : pitems) {
            if (i == null) {
                continue;
            }
            if (i.getType().equals(Material.STAINED_CLAY)) {
                continue;
            }
            player.getInventory().addItem(i);
        }
        for (ItemStack i : tpitems) {
            if (i == null) {
                continue;
            }
            if (i.getType().equals(Material.STAINED_CLAY)) {
                continue;
            }
            tp.getInventory().addItem(i);
        }
    }

    private List<ItemStack> getItems(Inventory inv, int start, int stop) {
        if (start < 0 || stop < 0) {
            return new ArrayList<>();
        }
        List<ItemStack> list = new ArrayList<>();
        int n = 0;
        for (int i = start; i < (stop + 1); i++) {
            list.add(inv.getItem(i));
        }
        return list;
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

    public void handle(InventoryClickEvent event) {
        ItemStack stack = event.getCursor();
        if (stack == null) {
            if (event.getCurrentItem() != null) {
                stack = event.getCurrentItem();
            } else {
                return;
            }
        }
        Player player = (Player) event.getWhoClicked();
        Player tp = PlayerUtil.findPlayer(ChatColor.stripColor(player.getOpenInventory().getTopInventory().getTitle())
                .replaceFirst("Trade with ", ""));
        if (tp == null) {
            return;
        }
        if (stack.equals(finalize)) {
            finalize(player);
            return;
        }
        ItemMeta meta = stack.getItemMeta();
        InventoryAction action = event.getAction();
        Bukkit.broadcastMessage(action.toString());
        int slot = event.getSlot();
        boolean starter = active.containsKey(player.getUniqueId());
        int least;
        int most;
        if (starter) {
            least = 13;
            most = 17;
        } else {
            least = 9;
            most = 13;
        }
        if (!event.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
            if (slot > least && slot < most) {
                if (action.name().startsWith("PLACE_")) {
                    event.setCancelled(false);
                    updateInventory(player, tp.getOpenInventory().getTopInventory(), least + 1, most - 1);
                    return;
                }
                if (action.name().startsWith("PICKUP_") || action.equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
                    Inventory inv = tp.getOpenInventory().getTopInventory();
                    inv.setItem(slot, new ItemCreator(Material.STAINED_CLAY, 1, (byte) 4, ChatColor.GOLD + "Waiting...",
                            new ArrayList<String>()));
                    event.setCancelled(false);
                    return;
                }
            } else {
                return;
            }
        } else {
            event.setCancelled(false);
            if (slot == 8) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You can't trade your MagicBand!");
                return;
            }
            if (!action.equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
                return;
            }
        }
        if (!event.isCancelled()) {
            Inventory inv = tp.getOpenInventory().getTopInventory();
            inv.setItem(slot, stack);
        }
    }

    private void finalize(Player player) {
        Player tp = PlayerUtil.findPlayer(ChatColor.stripColor(player.getOpenInventory().getTopInventory().getTitle())
                .replaceFirst("Trade with ", ""));
        if (tp == null) {
            return;
        }
        Inventory inv = player.getOpenInventory().getTopInventory();
        Inventory tpinv = tp.getOpenInventory().getTopInventory();
    }

    private void updateInventory(final Player p, final Inventory tinv, final int least, final int most) {
        final ItemStack wait = new ItemCreator(Material.STAINED_CLAY, 1, (byte) 4, ChatColor.GOLD + "Waiting...",
                new ArrayList<String>());
        Bukkit.getScheduler().runTaskLater(MagicAssistant.getInstance(), new Runnable() {
            @Override
            public void run() {
                List<ItemStack> items = getItems(p.getOpenInventory().getTopInventory(), least, most);
                int i = least;
                for (ItemStack item : items) {
                    if (item == null || item.getType().equals(Material.AIR)) {
                        tinv.setItem(i, wait);
                    } else {
                        tinv.setItem(i, item);
                    }
                    i++;
                }
            }
        }, 10L);
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