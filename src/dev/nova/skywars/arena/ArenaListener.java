package dev.nova.skywars.arena;

import dev.nova.skywars.player.SkyWarsPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerDropItemEvent;

public class ArenaListener implements Listener {

    @EventHandler
    public void onDrop(PlayerDropItemEvent event){
        SkyWarsPlayer player = SkyWarsPlayer.getPlayer(event.getPlayer());
        if(player == null) return;

        Arena arena = player.getGame();
        if(arena.getArenaState().equals(ArenaState.WAITING) || arena.getArenaState().equals(ArenaState.STARTING) || arena.getArenaState().equals(ArenaState.FULL)){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onHitE(EntityDamageByEntityEvent event){
        if(!((event.getEntity()) instanceof Player)) return;
        SkyWarsPlayer player = SkyWarsPlayer.getPlayer((Player) event.getEntity());
        if(player == null) return;

        Arena arena = player.getGame();
        if(arena.getArenaState().equals(ArenaState.WAITING) || arena.getArenaState().equals(ArenaState.STARTING) || arena.getArenaState().equals(ArenaState.FULL)){
            event.setCancelled(true);
        }else{
            player.setKiller(event.getEntity());
        }
    }

    @EventHandler
    public void onHitB(EntityDamageByBlockEvent event){
        if(!((event.getEntity()) instanceof Player)) return;
        SkyWarsPlayer player = SkyWarsPlayer.getPlayer((Player) event.getEntity());
        if(player == null) return;

        Arena arena = player.getGame();
        if(arena.getArenaState().equals(ArenaState.WAITING) || arena.getArenaState().equals(ArenaState.STARTING) || arena.getArenaState().equals(ArenaState.FULL)) {
            event.setCancelled(true);
        }else{
            if(player.getPlayer().getHealth() == 0){
                switch(event.getCause()){
                    case VOID:
                        if(player.getKiller() != null){
                            arena.sendToAllPlayersAndSpectators(ChatColor.LIGHT_PURPLE+player.getPlayer().getName()+ChatColor.GRAY+" got knocked into the void by "+ChatColor.LIGHT_PURPLE+player.getKiller().getName());
                        }else{
                            arena.sendToAllPlayersAndSpectators(ChatColor.LIGHT_PURPLE+player.getPlayer().getName()+ChatColor.GRAY+" fell into the void");
                        }
                        break;
                    case FALL:
                        if(player.getKiller() != null){
                            arena.sendToAllPlayersAndSpectators(ChatColor.LIGHT_PURPLE+player.getPlayer().getName()+ChatColor.GRAY+" fell to their death by "+ChatColor.LIGHT_PURPLE+player.getKiller().getName());
                        }else{
                            arena.sendToAllPlayersAndSpectators(ChatColor.LIGHT_PURPLE+player.getPlayer().getName()+ChatColor.GRAY+" fell to their death");
                        }
                        break;
                    case LAVA:
                        if(player.getKiller() != null){
                            arena.sendToAllPlayersAndSpectators(ChatColor.LIGHT_PURPLE+player.getPlayer().getName()+ChatColor.GRAY+" burned to death by "+ChatColor.LIGHT_PURPLE+player.getKiller().getName());
                        }else{
                            arena.sendToAllPlayersAndSpectators(ChatColor.LIGHT_PURPLE+player.getPlayer().getName()+ChatColor.GRAY+" burned to death");
                        }
                        break;
                    case FIRE:
                        if(player.getKiller() != null){
                            arena.sendToAllPlayersAndSpectators(ChatColor.LIGHT_PURPLE+player.getPlayer().getName()+ChatColor.GRAY+" got cooked by "+ChatColor.LIGHT_PURPLE+player.getKiller().getName());
                        }else{
                            arena.sendToAllPlayersAndSpectators(ChatColor.LIGHT_PURPLE+player.getPlayer().getName()+ChatColor.GRAY+" cooked to death");
                        }
                        break;
                }
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event){
        SkyWarsPlayer player = SkyWarsPlayer.getPlayer(event.getPlayer());
        if(player == null) return;

        Arena arena = player.getGame();
        if(arena.getArenaState().equals(ArenaState.WAITING) || arena.getArenaState().equals(ArenaState.STARTING) || arena.getArenaState().equals(ArenaState.FULL)){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event){
        SkyWarsPlayer player = SkyWarsPlayer.getPlayer(event.getPlayer());
        if(player == null) return;

        Arena arena = player.getGame();
        if(arena.getArenaState().equals(ArenaState.WAITING) || arena.getArenaState().equals(ArenaState.STARTING) || arena.getArenaState().equals(ArenaState.FULL)){
            event.setCancelled(true);
        }
    }
}
