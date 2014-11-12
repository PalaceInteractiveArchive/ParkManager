package com.legobuilder0813.MagicAssistant;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.legobuilder0813.MagicAssistant.Commands.Command_Tp;
import com.legobuilder0813.MagicAssistant.Commands.Command_day;
import com.legobuilder0813.MagicAssistant.Commands.Command_night;
import com.legobuilder0813.MagicAssistant.Commands.Command_noon;
import com.legobuilder0813.MagicAssistant.Listeners.ChatListener;
import com.legobuilder0813.MagicAssistant.Listeners.PlayerJoinAndLeave;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class MagicAssistant extends JavaPlugin implements Listener {
	public static MagicAssistant plugin;
	public static Inventory ni;
	public int randomNumber = 0;
	public final HashMap<Player, ArrayList<Block>> watching = new HashMap<Player, ArrayList<Block>>();
	public final HashMap<Player, ArrayList<Block>> chattimeout = new HashMap<Player, ArrayList<Block>>();
	private WorldEditPlugin we;
	private WorldGuardPlugin wg;

	public void onEnable() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(this, this);
		pm.registerEvents(new ChatListener(this), this);
		pm.registerEvents(new StitchEscape(this), this);
		pm.registerEvents(new PlayerJoinAndLeave(this), this);
		getConfig().options().copyDefaults(true);
		Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		saveConfig();
		getLogger().info("Magic Assistant is ready to help!");
	}

	public WorldEditPlugin getWE() {
		return we;
	}

	public WorldGuardPlugin getWG() {
		return wg;
	}

	public void onDisable() {
		getLogger().info("Magic Assistant is taking a coffee break.");
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		if (label.equalsIgnoreCase("day")) {
			Command_day.execute(label, args, sender);
			return true;
		} else if (label.equalsIgnoreCase("night")) {
			Command_night.execute(label, args, sender);
			return true;
		} else if (label.equalsIgnoreCase("noon")) {
			Command_noon.execute(label, args, sender);
			return true;
		} else if (label.equalsIgnoreCase("gwts")) {
			// List of types of hats:
			ItemStack clear = new ItemStack(Material.AIR);
			ItemStack red = new ItemStack(Material.LEATHER_HELMET, 1);
			ItemStack orj = new ItemStack(Material.LEATHER_HELMET, 1);
			ItemStack yel = new ItemStack(Material.LEATHER_HELMET, 1);
			ItemStack grn = new ItemStack(Material.LEATHER_HELMET, 1);
			ItemStack blu = new ItemStack(Material.LEATHER_HELMET, 1);
			ItemStack pur = new ItemStack(Material.LEATHER_HELMET, 1);
			ItemStack wht = new ItemStack(Material.LEATHER_HELMET, 1);
			ItemStack blk = new ItemStack(Material.LEATHER_HELMET, 1);
			ItemStack pnk = new ItemStack(Material.LEATHER_HELMET, 1);
			ItemStack aqa = new ItemStack(Material.LEATHER_HELMET, 1);
			LeatherArmorMeta rarm = (LeatherArmorMeta) red.getItemMeta();
			LeatherArmorMeta oarm = (LeatherArmorMeta) orj.getItemMeta();
			LeatherArmorMeta yarm = (LeatherArmorMeta) yel.getItemMeta();
			LeatherArmorMeta garm = (LeatherArmorMeta) grn.getItemMeta();
			LeatherArmorMeta barm = (LeatherArmorMeta) blu.getItemMeta();
			LeatherArmorMeta parm = (LeatherArmorMeta) pur.getItemMeta();
			LeatherArmorMeta warm = (LeatherArmorMeta) wht.getItemMeta();
			LeatherArmorMeta blarm = (LeatherArmorMeta) blk.getItemMeta();
			LeatherArmorMeta piarm = (LeatherArmorMeta) pnk.getItemMeta();
			LeatherArmorMeta aqam = (LeatherArmorMeta) aqa.getItemMeta();
			rarm.setColor(Color.fromRGB(170, 0, 0));
			oarm.setColor(Color.fromRGB(255, 102, 0));
			yarm.setColor(Color.fromRGB(255, 222, 0));
			garm.setColor(Color.fromRGB(0, 153, 0));
			barm.setColor(Color.fromRGB(51, 51, 255));
			parm.setColor(Color.fromRGB(39, 31, 155));
			warm.setColor(Color.fromRGB(255, 255, 255));
			blarm.setColor(Color.fromRGB(0, 0, 0));
			piarm.setColor(Color.fromRGB(255, 0, 255));
			aqam.setColor(Color.fromRGB(0, 255, 255));
			rarm.setDisplayName(ChatColor.LIGHT_PURPLE + "Mickey Ears");
			oarm.setDisplayName(ChatColor.LIGHT_PURPLE + "Mickey Ears");
			yarm.setDisplayName(ChatColor.LIGHT_PURPLE + "Mickey Ears");
			garm.setDisplayName(ChatColor.LIGHT_PURPLE + "Mickey Ears");
			barm.setDisplayName(ChatColor.LIGHT_PURPLE + "Mickey Ears");
			parm.setDisplayName(ChatColor.LIGHT_PURPLE + "Mickey Ears");
			warm.setDisplayName(ChatColor.LIGHT_PURPLE + "Mickey Ears");
			blarm.setDisplayName(ChatColor.LIGHT_PURPLE + "Mickey Ears");
			piarm.setDisplayName(ChatColor.LIGHT_PURPLE + "Mickey Ears");
			aqam.setDisplayName(ChatColor.LIGHT_PURPLE + "Mickey Ears");
			red.setItemMeta(rarm);
			orj.setItemMeta(oarm);
			yel.setItemMeta(yarm);
			grn.setItemMeta(garm);
			blu.setItemMeta(barm);
			pur.setItemMeta(parm);
			wht.setItemMeta(warm);
			blk.setItemMeta(blarm);
			pnk.setItemMeta(piarm);
			aqa.setItemMeta(aqam);
			// Referring to console
			if (!(sender instanceof Player)) {
				if (args.length == 0) {
					sender.sendMessage(ChatColor.DARK_AQUA
							+ "/gwts <red,orange,yellow,green,blue,aqua,purple,pink,white,black,done> [player]");
				} else if (args.length == 1) {
					sender.sendMessage(ChatColor.RED
							+ "Error: Cannot execute command through console");
				} else if (args.length == 2) {
					Player tp = sender.getServer().getPlayer(args[1]);
					PlayerInventory tpi = tp.getInventory();
					if (args[0].equals("red")) {
						tpi.setHelmet(red);
					} else if (args[0].equalsIgnoreCase("orange")) {
						tpi.setHelmet(orj);
					} else if (args[0].equalsIgnoreCase("yellow")) {
						tpi.setHelmet(yel);
					} else if (args[0].equalsIgnoreCase("green")) {
						tpi.setHelmet(grn);
					} else if (args[0].equalsIgnoreCase("blue")) {
						tpi.setHelmet(blu);
					} else if (args[0].equalsIgnoreCase("aqua")) {
						tpi.setHelmet(aqa);
					} else if (args[0].equalsIgnoreCase("purple")) {
						tpi.setHelmet(pur);
					} else if (args[0].equalsIgnoreCase("white")) {
						tpi.setHelmet(wht);
					} else if (args[0].equalsIgnoreCase("black")) {
						tpi.setHelmet(blk);
					} else if (args[0].equalsIgnoreCase("pink")) {
						tpi.setHelmet(pnk);
					} else if (args[0].equalsIgnoreCase("done")) {
						tpi.remove(red);
						tpi.remove(orj);
						tpi.remove(yel);
						tpi.remove(grn);
						tpi.remove(blu);
						tpi.remove(pur);
						tpi.remove(wht);
						tpi.remove(blk);
						tpi.remove(pnk);
						tpi.remove(aqa);
						tpi.setHelmet(clear);
					}
				}
				// As a player command
			} else if (sender.hasPermission("magicassistant.gwts")) {
				Player player = (Player) sender;
				if (args.length == 0) {
					player.sendMessage(ChatColor.DARK_AQUA
							+ "/gwts <red,orange,yellow,green,blue,aqua,purple,pink,white,black,done> [player]");
				} else if (args.length == 1) {
					PlayerInventory pi = player.getInventory();
					if (args[0].equalsIgnoreCase("red")) {
						pi.setHelmet(red);
					} else if (args[0].equalsIgnoreCase("orange")) {
						pi.setHelmet(orj);
					} else if (args[0].equalsIgnoreCase("yellow")) {
						pi.setHelmet(yel);
					} else if (args[0].equalsIgnoreCase("green")) {
						pi.setHelmet(grn);
					} else if (args[0].equalsIgnoreCase("blue")) {
						pi.setHelmet(blu);
					} else if (args[0].equalsIgnoreCase("aqua")) {
						pi.setHelmet(aqa);
					} else if (args[0].equalsIgnoreCase("purple")) {
						pi.setHelmet(pur);
					} else if (args[0].equalsIgnoreCase("white")) {
						pi.setHelmet(wht);
					} else if (args[0].equalsIgnoreCase("black")) {
						pi.setHelmet(blk);
					} else if (args[0].equalsIgnoreCase("pink")) {
						pi.setHelmet(pnk);
					} else if (args[0].equalsIgnoreCase("done")) {
						pi.remove(red);
						pi.remove(orj);
						pi.remove(yel);
						pi.remove(grn);
						pi.remove(blu);
						pi.remove(pur);
						pi.remove(wht);
						pi.remove(blk);
						pi.remove(pnk);
						pi.remove(aqa);
						pi.setHelmet(clear);
					}
				} else if (args.length == 2) {
					Player tp = sender.getServer().getPlayer(args[1]);
					PlayerInventory tpi = tp.getInventory();
					if (args[0].equals("red")) {
						tpi.setHelmet(red);
					} else if (args[0].equals("orange")) {
						tpi.setHelmet(orj);
					} else if (args[0].equals("yellow")) {
						tpi.setHelmet(yel);
					} else if (args[0].equals("green")) {
						tpi.setHelmet(grn);
					} else if (args[0].equals("blue")) {
						tpi.setHelmet(blu);
					} else if (args[0].equalsIgnoreCase("aqua")) {
						tpi.setHelmet(aqa);
					} else if (args[0].equals("purple")) {
						tpi.setHelmet(pur);
					} else if (args[0].equals("white")) {
						tpi.setHelmet(wht);
					} else if (args[0].equals("black")) {
						tpi.setHelmet(blk);
					} else if (args[0].equalsIgnoreCase("pink")) {
						tpi.setHelmet(pnk);
					} else if (args[0].equals("done")) {
						tpi.remove(red);
						tpi.remove(orj);
						tpi.remove(yel);
						tpi.remove(grn);
						tpi.remove(blu);
						tpi.remove(pur);
						tpi.remove(wht);
						tpi.remove(blk);
						tpi.remove(pnk);
						tpi.remove(aqa);
						tpi.setHelmet(clear);
					}
				}
			}
		} else if (label.equalsIgnoreCase("magicassistant")) {
			if (args.length == 0) {
				Player player = (Player) sender;
				player.sendMessage(ChatColor.GREEN
						+ "----------------------------------------------------");
				player.sendMessage(ChatColor.DARK_GREEN
						+ "Magic assistant was created by " + ChatColor.BLUE
						+ "Legobuilder0813");
				player.sendMessage(ChatColor.DARK_GREEN
						+ "Version Number: "
						+ ChatColor.GOLD
						+ Bukkit.getServer().getPluginManager()
								.getPlugin("MagicAssistant").getDescription()
								.getVersion());
				player.sendMessage(ChatColor.GREEN
						+ "----------------------------------------------------");
			} else if (args.length == 1) {
				Player player = (Player) sender;
				if (args[0].equalsIgnoreCase("reload")
						&& player.hasPermission("magicassistant.reload")) {
					player.sendMessage(ChatColor.AQUA + "[MagicAssistant]"
							+ ChatColor.BLUE + " Now reloading configuration");
					this.reloadConfig();
					player.sendMessage(ChatColor.AQUA + "[MagicAssistant]"
							+ ChatColor.BLUE + " Configuration reloaded!");
				}
			}
		} else if (label.equalsIgnoreCase("save")) {
			if (!(sender instanceof Player)) {
				return true;
			}
			Player player = (Player) sender;
			player.performCommand("save-all");
		} else if (label.equalsIgnoreCase("tp")) {
			Command_Tp.execute(label, args, sender);
			return true;
		} else if (label.equalsIgnoreCase("head")) {
			Player player = (Player) sender;
			PlayerInventory pi = player.getInventory();
			if (args.length == 1) {
				ItemStack head = new ItemStack(Material.SKULL_ITEM, 1,
						(short) 3);
				SkullMeta headm = (SkullMeta) head.getItemMeta();
				headm.setOwner(args[0]);
				head.setItemMeta(headm);
				pi.addItem(head);
				player.sendMessage(ChatColor.AQUA + "[MagicAssistant]"
						+ ChatColor.BLUE + " Enjoy your new head of " + args[0]
						+ "!");
			} else {
				player.sendMessage(ChatColor.RED + "/head [playerhead]");
			}
		}
		return false;
	}

	public static boolean isInt(String s) {
		try {
			Integer.parseInt(s);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	@SuppressWarnings("deprecation")
	public void checkInventories() {
		List<Player> onlinePlayers = Arrays.asList(Bukkit.getServer()
				.getOnlinePlayers());
		Iterator<Player> iterator = onlinePlayers.iterator();
		while (iterator.hasNext()) {
			Player onlinePlayer = iterator.next();
			PlayerInventory pi = onlinePlayer.getInventory();
			Inventory pec = onlinePlayer.getEnderChest();
			if (!(onlinePlayer.hasPermission("magicassistant.invcheck.bypass"))) {
				pi.remove(Material.TNT);
				pi.remove(Material.PUMPKIN);
				pi.remove(Material.JACK_O_LANTERN);
				pi.remove(Material.DIODE);
				pi.remove(Material.IRON_INGOT);
				pi.remove(Material.REDSTONE_COMPARATOR);
				pi.remove(Material.STONE_PLATE);
				pi.remove(Material.WOOD_PLATE);
				pi.remove(Material.BOAT);
				pi.remove(Material.TORCH);
				pi.remove(Material.RAILS);
				pi.remove(Material.POWERED_RAIL);
				pi.remove(Material.DETECTOR_RAIL);
				pi.remove(Material.ACTIVATOR_RAIL);
				pi.remove(Material.MILK_BUCKET);
				pi.remove(Material.LEVER);
				pi.remove(Material.COMMAND);
				pi.remove(Material.MINECART);
				pi.remove(Material.CACTUS);
				pi.remove(Material.WORKBENCH);
				pi.remove(Material.WOOD);
				pi.remove(Material.WOOD_BUTTON);
				pi.remove(Material.STONE_BUTTON);
				pi.remove(Material.REDSTONE_TORCH_ON);
				pi.remove(Material.REDSTONE_TORCH_OFF);
				pi.remove(Material.REDSTONE);
				pi.remove(Material.ITEM_FRAME);
				pi.remove(Material.FIREWORK);
				pi.remove(Material.PAINTING);
				pi.remove(Material.LAVA_BUCKET);
				pi.remove(Material.WATER_BUCKET);
				pi.remove(Material.BUCKET);
				pi.remove(Material.GOLD_SWORD);
				pi.remove(Material.ENCHANTED_BOOK);
				pi.remove(Material.STONE);
				pi.remove(Material.GRASS);
				pi.remove(Material.ARROW);
				pi.remove(Material.BOW);
				pi.remove(Material.EXP_BOTTLE);
				pi.remove(Material.LEASH);
				pi.remove(Material.WOOD_AXE);
				pi.remove(Material.STONE_AXE);
				pi.remove(Material.IRON_AXE);
				pi.remove(Material.DIAMOND_AXE);
				pi.remove(Material.GOLD_AXE);
				pi.remove(Material.BRICK);
				pi.remove(Material.WOOD_PICKAXE);
				pi.remove(Material.STONE_PICKAXE);
				pi.remove(Material.IRON_PICKAXE);
				pi.remove(Material.DIAMOND_PICKAXE);
				pi.remove(Material.GOLD_PICKAXE);
				pi.remove(Material.FIREWORK_CHARGE);
				pec.remove(Material.TNT);
				pec.remove(Material.DIODE);
				pec.remove(Material.IRON_INGOT);
				pec.remove(Material.REDSTONE_COMPARATOR);
				pec.remove(Material.STONE_PLATE);
				pec.remove(Material.WOOD_PLATE);
				pec.remove(Material.PUMPKIN);
				pec.remove(Material.JACK_O_LANTERN);
				pec.remove(Material.BOAT);
				pec.remove(Material.TORCH);
				pec.remove(Material.RAILS);
				pec.remove(Material.POWERED_RAIL);
				pec.remove(Material.DETECTOR_RAIL);
				pec.remove(Material.ACTIVATOR_RAIL);
				pec.remove(Material.MILK_BUCKET);
				pec.remove(Material.LEVER);
				pec.remove(Material.COMMAND);
				pec.remove(Material.MINECART);
				pec.remove(Material.CACTUS);
				pec.remove(Material.WORKBENCH);
				pec.remove(Material.WOOD);
				pec.remove(Material.WOOD_BUTTON);
				pec.remove(Material.STONE_BUTTON);
				pec.remove(Material.REDSTONE_TORCH_ON);
				pec.remove(Material.REDSTONE_TORCH_OFF);
				pec.remove(Material.REDSTONE);
				pec.remove(Material.ITEM_FRAME);
				pec.remove(Material.FIREWORK);
				pec.remove(Material.PAINTING);
				pec.remove(Material.LAVA_BUCKET);
				pec.remove(Material.WATER_BUCKET);
				pec.remove(Material.BUCKET);
				pec.remove(Material.GOLD_SWORD);
				pec.remove(Material.ENCHANTED_BOOK);
				pec.remove(Material.STONE);
				pec.remove(Material.GRASS);
				pec.remove(Material.ARROW);
				pec.remove(Material.BOW);
				pec.remove(Material.EXP_BOTTLE);
				pec.remove(Material.LEASH);
				pec.remove(Material.WOOD_AXE);
				pec.remove(Material.STONE_AXE);
				pec.remove(Material.IRON_AXE);
				pec.remove(Material.DIAMOND_AXE);
				pec.remove(Material.GOLD_AXE);
				pec.remove(Material.BRICK);
				pec.remove(Material.WOOD_PICKAXE);
				pec.remove(Material.STONE_PICKAXE);
				pec.remove(Material.IRON_PICKAXE);
				pec.remove(Material.DIAMOND_PICKAXE);
				pec.remove(Material.GOLD_PICKAXE);
				pec.remove(Material.FIREWORK_CHARGE);
			}
		}
	}

	public static void sendToServer(Player player, String server) {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		try {
			out.writeUTF("Connect");
			out.writeUTF(server);
			player.sendPluginMessage(
					Bukkit.getPluginManager().getPlugin("MagicAssistant"),
					"BungeeCord", b.toByteArray());
		} catch (Exception e) {
			player.sendMessage(ChatColor.RED
					+ "Sorry! It looks like something went wrong! It's probably out fault. We will try to fix it as soon as possible!");
		}
	}

	public static YamlConfiguration config() {
		return YamlConfiguration.loadConfiguration(new File(
				"plugins/MagicAssistant/config.yml"));
	}

	public static boolean isInPermGroup(Player player, String group) {
		String[] groups = WorldGuardPlugin.inst().getGroups(player);
		for (int i = 0; i < groups.length; i++) {
			if (groups[i].toLowerCase().equals(group.toLowerCase())) {
				return true;
			}
		}
		return false;
	}
}