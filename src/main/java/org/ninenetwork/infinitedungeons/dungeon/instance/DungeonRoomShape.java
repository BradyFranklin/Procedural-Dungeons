package org.ninenetwork.infinitedungeons.dungeon.instance;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.util.ArrayList;

@Getter
@Setter
public class DungeonRoomShape {

    String name;
    String roomIdentifier;
    int orientation;
    String roomType;
    String direction;
    Location roomCenterLocation;
    ArrayList<DungeonRoomPoint> gridConsumption;

    public DungeonRoomShape(String name) {
        this.name = name;
    }

}