package dev.nova.skywars;

import dev.nova.skywars.arena.Arena;
import dev.nova.skywars.arena.ArenaCommand;
import dev.nova.skywars.arena.ArenaListener;
import dev.nova.skywars.arena.ArenaManager;
import dev.nova.skywars.cages.CageManager;
import dev.nova.skywars.player.SkyWarsPlayer;
import dev.nova.skywars.ui.ArenaListGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class SkyWars extends JavaPlugin implements Listener {
    /**
     * TODO:
     *
     * Custom death messages :)
     */

    @Override
    public void onEnable() {

        ArenaManager.arenasFinalCache = new ArrayList<>();
        ArenaManager.arenas = new ArrayList<>();
        SkyWarsPlayer.skyWarsPlayerCache = new ArrayList<>();
        for(Player player : Bukkit.getOnlinePlayers()){
            SkyWarsPlayer skyWarsPlayer = new SkyWarsPlayer(player);
        }
        ItemStack li = new ItemStack(Material.REDSTONE);
        ItemMeta meta = li.getItemMeta();
        meta.setDisplayName(ChatColor.RED+"Leave Game");
        li.setItemMeta(meta);
        Arena.LEAVE_ITEM = li;

        File plugin = new File("./plugins/Skywars/");
        plugin.mkdir();

        File config = new File("./plugins/Skywars/","config.yml");

            try {
                config.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

        File arenas = new File("./plugins/Skywars/arenas");
        arenas.mkdir();

        File cages = new File("./plugins/Skywars/cages");
        cages.mkdir();

        Bukkit.getPluginManager().registerEvents(this,this);
        Bukkit.getPluginManager().registerEvents(new ArenaListGUI(),this);
        Bukkit.getPluginManager().registerEvents(new ArenaListener(),this);

        ArenaCommand arenaCommands = new ArenaCommand();
        getCommand("join").setExecutor(arenaCommands);
        getCommand("arena-list").setExecutor(arenaCommands);
        getCommand("forcestart").setExecutor(arenaCommands);
        getCommand("finalarena").setExecutor(arenaCommands);
        getCommand("arena").setExecutor(arenaCommands);

        Bukkit.getConsoleSender().sendMessage("[SKYWARS] "+ ChatColor.GREEN+"Loading arenas...");
        ArenaManager.loadArenas(new File("./plugins/Skywars/arenas"));

        Bukkit.getConsoleSender().sendMessage("[SKYWARS] "+ ChatColor.GREEN+"Loading cages...");
        CageManager.loadCages(new File("./plugins/Skywars/cages"));



        Arena test = new Arena(ArenaManager.getFinalArena("test").cloneArena(),false);

        new Thread("Arena-Manager"){
            @Override
            public void run() {
                while(true){
                    for(Arena arena : ArenaManager.arenas){
                        arena.updateState();
                    }
                }
            }
        }.start();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equalsIgnoreCase("leave")){
            if(sender instanceof Player){
                Player player = (Player) sender;

                SkyWarsPlayer skyWarsPlayer = SkyWarsPlayer.getPlayer(player);
                if(skyWarsPlayer.isInGame()){
                    skyWarsPlayer.getGame().leave(skyWarsPlayer,true);
                }else{
                    player.sendMessage(ChatColor.RED+"You are not in any game!");
                }
            }
        }
        return true;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        SkyWarsPlayer skyWarsPlayer = new SkyWarsPlayer(event.getPlayer());
    }
}
