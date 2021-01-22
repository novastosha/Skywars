package dev.nova.skywars.cages;

import com.sun.javafx.collections.ArrayListenerHelper;
import dev.nova.skywars.player.SkyWarsPlayer;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;

public class Cage {

    private final ArrayList<Location> locations;

    public Cage(ArrayList<Location> locations){
       this.locations = locations;
    }

}
