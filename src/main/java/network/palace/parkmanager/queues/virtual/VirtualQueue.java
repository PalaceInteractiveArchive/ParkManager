package network.palace.parkmanager.queues.virtual;

import lombok.Getter;
import lombok.Setter;
import network.palace.core.Core;
import network.palace.core.messagequeue.packets.SendPlayerPacket;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.ItemUtil;
import network.palace.core.utils.TextUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.message.PlayerQueuePacket;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

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
    private final int itemId;

    // whether players can join the queue
    @Getter private boolean open = false;
    // the list of players in queue
    private final LinkedList<UUID> queue = new LinkedList<>();
    private final HashMap<UUID, Long> sendToServer = new HashMap<>();

    @Getter @Setter private long lastAdvance = 0;
    @Getter @Setter private boolean updated = false;

    public VirtualQueue(String id, String name, int holdingArea, Location holdingAreaLocation, Location queueLocation, String server, Sign advanceSign, Sign stateSign, int itemId) {
        this.id = id;
        this.name = name;
        this.holdingArea = holdingArea;
        this.holdingAreaLocation = holdingAreaLocation;
        this.queueLocation = queueLocation;
        this.server = server;
        this.advanceSign = advanceSign;
        this.stateSign = stateSign;
        this.itemId = itemId;
    }

    public void admit() throws IOException {
        if (queue.isEmpty()) return;
        if (isHost()) {
            UUID uuid = queue.getFirst();
            CPlayer player = Core.getPlayerManager().getPlayer(uuid);
            if (player != null) {
                player.sendMessage(ChatColor.GREEN + "You've made it to the front of the queue!");
                player.getRegistry().removeEntry("virtualQueueHoldingArea");
                player.teleport(queueLocation);
            }
        }
        leaveQueue(queue.getFirst());
        updateSigns();
    }

    public void setOpen(boolean open) {
        if (this.open == open) return;
        this.open = open;
        updated = true;
        updateSigns();
        ListIterator<UUID> iterator = queue.listIterator();
        UUID uuid;
        int position = 1;
        String msg = open ? (ChatColor.GREEN + "The virtual queue " + name + ChatColor.GREEN + " has opened! You're in position #") :
                (ChatColor.AQUA + "The virtual queue " + name + ChatColor.AQUA + " has been closed! You're still in line, but you will lose your place if you leave the queue.");
        while (iterator.hasNext()) {
            uuid = iterator.next();
            position++;
            if (isHost()) ParkManager.getVirtualQueueManager().messagePlayer(uuid, open ? (msg + position) : msg);
        }
        if (isHost()) ParkManager.getVirtualQueueManager().setOpenStatus(this);
    }

    public void joinQueue(CPlayer player) throws Exception {
        if (!isHost()) {
            Core.getMessageHandler().sendMessage(new PlayerQueuePacket(id, player.getUniqueId(), true), Core.getMessageHandler().permanentClients.get("all_parks"));
            return;
        }
        joinQueue(player.getUniqueId());
    }

    public void joinQueue(UUID uuid) throws Exception {
        if (!open) {
            if (isHost())
                Core.getMessageHandler().sendMessageToPlayer(uuid, ChatColor.RED + "The virtual queue " + name + ChatColor.RED + " is currently closed, sorry!", false);
            return;
        }
        if (getPosition(uuid) >= 1) {
            if (isHost())
                Core.getMessageHandler().sendMessageToPlayer(uuid, ChatColor.RED + "You're already in the virtual queue " + name + "!", false);
            return;
        }
        queue.add(uuid);
        updated = true;
        updateSigns();
        if (isHost()) {
            Core.getMessageHandler().sendMessageToPlayer(uuid, ChatColor.GREEN + "You joined the virtual queue " + name +
                    " in position #" + getPosition(uuid) + "!", false);
            ParkManager.getVirtualQueueManager().addQueueMember(this, uuid);
        }
        if (getPosition(uuid) <= holdingArea) {
            markAsSendingToServer(uuid);
        }
    }

    public void leaveQueue(CPlayer player) throws IOException {
        leaveQueue(player, true);
    }

    public void leaveQueue(CPlayer player, boolean message) throws IOException {
        if (!isHost()) {
            Core.getMessageHandler().sendMessage(new PlayerQueuePacket(id, player.getUniqueId(), false), Core.getMessageHandler().permanentClients.get("all_parks"));
            return;
        }
        if (leaveQueue(player.getUniqueId())) {
            player.getRegistry().removeEntry("virtualQueueHoldingArea");
            if (message) player.sendMessage(ChatColor.GREEN + "You have left the virtual queue " + name + "!");
            if (Core.getInstanceName().equals(server)) player.performCommand("warp castle");
        } else {
            if (message) player.sendMessage(ChatColor.RED + "You aren't in the virtual queue " + name + "!");
        }
    }

    public boolean leaveQueue(UUID uuid) throws IOException {
        if (!isHost()) {
            Core.getMessageHandler().sendMessage(new PlayerQueuePacket(id, uuid, false), Core.getMessageHandler().permanentClients.get("all_parks"));
            return false;
        }
        int position = queue.indexOf(uuid);
        if (position >= 0) {
            queue.remove(uuid);
            updated = true;
            updateSigns();
            if (isHost()) ParkManager.getVirtualQueueManager().removeQueueMember(this, uuid);
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
            sendPositionMessage(uuid, position++);
        }
    }

    private void sendPositionMessage(CPlayer player) {
        sendPositionMessage(player, getPosition(player.getUniqueId()));
    }

    private void sendPositionMessage(CPlayer player, int pos) {
        if (pos >= 1)
            player.sendMessage(ChatColor.GREEN + "You are in position #" + pos + " in the virtual queue " + name + "!");
    }

    private void sendPositionMessage(UUID uuid, int pos) {
        if (pos >= 1 && isHost())
            ParkManager.getVirtualQueueManager().messagePlayer(uuid, ChatColor.GREEN +
                    "You are in position #" + pos + " in the virtual queue " + name + "!");
    }

    public int getPosition(UUID uuid) {
        return queue.indexOf(uuid) + 1;
    }

    public void updateQueue(List<UUID> queue) {
        if (queue == null) return;
        int size = this.queue.size();
        this.queue.clear();
        this.queue.addAll(queue);
        updated = true;
        updateSigns();
    }

    private void updateSigns() {
        if (!isHost()) return;
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
                stateSign.setLine(2, (open ? ChatColor.GREEN : ChatColor.RED) + "" + ChatColor.BOLD + (open ? "Open" : "Closed"));
                stateSign.setLine(3, ChatColor.YELLOW + "" + size + " Player" + TextUtil.pluralize(size));
                stateSign.update();
            }
        });
    }

    public boolean isHost() {
        return Core.getInstanceName().equals(server);
    }

    public ItemStack getItem(CPlayer player) {
        int pos = getPosition(player.getUniqueId());
        List<String> lore = new ArrayList<>(Arrays.asList(
                ChatColor.YELLOW + "Players: " + ChatColor.GREEN + getMembers().size(),
                ChatColor.YELLOW + "Status: " + (open ? ChatColor.GREEN + "Open" : ChatColor.RED + "Closed"),
                ChatColor.YELLOW + "Server: " + ChatColor.GREEN + server
        ));
        if (pos >= 1) {
            lore.addAll(0, Arrays.asList(ChatColor.RESET + "",
                    ChatColor.YELLOW + "" + ChatColor.BOLD + "Place in Line: " + ChatColor.AQUA + "" + ChatColor.BOLD + pos,
                    ChatColor.GREEN + "" + ChatColor.BOLD + "Right-Click to Leave the Queue", ChatColor.RESET + ""));
        } else {
            lore.addAll(Collections.singletonList(ChatColor.AQUA + "" + ChatColor.BOLD + "Right-Click to Join the Queue"));
        }
        return ItemUtil.create(Material.CONCRETE, 1, itemId, name, lore);
    }

    public List<UUID> getHoldingAreaMembers() {
        return queue.subList(0, queue.size() < holdingArea ? queue.size() : holdingArea);
    }

    public List<UUID> getMembers() {
        return new ArrayList<>(queue);
    }

    public HashMap<UUID, Long> getSendingToServer() {
        return new HashMap<>(sendToServer);
    }

    /**
     * Mark this player as going to be sent to the server in the next timer cycle
     *
     * @param uuid the uuid
     */
    public void markAsSendingToServer(UUID uuid) throws Exception {
        if (sendToServer.containsKey(uuid) || Core.getPlayerManager().getPlayer(uuid) != null) return;
        sendToServer.put(uuid, System.currentTimeMillis() + 10000);
        if (isHost())
            Core.getMessageHandler().sendMessageToPlayer(uuid, ChatColor.GREEN + "In " + ChatColor.AQUA + ChatColor.BOLD + "10 seconds " +
                    ChatColor.GREEN + "you will be sent to " + ChatColor.AQUA + server + ChatColor.GREEN +
                    " for the queue " + name + "...", false);
    }

    public void sendToServer(UUID uuid) {
        try {
            sendToServer.remove(uuid);
            if (isHost())
                Core.getMessageHandler().sendMessageToPlayer(uuid, ChatColor.GREEN + "Sending you to " + ChatColor.AQUA + server +
                        ChatColor.GREEN + " for the queue " + name + "...", false);
            Core.getMessageHandler().sendMessage(new SendPlayerPacket(uuid.toString(), Core.getInstanceName()), Core.getMessageHandler().ALL_PROXIES);
        } catch (Exception e) {
            Core.getInstance().getLogger().log(Level.SEVERE, "Error sending player to host server", e);
        }
    }
}
