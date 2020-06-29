package network.palace.parkmanager.queues.virtual;

import network.palace.core.Core;
import network.palace.parkmanager.dashboard.packets.parks.queue.CreateQueuePacket;
import network.palace.parkmanager.dashboard.packets.parks.queue.RemoveQueuePacket;
import network.palace.parkmanager.dashboard.packets.parks.queue.UpdateQueuePacket;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class VirtualQueueManager {
    private final List<VirtualQueue> queues = new ArrayList<>();

    public List<VirtualQueue> getQueues() {
        return queues.stream().collect(Collectors.toList());
    }

    public VirtualQueue getQueueById(String id) {
        for (VirtualQueue queue : getQueues()) {
            if (queue.getId().equals(id)) {
                return queue;
            }
        }
        return null;
    }

    public VirtualQueue getQueue(UUID uuid) {
        for (VirtualQueue queue : getQueues()) {
            if (queue.getUuid().equals(uuid)) {
                return queue;
            }
        }
        return null;
    }

    public void addQueue(VirtualQueue queue) {
        queues.add(queue);
        Core.getDashboardConnection().send(new CreateQueuePacket(queue.getId(), queue.getName(), queue.getHoldingArea(), queue.getServer()));
    }

    /**
     * Remove a VirtualQueue from this server
     *
     * @param id the id of the queue
     * @return true if successful, false if not
     */
    public boolean removeQueue(String id) {
        VirtualQueue queue = getQueueById(id);
        if (queue == null) return false;
        queues.remove(queue);
        if (queue.getServer().equals(Core.getInstanceName()))
            Core.getDashboardConnection().send(new RemoveQueuePacket(queue.getId()));
        return true;
    }

    public void handleCreate(CreateQueuePacket packet) {
        VirtualQueue queue = getQueueById(packet.getQueueId());
        if (queue != null) return;
        queues.add(new VirtualQueue(packet.getQueueId(), packet.getQueueName(), packet.getHoldingArea(), null, packet.getServer()));
    }

    public void handleRemove(RemoveQueuePacket packet) {
        removeQueue(packet.getQueueId());
    }

    public void handleUpdate(UpdateQueuePacket packet) {
        VirtualQueue queue = getQueueById(packet.getQueueId());
        if (queue == null) return;
        queue.setOpen(packet.isOpen());
        queue.updateQueue(packet.getQueue());
    }

    public void clearQueues() {
        queues.clear();
    }
}
