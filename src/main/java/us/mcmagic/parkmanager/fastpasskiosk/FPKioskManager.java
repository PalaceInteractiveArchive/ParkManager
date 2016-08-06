package us.mcmagic.parkmanager.fastpasskiosk;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import net.minecraft.server.v1_8_R3.MojangsonParseException;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.chat.formattedmessage.FormattedMessage;
import us.mcmagic.mcmagiccore.itemcreator.ItemCreator;
import us.mcmagic.mcmagiccore.permissions.Rank;
import us.mcmagic.parkmanager.ParkManager;
import us.mcmagic.parkmanager.handlers.FastPassData;
import us.mcmagic.parkmanager.handlers.KioskData;
import us.mcmagic.parkmanager.handlers.MonthOfYear;
import us.mcmagic.parkmanager.handlers.PlayerData;
import us.mcmagic.parkmanager.utils.HeadUtil;
import us.mcmagic.parkmanager.utils.SqlUtil;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by Marc on 2/19/16
 */
public class FPKioskManager implements Listener {
    private List<UUID> firstOpenMenu = new ArrayList<>();
    private List<UUID> openMenu = new ArrayList<>();

    public FPKioskManager() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(ParkManager.getInstance(),
                PacketType.Play.Client.USE_ENTITY, PacketType.Play.Client.CLOSE_WINDOW) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                try {
                    PacketContainer container = event.getPacket();
                    UUID uuid = event.getPlayer().getUniqueId();
                    if (container.getType().equals(PacketType.Play.Client.USE_ENTITY)) {
                        StructureModifier<Integer> ints = container.getIntegers();
                        Field f = ints.getField(0);
                        Integer i = (Integer) f.get(ints.getTarget());
                        if (i != null) {
                            Entity e = null;
                            for (Entity en : Bukkit.getWorlds().get(0).getEntities()) {
                                if (en.getEntityId() == i) {
                                    e = en;
                                    break;
                                }
                            }
                            if (e != null) {
                                if (e.getType().equals(EntityType.ARMOR_STAND) && e.getCustomName() != null) {
                                    if (e.getCustomName().equals("Kiosk")) {
                                        event.setCancelled(true);
                                        if (!firstOpenMenu.contains(uuid)) {
                                            firstOpenMenu.add(uuid);
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        openMenu.remove(event.getPlayer().getUniqueId());
                    }
                } catch (Exception ignored) {
                }
            }
        });
        Bukkit.getScheduler().runTaskTimer(ParkManager.getInstance(), () -> {
            for (UUID uuid : new ArrayList<>(firstOpenMenu)) {
                Player tp = Bukkit.getPlayer(uuid);
                if (tp != null) {
                    firstOpenMenu.remove(uuid);
                    openKiosk(tp);
                    openMenu.add(uuid);
                }
            }
        }, 0L, 1L);
        Bukkit.getScheduler().runTaskTimer(ParkManager.getInstance(), () -> {
            for (UUID uuid : new ArrayList<>(openMenu)) {
                Player tp = Bukkit.getPlayer(uuid);
                if (tp != null) {
                    updateKioskMenu(tp);
                }
            }
        }, 0L, 20L);
    }

    public void create(Player player) throws MojangsonParseException {
        World world = player.getWorld();
        Location loc = player.getLocation();
        Location loc1 = new Location(world, loc.getX(), loc.getY() - 0.7, loc.getZ(), loc.getYaw(), 0);
        ArmorStand a1 = world.spawn(loc1, ArmorStand.class);
        a1.setVisible(false);
        a1.setArms(false);
        a1.setSmall(true);
        a1.setBasePlate(false);
        a1.setGravity(false);
        a1.setHelmet(new ItemStack(Material.WOOL, 1, (byte) 15));
        a1.setCustomName("Kiosk");
        a1.setCustomNameVisible(false);
        Location loc2 = new Location(world, loc.getX(), loc.getY() - 0.3, loc.getZ(), loc.getYaw(), 0);
        ArmorStand a2 = world.spawn(loc2, ArmorStand.class);
        a2.setVisible(false);
        a2.setArms(false);
        a2.setSmall(true);
        a2.setBasePlate(false);
        a2.setGravity(false);
        a2.setHelmet(new ItemStack(Material.WOOL, 1, (byte) 15));
        a2.setCustomName("Kiosk");
        a2.setCustomNameVisible(false);
        Location loc3 = new Location(world, loc.getX(), loc.getY() - 0.6, loc.getZ(), loc.getYaw(), 0);
        ArmorStand a3 = world.spawn(loc3, ArmorStand.class);
        a3.setVisible(false);
        a3.setArms(false);
        a3.setBasePlate(false);
        a3.setGravity(false);
        a3.setHelmet(HeadUtil.getPlayerHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQ" +
                "ubmV0L3RleHR1cmUvMTVjMjkyYTI0ZjU0YTdhNDM3ODUyNjY1NTJkYmE3YTE4NGY5YzUwZTBkOTRiMzM3ZDhkM2U3NmU5ZTljY2" +
                "U3In19fQ==", "FastPass Kiosk"));
        a3.setCustomName("Kiosk");
        a3.setCustomNameVisible(false);
        world.save();
        player.sendMessage(ChatColor.GREEN + "A FastPass Kiosk has been created!");
    }

    private void updateKioskMenu(Player player) {
        if (player.getOpenInventory() == null) {
            openMenu.remove(player.getUniqueId());
            return;
        }
        Inventory inv = player.getOpenInventory().getTopInventory();
        if (inv == null) {
            openMenu.remove(player.getUniqueId());
            return;
        }
        if (!inv.getName().toLowerCase().contains("fastpass kiosk")) {
            openMenu.remove(player.getUniqueId());
            return;
        }
        PlayerData data = ParkManager.getPlayerData(player.getUniqueId());
        FastPassData fpdata = data.getFastPassData();
        KioskData kioskData = data.getKioskData();
        long vote = kioskData.getVote();
        int lastVote = kioskData.getLastVote();
        int today = new GregorianCalendar().get(Calendar.DAY_OF_YEAR);
        Calendar cur = Calendar.getInstance();
        Calendar lastG = Calendar.getInstance();
        Calendar lastD = Calendar.getInstance();
        Calendar lastS = Calendar.getInstance();
        lastG.setTime(new Date(kioskData.getMonthGuest()));
        lastD.setTime(new Date(kioskData.getMonthDVC()));
        lastS.setTime(new Date(kioskData.getMonthShare()));
        boolean sameYearG = lastG.get(Calendar.YEAR) == cur.get(Calendar.YEAR);
        boolean sameYearD = lastD.get(Calendar.YEAR) == cur.get(Calendar.YEAR);
        boolean sameYearS = lastS.get(Calendar.YEAR) == cur.get(Calendar.YEAR);
        ItemStack fpslow = new ItemCreator(fpItem(fpdata.getSlow(), fpdata.getSlowDay()), ChatColor.GREEN +
                "FastPass - Slow", Arrays.asList(ChatColor.GRAY + "Use this to skip the", ChatColor.GRAY +
                "line of a " + ChatColor.YELLOW + "Slow " + ChatColor.GRAY + "ride!", fpLore(fpdata.getSlow(),
                fpdata.getSlowDay(), "Slow")));
        ItemStack fpmod = new ItemCreator(fpItem(fpdata.getModerate(), fpdata.getModerateDay()), ChatColor.GREEN +
                "FastPass - Moderate", Arrays.asList(ChatColor.GRAY + "Use this to skip the", ChatColor.GRAY +
                "line of a " + ChatColor.YELLOW + "Moderate " + ChatColor.GRAY + "ride!", fpLore(fpdata.getModerate(),
                fpdata.getModerateDay(), "Moderate")));
        ItemStack fpthr = new ItemCreator(fpItem(fpdata.getThrill(), fpdata.getThrillDay()), ChatColor.GREEN +
                "FastPass - Thrill", Arrays.asList(ChatColor.GRAY + "Use this to skip the", ChatColor.GRAY +
                "line of a " + ChatColor.YELLOW + "Thrill " + ChatColor.GRAY + "ride!", fpLore(fpdata.getThrill(),
                fpdata.getThrillDay(), "Thrill")));
        ItemStack voteItem = new ItemCreator((System.currentTimeMillis() - vote > 43200000) ? Material.NETHER_STAR :
                Material.IRON_INGOT, ChatColor.GREEN + "Vote for MCMagic!", Arrays.asList(ChatColor.GRAY +
                "Vote for us on Minecraft Server", ChatColor.GRAY + "Lists daily and receive " + ChatColor.YELLOW +
                "5 Tokens!", voteLore(vote)));
        ItemStack monthGuest = new ItemCreator(monthItem(kioskData.getMonthGuest()), ChatColor.GREEN +
                "Monthly Tokens - Guest", Arrays.asList(ChatColor.GRAY + "Claim your monthly 50 Tokens!",
                ChatColor.GRAY + "Everyone can claim this prize!", monthLore(kioskData.getMonthGuest())));
        ItemStack monthDVC = new ItemCreator(monthItem(kioskData.getMonthDVC()), ChatColor.GREEN +
                "Monthly Tokens - DVC", Arrays.asList(ChatColor.GRAY + "Claim your monthly 50 Tokens!",
                ChatColor.GRAY + "You must be " + Rank.DVCMEMBER.getNameWithBrackets() + ChatColor.GRAY + " or above",
                monthLore(kioskData.getMonthDVC())));
        ItemStack monthShare = new ItemCreator(monthItem(kioskData.getMonthShare()), ChatColor.GREEN +
                "Monthly Tokens - Shareholder", Arrays.asList(ChatColor.GRAY + "Claim your monthly 50 Tokens!",
                ChatColor.GRAY + "You must be " + Rank.SHAREHOLDER.getNameWithBrackets() + ChatColor.GRAY + " or above",
                monthLore(kioskData.getMonthShare())));
        inv.setItem(10, fpslow);
        inv.setItem(11, fpmod);
        inv.setItem(12, fpthr);
        inv.setItem(13, voteItem);
        inv.setItem(14, monthGuest);
        inv.setItem(15, monthDVC);
        inv.setItem(16, monthShare);
    }

    public void openKiosk(Player player) {
        PlayerData data = ParkManager.getPlayerData(player.getUniqueId());
        FastPassData fpdata = data.getFastPassData();
        KioskData kioskData = data.getKioskData();
        long vote = kioskData.getVote();
        int lastVote = kioskData.getLastVote();
        Inventory inv = Bukkit.createInventory(player, 27, ChatColor.BLUE + "FastPass Kiosk");
        ItemStack fpslow = new ItemCreator(fpItem(fpdata.getSlow(), fpdata.getSlowDay()), ChatColor.GREEN +
                "FastPass - Slow", Arrays.asList(ChatColor.GRAY + "Use this to skip the", ChatColor.GRAY +
                "line of a " + ChatColor.YELLOW + "Slow " + ChatColor.GRAY + "ride!", fpLore(fpdata.getSlow(),
                fpdata.getSlowDay(), "Slow")));
        ItemStack fpmod = new ItemCreator(fpItem(fpdata.getModerate(), fpdata.getModerateDay()), ChatColor.GREEN +
                "FastPass - Moderate", Arrays.asList(ChatColor.GRAY + "Use this to skip the", ChatColor.GRAY +
                "line of a " + ChatColor.YELLOW + "Moderate " + ChatColor.GRAY + "ride!", fpLore(fpdata.getModerate(),
                fpdata.getModerateDay(), "Moderate")));
        ItemStack fpthr = new ItemCreator(fpItem(fpdata.getThrill(), fpdata.getThrillDay()), ChatColor.GREEN +
                "FastPass - Thrill", Arrays.asList(ChatColor.GRAY + "Use this to skip the", ChatColor.GRAY +
                "line of a " + ChatColor.YELLOW + "Thrill " + ChatColor.GRAY + "ride!", fpLore(fpdata.getThrill(),
                fpdata.getThrillDay(), "Thrill")));
        ItemStack voteItem = new ItemCreator((System.currentTimeMillis() - vote > 43200000) ? Material.NETHER_STAR :
                Material.IRON_INGOT, ChatColor.GREEN + "Vote for MCMagic!", Arrays.asList(ChatColor.GRAY +
                "Vote for us on Minecraft Server", ChatColor.GRAY + "Lists daily and receive " + ChatColor.YELLOW +
                "5 Tokens!", voteLore(vote)));
        ItemStack monthGuest = new ItemCreator(monthItem(kioskData.getMonthGuest()), ChatColor.GREEN +
                "Monthly Tokens - Guest", Arrays.asList(ChatColor.GRAY + "Claim your monthly 50 Tokens!",
                ChatColor.GRAY + "Everyone can claim this prize!", monthLore(kioskData.getMonthGuest())));
        ItemStack monthDVC = new ItemCreator(monthItem(kioskData.getMonthDVC()), ChatColor.GREEN +
                "Monthly Tokens - DVC", Arrays.asList(ChatColor.GRAY + "Claim your monthly 50 Tokens!",
                ChatColor.GRAY + "You must be " + Rank.DVCMEMBER.getNameWithBrackets() + ChatColor.GRAY + " or above",
                monthLore(kioskData.getMonthDVC())));
        ItemStack monthShare = new ItemCreator(monthItem(kioskData.getMonthShare()), ChatColor.GREEN +
                "Monthly Tokens - Shareholder", Arrays.asList(ChatColor.GRAY + "Claim your monthly 50 Tokens!",
                ChatColor.GRAY + "You must be " + Rank.SHAREHOLDER.getNameWithBrackets() + ChatColor.GRAY + " or above",
                monthLore(kioskData.getMonthShare())));
        inv.setItem(10, fpslow);
        inv.setItem(11, fpmod);
        inv.setItem(12, fpthr);
        inv.setItem(13, voteItem);
        inv.setItem(14, monthGuest);
        inv.setItem(15, monthDVC);
        inv.setItem(16, monthShare);
        player.openInventory(inv);
    }

    private String voteLore(long vote) {
        if (System.currentTimeMillis() - vote > 43200000) {
            return ChatColor.YELLOW + "Right-Click to Vote!";
        }
        return ChatColor.GOLD + "You already voted in the past 12 hours!";
    }

    private String monthLore(long last) {
        Calendar cur = Calendar.getInstance();
        Calendar lastC = Calendar.getInstance();
        lastC.setTime(new Date(last));
        boolean sameYear = lastC.get(Calendar.YEAR) == cur.get(Calendar.YEAR);
        if (!sameYear || (sameYear && lastC.get(Calendar.MONTH) != cur.get(Calendar.MONTH))) {
            return ChatColor.YELLOW + "Right-Click to Claim!";
        }
        return ChatColor.GOLD + "Claim again in " + timeToNextMonth();
    }

    private Material fpItem(int fpCount, int fpDay) {
        int day = getDayOfYear();
        if (day == fpDay) {
            return Material.IRON_INGOT;
        }
        return Material.CLAY_BRICK;
    }

    private Material monthItem(long last) {
        Calendar cur = Calendar.getInstance();
        Calendar lastC = Calendar.getInstance();
        lastC.setTime(new Date(last));
        boolean sameYear = lastC.get(Calendar.YEAR) == cur.get(Calendar.YEAR);
        if (!sameYear || (sameYear && lastC.get(Calendar.MONTH) != cur.get(Calendar.MONTH))) {
            return Material.DIAMOND;
        }
        return Material.IRON_INGOT;
    }

    private int getDayOfYear() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(new Date().getTime());
        return cal.get(Calendar.DAY_OF_YEAR);

    }

    private String fpLore(int fpCount, int day, String type) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(new Date().getTime());
        int curday = c.get(Calendar.DAY_OF_YEAR);
        if (curday != day) {
            if (fpCount < 1) {
                return ChatColor.YELLOW + "Right-Click to Claim!";
            }
            if (fpCount > 0) {
                return ChatColor.GOLD + "You already have a " + ChatColor.YELLOW + type + ChatColor.GOLD + " FastPass!";
            }
        }
        return ChatColor.GOLD + "Claim again in " + timeToTomorrow();
    }

    private String timeToNextMonth() {
        Calendar cur = Calendar.getInstance();
        cur.setTimeInMillis(new Date().getTime());
        int lastDayOfMonth = 0;
        MonthOfYear month = MonthOfYear.getFromNumber(cur.get(Calendar.MONTH));
        int days = month.getDays();
        if (month.equals(MonthOfYear.FEBRUARY) && cur.get(Calendar.YEAR) % 4 == 0) {
            days += 1;
        }
        int d = days - (cur.get(Calendar.DAY_OF_MONTH) + 1);
        int h = 24 - (cur.get(Calendar.HOUR_OF_DAY) + 1);
        int m = 60 - (cur.get(Calendar.MINUTE) + 1);
        int s = 60 - cur.get(Calendar.SECOND);
        return ChatColor.GRAY + "" + d + "d" + h + "h" + m + "m" + s + "s";
    }

    private String timeToTomorrow() {
        Calendar cur = Calendar.getInstance();
        cur.setTimeInMillis(new Date().getTime());
        int h = 24 - (cur.get(Calendar.HOUR_OF_DAY) + 1);
        int m = 60 - (cur.get(Calendar.MINUTE) + 1);
        int s = 60 - cur.get(Calendar.SECOND);
        return ChatColor.GRAY + "" + h + "h" + m + "m" + s + "s";
    }

    public void handle(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        if (item.getItemMeta() == null) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta.getDisplayName() == null) {
            return;
        }
        String name = ChatColor.stripColor(meta.getDisplayName());
        int slot = event.getSlot();
        PlayerData data = ParkManager.getPlayerData(player.getUniqueId());
        FastPassData fpd = data.getFastPassData();
        KioskData kioskData = data.getKioskData();
        int today = new GregorianCalendar().get(Calendar.DAY_OF_YEAR);
        Rank r = MCMagicCore.getUser(player.getUniqueId()).getRank();
        switch (slot) {
            case 10: {
                if (fpd.getSlow() < 1 && fpd.getSlowDay() != today) {
                    fpd.setSlow(1);
                    fpd.setSlowDay(today);
                    player.sendMessage(ChatColor.GREEN + "You claimed your " + ChatColor.YELLOW + "Daily Slow FastPass!");
                    player.playSound(player.getLocation(), Sound.LEVEL_UP, 1f, 1f);
                    updateFPData(player.getUniqueId(), fpd);
                }
                break;
            }
            case 11: {
                if (fpd.getModerate() < 1 && fpd.getModerateDay() != today) {
                    fpd.setModerate(1);
                    fpd.setModerateDay(today);
                    player.sendMessage(ChatColor.GREEN + "You claimed your " + ChatColor.YELLOW + "Daily Moderate FastPass!");
                    player.playSound(player.getLocation(), Sound.LEVEL_UP, 1f, 1f);
                    updateFPData(player.getUniqueId(), fpd);
                }
                break;
            }
            case 12: {
                if (fpd.getThrill() < 1 && fpd.getThrillDay() != today) {
                    fpd.setThrill(1);
                    fpd.setThrillDay(today);
                    player.sendMessage(ChatColor.GREEN + "You claimed your " + ChatColor.YELLOW + "Daily Thrill FastPass!");
                    player.playSound(player.getLocation(), Sound.LEVEL_UP, 1f, 1f);
                    updateFPData(player.getUniqueId(), fpd);
                }
                break;
            }
            case 13: {
                if (System.currentTimeMillis() - kioskData.getVote() <= 43200000) {
                    player.sendMessage(ChatColor.RED + "You already voted in the past 12 hours!");
                    break;
                }
                int voteSite = 1;
                if (kioskData.getLastVote() + 1 <= 4) {
                    voteSite = kioskData.getLastVote() + 1;
                }
                String link = "http://vote.mcmagic.us/?" + voteSite;
                FormattedMessage msg = new FormattedMessage("To vote for us, ").color(ChatColor.GREEN)
                        .then("click here!").color(ChatColor.AQUA).style(ChatColor.BOLD, ChatColor.UNDERLINE)
                        .link(link).tooltip(ChatColor.GREEN + "Click to visit " + link).then(" You will receive ")
                        .color(ChatColor.GREEN).then("5 Tokens").color(ChatColor.GOLD).style(ChatColor.BOLD)
                        .then(" after you vote.").color(ChatColor.GREEN);
                msg.send(player);
                player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 1f, 1f);
                player.closeInventory();
                break;
            }
            case 14: {
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date(kioskData.getMonthGuest()));
                if (cal.get(Calendar.MONTH) != Calendar.getInstance().get(Calendar.MONTH)) {
                    kioskData.setMonthGuest(System.currentTimeMillis());
                    player.sendMessage(ChatColor.GREEN + "You claimed your " + Rank.GUEST.getNameWithBrackets() +
                            ChatColor.YELLOW + " Monthly Tokens!");
                    player.playSound(player.getLocation(), Sound.LEVEL_UP, 1f, 1f);
                    MCMagicCore.economy.addTokens(player.getUniqueId(), 50, "Monthly Guest");
                    updateMonthlyData(player.getUniqueId(), kioskData);
                }
                break;
            }
            case 15: {
                if (r.getRankId() < Rank.DVCMEMBER.getRankId()) {
                    player.sendMessage(ChatColor.GREEN + "Donate to us for " + Rank.DVCMEMBER.getNameWithBrackets() +
                            ChatColor.GREEN + " at " + ChatColor.AQUA + "http://store.mcmagic.us " + ChatColor.GREEN +
                            "to claim this!");
                    return;
                }
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date(kioskData.getMonthDVC()));
                if (cal.get(Calendar.MONTH) != Calendar.getInstance().get(Calendar.MONTH)) {
                    kioskData.setMonthDVC(System.currentTimeMillis());
                    player.sendMessage(ChatColor.GREEN + "You claimed your " + Rank.DVCMEMBER.getNameWithBrackets() +
                            ChatColor.YELLOW + " Monthly Tokens!");
                    player.playSound(player.getLocation(), Sound.LEVEL_UP, 1f, 1f);
                    MCMagicCore.economy.addTokens(player.getUniqueId(), 50, "Monthly DVC");
                    updateMonthlyData(player.getUniqueId(), kioskData);
                }
                break;
            }
            case 16: {
                if (r.getRankId() < Rank.SHAREHOLDER.getRankId()) {
                    player.sendMessage(ChatColor.GREEN + "Donate to us for " + Rank.SHAREHOLDER.getNameWithBrackets() +
                            ChatColor.GREEN + " at " + ChatColor.AQUA + "http://store.mcmagic.us " + ChatColor.GREEN +
                            "to claim this!");
                    return;
                }
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date(kioskData.getMonthShare()));
                if (cal.get(Calendar.MONTH) != Calendar.getInstance().get(Calendar.MONTH)) {
                    kioskData.setMonthShare(System.currentTimeMillis());
                    player.sendMessage(ChatColor.GREEN + "You claimed your " + Rank.SHAREHOLDER.getNameWithBrackets() +
                            ChatColor.YELLOW + " Monthly Tokens!");
                    player.playSound(player.getLocation(), Sound.LEVEL_UP, 1f, 1f);
                    MCMagicCore.economy.addTokens(player.getUniqueId(), 50, "Monthly Shareholder");
                    updateMonthlyData(player.getUniqueId(), kioskData);
                }
                break;
            }
        }
    }

    private void updateMonthlyData(final UUID uuid, final KioskData data) {
        Bukkit.getScheduler().runTaskAsynchronously(ParkManager.getInstance(), () -> {
            try (Connection connection = SqlUtil.getConnection()) {
                PreparedStatement sql = connection.prepareStatement("UPDATE player_data SET monthguest=?," +
                        "monthdvc=?,monthshare=? WHERE uuid=?");
                sql.setLong(1, data.getMonthGuest());
                sql.setLong(2, data.getMonthDVC());
                sql.setLong(3, data.getMonthShare());
                sql.setString(4, uuid.toString());
                sql.execute();
                sql.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void updateKioskData(final UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            return;
        }
        final PlayerData data = ParkManager.getPlayerData(uuid);
        Bukkit.getScheduler().runTaskAsynchronously(ParkManager.getInstance(), () -> {
            try (Connection connection = SqlUtil.getConnection()) {
                PreparedStatement sql = connection.prepareStatement("SELECT vote,lastvote from player_data WHERE uuid=?");
                sql.setString(1, uuid.toString());
                ResultSet result = sql.executeQuery();
                if (!result.next()) {
                    return;
                }
                KioskData kioskData = data.getKioskData();
                kioskData.setVote(result.getLong("vote"));
                kioskData.setLastVote(result.getInt("lastvote"));
                result.close();
                sql.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void updateFPData(final UUID uuid, final FastPassData fastPassData) {
        Bukkit.getScheduler().runTaskAsynchronously(ParkManager.getInstance(), () -> {
            try (Connection connection = SqlUtil.getConnection()) {
                PreparedStatement sql = connection.prepareStatement("UPDATE player_data SET slow=?,moderate=?," +
                        "thrill=?,sday=?,mday=?,tday=? WHERE uuid=?");
                sql.setInt(1, fastPassData.getSlow());
                sql.setInt(2, fastPassData.getModerate());
                sql.setInt(3, fastPassData.getThrill());
                sql.setInt(4, fastPassData.getSlowDay());
                sql.setInt(5, fastPassData.getModerateDay());
                sql.setInt(6, fastPassData.getThrillDay());
                sql.setString(7, uuid.toString());
                sql.execute();
                sql.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}