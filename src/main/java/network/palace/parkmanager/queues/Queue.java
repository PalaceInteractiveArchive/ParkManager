package network.palace.parkmanager.queues;

import lombok.Getter;
import lombok.Setter;
import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.QueueType;
import network.palace.parkmanager.utils.TimeUtil;
import network.palace.parkwarp.ParkWarp;
import network.palace.parkwarp.handlers.Warp;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.*;

@Getter
public abstract class Queue {
    private int id;
    private final UUID uuid;
    @Setter protected String name, warp;
    @Setter protected int groupSize, delay;
    @Setter protected Location station;
    protected boolean open, paused = false;
    private LinkedList<UUID> queueMembers = new LinkedList<>();
    private LinkedList<UUID> fastPassMembers = new LinkedList<>();
    private List<QueueSign> signs;

    /**
     * The timestamp in milliseconds the next group will be brought in.
     * If equal to 0, no group is scheduled.
     */
    private long nextGroup = 0;

    /**
     * Used to keep track of switching between FastPass and standby groups
     */
    private boolean bringInFastPass = false;

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
     * @return true if the player successfully joins the queue, false if not
     */
    public boolean joinQueue(CPlayer player) {
        if (!open) {
            player.sendMessage(ChatColor.RED + "This queue is currently closed, check back soon!");
            return false;
        }
        ParkManager.getQueueManager().leaveAllQueues(player);
        queueMembers.add(player.getUniqueId());
        player.sendMessage(ChatColor.GREEN + "You've joined the queue for " + name + ChatColor.GREEN +
                " at position #" + getPosition(player.getUniqueId()));
        if (queueMembers.size() == 1 && nextGroup == 0) {
            player.sendMessage(ChatColor.GREEN + "Since you joined an empty queue, you'll have a 10 second wait in case other players join.");
            nextGroup = TimeUtil.getCurrentSecondInMillis() + 10000;
        }
        return true;
    }

    /**
     * Add a player to the FastPass queue.
     * If, when they join, the standby queue is empty, they will not be allowed to join the FastPass queue.
     * Players can only join the FastPass queue when the main queue isn't empty, otherwise they'd waste the FastPass.
     *
     * @param player the player
     * @return true if the player successfully joins the queue, false if not
     */
    public boolean joinFastPassQueue(CPlayer player) {
        if (!open) {
            player.sendMessage(ChatColor.RED + "This queue is currently closed, check back soon!");
            return false;
        }
        if (queueMembers.isEmpty()) {
            player.sendMessage(ChatColor.RED + "You can't join the FastPass queue when the standby queue is empty!");
            return false;
        }
        if (!player.getRegistry().hasEntry("fastPassCount")) {
            player.sendMessage(ChatColor.RED + "There was a problem redeeming your FastPass!");
            return false;
        }
        if (((int) player.getRegistry().getEntry("fastPassCount")) <= 0) {
            player.sendMessage(ChatColor.RED + "You do not have a FastPass to redeem, sorry!");
            return false;
        }
        fastPassMembers.add(player.getUniqueId());
        player.sendMessage(ChatColor.GREEN + "You've joined the queue for " + name + ChatColor.GREEN +
                " at position #" + getPosition(player.getUniqueId()));
        return true;
    }

    /**
     * Remove a player from the queue.
     *
     * @param player the player
     * @param force  true if player is being removed from the queue, false if the player is choosing to leave
     */
    public void leaveQueue(CPlayer player, boolean force) {
        boolean mainQueue = queueMembers.remove(player.getUniqueId());
        boolean fpQueue = fastPassMembers.remove(player.getUniqueId());
        if (mainQueue || fpQueue) {
            if (force) {
                player.sendMessage(ChatColor.GREEN + "You've been removed from the " + (fpQueue ? "FastPass" : "") + " queue for " + name);
            } else {
                player.sendMessage(ChatColor.GREEN + "You've left the" + (fpQueue ? "FastPass" : "") + " queue for " + name);
            }
        }
    }

    public void emptyQueue() {
        for (UUID uuid : new LinkedList<>(queueMembers)) {
            CPlayer player = Core.getPlayerManager().getPlayer(uuid);
            if (player != null) leaveQueue(player, true);
        }
        for (UUID uuid : new LinkedList<>(fastPassMembers)) {
            CPlayer player = Core.getPlayerManager().getPlayer(uuid);
            if (player != null) leaveQueue(player, true);
        }
    }

    public abstract QueueType getQueueType();

    public void setOpen(boolean b) {
        this.open = b;
        String msg;
        if (b) {
            msg = ChatColor.GREEN + "The queue for " + name + ChatColor.GREEN + " has just reopened!";
        } else {
            msg = ChatColor.GREEN + "The queue for " + name + ChatColor.GREEN + " has just been " + ChatColor.RED + "closed. "
                    + ChatColor.GREEN + "You can keep your place in line, but if you leave you can't rejoin until the queue reopens!";
        }
        queueMembers.forEach(id -> {
            CPlayer player = Core.getPlayerManager().getPlayer(id);
            if (player == null) return;
            player.sendMessage(msg);
        });
        fastPassMembers.forEach(id -> {
            CPlayer player = Core.getPlayerManager().getPlayer(id);
            if (player == null) return;
            player.sendMessage(msg);
        });
    }

    /**
     * Called every second by a scheduled timer in QueueManager.
     * This method handles bringing in scheduled groups and scheduling new groups after previous groups are brought in.
     *
     * @param currentTime the output from System.currentTimeMillis from a higher-level timer, so all times are the same value.
     */
    public void tick(long currentTime) {
        if (!open || (paused && nextGroup != 0)) {
            nextGroup += 1000;
        }
        if (nextGroup != 0) {
            //A group is scheduled
            if (nextGroup <= currentTime && open && !paused) {
                //Time to bring in the next group
                if (!queueMembers.isEmpty() || !fastPassMembers.isEmpty()) {
                    //There's more players after the previous group, so schedule another group
                    nextGroup = currentTime + (delay * 1000);
                    bringInNextGroup();
                } else {
                    //No players are in the queue, so don't schedule anything
                    nextGroup = 0;
                }
            } else {
                //Next group is scheduled but hasn't happened yet, so we're counting down
                queueMembers.forEach(uuid -> {
                    CPlayer player = Core.getPlayerManager().getPlayer(uuid);
                    if (player != null)
                        player.getActionBar().show(ChatColor.GREEN + "You're #" + getPosition(uuid) + " in queue for " + name + ChatColor.YELLOW + " | " + "Wait: " + getWaitFor(uuid, currentTime));
                });
                fastPassMembers.forEach(uuid -> {
                    CPlayer player = Core.getPlayerManager().getPlayer(uuid);
                    if (player != null)
                        player.getActionBar().show(ChatColor.GREEN + "You're #" + getPosition(uuid) + " in queue for " + name + ChatColor.YELLOW + " | " + "Wait: " + getWaitFor(uuid, currentTime));
                });
            }
        }
        signs.forEach(queueSign -> {
            queueSign.setAmount(queueSign.isFastPassSign() ? fastPassMembers.size() : queueMembers.size());
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
        if (!open) {
            return ChatColor.RED + "Closed";
        }
        if (nextGroup == 0) {
            return "No Wait" + (paused ? (ChatColor.YELLOW + " Paused") : "");
        }
        LinkedList<UUID> fullQueue = getFullQueue();
        int position = fullQueue.indexOf(uuid);
        if (position == -1) {
            //Not in queue, so get wait time as if they were after the last player in queue.
            position = fullQueue.size();
        }
        //The group the player is in, starting at 0
        int group = (int) Math.floor(((float) position) / groupSize);

        //Get time until player's group is up, plus time until current group is up
        int seconds = (int) Math.floor((delay * group) + ((nextGroup - currentTime) / 1000f));

        Calendar from = new GregorianCalendar();
        from.setTimeInMillis(currentTime);

        Calendar to = new GregorianCalendar();
        to.setTimeInMillis(currentTime + (seconds * 1000));

        String msg = TimeUtil.formatDateDiff(from, to);
        return (msg.equalsIgnoreCase("now") ? "No Wait" : msg) + (paused ? (ChatColor.YELLOW + " Paused") : "");
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
        LinkedList<UUID> fullQueue = getFullQueue();
        bringInFastPass = !bringInFastPass;
        for (int i = 0; i < groupSize; i++) {
            UUID uuid;
            try {
                uuid = fullQueue.pop();
            } catch (NoSuchElementException e) {
                break;
            }
            CPlayer player = Core.getPlayerManager().getPlayer(uuid);
            if (player != null) players.add(player);
        }
        if (players.isEmpty()) return;
        players.forEach(p -> {
            queueMembers.remove(p.getUniqueId());
            if (fastPassMembers.remove(p.getUniqueId())) {
                if (!ParkManager.getQueueManager().chargeFastPass(p)) {
                    // Player doesn't have a FastPass
                    p.sendMessage(ChatColor.RED + "You don't have a FastPass for this ride!");
                    Warp w = ParkWarp.getWarpUtil().findWarp(getWarp());
                    if (w != null) p.teleport(w);
                    return;
                }
                p.sendMessage(ChatColor.GREEN + "You have been charged " + ChatColor.AQUA + "1 " + ChatColor.GREEN + "FastPass!");
            }
            p.teleport(station);
        });
        handleSpawn(players);
        if (!fullQueue.isEmpty()) {
            ListIterator<UUID> iterator = fullQueue.listIterator(0);
            int pos = 1;
            while (iterator.hasNext()) {
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
        int index = getFullQueue().indexOf(uuid);
        if (index == -1) return -1;
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

    public int getFastPassQueueSize() {
        return fastPassMembers.size();
    }

    /**
     * Get the full queue with both standby and FastPass queues
     *
     * @return a LinkedList with the FastPass and main queue merged together
     */
    public LinkedList<UUID> getFullQueue() {
        if (fastPassMembers.isEmpty()) return new LinkedList<>(queueMembers);

        LinkedList<UUID> list = new LinkedList<>();
        int standby = queueMembers.size(), fp = fastPassMembers.size();
        boolean bringInFP = bringInFastPass;

        while (standby > 0 || fp > 0) {
            if (bringInFP && fp > 0) {
                fp = processList(fp, fastPassMembers, list);
            } else if (standby > 0) {
                standby = processList(standby, queueMembers, list);
            }
            bringInFP = !bringInFP;
        }

        return list;
    }

    private int processList(int size, LinkedList<UUID> fromList, LinkedList<UUID> toList) {
        if (size <= groupSize) {
            toList.addAll(fromList);
            size = 0;
        } else {
            for (int i = 0; i < groupSize; i++) {
                toList.add(fromList.get(i));
            }
            size -= groupSize;
        }
        return size;
    }

    public void updateSigns() {
        signs.forEach(QueueSign::updateSign);
    }

    public boolean isInQueue(CPlayer player) {
        return queueMembers.contains(player.getUniqueId()) || fastPassMembers.contains(player.getUniqueId());
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }
}
