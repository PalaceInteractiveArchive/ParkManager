package network.palace.parkmanager.fpkiosk;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.google.common.collect.ImmutableMap;
import network.palace.core.Core;
import network.palace.core.menu.Menu;
import network.palace.core.menu.MenuButton;
import network.palace.core.message.FormattedMessage;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.core.utils.ItemUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.MonthOfYear;
import network.palace.parkmanager.handlers.RewardData;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

public class FastPassKioskManager {
    private List<UUID> menu = new ArrayList<>();
    private final ZoneId timeZone = ZoneId.of("America/New_York");
    private final FormattedMessage websiteMessage = new FormattedMessage("\n")
            .then("Click here to visit our website!").color(ChatColor.YELLOW).style(ChatColor.BOLD)
            .link("https://forums.palace.network").tooltip(ChatColor.GREEN + "Click to visit " +
                    ChatColor.AQUA + "https://forums.palace.network")
            .then("\n");
    private final FormattedMessage voteMessage = new FormattedMessage("\n")
            .then("Click here to vote for us on Minecraft Server Lists!").color(ChatColor.YELLOW).style(ChatColor.BOLD)
            .link("https://vote.palace.network/?1").tooltip(ChatColor.GREEN + "Click to vote for us!").then("\n");

    public FastPassKioskManager() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(ParkManager.getInstance(), PacketType.Play.Client.USE_ENTITY) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                CPlayer player = Core.getPlayerManager().getPlayer(event.getPlayer());
                if (player == null || player.isInVehicle()) return;
                int id = event.getPacket().getIntegers().read(0);
                for (ArmorStand stand : player.getWorld().getEntitiesByClass(ArmorStand.class)) {
                    if (isKiosk(stand) && stand.getEntityId() == id) {
                        event.setCancelled(true);
                        Bukkit.getScheduler().runTask(ParkManager.getInstance(), () -> openMenu(player));
                    }
                }
            }
        });
        Core.runTaskTimer(ParkManager.getInstance(), () -> {
            for (UUID uuid : new ArrayList<>(menu)) {
                CPlayer tp = Core.getPlayerManager().getPlayer(uuid);
                if (tp == null) {
                    menu.remove(uuid);
                    return;
                }
                updateKioskMenu(tp);
            }
        }, 0L, 20L);
    }

    private void openMenu(CPlayer player) {
        player.sendMessage(ChatColor.BLUE + "Logging in to FastPass+ Kiosk...");
        Core.runTaskLater(ParkManager.getInstance(), () -> {
            menu.add(player.getUniqueId());
            player.getRegistry().addEntry("needsKioskInventory", true);
        }, 4L);
    }

    private void updateKioskMenu(CPlayer player) {
        Menu menu;
        if (player.getRegistry().hasEntry("kioskInventory")) {
            menu = (Menu) player.getRegistry().getEntry("kioskInventory");
        } else if (player.getRegistry().hasEntry("needsKioskInventory")) {
            player.getRegistry().removeEntry("needsKioskInventory");
            menu = new Menu(27, ChatColor.GREEN + "FastPass+ Kiosk", player, new ArrayList<>());
            player.getRegistry().addEntry("kioskInventory", menu);
            menu.setOnClose(() -> {
                FastPassKioskManager.this.menu.remove(player.getUniqueId());
                player.getRegistry().removeEntry("kioskInventory");
                player.sendMessage(ChatColor.BLUE + "Logged out of FastPass+ Kiosk");
            });
            menu.open();
        } else {
            return;
        }
        if (menu != null) setInventory(player, menu);
    }

    public void handleJoin(CPlayer player, Document fastpassDocument) {
        if (!fastpassDocument.containsKey("lastClaimed")) {
            fastpassDocument.put("lastClaimed", 0L);
        }
        if (!fastpassDocument.containsKey("count")) {
            fastpassDocument.put("count", 0);
        }
        player.getRegistry().addEntry("lastFastPassClaim", fastpassDocument.getLong("lastClaimed"));
        player.getRegistry().addEntry("fastPassCount", fastpassDocument.getInteger("count"));
        Core.runTaskAsynchronously(ParkManager.getInstance(), () -> {
            Document rewardDocument = Core.getMongoHandler().getMonthlyRewards(player.getUniqueId());
            player.getRegistry().addEntry("kioskRewardData", new RewardData(rewardDocument.getLong("settler"),
                    rewardDocument.getLong("dweller"),
                    rewardDocument.getLong("noble"),
                    rewardDocument.getLong("majestic"),
                    rewardDocument.getLong("honorable")));
        });
    }

    private void setInventory(CPlayer player, Menu menu) {
        if (!player.getRegistry().hasEntry("kioskRewardData")) {
            this.menu.remove(player.getUniqueId());
            player.closeInventory();
            return;
        }
        RewardData rewardData = (RewardData) player.getRegistry().getEntry("kioskRewardData");
        boolean fpClaimed = monthlyClaim((long) player.getRegistry().getEntry("lastFastPassClaim"));
        boolean settler = monthlyClaim(rewardData.getSettler());
        boolean dweller = monthlyClaim(rewardData.getDweller());
        boolean noble = monthlyClaim(rewardData.getNoble());
        boolean majestic = monthlyClaim(rewardData.getMajestic());
        boolean honorable = monthlyClaim(rewardData.getHonorable());

        menu.setButton(new MenuButton(4, ItemUtil.create(fpClaimed ? Material.IRON_INGOT : Material.BRICK,
                ChatColor.GREEN + "Monthly FastPass", getClaimLore(fpClaimed,
                        Arrays.asList(ChatColor.GRAY + "Use this to skip the line", ChatColor.GRAY + "for any attraction!"))),
                ImmutableMap.of(ClickType.LEFT, this::claimFastPass)));
        menu.setButton(new MenuButton(12, ItemUtil.create(Material.NETHER_STAR, ChatColor.GREEN + "Join the discussion!",
                Arrays.asList(ChatColor.GRAY + "Check out our Forums at ", ChatColor.AQUA + "forums.palace.network",
                        ChatColor.GRAY + "for news, posts and more!")), ImmutableMap.of(ClickType.LEFT, websiteMessage::send)));
        menu.setButton(new MenuButton(14, ItemUtil.create(Material.NETHER_STAR, ChatColor.GREEN + "Vote for Palace!", Arrays.asList(ChatColor.GRAY +
                        "Vote for us on Minecraft Server", ChatColor.GRAY + "Lists! Voting rewards will be",
                ChatColor.GRAY + "returning soon!")), ImmutableMap.of(ClickType.LEFT, voteMessage::send)));
        menu.setButton(new MenuButton(18, ItemUtil.create(settler ? Material.IRON_INGOT : Material.DIAMOND,
                ChatColor.DARK_AQUA + "Monthly Tokens - Settler", getClaimLore(settler,
                        Arrays.asList(ChatColor.GRAY + "Claim your monthly 20 Tokens!",
                                ChatColor.GRAY + "Everyone can claim this prize!"))),
                ImmutableMap.of(ClickType.LEFT, p -> claimTokens(p, Rank.SETTLER, rewardData))));
        menu.setButton(new MenuButton(20, ItemUtil.create(dweller ? Material.IRON_INGOT : Material.DIAMOND,
                ChatColor.AQUA + "Monthly Tokens - Dweller", getClaimLore(dweller,
                        Arrays.asList(ChatColor.GRAY + "Claim your monthly 20 Tokens!",
                                ChatColor.GRAY + "You must be " + Rank.DWELLER.getFormattedName() + ChatColor.GRAY + " or above"))),
                ImmutableMap.of(ClickType.LEFT, p -> claimTokens(p, Rank.DWELLER, rewardData))));
        menu.setButton(new MenuButton(22, ItemUtil.create(noble ? Material.IRON_INGOT : Material.DIAMOND,
                ChatColor.BLUE + "Monthly Tokens - Noble", getClaimLore(noble,
                        Arrays.asList(ChatColor.GRAY + "Claim your monthly 20 Tokens!",
                                ChatColor.GRAY + "You must be " + Rank.NOBLE.getFormattedName() + ChatColor.GRAY + " or above"))),
                ImmutableMap.of(ClickType.LEFT, p -> claimTokens(p, Rank.NOBLE, rewardData))));
        menu.setButton(new MenuButton(24, ItemUtil.create(majestic ? Material.IRON_INGOT : Material.DIAMOND,
                ChatColor.DARK_PURPLE + "Monthly Tokens - Majestic", getClaimLore(majestic,
                        Arrays.asList(ChatColor.GRAY + "Claim your monthly 20 Tokens!",
                                ChatColor.GRAY + "You must be " + Rank.MAJESTIC.getFormattedName() + ChatColor.GRAY + " or above"))),
                ImmutableMap.of(ClickType.LEFT, p -> claimTokens(p, Rank.MAJESTIC, rewardData))));
        menu.setButton(new MenuButton(26, ItemUtil.create(honorable ? Material.IRON_INGOT : Material.DIAMOND,
                ChatColor.LIGHT_PURPLE + "Monthly Tokens - Honorable", getClaimLore(honorable,
                        Arrays.asList(ChatColor.GRAY + "Claim your monthly 20 Tokens!",
                                ChatColor.GRAY + "You must be " + Rank.HONORABLE.getFormattedName() + ChatColor.GRAY + " or above"))),
                ImmutableMap.of(ClickType.LEFT, p -> claimTokens(p, Rank.HONORABLE, rewardData))));
    }

    private void claimTokens(CPlayer player, Rank rank, RewardData rewardData) {
        long lastClaim;
        switch (rank) {
            case SETTLER:
                lastClaim = rewardData.getSettler();
                break;
            case DWELLER:
                lastClaim = rewardData.getDweller();
                break;
            case NOBLE:
                lastClaim = rewardData.getNoble();
                break;
            case MAJESTIC:
                lastClaim = rewardData.getMajestic();
                break;
            case HONORABLE:
                lastClaim = rewardData.getHonorable();
                break;
            default:
                player.sendMessage(ChatColor.RED + "There was an error claiming that reward!");
                return;
        }
        if (monthlyClaim(lastClaim)) {
            player.sendMessage(ChatColor.RED + "You've already claimed that reward for this month!");
            return;
        }
        long nextClaim = System.currentTimeMillis();
        switch (rank) {
            case SETTLER:
                rewardData.setSettler(nextClaim);
                break;
            case DWELLER:
                rewardData.setDweller(nextClaim);
                break;
            case NOBLE:
                rewardData.setNoble(nextClaim);
                break;
            case MAJESTIC:
                rewardData.setMajestic(nextClaim);
                break;
            case HONORABLE:
                rewardData.setHonorable(nextClaim);
                break;
        }
        player.addTokens(20, "monthly " + rank.name().toLowerCase());
        Core.runTaskAsynchronously(ParkManager.getInstance(), () -> Core.getMongoHandler().updateMonthlyRewardData(player.getUniqueId(),
                rewardData.getSettler(), rewardData.getDweller(), rewardData.getNoble(), rewardData.getMajestic(), rewardData.getHonorable()));
        player.sendMessage(ChatColor.YELLOW + "You've claimed your 20 monthly " + rank.getFormattedName() +
                ChatColor.YELLOW + " tokens!");
    }

    private void claimFastPass(CPlayer player) {
        long lastClaim = (long) player.getRegistry().getEntry("lastFastPassClaim");
        if (monthlyClaim(lastClaim)) {
            player.sendMessage(ChatColor.RED + "You've already claimed that reward for this month!");
            return;
        }
        long nextClaim = System.currentTimeMillis();
        player.getRegistry().addEntry("lastFastPassClaim", nextClaim);
        player.getRegistry().addEntry("fastPassCount", (int) player.getRegistry().getEntry("fastPassCount") + 1);
        Core.runTaskAsynchronously(ParkManager.getInstance(), () -> {
            Core.getMongoHandler().updateFastPassData(player.getUniqueId(), nextClaim);
            Core.getMongoHandler().addFastPass(player.getUniqueId(), 1);
        });
        player.sendMessage(ChatColor.YELLOW + "You've claimed your monthly FastPass!");
    }

    private List<String> getClaimLore(boolean claimed, List<String> lore) {
        lore = new ArrayList<>(lore);
        if (claimed) {
            lore.add(ChatColor.YELLOW + "Claim in " + ChatColor.GRAY + timeToNextMonth());
        } else {
            lore.add(ChatColor.YELLOW + "Right-Click to Claim!");
        }
        return lore;
    }

    private boolean monthlyClaim(long lastFastPassClaim) {
        ZonedDateTime lastFPClaim = ZonedDateTime.ofInstant(Instant.ofEpochMilli(lastFastPassClaim), timeZone);
        ZonedDateTime now = ZonedDateTime.ofInstant(Instant.now(), timeZone);
        return YearMonth.from(lastFPClaim).equals(YearMonth.from(now));
    }

    private String timeToNextMonth() {
        Calendar cur = Calendar.getInstance();
        cur.setTimeInMillis(new Date().getTime());
        MonthOfYear month = MonthOfYear.getFromNumber(cur.get(Calendar.MONTH));
        int days = month.getDays();
        boolean leap = cur.get(Calendar.YEAR) % 4 == 0;
        if (month.equals(MonthOfYear.FEBRUARY) && leap) {
            days += 1;
        }
        int d = days - (cur.get(Calendar.DAY_OF_MONTH) + 1);
        int h = 24 - (cur.get(Calendar.HOUR_OF_DAY) + 1);
        int m = 60 - (cur.get(Calendar.MINUTE) + 1);
        int s = 60 - cur.get(Calendar.SECOND);
        return ChatColor.GRAY + "" + d + "d" + h + "h" + m + "m" + s + "s";
    }

    public void spawn(CPlayer player) {
        Location loc = player.getLocation();
        if (loc.getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.AIR)) {
            player.sendMessage(ChatColor.RED + "You must be standing on the ground when creating a FastPass+ Kiosk!");
            return;
        }

        //Snap the kiosk to face the nearest cardinal/intercardinal direction (N/E/S/W or NE/SE/NW/SW)
        float adjustedYaw = 45 * (Math.round(loc.getYaw() / 45));

        //Similarly, snap the kiosk to the nearest half-block coordinate for x and z (center of the block or on the line)
        double x = 0.5 * (Math.round(loc.getX() / 0.5));
        double y = loc.getBlockY();
        double z = 0.5 * (Math.round(loc.getZ() / 0.5));

        Location realLoc = new Location(loc.getWorld(), x, y, z, adjustedYaw, 0);

        ItemStack model = new ItemStack(Material.DIAMOND_SWORD, 1);
        ItemMeta meta = model.getItemMeta();
        ((Damageable) meta).setDamage(6);
        model.setItemMeta(meta);

        ArmorStand stand = lock(realLoc.getWorld().spawn(realLoc, ArmorStand.class));
        stand.setGravity(false);
        stand.setVisible(false);
        stand.setArms(false);
        stand.setBasePlate(false);
        stand.setHelmet(model);
        stand.addScoreboardTag("kiosk");

        player.sendMessage(ChatColor.GREEN + "Spawned in a new FastPass+ Kiosk!");
    }

    public boolean isKiosk(Entity e) {
        return e.getType().equals(EntityType.ARMOR_STAND) && e.getScoreboardTags().contains("kiosk");
    }

    public static ArmorStand lock(ArmorStand stand) {
        try {
            String lockField;
            switch (Core.getMinecraftVersion()) {
                case "v1_13_R1":
                case "v1_13_R2":
                    lockField = "bH";
                    break;
                case "v1_12_R1":
                    lockField = "bB";
                    break;
                default:
                    lockField = "bA";
                    break;
            }
            Field f = Class.forName("net.minecraft.server." + Core.getMinecraftVersion() + ".EntityArmorStand")
                    .getDeclaredField(lockField);
            if (f != null) {
                f.setAccessible(true);
                Object craftStand = Class.forName("org.bukkit.craftbukkit." + Core.getMinecraftVersion() +
                        ".entity.CraftArmorStand").cast(stand);
                Object handle = craftStand.getClass().getDeclaredMethod("getHandle").invoke(craftStand);
                f.set(handle, 2096896);
            }
        } catch (NoSuchFieldException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return stand;
    }
}
