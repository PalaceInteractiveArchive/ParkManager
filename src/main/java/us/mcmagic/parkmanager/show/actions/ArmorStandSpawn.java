package us.mcmagic.parkmanager.show.actions;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import us.mcmagic.parkmanager.show.Show;
import us.mcmagic.parkmanager.show.handlers.armorstand.ShowStand;

/**
 * Created by Marc on 10/11/15
 */
public class ArmorStandSpawn extends ShowAction {
    private ShowStand stand;
    private Location loc;

    public ArmorStandSpawn(Show show, long time, ShowStand stand, Location loc) {
        super(show, time);
        this.stand = stand;
        this.loc = loc;
    }

    @Override
    public void play() {
        if (stand.hasSpawned()) {
            Bukkit.broadcast("ArmorStand with ID " + stand.getId() + " has spawned already", "arcade.bypass");
            return;
        }
        stand.spawn();
        ArmorStand armor = loc.getWorld().spawn(loc, ArmorStand.class);
        stand.setStand(armor);
    }
}