package us.mcmagic.magicassistant.show.actions;

import org.bukkit.Location;

import us.mcmagic.magicassistant.show.Show;

public class BlockAction extends ShowAction {
    public Location Location;
    public int Type;
    public byte Data;

    public BlockAction(Show show, long time, Location location, int type, byte data) {
        super(show, time);

        Location = location;
        Type = type;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void Play() {
        Location.getBlock().setTypeId(Type);
    }
}
