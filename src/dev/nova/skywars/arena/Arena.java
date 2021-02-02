package dev.nova.skywars.arena;

import dev.nova.skywars.SkyWars;
import dev.nova.skywars.player.SkyWarsPlayer;
import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class Arena {

    public static ItemStack LEAVE_ITEM;
    private final FinalArena fromClone;
    private Location waitingSpawnPOS1;
    private Location waitingSpawnPOS2;
    private Location waitingSpawn;
    private World world;
    private String codeName;
    private String displayName;
    private ArrayList<SkyWarsPlayer> players;
    private ArrayList<SkyWarsPlayer> spectators;
    private ArrayList<dev.nova.skywars.arena.Chest> chests;
    private HashMap<SkyWarsPlayer, Location> playerCage;
    private ArenaState state;
    private int minPlayers;
    private int timeToStart;
    private boolean privateGame;
    private int maxPlayers;
    private int ID;
    private boolean count;
    private boolean force;
    private int taskID;
    private boolean started;

    public Arena(FinalArena fromClone, boolean privateGame) {
        this.fromClone = fromClone;
        try {
            maxPlayers = fromClone.getMaxPlayers();
            minPlayers = Math.round(((float) maxPlayers / 2));
            codeName = fromClone.getCodeName();
            displayName = fromClone.getFancyName();
            state = ArenaState.WAITING;
            ID = ArenaManager.arenas.size() + 1;
            timeToStart = 30;
            playerCage = new HashMap<>();
            players = new ArrayList<>();
            spectators = new ArrayList<>();
            this.privateGame = privateGame;
            world = ArenaManager.copyWorld(fromClone.getWorld(), fromClone.getCodeName() + "_" + ID);
            this.waitingSpawn = fromClone.getWaitingArea().clone();
            waitingSpawn.setWorld(world);
            this.waitingSpawnPOS1 = fromClone.getWaitingAreaP1().clone();
            this.waitingSpawnPOS2 = fromClone.getWaitingAreaP2().clone();
            waitingSpawnPOS2.setWorld(world);
            waitingSpawnPOS1.setWorld(world);
            chests = new ArrayList<>();
            for(dev.nova.skywars.arena.Chest chest : fromClone.getChests()){

                chests.add(chest.cloneChest(world));
            }

            Bukkit.getConsoleSender().sendMessage("[SKYWARS] " + ChatColor.GOLD + "An arena has been created from: " + codeName + " with id: " + ID);
            ArenaManager.arenas.add(this);
            BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
            taskID = scheduler.scheduleSyncRepeatingTask(SkyWars.getPlugin(SkyWars.class), new Runnable() {
                @Override
                public void run() {
                    if(!started)
                    {
                        sendToAllPlayers(ChatColor.GRAY + "[" + ChatColor.LIGHT_PURPLE + "SKYWARS" + ChatColor.GRAY + "]" + " The game is starting in: " + timeToStart);
                        started = true;
                    }
                    if (count) {
                        if (timeToStart <= 10 && timeToStart != 0) {
                            sendToAllPlayers(ChatColor.GRAY + "[" + ChatColor.LIGHT_PURPLE + "SKYWARS" + ChatColor.GRAY + "]" + " The game is starting in: " + timeToStart);
                        }
                        timeToStart--;
                    }

                    if (count) {
                        if (timeToStart == 0) {
                            setState(ArenaState.INGAME);
                            sendToAllPlayers(ChatColor.GRAY + "[" + ChatColor.LIGHT_PURPLE + "SKYWARS" + ChatColor.GRAY + "]" + " The game has begun!");
                            count = false;
                            startGame();
                        }
                    }
                }
            }, 0L, 20L);
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage("[SKYWARS] " + ChatColor.RED + " Unable to load the arena: " + ID + " (" + e.getMessage() + ")");
            Bukkit.getConsoleSender().sendMessage("PLEASE REPORT THE ERROR AS SOON AS POSSIBLE");
            e.printStackTrace();
        }
    }

    private void startGame() {
        fillChests();
    }

    private void stopGame(){

    }

    public World getWorld() {
        return world;
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

    public String getDisplayName() {
        return displayName;
    }

    public FinalArena getFromClone() {
        return fromClone;
    }

    public int getID() {
        return ID;
    }

    public void fillChests(){
        for (dev.nova.skywars.arena.Chest chest : chests){
            chest.generateLoot();
        }
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
            }
        }
        if (!force) {
            if (state.equals(ArenaState.FULL) || state.equals(ArenaState.STARTING)) {
                if (players.size() < maxPlayers && players.size() < minPlayers) {
                    sendToAllPlayers(ChatColor.RED + "Waiting canceled!");
                    setState(ArenaState.WAITING);
                    setTimeToStart(1);
                    count = false;

                }
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
        playerCage = new HashMap<>();
        force = false;
        state = ArenaState.WAITING;
        started = false;
    }

    private void resetMap() {
        String worldName = world.getName();
        Bukkit.unloadWorld(world, false);
        File worldFolder = new File(Bukkit.getWorldContainer().getPath() + "/" + worldName);
        worldFolder.delete();
        //FIXME: world = ArenaManager.copyWorld(fromClone.getWorld(), fromClone.getCodeName() + "_" + ID);
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
        //player.getPlayer().teleport(new Location(world,83, 76 ,262));
        sendToAllPlayers(ChatColor.GRAY + "[" + ChatColor.LIGHT_PURPLE + "SKYWARS" + ChatColor.GRAY + "]" + ChatColor.LIGHT_PURPLE + player.getPlayer().getName() + ChatColor.GRAY + " has joined the game!");
        //TODO HANDLE CAGE SELECTION
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
        //player.getPlayer().setGameMode(GameMode.ADVENTURE);
    }

    public ArrayList<SkyWarsPlayer> getSpectators() {
        return spectators;
    }

    public void kill(SkyWarsPlayer player) {
        player.getPlayer().getInventory().clear();
        player.setInGame(false);
        players.remove(player);
        spectators.add(player);
        player.getPlayer().teleport(playerCage.get(player));
        player.getPlayer().setGameMode(GameMode.SPECTATOR);
    }

    public void setCount(boolean b) {
        count = b;
    }

    public void setForce(boolean force) {
        this.force = force;
    }

    public boolean isForce() {
        return force;
    }
}
