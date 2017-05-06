package network.palace.parkmanager.uso.rrr;

import network.palace.audio.Audio;
import network.palace.audio.handlers.AudioArea;
import network.palace.core.utils.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * Created by Marc on 4/9/17.
 */
public class RipRideRockit {
    private HashMap<String, Song> songs = new HashMap<>();
    private HashMap<UUID, String> selections = new HashMap<>();

    public RipRideRockit() {
        initialize();
    }

    public void initialize() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File("plugins/ParkManager/songs.yml"));
        /*
        songs:
            hbfs:
                area: rockit-hbfs
                title: '&aHarder Better Faster Stronger'
         */
        ConfigurationSection section = config.getConfigurationSection("songs");
        if (section == null) {
            return;
        }
        int i = 0;
        for (String s : section.getKeys(false)) {
            if (i >= 7) {
                break;
            }
            ConfigurationSection sec = section.getConfigurationSection(s);
            String title = ChatColor.translateAlternateColorCodes('&', sec.getString("title"));
            ItemStack item = ItemUtil.create(randomRecord(), title);
            Song song = new Song(s, sec.getString("area"), title, item);
            songs.put(s, song);
            i++;
        }
        for (Map.Entry<String, Song> entry : songs.entrySet()) {
            Song song = entry.getValue();
        }
    }

    public void remove(UUID uuid) {
        selections.remove(uuid);
    }

    public void handle(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        String name = meta.getDisplayName();
        for (Song s : songs.values()) {
            if (s.getTitle().equals(name)) {
                selections.put(player.getUniqueId(), s.getName());
                break;
            }
        }
        player.closeInventory();
        player.sendMessage(ChatColor.GREEN + "You selected " + name + "!");
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 2f);
    }

    public void startSong(Player player) {
        player.closeInventory();
        if (!selections.containsKey(player.getUniqueId())) {
            return;
        }
        Song song = songs.get(selections.remove(player.getUniqueId()));
        AudioArea area = Audio.getInstance().getByName(song.getArea());
        if (area == null) {
            return;
        }
        area.triggerPlayer(player);
    }

    public void chooseSong(Player player) {
        if (!player.isInsideVehicle()) {
            return;
        }
        Inventory inv = Bukkit.createInventory(player, 27, ChatColor.RED + "Rip Ride Rockit Song Selection");
        int place = 13;
        int change = 1;
        if (songs.size() % 2 == 0) {
            place = 12;
            change = 2;
        }
        for (Song s : songs.values()) {
            inv.setItem(place, s.getItem());
            place += change;
            if (change >= 1) {
                change = -(change + 1);
            } else {
                change = -(change - 1);
            }
        }
        player.openInventory(inv);
    }

    private Material randomRecord() {
        switch (new Random().nextInt(12) + 1) {
            case 1:
                return Material.GOLD_RECORD;
            case 2:
                return Material.GREEN_RECORD;
            case 3:
                return Material.RECORD_3;
            case 4:
                return Material.RECORD_4;
            case 5:
                return Material.RECORD_5;
            case 6:
                return Material.RECORD_6;
            case 7:
                return Material.RECORD_7;
            case 8:
                return Material.RECORD_8;
            case 9:
                return Material.RECORD_9;
            case 10:
                return Material.RECORD_10;
            case 11:
                return Material.RECORD_11;
            default:
                return Material.RECORD_12;
        }
    }
}
