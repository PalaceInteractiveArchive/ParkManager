package us.mcmagic.magicassistant.utils;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class FileUtil {

	public static void setupConfig() {
		try {
			File file = new File("plugins/Hub/config.yml");
			file.createNewFile();
			YamlConfiguration config = YamlConfiguration
					.loadConfiguration(file);
			config.set("server-name", "Hub");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void setupFoodFile() {
		try {
			File file = new File("plugins/Hub/food.yml");
			file.createNewFile();
			YamlConfiguration config = YamlConfiguration
					.loadConfiguration(file);
			config.set("food-names", Arrays.asList(""));
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void setSpawn(Location loc) {
		File file = new File("plugins/Hub/config.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		config.set("spawn.world", loc.getWorld().getName());
		config.set("spawn.x", loc.getX());
		config.set("spawn.y", loc.getY());
		config.set("spawn.z", loc.getZ());
		config.set("spawn.yaw", loc.getYaw());
		config.set("spawn.pitch", loc.getPitch());
		saveFile(file, config);
	}

	public static void saveFile(File file, YamlConfiguration config) {
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void setHub(Location loc) {
		File file = new File("plugins/Hub/config.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		config.set("hub.world", loc.getWorld().getName());
		config.set("hub.x", loc.getX());
		config.set("hub.y", loc.getY());
		config.set("hub.z", loc.getZ());
		config.set("hub.yaw", loc.getYaw());
		config.set("hub.pitch", loc.getPitch());
		saveFile(file, config);
	}

	public static void setFirstJoinItems() {
		File file = new File("plugins/Hub/config.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		List<String> list = Arrays.asList("260 6", "310 1", "351 1", "264 10");
		config.set("first-join-items", list);
		saveFile(file, config);
	}

	public static void setFirstJoinMessages() {
		File file = new File("plugins/Hub/config.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		List<String> msgs = Arrays.asList(
				"&fHey everyone, it’s &d%pl% &ffirst visit to McMagic!",
				"&d%total% Guests have visited McMagic since 2012!");
		config.set("first-join-messages", msgs);
		saveFile(file, config);
	}
}