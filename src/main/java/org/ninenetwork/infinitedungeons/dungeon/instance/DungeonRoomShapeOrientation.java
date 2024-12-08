package org.ninenetwork.infinitedungeons.dungeon.instance;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.util.ArrayList;

public class DungeonRoomShapeOrientation {

    @Setter @Getter
    String shape;

    @Setter @Getter
    boolean isDefaultOrientation;

    @Setter @Getter
    int rotations;

    ArrayList<Location> relativeLocationsNeeded;

    public DungeonRoomShapeOrientation() {
    }

    public static ArrayList<Location> calculateLocationsNeededOrientation(String shape, Location baseLocation, int rotations) {
        ArrayList<Location> locations = new ArrayList<>();
        int baseRoomDiameter = 31;
        int gapSize = 3;
        int sizeTotal = baseRoomDiameter + gapSize;
        switch (shape) {
            case "1x1_Square":
                locations.add(baseLocation);
                break;
            case "2x2_Square":
                if (rotations == 0) {
                    locations.add(baseLocation);
                    locations.add(baseLocation.clone().add(0, 0, sizeTotal));
                    locations.add(baseLocation.clone().add(sizeTotal, 0, sizeTotal));
                    locations.add(baseLocation.clone().add(sizeTotal, 0, 0));
                } else if (rotations == 1) {
                    locations.add(baseLocation);
                    locations.add(baseLocation.clone().add(-sizeTotal, 0, 0));
                    locations.add(baseLocation.clone().add(-sizeTotal, 0, sizeTotal));
                    locations.add(baseLocation.clone().add(0, 0, sizeTotal));
                } else if (rotations == 2) {
                    locations.add(baseLocation);
                    locations.add(baseLocation.clone().add(0, 0, -sizeTotal));
                    locations.add(baseLocation.clone().add(-sizeTotal, 0, -sizeTotal));
                    locations.add(baseLocation.clone().add(-sizeTotal, 0, 0));
                } else if (rotations == 3) {
                    locations.add(baseLocation);
                    locations.add(baseLocation.clone().add(sizeTotal, 0, 0));
                    locations.add(baseLocation.clone().add(sizeTotal, 0, -sizeTotal));
                    locations.add(baseLocation.clone().add(0, 0, -sizeTotal));
                }
                break;
            case "1x1x1_L":
                if (rotations == 0) {
                    locations.add(baseLocation.clone().add(0, 0, sizeTotal));
                    locations.add(baseLocation);
                    locations.add(baseLocation.clone().add(sizeTotal, 0, 0));
                } else if (rotations == 1) {
                    locations.add(baseLocation.clone().add(-sizeTotal, 0, 0));
                    locations.add(baseLocation);
                    locations.add(baseLocation.clone().add(0, 0, sizeTotal));
                } else if (rotations == 2) {
                    locations.add(baseLocation.clone().add(0, 0, -sizeTotal));
                    locations.add(baseLocation);
                    locations.add(baseLocation.clone().add(-sizeTotal, 0, 0));
                } else if (rotations == 3) {
                    locations.add(baseLocation.clone().add(sizeTotal, 0, 0));
                    locations.add(baseLocation);
                    locations.add(baseLocation.clone().add(0, 0, -sizeTotal));
                }
                break;
            case "1x2_Rectangle":
                if (rotations == 0) {
                    locations.add(baseLocation);
                    locations.add(baseLocation.clone().add(0, 0, sizeTotal));
                } else if (rotations == 1) {
                    locations.add(baseLocation);
                    locations.add(baseLocation.clone().add(-sizeTotal, 0, 0));
                } else if (rotations == 2) {
                    locations.add(baseLocation);
                    locations.add(baseLocation.clone().add(0, 0, -sizeTotal));
                } else if (rotations == 3) {
                    locations.add(baseLocation);
                    locations.add(baseLocation.clone().add(sizeTotal, 0, 0));
                }
                break;
            case "1x3_Rectangle":
                if (rotations == 0) {
                    locations.add(baseLocation);
                    locations.add(baseLocation.clone().add(0, 0, sizeTotal));
                    locations.add(baseLocation.clone().add(0, 0, sizeTotal * 2));
                } else if (rotations == 1) {
                    locations.add(baseLocation);
                    locations.add(baseLocation.clone().add(-sizeTotal, 0, 0));
                    locations.add(baseLocation.clone().add(-(sizeTotal * 2), 0, 0));
                } else if (rotations == 2) {
                    locations.add(baseLocation);
                    locations.add(baseLocation.clone().add(0, 0, -sizeTotal));
                    locations.add(baseLocation.clone().add(0, 0, -(sizeTotal * 2)));
                } else if (rotations == 3) {
                    locations.add(baseLocation);
                    locations.add(baseLocation.clone().add(sizeTotal, 0, 0));
                    locations.add(baseLocation.clone().add(sizeTotal * 2, 0, 0));
                }
                break;
            case "1x4_Rectangle":
                if (rotations == 0) {
                    locations.add(baseLocation);
                    locations.add(baseLocation.clone().add(0, 0, sizeTotal));
                    locations.add(baseLocation.clone().add(0, 0, sizeTotal * 2));
                    locations.add(baseLocation.clone().add(0, 0, sizeTotal * 3));
                } else if (rotations == 1) {
                    locations.add(baseLocation);
                    locations.add(baseLocation.clone().add(-sizeTotal, 0, 0));
                    locations.add(baseLocation.clone().add(-(sizeTotal * 2), 0, 0));
                    locations.add(baseLocation.clone().add(-(sizeTotal * 3), 0, 0));
                } else if (rotations == 2) {
                    locations.add(baseLocation);
                    locations.add(baseLocation.clone().add(0, 0, -sizeTotal));
                    locations.add(baseLocation.clone().add(0, 0, -(sizeTotal * 2)));
                    locations.add(baseLocation.clone().add(0, 0, -(sizeTotal * 3)));
                } else if (rotations == 3) {
                    locations.add(baseLocation);
                    locations.add(baseLocation.clone().add(sizeTotal, 0, 0));
                    locations.add(baseLocation.clone().add(sizeTotal * 2, 0, 0));
                    locations.add(baseLocation.clone().add(sizeTotal * 3, 0, 0));
                }
                break;
        }
        return locations;
    }

}