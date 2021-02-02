package dev.nova.skywars.arena;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.util.*;

public class ArenaManager {


    /**
     * NOTE:
     * <p>
     * Arenas are based on IDs where FinalArenas are based on codeNames...
     */
    public static ArrayList<FinalArena> arenasFinalCache;
    public static ArrayList<Arena> arenas;

    private static void copyFileStructure(File source, File target) {
        try {
            ArrayList<String> ignore = new ArrayList<>(Arrays.asList("uid.dat", "session.lock","playerdata","stats","advancements"));
            if (!ignore.contains(source.getName())) {
                if (source.isDirectory()) {
                    if (!target.exists())
                        if (!target.mkdirs())
                            throw new IOException("Couldn't create world directory!");
                    String files[] = source.list();
                    for (String file : files) {
                        File srcFile = new File(source, file);
                        File destFile = new File(target, file);
                        copyFileStructure(srcFile, destFile);
                    }
                } else {
                    InputStream in = new FileInputStream(source);
                    OutputStream out = new FileOutputStream(target);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = in.read(buffer)) > 0)
                        out.write(buffer, 0, length);
                    in.close();
                    out.close();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static World copyWorld(World originalWorld, String newWorldName) {
        File copiedFile = new File(Bukkit.getWorldContainer(), newWorldName);
        copyFileStructure(originalWorld.getWorldFolder(), copiedFile);
        return new WorldCreator(newWorldName).createWorld();
    }

    public static void loadArenas(File dest) {
        Bukkit.getConsoleSender().sendMessage("[SKYWARS] " + ChatColor.DARK_AQUA + "Looking for arenas in: " + dest.getName());
        for (File file : Objects.requireNonNull(dest.listFiles())) {
            if (file.isDirectory()) {
                loadArenas(file);
            } else {
                if (!file.getName().startsWith("-")) {
                    if (file.getName().endsWith(".yml")) {
                        loadArena(file);
                    } else {
                        Bukkit.getConsoleSender().sendMessage("[SKYWARS] " + ChatColor.RED + "Invalid file type: '" + file.getName() + "'");
                    }
                }
            }
        }
    }

    public static boolean loadArena(File file) {
        Bukkit.getConsoleSender().sendMessage("[SKYWARS] " + ChatColor.AQUA + "Loading arena: " + file.getName());
        YamlConfiguration configuration = new YamlConfiguration();
        try {
            configuration.load(file);
        } catch (IOException | InvalidConfigurationException err) {
            Bukkit.getConsoleSender().sendMessage("[SKYWARS] " + ChatColor.RED + "Loading failed: " + file.getName());
            err.printStackTrace();
            return false;
        }

        ConfigurationSection configurationSection = configuration.getConfigurationSection("data");

        if (configurationSection == null) {
            Bukkit.getConsoleSender().sendMessage("[SKYWARS] " + ChatColor.RED + "Loading failed: " + file.getName()+" (Is the config empty?)");
            return false;
        }

        if (!configurationSection.get("maxPlayers").getClass().getTypeName().equalsIgnoreCase("java.lang.Integer")) {
            Bukkit.getConsoleSender().sendMessage("[SKYWARS] " + ChatColor.RED + "Loading failed: " + file.getName() + " (maxPlayers must be an Integer)");
            return false;
        }
        if (!configurationSection.get("codeName").getClass().getTypeName().equalsIgnoreCase("java.lang.String")) {
            Bukkit.getConsoleSender().sendMessage("[SKYWARS] " + ChatColor.RED + "Loading failed: " + file.getName() + " (codeName must be String)");
            return false;
        } else {
            if (configurationSection.getString("codeName").contains(" ")) {
                Bukkit.getConsoleSender().sendMessage("[SKYWARS] " + ChatColor.RED + "Loading failed: " + file.getName() + " (codeName is String but contains space(s))");
                return false;
            }
        }
        if (!configurationSection.get("displayName").getClass().getTypeName().equalsIgnoreCase("java.lang.String")) {
            Bukkit.getConsoleSender().sendMessage("[SKYWARS] " + ChatColor.RED + "Loading failed: " + file.getName() + " (displayName must be String)");
            return false;
        }

        if (!configurationSection.get("world").getClass().getTypeName().equalsIgnoreCase("java.lang.String")) {
            Bukkit.getConsoleSender().sendMessage("[SKYWARS] " + ChatColor.RED + "Loading failed: " + file.getName() + " (world must be String)");
            return false;
        }

        String worldString = configurationSection.getString("world");
        World world = Bukkit.getWorld(worldString);

        if (world == null) {
            Bukkit.getConsoleSender().sendMessage("[SKYWARS] " + ChatColor.RED + "Loading failed: " + file.getName() + " (A world with name: " + worldString + " does not exist!)");
            return false;
        }

        Bukkit.getConsoleSender().sendMessage("[SKYWARS] " + ChatColor.BLUE + "Building arena...");
        int maxPlayers = configurationSection.getInt("maxPlayers");
        String codeName = configurationSection.getString("codeName");
        String displayName = configurationSection.getString("displayName");

        ArrayList<dev.nova.skywars.arena.Chest> chests = new ArrayList<>();
        if(configurationSection.getConfigurationSection("chests") == null){
            Bukkit.getConsoleSender().sendMessage("[SKYWARS] " + ChatColor.RED + "Loading failed: " + file.getName() + " (No chests found!)");
            return true;
        }
        if(configurationSection.get("cages") == null){
            Bukkit.getConsoleSender().sendMessage("[SKYWARS] " + ChatColor.RED + "Loading failed: " + file.getName() + " (No cages found!)");
            return true;
        }

        List<Location> cages = (List<Location>) configuration.getList("data.cages");

        configurationSection.getConfigurationSection("chests").getKeys(false).forEach(chest ->{
            ChestType type = ChestType.valueOf(configurationSection.getConfigurationSection("chests").getString(chest));
            String[] chestLocation = chest.split("_");
            World chestWorld = Bukkit.getWorld(chestLocation[3]);
            Location location = new Location(chestWorld,Integer.parseInt(chestLocation[0]),Integer.parseInt(chestLocation[1]),Integer.parseInt(chestLocation[2]));
            Block block = chestWorld.getBlockAt(location);
            if(block.getType().equals(Material.CHEST)){
                chests.add(new dev.nova.skywars.arena.Chest((Chest) block.getState(),type));
                Bukkit.getConsoleSender().sendMessage("[SKYWARS] "+ChatColor.GREEN+"The chest at: "+chestLocation[0]+" "+chestLocation[1]+" "+chestLocation[2]);
            }
        });

        FinalArena arena = new FinalArena(codeName, displayName, maxPlayers,cages,world,world.getBlockAt(0,0,0).getLocation(),world.getBlockAt(0,0,0).getLocation(),world.getBlockAt(0,0,0).getLocation(),chests);
        arenasFinalCache.add(arena);
        Bukkit.getConsoleSender().sendMessage("[SKYWARS] " + ChatColor.GREEN + "Arena: " + displayName + " has successfully loaded!");
        return true;
    }

    public static ArrayList<FinalArena> getFinaArenas() {
        return arenasFinalCache;
    }

    public static ArrayList<Arena> getArenas() {
        return arenas;
    }

    public static FinalArena getFinalArena(String codeName) {
        for (FinalArena arena : arenasFinalCache) {
            if (arena.getCodeName().equals(codeName)) {
                return arena;
            }
        }
        return null;
    }

    public static Arena getArena(int id) {
        for (Arena arena : arenas) {
            if (arena.getID() == id) {
                return arena;
            }
        }
        return null;
    }
}
