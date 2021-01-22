package dev.nova.skywars.arena;

public class FinalArena implements Cloneable {

    private final String codeName;
    private final String fancyName;
    private final int maxPlayers;

    public FinalArena(String codeName, String displayName, int maxPlayers) {
        this.codeName = codeName;
        this.fancyName = displayName;
        this.maxPlayers = maxPlayers;
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
}
