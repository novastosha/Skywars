package dev.nova.skywars.arena;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.List;

public class FinalArena implements Cloneable {

    private final String codeName;
    private final String fancyName;
    private final World world;
    private final int maxPlayers;
    private final Location waitingArea;
    private final Location waitingAreaP1;
    private final Location waitingAreaP2;
    private final List<Location> cages;
    private final List<Location> chests;

    public FinalArena(String codeName, String displayName, int maxPlayers, List<Location> cages, World world, Location waiting, Location waitingAreaP1, Location waitingAreaP2,List<Location> chests) {
        this.codeName = codeName;
        this.fancyName = displayName;
        this.maxPlayers = maxPlayers;
        this.world = world;
        this.waitingArea = waiting;
        this.waitingAreaP1 = waitingAreaP1;
        this.waitingAreaP2 = waitingAreaP2;
        this.cages =cages;
        this.chests = chests;
    }

    public String getCodeName() {
        return codeName;
    }

    public String getFancyName() {
        return fancyName;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public List<Location> getChests() {
        return chests;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public FinalArena cloneArena() {
        try {
            return (FinalArena) clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public World getWorld() {
        return world;
    }

    public Location getWaitingArea() {
        return waitingArea;
    }

    public Location getWaitingAreaP1() {
        return waitingAreaP1;
    }

    public Location getWaitingAreaP2() {
        return waitingAreaP2;
    }

    public List<Location> getCages() {
        return cages;
    }
}
