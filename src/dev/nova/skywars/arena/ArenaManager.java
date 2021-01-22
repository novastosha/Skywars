package dev.nova.skywars.arena;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class ArenaManager {


    /**
     * NOTE:
     *
     * Arenas are based on IDs where FinalArenas are based on codeNames...
     */
    public static ArrayList<FinalArena> arenasFinalCache;
    public static ArrayList<Arena> arenas;

    public static void loadArenas(File dest){
        Bukkit.getConsoleSender().sendMessage("[SKYWARS] "+ ChatColor.DARK_AQUA+"Looking for arenas in: "+dest.getName());
        for(File file : Objects.requireNonNull(dest.listFiles())){
            if(file.isDirectory()){
                loadArenas(file);
            }else{
                if(!file.getName().startsWith("-")) {
                    if (file.getName().endsWith(".yml")) {
                        loadArena(file);
                    } else {
                        Bukkit.getConsoleSender().sendMessage("[SKYWARS] " + ChatColor.RED + "Invalid file type: '" + file.getName() + "'");
                    }
                }
            }
        }
    }

    public static void loadArena(File file){
        Bukkit.getConsoleSender().sendMessage("[SKYWARS] "+ ChatColor.AQUA+"Loading arena: "+file.getName());
        YamlConfiguration configuration = new YamlConfiguration();
        try{
            configuration.load(file);
        }catch (IOException | InvalidConfigurationException err){
            Bukkit.getConsoleSender().sendMessage("[SKYWARS] "+ ChatColor.RED+"Loading failed: "+file.getName());
            err.printStackTrace();
        }

        ConfigurationSection configurationSection = configuration.getConfigurationSection("data") != null ? configuration.getConfigurationSection("data") : null;

        if(configurationSection == null || !configurationSection.contains("maxPlayers") || !configurationSection.contains("codeName") ||!configurationSection.contains("displayName")){
            Bukkit.getConsoleSender().sendMessage("[SKYWARS] "+ ChatColor.RED+"Loading failed: "+file.getName());
            return;
        }

        if(!configurationSection.get("maxPlayers").getClass().getTypeName().equalsIgnoreCase("java.lang.Integer")){
            Bukkit.getConsoleSender().sendMessage("[SKYWARS] "+ ChatColor.RED+"Loading failed: "+file.getName()+" (maxPlayers must be an Integer)");
            return;
        }
        if(!configurationSection.get("codeName").getClass().getTypeName().equalsIgnoreCase("java.lang.String")){
            Bukkit.getConsoleSender().sendMessage("[SKYWARS] "+ ChatColor.RED+"Loading failed: "+file.getName()+" (codeName must be String)");
            return;
        }else{
            if(configurationSection.getString("codeName").contains(" ")){
                Bukkit.getConsoleSender().sendMessage("[SKYWARS] "+ ChatColor.RED+"Loading failed: "+file.getName()+" (codeName is String but contains space(s))");
                return;
            }
        }
        if(!configurationSection.get("displayName").getClass().getTypeName().equalsIgnoreCase("java.lang.String")){
            Bukkit.getConsoleSender().sendMessage("[SKYWARS] "+ ChatColor.RED+"Loading failed: "+file.getName()+" (displayName must be String)");
            return;
        }


        Bukkit.getConsoleSender().sendMessage("[SKYWARS] "+ ChatColor.BLUE+"Building arena...");
        int maxPlayers = configurationSection.getInt("maxPlayers");
        String codeName = configurationSection.getString("codeName");
        String displayName = configurationSection.getString("displayName");
        FinalArena arena = new FinalArena(codeName,displayName,maxPlayers);
        arenasFinalCache.add(arena);
        Bukkit.getConsoleSender().sendMessage("[SKYWARS] "+ ChatColor.GREEN+"Arena: "+displayName+" has successfully loaded!");
    }

    public static ArrayList<FinalArena> getFinaArenas() {
        return arenasFinalCache;
    }

    public static ArrayList<Arena> getArenas() {
        return arenas;
    }

    public static FinalArena getFinalArena(String codeName){
        for(FinalArena arena : arenasFinalCache) {
            if(arena.getCodeName().equals(codeName)){
                return arena;
            }
        }
        return null;
    }

    public static Arena getArena(int id){
        for(Arena arena : arenas){
            if(arena.getID() == id){
                return arena;
            }
        }
        return null;
    }
}
