package network.palace.parkmanager.queues.virtual;

import com.google.common.collect.ImmutableMap;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import network.palace.core.Core;
import network.palace.core.menu.Menu;
import network.palace.core.menu.MenuButton;
import network.palace.core.messagequeue.MessageClient;
import network.palace.core.messagequeue.packets.MQPacket;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.ItemUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.message.CreateQueuePacket;
import network.palace.parkmanager.message.PlayerQueuePacket;
import network.palace.parkmanager.message.RemoveQueuePacket;
import network.palace.parkmanager.message.UpdateQueuePacket;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.event.inventory.ClickType;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

public class VirtualQueueManager {
    private final List<VirtualQueue> queues = new ArrayList<>();
    private final HashMap<UUID, Menu> openMenus = new HashMap<>();
    private final MongoCollection<Document> virtualQueuesCollection;

    public VirtualQueueManager() {
        virtualQueuesCollection = Core.getMongoHandler().getDatabase().getCollection("virtual_queues");
        Core.runTaskTimer(new Runnable() {
            int i = 1;

            @Override
            public void run() {
                if (queues.isEmpty()) return;

                boolean announce = i++ >= 8;
                if (announce) i = 1;

                List<MQPacket> packets = new ArrayList<>();
                queues.forEach(queue -> {
                    // Only run this task on the host server
                    if (!queue.isHost()) return;

                    // Message all players their current position (including players not on current server)
                    if (announce) queue.sendPositionMessages();

                    // Send out any necessary updates to all park servers
                    if (queue.isUpdated()) {
                        queue.setUpdated(false);
                        packets.add(new UpdateQueuePacket(queue.getId(), queue.isOpen(), queue.getMembers()));
                    }

                    // Only do the remaining tasks if the queue is open
                    if (!queue.isOpen()) return;

                    // Send all sendingToServer players to the host server
                    HashMap<UUID, Long> sendingToServer = queue.getSendingToServer();
                    sendingToServer.forEach((uuid, time) -> {
                        if (System.currentTimeMillis() >= time) queue.sendToServer(uuid);
                    });

                    // Notify all players in the holdingArea that they are about to be brought to the host server
                    List<UUID> holdingAreaMembers = queue.getHoldingAreaMembers();
                    holdingAreaMembers.forEach(uuid -> {
                        try {
                            if (sendingToServer.containsKey(uuid)) return;
                            CPlayer player;
                            if ((player = Core.getPlayerManager().getPlayer(uuid)) != null) {
                                if (!player.getRegistry().hasEntry("virtualQueueHoldingArea")) {
                                    player.getRegistry().addEntry("virtualQueueHoldingArea", true);
                                    player.teleport(queue.getHoldingAreaLocation());
                                }
                            } else {
                                queue.markAsSendingToServer(uuid);
                            }
                        } catch (Exception e) {
                            Core.getInstance().getLogger().log(Level.SEVERE, "Error sending cross-server message for virtual queue", e);
                        }
                    });
                });
                MessageClient allParks = Core.getMessageHandler().permanentClients.get("all_parks");
                packets.forEach(packet -> {
                    try {
                        Core.getMessageHandler().sendMessage(packet, allParks);
                    } catch (IOException e) {
                        Core.getInstance().getLogger().log(Level.SEVERE, "Error sending virtual queue update packets", e);
                    }
                });
            }
        }, 20L, 100L);
    }

    @SuppressWarnings("rawtypes")
    public void initializeFromDatabase() {
        for (Document doc : virtualQueuesCollection.find()) {
            VirtualQueue queue = new VirtualQueue(doc.getString("queueId"), doc.getString("queueName"),
                    doc.getInteger("holdingArea"), null, null, doc.getString("server"),
                    null, null, getRandomItemId());
            List<UUID> members = new ArrayList<>();
            ArrayList array = doc.get("queue", ArrayList.class);
            for (Object o : array) {
                try {
                    members.add(UUID.fromString((String) o));
                } catch (Exception ignored) {
                }
            }
            queue.updateQueue(members);
            if (queue.isHost()) {
                // If the host server is loading a queue from the database, the server likely crashed without properly removing the queue
                try {
                    ParkManager.getVirtualQueueManager().removeQueue(queue);
                } catch (Exception e) {
                    Core.getInstance().getLogger().log(Level.SEVERE, "Error closing virtual queue after startup following recent crash", e);
                }
            } else {
                queue.setOpen(doc.getBoolean("open"));
                queues.add(queue);
            }
        }
    }

    public List<VirtualQueue> getQueues() {
        return new ArrayList<>(queues);
    }

    public VirtualQueue getQueueById(String id) {
        for (VirtualQueue queue : getQueues()) {
            if (queue.getId().equals(id)) {
                return queue;
            }
        }
        return null;
    }

    public VirtualQueue getQueue(Sign s) {
        for (VirtualQueue queue : getQueues()) {
            Sign advanceSign = queue.getAdvanceSign();
            Sign stateSign = queue.getStateSign();
            if ((advanceSign != null && advanceSign.getLocation().equals(s.getLocation()))
                    || (stateSign != null && stateSign.getLocation().equals(s.getLocation()))) return queue;
        }
        return null;
    }

    public void addQueue(VirtualQueue queue) throws IOException {
        queues.add(queue);
        if (queue.isHost()) {
            List<String> members = new ArrayList<>();
            queue.getMembers().forEach(uuid -> members.add(uuid.toString()));
            virtualQueuesCollection.insertOne(
                    new Document("queueId", queue.getId())
                            .append("queueName", queue.getName())
                            .append("server", queue.getServer())
                            .append("holdingArea", queue.getHoldingArea())
                            .append("open", queue.isOpen())
                            .append("queue", members)
            );
        }
        Core.getMessageHandler().sendMessage(new CreateQueuePacket(queue.getId(), queue.getName(), queue.getHoldingArea(), queue.getServer()), Core.getMessageHandler().permanentClients.get("all_parks"));
    }

    /**
     * Remove a VirtualQueue from this server
     *
     * @param id the id of the queue
     * @return true if successful, false if not
     */
    public boolean removeQueue(String id) throws IOException {
        VirtualQueue queue = getQueueById(id);
        if (queue == null) return false;
        return removeQueue(queue);
    }

    /**
     * Remove a VirtualQueue from this server
     *
     * @param queue the queue
     * @return true if successful, false if not
     */
    public boolean removeQueue(VirtualQueue queue) throws IOException {
        queues.remove(queue);
        if (queue.isHost()) {
            queue.getMembers().forEach(uuid -> {
                try {
                    Core.getMessageHandler().sendMessageToPlayer(uuid, ChatColor.AQUA + "The virtual queue " + queue.getName() + ChatColor.AQUA + " has been removed.", false);
                    Core.getMessageHandler().sendStaffMessage(ChatColor.GREEN + "A virtual queue (" + queue.getName() +
                            ChatColor.GREEN + ") has been removed from " + ChatColor.AQUA + Core.getInstanceName() +
                            ChatColor.GREEN);
                } catch (Exception e) {
                    Core.getInstance().getLogger().log(Level.SEVERE, "Error sending cross-server message for virtual queue", e);
                }
            });
            virtualQueuesCollection.deleteOne(Filters.eq("queueId", queue.getId()));
            Core.getMessageHandler().sendMessage(new RemoveQueuePacket(queue.getId()), Core.getMessageHandler().permanentClients.get("all_parks"));
        }
        return true;
    }

    public void handleCreate(CreateQueuePacket packet) {
        VirtualQueue queue = getQueueById(packet.getQueueId());
        if (queue != null) return;
        queues.add(new VirtualQueue(packet.getQueueId(), packet.getQueueName(), packet.getHoldingArea(), null,
                null, packet.getServer(), null, null, getRandomItemId()));
    }

    public void handleRemove(RemoveQueuePacket packet) {
        try {
            removeQueue(packet.getQueueId());
        } catch (IOException e) {
            Core.getInstance().getLogger().log(Level.SEVERE, "Error removing virtual queue - MessageQueue issue?", e);
        }
    }

    public void handleUpdate(UpdateQueuePacket packet) {
        VirtualQueue queue = getQueueById(packet.getQueueId());
        if (queue == null || queue.isHost()) return;
        queue.setOpen(packet.isOpen());
        queue.updateQueue(packet.getQueue());
        int i = -1;
        for (VirtualQueue q : queues) {
            i++;
            if (q.getId().equals(queue.getId())) break;
        }
        int pos = i;
        Core.runTask(ParkManager.getInstance(), () -> openMenus.forEach((uuid, menu) -> {
            CPlayer player = Core.getPlayerManager().getPlayer(uuid);
            if (player != null) {
                menu.setButton(new MenuButton(pos, ItemUtil.unbreakable(queue.getItem(player)), ImmutableMap.of(ClickType.RIGHT, p -> {
                    p.closeInventory();
                    if (queue.getPosition(p.getUniqueId()) < 1) {
                        try {
                            queue.joinQueue(p);
                        } catch (Exception e) {
                            Core.getInstance().getLogger().log(Level.SEVERE, "Error joining virtual queue", e);
                            p.sendMessage(ChatColor.RED + "An error occurred while joining that queue - try again soon!");
                        }
                    } else {
                        try {
                            queue.leaveQueue(p);
                        } catch (Exception e) {
                            Core.getInstance().getLogger().log(Level.SEVERE, "Error joining virtual queue", e);
                            p.sendMessage(ChatColor.RED + "An error occurred while joining that queue - try again soon!");
                        }
                    }
                })));
            }
        }));
    }

    public void handlePlayer(PlayerQueuePacket packet) {
        VirtualQueue queue = getQueueById(packet.getQueueId());
        if (queue == null || !queue.isHost()) return;
        if (packet.isJoining()) {
            try {
                queue.joinQueue(packet.getPlayerUUID());
            } catch (Exception e) {
                Core.getInstance().getLogger().log(Level.SEVERE, "Error adding player to virtual queue via packet", e);
            }
        } else {
            try {
                queue.leaveQueue(packet.getPlayerUUID());
            } catch (IOException e) {
                Core.getInstance().getLogger().log(Level.SEVERE, "Error removing player from virtual queue via packet", e);
            }
        }
    }

    public void clearQueues() {
        new ArrayList<>(queues).forEach(queue -> {
            try {
                removeQueue(queue);
            } catch (IOException e) {
                Core.getInstance().getLogger().log(Level.SEVERE, "Error removing hosted queue on shutdown!", e);
            }
        });
        queues.clear();
    }

    public int getRandomItemId() {
        int[] ids = new int[]{0, 1, 2, 3, 4, 5, 6, 9, 10, 11, 13, 14};
        return ids[new Random().nextInt(ids.length - 1)];
    }

    public void addToOpenMenus(CPlayer player, Menu menu) {
        openMenus.put(player.getUniqueId(), menu);
        menu.setOnClose(() -> removeFromOpenMenus(player));
    }

    private void removeFromOpenMenus(CPlayer player) {
        openMenus.remove(player.getUniqueId());
    }

    public void messagePlayer(UUID uuid, String msg) {
        try {
            Core.getMessageHandler().sendMessageToPlayer(uuid, msg, false);
        } catch (Exception e) {
            Core.getInstance().getLogger().log(Level.SEVERE, "Error sending cross-server message with MQ", e);
        }
    }

    public void setOpenStatus(VirtualQueue queue) {
        virtualQueuesCollection.updateOne(Filters.eq("queueId", queue.getId()), Updates.set("open", queue.isOpen()));
    }

    public void addQueueMember(VirtualQueue queue, UUID member) {
        virtualQueuesCollection.updateOne(Filters.eq("queueId", queue.getId()), Updates.push("queue", member.toString()));
    }

    public void removeQueueMember(VirtualQueue queue, UUID member) {
        virtualQueuesCollection.updateOne(Filters.eq("queueId", queue.getId()), Updates.pull("queue", member.toString()));
    }
}
