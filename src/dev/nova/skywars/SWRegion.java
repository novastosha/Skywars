package dev.nova.skywars;

import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

public class SWRegion {

    //SWRegion = SkyWars 3D Region

    private int radius;
    private Location loc2;
    private Location loc1;
    private List<Block> blocks;

    public int getRadius() {
        return radius;
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    public Location getPosition1() {
        return loc1;
    }

    public Location getPosition2() {
        return loc2;
    }


    public Location[] getPositions() {
        return new Location[]{loc1, loc2};
    }


    public SWRegion(Location position1, Location position2) {

        this.loc1 = position1;
        this.loc2 = position2;
        this.radius = 0;

        blocks = processRegion(RegionType.CUBE);

    }

    public SWRegion(Location location, int radius, RegionType regionType) {
        this.loc1 = location;
        this.loc2 = null;
        this.radius = radius;

        switch (regionType) {
            case ROUND:
                blocks = processRegion(RegionType.ROUND);

                break;
            case CUBE_RADIUS:
                blocks = processRegion(RegionType.CUBE_RADIUS);

                break;
        }


    }


    protected List<Block> processRegion(RegionType regionType) {

        List<Block> blocks = new ArrayList<>();


        switch (regionType) {
            case CUBE:
                if (loc1 != null && loc2 != null) {

                    int topBlockX = (loc1.getBlockX() < loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX());
                    int bottomBlockX = (loc1.getBlockX() > loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX());

                    int topBlockY = (loc1.getBlockY() < loc2.getBlockY() ? loc2.getBlockY() : loc1.getBlockY());
                    int bottomBlockY = (loc1.getBlockY() > loc2.getBlockY() ? loc2.getBlockY() : loc1.getBlockY());

                    int topBlockZ = (loc1.getBlockZ() < loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ());
                    int bottomBlockZ = (loc1.getBlockZ() > loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ());


                    for (int x = bottomBlockX; x <= topBlockX; x++) {
                        for (int z = bottomBlockZ; z <= topBlockZ; z++) {
                            for (int y = bottomBlockY; y <= topBlockY; y++) {


                                Block block = loc1.getWorld().getBlockAt(x, y, z);

                                blocks.add(block);

                            }
                        }
                    }
                }
                break;
            case ROUND:
                Location finalLoc = new Location(loc1.getWorld(), loc1.getBlockX(), loc1.getBlockY(), loc1.getBlockZ());
                for (int x = (loc1.getBlockX() - radius); x <= (loc1.getBlockX() + radius); x++) {
                    for (int y = (loc1.getBlockY() - radius); y <= (loc1.getBlockY() + radius); y++) {
                        for (int z = (loc1.getBlockZ() - radius); z <= (loc1.getBlockZ() + radius); z++) {
                            Location l = new Location(loc1.getWorld(), x, y, z);
                            if (l.distance(finalLoc) <= radius) {
                                blocks.add(l.getBlock());
                            }
                        }
                    }
                }
                break;
            case CUBE_RADIUS:

                break;
        }

        return blocks;


    }

    private enum RegionType {
        CUBE,
        CUBE_RADIUS,
        ROUND
    }

}
