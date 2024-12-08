package org.ninenetwork.infinitedungeons.dungeon.instance;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.model.ConfigSerializable;
import org.mineacademy.fo.region.Region;
import org.ninenetwork.infinitedungeons.dungeon.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class DungeonRoomInstance implements ConfigSerializable {

    private DungeonRoomType type;

    private String roomType;

    private List<String> schematics;

    private String roomIdentifier;

    private int roomRadius;

    private String roomSizeTag;

    ////////////////////////////////////////////////

    private Dungeon dungeon;

    private DungeonRoom dungeonRoom;

    private ArrayList<DungeonRoomPoint> dungeonRoomPoints;

    private DungeonRoomShape shape;

    private DungeonGrid grid;

    private Region roomRegion;

    //ForwardPositionLocation: 0 - North, 1 - East, 2 - South, 3 - West
    private int orientation;

    private ArrayList<Location> possibleDoorLocations;

    private ArrayList<DungeonDoor> dungeonDoors;

    private ArrayList<DungeonRoomInstance> roomsConnected;

    private boolean isConnected;

    public DungeonRoomInstance(DungeonRoom instance) {

    }

    public DungeonRoomInstance(Dungeon dungeon, DungeonRoom dungeonRoom) {
        this.dungeon = dungeon;
        this.dungeonRoom = dungeonRoom;
        this.type = dungeonRoom.getType();
        this.schematics = dungeonRoom.getSchematics();
        this.roomIdentifier = dungeonRoom.getRoomIdentifier();
        this.roomRadius = dungeonRoom.getRoomRadius();
        this.roomRegion = dungeonRoom.getRoomRegion();
        this.roomSizeTag = dungeonRoom.getRoomSizeTag();
        this.roomsConnected = new ArrayList<>();
    }

    @Override
    public SerializedMap serialize() {
        return SerializedMap.ofArray(
                "Type", this.type,
                "Schematics", this.schematics,
                "Room_Identifier", this.roomIdentifier,
                "Room_Radius", this.roomRadius,
                "Room_Size_Tag", this.roomSizeTag,
                "Room_Region", this.roomRegion,
                "Orientation", this.orientation);
    }

    public static DungeonRoomInstance getRoomFromLocation(Dungeon dungeon, Location location) {
        for (DungeonRoomInstance instance : dungeon.getDungeonRooms()) {
            for (DungeonRoomPoint point : instance.getDungeonRoomPoints()) {
                if (point.getCenterLocation().equals(location)) {
                    return instance;
                }
            }
        }
        return null;
    }

    public static DungeonRoomInstance getRoomFromPoint(Dungeon dungeon, DungeonRoomPoint point) {
        for (DungeonRoomInstance instance : dungeon.getDungeonRooms()) {
            if (instance.getDungeonRoomPoints().contains(point)) {
                return instance;
            }
        }
        return null;
    }

    public void addConnectedRoom(DungeonRoomInstance instance) {
        ArrayList<DungeonRoomInstance> instances = getRoomsConnected();
        instances.add(instance);
        setRoomsConnected(instances);
    }

    public static DungeonRoomInstance deserialize(SerializedMap map, DungeonRoom instance) {
        DungeonRoomInstance data = new DungeonRoomInstance(instance);

        data.type = map.get("Type", DungeonRoomType.class);
        data.schematics = map.getStringList("Schematics");
        data.roomIdentifier = map.getString("Room_Identifier");
        data.roomRadius = map.getInteger("Room_Radius");
        data.roomSizeTag = map.getString("Room_Size_Tag");
        data.orientation = map.getInteger("Orientation");

        return data;
    }

    public void initializePossibleDoors(Dungeon dungeon, DungeonRoomInstance dungeonRoomInstance, ArrayList<DungeonRoomPoint> gridConsumption) {
        ArrayList<Location> possibleDoors = new ArrayList<>();
        Location midPoint;
        Location side1;
        Location side2;
        Location side3;
        Location side4;
        for (DungeonRoomPoint point : dungeonRoomPoints) {
            side1 = point.getCenterLocation().clone().add(34.0, 0.0, 0.0);
            side2 = point.getCenterLocation().clone().add(-34.0, 0.0, 0.0);
            side3 = point.getCenterLocation().clone().add(0.0, 0.0, 34.0);
            side4 = point.getCenterLocation().clone().add(0.0, 0.0, -34.0);
            if (!checkLocationIsGridPoint(side1, gridConsumption) && (dungeon.getPointLocations().contains(side1))) {
                midPoint = DungeonGeneration.findMidPoint(point.getCenterLocation(), side1);
                possibleDoors.add(midPoint);
            }
            if (!checkLocationIsGridPoint(side2, gridConsumption) && (dungeon.getPointLocations().contains(side2))) {
                midPoint = DungeonGeneration.findMidPoint(point.getCenterLocation(), side2);
                possibleDoors.add(midPoint);
            }
            if (!checkLocationIsGridPoint(side3, gridConsumption) && (dungeon.getPointLocations().contains(side3))) {
                midPoint = DungeonGeneration.findMidPoint(point.getCenterLocation(), side3);
                possibleDoors.add(midPoint);
            }
            if (!checkLocationIsGridPoint(side4, gridConsumption) && (dungeon.getPointLocations().contains(side4))) {
                midPoint = DungeonGeneration.findMidPoint(point.getCenterLocation(), side4);
                possibleDoors.add(midPoint);
            }
        }
        if (dungeonRoomInstance.roomIdentifier.equals("2x2_Square")) {
            for (Location location : possibleDoors) {
                location.clone().add(0.0, 2.0, 0.0).getBlock().setType(Material.BEDROCK);
            }
        }
        this.possibleDoorLocations = possibleDoors;
    }

    public static boolean checkLocationIsGridPoint(Location location, ArrayList<DungeonRoomPoint> gridConsumption) {
        for (DungeonRoomPoint point : gridConsumption) {
            if (location.equals(point.getCenterLocation())) {
                return true;
            }
        }
        return false;
    }

    public static Location findAdjacentPointDirectional(Location point, int orientation) {
        Location location = point;

        return location;
    }

}
