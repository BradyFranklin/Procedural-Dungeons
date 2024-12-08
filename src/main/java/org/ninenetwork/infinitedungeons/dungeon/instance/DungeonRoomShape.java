package org.ninenetwork.infinitedungeons.dungeon.instance;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class DungeonRoomShape {

    String name;
    String roomIdentifier;
    int orientation;
    String roomType;
    String direction;
    ArrayList<DungeonRoomPoint> gridConsumption;

    public DungeonRoomShape(String name) {
        this.name = name;
    }

}