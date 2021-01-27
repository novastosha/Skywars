package dev.nova.skywars.arena;

import dev.nova.skywars.player.SkyWarsPlayer;
import dev.nova.skywars.ui.ArenaListGUI;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
                    sender.sendMessage(ChatColor.RED+"The arguments must be a number");
                    return true;
                }
            }
        }
        return true;
    }
}
