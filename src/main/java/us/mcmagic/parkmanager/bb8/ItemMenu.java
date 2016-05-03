package us.mcmagic.parkmanager.bb8;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import us.mcmagic.parkmanager.ParkManager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

// Author: BeMacized
// http://models.bemacized.net/

public class ItemMenu implements Listener {
    private String title;
    private HashMap<Integer, MenuItem> items;
    private Inventory inv;
    private final Player player;
    private int taskid = -1;
    private boolean closeOnClick;
    private boolean dead;
    private int cancel_task = -1;

    public ItemMenu(String title, Player player) {
        this.player = player;
        this.title = title;
        items = new HashMap<>();
    }

    public void setCancelTask(int cancel_task) {
        this.cancel_task = cancel_task;
    }

    public void setCloseOnClick(boolean value) {
        closeOnClick = value;
    }

    public MenuItem addItem(int slot, MenuItem item) {
        items.put(slot, item);
        return item;
    }

    public MenuItem addItem(int x, int y, MenuItem item) {
        return addItem(x + y * 9, item);
    }

    public void clear() {
        items.clear();
        inv.clear();
    }

    public void show() {
        player.closeInventory();
        Bukkit.getPluginManager().registerEvents(this, ParkManager.getInstance());
        taskid = Bukkit.getScheduler().scheduleSyncDelayedTask(ParkManager.getInstance(), this::close, 20L * 300L);
        regenerate();
        player.openInventory(inv);
    }

    private int getSize() {
        int highestSlot = 0;
        for (Integer integer : items.keySet()) {
            if (integer > highestSlot) {
                highestSlot = integer;
            }
        }
        return (int) Math.ceil((highestSlot + 1) / 9.0d) * 9;
    }

    public void regenerate() {
        Iterator<Map.Entry<Integer, MenuItem>> it = items.entrySet().iterator();
        inv = Bukkit.createInventory(player, getSize(), title);
        while (it.hasNext()) {
            Map.Entry<Integer, MenuItem> next = it.next();
            inv.setItem(next.getKey(), next.getValue().getItem().get());
        }
    }

    public void close() {
        HandlerList.unregisterAll(this);
        if (inv != null) player.closeInventory();
        if (taskid != -1) Bukkit.getScheduler().cancelTask(taskid);
        if (cancel_task != -1) Bukkit.getScheduler().cancelTask(cancel_task);
        dead = true;
    }

    public boolean isDead() {
        return dead;
    }

    public void update() {
        inv.clear();
        for (Map.Entry<Integer, MenuItem> next : items.entrySet()) {
            inv.setItem(next.getKey(), next.getValue().getItem().get());
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer().equals(player)) close();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        if (event.getWhoClicked().equals(player) && event.getCurrentItem() != null) {
            int slot = event.getSlot();
            MenuItem item = items.get(slot);
            if (item != null) {
                event.setCancelled(true);
                item.getExec().run();
                if (this.closeOnClick) close();
            }
        }
    }

}