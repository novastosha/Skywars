package dev.nova.skywars.player;

import dev.nova.skywars.arena.Arena;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class SkyWarsPlayer {

    public static ArrayList<SkyWarsPlayer> skyWarsPlayerCache;
    private final Player player;
    private Arena game;
    private boolean inGame;

    public SkyWarsPlayer(Player player){
        this.player = player;
        skyWarsPlayerCache.add(this);
    }

    public Player getPlayer() {
        return player;
    }
    
    public static SkyWarsPlayer getPlayer(Player player){
        for(SkyWarsPlayer skyWarsPlayer : skyWarsPlayerCache){
            if(skyWarsPlayer.getPlayer() == player){
                return skyWarsPlayer;
            }
        }
        return null;
    }

    public static ArrayList<SkyWarsPlayer> getKitPVPPlayerCache() {
        return skyWarsPlayerCache;
    }

    public boolean isInGame() {
        return inGame;
    }

    public void setInGame(boolean inGame) {
        this.inGame = inGame;
    }

    public void setGame(Arena arena) {
        game = arena;
    }

    public Arena getGame() {
        return game;
    }
}
