package us.mcmagic.parkmanager.chairs;

import org.bukkit.Location;
import org.bukkit.entity.Arrow;

public interface IArrowFactory {
    Arrow spawnArrow(Location location);
}