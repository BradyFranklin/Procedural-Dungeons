package org.ninenetwork.infinitedungeons.dungeon.instance;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.BlockUtil;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.menu.model.SkullCreator;
import org.mineacademy.fo.model.ConfigSerializable;
import org.mineacademy.fo.model.SimpleHologramStand;
import org.mineacademy.fo.region.Region;
import org.ninenetwork.infinitedungeons.PlayerCache;
import org.ninenetwork.infinitedungeons.animation.SimpleAnimatedHologram;
import org.ninenetwork.infinitedungeons.dungeon.*;
import org.ninenetwork.infinitedungeons.dungeon.door.DungeonDoor;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Getter
@Setter
@RequiredArgsConstructor
public class DungeonRoomInstance implements ConfigSerializable {

    ArrayList<LivingEntity> roomCompleteMobs = new ArrayList<>();

    private DungeonRoomType type;
    private String roomType;
    private List<String> schematics;
    private List<Location> mobSpawnpoints;
    private Map<Location, Boolean> secretMap;
    private String roomIdentifier;
    private int roomRadius;
    private String roomSizeTag;
    private boolean isKeyRoom;

    // Game Logic Values //
    private boolean cleared;
    private boolean completed;
    private int secretAmount;
    private int secretsCompleted;

    ////////////////////////////////////////////////

    private Dungeon dungeon;
    private DungeonRoom dungeonRoom;
    private ArrayList<DungeonRoomPoint> dungeonRoomPoints;
    private DungeonRoomShape shape;
    private DungeonGrid grid;
    private Region roomRegion;
    private Region secondRoomRegion;
    //ForwardPositionLocation: 0 - North, 1 - East, 2 - South, 3 - West
    private int orientation;
    private ArrayList<DungeonDoor> dungeonDoors;
    private ArrayList<DungeonRoomInstance> roomsConnected;
    private List<Location> connectedRoomLocations;
    private boolean isConnected;
    private Location roomCenter;
    private int floorDifference;

    public DungeonRoomInstance(DungeonRoom instance) {

    }

    public DungeonRoomInstance(Dungeon dungeon, DungeonRoom dungeonRoom) {
        this.dungeon = dungeon;
        this.dungeonRoom = dungeonRoom;
        this.type = dungeonRoom.getType();
        this.schematics = dungeonRoom.getSchematics();
        this.roomIdentifier = dungeonRoom.getRoomIdentifier();
        this.roomRadius = dungeonRoom.getRoomRadius();
        this.roomSizeTag = dungeonRoom.getRoomSizeTag();
        this.roomsConnected = new ArrayList<>();
        this.connectedRoomLocations = new ArrayList<>();
        this.floorDifference = findFloorHeight(dungeonRoom);
        this.cleared = false;
        this.completed = false;
        this.secretsCompleted = 0;
        this.isKeyRoom = false;
        this.secretMap = new HashMap<>();
        this.mobSpawnpoints = new ArrayList<>();
        this.dungeon.save();
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
                "Secondary_Room_Region", this.secondRoomRegion,
                "Orientation", this.orientation,
                "Cleared", this.cleared,
                "Completed", this.completed,
                "Secret_Amount", this.secretAmount,
                "Secrets_Completed", this.secretsCompleted,
                "Secret_Mapping", this.secretMap,
                "Room_Center", this.roomCenter,
                "Mob_Spawnpoints", this.mobSpawnpoints,
                "Rooms_Connected", this.connectedRoomLocations);
    }

    public static DungeonRoomInstance deserialize(SerializedMap map) {
        DungeonRoomInstance data = new DungeonRoomInstance();

        data.type = map.get("Type", DungeonRoomType.class);
        data.schematics = map.getStringList("Schematics");
        data.roomIdentifier = map.getString("Room_Identifier");
        data.roomRadius = map.getInteger("Room_Radius");
        data.roomSizeTag = map.getString("Room_Size_Tag");
        data.roomRegion = map.get("Room_Region", Region.class);
        data.secondRoomRegion = map.get("Secondary_Room_Region", Region.class);
        data.orientation = map.getInteger("Orientation");
        data.cleared = map.getBoolean("Cleared");
        data.completed = map.getBoolean("Completed");
        data.secretAmount = map.getInteger("Secret_Amount");
        data.secretsCompleted = map.getInteger("Secrets_Completed");
        data.roomCenter = map.getLocation("Room_Center");
        data.secretMap = map.getMap("Secret_Mapping", Location.class, Boolean.class);
        data.connectedRoomLocations = map.getList("Rooms_Connected", Location.class);
        data.mobSpawnpoints = map.getList("Mob_Spawnpoints", Location.class);

        return data;
    }

    public void runClearRoom(Dungeon dungeon, Location location, DungeonRoomInstance instance) {
        if (instance.isKeyRoom) {
            dropKey(dungeon, location);
            dungeon.setPlayerHasKey(true);
            for (PlayerCache player : dungeon.getPlayerCaches()) {
                Common.tell(player.toPlayer(), "&fYour team has picked up a &8Wither &7Key&f!");
            }
        } else {

        }
        clearRoom(dungeon, instance);
    }

    public void dropClearReward(Dungeon dungeon, Location location) {

    }

    public void dropKey(Dungeon dungeon, Location location) {
        ItemStack item = SkullCreator.itemFromUrl("http://textures.minecraft.net/texture/cce714e19eeb453cc1dc3690d6b8fed8f6eecbdb5a421de2f85ff53eaf08359b");
        AtomicBoolean isFinished = new AtomicBoolean(false);
        location = location.add(0.5, -1.0, 0.5);
        final SimpleHologramStand stand = new SimpleAnimatedHologram(location, item);
        stand.setGlowing(true);
        stand.spawn();
        final Entity entity = stand.getEntity();
        Common.runLater(20 * 5, () -> {
            stand.removeLore();
            stand.remove();
            isFinished.set(false);
        });
    }

    public void clearRoom(Dungeon dungeon, DungeonRoomInstance instance) {
        Location location = instance.getRoomCenter();
        location.setY(BlockUtil.findHighestBlockNoSnow(instance.getRoomCenter()) + 3.0);
        location.getBlock().setType(Material.WHITE_CONCRETE);
        instance.setCleared(true);
        dungeon.getDungeonScore().addClearedRoom();
        //dungeon.scoreChange("Cleared Room");
    }

    public void completeRoom(Dungeon dungeon, DungeonRoomInstance instance) {
        Location location = instance.getRoomCenter();
        location.setY(BlockUtil.findHighestBlockNoSnow(instance.getRoomCenter()));
        location.getBlock().setType(Material.GREEN_CONCRETE);
        instance.setCompleted(true);
        //dungeon.scoreChange("Completed Room");
    }

    public void setKeyRoom(boolean keyRoom) {
        isKeyRoom = keyRoom;
        this.dungeon.save();
    }

    public void addSecretToMap(Location location) {
        this.secretMap.put(location, false);
        this.dungeon.save();
    }

    public boolean checkSecretAlreadyFound(Location location) {
        if (this.secretMap.containsKey(location)) {
            return this.secretMap.get(location);
        }
        return false;
    }

    public void setSecretFound(Location location) {
        this.secretMap.replace(location, false, true);
        this.dungeon.save();
    }

    public void addMobSpawnpoint(Location location) {
        this.mobSpawnpoints.add(location);
        this.dungeon.save();
    }

    public void setMobSpawnpoints(ArrayList<Location> spawnpoints) {
        this.mobSpawnpoints = spawnpoints;
        this.dungeon.save();
    }

    public void removeMobSpawnpoint(Location location) {
        this.mobSpawnpoints.remove(location);
        this.dungeon.save();
    }

    public void setRoomCenter(Location location) {
        this.roomCenter = location;
        this.dungeon.save();
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
        this.dungeon.save();
    }

    public void setType(DungeonRoomType type) {
        this.type = type;
        this.dungeon.save();
    }

    public void setFloorDifference(int floorDifference) {
        this.floorDifference = floorDifference;
        this.dungeon.save();
    }

    public int findFloorHeight(DungeonRoom dungeonRoom) {
        if (dungeonRoom.getFloorHeight() != null) {
            return (int) Math.floor(dungeonRoom.getFloorHeight().getY() - dungeonRoom.getRoomRegion().getPrimary().getY());
        } else {
            return 0;
        }
    }

    public void setDungeonRoomRegions() {
        if (!this.getDungeonRoomPoints().isEmpty()) {
            ArrayList<Location> points = findPrimarySecondary(this.getDungeonRoomPoints());
            if (!points.isEmpty()) {
                this.roomRegion = new Region(points.get(0), points.get(1).clone().add(0.0, 58.0, 0.0));
                if (points.size() > 2) {
                    this.secondRoomRegion = new Region(points.get(2), points.get(3).clone().add(0.0, 58.0, 0.0));
                }
                this.dungeon.save();
            }
        }
    }

    public void setMobSpawnpoints() {

    }

    public String chooseRandomRoomCompletionType() {
        Random rand = new Random();
        int num = rand.nextInt(3);
        if (num == 1) {
            return "Starred";
        } else if (num == 2) {
            return "Miniboss";
        } else {
            return "Starred";
        }
    }

    /*
    public void initializeRoomMobs() {
        int amount = DungeonMobUtil.chooseMobAmount(this.getRoomIdentifier());
        if (type == DungeonRoomType.BLOODRUSH) {
            this.livingEntities = DungeonMobUtil.spawnStarredMobs(this, amount);
            for (LivingEntity entity : this.livingEntities) {
                dungeon.getMobTracking().put(entity, this);
            }
        }
    }
    */

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

    public static DungeonRoomInstance getRoomFromAnyLocation(Dungeon dungeon, Location location) {
        Common.log("Dungeon name " + dungeon.getName());
        for (DungeonRoomInstance instance : dungeon.getDungeonRooms()) {
            Common.log("Instance room name " + instance.getDungeonRoom().getName());
            Region region = instance.getRoomRegion();
            Common.log("Primary " + region.getPrimary().getX() + ", " + region.getPrimary().getZ());
            Common.log("Secondary " + region.getSecondary().getX() + ", " + region.getSecondary().getZ());
            Common.log("Location " + location.getX() + ", " + location.getZ());
            if (isWithin(region.getPrimary(), region.getSecondary(), location)) {
                Common.log("getRoomFromAnyLocation returned a valid room");
                return instance;
            }
            if (instance.getRoomIdentifier().equalsIgnoreCase("1x1x1_L")) {
                Region secondaryRegion = instance.getSecondRoomRegion();
                if (isWithin(secondaryRegion.getPrimary(), secondaryRegion.getSecondary(), location)) {
                    return instance;
                }
            }
        }
        Common.log("getRoomFromAnyLocation returned null");
        return null;
    }

    public static boolean isWithin(Location primary, Location secondary, Location location) {
        int x = (int)location.getX();
        int y = (int)location.getY();
        int z = (int)location.getZ();
        if (primary.getX() > secondary.getX()) {
            if (!((double) x <= primary.getX() && (double) x >= secondary.getX())) {
                Common.log("failed x check 1");
                return false;
            }
        } else {
            if (!((double) x >= primary.getX() && (double) x <= secondary.getX())) {
                Common.log("failed x check 2");
                return false;
            }
        }
        if (primary.getY() >  secondary.getY()) {
            if (!((double) y <= primary.getY() && (double) y >= secondary.getY())) {
                Common.log("failed y check 1");
                return false;
            }
        } else {
            if (!((double) y >= primary.getY() && (double) y <= secondary.getY())) {
                Common.log("failed y check 2");
                return false;
            }
        }
        if (primary.getZ() > secondary.getZ()) {
            if (!((double) z <= primary.getZ() && (double) z >= secondary.getZ())) {
                Common.log("failed z check 1");
                return false;
            }
        } else {
            if (!((double) z >= primary.getZ() && (double) z <= secondary.getZ())) {
                Common.log("failed z check 2");
                return false;
            }
        }
        return true;
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
        if (!instances.contains(instance)) {
            instances.add(instance);
        }
        setRoomsConnected(instances);
        if (!this.connectedRoomLocations.contains(instance.getRoomCenter())) {
            this.connectedRoomLocations.add(instance.getRoomCenter());
        }
        this.dungeon.save();
    }

    public static boolean checkLocationIsGridPoint(Location location, ArrayList<DungeonRoomPoint> gridConsumption) {
        for (DungeonRoomPoint point : gridConsumption) {
            if (location.equals(point.getCenterLocation())) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<Location> findPrimarySecondary(ArrayList<DungeonRoomPoint> gridConsumption) {
        ArrayList<Location> primarySecondary = new ArrayList<>();
        if (!gridConsumption.isEmpty()) {
            Location primaryPoint = null;
            Location secondaryPoint = null;
            Location secondPrimary = null;
            Location secondSecondary = null;
            DungeonRoomShape shape = this.getShape();
            String shapeKey = this.getRoomIdentifier();
            int orientation = this.getOrientation();
            DungeonRoomPoint point1 = gridConsumption.get(0);
            DungeonRoomPoint point2 = null;
            DungeonRoomPoint point3 = null;
            if (gridConsumption.size() > 1) {
                point2 = gridConsumption.get(1);
            }
            if (gridConsumption.size() > 2) {
                point3 = gridConsumption.get(2);
            }
            if (shapeKey.equals("1x1_Square")) {
                primaryPoint = point1.getCenterLocation().clone().add(-14.0, 0.0, -14.0);
                secondaryPoint = point1.getCenterLocation().clone().add(14.0, 0.0, 14.0);
            } else if (shapeKey.equals("2x2_Square")) {
                //not functioning right
                if (point1.getCenterLocation().getX() < point3.getCenterLocation().getX()) {
                    primaryPoint = point1.getCenterLocation().clone().add(-14.0, 0.0, -14.0);
                    secondaryPoint = point3.getCenterLocation().clone().add(14, 0.0, 14.0);
                } else {
                    primaryPoint = point1.getCenterLocation().clone().add(14.0, 0.0, -14.0);
                    secondaryPoint = point3.getCenterLocation().clone().add(-14.0, 0.0, 14.0);
                }
            } else if (shapeKey.equals("1x1x1_L")) {
                Location baseLocation = point2.getCenterLocation();
                if (orientation == 0) {
                    primaryPoint = baseLocation.clone().add(48.0, 0.0, 14.0);
                    secondaryPoint = baseLocation.clone().add(-14.0, 0.0, -14.0);
                    secondPrimary = baseLocation.clone().add(-14.0, 0.0, 15.0);
                    secondSecondary = baseLocation.clone().add(14.0, 0.0, 48.0);
                } else if (orientation == 1) {
                    primaryPoint = baseLocation.clone().add(14.0, 0.0, 48.0);
                    secondaryPoint = baseLocation.clone().add(-14.0, 0.0, -14.0);
                    secondPrimary = baseLocation.clone().add(-14.0, 0.0, -15.0);
                    secondSecondary = baseLocation.clone().add(-48.0, 0.0, 14.0);
                } else if (orientation == 2) {
                    primaryPoint = baseLocation.clone().add(-48.0, 0.0, 14.0);
                    secondaryPoint = baseLocation.clone().add(14.0, 0.0, -14.0);
                    secondPrimary = baseLocation.clone().add(14.0, 0.0, -15.0);
                    secondSecondary = baseLocation.clone().add(-14.0, 0.0, -48.0);
                } else if (orientation == 3) {
                    primaryPoint = baseLocation.clone().add(-14.0, 0.0, -48.0);
                    secondaryPoint = baseLocation.clone().add(14.0, 0.0, 14.0);
                    secondPrimary = baseLocation.clone().add(14.0, 0.0, 15.0);
                    secondSecondary = baseLocation.clone().add(48.0, 0.0, -14.0);
                }
            } else if (shapeKey.equals("1x2_Rectangle") || shapeKey.equals("1x3_Rectangle") || shapeKey.equals("1x4_Rectangle")) {
                DungeonRoomPoint primary = gridConsumption.get(0);
                if (point1.getCenterLocation().getX() == point2.getCenterLocation().getX()) {
                    for (DungeonRoomPoint point : gridConsumption) {
                        if (!point.equals(primary)) {
                            if (point.getCenterLocation().getZ() < primary.getCenterLocation().getZ()) {
                                primary = point;
                            }
                        }
                    }
                } else if (point1.getCenterLocation().getZ() == point2.getCenterLocation().getZ()) {
                    for (DungeonRoomPoint point : gridConsumption) {
                        if (!point.equals(primary)) {
                            if (point.getCenterLocation().getX() < primary.getCenterLocation().getX()) {
                                primary = point;
                            }
                        }
                    }
                }
                primaryPoint = primary.getCenterLocation().clone().add(-14.0, 0.0, -14.0);
                DungeonRoomPoint secondary = gridConsumption.get(0);
                if (point1.getCenterLocation().getX() == point2.getCenterLocation().getX()) {
                    for (DungeonRoomPoint point : gridConsumption) {
                        if (!point.equals(secondary)) {
                            if (point.getCenterLocation().getZ() > secondary.getCenterLocation().getZ()) {
                                secondary = point;
                            }
                        }
                    }
                } else if (point1.getCenterLocation().getZ() == point2.getCenterLocation().getZ()) {
                    for (DungeonRoomPoint point : gridConsumption) {
                        if (!point.equals(secondary)) {
                            if (point.getCenterLocation().getX() > secondary.getCenterLocation().getX()) {
                                secondary = point;
                            }
                        }
                    }
                }
                secondaryPoint = secondary.getCenterLocation().clone().add(14.0, 0.0, 14.0);
            }
            if (primaryPoint != null && secondaryPoint != null) {
                primarySecondary.add(primaryPoint);
                primarySecondary.add(secondaryPoint);
            }
            if (shape.getRoomIdentifier().equalsIgnoreCase("1x1x1_L")) {
                if (secondPrimary != null && secondSecondary != null) {
                    primarySecondary.add(secondPrimary);
                    primarySecondary.add(secondSecondary);
                }
            }
        }
        return primarySecondary;
    }

    /*
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
    */

}
