package dev.nova.skywars.cages;

import dev.nova.skywars.player.SkyWarsPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class CageManager {

    public static ArrayList<Cage> cages;

    public static void loadCages(File dest){
        Bukkit.getConsoleSender().sendMessage("[SKYWARS] "+ ChatColor.DARK_AQUA+"Looking for cages in: "+dest.getName());
        for(File file : Objects.requireNonNull(dest.listFiles())){
            if(file.isDirectory()){
                loadCages(file);
            }else{
                if(!file.getName().startsWith("-")) {
                    if (file.getName().endsWith(".yml")) {
                        loadCage(file);
                    } else {
                        Bukkit.getConsoleSender().sendMessage("[SKYWARS] " + ChatColor.RED + "Invalid file type: '" + file.getName() + "'");
                    }
                }
            }
        }
    }

    public static boolean loadCage(File file) {
        Bukkit.getConsoleSender().sendMessage("[SKYWARS] " + ChatColor.AQUA + "Loading cage: " + file.getName());
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
            Bukkit.getConsoleSender().sendMessage("[SKYWARS] " + ChatColor.RED + "Loading failed: " + file.getName() + " (Is the config empty?)");
            return false;
        }
        HashMap<String, Material> locations = new HashMap<>();
        configurationSection.getKeys(false).forEach(blockLocation -> {
            locations.put(blockLocation,Material.valueOf(configurationSection.getString(blockLocation)));
        });

        cages.add(new Cage(locations));
        Bukkit.getConsoleSender().sendMessage("[SKYWARS] "+ChatColor.GREEN+"Loaded cage");

        return true;
    }
}
