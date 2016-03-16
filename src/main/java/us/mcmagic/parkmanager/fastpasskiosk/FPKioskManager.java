package us.mcmagic.parkmanager.fastpasskiosk;

import net.minecraft.server.v1_9_R1.MojangsonParseException;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import us.mcmagic.mcmagiccore.itemcreator.ItemCreator;
import us.mcmagic.mcmagiccore.permissions.Rank;
import us.mcmagic.parkmanager.ParkManager;
import us.mcmagic.parkmanager.handlers.FastPassData;
import us.mcmagic.parkmanager.handlers.KioskData;
import us.mcmagic.parkmanager.handlers.MonthOfYear;
import us.mcmagic.parkmanager.handlers.PlayerData;
import us.mcmagic.parkmanager.utils.HeadUtil;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Marc on 2/19/16
 */
public class FPKioskManager {

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
        a1.setMetadata("kiosk", new FixedMetadataValue(ParkManager.getInstance(), true));
        Location loc2 = new Location(world, loc.getX(), loc.getY() - 0.3, loc.getZ(), loc.getYaw(), 0);
        ArmorStand a2 = world.spawn(loc2, ArmorStand.class);
        a2.setVisible(false);
        a2.setArms(false);
        a2.setSmall(true);
        a2.setBasePlate(false);
        a2.setGravity(false);
        a2.setHelmet(new ItemStack(Material.WOOL, 1, (byte) 15));
        a2.setMetadata("kiosk", new FixedMetadataValue(ParkManager.getInstance(), true));
        Location loc3 = new Location(world, loc.getX(), loc.getY() - 0.6, loc.getZ(), loc.getYaw(), 0);
        ArmorStand a3 = world.spawn(loc3, ArmorStand.class);
        a3.setVisible(false);
        a3.setArms(false);
        a3.setBasePlate(false);
        a3.setGravity(false);
        a3.setHelmet(HeadUtil.getPlayerHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQ" +
                "ubmV0L3RleHR1cmUvMTVjMjkyYTI0ZjU0YTdhNDM3ODUyNjY1NTJkYmE3YTE4NGY5YzUwZTBkOTRiMzM3ZDhkM2U3NmU5ZTljY2" +
                "U3In19fQ==", "FastPass Kiosk"));
        a3.setMetadata("kiosk", new FixedMetadataValue(ParkManager.getInstance(), true));
        world.save();
        player.sendMessage(ChatColor.GREEN + "A FastPass Kiosk has been created!");
    }

    public void openKiosk(Player player) {
        PlayerData data = ParkManager.getPlayerData(player.getUniqueId());
        FastPassData fpdata = data.getFastPassData();
        KioskData kioskData = data.getKioskData();
        long lastVote = kioskData.getLastVote();
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
        ItemStack vote = new ItemCreator((System.currentTimeMillis() - lastVote > 86400000) ? Material.NETHER_STAR :
                Material.IRON_INGOT, ChatColor.GREEN + "Vote for MCMagic!", Arrays.asList(ChatColor.GRAY +
                "Vote for us on Minecraft Server", ChatColor.GRAY + "Lists daily and receive " + ChatColor.YELLOW +
                "5 Tokens!", voteLore(lastVote)));
        ItemStack monthGuest = new ItemCreator(Material.DIAMOND, ChatColor.GREEN + "Monthly Tokens - Guest",
                Arrays.asList(ChatColor.GRAY + "Claim your monthly 50 Tokens!", ChatColor.GRAY +
                        "Everyone can claim this prize!", monthLore(kioskData.getMonthGuest())));
        ItemStack monthDVC = new ItemCreator(Material.DIAMOND, ChatColor.GREEN + "Monthly Tokens - DVC",
                Arrays.asList(ChatColor.GRAY + "Claim your monthly 50 Tokens!", ChatColor.GRAY +
                                "You must be " + Rank.DVCMEMBER.getNameWithBrackets() + " or above",
                        monthLore(kioskData.getMonthDVC())));
        ItemStack monthShare = new ItemCreator(Material.DIAMOND, ChatColor.GREEN + "Monthly Tokens - Shareholder",
                Arrays.asList(ChatColor.GRAY + "Claim your monthly 50 Tokens!", ChatColor.GRAY +
                                "You must be " + Rank.SHAREHOLDER.getNameWithBrackets() + " or above",
                        monthLore(kioskData.getMonthShare())));
        player.openInventory(inv);
    }

    private String voteLore(long lastVote) {
        if (System.currentTimeMillis() - lastVote > 86400000) {
            return ChatColor.YELLOW + "Right-Click to Vote!";
        }
        return ChatColor.GOLD + "You already voted today!";
    }

    private String monthLore(long last) {
        Calendar cur = Calendar.getInstance();
        MonthOfYear month = MonthOfYear.getFromNumber(cur.get(Calendar.MONTH));
        int days = month.getDays();
        if (month.equals(MonthOfYear.FEBRUARY)) {
            if (cur.get(Calendar.YEAR) % 4 == 0) {
                days += 1;
            }
        }
        if ((System.currentTimeMillis() / 1000) - (last / 1000) > (days * 86400)) {
        }
        return "";
    }

    private Material fpItem(int fpCount, int fpDay) {
        int day = getDayOfYear();
        if (day == fpDay) {
            return Material.IRON_INGOT;
        }
        return Material.CLAY_BRICK;
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
        if (fpCount < 1 && (curday != day)) {
            return ChatColor.YELLOW + "Right-Click to Claim!";
        }
        if (fpCount > 0) {
            if (curday == day) {
                Calendar cal = Calendar.getInstance();
                cal.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), curday + 1);
                long mills = cal.getTimeInMillis();
                return ChatColor.GOLD + "Claim again in " + timeToTomorrow();
            }
            return ChatColor.GOLD + "You already have a " + ChatColor.YELLOW + type + ChatColor.GOLD + " FastPass!";
        }
        return "";
    }

    private String timeToNextMonth() {
        Calendar cur = Calendar.getInstance();
        cur.setTimeInMillis(new Date().getTime());
        int lastDayOfMonth = 0;
        MonthOfYear month = MonthOfYear.getFromNumber(cur.get(Calendar.MONTH));
        int days = month.getDays();
        if (month.equals(MonthOfYear.FEBRUARY)) {
            if (cur.get(Calendar.YEAR) % 4 == 0) {
                days += 1;
            }
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
    }
}