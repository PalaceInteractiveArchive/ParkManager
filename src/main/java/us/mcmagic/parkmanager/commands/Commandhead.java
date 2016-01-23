package us.mcmagic.parkmanager.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Marc on 3/10/15
 */
public class Commandhead implements CommandExecutor {
    private HashMap<String, UUID> cache = new HashMap<>();
    private HashMap<UUID, String> textureCache = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can do this!");
            return true;
        }
        sender.sendMessage(ChatColor.RED + "This command has been disabled. Visit " + ChatColor.AQUA +
                "heads.freshcoal.com " + ChatColor.RED + "to get Player Heads.");
        return true;
        /*
        try {
            Player player = (Player) sender;
            if (args.length == 1) {
                String name = args[0];
                if (cache.containsKey(name)) {
                    UUID uuid = cache.get(name);
                    if (textureCache.containsKey(uuid)) {
                        String texture = textureCache.get(uuid);
                        String tag = "{display:{Name:\"" + name + "\"},SkullOwner:{Id:\"" + uuid.toString() +
                                "\",Properties:{textures:[{Value:\"" + texture + "\"}]}}}";
                        player.sendMessage(ChatColor.GREEN + "Using cache values...");
                        player.sendMessage(ChatColor.BLUE + "Here's your head of " + ChatColor.AQUA + args[0] + "!");
                        player.performCommand("minecraft:give " + player.getName() + " skull 1 3 " + tag);
                        return true;
                    } else {
                        Gson gson = new Gson();
                        player.sendMessage(ChatColor.GREEN + "Retrieving Texture Hash for " + name + "...");
                        String texture = readUrl("https://sessionserver.mojang.com/session/minecraft/profile/" +
                                uuid.toString().replaceAll("-", ""));
                        JsonObject textureData = gson.fromJson(texture, JsonObject.class);
                        String finalTexture = "";
                        if (textureData != null) {
                            finalTexture = textureData.get("properties").getAsJsonArray().get(0).getAsJsonObject().get("value").getAsString();
                        }
                        textureCache.put(uuid, finalTexture);
                        String tag = "{display:{Name:\"" + name + "\"},SkullOwner:{Id:\"" + uuid.toString() +
                                "\",Properties:{textures:[{Value:\"" + finalTexture + "\"}]}}}";
                        player.sendMessage(ChatColor.BLUE + "Here's your head of " + ChatColor.AQUA + args[0] + "!");
                        player.performCommand("minecraft:give " + player.getName() + " skull 1 3 " + tag);
                    }
                    return true;
                }
                player.sendMessage(ChatColor.GREEN + "Retrieving UUID for " + name + "...");
                Gson gson = new Gson();
                UUID uuid;
                String uuidString = "";
                if (Bukkit.getPlayer(name) != null) {
                    uuid = Bukkit.getPlayer(name).getUniqueId();
                    uuidString = uuid.toString().replaceAll("-", "");
                } else {
                    String uuidFromName = readUrl("https://api.mojang.com/users/profiles/minecraft/" + name);
                    JsonObject uuidData = gson.fromJson(uuidFromName, JsonObject.class);
                    if (uuidData != null) {
                        uuidString = uuidData.get("id").getAsString();
                    }
                    uuid = insertDashUUID(uuidString);
                }
                cache.put(name, uuid);
                player.sendMessage(ChatColor.GREEN + "Retrieving Texture Hash for " + name + "...");
                String texture = readUrl("https://sessionserver.mojang.com/session/minecraft/profile/" + uuidString);
                JsonObject textureData = gson.fromJson(texture, JsonObject.class);
                String finalTexture = "";
                if (textureData != null) {
                    finalTexture = textureData.get("properties").getAsJsonArray().get(0).getAsJsonObject().get("value").getAsString();
                }
                textureCache.put(uuid, finalTexture);
                String tag = "{display:{Name:\"" + name + "\"},SkullOwner:{Id:\"" + uuid.toString() +
                        "\",Properties:{textures:[{Value:\"" + finalTexture + "\"}]}}}";
                player.sendMessage(ChatColor.BLUE + "Here's your head of " + ChatColor.AQUA + args[0] + "!");
                player.performCommand("minecraft:give " + player.getName() + " skull 1 3 " + tag);
                return true;
            }
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("-f")) {
                    String name = args[1];
                    player.sendMessage(ChatColor.GREEN + "Forcing update...");
                    player.sendMessage(ChatColor.GREEN + "Retrieving UUID for " + name + "...");
                    String uuidFromName = readUrl("https://api.mojang.com/users/profiles/minecraft/" + name);
                    Gson gson = new Gson();
                    JsonObject uuidData = gson.fromJson(uuidFromName, JsonObject.class);
                    String uuidString = "";
                    if (uuidData != null) {
                        uuidString = uuidData.get("id").getAsString();
                    }
                    UUID uuid = insertDashUUID(uuidString);
                    cache.remove(name);
                    cache.put(name, uuid);
                    player.sendMessage(ChatColor.GREEN + "Retrieving Texture Hash for " + name + "...");
                    String texture = readUrl("https://sessionserver.mojang.com/session/minecraft/profile/" + uuidString);
                    JsonObject textureData = gson.fromJson(texture, JsonObject.class);
                    String finalTexture = "";
                    if (textureData != null) {
                        finalTexture = textureData.get("properties").getAsJsonArray().get(0).getAsJsonObject().get("value").getAsString();
                    }
                    textureCache.remove(uuid);
                    textureCache.put(uuid, finalTexture);
                    String tag = "{display:{Name:\"" + name + "\"},SkullOwner:{Id:\"" + uuid.toString() +
                            "\",Properties:{textures:[{Value:\"" + finalTexture + "\"}]}}}";
                    player.sendMessage(ChatColor.BLUE + "Here's your head of " + ChatColor.AQUA + args[1] + "!");
                    player.performCommand("minecraft:give " + player.getName() + " skull 1 3 " + tag);
                    return true;
                }
            }
            player.sendMessage(ChatColor.RED + "/head [-f] [Username]");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            sender.sendMessage(ChatColor.RED + "Error getting that head!");
            return true;
        }
        */
    }

    private String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder buffer = new StringBuilder();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1) buffer.append(chars, 0, read);
            return buffer.toString();
        } finally {
            if (reader != null) reader.close();
        }
    }

    public static UUID insertDashUUID(String uuid) {
        StringBuffer sb = new StringBuffer(uuid);
        sb.insert(8, "-");
        sb = new StringBuffer(sb.toString());
        sb.insert(13, "-");
        sb = new StringBuffer(sb.toString());
        sb.insert(18, "-");
        sb = new StringBuffer(sb.toString());
        sb.insert(23, "-");
        return UUID.fromString(sb.toString());
    }
}
