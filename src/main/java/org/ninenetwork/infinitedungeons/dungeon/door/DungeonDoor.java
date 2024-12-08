package org.ninenetwork.infinitedungeons.dungeon;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.FileUtil;
import org.mineacademy.fo.region.Region;
import org.mineacademy.fo.remain.CompParticle;
import org.mineacademy.fo.settings.FileConfig;
import org.ninenetwork.infinitedungeons.dungeon.instance.DungeonRoomInstance;
import org.ninenetwork.infinitedungeons.dungeon.instance.DungeonRoomPoint;
import org.ninenetwork.infinitedungeons.map.SchematicManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class DungeonDoor {

    private ArrayList<DungeonRoomInstance> connectingRooms = new ArrayList<>();

    private Region doorRegion;

    private Location connectionLocation;

    private String directional;

    private File schematic;

    protected DungeonDoor(Region region, File schematic) {
        this.doorRegion = region;
        this.schematic = schematic;
    }

    public void initializeDungeonDoor(Dungeon dungeon, DungeonRoomPoint point, DungeonRoomPoint nextPoint, File schematic, DungeonRoomInstance connector, DungeonRoomInstance connected) {
        setConnectingRooms(connector, connected);
        Location midPoint = DungeonGeneration.findMidPoint(point.getCenterLocation(), nextPoint.getCenterLocation());
        Location finalPaste = point.getCenterLocation();
        int orientation = 0;
        if (point.getCenterLocation().getX() == nextPoint.getCenterLocation().getX()) {
            finalPaste = midPoint.clone().add(-2.0, 0.0, -2.0);
        } else if (point.getCenterLocation().getZ() == nextPoint.getCenterLocation().getZ()) {
            finalPaste = midPoint.clone().add(0.0, 0.0, -2.0);
            orientation = 1;
        }
        SchematicManager.pasteDoor(finalPaste, schematic, orientation);
    }

    public void setConnectingRooms(DungeonRoomInstance connector, DungeonRoomInstance connected) {
        ArrayList<DungeonRoomInstance> rooms = new ArrayList<>();
        rooms.add(connector);
        rooms.add(connected);
        this.connectingRooms = rooms;
    }

    public static void addDungeonDoor(Dungeon dungeon, DungeonRoomPoint point, DungeonRoomPoint nextPoint) {
        if (!Objects.equals(DungeonRoomInstance.getRoomFromPoint(dungeon, point), DungeonRoomInstance.getRoomFromPoint(dungeon, nextPoint))) {
            Location midPoint = DungeonGeneration.findMidPoint(point.getCenterLocation(), nextPoint.getCenterLocation());
            Location firstPoint = null;
            Location secondPoint = null;
            if (point.getCenterLocation().getX() == nextPoint.getCenterLocation().getX()) {
                firstPoint = midPoint.clone().add(2.0, 5.0, 2.0);
                secondPoint = midPoint.clone().add(-2.0, 0.0, -2.0);
            } else if (point.getCenterLocation().getZ() == nextPoint.getCenterLocation().getZ()) {
                firstPoint = midPoint.clone().add(-2.0, 5.0, 2.0);
                secondPoint = midPoint.clone().add(2.0, 0.0, -2.0);
            }
            Region region = new Region(firstPoint, secondPoint);
            File schematic = FileUtil.getFile("DungeonStorage/Schematics/door.schematic");
            DungeonDoorLock dungeonDoor = new DungeonDoorLock(region, schematic);
            dungeonDoor.initializeDungeonDoor(dungeon, point, nextPoint, schematic, DungeonRoomInstance.getRoomFromPoint(dungeon, point), DungeonRoomInstance.getRoomFromPoint(dungeon, nextPoint));
        } else {
            Common.log("Sent door creation command but was overridden.");
        }
    }

}