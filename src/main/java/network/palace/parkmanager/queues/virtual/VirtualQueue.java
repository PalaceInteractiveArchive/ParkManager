package network.palace.parkmanager.queues.virtual;

import lombok.Getter;
import lombok.Setter;
import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.*;

public class VirtualQueue {
    // id of the queue
    // name of the queue
    @Getter protected String id, name;
    // number of players that fit in the holding area for the queue
    @Getter protected int holdingArea;
    @Getter protected Location holdingAreaLocation, queueLocation;
    // server the queue was created on
    @Getter protected String server;
    @Getter private final UUID uuid = UUID.randomUUID();

    // whether players can join the queue
    @Getter private boolean open = false;
    // the list of players in queue
    private final LinkedList<UUID> queue = new LinkedList<>();

    @Getter @Setter private boolean updated = false;

    public VirtualQueue(String id, String name, int holdingArea, Location holdingAreaLocation, String server) {
        this.id = id;
        this.name = name;
        this.holdingArea = holdingArea;
        this.holdingAreaLocation = holdingAreaLocation;
        this.server = server;
    }

    public void admit() {
        if (queue.isEmpty()) return;
        leaveQueue(queue.getFirst());
    }

    public void setOpen(boolean open) {
        if (this.open == open) return;
        this.open = open;
        updated = true;
        ListIterator<UUID> iterator = queue.listIterator();
        UUID uuid;
        int position = 1;
        String msg = open ? (ChatColor.GREEN + "The virtual queue " + name + " has opened! You're in position #") :
                (ChatColor.AQUA + "The virtual queue " + name + " has been closed! You're still in line, but you will lose your place if you leave the queue.");
        while (iterator.hasNext()) {
            uuid = iterator.next();
            position++;
            CPlayer tp;
            if (uuid != null && ((tp = Core.getPlayerManager().getPlayer(uuid)) != null)) {
                tp.sendMessage(msg + (open ? (msg + position) : msg));
            }
        }
    }

    public boolean joinQueue(CPlayer player) {
        if (!open) {
            player.sendMessage(ChatColor.RED + "The virtual queue " + name + ChatColor.RED + " is currently closed, sorry!");
            return false;
        }
        if (getPosition(player.getUniqueId()) >= 1) {
            player.sendMessage(ChatColor.RED + "You're already in the virtual queue " + name + "!");
            return false;
        }
        queue.add(player.getUniqueId());
        updated = true;
        player.sendMessage(ChatColor.GREEN + "You joined the virtual queue " + name + "!");
        return true;
    }

    public boolean leaveQueue(CPlayer player, boolean message) {
        if (leaveQueue(player.getUniqueId())) {
            if (message) player.sendMessage(ChatColor.GREEN + "You have left the virtual queue " + name + "!");
            return true;
        } else {
            if (message) player.sendMessage(ChatColor.RED + "You aren't in the virtual queue " + name + "!");
            return false;
        }
    }

    public boolean leaveQueue(UUID uuid) {
        int position = queue.indexOf(uuid);
        if (position >= 0) {
            queue.remove(uuid);
            updated = true;
            ListIterator<UUID> iterator = queue.listIterator(position);
            UUID playerInQueue;
            while (iterator.hasNext()) {
                playerInQueue = iterator.next();
                position++;
                CPlayer tp;
                if (playerInQueue != null && ((tp = Core.getPlayerManager().getPlayer(playerInQueue)) != null)) {
                    sendPositionMessage(tp, position);
                }
            }
            return true;
        }
        return false;
    }

    public void sendPositionMessages() {
        ListIterator<UUID> iterator = queue.listIterator();
        UUID uuid;
        int position = 1;
        while (iterator.hasNext()) {
            uuid = iterator.next();
            position++;
            CPlayer tp;
            if (uuid != null && ((tp = Core.getPlayerManager().getPlayer(uuid)) != null)) {
                sendPositionMessage(tp, position);
            }
        }
    }

    private void sendPositionMessage(CPlayer player) {
        sendPositionMessage(player, getPosition(player.getUniqueId()));
    }

    private void sendPositionMessage(CPlayer player, int pos) {
        if (pos >= 1) {
            player.sendMessage(ChatColor.GREEN + "You are in position #" + pos + " in the virtual queue " + name + "!");
        }
    }

    public int getPosition(UUID uuid) {
        return queue.indexOf(uuid) + 1;
    }

    public List<UUID> getHoldingAreaMembers() {
        return queue.subList(0, queue.size() < holdingArea ? queue.size() : holdingArea);
    }

    public List<UUID> getMembers() {
        return new ArrayList<>(queue);
    }

    public void updateQueue(List<UUID> queue) {
        this.queue.clear();
        this.queue.addAll(queue);
    }
}
