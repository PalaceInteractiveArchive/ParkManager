package us.mcmagic.parkmanager.show.actions;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.audioserver.AudioArea;
import us.mcmagic.parkmanager.show.Show;

import java.util.UUID;

/**
 * Created by Marc on 2/27/16
 */
public class AudioStart extends ShowAction {
    private AudioArea area;

    public AudioStart(Show show, long time, String areaName) {
        super(show, time);
        area = MCMagicCore.audioServer.getByName(areaName);
        if (area == null) {
            Bukkit.broadcast("Error finding Audio Area " + areaName + "!", "arcade.bypass");
        }
    }

    @Override
    public void play() {
        show.musicTime = System.currentTimeMillis();
        show.areaName = area.getAreaName();
        if (area != null) {
            for (UUID uuid : show.getNearPlayers()) {
                Player tp = Bukkit.getPlayer(uuid);
                if (tp != null) {
                    area.triggerPlayer(tp);
                }
            }
        }
    }
}