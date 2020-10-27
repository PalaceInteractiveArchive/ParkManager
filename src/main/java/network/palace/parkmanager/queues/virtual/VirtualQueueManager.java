package network.palace.parkmanager.queues.virtual;

import com.google.common.collect.ImmutableMap;
import network.palace.core.Core;
import network.palace.core.menu.Menu;
import network.palace.core.menu.MenuButton;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.ItemUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.dashboard.packets.parks.queue.CreateQueuePacket;
import network.palace.parkmanager.dashboard.packets.parks.queue.RemoveQueuePacket;
import network.palace.parkmanager.dashboard.packets.parks.queue.UpdateQueuePacket;
import org.bukkit.block.Sign;
import org.bukkit.event.inventory.ClickType;

import java.util.*;
import java.util.stream.Collectors;

public class VirtualQueueManager {
    private final List<VirtualQueue> queues = new ArrayList<>();
    private HashMap<UUID, Menu> openMenus = new HashMap<>();

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

    public VirtualQueue getQueue(Sign s) {
        for (VirtualQueue queue : getQueues()) {
            Sign advanceSign = queue.getAdvanceSign();
            Sign stateSign = queue.getStateSign();
            if ((advanceSign != null && advanceSign.getLocation().equals(s.getLocation()))
                    || (stateSign != null && stateSign.getLocation().equals(s.getLocation()))) return queue;
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
        if (!queue.isOpen() && queue.getServer().equals(Core.getInstanceName()))
            Core.getDashboardConnection().send(new RemoveQueuePacket(queue.getId()));
        return true;
    }

    public void handleCreate(CreateQueuePacket packet) {
        VirtualQueue queue = getQueueById(packet.getQueueId());
        if (queue != null) return;
        queues.add(new VirtualQueue(packet.getQueueId(), packet.getQueueName(), packet.getHoldingArea(), null,
                null, packet.getServer(), null, null, getRandomItemId()));
    }

    public void handleRemove(RemoveQueuePacket packet) {
        removeQueue(packet.getQueueId());
    }

    public void handleUpdate(UpdateQueuePacket packet) {
        VirtualQueue queue = getQueueById(packet.getQueueId());
        if (queue == null) return;
        queue.setOpen(packet.isOpen());
        queue.updateQueue(packet.getQueue());
        int i = -1;
        for (VirtualQueue q : queues) {
            i++;
            if (q.getUuid().equals(queue.getUuid())) break;
        }
        int pos = i;
        Core.runTask(ParkManager.getInstance(), () -> openMenus.forEach((uuid, menu) -> {
            CPlayer player = Core.getPlayerManager().getPlayer(uuid);
            if (player != null) {
                menu.setButton(new MenuButton(pos, ItemUtil.unbreakable(queue.getItem(player)), ImmutableMap.of(ClickType.RIGHT, p -> {
                    p.closeInventory();
                    if (queue.getPosition(p.getUniqueId()) < 1) {
                        queue.joinQueue(p);
                    } else {
                        queue.leaveQueue(p);
                    }
                })));
            }
        }));
    }

    public void clearQueues() {
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
}
