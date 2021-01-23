package dev.nova.skywars.arena;

import org.bukkit.World;

public class FinalArena implements Cloneable {

    private final String codeName;
    private final String fancyName;
    private final World world;
    private final int maxPlayers;

    public FinalArena(String codeName, String displayName, int maxPlayers,World world) {
        this.codeName = codeName;
        this.fancyName = displayName;
        this.maxPlayers = maxPlayers;
        this.world = world;
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
}
