package dev.nova.skywars.arena;

import dev.nova.skywars.player.SkyWarsPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;

public class Arena extends Thread {

    public static ItemStack LEAVE_ITEM;
    private final FinalArena fromClone;
    private String codeName;
    private String displayName;
    private ArrayList<SkyWarsPlayer> players;
    private ArrayList<SkyWarsPlayer> spectators;
    private HashMap<SkyWarsPlayer,Location> playerCage;
    private ArenaState state;
    private int minPlayers;
    private int timeToStart;
    private boolean privateGame;
    private int maxPlayers;
    private int ID;
    private boolean count;

    public Arena(FinalArena fromClone, boolean privateGame) {
        this.fromClone = fromClone;
        maxPlayers = fromClone.getMaxPlayers();
        minPlayers = 1;
        minPlayers = 1;
        codeName = fromClone.getCodeName();
        displayName = fromClone.getFancyName();
        state = ArenaState.WAITING;
        ID = ArenaManager.arenas.size() + 1;
        players = new ArrayList<>();
        spectators = new ArrayList<>();
        this.privateGame = privateGame;
        Bukkit.getConsoleSender().sendMessage("[SKYWARS] " + ChatColor.GOLD + "An arena has been created from: " + codeName + " with id: " + ID);
        ArenaManager.arenas.add(this);
    }


    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public ArrayList<SkyWarsPlayer> getPlayers() {
        return players;
    }

    public String getCodeName() {
        return codeName;
    }

    public boolean isPrivateGame() {
        return privateGame;
    }

    public void setPrivateGame(boolean privateGame) {
        this.privateGame = privateGame;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    @Override
    public void run() {

            sendToAllPlayers(ChatColor.GRAY + "[" + ChatColor.LIGHT_PURPLE + "SKYWARS" + ChatColor.GRAY + "]" + " The game is starting in: " + timeToStart);
            while (timeToStart != 0) {
                if (count) {
                    if (timeToStart <= 10) {
                        sendToAllPlayers(ChatColor.GRAY + "[" + ChatColor.LIGHT_PURPLE + "SKYWARS" + ChatColor.GRAY + "]" + " The game is starting in: " + timeToStart);
                    }

                    timeToStart--;
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (count) {
                if (timeToStart == 0) {
                    setState(ArenaState.INGAME);
                    sendToAllPlayers(ChatColor.GRAY + "[" + ChatColor.LIGHT_PURPLE + "SKYWARS" + ChatColor.GRAY + "]" + " The game has begun!");
                }
            }

    }

    public String getDisplayName() {
        return displayName;
    }

    public FinalArena getFromClone() {
        return fromClone;
    }

    public int getID() {
        return ID;
    }

    public void updateState() {

        if (state.equals(ArenaState.WAITING)) {
            if (players.size() == maxPlayers || players.size() == minPlayers) {
                if (players.size() == maxPlayers) {
                    setState(ArenaState.FULL);
                    setTimeToStart(10);

                }
                if (players.size() == minPlayers) {
                    setState(ArenaState.STARTING);
                    setTimeToStart(30);

                }
                count = true;
                run();
            }
        }
        if (state.equals(ArenaState.FULL) || state.equals(ArenaState.STARTING)) {
            if (players.size() < maxPlayers && players.size() < minPlayers) {
                sendToAllPlayers(ChatColor.RED + "Waiting canceled!");
                setState(ArenaState.WAITING);
                setTimeToStart(1);
                count = false;

            }
        }
        if (state.equals(ArenaState.INGAME)) {
            if (players.size() == 1 || players.size() == 0) {
                if (players.size() == 1) {
                    sendToAllPlayersAndSpectators(ChatColor.GRAY + "-----------------------------------------------------");
                    sendToAllPlayersAndSpectators(ChatColor.AQUA + players.get(0).getPlayer().getName() + " has won!");
                    sendToAllPlayersAndSpectators(ChatColor.GRAY + "-----------------------------------------------------");
                }
                if (players.size() == 0) {
                    sendToAllPlayersAndSpectators(ChatColor.GRAY + "-----------------------------------------------------");
                    sendToAllPlayersAndSpectators(ChatColor.AQUA + "No one have won!");
                    sendToAllPlayersAndSpectators(ChatColor.GRAY + "-----------------------------------------------------");
                }
                resetArena();
                for (SkyWarsPlayer skyWarsPlayer : players) {
                    leave(skyWarsPlayer, false);
                }
                for (SkyWarsPlayer skyWarsPlayer : spectators) {
                    leave(skyWarsPlayer, false);
                }
                players = new ArrayList<>();
                spectators = new ArrayList<>();
                resetMap();
            }
        }
    }

    private void resetArena() {
        setState(ArenaState.RESETTING);
        maxPlayers = fromClone.getMaxPlayers();
        minPlayers = 1;
        codeName = fromClone.getCodeName();
        displayName = fromClone.getFancyName();
        state = ArenaState.WAITING;

    }

    private void resetMap() {
    }

    public void sendToAllPlayersAndSpectators(String message) {
        for (SkyWarsPlayer skyWarsPlayer : players) {
            skyWarsPlayer.getPlayer().sendMessage(message);
        }
        for (SkyWarsPlayer skyWarsPlayer : spectators) {
            skyWarsPlayer.getPlayer().sendMessage(message);
        }
    }

    public int getTimeToStart() {
        return timeToStart;
    }

    public void join(SkyWarsPlayer player) {

        players.add(player);
        player.getPlayer().getInventory().clear();
        player.getPlayer().getInventory().setItem(8, LEAVE_ITEM);
        player.setInGame(true);
        player.setGame(this);
        sendToAllPlayers(ChatColor.GRAY + "[" + ChatColor.LIGHT_PURPLE + "SKYWARS" + ChatColor.GRAY + "]" + ChatColor.LIGHT_PURPLE+player.getPlayer().getName()+ChatColor.GRAY+" has joined the game!");
    }


    public void sendToAllPlayers(String message) {
        for (SkyWarsPlayer skyWarsPlayer : players) {
            skyWarsPlayer.getPlayer().sendMessage(message);
        }
    }

    public void setTimeToStart(int timeToStart) {
        this.timeToStart = timeToStart;
    }

    public void setState(ArenaState state) {
        this.state = state;
    }

    public ArenaState getArenaState() {
        return state;
    }

    public void leave(SkyWarsPlayer player, boolean remove) {
        if (remove) players.remove(player);
        if (remove) spectators.remove(player);
        player.getPlayer().getInventory().clear();
        player.setInGame(false);
        player.setGame(null);
    }

    public ArrayList<SkyWarsPlayer> getSpectators() {
        return spectators;
    }

}
