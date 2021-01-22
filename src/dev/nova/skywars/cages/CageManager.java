package dev.nova.skywars.cages;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.io.File;
import java.util.Objects;

public class CageManager {

    public static void loadCages(File dest){
        Bukkit.getConsoleSender().sendMessage("[SKYWARS] "+ ChatColor.DARK_AQUA+"Looking for arenas in: "+dest.getName());
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

    public static void loadCage(File file) {
    }
}
