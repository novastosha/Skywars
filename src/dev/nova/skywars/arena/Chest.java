package dev.nova.skywars.arena;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

public class Chest implements Cloneable{

    private org.bukkit.block.Chest chestBlock;
    private final ChestType chestType;

    public Chest(org.bukkit.block.Chest chestBlock, ChestType chestType){
        this.chestType = chestType;
        this.chestBlock = chestBlock;
    }

    public org.bukkit.block.Chest getChestBlock() {
        return chestBlock;
    }

    public ChestType getChestType() {
        return chestType;
    }

    public void generateLoot(){
        switch (chestType){
            case MID:
                chestBlock.getBlockInventory().addItem(new ItemStack(Material.DIAMOND));
                break;
            case ISLAND:
                chestBlock.getBlockInventory().addItem(new ItemStack(Material.IRON_INGOT));
                break;
        }
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     *
     * PLEASE USE ON ARENA ONLY!
     *
     * @param world World to transform to...
     * @return Chest location on the world
     */
    public Chest cloneChest(World world){
        try{
            Chest chest = (Chest) clone();
            chest.chestBlock = (org.bukkit.block.Chest) world.getBlockAt(chestBlock.getLocation()).getState();
            return chest;
        }catch (CloneNotSupportedException e){
            System.out.println("PLEASE REPORT THIS AS SOON AS POSSIBLE!");
            e.printStackTrace();
        }
        return null;
    }
}
