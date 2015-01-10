package us.mcmagic.magicassistant.show.actions;

import us.mcmagic.magicassistant.show.Show;
import us.mcmagic.magicassistant.show.ShowNPC;

public class NPCRemoveAction extends ShowAction {
    public String Name;

    public NPCRemoveAction(Show show, long time, String npc) {
        super(show, time);

        Name = npc;
    }

    @Override
    public void Play() {
        //Remove Old
        ShowNPC npc = Show.GetNPC().remove(Name);
        if (npc != null)
            npc.Clean();

        System.out.println("Removed NPC: " + Name);
    }
}
