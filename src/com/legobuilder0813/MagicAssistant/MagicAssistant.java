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
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.legobuilder0813.MagicAssistant.Commands.Command_delay;
import com.legobuilder0813.MagicAssistant.Commands.Command_vanish;
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
		pm.registerEvents(new ScoreboardClass(this), this);
		pm.registerEvents(new PlayerJoinAndLeave(this), this);
		getConfig().options().copyDefaults(true);
		Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		saveConfig();
		getLogger().info("[MagicAssistant] Magic Assistant is ready to help!");
	}

	public WorldEditPlugin getWE() {
		return we;
	}

	public WorldGuardPlugin getWG() {
		return wg;
	}

	public void onDisable() {
		getLogger().info(
				"[MagicAssistant] Magic Assistant is taking a coffee break.");
	}

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		if (label.equalsIgnoreCase("vanish") || label.equalsIgnoreCase("v")) {
			Command_vanish.execute(sender, label, args);
			return true;
		} else if (label.equalsIgnoreCase("joinarcade")) {
			if (!(sender instanceof Player)) {
				if (args.length == 1) {
					String pname = args[0];
					sendToServer(Bukkit.getPlayer(pname), "Arcade");
					return true;
				}
			} else {
				sender.sendMessage(ChatColor.WHITE
						+ "Unknown command. Type \"/help\" for help.");
			}
		} else if (label.equalsIgnoreCase("delay")) {
			Command_delay.execute(sender, label, args);
			return true;
		} else if (label.equalsIgnoreCase("tot")) {
			if (!(sender instanceof Player)) {
				if (args.length == 4) {
					if (args[0].equalsIgnoreCase("star")) {
						if (isInt(args[1]) && isInt(args[2]) && isInt(args[3])) {
							World world = Bukkit.getOnlinePlayers()[0]
									.getWorld();
							int x = Integer.parseInt(args[1]);
							int y = Integer.parseInt(args[2]);
							int z = Integer.parseInt(args[3]);
							Location loc = new Location(world, x, y, z);
							/*
							 * Firework fw = world.spawn(loc, Firework.class);
							 * ((
							 * CraftWorld)world).getHandle().broadcastEntityEffect
							 * (((CraftFirework)fw).getHandle(), (byte)17);
							 * ((CraftFirework)fw).getHandle().die();
							 */
							try {
								ParticleEffectClass.playFirework(world, loc);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		} else if (label.equalsIgnoreCase("stitch")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if (args.length == 0) {
					player.sendMessage(ChatColor.BLUE
							+ "Stitch's Great Escape "
							+ ChatColor.GOLD
							+ "v"
							+ Bukkit.getPluginManager()
									.getPlugin(this.getName()).getDescription()
									.getVersion() + " " + ChatColor.AQUA
							+ "by: Legobuilder0813");
					player.sendMessage(ChatColor.BLUE
							+ "/stitch leave = leave the show");
					return true;
				}
				if (args.length == 1) {
					if (args[0].equalsIgnoreCase("leave")) {
						StitchEscape.leaveShow(player);
						return true;
					}
					if (args[0].equalsIgnoreCase("lock")) {
						if (player.hasPermission("stitch.lock")) {
							StitchEscape.lockShow(player);
							return true;
						} else {
							player.sendMessage(ChatColor.RED
									+ "You don't have permission to use this command!");
						}
					}
					if (args[0].equalsIgnoreCase("setlocation")) {
						if (player.hasPermission("stitch.setlocation")) {
							Location loc = player.getLocation();
							double x = loc.getX();
							double y = loc.getY();
							double z = loc.getZ();
							double yaw = loc.getYaw();
							double pitch = loc.getPitch();
							int num = getConfig().getInt("stitch.amount") + 1;
							getConfig().set("stitch." + num + ".x", x);
							getConfig().set("stitch." + num + ".y", y);
							getConfig().set("stitch." + num + ".z", z);
							getConfig().set("stitch." + num + ".yaw", yaw);
							getConfig().set("stitch." + num + ".pitch", pitch);
							getConfig().set("stitch." + num + ".inuse", false);
							getConfig().set("stitch.amount", num);
							saveConfig();
							player.sendMessage(ChatColor.BLUE
									+ "Location number " + ChatColor.GOLD + num
									+ ChatColor.BLUE + " has been set!");
						}
						return true;
					}
					if (args[0].equalsIgnoreCase("eject")) {
						if (player.hasPermission("stitch.lock")) {
							for (Player tp : Bukkit.getOnlinePlayers()) {
								if (watching.containsKey(tp)) {
									watching.remove(tp);
									tp.sendMessage(ChatColor.BLUE
											+ "Stitch's Great Escape has ended. You are free to move again.");
								}
							}
							int amount = getConfig().getInt("stitch.amount");
							for (int i = 1; i <= amount; i++) {
								if (getConfig().getBoolean(
										"stitch." + i + ".inuse") != false) {
									getConfig().set("stitch." + i + ".inuse",
											false);
									saveConfig();
								}
							}
							player.sendMessage(ChatColor.BLUE
									+ "All guests have been ejected. The show is ready to run again!");
						} else {
							player.sendMessage(ChatColor.RED
									+ "You don't have permission to use this command!");
						}
					}
				}
				return true;
			}
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("dark")) {
					for (Player tp : Bukkit.getOnlinePlayers()) {
						if (watching.containsKey(tp)) {
							tp.addPotionEffect(new PotionEffect(
									PotionEffectType.BLINDNESS, 10000, 1));
						}
					}
					return true;
				}
				if (args[0].equalsIgnoreCase("light")) {
					for (Player tp : Bukkit.getOnlinePlayers()) {
						if (watching.containsKey(tp)
								&& tp.hasPotionEffect(PotionEffectType.BLINDNESS)) {
							tp.removePotionEffect(PotionEffectType.BLINDNESS);
						}
					}
					return true;
				}
				if (args[0].equalsIgnoreCase("burp")) {
					for (Player tp : Bukkit.getOnlinePlayers()) {
						if (watching.containsKey(tp)) {
							tp.addPotionEffect(new PotionEffect(
									PotionEffectType.CONFUSION, 5, 1));
						}
					}
					return true;
				}
				return true;
			}
			if (args.length == 2) {
				if (args[0].equalsIgnoreCase("add")) {
					OfflinePlayer tp = Bukkit.getOfflinePlayer(args[1]);
					if (tp.isOnline()) {
						Player target = Bukkit.getPlayer(args[1]);
						StitchEscape.joinShow(target);
					}
					return true;
				}
				if (args[0].equalsIgnoreCase("effect")) {
					if (args[1].equalsIgnoreCase("spit")) {
						Bukkit.getWorld(
								Bukkit.getOnlinePlayers()[0].getWorld()
										.getName())
								.playEffect(
										new Location(
												Bukkit.getOnlinePlayers()[0]
														.getWorld(),
												-1742, 64, -1211),
										Effect.POTION_BREAK, 6);
						Bukkit.getWorld(
								Bukkit.getOnlinePlayers()[0].getWorld()
										.getName())
								.playEffect(
										new Location(
												Bukkit.getOnlinePlayers()[0]
														.getWorld(),
												-1745, 64, -1208),
										Effect.POTION_BREAK, 6);
						Bukkit.getWorld(
								Bukkit.getOnlinePlayers()[0].getWorld()
										.getName())
								.playEffect(
										new Location(
												Bukkit.getOnlinePlayers()[0]
														.getWorld(),
												-1748, 64, -1211),
										Effect.POTION_BREAK, 6);
						Bukkit.getWorld(
								Bukkit.getOnlinePlayers()[0].getWorld()
										.getName())
								.playEffect(
										new Location(
												Bukkit.getOnlinePlayers()[0]
														.getWorld(),
												-1745, 64, -1214),
										Effect.POTION_BREAK, 6);
						return true;
					}
					if (args[1].equalsIgnoreCase("burp")) {
						Bukkit.getWorld(
								Bukkit.getOnlinePlayers()[0].getWorld()
										.getName())
								.playEffect(
										new Location(
												Bukkit.getOnlinePlayers()[0]
														.getWorld(),
												-1742, 64, -1211),
										Effect.POTION_BREAK, 4);
						Bukkit.getWorld(
								Bukkit.getOnlinePlayers()[0].getWorld()
										.getName())
								.playEffect(
										new Location(
												Bukkit.getOnlinePlayers()[0]
														.getWorld(),
												-1745, 64, -1208),
										Effect.POTION_BREAK, 4);
						Bukkit.getWorld(
								Bukkit.getOnlinePlayers()[0].getWorld()
										.getName())
								.playEffect(
										new Location(
												Bukkit.getOnlinePlayers()[0]
														.getWorld(),
												-1748, 64, -1211),
										Effect.POTION_BREAK, 4);
						Bukkit.getWorld(
								Bukkit.getOnlinePlayers()[0].getWorld()
										.getName())
								.playEffect(
										new Location(
												Bukkit.getOnlinePlayers()[0]
														.getWorld(),
												-1745, 64, -1214),
										Effect.POTION_BREAK, 4);
					}
				}
			}
		} else if (label.equalsIgnoreCase("invcheck")) {
			if (!(sender instanceof Player)) {
				checkInventories();
				Bukkit.broadcast(ChatColor.GOLD
						+ "All banned items were removed by "
						+ ChatColor.DARK_GREEN + "Utilidors",
						"magicassistant.invcheck");
			} else {
				Player player = (Player) sender;
				checkInventories();
				Bukkit.broadcast(ChatColor.GOLD
						+ "All banned items were removed by "
						+ ChatColor.DARK_GREEN + player.getName(),
						"magicassistant.invcheck");
			}
		} else if (label.equalsIgnoreCase("sethub")) {
			Player player = (Player) sender;
			if (player.isOp()) {
				double x = player.getLocation().getX();
				double y = player.getLocation().getY();
				double z = player.getLocation().getZ();
				double yaw = player.getLocation().getYaw();
				double pitch = player.getLocation().getPitch();
				getConfig().set("hub.x", x);
				getConfig().set("hub.y", y);
				getConfig().set("hub.z", z);
				getConfig().set("hub.yaw", yaw);
				getConfig().set("hub.pitch", pitch);
				getConfig().set("hubworld", player.getWorld().getName());
				saveConfig();
				player.sendMessage(ChatColor.DARK_AQUA
						+ "The hub location has been set!");
			} else {
				player.sendMessage(ChatColor.RED
						+ "You do not have permission to use this command!");
			}
		} else if (label.equalsIgnoreCase("hub")) {
			double x = getConfig().getDouble("hub.x");
			double y = getConfig().getDouble("hub.y");
			double z = getConfig().getDouble("hub.z");
			float yaw = getConfig().getInt("hub.yaw");
			float pitch = getConfig().getInt("hub.pitch");
			String world = getConfig().getString("hubworld");
			if (args.length == 0) {
				Player player = (Player) sender;
				player.sendMessage(ChatColor.DARK_AQUA
						+ "You have arrived at the hub!");
				player.teleport(new Location(Bukkit.getWorld(world), x, y, z,
						yaw, pitch));
				return true;
			} else if (args.length == 1) {
				Player player = (Player) sender;
				if (player.hasPermission("magicassistant.hub.otherplayer")) {
					boolean proceed = false;
					Player targetPlayer = Bukkit.getOnlinePlayers()[0];
					for (Player tp : Bukkit.getOnlinePlayers()) {
						if (tp.getName().toLowerCase()
								.equals(args[0].toLowerCase())) {
							proceed = true;
						}
					}
					if (!proceed) {
						player.sendMessage(ChatColor.RED
								+ "That player isn't online!");
						return true;
					}
					targetPlayer.teleport(new Location(Bukkit.getWorld(world),
							x, y, z, yaw, pitch));
					player.sendMessage(ChatColor.DARK_AQUA
							+ targetPlayer.getName()
							+ " has arrived at the hub!");
					targetPlayer.sendMessage(ChatColor.DARK_AQUA
							+ "You have arrived at the hub!");
					return true;
				}
				return true;
			}
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
		} else if (label.equalsIgnoreCase("rp")) {
			sender.sendMessage(ChatColor.YELLOW
					+ "Get our resource pack with custom items, music, and more at this link:");
			sender.sendMessage(ChatColor.LIGHT_PURPLE
					+ getConfig().getString("rplink"));
		} else if (label.equalsIgnoreCase("mumble")) {
			Player player = (Player) sender;
			player.sendMessage(ChatColor.DARK_GREEN
					+ "----------------------------------------------------");
			player.sendMessage(ChatColor.DARK_AQUA + "Our mumble ip is: "
					+ getConfig().getString("mumble.ip"));
			player.sendMessage(ChatColor.DARK_AQUA + "Use the port: "
					+ getConfig().getString("mumble.port"));
			player.sendMessage(ChatColor.DARK_AQUA
					+ "Use your in-game-name for your username");
			player.sendMessage(ChatColor.DARK_AQUA
					+ "We do not have a password on our mumble.");
			player.sendMessage(ChatColor.AQUA
					+ "Here's the download link for mumble: "
					+ getConfig().getString("mumble.downloadlink"));
			player.sendMessage(ChatColor.DARK_GREEN
					+ "----------------------------------------------------");
		} else if (label.equalsIgnoreCase("save")) {
			Player player = (Player) sender;
			player.performCommand("save-all");
		} else if (label.equalsIgnoreCase("head")) {
			Player player = (Player) sender;
			if (getConfig().getString("headcmd").equals("false")) {
				player.sendMessage(ChatColor.RED + "That command is disabled.");
			} else if (getConfig().getString("headcmd").equals("true")) {
				Player pl = (Player) sender;
				PlayerInventory pi = pl.getInventory();
				if (args.length == 1) {
					ItemStack head = new ItemStack(Material.SKULL_ITEM, 1,
							(short) 3);
					SkullMeta headm = (SkullMeta) head.getItemMeta();
					headm.setOwner(args[0]);
					head.setItemMeta(headm);
					pi.addItem(head);
					pl.sendMessage(ChatColor.AQUA + "[MagicAssistant]"
							+ ChatColor.BLUE + " Enjoy your new head of "
							+ args[0] + "!");
				} else {
					pl.sendMessage(ChatColor.RED + "/head [playerhead]");
				}
			} else {
				getConfig().set("headcmd", "false");
				player.sendMessage(ChatColor.RED + "That command is disabled.");
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