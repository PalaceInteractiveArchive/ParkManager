package us.mcmagic.magicassistant;

import org.bukkit.ChatColor;
import us.mcmagic.magicassistant.magicband.BandColor;
import us.mcmagic.mcmagiccore.permissions.Rank;
import us.mcmagic.mcmagiccore.player.PlayerUtil;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Marc on 12/13/14
 */
public class PlayerData {
    public UUID uuid;
    public boolean dvc;
    public ChatColor bandName;
    public BandColor bandColor;
    public HashMap<UUID, String> friends = new HashMap<>();

    public PlayerData(UUID uuid, ChatColor bandName, BandColor bandColor, HashMap<UUID, String> friends) {
        this.uuid = uuid;
        dvc = PlayerUtil.getUser(uuid).getRank().equals(Rank.DVCMEMBER);
        this.bandName = bandName;
        this.bandColor = bandColor;
        this.friends = friends;
    }

    public UUID getUuid() {
        return uuid;
    }

    public boolean getDVC() {
        return dvc;
    }

    public ChatColor getBandName() {
        return bandName;
    }

    public BandColor getBandColor() {
        return bandColor;
    }

    public HashMap<UUID, String> getFriendList() {
        return friends;
    }
}
