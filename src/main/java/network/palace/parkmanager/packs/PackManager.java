package network.palace.parkmanager.packs;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import network.palace.core.Core;
import network.palace.core.events.CurrentPackReceivedEvent;
import network.palace.core.menu.Menu;
import network.palace.core.menu.MenuButton;
import network.palace.core.message.FormattedMessage;
import network.palace.core.player.CPlayer;
import network.palace.core.resource.ResourceStatusEvent;
import network.palace.core.utils.ItemUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.utils.FileUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class PackManager implements Listener {
    private String serverPack = "WDW";

    public PackManager() {
        FileUtil.FileSubsystem subsystem;
        if (ParkManager.getFileUtil().isSubsystemRegistered("packs")) {
            subsystem = ParkManager.getFileUtil().getSubsystem("packs");
        } else {
            subsystem = ParkManager.getFileUtil().registerSubsystem("packs");
        }
        try {
            JsonElement element = subsystem.getFileContents("packs");
            if (element.isJsonObject()) {
                JsonObject object = element.getAsJsonObject();
                if (object.has("pack")) {
                    serverPack = object.get("pack").getAsString();
                } else {
                    saveToFile();
                }
            } else {
                saveToFile();
            }
            Core.logMessage("PackManager", "Loaded the PackManager config!");
        } catch (IOException e) {
            Core.logMessage("PackManager", "There was an error loading the PackManager config!");
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onCurrentPackReceived(CurrentPackReceivedEvent event) {
        CPlayer player = event.getPlayer();
        player.getRegistry().addEntry("pack", event.getPack());
        if (player.getRegistry().hasEntry("packSetting")) {
            sendPack(player);
        } else {
            player.getRegistry().addEntry("needsPack", true);
        }
    }

    @EventHandler
    public void onResourceStatus(ResourceStatusEvent event) {
        CPlayer player = event.getPlayer();
        switch (event.getStatus()) {
            case ACCEPTED:
                player.sendMessage(ChatColor.GREEN + "Resource Pack accepted! Downloading now...");
                break;
            case LOADED:
                player.sendMessage(ChatColor.GREEN + "Resource Pack loaded!");
                break;
            case DECLINED:
                player.sendMessage(ChatColor.RED + "You have declined the Resource Pack!");
                break;
            default: {
                if (player.getRegistry().hasEntry("packDownloadURL")) {
                    String url = (String) player.getRegistry().getEntry("packDownloadURL");
                    new FormattedMessage("Download failed! ").color(ChatColor.RED)
                            .then("You can download the pack manually by clicking ").color(ChatColor.AQUA)
                            .then("here").color(ChatColor.YELLOW).style(ChatColor.UNDERLINE).link(url).send(player);
                } else {
                    player.sendMessage(ChatColor.RED + "Download failed!");
                    player.sendMessage(ChatColor.YELLOW + "For help with this, visit: " + ChatColor.AQUA +
                            "https://palnet.us/rphelp");
                }
            }
        }
    }

    private void sendPack(CPlayer player) {
        String packSetting = (String) player.getRegistry().getEntry("packSetting");
        if (packSetting.equals("enabled") && !player.getRegistry().getEntry("pack").equals(serverPack)) {
            Core.getResourceManager().sendPack(player, serverPack);
        } else if (packSetting.equals("blank")) {
            Core.getResourceManager().sendPack(player, "Blank");
        }
    }

    public void handleJoin(CPlayer player, String s) {
        if (s.equals("ask") || s.equals("yes") || s.equals("no")) {
            Core.runTaskLater(ParkManager.getInstance(), () -> openMenu(player), 20L);
            return;
        }
        player.getRegistry().addEntry("packSetting", s);
        if (player.getRegistry().hasEntry("needsPack") && (boolean) player.getRegistry().getEntry("needsPack")) {
            sendPack(player);
        }
    }

    /**
     * Open a menu where you choose what pack setting you want
     *
     * @param player the player
     */
    public void openMenu(CPlayer player) {
        String setting;
        if (!player.getRegistry().hasEntry("packSetting")) {
            setting = "ask";
        } else {
            setting = (String) player.getRegistry().getEntry("packSetting");
        }
        String selected = ChatColor.YELLOW + " (SELECTED)";
        List<MenuButton> buttons = Arrays.asList(
                new MenuButton(10, ItemUtil.create(Material.STAINED_CLAY, 1, 3,
                        ChatColor.AQUA + "Blank" + (setting.equals("blank") ? selected : ""),
                        Arrays.asList(ChatColor.GRAY + "You will be sent a blank",
                                ChatColor.GRAY + "resource pack with this setting")),
                        ImmutableMap.of(ClickType.LEFT, p -> changeSetting(p, "blank"))),
                new MenuButton(13, ItemUtil.create(Material.CONCRETE, 1, 14,
                        ChatColor.RED + "Disabled" + (setting.equals("disabled") ? selected : ""),
                        Arrays.asList(ChatColor.GRAY + "You won't receive any",
                                ChatColor.GRAY + "resource packs with this setting")),
                        ImmutableMap.of(ClickType.LEFT, p -> changeSetting(p, "disabled"))),
                new MenuButton(16, ItemUtil.create(Material.CONCRETE, 1, 13,
                        ChatColor.GREEN + "Enabled" + (setting.equals("enabled") ? selected : ""),
                        Arrays.asList(ChatColor.GRAY + "You will be sent all park",
                                ChatColor.GRAY + "resource packs with this setting")),
                        ImmutableMap.of(ClickType.LEFT, p -> changeSetting(p, "enabled")))
        );

        new Menu(27, ChatColor.BLUE + "Pack Setting", player, buttons).open();
    }

    /**
     * Set the player's pack setting and send them the new pack
     *
     * @param player  the player
     * @param setting the pack setting
     */
    private void changeSetting(CPlayer player, String setting) {
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 1f, 2f);
        switch (setting.toLowerCase()) {
            case "enabled": {
                player.sendMessage(ChatColor.GREEN + "You've enabled Park Resource Packs!");
                player.getRegistry().addEntry("packSetting", "enabled");
                break;
            }
            case "disabled": {
                player.sendMessage(ChatColor.RED + "You've disabled Park Resource Packs!");
                player.getRegistry().addEntry("packSetting", "disabled");
                break;
            }
            case "blank": {
                player.sendMessage(ChatColor.DARK_AQUA + "You will be sent a " + ChatColor.AQUA + "blank " + ChatColor.DARK_AQUA + "resource pack in the Parks!");
                player.getRegistry().addEntry("packSetting", "blank");
                break;
            }
        }
        player.closeInventory();
        sendPack(player);
        Core.runTaskAsynchronously(ParkManager.getInstance(), () -> Core.getMongoHandler().setParkSetting(player.getUniqueId(), "pack", setting));
    }

    public void setServerPack(String pack) {
        this.serverPack = pack;
        saveToFile();
    }

    public void saveToFile() {
        JsonObject object = new JsonObject();
        object.addProperty("pack", serverPack);
        try {
            ParkManager.getFileUtil().getSubsystem("packs").writeFileContents("packs", object);
        } catch (IOException e) {
            Core.logMessage("PackManager", "There was an error writing to the PackManager config!");
            e.printStackTrace();
        }
    }
}
