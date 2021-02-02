package dev.nova.skywars.arena;

import dev.nova.skywars.cages.CageManager;
import dev.nova.skywars.player.SkyWarsPlayer;
import dev.nova.skywars.ui.ArenaListGUI;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

                File arenaFile = new File("./plugins/Skywars/arenas", args[1] + ".yml");
                if (arenaFile.exists()) {
                    arenaFile.renameTo(new File("./plugins/Skywars/arenas","-" + args[1]+".yml"));
                    if(arena != null) ArenaManager.getFinaArenas().remove(arena);
                    sender.sendMessage(ChatColor.RED + "Disabled the arena: " + args[1]);
                    Bukkit.getConsoleSender().sendMessage("[SKYWARS] " + ChatColor.RED + "The arena: " + args[1] + " has been disabled by an administrator.");

                }else{
                    sender.sendMessage(ChatColor.RED+"The arena: "+args[1]+" is already disabled!");
                }
            }

            if(args[0].equalsIgnoreCase("reload")){
                FinalArena arena = ArenaManager.getFinalArena(args[1]);
                if (arena == null) {
                    sender.sendMessage(ChatColor.RED + "Can't find any arena with this name");
                    return true;
                }
                File arenaFile = new File("./plugins/Skywars/arenas", arena.getCodeName() + ".yml");
                if (arenaFile.exists()) {
                    if(sender instanceof Player){
                        Player player = (Player) sender;
                        player.sendMessage(ChatColor.YELLOW+"Attempting to reload the arena: "+args[1]);
                        player.performCommand("finalarena disable "+args[1]);
                        player.performCommand("finalarena enable "+args[1]);
                    }else {
                        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "Attempting to reload the arena: " + args[1]);
                        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
                        Bukkit.dispatchCommand(console, "finalarena disable " + args[1]);
                        Bukkit.dispatchCommand(console, "finalarena enable " + args[1]);
                    }
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
            if(args[0].equalsIgnoreCase("addChest")) {
                if (sender instanceof Player) {
                    File arenaFile = new File("./plugins/Skywars/arenas", "-" + args[1] + ".yml");
                    if (arenaFile.exists()) {
                        try {
                            if (args.length == 3) {

                                Player player = (Player) sender;

                                YamlConfiguration configuration = new YamlConfiguration();
                                configuration.load(arenaFile);
                                Block block = player.getTargetBlock(null, 100);
                                if(!block.getType().equals(Material.CHEST)){
                                    player.sendMessage(ChatColor.RED+"The block must be a chest!");
                                    return true;
                                }
                                if(ChestType.valueOf(args[2]) == null){
                                    player.sendMessage(ChatColor.RED+"Cannot find this chest type!");
                                    return true;
                                }
                                Location blockLocation = block.getLocation();
                                String blockLocationString = blockLocation.getBlockX()+"_"+blockLocation.getBlockY()+"_"+blockLocation.getBlockZ()+"_"+blockLocation.getWorld().getName();
                                configuration.set("data.chests."+blockLocationString, args[2]);
                                configuration.save(arenaFile);
                                player.sendMessage(ChatColor.GREEN+"The chest has been added!");
                            } else {
                                sender.sendMessage(ChatColor.RED+"Missing arguments!");
                            }
                        } catch (Exception exception) {
                            sender.sendMessage(ChatColor.RED + "Error occurred while trying to create the arena: " + args[1] + " (See console)");
                            exception.printStackTrace();
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "Unable to find any disabled arena with name: " + args[1]);
                    }
                }else{
                    sender.sendMessage(ChatColor.RED+"Only players are able to execute this command!");
                }
            }
            if(args[0].equalsIgnoreCase("addCage")){
                if (sender instanceof Player) {
                    File arenaFile = new File("./plugins/Skywars/arenas", "-" + args[1] + ".yml");
                    if (arenaFile.exists()) {
                        try {
                            if (args.length == 3) {

                                Player player = (Player) sender;

                                YamlConfiguration configuration = new YamlConfiguration();
                                configuration.load(arenaFile);
                                List<Location> cages =  new ArrayList<>();

                                if((List<Location>) configuration.getList("data.cages") != null){
                                    cages = (List<Location>) configuration.getList("data.cages");
                                }

                                cages.add(new Location(player.getWorld(),player.getLocation().getBlockX(),player.getLocation().getBlockY(),player.getLocation().getBlockZ()));

                                configuration.set("data.cages" ,cages);
                                configuration.save(arenaFile);
                            } else {
                                sender.sendMessage(ChatColor.RED+"Missing arguments!");
                            }
                        } catch (Exception exception) {
                            sender.sendMessage(ChatColor.RED + "Error occurred while trying to create the arena: " + args[1] + " (See console)");
                            exception.printStackTrace();
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "Unable to find any disabled arena with name: " + args[1]);
                    }
                }else{
                    sender.sendMessage(ChatColor.RED+"Only players are able to execute this command!");
                }
            }
        }
        return true;
    }
}
