package dev.nova.skywars.cages;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;

public class CageCommand implements CommandExecutor, Listener
{

    private final HashMap<Player, Location[]> cageLocations = new HashMap<>();

    public HashMap<Player, Location[]> getCageLocations() {
        return cageLocations;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if(args.length == 0){
            commandSender.sendMessage(ChatColor.RED+"Missing arguments!");
        }
        if(args.length == 1){
            if(commandSender instanceof Player) {
                Player player = (Player) commandSender;
                if (args[0].equalsIgnoreCase("wand")) {

                }
            }
        }
        return true;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event){

    }
}
