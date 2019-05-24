package network.palace.parkmanager.showschedule;

import com.google.common.collect.ImmutableMap;
import com.mongodb.Block;
import lombok.AllArgsConstructor;
import lombok.Getter;
import network.palace.core.Core;
import network.palace.core.menu.Menu;
import network.palace.core.menu.MenuButton;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.ItemUtil;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

import java.util.*;

public class ScheduleManager {
    private List<ScheduledShow> shows = new ArrayList<>();
    private List<String> times = new ArrayList<>();
    @Getter private List<MenuButton> buttons = new ArrayList<>();

    public ScheduleManager() {
        Core.runTaskTimerAsynchronously(this::updateShows, 0L, 36000L);
    }

    public void updateShows() {
        List<ScheduledShow> shows = new ArrayList<>();
        Core.getMongoHandler().getScheduledShows().forEach((Block<? super Document>) doc ->
                shows.add(new ScheduledShow(ShowType.fromString(doc.getString("show")), ShowDay.fromString(doc.getString("day")), doc.getInteger("time"), getTime(doc.getInteger("time")))));
        shows.sort((o1, o2) -> {
            if (o1.getRawTime() == o2.getRawTime()) {
                return o1.getDay().ordinal() - o2.getDay().ordinal();
            }
            return o1.getRawTime() - o2.getRawTime();
        });
        Core.runTask(() -> {
            this.shows = shows;
            updateButtons();
        });
    }

    private void updateButtons() {
        buttons.clear();

        ItemStack monday = new ItemStack(Material.BLACK_BANNER);
        ItemStack tuesday = new ItemStack(Material.BLACK_BANNER);
        ItemStack wednesday = new ItemStack(Material.BLACK_BANNER);
        ItemStack thursday = new ItemStack(Material.BLACK_BANNER);
        ItemStack friday = new ItemStack(Material.BLACK_BANNER);
        ItemStack saturday = new ItemStack(Material.BLACK_BANNER);
        ItemStack sunday = new ItemStack(Material.BLACK_BANNER);
        BannerMeta bm = (BannerMeta) monday.getItemMeta();
        BannerMeta bt = (BannerMeta) tuesday.getItemMeta();
        BannerMeta bw = (BannerMeta) wednesday.getItemMeta();
        BannerMeta bth = (BannerMeta) thursday.getItemMeta();
        BannerMeta bf = (BannerMeta) friday.getItemMeta();
        BannerMeta bs = (BannerMeta) saturday.getItemMeta();
        BannerMeta bsu = (BannerMeta) sunday.getItemMeta();
        List<Pattern> m = new ArrayList<>();
        List<Pattern> t = new ArrayList<>();
        List<Pattern> w = new ArrayList<>();
        List<Pattern> f = new ArrayList<>();
        List<Pattern> s = new ArrayList<>();
        DyeColor lb = DyeColor.LIGHT_BLUE;
        DyeColor bl = DyeColor.BLACK;
        m.add(new Pattern(lb, PatternType.TRIANGLE_TOP));
        m.add(new Pattern(lb, PatternType.TRIANGLES_TOP));
        m.add(new Pattern(lb, PatternType.STRIPE_LEFT));
        m.add(new Pattern(lb, PatternType.STRIPE_RIGHT));
        t.add(new Pattern(lb, PatternType.STRIPE_CENTER));
        t.add(new Pattern(lb, PatternType.STRIPE_TOP));
        w.add(new Pattern(lb, PatternType.TRIANGLE_BOTTOM));
        w.add(new Pattern(bl, PatternType.TRIANGLES_BOTTOM));
        w.add(new Pattern(lb, PatternType.STRIPE_LEFT));
        w.add(new Pattern(lb, PatternType.STRIPE_RIGHT));
        f.add(new Pattern(lb, PatternType.STRIPE_MIDDLE));
        f.add(new Pattern(bl, PatternType.STRIPE_RIGHT));
        f.add(new Pattern(lb, PatternType.STRIPE_LEFT));
        f.add(new Pattern(lb, PatternType.STRIPE_TOP));
        s.add(new Pattern(lb, PatternType.TRIANGLE_TOP));
        s.add(new Pattern(lb, PatternType.TRIANGLE_BOTTOM));
        s.add(new Pattern(lb, PatternType.SQUARE_TOP_RIGHT));
        s.add(new Pattern(lb, PatternType.SQUARE_BOTTOM_LEFT));
        s.add(new Pattern(bl, PatternType.RHOMBUS_MIDDLE));
        s.add(new Pattern(lb, PatternType.STRIPE_DOWNRIGHT));
        bm.setPatterns(m);
        bt.setPatterns(t);
        bw.setPatterns(w);
        bth.setPatterns(t);
        bf.setPatterns(f);
        bs.setPatterns(s);
        bsu.setPatterns(s);
        bm.setDisplayName(ChatColor.GREEN + "Monday");
        bt.setDisplayName(ChatColor.GREEN + "Tuesday");
        bw.setDisplayName(ChatColor.GREEN + "Wednesday");
        bth.setDisplayName(ChatColor.GREEN + "Thursday");
        bf.setDisplayName(ChatColor.GREEN + "Friday");
        bs.setDisplayName(ChatColor.GREEN + "Saturday");
        bsu.setDisplayName(ChatColor.GREEN + "Sunday");
        monday.setItemMeta(bm);
        tuesday.setItemMeta(bt);
        wednesday.setItemMeta(bw);
        thursday.setItemMeta(bth);
        friday.setItemMeta(bf);
        saturday.setItemMeta(bs);
        sunday.setItemMeta(bsu);

        buttons.addAll(Arrays.asList(new MenuButton(1, monday), new MenuButton(2, tuesday), new MenuButton(3, wednesday),
                new MenuButton(4, thursday), new MenuButton(5, friday), new MenuButton(6, saturday),
                new MenuButton(7, sunday)));

        List<String> times = new ArrayList<>();
        shows.stream().filter(show -> !times.contains(show.getTime())).forEach(show -> times.add(show.getTime()));
        HashMap<String, Integer> timeMap = new HashMap<>();
        int i = 9;
        for (String st : times) {
            if (i >= 54) {
                break;
            }
            buttons.add(new MenuButton(i, ItemUtil.create(Material.CLOCK, ChatColor.GREEN + st + " EST")));
            timeMap.put(st, i / 9);
            i += 9;
        }

        for (ScheduledShow show : shows) {
            ShowType type = show.getType();
            int place = getShowPos(show.getDay(), show.getTime(), timeMap);
            if (type.getType().equals(Material.RED_BANNER)) {
                ItemStack banner = new ItemStack(Material.RED_BANNER);
                BannerMeta bmeta = (BannerMeta) banner.getItemMeta();
                bmeta.addPattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_SMALL));
                bmeta.addPattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_SMALL));
                bmeta.addPattern(new Pattern(DyeColor.BLUE, PatternType.SQUARE_TOP_RIGHT));
                bmeta.addPattern(new Pattern(DyeColor.BLUE, PatternType.SQUARE_TOP_RIGHT));
                bmeta.addPattern(new Pattern(DyeColor.BLUE, PatternType.SQUARE_TOP_RIGHT));
                bmeta.setDisplayName(type.getName());
                banner.setItemMeta(bmeta);
                buttons.add(new MenuButton(place, banner));
                continue;
            }
            buttons.add(new MenuButton(place, ItemUtil.create(type.getType(), type.getName(),
                    new ArrayList<>())));
        }
    }

    public List<ScheduledShow> getShows() {
        return new ArrayList<>(shows);
    }

    public String getTime(int time) {
        String am;
        if (time >= 1200) {
            am = "PM";
        } else {
            am = "AM";
        }
        if (time == 0) {
            return "12:00 AM";
        }
        return ((time / 100) % 12) + ":00 " + am;
    }

    private int getShowPos(ShowDay day, String time, HashMap<String, Integer> timeMap) {
        int i = 10;
        i += day.ordinal();
        i += (9 * (timeMap.get(time) - 1));
        return i;
    }

    public void editSchedule(CPlayer player) {
        List<MenuButton> buttons = new ArrayList<>(this.buttons);
        for (int i = 0; i < buttons.size(); i++) {
            MenuButton b = buttons.get(i);
            ItemStack item = b.getItemStack();
            if (item == null || item.getType() == null || item.getType().equals(Material.BLACK_BANNER) || item.getType().equals(Material.CLOCK) || item.getType().equals(Material.ARROW))
                continue;
            buttons.set(i, new MenuButton(b.getSlot(), getEditItem(b.getItemStack()), ImmutableMap.of(ClickType.LEFT, p -> editShow(player, b.getSlot()))));
        }

        new Menu(Core.createInventory(54, ChatColor.BLUE + "Edit Timetable"), ChatColor.BLUE + "Edit Timetable", player, buttons).open();
    }

    private void editShow(CPlayer player, int slot) {
        List<String> times = new ArrayList<>();
        shows.stream().filter(show -> !times.contains(show.getTime())).forEach(show -> times.add(show.getTime()));
        HashMap<String, Integer> timeMap = new HashMap<>();
        int i = 9;
        for (String st : times) {
            if (i >= 54) break;
            timeMap.put(st, i / 9);
            i += 9;
        }

        ScheduledShow show = null;
        int replace = 0;
        for (ScheduledShow s : getShows()) {
            if (getShowPos(s.getDay(), s.getTime(), timeMap) == slot) {
                show = s;
                break;
            }
            replace++;
        }
        if (show == null) return;

        i = 0;
        List<MenuButton> buttons = new ArrayList<>();
        int finalReplace = replace;
        ScheduledShow finalShow = show;
        for (ShowType type : ShowType.values()) {
            buttons.add(new MenuButton(i++, ItemUtil.create(type.getType(), type.getName(),
                    Arrays.asList(ChatColor.GREEN + "Update the timetable entry", ChatColor.GREEN + "for " + ChatColor.AQUA + finalShow.getDay().name() + " at " + finalShow.getTime())),
                    ImmutableMap.of(ClickType.LEFT, p -> {
                        p.sendMessage(ChatColor.GREEN + "Set the show at " + ChatColor.AQUA + finalShow.getDay().name() + " at " + finalShow.getTime() + ChatColor.GREEN + " (" + finalReplace + ") to " + type.getName());
                        shows.set(finalReplace, new ScheduledShow(type, finalShow.getDay(), finalShow.getRawTime(), finalShow.getTime()));
                        updateButtons();
                        Core.runTaskAsynchronously(this::saveToDatabase);
                        editSchedule(p);
                    }))
            );
        }

        new Menu(Core.createInventory(54, ChatColor.BLUE + "Edit Show"), ChatColor.BLUE + "Edit Show", player, buttons).open();
    }

    private void saveToDatabase() {
        List<Document> list = new ArrayList<>();
        for (ScheduledShow show : getShows()) {
            list.add(new Document("day", show.getDay().name().toLowerCase())
                    .append("time", show.getRawTime())
                    .append("show", show.getType().getDBName()));
        }
        Core.getMongoHandler().updateScheduledShows(list);
    }

    private ItemStack getEditItem(ItemStack i) {
        return ItemUtil.create(i.getType(), i.getItemMeta().getDisplayName(), Collections.singletonList(ChatColor.GREEN + "Left-Click to change this show"));
    }

    @Getter
    @AllArgsConstructor
    public static class ScheduledShow {
        private ShowType type;
        private ShowDay day;
        private int rawTime;
        private String time;
    }

    public enum ShowDay {
        MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY;

        public static ShowDay fromString(String s) {
            switch (s.toLowerCase()) {
                case "monday":
                    return MONDAY;
                case "tuesday":
                    return TUESDAY;
                case "wednesday":
                    return WEDNESDAY;
                case "thursday":
                    return THURSDAY;
                case "friday":
                    return FRIDAY;
                case "saturday":
                    return SATURDAY;
                case "sunday":
                    return SUNDAY;
            }
            return null;
        }
    }
}
