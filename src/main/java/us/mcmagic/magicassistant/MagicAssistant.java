package us.mcmagic.magicassistant;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.java.JavaPlugin;
import us.mcmagic.magicassistant.blockchanger.BlockChanger;
import us.mcmagic.magicassistant.commands.*;
import us.mcmagic.magicassistant.handlers.*;
import us.mcmagic.magicassistant.listeners.*;
import us.mcmagic.magicassistant.resourcepack.PackManager;
import us.mcmagic.magicassistant.shooter.MessageTimer;
import us.mcmagic.magicassistant.shooter.Shooter;
import us.mcmagic.magicassistant.show.ticker.Ticker;
import us.mcmagic.magicassistant.utils.*;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.resource.ResourceStatusEvent;

import java.io.*;
import java.util.*;

public class MagicAssistant extends JavaPlugin implements Listener {
    public static Inventory ni;
    public static List<FoodLocation> foodLocations = new ArrayList<>();
    public static HashMap<UUID, PlayerData> playerData = new HashMap<>();
    public int randomNumber = 0;
    public static List<Warp> warps = new ArrayList<>();
    public static List<HotelRoom> hotelRooms = new ArrayList<>();
    public static HashMap<Integer, List<Ride>> ridePages = new HashMap<>();
    public static HashMap<Integer, List<PlayerData.Attraction>> attPages = new HashMap<>();
    public static String serverName;
    public static Location spawn;
    public static Location hub;
    public static boolean spawnOnJoin;
    public static boolean crossServerInv;
    public static boolean resortsServer;
    public static FileConfiguration config = YamlConfiguration.loadConfiguration(new File("plugins/MagicAssistant/config.yml"));
    private WorldGuardPlugin wg;
    public static List<String> joinMessages = config
            .getStringList("join-messages");
    public static Map<Integer, Integer> firstJoinItems = new HashMap<>();
    public static Map<UUID, String> userCache = new HashMap<>();
    public static List<String> newJoinMessage = new ArrayList<>();
    public static boolean party = false;
    public static List<String> partyServer = new ArrayList<>();
    public static boolean hubServer;
    public static boolean serverEnabling = true;
    public static MagicAssistant instance;
    public BlockChanger blockChanger = new BlockChanger();
    public PackManager packManager = new PackManager();
    public BandUtil bandUtil = new BandUtil();


    public void onEnable() {
        instance = this;
        SqlUtil.initialize();
        registerListeners();
        registerCommands();
        InventoryUtil.initialize();
        bandUtil.initialize();
        bandUtil.askForParty();
        getConfig().options().copyDefaults(true);
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        Bukkit.getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new PluginMessage(this));
        saveConfig();
        FileUtil.setupConfig();
        FileUtil.setupMenuFile();
        try {
            blockChanger.initialize();
        } catch (FileNotFoundException ignored) {
            File file = new File("plugins/MagicAssistant/blockchanger.yml");
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        warps.clear();
        setupFirstJoinItems();
        setupNewJoinMessages();
        WarpUtil.refreshWarps();
        getLogger().info("Warps Initialized!");
        getLogger().info("Initializing Hotel Rooms...");
        HotelUtil.refreshRooms();
        Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
            @Override
            public void run() {
                if (MCMagicCore.getInstance().getMCMagicConfig().serverName.equalsIgnoreCase("Resorts")) { //Originally 'resorts'
                    boolean updateNecessary = false;
                    for (HotelRoom room : hotelRooms) {
                        if (room.getOccupationCooldown() > 0) {
                            room.decrementOccupationCooldown();
                            HotelUtil.updateRoom(room);
                            updateNecessary = true;
                        } else if (room.isOccupied()) {
                            UUID uuid = UUID.fromString(room.getCurrentOccupant());
                            if (Bukkit.getPlayer(uuid) != null && Bukkit.getPlayer(uuid).isOnline()) {
                                Bukkit.getPlayer(uuid).sendMessage(ChatColor.GREEN + "Your reservation of the " + room.getName() + " room has lapsed and you have been checked out.  Please come stay with us again soon!");
                            } else {
                                room.setCheckoutNotificationRecipient(uuid.toString());
                            }
                            room.setCurrentOccupant(null);
                            HotelUtil.updateRoom(room);
                            updateNecessary = true;
                        }
                    }
                    if (updateNecessary) {
                        HotelUtil.updateRooms();
                    }
                }
            }
        }, 0L, 72000L);
        Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
            @Override
            public void run() {
                boolean updateNecessary = false;
                for (HotelRoom room : hotelRooms) {
                    if (room.getCheckoutNotificationRecipient() != null) {
                        UUID uuid = UUID.fromString(room.getCheckoutNotificationRecipient());
                        if (Bukkit.getPlayer(uuid) != null && Bukkit.getPlayer(uuid).isOnline()) {
                            Bukkit.getPlayer(uuid).sendMessage(ChatColor.GREEN + "Your reservation of the " + room.getName() + " room has lapsed and you have been checked out.  Please come stay with us again soon!");
                            room.setCheckoutNotificationRecipient(null);
                            HotelUtil.updateRoom(room);
                            updateNecessary = true;
                        }
                    }
                }
                if (updateNecessary) {
                    HotelUtil.updateRooms();
                }
            }
        }, 0L, 6000L);
        getLogger().info("Hotel Rooms Initialized!");
        getLogger().info("Initializing Food Locations...");
        setupFoodLocations();
        setupRides();
        setupAttractions();
        if (config.getBoolean("show-server")) {
            // Show Ticker
            System.out.println("Starting Show Timer");
            Bukkit.getScheduler().runTaskTimer(this, new Ticker(), 1, 1);
            System.out.println("Show Timer Started!");
        }
        hub = new Location(Bukkit.getWorlds().get(0), getConfig().getDouble("hub.x"), getConfig().getDouble("hub.y"), getConfig().getDouble("hub.z"), getConfig().getInt("hub.yaw"), getConfig().getInt("hub.pitch"));
        spawn = new Location(Bukkit.getWorld(getConfig().getString("spawn.world")), getConfig().getDouble("spawn.x"), getConfig().getDouble("spawn.y"), getConfig().getDouble("spawn.z"), getConfig().getInt("spawn.yaw"), getConfig().getInt("spawn.pitch"));
        serverName = getConfig().getString("server-name");
        spawnOnJoin = getConfig().getBoolean("spawn-on-join");
        crossServerInv = getConfig().getBoolean("transfer-inventories");
        resortsServer = serverName == "Resorts";
        hubServer = getConfig().getBoolean("hub-server");
        packManager.initialize();
        Bukkit.getScheduler().runTaskLater(this, new Runnable() {
            @Override
            public void run() {
                getLogger().info("Players can now join");
                serverEnabling = false;
            }
        }, 100L);
    }

    public void onDisable() {
        warps.clear();
    }

    public static MagicAssistant getInstance() {
        return instance;
    }

    public static List<Warp> getWarps() {
        return new ArrayList<>(warps);
    }

    public static void clearWarps() {
        warps.clear();
    }

    public void setupNewJoinMessages() {
        FileConfiguration config = getConfig();
        List<String> msgs = config.getStringList("first-join-message");
        for (String msg : msgs) {
            newJoinMessage.add(ChatColor.translateAlternateColorCodes('&', msg));
        }
    }

    public void setupFirstJoinItems() {
        FileConfiguration config = getConfig();
        for (String s : config.getStringList("first-join-items")) {
            String[] items = s.split(" ");
            try {
                firstJoinItems.put(Integer.parseInt(items[0]),
                        Integer.parseInt(items[1]));
            } catch (NumberFormatException ignored) {
            }
        }
    }

    public void setHub(Location loc) {
        hub = loc;
    }

    public WorldGuardPlugin getWG() {
        return wg;
    }

    public static boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void sendToServer(Player player, String server) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("Connect");
            out.writeUTF(server);
            player.sendPluginMessage(this, "BungeeCord", b.toByteArray());
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED
                    + "Sorry! It looks like something went wrong! It's probably out fault. We will try to fix it as soon as possible!");
        }
    }

    public static YamlConfiguration config() {
        return YamlConfiguration.loadConfiguration(new File(
                "plugins/magicassistant/config.yml"));
    }

    public static boolean isInPermGroup(Player player, String group) {
        String[] groups = WorldGuardPlugin.inst().getGroups(player);
        for (String group1 : groups) {
            if (group1.toLowerCase().equals(group.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public static PlayerData getPlayerData(UUID uuid) {
        return playerData.get(uuid);
    }

    public void setupFoodLocations() {
        File file = new File("plugins/MagicAssistant/menus.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        List<String> locations = config.getStringList("food-names");
        for (String location : locations) {
            String name = config
                    .getString("food." + location + ".name");
            String warp = config
                    .getString("food." + location + ".warp");
            int type = config.getInt("food." + location + ".type");
            byte data;
            if (config.contains("food." + location + ".data")) {
                data = (byte) config.getInt("food." + location
                        + ".data");
            } else {
                data = 0;
            }
            FoodLocation loc = new FoodLocation(name, warp, type, data);
            foodLocations.add(loc);
        }
    }

    private void setupRides() {
        File file = new File("plugins/MagicAssistant/menus.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        List<String> locations = config.getStringList("ride-names");
        List<Ride> rides = new ArrayList<>();
        for (String location : locations) {
            String name = config
                    .getString("ride." + location + ".name");
            String warp = config
                    .getString("ride." + location + ".warp");
            int type = config.getInt("ride." + location + ".type");
            byte data;
            if (config.contains("ride." + location + ".data")) {
                data = (byte) config.getInt("ride." + location
                        + ".data");
            } else {
                data = 0;
            }
            Ride ride = new Ride(name, warp, type, data);
            rides.add(ride);
        }
        int pages;
        if (locations.isEmpty()) {
            pages = 1;
        } else {
            pages = ((int) Math.ceil(locations.size() / 21)) + 1;
        }
        ridePages.clear();
        if (pages > 1) {
            int i = 1;
            int i2 = 1;
            for (Ride ride : rides) {
                if (i2 >= 22) {
                    i++;
                    i2 = 1;
                }
                if (i2 == 1) {
                    ridePages.put(i, new ArrayList<Ride>());
                    ridePages.get(i).add(ride);
                } else {
                    ridePages.get(i).add(ride);
                }
                i2++;
            }
        } else {
            ridePages.put(1, rides);
        }
    }

    private void setupAttractions() {
        File file = new File("plugins/MagicAssistant/menus.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        List<String> locations = config.getStringList("attraction-names");
        List<PlayerData.Attraction> attractions = new ArrayList<>();
        for (String location : locations) {
            String name = config
                    .getString("attraction." + location + ".name");
            String warp = config
                    .getString("attraction." + location + ".warp");
            int type = config.getInt("attraction." + location + ".type");
            byte data;
            if (config.contains("attraction." + location + ".data")) {
                data = (byte) config.getInt("attraction." + location
                        + ".data");
            } else {
                data = 0;
            }
            PlayerData.Attraction att = new PlayerData.Attraction(name, warp, type, data);
            attractions.add(att);
        }
        int pages;
        if (locations.isEmpty()) {
            pages = 1;
        } else {
            pages = ((int) Math.ceil(locations.size() / 21)) + 1;
        }
        attPages.clear();
        if (pages > 1) {
            int i = 1;
            int i2 = 1;
            for (PlayerData.Attraction att : attractions) {
                if (i2 >= 22) {
                    i++;
                    i2 = 1;
                }
                if (i2 == 1) {
                    attPages.put(i, new ArrayList<PlayerData.Attraction>());
                    attPages.get(i).add(att);
                } else {
                    attPages.get(i).add(att);
                }
                i2++;
            }
        } else {
            attPages.put(1, attractions);
        }
    }

    public static Ride getRide(String name) {
        for (Map.Entry<Integer, List<Ride>> rides : ridePages.entrySet()) {
            for (Ride ride : rides.getValue()) {
                if (ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', ride.getDisplayName())).equals(name)) {
                    return ride;
                }
            }
        }
        return null;
    }

    public static PlayerData.Attraction getAttraction(String name) {
        for (Map.Entry<Integer, List<PlayerData.Attraction>> attractions : attPages.entrySet()) {
            for (PlayerData.Attraction ride : attractions.getValue()) {
                if (ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', ride.getDisplayName())).equals(name)) {
                    return ride;
                }
            }
        }
        return null;
    }

    public static void removeWarp(Warp warp) {
        warps.remove(warp);
    }

    public static void addWarp(Warp warp) {
        warps.add(warp);
    }

    public void registerCommands() {
        Command_magic magic = new Command_magic();
        getCommand("autograph").setExecutor(new Command_autograph());
        getCommand("bc").setExecutor(new Command_bc());
        getCommand("day").setExecutor(new Command_day());
        getCommand("delay").setExecutor(new Command_delay());
        getCommand("delwarp").setExecutor(new Command_delwarp());
        getCommand("enderchest").setExecutor(new Command_enderchest());
        getCommand("fly").setExecutor(new Command_fly());
        getCommand("give").setExecutor(new Command_give());
        getCommand("gwts").setExecutor(new Command_gwts());
        getCommand("head").setExecutor(new Command_head());
        getCommand("heal").setExecutor(new Command_heal());
        getCommand("helpop").setExecutor(new Command_helpop());
        getCommand("helpop").setAliases(Arrays.asList("ac"));
        getCommand("hub").setExecutor(new Command_hub());
        getCommand("invsee").setExecutor(new Command_invsee());
        getCommand("item").setExecutor(new Command_item());
        getCommand("item").setAliases(Arrays.asList("i"));
        getCommand("magic").setExecutor(magic);
        getCommand("mb").setExecutor(new Command_mb());
        getCommand("more").setExecutor(new Command_more());
        getCommand("msg").setExecutor(new Command_msg());
        getCommand("msg").setAliases(Arrays.asList("tell", "t", "w", "whisper", "m"));
        getCommand("night").setExecutor(new Command_night());
        getCommand("noon").setExecutor(new Command_noon());
        getCommand("pack").setExecutor(new Command_pack());
        getCommand("ptime").setExecutor(new Command_ptime());
        getCommand("pweather").setExecutor(new Command_pweather());
        getCommand("serverparty").setExecutor(new Command_serverparty());
        getCommand("sethub").setExecutor(new Command_sethub());
        getCommand("setspawn").setExecutor(new Command_setspawn());
        getCommand("setwarp").setExecutor(new Command_setwarp());
        getCommand("smite").setExecutor(new Command_smite());
        getCommand("spawn").setExecutor(new Command_spawn());
        getCommand("top").setExecutor(new Command_top());
        getCommand("tp").setExecutor(new Command_tp());
        getCommand("uwarp").setExecutor(new Command_uwarp());
        getCommand("vanish").setExecutor(new Command_vanish());
        getCommand("vanish").setAliases(Arrays.asList("v"));
        getCommand("warp").setExecutor(new Command_warp());
        getCommand("wrl").setExecutor(new Command_wrl());
        Bukkit.getPluginManager().registerEvents(magic, this);
    }

    public void registerListeners() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(this, this);
        pm.registerEvents(new ChatListener(this), this);
        pm.registerEvents(new PlayerJoinAndLeave(), this);
        pm.registerEvents(new SignChange(this), this);
        pm.registerEvents(new BlockEdit(this), this);
        pm.registerEvents(new InventoryClick(this), this);
        pm.registerEvents(new PlayerDropItem(), this);
        pm.registerEvents(new PlayerInteract(this), this);
        pm.registerEvents(blockChanger, this);
        pm.registerEvents(packManager, this);
        pm.registerEvents(new VisibleUtil(this), this);
        pm.registerEvents(new HotelUtil(this), this);
        pm.registerEvents(new InventoryUtil(this), this);
        pm.registerEvents(new FountainUtil(this), this);
        pm.registerEvents(new PlayerCloseInventory(), this);
        if (getConfig().getBoolean("shooter-enabled")) {
            MessageTimer.start(this);
            pm.registerEvents(new Shooter(this), this);
        }
        System.out.println("Resource");
        for (RegisteredListener list : ResourceStatusEvent.getHandlerList().getRegisteredListeners()) {
            System.out.println(list.getListener().toString());
        }
        System.out.println("InventoryClick");
        for (RegisteredListener list : InventoryClickEvent.getHandlerList().getRegisteredListeners()) {
            System.out.println(list.getListener().toString());
        }
        System.out.println("InventoryClose");
        for (RegisteredListener list : InventoryCloseEvent.getHandlerList().getRegisteredListeners()) {
            System.out.println(list.getListener().toString());
        }
    }
}
