package network.palace.parkmanager.utils;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;

import java.util.UUID;

public class BuildUtil {

    public boolean isInBuildMode(UUID uuid) {
        return isInBuildMode(Core.getPlayerManager().getPlayer(uuid));
    }

    public boolean isInBuildMode(CPlayer player) {
        if (player == null || !player.getRegistry().hasEntry("buildMode")) return false;
        return (boolean) player.getRegistry().getEntry("buildMode");
    }
}
