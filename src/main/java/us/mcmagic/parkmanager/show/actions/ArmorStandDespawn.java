package us.mcmagic.parkmanager.show.actions;

import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import us.mcmagic.parkmanager.show.Show;
import us.mcmagic.parkmanager.show.handlers.armorstand.ShowStand;

/**
 * Created by Marc on 10/11/15
 */
public class ArmorStandDespawn extends ShowAction {
    private ShowStand stand;

    public ArmorStandDespawn(Show show, long time, ShowStand stand) {
        super(show, time);
        this.stand = stand;
    }

    @Override
    public void play() {
        if (!stand.hasSpawned()) {
            Bukkit.broadcast("ArmorStand with ID " + stand.getId() + " has not spawned", "arcade.bypass");
            return;
        }
        ArmorStand armor = stand.getStand();
        armor.remove();
        stand.setStand(null);
        stand.despawn();
    }
}