package network.palace.parkmanager.queues.virtual;

import lombok.Getter;
import lombok.Setter;
import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.TextUtil;
import network.palace.parkmanager.ParkManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class VirtualQueue {
    // id of the queue
    // name of the queue
    @Getter protected String id, name;
    // number of players that fit in the holding area for the queue
    @Getter protected int holdingArea;
    @Getter protected Location holdingAreaLocation, queueLocation;
    // server the queue was created on
    @Getter protected String server;
    @Getter @Setter protected Sign advanceSign;
    @Getter @Setter protected Sign stateSign;
    @Getter private final UUID uuid = UUID.randomUUID();

    // whether players can join the queue
    @Getter private boolean open = false;
    // the list of players in queue
    private final LinkedList<UUID> queue = new LinkedList<>();
    @Getter @Setter private long lastAdvance = 0;

    public VirtualQueue(String id, String name, int holdingArea, Location holdingAreaLocation, Location queueLocation, String server, Sign advanceSign, Sign stateSign) {
        this.id = id;
        this.name = name;
        this.holdingArea = holdingArea;
        this.holdingAreaLocation = holdingAreaLocation;
        this.queueLocation = queueLocation;
        this.server = server;
        this.advanceSign = advanceSign;
        this.stateSign = stateSign;
    }

    public void admit() {
        if (queue.isEmpty()) return;
        leaveQueue(queue.getFirst());
    }

    public void setOpen(boolean open) {
        if (this.open == open) return;
        this.open = open;
        updateSigns();
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
            return true;
        }
        return false;
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
        int size = this.queue.size();
        this.queue.clear();
        this.queue.addAll(queue);
        if (this.queue.size() != size) updateSigns();
    }

    private void updateSigns() {
        Core.runTask(ParkManager.getInstance(), () -> {
            int size = this.queue.size();
            if (advanceSign != null) {
                advanceSign.setLine(0, ChatColor.AQUA + "[Virtual Queue]");
                advanceSign.setLine(1, ChatColor.BLUE + id);
                advanceSign.setLine(2, ChatColor.YELLOW + "" + ChatColor.BOLD + "Advance");
                advanceSign.setLine(3, ChatColor.YELLOW + "" + size + " Player" + TextUtil.pluralize(size));
                advanceSign.update();
            }
            if (stateSign != null) {
                stateSign.setLine(0, ChatColor.AQUA + "[Virtual Queue]");
                stateSign.setLine(1, ChatColor.BLUE + id);
                stateSign.setLine(2, (open ? ChatColor.GREEN : ChatColor.RED) + "" + ChatColor.BOLD + (open ? "Opened" : "Closed"));
                stateSign.setLine(3, ChatColor.YELLOW + "" + size + " Player" + TextUtil.pluralize(size));
                stateSign.update();
            }
        });
    }

    public boolean cantEdit() {
        return !Core.getInstanceName().equals(server);
    }
}
