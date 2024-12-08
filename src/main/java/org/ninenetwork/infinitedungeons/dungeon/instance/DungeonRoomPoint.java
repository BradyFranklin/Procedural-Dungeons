package org.ninenetwork.infinitedungeons.dungeon.instance;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.remain.Remain;
import org.ninenetwork.infinitedungeons.dungeon.Dungeon;

import java.util.ArrayList;

@Getter
@Setter
public class DungeonRoomPoint {

    boolean isAlreadyUsed;
    Location centerLocation;
    int pointPosition;

    public DungeonRoomPoint(int pointPosition, Location centerLocation, boolean isAlreadyUsed) {
        this.isAlreadyUsed = isAlreadyUsed;
        this.centerLocation = centerLocation;
        this.pointPosition = pointPosition;
    }

    public static ArrayList<DungeonRoomPoint> getPointsFromLocations(Dungeon dungeon, ArrayList<Location> locations) {
        ArrayList<DungeonRoomPoint> points = new ArrayList<>();
        for (Location location : locations) {
            points.add(null);
            //Common.log("Size of points in getpointsfromlocations: " + points.size());
        }
        for (DungeonRoomPoint point : dungeon.getPointTracking()) {
            if (locations.contains(point.getCenterLocation())) {
                points.set(locations.indexOf(point.getCenterLocation()),point);
            }
        }
        return points;
    }

}