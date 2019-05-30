package network.palace.parkmanager.queues;

import lombok.Getter;
import lombok.Setter;
import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.QueueType;
import network.palace.parkmanager.utils.TimeUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.*;

@Getter
public abstract class Queue {
    private int id;
    private final UUID uuid;
    protected String name, warp;
    protected int groupSize, delay;
    protected Location station;
    @Setter protected boolean open;
    private LinkedList<UUID> queueMembers = new LinkedList<>();
    private List<QueueSign> signs;

    /**
     * The timestamp in milliseconds the next group will be brought in.
     * If equal to 0, no group is scheduled.
     */
    private long nextGroup = 0;

    public Queue(int id, UUID uuid, String name, String warp, int groupSize, int delay, boolean open, Location station, List<QueueSign> signs) {
        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.warp = warp;
        this.groupSize = groupSize;
        this.delay = delay;
        this.station = station;
        this.open = open;
        this.signs = signs;
    }

    /**
     * Add a player to the queue.
     * If, when they join, they're the only one in the queue and there's no scheduled group (nextGroup==0), then schedule a group 10 seconds from now.
     * This 10-second period allows other players to join if they want to, in case the player is with a group
     *
     * @param player the player
     */
    public void joinQueue(CPlayer player) {
        queueMembers.add(player.getUniqueId());
        player.sendMessage(ChatColor.GREEN + "You've joined the queue for " + name + ChatColor.GREEN +
                " at position #" + getPosition(player.getUniqueId()));
        if (queueMembers.size() == 1 && nextGroup == 0) {
            player.sendMessage(ChatColor.GREEN + "Since you joined an empty queue, you'll have a 10 second wait in case other players join.");
            nextGroup = TimeUtil.getCurrentSecondInMillis() + 10000;
        }
    }

    /**
     * Remove a player from the queue.
     * If, when they're removed from the queue, the queue is empty, cancel any scheduled group.
     *
     * @param player the player
     * @param force  true if player is being removed from the queue, false if the player is choosing to leave
     */
    public void leaveQueue(CPlayer player, boolean force) {
        if (queueMembers.remove(player.getUniqueId())) {
            if (force) {
                player.sendMessage(ChatColor.GREEN + "You've been removed from the queue for " + name);
            } else {
                player.sendMessage(ChatColor.GREEN + "You've left the queue for " + name);
            }
//            if (queueMembers.isEmpty()) {
            //Since the queue is empty, cancel the next scheduled group
//                nextGroup = 0;
//            }
        }
    }

    public void emptyQueue() {
        for (UUID uuid : new LinkedList<>(queueMembers)) {
            CPlayer player = Core.getPlayerManager().getPlayer(uuid);
            if (player != null) leaveQueue(player, true);
        }
    }

    public abstract QueueType getQueueType();

    /**
     * Called every second by a scheduled timer in QueueManager.
     * This method handles bringing in scheduled groups and scheduling new groups after previous groups are brought in.
     *
     * @param currentTime the output from System.currentTimeMillis from a higher-level timer, so all times are the same value.
     */
    public void tick(long currentTime) {
        if (nextGroup <= currentTime) {
            //Time to bring in the next group
            if (!queueMembers.isEmpty()) {
                //Only bring in a group if there are players in the queue
                if (!queueMembers.isEmpty()) {
                    //There's more players after the previous group, so schedule another group
                    nextGroup = currentTime + (delay * 1000);
                    bringInNextGroup();
                } else {
                    //The previous group was the last one in the queue, so don't schedule anything
                    nextGroup = 0;
                }
            } else {
                //No players were in the queue, so don't schedule anything
                nextGroup = 0;
            }
        } else if (nextGroup != 0) {
            //Next group is scheduled but hasn't happened yet, so we're counting down
            queueMembers.forEach(uuid -> {
                CPlayer player = Core.getPlayerManager().getPlayer(uuid);
                if (player != null)
                    player.getActionBar().show(ChatColor.GREEN + "You're #" + getPosition(uuid) + " in queue for " + name + " | " + ChatColor.LIGHT_PURPLE + "Wait: " + getWaitFor(uuid, currentTime));
            });
//        } else {
            //No group scheduled, do nothing
        }
        signs.forEach(queueSign -> {
            queueSign.setAmount(queueMembers.size());
            queueSign.setWait(getWaitFor(null, currentTime));
            queueSign.updateSign();
        });
    }

    /**
     * Get the wait time for the player in this queue.
     * If the player is in this queue, return their estimated wait time.
     * If the player is not in this queue, return the estimated wait time as if they were after the last player in queue.
     * ex: If there are 5 players in queue, and this player isn't one of them, get the wait time for the 6th player in queue.
     *
     * @param uuid the uuid of the player
     * @return a String with their estimated wait time, such as "5min 25s"
     */
    public String getWaitFor(UUID uuid) {
        return getWaitFor(uuid, TimeUtil.getCurrentSecondInMillis());
    }

    /**
     * Get the wait time for the player in this queue.
     * If the player is in this queue, return their estimated wait time.
     * If the player is not in this queue, return the estimated wait time as if they were after the last player in queue.
     * ex: If there are 5 players in queue, and this player isn't one of them, get the wait time for the 6th player in queue.
     *
     * @param uuid        the uuid of the player
     * @param currentTime the value from System.currentTimeMillis
     * @return a String with their estimated wait time, such as "5min 25s"
     */
    public String getWaitFor(UUID uuid, long currentTime) {
        if (nextGroup == 0) {
            return "No Wait";
        }
        int position = queueMembers.indexOf(uuid);
        if (position == -1) {
            //Not in queue, so get wait time as if they were after the last player in queue.
            position = queueMembers.size();
        }
        //The group the player is in, starting at 0
        int group = (int) Math.floor(((float) position) / groupSize);

        //Get time until player's group is up, plus time until current group is up
        int seconds = (int) Math.floor((delay * group) + ((nextGroup - currentTime) / 1000));

        Calendar from = new GregorianCalendar();
        from.setTimeInMillis(currentTime);

        Calendar to = new GregorianCalendar();
        to.setTimeInMillis(currentTime + (seconds * 1000));

        String msg = TimeUtil.formatDateDiff(from, to);
        return msg.equalsIgnoreCase("now") ? "No Wait" : msg;
    }

    /**
     * Called from {@link #tick(long)} once a scheduled group is brought in.
     * This method pops {@link #groupSize} players from the queue (or less, depending on the size of the queue).
     * These players are teleported to {@link #station} and the list of players is passed through to {@link #handleSpawn(List)}.
     * Any remaining players in the queue are notified they moved up {@link #groupSize} spaces.
     * <p>
     * If there are less than {@link #groupSize} players in the queue, the entire queue is brought in.
     * We can have groups smaller than {@link #groupSize}, but never larger.
     */
    protected void bringInNextGroup() {
        List<CPlayer> players = new ArrayList<>();
        for (int i = 0; i < groupSize; i++) {
            UUID uuid;
            try {
                uuid = queueMembers.pop();
            } catch (NoSuchElementException e) {
                break;
            }
            CPlayer player = Core.getPlayerManager().getPlayer(uuid);
            if (player != null) players.add(player);
        }
        if (players.isEmpty()) return;
        players.forEach(p -> p.teleport(station));
        handleSpawn(players);
        if (!queueMembers.isEmpty()) {
            ListIterator<UUID> iterator = queueMembers.listIterator(0);
            int pos = 1;
            while (iterator.hasNext()) {
                UUID uuid = iterator.next();
                CPlayer player = Core.getPlayerManager().getPlayer(iterator.next());
                player.sendMessage(ChatColor.GREEN + "You've moved up " + players.size() + " places in queue for " + name
                        + ChatColor.GREEN + " to position #" + pos++);
            }
        }
    }

    /**
     * A method meant to be defined in extensions of this class.
     * Those classes can do whatever they might need to with the list of players.
     *
     * @param players the list of players brought in from the queue to the station.
     */
    protected abstract void handleSpawn(List<CPlayer> players);

    /**
     * Get a player's position in the queue.
     *
     * @param uuid the uuid of the player
     * @return the player's position, or -1 if they're not in the queue
     */
    public int getPosition(UUID uuid) {
        int index = queueMembers.indexOf(uuid);
        if (index == -1) {
            return -1;
        }
        return index + 1;
    }

    public void addSign(QueueSign sign) {
        signs.add(sign);
        ParkManager.getQueueManager().saveToFile();
    }

    public void removeSign(QueueSign sign) {
        signs.remove(sign);
        ParkManager.getQueueManager().saveToFile();
    }

    public void removeSign(Location location) {
        QueueSign toRemove = null;
        for (QueueSign sign : getSigns()) {
            if (sign.getLocation().equals(location)) {
                toRemove = sign;
                break;
            }
        }
        if (toRemove != null) removeSign(toRemove);
    }

    public int getQueueSize() {
        return queueMembers.size();
    }

    public void updateSigns() {
        signs.forEach(QueueSign::updateSign);
    }

    public boolean isInQueue(CPlayer player) {
        return queueMembers.contains(player.getUniqueId());
    }
}
