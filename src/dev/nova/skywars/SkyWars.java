package dev.nova.skywars;

import dev.nova.skywars.arena.Arena;
import dev.nova.skywars.arena.ArenaCommand;
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



    @Override
    public void onEnable() {

        ArenaManager.arenasFinalCache = new ArrayList<>();
        ArenaManager.arenas = new ArrayList<>();
        SkyWarsPlayer.skyWarsPlayerCache = new ArrayList<>();
        ItemStack li = new ItemStack(Material.REDSTONE);
        ItemMeta meta = li.getItemMeta();
        meta.setDisplayName(ChatColor.RED+"Leave Game");
        li.setItemMeta(meta);
        Arena.LEAVE_ITEM = li;


        File plugin = new File("./plugins/Skywars/");
        if(!plugin.exists()) plugin.mkdir();


        File config = new File("./plugins/Skywars/","config.yml");
        FileWriter writer = null;

        if(!config.exists()) {
            try {
                config.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try{
                writer = new FileWriter(config);
            }catch (IOException e){
                e.printStackTrace();
            }
            try {
                writer.write("##########################################################################");
                writer.write("                      GENERAL SKYWARS CONFIG");
                writer.write("##########################################################################");
                writer.flush();
                writer.close();
            } catch (IOException ignored) {

            }
        }

        if(config.exists()){
            try{
                writer = new FileWriter(config);
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        File arenas = new File("./plugins/Skywars/arenas");
        if(!arenas.exists()) arenas.mkdir();


        File cages = new File("./plugins/Skywars/cages");
        if(!cages.exists()) cages.mkdir();

        Bukkit.getConsoleSender().sendMessage("[SKYWARS] "+ ChatColor.GREEN+"Loading arenas...");
        ArenaManager.loadArenas(new File("./plugins/Skywars/arenas"));

        Bukkit.getPluginManager().registerEvents(this,this);

        Bukkit.getConsoleSender().sendMessage("[SKYWARS] "+ ChatColor.GREEN+"Loading cages...");
        CageManager.loadCages(new File("./plugins/Skywars/cages"));

        getCommand("join").setExecutor(new ArenaCommand());
        getCommand("arena-list").setExecutor(new ArenaCommand());
        Bukkit.getPluginManager().registerEvents(new ArenaListGUI(),this);

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
