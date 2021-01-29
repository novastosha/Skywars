package dev.nova.skywars.arena;

import dev.nova.skywars.player.SkyWarsPlayer;
import dev.nova.skywars.ui.ArenaListGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class ArenaCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equalsIgnoreCase("join")){
            if(!(sender instanceof Player)){
                return true;
            }
            Player player = (Player) sender;
            if(args.length == 0){
                player.sendMessage(ChatColor.RED+"Please complete your command with an Arena ID!");
                return true;
            }
            if(args.length > 1){
                player.sendMessage(ChatColor.RED+"Way too many arguments!");
            }
            try{
                Arena arena = ArenaManager.getArena(Integer.parseInt(args[0]));
                if(arena == null){
                    player.sendMessage(ChatColor.RED+"No arena exists with the id: "+args[0]);
                    return true;
                }
                arena.join(SkyWarsPlayer.getPlayer(player));
            }catch (NumberFormatException e){
                player.sendMessage(ChatColor.RED+"The arguments must be a number");
                return true;
            }

        }
        if(command.getName().equalsIgnoreCase("arena-list")){
            if(!(sender instanceof Player)){
                return true;
            }
            Player player = (Player) sender;
            ArenaListGUI.openInventory(player,true);
        }
        if(command.getName().equalsIgnoreCase("forcestart")){
            if(args.length == 0){
                if(!(sender instanceof Player)){
                    return true;
                }
                SkyWarsPlayer player = SkyWarsPlayer.getPlayer((Player) sender);
                if(!player.isInGame()){
                    player.getPlayer().sendMessage(ChatColor.RED+"You are not in a game!");
                    return true;
                }
                Arena arena = player.getGame();
                arena.setState(ArenaState.STARTING);
                arena.setTimeToStart(30);
                arena.setCount(true);
                arena.setForce(true);
                sender.sendMessage(ChatColor.GRAY+"Force starting Arena: "+arena.getID());
            }
            if (args.length == 1){
                try{
                    Arena arena = ArenaManager.getArena(Integer.parseInt(args[0]));
                    if(arena == null){
                        sender.sendMessage(ChatColor.RED+"No arena exists with the id: "+args[0]);
                        return true;
                    }
                    arena.setState(ArenaState.STARTING);
                    arena.setTimeToStart(30);
                    arena.setCount(true);
                    arena.setForce(true);
                    sender.sendMessage(ChatColor.GRAY+"Force starting Arena: "+arena.getID());
                }catch (NumberFormatException e){
                    sender.sendMessage(ChatColor.RED+"The argument must be a number");
                    return true;
                }
            }
        }
        if(command.getName().equalsIgnoreCase("finalarena")){
            if(args.length == 0){
                sender.sendMessage(ChatColor.RED+"Missing arguments!");
                return true;
            }
            if(args.length == 1){
                sender.sendMessage(ChatColor.RED+"Missing arguments!");
                return true;
            }
            if(args[0].equalsIgnoreCase("disable")) {
                FinalArena arena = ArenaManager.getFinalArena(args[1]);
                if (arena == null) {
                    sender.sendMessage(ChatColor.RED + "Can't find any arena with this name");
                    return true;
                }
                File arenaFile = new File("./plugins/Skywars/arenas", arena.getCodeName() + ".yml");
                if (arenaFile.exists()) {
                    arenaFile.renameTo(new File("./plugins/Skywars/arenas","-" + arena.getCodeName()+".yml"));
                    ArenaManager.getFinaArenas().remove(arena);
                    sender.sendMessage(ChatColor.RED + "Disabled the arena: " + arena.getCodeName());
                    Bukkit.getConsoleSender().sendMessage("[SKYWARS] " + ChatColor.RED + "The arena: " + arena.getCodeName() + " has been disabled by an administrator.");

                }else{
                    sender.sendMessage(ChatColor.RED+"The arena: "+arena.getCodeName()+" is already disabled!");
                }
            }
            if(args[0].equalsIgnoreCase("enable")) {
                File arenaFile = new File("./plugins/Skywars/arenas", "-"+args[1] + ".yml");
                if (arenaFile.exists()) {

                    sender.sendMessage(ChatColor.YELLOW+"Attempting to enable the arena: "+args[1]);
                    if(!ArenaManager.loadArena(new File("./plugins/Skywars/arenas","-"+args[1] + ".yml"))){
                        sender.sendMessage(ChatColor.RED+"There was an error enabling the arena: "+args[1]+ " (See console)");
                        return true;
                    }
                    Bukkit.getConsoleSender().sendMessage("[SKYWARS] " + ChatColor.GREEN + "The arena: " + args[1] + " has been enabled by an administrator.");
                    arenaFile.renameTo(new File("./plugins/Skywars/arenas",args[1] + ".yml"));
                    sender.sendMessage(ChatColor.GREEN + "Enabled the arena: " + args[1]);
                }else{
                    sender.sendMessage(ChatColor.RED+"The arena: "+args[1]+" is already enabled or does not exist!");
                }
            }
            if(args[0].equalsIgnoreCase("remove")){

            }

            //Final arena creation.
            if(args[0].equalsIgnoreCase("create")){
                File arenaFile = new File("./plugins/Skywars/arenas", args[1] + ".yml");
                if (!arenaFile.exists()) {
                    try{
                        arenaFile = new File("./plugins/Skywars/arenas", "-"+args[1] + ".yml");
                        arenaFile.createNewFile();
                        sender.sendMessage(ChatColor.GREEN+"Created the initial arena file...");
                        sender.sendMessage(ChatColor.GREEN+"When you are finished making arena and its ready, type: "+ChatColor.GRAY+"/arena enable "+args[1]);
                    }catch (Exception exception){
                        sender.sendMessage(ChatColor.RED+"Error occurred while trying to create the arena: "+args[1]+" (See console)");
                        exception.printStackTrace();
                    }
                }else{
                    sender.sendMessage(ChatColor.RED+"The arena: "+args[1]+" already exists!");
                }
            }
            if(args[0].equalsIgnoreCase("setWorld")){
                File arenaFile = new File("./plugins/Skywars/arenas", "-"+args[1] + ".yml");
                if (arenaFile.exists()) {
                    try{
                        if(args.length == 3){
                            World world = Bukkit.getWorld(args[2]);
                            if(world == null){
                                sender.sendMessage(ChatColor.RED+"World doesn't exist!");
                                return true;
                            }
                            YamlConfiguration configuration = new YamlConfiguration();
                            configuration.load(arenaFile);
                            configuration.set("data.world",world.getName());
                            configuration.save(arenaFile);
                            sender.sendMessage(ChatColor.GREEN+"The final arena world has been set: "+ChatColor.GRAY+world.getName());
                        }else{
                            sender.sendMessage(ChatColor.RED+"Missing arguments!");
                        }
                    }catch (Exception exception){
                        sender.sendMessage(ChatColor.RED+"Error occurred while trying to create the arena: "+args[1]+" (See console)");
                        exception.printStackTrace();
                    }
                }else{
                    sender.sendMessage(ChatColor.RED+"Unable to find any disabled arena with name: "+args[1]);
                }
            }
        }
        return true;
    }
}
