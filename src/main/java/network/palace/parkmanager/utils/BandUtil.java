package network.palace.parkmanager.utils;

import network.palace.core.Core;
import network.palace.core.economy.CurrencyType;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.*;
import org.bson.Document;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Marc on 12/13/14
 */
public class BandUtil {
    private static ItemStack back = new ItemStack(Material.FIREWORK_CHARGE);
    private HashMap<UUID, DataResponse> dataResponses = new HashMap<>();

    public BandUtil() {
        initialize();
    }

    private void initialize() {
        FireworkEffectMeta bm = (FireworkEffectMeta) back.getItemMeta();
        bm.setDisplayName(ChatColor.GREEN + "Back");
        bm.setEffect(FireworkEffect.builder().withColor(Color.ORANGE).build());
        back.setItemMeta(bm);
        Bukkit.getScheduler().runTaskTimer(ParkManager.getInstance(), () -> {
            for (Map.Entry<UUID, DataResponse> entry : new HashSet<>(dataResponses.entrySet())) {
                DataResponse response = dataResponses.remove(entry.getKey());
                CPlayer user = Core.getPlayerManager().getPlayer(entry.getKey());
                if (user == null) {
                    continue;
                }
                Player player = Bukkit.getPlayer(user.getUniqueId());
                if (player == null) {
                    continue;
                }
                Rank rank = user.getRank();
                Inventory inv = player.getOpenInventory().getTopInventory();
                if (inv == null) {
                    continue;
                }
                ItemStack pinfo = inv.getItem(4);
                if (pinfo == null) {
                    continue;
                }
                ItemMeta meta = pinfo.getItemMeta();
                FastPassData data = ParkManager.getInstance().getPlayerData(player.getUniqueId()).getFastPassData();
                meta.setLore(Arrays.asList(ChatColor.GREEN + "Name: " + ChatColor.YELLOW + user.getName(),
                        ChatColor.GREEN + "Rank: " + rank.getFormattedName(),
                        ChatColor.GREEN + "Balance: " + ChatColor.YELLOW + "$" + response.getBalance(),
                        ChatColor.GREEN + "Tokens: " + ChatColor.YELLOW + "✪ " + response.getTokens(),
                        ChatColor.GREEN + "Slow FPs: " + ChatColor.YELLOW + data.getSlow(),
                        ChatColor.GREEN + "Moderate FPs: " + ChatColor.YELLOW + data.getModerate(),
                        ChatColor.GREEN + "Thrill FPs: " + ChatColor.YELLOW + data.getThrill()));
                pinfo.setItemMeta(meta);
                inv.setItem(4, pinfo);
            }
        }, 0L, 10L);
    }

    public boolean isLoading(Player player) {
        return dataResponses.containsKey(player.getUniqueId());
    }

    public PlayerData setupPlayerData(UUID uuid) {
        try {
            ParkManager parkManager = ParkManager.getInstance();

            ChatColor bandNameColor = getBandNameColor(Core.getMongoHandler().getMagicBandNameColor(uuid));
            BandColor bandColor = !parkManager.isResort(Resort.USO) ? getBandColor(Core.getMongoHandler().getMagicBandType(uuid)) : BandColor.USO;
            boolean special = bandColor.getName().startsWith("s") || parkManager.isResort(Resort.USO);

            boolean flash = (Boolean) Core.getMongoHandler().getParkSetting(uuid, "flash");
            boolean visibility = (Boolean) Core.getMongoHandler().getParkSetting(uuid, "visibility");
            boolean hotel = (Boolean) Core.getMongoHandler().getParkSetting(uuid, "hotel");

            Document fpDoc = Core.getMongoHandler().getParkData(uuid, "fastpass");
            Document monthly = Core.getMongoHandler().getMonthlyRewards(uuid);
            Document vote = Core.getMongoHandler().getVoteData(uuid);

            FastPassData fpData = new FastPassData(fpDoc.getInteger("slow"), fpDoc.getInteger("moderate"),
                    fpDoc.getInteger("thrill"), fpDoc.getInteger("slowday"), fpDoc.getInteger("moderateday"),
                    fpDoc.getInteger("thrillday"));
            long settler = 0;
            long dweller = 0;
            long noble = 0;
            long majestic = 0;
            long honorable = 0;
            if (monthly.containsKey("settler")) {
                settler = monthly.getLong("settler");
            }
            if (monthly.containsKey("dweller")) {
                dweller = monthly.getLong("dweller");
            }
            if (monthly.containsKey("noble")) {
                noble = monthly.getLong("noble");
            }
            if (monthly.containsKey("majestic")) {
                majestic = monthly.getLong("majestic");
            }
            if (monthly.containsKey("honorable")) {
                honorable = monthly.getLong("honorable");
            }
            KioskData kioskData = new KioskData(settler, dweller, noble, majestic, honorable,
                    vote.getLong("lastTime"), vote.getInteger("lastSite"));

            String outfit = Core.getMongoHandler().getParkValue(uuid, "outfit");
            String pack = (String) Core.getMongoHandler().getParkSetting(uuid, "pack");

            PlayerData data = new PlayerData(uuid, bandNameColor, bandColor, special, flash, visibility, hotel, fpData, kioskData, outfit, pack);

            List<UUID> friends = Core.getMongoHandler().getFriendList(uuid);
            data.setFriends(friends);

            List<Integer> purchases = new ArrayList<>();
            for (Object o : Core.getMongoHandler().getOutfitPurchases(uuid)) {
                Document doc = (Document) o;
                int id = doc.getInteger("id");
                purchases.add(id);
            }
            data.setPurchases(purchases);

            TreeMap<String, RideCount> rides = new TreeMap<>();
            for (Object o : Core.getMongoHandler().getRideCounterData(uuid)) {
                Document doc = (Document) o;
                String name = doc.getString("name");
                String server = doc.getString("server");
                if (rides.containsKey(name) && rides.get(name).getServer().equalsIgnoreCase(server)) {
                    rides.get(name).addCount(1);
                } else {
                    rides.put(name, new RideCount(name, server));
                }
            }
            data.setRideCounts(rides);

            parkManager.addPlayerData(data);
            dataResponses.remove(uuid);

            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setSetting(final CPlayer player, final String name, final boolean value) {
        Core.getMongoHandler().setParkSetting(player.getUniqueId(), name, value);
    }

    public void setBandColor(final CPlayer player, final BandColor color) {
        ParkManager parkManager = ParkManager.getInstance();
        PlayerData data = parkManager.getPlayerData(player.getUniqueId());
        player.sendMessage(ChatColor.GREEN + "You have changed the color of your " + data.getBandName() +
                (parkManager.isResort(Resort.WDW) || parkManager.isResort(Resort.DLR) ? "MagicBand!" : "Power Pass!"));

        Core.runTaskAsynchronously(() -> {
            Core.getMongoHandler().setMagicBandData(player.getUniqueId(), "bandtype", color.getName());
            data.setBandColor(color);
            data.setSpecial(color.getName().startsWith("s"));
            giveBandToPlayer(player);
        });
    }

    public void setBandColor(final CPlayer player, Material color) {
        final String name = getBandName(color);
        BandColor bandColor = getBandColor(name);
        setBandColor(player, bandColor);
    }

    public void giveBandToPlayer(CPlayer player) {
        ParkManager parkManager = ParkManager.getInstance();
        PlayerData data = parkManager.getPlayerData(player.getUniqueId());
        ItemStack mb;
        if (data.isSpecial()) {
            mb = new ItemStack(getBandMaterial(data.getBandColor()));
            ItemMeta mbm = mb.getItemMeta();
            mbm.setDisplayName(data.getBandName() + (parkManager.isResort(Resort.WDW) || parkManager.isResort(Resort.DLR) ?
                    "MagicBand " : "Power Pass ") + ChatColor.GRAY + "(Right-Click)");
            mbm.setLore(Arrays.asList(ChatColor.GREEN + "Click me to open",
                    ChatColor.GREEN + "the park menu!"));
            mb.setItemMeta(mbm);
        } else {
            mb = new ItemStack(Material.FIREWORK_CHARGE);
            FireworkEffectMeta mbm = (FireworkEffectMeta) mb.getItemMeta();
            mbm.setEffect(FireworkEffect.builder().withColor(getBandColor(data.getBandColor())).build());
            mbm.setDisplayName(data.getBandName() + (parkManager.isResort(Resort.WDW) || parkManager.isResort(Resort.DLR) ?
                    "MagicBand " : "Power Pass ") + ChatColor.GRAY + "(Right-Click)");
            mbm.setLore(Arrays.asList(ChatColor.GREEN + "Click me to open",
                    ChatColor.GREEN + "the park menu!"));
            mb.setItemMeta(mbm);
        }
        player.getInventory().setItem(8, mb);
    }

    public void loadPlayerData(Player player) {
        if (dataResponses.containsKey(player.getUniqueId())) {
            return;
        }
        final UUID uuid = player.getUniqueId();
        Bukkit.getScheduler().runTaskAsynchronously(ParkManager.getInstance(), () -> {
            DataResponse response = new DataResponse(uuid, Core.getMongoHandler().getCurrency(uuid, CurrencyType.BALANCE),
                    Core.getMongoHandler().getCurrency(uuid, CurrencyType.TOKENS));
            dataResponses.put(uuid, response);
        });
    }

    public void cancelLoadPlayerData(UUID uuid) {
        dataResponses.remove(uuid);
    }

    public String getBandName(Material color) {
        switch (color) {
            case PAPER:
                return ParkManager.getInstance().isResort(Resort.USO) ? "uso" : "s1";
            case IRON_BARDING:
                return "s2";
            case GOLD_BARDING:
                return "s3";
            case DIAMOND_BARDING:
                return "s4";
            case GHAST_TEAR:
                return "s5";
            default:
                return "blue";
        }
    }

    public void setBandName(final CPlayer player, final ChatColor color) {
        ParkManager parkManager = ParkManager.getInstance();
        PlayerData data = parkManager.getPlayerData(player.getUniqueId());
        data.setBandName(color);
        player.sendMessage(ChatColor.GREEN + "You have changed the name color of your " + data.getBandName() +
                (parkManager.isResort(Resort.WDW) || parkManager.isResort(Resort.DLR) ? "MagicBand!" : "Power Pass!"));

        Core.runTaskAsynchronously(() -> {
            Core.getMongoHandler().setMagicBandData(player.getUniqueId(), "namecolor", getBandNameColor(color));
            giveBandToPlayer(player);
        });
    }

    public static ItemStack getBackItem() {
        return back;
    }

    public BandColor getBandColor(String string) {
        switch (string) {
            case "red":
                return BandColor.RED;
            case "orange":
                return BandColor.ORANGE;
            case "yellow":
                return BandColor.YELLOW;
            case "green":
                return BandColor.GREEN;
            case "blue":
                return BandColor.BLUE;
            case "purple":
                return BandColor.PURPLE;
            case "pink":
                return BandColor.PINK;
            case "s1":
                return BandColor.SPECIAL1;
            case "s2":
                return BandColor.SPECIAL2;
            case "s3":
                return BandColor.SPECIAL3;
            case "s4":
                return BandColor.SPECIAL4;
            case "s5":
                return BandColor.SPECIAL5;
            default:
                return BandColor.BLUE;
        }
    }

    public Material getBandMaterial(BandColor color) {
        switch (color) {
            case SPECIAL1:
                return Material.PAPER;
            case SPECIAL2:
                return Material.IRON_BARDING;
            case SPECIAL3:
                return Material.GOLD_BARDING;
            case SPECIAL4:
                return Material.DIAMOND_BARDING;
            case SPECIAL5:
                return Material.GHAST_TEAR;
            case USO:
                return Material.PAPER;
            default:
                return Material.FIREWORK_CHARGE;
        }
    }

    public String getBandNameColor(ChatColor color) {
        switch (color) {
            case RED:
                return "red";
            case GOLD:
                return "orange";
            case YELLOW:
                return "yellow";
            case GREEN:
                return "green";
            case DARK_GREEN:
                return "darkgreen";
            case BLUE:
                return "blue";
            case DARK_PURPLE:
                return "purple";
            default:
                return "orange";
        }
    }

    public ChatColor getBandNameColor(String string) {
        switch (string) {
            case "red":
                return ChatColor.RED;
            case "orange":
                return ChatColor.GOLD;
            case "yellow":
                return ChatColor.YELLOW;
            case "green":
                return ChatColor.GREEN;
            case "darkgreen":
                return ChatColor.DARK_GREEN;
            case "blue":
                return ChatColor.BLUE;
            case "purple":
                return ChatColor.DARK_PURPLE;
            default:
                return ChatColor.GOLD;
        }
    }

    public Color getBandColor(BandColor color) {
        switch (color) {
            case RED:
                return Color.fromRGB(255, 40, 40);
            case ORANGE:
                return Color.fromRGB(247, 140, 0);
            case YELLOW:
                return Color.fromRGB(239, 247, 0);
            case GREEN:
                return Color.fromRGB(0, 192, 13);
            case BLUE:
                return Color.fromRGB(41, 106, 255);
            case PURPLE:
                return Color.fromRGB(176, 0, 220);
            case PINK:
                return Color.fromRGB(246, 120, 255);
            default:
                return Color.fromRGB(0, 102, 255);
        }
    }

    public String currentTime() {
        Date current = new Date(System.currentTimeMillis());
        String h = new SimpleDateFormat("HH").format(current);
        String minute = new SimpleDateFormat("mm").format(current);
        String second = new SimpleDateFormat("ss").format(current);
        String hour;
        if (Integer.parseInt(h) > 12) {
            hour = (Integer.parseInt(h) - 12) + ":" + minute + ":" + second + " PM";
        } else if (Integer.parseInt(h) == 12) {
            hour = 12 + ":" + minute + ":" + second + " PM";
        } else if (Integer.parseInt(h) == 0) {
            hour = 12 + ":" + minute + ":" + second + " AM";
        } else {
            hour = h + ":" + minute + ":" + second + " AM";
        }
        return hour;
    }
}