package network.palace.parkmanager.shows;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import network.palace.core.Core;
import network.palace.core.menu.Menu;
import network.palace.core.menu.MenuButton;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.core.utils.ItemUtil;
import network.palace.core.utils.TextUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.utils.FileUtil;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ShowMenuManager {
    private final List<ShowEntry> shows = new ArrayList<>();
    private final List<ShowRequest> requests = new ArrayList<>();
    private FileUtil.FileSubsystem subsystem;

    public ShowMenuManager() {
        initialize();
    }

    public void initialize() {
        shows.clear();
        if (ParkManager.getFileUtil().isSubsystemRegistered("showmenu")) {
            subsystem = ParkManager.getFileUtil().getSubsystem("showmenu");
        } else {
            subsystem = ParkManager.getFileUtil().registerSubsystem("showmenu");
        }
        try {
            JsonElement element = subsystem.getFileContents("shows");
            if (element.isJsonArray()) {
                JsonArray array = element.getAsJsonArray();
                for (JsonElement entry : array) {
                    JsonObject object = entry.getAsJsonObject();
                    shows.add(new ShowEntry(object.get("showFile").getAsString(),
                            object.get("region").getAsString(),
                            object.get("displayName").getAsString()));
                }
            }
            saveToFile();
            Core.logMessage("ShowMenu", "Loaded " + shows.size() + " show" + TextUtil.pluralize(shows.size()) + "!");
        } catch (IOException e) {
            Core.logMessage("ShowMenu", "There was an error loading the ShowMenu config!");
            e.printStackTrace();
        }
        try {
            JsonObject object = (JsonObject) subsystem.getFileContents("limits");
            if (object.has("users")) return;
            object.add("users", new JsonArray());
            object.add("shows", new JsonArray());
            subsystem.writeFileContents("limits", object);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ShowEntry getShow(String name) {
        ShowEntry show = null;
        for (ShowEntry entry : shows) {
            if (entry.getCommand().equals(name)) {
                show = entry;
                break;
            }
        }
        return show;
    }

    public void addShow(ShowEntry entry) {
        shows.add(entry);
        saveToFile();
    }

    public boolean removeShow(ShowEntry entry) {
        boolean b = shows.remove(entry);
        saveToFile();
        return b;
    }

    public void listShows(CPlayer player) {
        player.sendMessage(ChatColor.GREEN + "Shareholder Show Menu Shows:");
        for (ShowEntry entry : shows) {
            player.sendMessage(ChatColor.AQUA + "- " + ChatColor.GREEN + entry.getCommand() + ChatColor.YELLOW + ", " +
                    ChatColor.GREEN + entry.getRegion() + ChatColor.YELLOW + ", " + ChatColor.GREEN + entry.getDisplayName());
        }
    }

    private ShowRequest getRequest(UUID requestId) {
        for (ShowRequest request : requests) {
            if (request.getRequestId().equals(requestId)) {
                return request;
            }
        }
        return null;
    }

    public void openShowMenu(CPlayer player) {
        List<MenuButton> buttons = new ArrayList<>();
        int i = 0;
        for (ShowEntry entry : shows) {
            buttons.add(new MenuButton(i++, entry.getItem(), ImmutableMap.of(ClickType.LEFT, p -> openShowConfirm(p, entry))));
        }
        new Menu(27, Rank.SHAREHOLDER.getTagColor() + "Shareholder Show Menu", player, buttons).open();
    }

    public void openRequestMenu(CPlayer player) {
        List<MenuButton> buttons = new ArrayList<>();
        int i = 0;
        for (ShowRequest request : requests) {
            CPlayer requester = Core.getPlayerManager().getPlayer(request.getUuid());
            if (requester == null) continue;

            ItemStack item = request.getShow().getItem();
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(request.getShow().getDisplayName());
            meta.setLore(Collections.singletonList(ChatColor.GREEN + "Requested by: " + ChatColor.LIGHT_PURPLE + requester.getName()));
            item.setItemMeta(meta);

            buttons.add(new MenuButton(i++, item, ImmutableMap.of(ClickType.LEFT, p -> openRequestConfirm(p, request, item))));
        }
        new Menu(27, ChatColor.LIGHT_PURPLE + "Shareholder Show Requests", player, buttons).open();
    }

    public void openShowConfirm(CPlayer player, ShowEntry entry) {
        String s = canRequestShow(player, entry);
        if (s != null) {
            player.closeInventory();
            player.sendMessage(ChatColor.RED + "Uh oh! " + ChatColor.AQUA + s);
            return;
        }
        new Menu(27, Rank.SHAREHOLDER.getTagColor() + "Request Show Start", player, Arrays.asList(
                new MenuButton(4, entry.getItem()),
                new MenuButton(11, ItemUtil.create(Material.CONCRETE, ChatColor.RED + "Cancel", 14),
                        ImmutableMap.of(ClickType.LEFT, CPlayer::closeInventory)),
                new MenuButton(15, ItemUtil.create(Material.CONCRETE, ChatColor.GREEN + "Confirm", 13),
                        ImmutableMap.of(ClickType.LEFT, p -> handleShowRequest(p, entry)))
        )).open();
    }

    public void openRequestConfirm(CPlayer player, ShowRequest request, ItemStack item) {
        new Menu(27, Rank.SHAREHOLDER.getTagColor() + "Respond To Request", player, Arrays.asList(
                new MenuButton(4, item),
                new MenuButton(11, ItemUtil.create(Material.CONCRETE, ChatColor.RED + "Deny", 14),
                        ImmutableMap.of(ClickType.LEFT, p -> handleRequestResposne(p, request, false))),
                new MenuButton(15, ItemUtil.create(Material.CONCRETE, ChatColor.GREEN + "Approve", 13),
                        ImmutableMap.of(ClickType.LEFT, p -> handleRequestResposne(p, request, true)))
        )).open();
    }

//    public void handlePacket(PacketShowRequestResponse packet) {
//        ShowRequest request = getRequest(packet.getRequestId());
//        if (request == null) return;
//        request.setCanBeApproved(true);
//    }

    private void handleShowRequest(CPlayer player, ShowEntry entry) {
        player.closeInventory();
        player.sendMessage(ChatColor.GREEN + "Processing request...");
        UUID requestId = UUID.randomUUID();
        requests.add(new ShowRequest(requestId, player.getUniqueId(), entry));
        try {
            Core.getMessageHandler().sendStaffMessage(ChatColor.GREEN + "A shareholder " + player.getName() + " has requested the show " + entry.getDisplayName() + "! " + ChatColor.GREEN + "To accept/deny this request, head to " + Core.getInstanceName() + " and run /shows!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleRequestResposne(CPlayer player, ShowRequest request, boolean approve) {
        player.closeInventory();
        requests.remove(request);
        CPlayer requester = Core.getPlayerManager().getPlayer(request.getUuid());
        if (approve) {
            String cmd = "multishow start " + request.getCommand();
            Core.logMessage("Shareholder Show", cmd);
            player.performCommand(cmd);
//            ShowPlugin.startShow(request.getShow().getShowFile(), new Show(ParkManager.getInstance(), file));
            updateLimitsFile(player, request);
            Core.getMongoHandler().getDatabase().getCollection("players")
                    .updateOne(Filters.eq("uuid", player.getUniqueId().toString()),
                            Updates.set("showRequests", new Document("lastShow", request.getShow().getCommand())
                                    .append("lastRan", System.currentTimeMillis() / 1000)));
        } else {
            requester.sendMessage(ChatColor.RED + "A staff member has declined your request to run a show. Please try again soon!");
        }
    }

    public String canRequestShow(CPlayer player, ShowEntry entry) {
        try {
            JsonElement element = subsystem.getFileContents("limits");
            if (element.isJsonObject()) {
                JsonObject object = (JsonObject) element;
                JsonArray users = object.getAsJsonArray("users");
                for (JsonElement e : users) {
                    JsonObject o = (JsonObject) e;

                    UUID uuid = UUID.fromString(o.get("uuid").getAsString());
                    if (!uuid.equals(player.getUniqueId())) continue;

                    JsonArray shows = o.getAsJsonArray("shows");
                    for (JsonElement e2 : shows) {
                        JsonObject show = (JsonObject) e2;

                        String showFile = show.get("name").getAsString();
                        if (showFile.equals(entry.getCommand())) {
                            if (show.get("lastRan").getAsLong() > ((System.currentTimeMillis() / 1000) - 172800)) {
                                return "You've already run this show within the last 48 hours, try again soon!";
                            } else {
                                break;
                            }
                        }
                    }
                    break;
                }
                JsonArray shows = object.getAsJsonArray("shows");
                for (JsonElement e : shows) {
                    JsonObject show = (JsonObject) e;

                    String showFile = show.get("name").getAsString();
                    if (showFile.equals(entry.getCommand())) {
                        if (show.get("lastRan").getAsLong() > ((System.currentTimeMillis() / 1000) - 43200)) {
                            return "This show was run by a Shareholder within the last 12 hours, try again soon!";
                        } else {
                            break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void updateLimitsFile(CPlayer player, ShowRequest request) {
        try {
            JsonObject object = (JsonObject) subsystem.getFileContents("limits");

            JsonObject show = new JsonObject();
            show.addProperty("name", request.getShow().getCommand());
            show.addProperty("lastRan", System.currentTimeMillis() / 1000);

            JsonArray users = object.getAsJsonArray("users");
            JsonArray userShows = new JsonArray();
            for (JsonElement e : users) {
                JsonObject o = (JsonObject) e;

                UUID uuid = UUID.fromString(o.get("uuid").getAsString());
                if (!uuid.equals(player.getUniqueId())) continue;

                userShows = o.getAsJsonArray("shows");
                users.remove(o);
                break;
            }
            for (JsonElement e : userShows) {
                JsonObject s = (JsonObject) e;

                String showFile = s.get("name").getAsString();
                if (showFile.equals(request.getShow().getCommand())) userShows.remove(s);
            }
            userShows.add(show);

            JsonObject user = new JsonObject();
            user.addProperty("uuid", player.getUniqueId().toString());
            user.add("shows", userShows);
            users.add(user);

            JsonArray shows = object.getAsJsonArray("shows");
            for (JsonElement e : shows) {
                JsonObject s = (JsonObject) e;

                String showFile = s.get("name").getAsString();
                if (showFile.equals(request.getShow().getCommand())) shows.remove(s);
            }
            shows.add(show);

            subsystem.writeFileContents("limits", object);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveToFile() {
        JsonArray array = new JsonArray();
        shows.sort(Comparator.comparing(showEntry -> ChatColor.stripColor(showEntry.getDisplayName().toLowerCase())));
        for (ShowEntry entry : shows) {
            JsonObject object = new JsonObject();
            object.addProperty("showFile", entry.getCommand());
            object.addProperty("region", entry.getRegion());
            object.addProperty("displayName", entry.getDisplayName());
            array.add(object);
        }
        try {
            ParkManager.getFileUtil().getSubsystem("showmenu").writeFileContents("shows", array);
        } catch (IOException e) {
            Core.logMessage("ShowMenu", "There was an error writing to the ShowMenu config!");
            e.printStackTrace();
        }
    }
}
