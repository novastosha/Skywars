package dev.nova.skywars.ui;

import dev.nova.skywars.arena.Arena;
import dev.nova.skywars.arena.ArenaManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ArenaListGUI implements Listener{

    public static void openInventory(Player player,boolean open) {
        Inventory arenaList = Bukkit.createInventory(null, 36, ChatColor.LIGHT_PURPLE + "Arena list");


            int slot= 0;
            for (Arena arena : ArenaManager.arenas) {
                ItemStack arenaItem = null;
                if (arena.getMaxPlayers() == arena.getPlayers().size()) {
                    arenaItem = new ItemStack(Material.STAINED_CLAY, 1, (short) 14);
                }
                if (arena.getMinPlayers() == arena.getPlayers().size()) {
                    arenaItem = new ItemStack(Material.STAINED_CLAY, 1, (short) 1);
                }
                if (arena.getPlayers().size() < arena.getMinPlayers() && arena.getPlayers().size() < arena.getMaxPlayers()) {
                    arenaItem = new ItemStack(Material.STAINED_CLAY, 1, (short) 5);
                }
                ItemMeta itemMeta = arenaItem.getItemMeta();

                itemMeta.setDisplayName(arena.getArenaState().getColor() + arena.getDisplayName());

                List<String> lore = new ArrayList<>();

                if (itemMeta.hasLore()) lore = itemMeta.getLore();

                lore.add("  ");
                lore.add(ChatColor.DARK_GRAY + "§lID: §r" + arena.getArenaState().getColor() + arena.getID());
                lore.add("   ");
                lore.add(ChatColor.DARK_GRAY + "§lPlayers: §r" + arena.getArenaState().getColor() + arena.getPlayers().size() + ChatColor.DARK_GRAY + "§l/§r" + arena.getArenaState().getColor() + arena.getMaxPlayers());

                itemMeta.setLore(lore);

                arenaItem.setItemMeta(itemMeta);
                arenaList.setItem(slot,arenaItem);
                slot++;


        }
        if(open) player.openInventory(arenaList);
        openInventory(player,false);
    }
    @EventHandler
    public void onClick(InventoryClickEvent event){
        String name = ChatColor.stripColor(event.getClickedInventory().getName());
        if(name.equalsIgnoreCase("Arena List")){
            ItemStack item = event.getCurrentItem();

            ItemMeta itemMeta = item.getItemMeta();

            String id = ChatColor.stripColor(itemMeta.getLore().get(1));
            id = id.replaceAll("ID: ","");
            int idInt = Integer.parseInt(id);

            ((Player) event.getWhoClicked()).performCommand("join "+1);
            event.setCancelled(true);
            ((Player) event.getWhoClicked()).closeInventory();
        }
    }
}
