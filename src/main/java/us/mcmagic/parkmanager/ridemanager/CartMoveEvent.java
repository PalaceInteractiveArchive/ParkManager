package us.mcmagic.parkmanager.ridemanager;

import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by Marc on 4/1/15
 */
public class CartMoveEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private Cart cart;
    private Location from;
    private Location to;
    private boolean cancelled = false;

    public CartMoveEvent(Cart cart, Location from, Location to) {
        this.cart = cart;
        this.from = from;
        this.to = to;
    }

    public Cart getCart() {
        return cart;
    }

    public Location getFrom() {
        return from;
    }

    public Location getTo() {
        return to;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }
}
