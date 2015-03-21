package us.mcmagic.magicassistant.show;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import org.bukkit.*;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import us.mcmagic.magicassistant.show.actions.*;
import us.mcmagic.magicassistant.utils.MathUtil;
import us.mcmagic.magicassistant.utils.WorldUtil;
import us.mcmagic.mcmagiccore.particles.ParticleEffect;
import us.mcmagic.mcmagiccore.title.TitleObject;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class Show {
    private World _world;

    private Location _loc;
    private int _radius = 75;

    private long _startTime;

    private HashSet<ShowAction> _actions;

    private HashMap<String, FireworkEffect> _effectMap;

    private HashMap<String, String> _invalidLines;

    private HashMap<String, ShowNPC> _npc;
    private int _npcTick = 0;

    public Show(JavaPlugin plugin, File file) {
        _world = Bukkit.getWorlds().get(0);

        _effectMap = new HashMap<>();
        _invalidLines = new HashMap<>();
        _npc = new HashMap<>();

        LoadActions(file);

        _startTime = System.currentTimeMillis();
    }

    private void LoadActions(File file) {
        _actions = new HashSet<>();
        String strLine = "";

        try {
            FileInputStream fstream = new FileInputStream(file);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            // Parse Lines
            while ((strLine = br.readLine()) != null) {
                if (strLine.length() == 0 || strLine.startsWith("#"))
                    continue;

                String[] tokens = strLine.split("\\s+");

                if (tokens.length < 3) {
                    System.out.println("Invalid Show Line [" + strLine + "]");
                }

                // Set Show location
                if (tokens[1].equals("Location")) {
                    Location loc = WorldUtil.strToLoc(_world.getName() + ","
                            + tokens[2]);

                    if (loc == null) {
                        _invalidLines.put(strLine, "Invalid Location Line");
                        continue;
                    }

                    _loc = loc;
                    continue;
                }

                // Set Text Radius
                if (tokens[1].equals("TextRadius")) {
                    try {
                        _radius = Integer.parseInt(tokens[2]);
                    } catch (Exception e) {
                        _invalidLines.put(strLine, "Invalid Text Radius");
                    }

                    continue;
                }

                // Load Firework Effects
                if (tokens[0].equals("Effect")) {
                    FireworkEffect effect = ParseEffect(tokens[2]);

                    if (effect == null) {
                        _invalidLines.put(strLine, "Invalid Effect Line");
                        continue;
                    }

                    _effectMap.put(tokens[1], effect);
                    continue;
                }

                // Get time
                String[] timeToks = tokens[0].split("_");

                long time = 0;
                for (String timeStr : timeToks) {
                    time += (long) (Double.parseDouble(timeStr) * 1000);
                }

                // Text
                if (tokens[1].contains("Text")) {
                    String text = "";
                    for (int i = 2; i < tokens.length; i++)
                        text += tokens[i] + " ";
                    if (text.length() > 1)
                        text = text.substring(0, text.length() - 1);
                    _actions.add(new TextAction(this, time, text));
                }

                // Music
                else if (tokens[1].contains("Music")) {
                    try {
                        int id = Integer.parseInt(tokens[2]);

                        _actions.add(new MusicAction(this, time, id));
                    } catch (Exception e) {
                        _invalidLines.put(strLine, "Invalid Material");
                        continue;
                    }
                }

                // Pulse
                else if (tokens[1].contains("Pulse")) {
                    Location loc = WorldUtil.strToLoc(_world.getName() + ","
                            + tokens[2]);
                    if (loc == null) {
                        _invalidLines.put(strLine, "Invalid Location");
                        continue;
                    }

                    _actions.add(new PulseAction(this, time, loc));
                }

                // Lightning
                else if (tokens[1].contains("Lightning")) {
                    Location loc = WorldUtil.strToLoc(_world.getName() + ","
                            + tokens[2]);

                    if (loc == null) {
                        _invalidLines.put(strLine, "Invalid Location");
                        continue;
                    }

                    _actions.add(new LightningAction(this, time, loc));
                }
                // NPC Spawn
                else if (tokens[1].contains("NPC")) {
                    // 0 NPC Spawn Name x,y,z Type MaterialInHand
                    if (tokens.length < 4) {
                        _invalidLines.put(strLine, "Invalid NPC Line");
                        continue;
                    }

                    String name = tokens[3];

                    // Spawn
                    if (tokens[2].contains("Spawn")) {
                        if (tokens.length < 5) {
                            _invalidLines
                                    .put(strLine, "Invalid NPC Spawn Line");
                            continue;
                        }

                        // type
                        EntityType type;
                        if (tokens.length >= 6) {
                            try {
                                type = EntityType.valueOf(tokens[5]);
                            } catch (Exception e) {
                                _invalidLines.put(strLine,
                                        "Invalid NPC Spawn Line: Entity Type");
                                continue;
                            }
                        } else {
                            type = EntityType.SKELETON;
                        }

                        Material holding = null;
                        if (tokens.length >= 7) {
                            try {
                                holding = Material.valueOf(tokens[6]);
                            } catch (Exception e) {
                                _invalidLines.put(strLine,
                                        "Invalid NPC Spawn Line: Item In Hand");
                                continue;
                            }
                        }

                        // Loc
                        Location loc = WorldUtil.strToLoc(_world.getName()
                                + "," + tokens[4]);

                        if (loc == null) {
                            _invalidLines.put(strLine, "Invalid Location");
                            continue;
                        }

                        // Add
                        _actions.add(new NPCSpawnAction(this, time, name, loc,
                                type, holding));
                    }

                    // Remove
                    else if (tokens[2].contains("Remove")) {
                        _actions.add(new NPCRemoveAction(this, time, name));
                    }

                    // Move
                    else if (tokens[2].contains("Move")) {
                        if (tokens.length < 5) {
                            _invalidLines.put(strLine, "Invalid NPC Line");
                            continue;
                        }

                        // Speed
                        float speed = 1f;
                        if (tokens.length >= 6) {
                            try {
                                speed = Float.valueOf(tokens[5]);
                            } catch (Exception e) {
                                _invalidLines.put(strLine,
                                        "Invalid NPC Spawn Line");
                                continue;
                            }
                        }

                        // Loc
                        Location loc = WorldUtil.strToLoc(_world.getName()
                                + "," + tokens[4]);

                        if (loc == null) {
                            _invalidLines.put(strLine, "Invalid Location");
                            continue;
                        }

                        _actions.add(new NPCMoveAction(this, time, name, loc,
                                speed));
                    }
                }

                // Block
                else if (tokens[1].contains("Block")) {
                    Location loc = WorldUtil.strToLoc(_world.getName() + ","
                            + tokens[3]);

                    if (loc == null) {
                        _invalidLines.put(strLine, "Invalid Location");
                        continue;
                    }

                    String[] list;
                    if (tokens[2].contains(":")) {
                        list = tokens[2].split(":");
                    } else {
                        list = null;
                    }
                    try {
                        int id;
                        byte data;
                        if (list != null) {
                            id = Integer.parseInt(list[0]);
                            data = Byte.parseByte(list[1]);
                        } else {
                            id = Integer.parseInt(tokens[2]);
                            data = (byte) 0;
                        }
                        _actions.add(new BlockAction(this, time, loc, id, data));
                    } catch (Exception e) {
                        _invalidLines.put(strLine,
                                "Invalid Block ID or Block data");
                        continue;
                    }
                }

                // Firework
                else if (tokens[1].contains("Firework")) {
                    if (tokens.length != 7) {
                        _invalidLines.put(strLine,
                                "Invalid Firework Line Length");
                        continue;
                    }

                    // location
                    Location loc = WorldUtil.strToLoc(_world.getName() + ","
                            + tokens[2]);
                    if (loc == null) {
                        _invalidLines.put(strLine, "Invalid Location");
                        continue;
                    }

                    // Effect List
                    ArrayList<FireworkEffect> effectList = new ArrayList<>();
                    String[] effects = tokens[3].split(",");
                    for (String effect : effects) {
                        if (_effectMap.containsKey(effect)) {
                            effectList.add(_effectMap.get(effect));
                        }
                    }
                    if (effectList.isEmpty()) {
                        _invalidLines.put(strLine, "Invalid Effects");
                        continue;
                    }

                    // Power
                    int power;
                    try {
                        power = Integer.parseInt(tokens[4]);

                        if (power < 0 || power > 5) {
                            _invalidLines.put(strLine, "Power too High/Low");
                            continue;
                        }
                    } catch (Exception e) {
                        _invalidLines.put(strLine, "Invalid Power");
                        continue;
                    }

                    // Direction
                    Vector dir;
                    try {
                        String[] coords = tokens[5].split(",");
                        dir = new Vector(Double.parseDouble(coords[0]),
                                Double.parseDouble(coords[1]),
                                Double.parseDouble(coords[2]));
                    } catch (Exception e) {
                        _invalidLines.put(strLine, "Invalid Direction");
                        continue;
                    }

                    // Dir Power
                    double dirPower;
                    try {
                        dirPower = Double.parseDouble(tokens[6]);

                        if (dirPower < 0 || dirPower > 10) {
                            _invalidLines.put(strLine,
                                    "Direction Power too High/Low");
                            continue;
                        }
                    } catch (Exception e) {
                        _invalidLines.put(strLine, "Invalid Direction Power");
                        continue;
                    }

                    _actions.add(new FireworkAction(this, time, loc,
                            effectList, power, dir, dirPower));
                }

                // Schematic
                else if (tokens[1].contains("Schematic")) {
                    // http://goo.gl/SAzALY
                    if (isInt(tokens[3]) && isInt(tokens[4])
                            && isInt(tokens[5])) {
                        int x = Integer.parseInt(tokens[3]);
                        int y = Integer.parseInt(tokens[4]);
                        int z = Integer.parseInt(tokens[5]);
                        Location pasteloc = new Location(
                                Bukkit.getWorld(tokens[6]), x, y, z);
                        WorldEditPlugin wep = (WorldEditPlugin) Bukkit
                                .getPluginManager().getPlugin("WorldEdit");
                        File schemfile = new File(wep.getDataFolder().getPath()
                                + "/schematics/" + tokens[2] + ".schematic");
                        boolean noAir;
                        noAir = !tokens[7].toLowerCase().contains("false");
                        _actions.add(new SchematicAction(this, time, pasteloc,
                                schemfile, noAir));
                    }
                    // 0 Schematic filename x y z world true/false
                    continue;
                } else if (tokens[1].contains("Fountain")) {
                    Location loc = WorldUtil.strToLoc(_world.getName() + ","
                            + tokens[4]);
                    Double[] values = WorldUtil.strToDoubleList(_world
                            .getName() + "," + tokens[5]);
                    double duration = Double.parseDouble(tokens[3]);
                    String[] list;
                    if (tokens[2].contains(":")) {
                        list = tokens[2].split(":");
                    } else {
                        list = null;
                    }
                    try {
                        int type;
                        byte data;
                        if (list != null) {
                            type = Integer.parseInt(list[0]);
                            data = Byte.parseByte(list[1]);
                        } else {
                            type = Integer.parseInt(tokens[2]);
                            data = (byte) 0;
                        }
                        Vector force = new Vector(values[0], values[1], values[2]);
                        _actions.add(new FountainAction(this, time, loc,
                                duration, type, data, force));
                    } catch (NumberFormatException e) {
                        _invalidLines.put(strLine, "Invalid Fountain Type");
                        e.printStackTrace();
                    }
                } else if (tokens[1].contains("Title")) {
                    // 0 Title title fadeIn fadeOut stay title...
                    TitleObject.TitleType type = TitleObject.TitleType.valueOf(tokens[2].toUpperCase());
                    int fadeIn = Integer.parseInt(tokens[3]);
                    int fadeOut = Integer.parseInt(tokens[4]);
                    int stay = Integer.parseInt(tokens[5]);
                    String text = "";
                    for (int i = 6; i < tokens.length; i++)
                        text += tokens[i] + " ";
                    if (text.length() > 1)
                        text = text.substring(0, text.length() - 1);
                    _actions.add(new TitleAction(this, time, type, text, fadeIn, fadeOut, stay));
                } else if (tokens[1].contains("Particle")) {
                    // 0 Particle type x,y,z oX oY oZ speed amount
                    ParticleEffect effect = ParticleEffect.fromString(tokens[2]);
                    Location location = WorldUtil.strToLoc(_world.getName() + ","
                            + tokens[3]);
                    float offsetX = Float.parseFloat(tokens[4]);
                    float offsetY = Float.parseFloat(tokens[5]);
                    float offsetZ = Float.parseFloat(tokens[6]);
                    float speed = Float.parseFloat(tokens[7]);
                    int amount = Integer.parseInt(tokens[8]);
                    _actions.add(new ParticleAction(this, time, effect, location, offsetX, offsetY, offsetZ, speed, amount));
                }
            }

            in.close();
        } catch (Exception e) {
            System.out.println("Error on Line [" + strLine + "]");
            Bukkit.broadcast("Error on Line [" + strLine + "]", "arcade.bypass");
            e.printStackTrace();
        }

        if (_loc == null) {
            _invalidLines.put("Missing Line", "Show Location x,y,z");
        }

        for (String cur : _invalidLines.keySet()) {
            System.out.print(ChatColor.GOLD + _invalidLines.get(cur) + " @ "
                    + ChatColor.WHITE + cur.replaceAll("\t", " "));
            Bukkit.broadcast(ChatColor.GOLD + _invalidLines.get(cur)
                    + " @ " + ChatColor.WHITE + cur.replaceAll("\t", " "), "arcade.bypass");
        }
    }

    public boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    public boolean update() {
        if (!_invalidLines.isEmpty()) {
            return true;
        }
        _npcTick = (_npcTick + 1) % 5;
        if (_npcTick == 0) {
            for (ShowNPC npc : _npc.values()) {
                npc.Move();
            }
        }
        // Show Action
        Iterator<ShowAction> actionIterator = _actions.iterator();
        while (actionIterator.hasNext()) {
            ShowAction action = actionIterator.next();
            if (System.currentTimeMillis() - _startTime < action.time) {
                continue;
            }
            action.play();
            actionIterator.remove();
        }
        return _actions.isEmpty();
    }

    public void displayText(String text) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (MathUtil.offset(player.getLocation(), _loc) < _radius) {
                player.sendMessage(ChatColor.AQUA + ChatColor.translateAlternateColorCodes('&', text));
            }
        }
    }

    public void displayTitle(TitleObject title) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (MathUtil.offset(player.getLocation(), _loc) < _radius) {
                title.send(player);
            }
        }
    }

    @SuppressWarnings("deprecation")
    public void playMusic(int record) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playEffect(_loc, Effect.RECORD_PLAY, record);
        }
    }

    public FireworkEffect ParseEffect(String effect) {
        String[] tokens = effect.split(",");

        // Shape
        Type shape;
        try {
            shape = Type.valueOf(tokens[0]);
        } catch (Exception e) {
            _invalidLines.put(effect, "Invalid type [" + tokens[0] + "]");
            return null;
        }

        // Color
        ArrayList<Color> colors = new ArrayList<>();
        for (String color : tokens[1].split("&")) {
            if (color.equalsIgnoreCase("AQUA"))
                colors.add(Color.AQUA);
            else if (color.equalsIgnoreCase("BLACK"))
                colors.add(Color.BLACK);
            else if (color.equalsIgnoreCase("BLUE"))
                colors.add(Color.BLUE);
            else if (color.equalsIgnoreCase("FUCHSIA"))
                colors.add(Color.FUCHSIA);
            else if (color.equalsIgnoreCase("GRAY"))
                colors.add(Color.GRAY);
            else if (color.equalsIgnoreCase("GREEN"))
                colors.add(Color.GREEN);
            else if (color.equalsIgnoreCase("LIME"))
                colors.add(Color.LIME);
            else if (color.equalsIgnoreCase("MAROON"))
                colors.add(Color.MAROON);
            else if (color.equalsIgnoreCase("NAVY"))
                colors.add(Color.NAVY);
            else if (color.equalsIgnoreCase("OLIVE"))
                colors.add(Color.OLIVE);
            else if (color.equalsIgnoreCase("ORANGE"))
                colors.add(Color.ORANGE);
            else if (color.equalsIgnoreCase("PURPLE"))
                colors.add(Color.PURPLE);
            else if (color.equalsIgnoreCase("RED"))
                colors.add(Color.RED);
            else if (color.equalsIgnoreCase("SILVER"))
                colors.add(Color.SILVER);
            else if (color.equalsIgnoreCase("TEAL"))
                colors.add(Color.TEAL);
            else if (color.equalsIgnoreCase("WHITE"))
                colors.add(Color.WHITE);
            else if (color.equalsIgnoreCase("YELLOW"))
                colors.add(Color.YELLOW);
            else {
                _invalidLines.put(effect, "Invalid Color [" + color + "]");
                return null;
            }

        }
        if (colors.isEmpty()) {
            _invalidLines.put(effect, "No Valid Colors");
            return null;
        }

        // Fade
        ArrayList<Color> fades = new ArrayList<>();
        for (String color : tokens[1].split("&")) {
            if (color.equalsIgnoreCase("AQUA"))
                fades.add(Color.AQUA);
            else if (color.equalsIgnoreCase("BLACK"))
                fades.add(Color.BLACK);
            else if (color.equalsIgnoreCase("BLUE"))
                fades.add(Color.BLUE);
            else if (color.equalsIgnoreCase("FUCHSIA"))
                fades.add(Color.FUCHSIA);
            else if (color.equalsIgnoreCase("GRAY"))
                fades.add(Color.GRAY);
            else if (color.equalsIgnoreCase("GREEN"))
                fades.add(Color.GREEN);
            else if (color.equalsIgnoreCase("LIME"))
                fades.add(Color.LIME);
            else if (color.equalsIgnoreCase("MAROON"))
                fades.add(Color.MAROON);
            else if (color.equalsIgnoreCase("NAVY"))
                fades.add(Color.NAVY);
            else if (color.equalsIgnoreCase("OLIVE"))
                fades.add(Color.OLIVE);
            else if (color.equalsIgnoreCase("ORANGE"))
                fades.add(Color.ORANGE);
            else if (color.equalsIgnoreCase("PURPLE"))
                fades.add(Color.PURPLE);
            else if (color.equalsIgnoreCase("RED"))
                fades.add(Color.RED);
            else if (color.equalsIgnoreCase("SILVER"))
                fades.add(Color.SILVER);
            else if (color.equalsIgnoreCase("TEAL"))
                fades.add(Color.TEAL);
            else if (color.equalsIgnoreCase("WHITE"))
                fades.add(Color.WHITE);
            else if (color.equalsIgnoreCase("YELLOW"))
                fades.add(Color.YELLOW);
            else {
                _invalidLines.put(effect, "Invalid Fade Color [" + color + "]");
                return null;
            }
        }
        if (fades.isEmpty()) {
            _invalidLines.put(effect, "No Valid Fade Colors");
            return null;
        }

        boolean flicker = effect.toUpperCase().contains("FLICKER");
        boolean trail = effect.toUpperCase().contains("TRAIL");

        // Firework
        return FireworkEffect.builder().with(shape).withColor(colors).withFade(fades.get(0)).flicker(flicker).trail(trail).build();
    }

    public HashMap<String, ShowNPC> GetNPC() {
        return _npc;
    }
}
