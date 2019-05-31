package network.palace.parkmanager.packs;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import network.palace.core.Core;
import network.palace.core.events.CurrentPackReceivedEvent;
import network.palace.core.menu.Menu;
import network.palace.core.menu.MenuButton;
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
                for (int i = 0; i < 5; i++) {
                    player.sendMessage(" ");
                }
                player.sendMessage(ChatColor.RED + "You have declined the Resource Pack!");
                player.sendMessage(ChatColor.YELLOW + "For help with this, visit: " + ChatColor.AQUA +
                        "https://palace.network/rphelp");
                break;
            default:
                player.sendMessage(ChatColor.RED + "Download failed! Please report this to a Staff Member. (Error Code 101)");
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
        if (s.equals("ask")) {
            Core.runTaskLater(() -> openMenu(player), 20L);
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
                new MenuButton(10, ItemUtil.create(Material.LIGHT_BLUE_TERRACOTTA, ChatColor.AQUA + "Blank" + (setting.equals("blank") ? selected : ""),
                        Arrays.asList(ChatColor.GRAY + "You will be sent a blank",
                                ChatColor.GRAY + "resource pack with this setting")),
                        ImmutableMap.of(ClickType.LEFT, p -> changeSetting(p, "blank"))),
                new MenuButton(13, ItemUtil.create(Material.RED_CONCRETE, ChatColor.RED + "Disabled" + (setting.equals("disabled") ? selected : ""),
                        Arrays.asList(ChatColor.GRAY + "You won't receive any",
                                ChatColor.GRAY + "resource packs with this setting")),
                        ImmutableMap.of(ClickType.LEFT, p -> changeSetting(p, "disabled"))),
                new MenuButton(16, ItemUtil.create(Material.GREEN_CONCRETE, ChatColor.GREEN + "Enabled" + (setting.equals("enabled") ? selected : ""),
                        Arrays.asList(ChatColor.GRAY + "You will be sent all park",
                                ChatColor.GRAY + "resource packs with this setting")),
                        ImmutableMap.of(ClickType.LEFT, p -> changeSetting(p, "enabled")))
        );

        new Menu(Core.createInventory(27, ChatColor.BLUE + "Pack Setting"), ChatColor.BLUE + "Pack Setting",
                player, buttons).open();
    }

    /**
     * Set the player's pack setting and send them the new pack
     *
     * @param player  the player
     * @param setting the pack setting
     */
    private void changeSetting(CPlayer player, String setting) {
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 2f);
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
        Core.runTaskAsynchronously(() -> Core.getMongoHandler().setParkSetting(player.getUniqueId(), "pack", setting));
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
