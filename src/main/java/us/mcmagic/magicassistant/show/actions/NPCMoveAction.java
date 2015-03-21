package us.mcmagic.magicassistant.show.actions;

import us.mcmagic.magicassistant.show.Show;
import us.mcmagic.magicassistant.show.ShowNPC;
import org.bukkit.Location;

public class NPCMoveAction extends ShowAction {
    public String Name;
    public Location Location;
    public float Speed;

    public NPCMoveAction(Show show, long time, String npc, Location location, float speed) {
        super(show, time);

        Name = npc;
        Location = location;
        Speed = speed;
    }

    @Override
    public void play() {
        ShowNPC npc = Show.GetNPC().get(Name);
        if (npc != null) {
            npc.SetTarget(Location, Speed);
        }
    }
}
