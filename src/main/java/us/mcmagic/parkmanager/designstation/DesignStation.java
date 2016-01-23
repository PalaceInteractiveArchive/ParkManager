package us.mcmagic.parkmanager.designstation;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.mcmagic.mcmagiccore.chat.formattedmessage.FormattedMessage;

import java.util.*;

/**
 * Created by LukeSmalley on 5/18/2015.
 */
public class DesignStation {

    private static Map<UUID, TestTrackVehicle> playerVehicles = new HashMap<>();

    public static TestTrackVehicle getPlayerVehicle(UUID uuid) {
        if (playerVehicles.get(uuid) == null) {
            playerVehicles.put(uuid, new TestTrackVehicle());
        }
        return playerVehicles.get(uuid);
    }

    public static void removePlayerVehicle(UUID uuid) {
        try {
            playerVehicles.remove(uuid);
        } catch (Exception ignored) {
        }
    }

    public static ItemStack createCar = new ItemStack(Material.MINECART, 1);
    public static ItemStack createTruck = new ItemStack(Material.MINECART, 1);
    public static ItemStack createSmartcar = new ItemStack(Material.MINECART, 1);

    public static ItemStack nextButton = new ItemStack(Material.STAINED_CLAY, 1, (short) 5);
    public static ItemStack backButton = new ItemStack(Material.STAINED_CLAY, 1, (short) 14);

    public static ItemStack tallerButton = new ItemStack(Material.STAINED_CLAY, 1, (short) 9);
    public static ItemStack shorterButton = new ItemStack(Material.STAINED_CLAY, 1, (short) 9);
    public static ItemStack widerButton = new ItemStack(Material.STAINED_CLAY, 1, (short) 9);
    public static ItemStack thinnerButton = new ItemStack(Material.STAINED_CLAY, 1, (short) 9);

    public static ItemStack redButton = new ItemStack(Material.WOOL, 1, (short) 14);
    public static ItemStack darkGreenButton = new ItemStack(Material.WOOL, 1, (short) 13);
    public static ItemStack lightGreenButton = new ItemStack(Material.WOOL, 1, (short) 5);
    public static ItemStack yellowButton = new ItemStack(Material.WOOL, 1, (short) 4);
    public static ItemStack purpleButton = new ItemStack(Material.WOOL, 1, (short) 10);
    public static ItemStack magentaButton = new ItemStack(Material.WOOL, 1, (short) 2);
    public static ItemStack darkGreyButton = new ItemStack(Material.WOOL, 1, (short) 7);
    public static ItemStack lightGreyButton = new ItemStack(Material.WOOL, 1, (short) 8);
    public static ItemStack whiteButton = new ItemStack(Material.WOOL, 1, (short) 0);
    public static ItemStack darkBlueButton = new ItemStack(Material.WOOL, 1, (short) 11);
    public static ItemStack cyanButton = new ItemStack(Material.WOOL, 1, (short) 9);
    public static ItemStack lightBlueButton = new ItemStack(Material.WOOL, 1, (short) 3);

    public static ItemStack solarDriveEngine = new ItemStack(Material.STAINED_CLAY, 1, (short) 5);
    public static ItemStack fuelCellEngine = new ItemStack(Material.STAINED_CLAY, 1, (short) 5);
    public static ItemStack ecoElectricEngine = new ItemStack(Material.STAINED_CLAY, 1, (short) 5);
    public static ItemStack evHybridEngine = new ItemStack(Material.STAINED_CLAY, 1, (short) 9);
    public static ItemStack gasEngine = new ItemStack(Material.STAINED_CLAY, 1, (short) 2);
    public static ItemStack superChargedEngine = new ItemStack(Material.STAINED_CLAY, 1, (short) 2);
    public static ItemStack plasmaBurnerEngine = new ItemStack(Material.STAINED_CLAY, 1, (short) 2);

    //Power - Light Purple
    //Responsiveness - Aqua
    //Capability - Yellow
    //Efficiency - Light Green

    public static void initialize() {
        ItemMeta nbm = nextButton.getItemMeta();
        nbm.setDisplayName(ChatColor.GREEN + "Continue");
        nbm.setLore(Collections.singletonList(ChatColor.WHITE + "Click to continue to the next step."));
        nextButton.setItemMeta(nbm);

        ItemMeta bbm = backButton.getItemMeta();
        bbm.setDisplayName(ChatColor.DARK_RED + "Back");
        bbm.setLore(Collections.singletonList(ChatColor.WHITE + "Click to return to the previous step."));
        backButton.setItemMeta(bbm);


        ItemMeta ccm = createCar.getItemMeta();
        ccm.setDisplayName(ChatColor.DARK_GREEN + "Design a Car");
        ccm.setLore(Arrays.asList(ChatColor.WHITE + "Click to begin designing your own car.", ChatColor.LIGHT_PURPLE + "[+Power]", ChatColor.AQUA + "[+Responsiveness]"));
        createCar.setItemMeta(ccm);

        ItemMeta ctm = createTruck.getItemMeta();
        ctm.setDisplayName(ChatColor.DARK_GREEN + "Design a Truck");
        ctm.setLore(Arrays.asList(ChatColor.WHITE + "Click to begin designing your own truck.", ChatColor.YELLOW + "[+Capability]", ChatColor.LIGHT_PURPLE + "[+Power]", ChatColor.GREEN + "[-Efficiency]"));
        createTruck.setItemMeta(ctm);

        ItemMeta cscm = createSmartcar.getItemMeta();
        cscm.setDisplayName(ChatColor.DARK_GREEN + "Design a Smart Car");
        cscm.setLore(Arrays.asList(ChatColor.WHITE + "Click to begin designing your own eco-friendly car.", ChatColor.GREEN + "[+Efficiency]", ChatColor.LIGHT_PURPLE + "[-Power]"));
        createSmartcar.setItemMeta(cscm);


        ItemMeta tbm = tallerButton.getItemMeta();
        tbm.setDisplayName(ChatColor.WHITE + "Taller (Vertical)");
        tbm.setLore(Collections.singletonList(ChatColor.GRAY + "Click to make your vehicle taller."));
        tallerButton.setItemMeta(tbm);

        ItemMeta sbm = shorterButton.getItemMeta();
        sbm.setDisplayName(ChatColor.WHITE + "Shorter (Vertical)");
        sbm.setLore(Collections.singletonList(ChatColor.GRAY + "Click to make your vehicle less tall."));
        shorterButton.setItemMeta(sbm);

        ItemMeta wbm = widerButton.getItemMeta();
        wbm.setDisplayName(ChatColor.WHITE + "Longer (Horizontal)");
        wbm.setLore(Collections.singletonList(ChatColor.GRAY + "Click to make your vehicle longer."));
        widerButton.setItemMeta(wbm);

        ItemMeta thbm = thinnerButton.getItemMeta();
        thbm.setDisplayName(ChatColor.WHITE + "Shorter (Horizontal)");
        thbm.setLore(Collections.singletonList(ChatColor.GRAY + "Click to make your vehicle shorter."));
        thinnerButton.setItemMeta(thbm);


        ItemMeta crbm = redButton.getItemMeta();
        crbm.setDisplayName(ChatColor.DARK_RED + "Red");
        crbm.setLore(Collections.singletonList(ChatColor.GRAY + "Click to color your vehicle red."));
        redButton.setItemMeta(crbm);

        ItemMeta cdgbm = darkGreenButton.getItemMeta();
        cdgbm.setDisplayName(ChatColor.DARK_GREEN + "Dark Green");
        cdgbm.setLore(Collections.singletonList(ChatColor.GRAY + "Click to color your vehicle dark green."));
        darkGreenButton.setItemMeta(cdgbm);

        ItemMeta clgbm = lightGreenButton.getItemMeta();
        clgbm.setDisplayName(ChatColor.GREEN + "Light Green");
        clgbm.setLore(Collections.singletonList(ChatColor.GRAY + "Click to color your vehicle light green."));
        lightGreenButton.setItemMeta(clgbm);

        ItemMeta cybm = yellowButton.getItemMeta();
        cybm.setDisplayName(ChatColor.YELLOW + "Yellow");
        cybm.setLore(Collections.singletonList(ChatColor.GRAY + "Click to color your vehicle yellow."));
        yellowButton.setItemMeta(cybm);

        ItemMeta cpbm = purpleButton.getItemMeta();
        cpbm.setDisplayName(ChatColor.DARK_PURPLE + "Purple");
        cpbm.setLore(Collections.singletonList(ChatColor.GRAY + "Click to color your vehicle purple."));
        purpleButton.setItemMeta(cpbm);

        ItemMeta cmbm = magentaButton.getItemMeta();
        cmbm.setDisplayName(ChatColor.LIGHT_PURPLE + "Magenta");
        cmbm.setLore(Collections.singletonList(ChatColor.GRAY + "Click to color your vehicle magenta."));
        magentaButton.setItemMeta(cmbm);

        ItemMeta cdgrbm = darkGreyButton.getItemMeta();
        cdgrbm.setDisplayName(ChatColor.DARK_GRAY + "Dark Grey");
        cdgrbm.setLore(Collections.singletonList(ChatColor.GRAY + "Click to color your vehicle dark grey."));
        darkGreyButton.setItemMeta(cdgrbm);

        ItemMeta clgrbm = lightGreyButton.getItemMeta();
        clgrbm.setDisplayName(ChatColor.GRAY + "Light Grey");
        clgrbm.setLore(Collections.singletonList(ChatColor.GRAY + "Click to color your vehicle light grey."));
        lightGreyButton.setItemMeta(clgrbm);

        ItemMeta cwbm = whiteButton.getItemMeta();
        cwbm.setDisplayName(ChatColor.WHITE + "White");
        cwbm.setLore(Collections.singletonList(ChatColor.GRAY + "Click to color your vehicle white."));
        whiteButton.setItemMeta(cwbm);

        ItemMeta cdbbm = darkBlueButton.getItemMeta();
        cdbbm.setDisplayName(ChatColor.DARK_BLUE + "Dark Blue");
        cdbbm.setLore(Collections.singletonList(ChatColor.GRAY + "Click to color your vehicle dark blue."));
        darkBlueButton.setItemMeta(cdbbm);

        ItemMeta ccbm = cyanButton.getItemMeta();
        ccbm.setDisplayName(ChatColor.DARK_AQUA + "Aqua");
        ccbm.setLore(Collections.singletonList(ChatColor.GRAY + "Click to color your vehicle aqua."));
        cyanButton.setItemMeta(ccbm);

        ItemMeta clbbm = lightBlueButton.getItemMeta();
        clbbm.setDisplayName(ChatColor.BLUE + "Light Blue");
        clbbm.setLore(Collections.singletonList(ChatColor.GRAY + "Click to color your vehicle light blue."));
        lightBlueButton.setItemMeta(clbbm);


        ItemMeta sdem = solarDriveEngine.getItemMeta();
        sdem.setDisplayName(ChatColor.GREEN + "Solar Drive Engine");
        sdem.setLore(Arrays.asList(ChatColor.GRAY + "Click to power your vehicle with the sun.", ChatColor.LIGHT_PURPLE + "[-2 Power]", ChatColor.GREEN + "[+3 Efficiency]"));
        solarDriveEngine.setItemMeta(sdem);

        ItemMeta fcem = fuelCellEngine.getItemMeta();
        fcem.setDisplayName(ChatColor.GREEN + "Fuel Cell Engine");
        fcem.setLore(Arrays.asList(ChatColor.GRAY + "Click to power your vehicle with a fuel cell engine.", ChatColor.LIGHT_PURPLE + "[-1 Power]", ChatColor.GREEN + "[+2 Efficiency]"));
        fuelCellEngine.setItemMeta(fcem);

        ItemMeta eeem = ecoElectricEngine.getItemMeta();
        eeem.setDisplayName(ChatColor.GREEN + "Eco-Electric Engine");
        eeem.setLore(Arrays.asList(ChatColor.GRAY + "Click to power your vehicle with electricity.", ChatColor.LIGHT_PURPLE + "[-1 Power]", ChatColor.GREEN + "[+1 Efficiency]"));
        ecoElectricEngine.setItemMeta(eeem);

        ItemMeta evem = evHybridEngine.getItemMeta();
        evem.setDisplayName(ChatColor.WHITE + "EV Hybrid Engine");
        evem.setLore(Arrays.asList(ChatColor.GRAY + "Click to power your vehicle with a hybrid engine.", ChatColor.AQUA + "[Balanced Power and Efficiency]"));
        evHybridEngine.setItemMeta(evem);

        ItemMeta gem = gasEngine.getItemMeta();
        gem.setDisplayName(ChatColor.LIGHT_PURPLE + "Gasoline Engine");
        gem.setLore(Arrays.asList(ChatColor.GRAY + "Click to power your vehicle with gasoline.", ChatColor.LIGHT_PURPLE + "[+1 Power]", ChatColor.GREEN + "[-1 Efficiency]"));
        gasEngine.setItemMeta(gem);

        ItemMeta scem = superChargedEngine.getItemMeta();
        scem.setDisplayName(ChatColor.LIGHT_PURPLE + "Supercharged Engine");
        scem.setLore(Arrays.asList(ChatColor.GRAY + "Click to power your vehicle with a supercharged engine.", ChatColor.LIGHT_PURPLE + "[+2 Power]", ChatColor.GREEN + "[-1 Efficiency]"));
        superChargedEngine.setItemMeta(scem);

        ItemMeta pbem = plasmaBurnerEngine.getItemMeta();
        pbem.setDisplayName(ChatColor.LIGHT_PURPLE + "Plasma Burner Engine");
        pbem.setLore(Arrays.asList(ChatColor.GRAY + "Click to power your vehicle with pure speed.", ChatColor.LIGHT_PURPLE + "[+3 Power]", ChatColor.GREEN + "[-2 Efficiency]"));
        plasmaBurnerEngine.setItemMeta(pbem);
    }

    public static ItemStack getPlayerVehicleItem(UUID uuid) {
        TestTrackVehicle vehicle = getPlayerVehicle(uuid);

        ItemStack item = new ItemStack(Material.MINECART, 1);
        ItemMeta itemMeta = item.getItemMeta();

        itemMeta.setDisplayName(ChatColor.DARK_GREEN + "Your Vehicle:");

        List<String> lore = new ArrayList<>();

        lore.add(ChatColor.LIGHT_PURPLE + "Power: " + Integer.toString(Math.max(1, Math.min(vehicle.getPowerScore(), 99))));
        lore.add(ChatColor.AQUA + "Responsiveness: " + Integer.toString(Math.max(1, Math.min(vehicle.getResponsivenessScore(), 99))));
        lore.add(ChatColor.YELLOW + "Capability: " + Integer.toString(Math.max(1, Math.min(vehicle.getCapabilityScore(), 99))));
        lore.add(ChatColor.GREEN + "Efficiency: " + Integer.toString(Math.max(1, Math.min(vehicle.getEfficiencyScore(), 99))));

        String template = "";

        if (vehicle.type == TestTrackVehicle.carType) {
            template = "&09$2w&07\n*&07$6w&05\n&01$w97&01\n^$99w\n$3o2$w8o2$3\n&03o2&0w8o2&03";
        } else if (vehicle.type == TestTrackVehicle.truckType) {
            template = "&08$2w&08\n*&06$4w&08\n&01$w98\n^$99w\n$3o2$w8o2$3\n&03o2&0w8o2&03";
        } else if (vehicle.type == TestTrackVehicle.ecoCarType) {
            template = "&08$2w&05\n*&06$6w&03\n&01$w94&01\n^$96w\n$2o2$w7o2$2\n&02o2&0w7o2&02";
        }

        int widthOffset = vehicle.getWidthOffset();

        try {
            for (String line : template.split("\n")) {
                String loreLine = "";
                boolean repeatLine = false;
                boolean repeatLineOdd = false;

                for (int i = 0; i < line.length(); i++) {
                    char c = line.charAt(i);
                    if (Character.isDigit(c)) {
                        for (int ii = 0; ii < Integer.parseInt(Character.toString(c)); ii++) {
                            loreLine += "█";
                        }
                    } else if (c == 'w') {
                        for (int ii = 0; ii < widthOffset + vehicle.width; ii++) {
                            loreLine += "█";
                        }
                    } else if (c == '&') {
                        loreLine += "§";
                        loreLine += line.charAt(i + 1);
                        i++;
                    } else if (c == '$') {
                        loreLine += vehicle.color;
                    } else if (c == '*') {
                        repeatLine = true;
                    } else if (c == '^') {
                        repeatLineOdd = true;
                    } else if (c == 'o') {
                        if (vehicle.color.equals(ChatColor.DARK_GRAY)) {
                            loreLine += ChatColor.GRAY;
                        } else {
                            loreLine += ChatColor.DARK_GRAY;
                        }
                    }
                }

                if (repeatLine) {
                    for (int i = 0; i < (vehicle.height + 2) / 2; i++) {
                        lore.add(loreLine);
                    }
                } else if (repeatLineOdd) {
                    for (int i = 0; i < (vehicle.height + 2) / 2; i++) {
                        lore.add(loreLine);
                    }
                    if ((vehicle.height + 2) % 2 == 1) {
                        lore.add(loreLine);
                    }
                } else {
                    lore.add(loreLine);
                }
            }
        } catch (Exception ex) {
            lore.add("Vehicle render failed.");
        }

        itemMeta.setLore(lore);

        item.setItemMeta(itemMeta);
        return item;
    }


    public static void openPickModelInventory(Player player) {
        Inventory designStationInventory = Bukkit.createInventory(player, 27, ChatColor.BLUE + "Pick Model");
        designStationInventory.setItem(12, createCar);
        designStationInventory.setItem(13, createTruck);
        designStationInventory.setItem(14, createSmartcar);
        player.openInventory(designStationInventory);
    }

    public static void openPickSizeAndColorInventory(Player player) {
        Inventory designStationInventory = Bukkit.createInventory(player, 54, ChatColor.BLUE + "Pick Size/Color");

        designStationInventory.setItem(1, nextButton);
        designStationInventory.setItem(0, backButton);

        designStationInventory.setItem(19, tallerButton);
        designStationInventory.setItem(37, shorterButton);
        designStationInventory.setItem(29, widerButton);
        designStationInventory.setItem(27, thinnerButton);

        designStationInventory.setItem(15, redButton);
        designStationInventory.setItem(16, darkGreenButton);
        designStationInventory.setItem(17, lightGreenButton);
        designStationInventory.setItem(24, yellowButton);
        designStationInventory.setItem(25, purpleButton);
        designStationInventory.setItem(26, magentaButton);
        designStationInventory.setItem(33, darkGreyButton);
        designStationInventory.setItem(34, lightGreyButton);
        designStationInventory.setItem(35, whiteButton);
        designStationInventory.setItem(42, darkBlueButton);
        designStationInventory.setItem(43, cyanButton);
        designStationInventory.setItem(44, lightBlueButton);

        designStationInventory.setItem(31, getPlayerVehicleItem(player.getUniqueId()));

        player.openInventory(designStationInventory);
    }

    public static void openPickEngineInventory(Player player) {
        Inventory designStationInventory = Bukkit.createInventory(player, 36, ChatColor.BLUE + "Pick Engine");

        designStationInventory.setItem(0, backButton);

        designStationInventory.setItem(19, solarDriveEngine);
        designStationInventory.setItem(20, fuelCellEngine);
        designStationInventory.setItem(21, ecoElectricEngine);
        designStationInventory.setItem(22, evHybridEngine);
        designStationInventory.setItem(23, gasEngine);
        designStationInventory.setItem(24, superChargedEngine);
        designStationInventory.setItem(25, plasmaBurnerEngine);

        player.openInventory(designStationInventory);
    }

    public static void showStats(Player player) {
        if (!playerVehicles.containsKey(player.getUniqueId())) {
            return;
        }
        ItemStack item = getPlayerVehicleItem(player.getUniqueId());
        TestTrackVehicle vehicle = playerVehicles.remove(player.getUniqueId());
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.BLUE + "Design Score");
        List<String> lore = meta.getLore();
        lore.add(" ");
        int score = vehicle.getPowerScore() + vehicle.getEfficiencyScore() + vehicle.getCapabilityScore() +
                vehicle.getResponsivenessScore();
        lore.add(ChatColor.AQUA + "Final Score: " + score);
        meta.setLore(lore);
        item.setItemMeta(meta);
        FormattedMessage msg = new FormattedMessage("Hover this message to see your score!\n");
        msg.color(ChatColor.YELLOW);
        msg.style(ChatColor.BOLD);
        msg.itemTooltip(item);
        msg.send(player);
    }

    private static int genRandom(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }
}
