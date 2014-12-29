package us.mcmagic.magicassistant.utils;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import us.mcmagic.magicassistant.MagicAssistant;

public class WorldGuardClass implements Listener {
    static MagicAssistant pl;

    public WorldGuardClass(MagicAssistant instance) {
        pl = instance;
    }

    public static void test(Player player) {
        World world = player.getWorld();
        Bukkit.broadcastMessage(pl.getWG().getRegionManager(world)
                .getApplicableRegions(player.getLocation()).toString());
    }

    public static boolean isInRegion(Player player, String region) {
        World world = player.getWorld();
        ApplicableRegionSet ar = pl.getWG().getRegionManager(world)
                .getApplicableRegions(player.getLocation());
        for (ProtectedRegion pr : ar) {
            return pr.getId().equals(region);
        }
        return false;
    }

    public static boolean isInPermGroup(Player player, String group) {
        return WorldGuardPlugin.inst().inGroup(player, group);
    }
}