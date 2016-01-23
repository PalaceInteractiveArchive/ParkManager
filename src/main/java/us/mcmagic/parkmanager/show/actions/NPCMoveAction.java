package us.mcmagic.parkmanager.show.actions;

import org.bukkit.Location;
import us.mcmagic.parkmanager.show.Show;
import us.mcmagic.parkmanager.show.handlers.ShowNPC;

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
        ShowNPC npc = show.getNPCMap().get(Name);
        if (npc != null) {
            npc.SetTarget(Location, Speed);
        }
    }
}
