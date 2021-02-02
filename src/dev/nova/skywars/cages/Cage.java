package dev.nova.skywars.cages;

import org.bukkit.Location;
import org.bukkit.Material;

import java.util.HashMap;

public class Cage {

    private final HashMap<String, Material> locations;

    public Cage(HashMap<String, Material> locations) {
        this.locations = locations;
    }

    public void paste(Location location) {
        location = new Location(location.getWorld(),location.getBlockX(),location.getBlockY(),location.getBlockZ());
        for(String stringLocation : locations.keySet()){
            String[] xyz = stringLocation.split("_");

            int pasteX = 0,pasteY = 0,pasteZ = 0;
            Location pasteLocation;

            int inCoord = 0;
            for(String xoyoz : xyz){

                if(xoyoz.contains("+")){
                    switch (inCoord){
                        case 0:
                            pasteX = Integer.parseInt(xoyoz.split("\\+")[1]);
                            break;
                        case 1:
                            pasteY = Integer.parseInt(xoyoz.split("\\+")[1]);
                            break;
                        case 2:
                            pasteZ = Integer.parseInt(xoyoz.split("\\+")[1]);
                            break;
                    }
                }else if(xoyoz.contains("-")) {
                    switch (inCoord) {
                        case 0:
                            pasteX = -Integer.parseInt(xoyoz.split("-")[1]);
                            break;
                        case 1:
                            pasteY = -Integer.parseInt(xoyoz.split("-")[1]);
                            break;
                        case 2:
                            pasteZ = -Integer.parseInt(xoyoz.split("-")[1]);
                            break;
                    }
                }
                inCoord++;
            }

            pasteLocation = new Location(location.getWorld(), location.getBlockX()+pasteX,location.getBlockY()+pasteY,location.getBlockZ()+pasteZ);
            pasteLocation.getWorld().getBlockAt(pasteLocation).setType(locations.get(stringLocation));
            location = new Location(location.getWorld(),location.getBlockX(),location.getBlockY(),location.getBlockZ());
        }
    }

    public void remove(Location location) {
        location = new Location(location.getWorld(),location.getBlockX(),location.getBlockY(),location.getBlockZ());
        for(String stringLocation : locations.keySet()){
            String[] xyz = stringLocation.split("_");

            int pasteX = 0,pasteY = 0,pasteZ = 0;
            Location pasteLocation;

            int inCoord = 0;
            for(String xoyoz : xyz){

                if(xoyoz.contains("+")){
                    switch (inCoord){
                        case 0:
                            pasteX = Integer.parseInt(xoyoz.split("\\+")[1]);
                            break;
                        case 1:
                            pasteY = Integer.parseInt(xoyoz.split("\\+")[1]);
                            break;
                        case 2:
                            pasteZ = Integer.parseInt(xoyoz.split("\\+")[1]);
                            break;
                    }
                }else if(xoyoz.contains("-")) {
                    switch (inCoord) {
                        case 0:
                            pasteX = -Integer.parseInt(xoyoz.split("-")[1]);
                            break;
                        case 1:
                            pasteY = -Integer.parseInt(xoyoz.split("-")[1]);
                            break;
                        case 2:
                            pasteZ = -Integer.parseInt(xoyoz.split("-")[1]);
                            break;
                    }
                }
                inCoord++;
            }

            pasteLocation = new Location(location.getWorld(), location.getBlockX()+pasteX,location.getBlockY()+pasteY,location.getBlockZ()+pasteZ);
            pasteLocation.getWorld().getBlockAt(pasteLocation).setType(Material.AIR);
            location = new Location(location.getWorld(),location.getBlockX(),location.getBlockY(),location.getBlockZ());
        }
    }
}
