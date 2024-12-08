package org.ninenetwork.infinitedungeons.dungeon;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.mineacademy.fo.Common;
import org.ninenetwork.infinitedungeons.dungeon.door.DungeonDoor;
import org.ninenetwork.infinitedungeons.dungeon.door.DungeonDoorLock;
import org.ninenetwork.infinitedungeons.dungeon.door.DungeonLobbyDoor;
import org.ninenetwork.infinitedungeons.dungeon.instance.DungeonRoomInstance;
import org.ninenetwork.infinitedungeons.dungeon.instance.DungeonRoomPoint;
import org.ninenetwork.infinitedungeons.settings.Settings;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class DungeonPathfinding {

    protected DungeonGrid grid;
    protected DungeonRoomInstance currentRoom;

    protected Location lobbyCenter;
    protected Location bloodCenter;
    protected double bloodX;
    protected double bloodZ;
    protected double lobbyX;
    protected double lobbyZ;

    protected ArrayList<DungeonRoomPoint> allPointsInDungeon;
    protected ArrayList<Location> allLocationsInDungeon;
    protected List<DungeonRoomInstance> dungeonRooms;

    protected ArrayList<DungeonRoomInstance> bloodRushPath = new ArrayList<>();
    //protected ArrayList<DungeonRoomConnection> allConnections = new ArrayList<>();
    protected DungeonRoomPoint currentEvaluationPoint;
    protected ArrayList<DungeonRoomInstance> roomNetwork = new ArrayList<>();

    protected String directionToMove;
    protected String priorityDirection;
    protected String priorityUnchangeable;

    protected boolean bloodRushPathingDone;
    protected boolean isAligned;
    protected boolean bloodRushAddedNetwork;

    public DungeonPathfinding(Dungeon dungeon, DungeonGrid grid) {
        this.lobbyCenter = dungeon.getLobbyLocation();
        this.bloodCenter = dungeon.getBloodLocation();
        this.allPointsInDungeon = dungeon.getPointTracking();
        this.allLocationsInDungeon = dungeon.getPointLocations();
        this.dungeonRooms = dungeon.getDungeonRooms();
        this.grid = grid;
        this.bloodX = bloodCenter.getX();
        this.bloodZ = bloodCenter.getZ();
        this.lobbyX = lobbyCenter.getX();
        this.lobbyZ = lobbyCenter.getZ();
        this.currentRoom = grid.getLobbyRoom(dungeon);
        this.bloodRushPathingDone = false;
        this.isAligned = false;
        this.bloodRushAddedNetwork = false;
    }

    public void initializePathfinding(Dungeon dungeon, DungeonGrid grid) {
        if (!dungeon.getDungeonIntegrity().isIntegrityMaintained()) {
            return;
        }
        dungeon.getDungeonIntegrity().setBloodPathfinding(DungeonIntegrityStatus.RUNNING);

        setDirectionToMove("Move " + grid.getBloodRoom(dungeon).getShape().getDirection());
        this.setAligned(lobbyX == bloodX || lobbyZ == bloodZ);
        setPriorityDirection(getPriorityDirection(lobbyCenter, bloodCenter, directionToMove));
        bloodRushPath.add(grid.getLobbyRoom(dungeon));
        boolean generateAlignedNewPoint = false;

        findFirstMove(dungeon);
        int integrity = 0;
        while (!bloodRushPathingDone) {
            integrity++;
            if (integrity > 100) {
                break;
            }
            if (!this.isAligned) {
                DungeonRoomPoint point = findPriorityBestPoint(getCurrentRoom());
                if (point != null) {
                    if (point.getCenterLocation().getX() == bloodX || point.getCenterLocation().getZ() == bloodZ) {
                        if (point.getCenterLocation().getX() == bloodX) {
                            Common.log("set aligned with blood to true");
                            this.setAligned(true);
                            setPriorityUnchangeable("x");
                        } else if (point.getCenterLocation().getZ() == bloodZ) {
                            Common.log("set aligned with blood to true");
                            this.setAligned(true);
                            setPriorityUnchangeable("z");
                        }
                        setCurrentEvaluationPoint(point);
                    } else {
                        DungeonRoomPoint nextPoint = findNextPointFromCurrentPoint(dungeon, point, getPriorityDirection());
                        if (nextPoint != null) {
                            if (DungeonRoomInstance.getRoomFromPoint(dungeon, nextPoint) == null) {
                                setDirectionToMove(reverseDirection(getPriorityDirection()));
                                point = findBestPoint(getCurrentRoom());
                                nextPoint = findNextPointFromCurrentPoint(dungeon, point, getDirectionToMove());
                            }
                            DungeonRoomInstance roomInstance = DungeonRoomInstance.getRoomFromPoint(dungeon, nextPoint);
                            Location midPoint = DungeonGeneration.findMidPoint(point.getCenterLocation(), nextPoint.getCenterLocation());
                            midPoint.getBlock().setType(Material.REDSTONE_BLOCK);
                            DungeonDoorLock.addLockedDungeonDoor(dungeon, point, nextPoint);
                            setCurrentEvaluationPoint(nextPoint);
                            bloodRushPath.add(roomInstance);
                            setCurrentRoom(roomInstance);
                            Common.log("Added room as priority at " + nextPoint.getCenterLocation().getX() + " " + nextPoint.getCenterLocation().getY() + " " + nextPoint.getCenterLocation().getZ()
                                    + " Blood xz is " + bloodX + " " + bloodZ);

                        } else {
                            dungeon.getDungeonIntegrity().setBloodPathfinding(DungeonIntegrityStatus.FAILED);
                            dungeon.getDungeonIntegrity().changeIntegrity(false, "BloodPathfinding");
                        }
                    }
                }
            } else {
                DungeonRoomPoint point;
                if (!generateAlignedNewPoint) {
                    point = getCurrentEvaluationPoint();
                    generateAlignedNewPoint = true;
                } else {
                    point = findBestPoint(getCurrentRoom());
                }
                DungeonRoomPoint nextPoint = findNextPointFromCurrentPoint(dungeon, point, getDirectionToMove());
                //added or next point null check
                if (DungeonRoomInstance.getRoomFromPoint(dungeon, nextPoint) == null || nextPoint == null) {
                    setDirectionToMove(reverseDirection(getPriorityDirection()));
                    point = findBestPoint(getCurrentRoom());
                    nextPoint = findNextPointFromCurrentPoint(dungeon, point, getDirectionToMove());
                }
                DungeonRoomInstance roomInstance = DungeonRoomInstance.getRoomFromPoint(dungeon, nextPoint);
                Location midPoint = DungeonGeneration.findMidPoint(point.getCenterLocation(), nextPoint.getCenterLocation());
                midPoint.getBlock().setType(Material.REDSTONE_BLOCK);
                DungeonDoorLock.addLockedDungeonDoor(dungeon, point, nextPoint);
                setCurrentEvaluationPoint(nextPoint);
                setCurrentRoom(roomInstance);
                Common.log("Added room as non priority at " + nextPoint.getCenterLocation().getX() + " " + nextPoint.getCenterLocation().getY() + " " + nextPoint.getCenterLocation().getZ()
                         + " Blood xz is " + bloodX + " " + bloodZ);
                if (nextPoint.getCenterLocation().equals(bloodCenter)) {
                    setBloodRushPathingDone(true);
                    bloodRushPath.add(DungeonRoomInstance.getRoomFromPoint(dungeon, nextPoint));

                    int instanceIterator = 0;
                    for (DungeonRoomInstance instance : bloodRushPath) {
                        int pointIterator = 0;
                        for (DungeonRoomPoint check : instance.getDungeonRoomPoints()) {
                            Common.log("BR Pathfinding room posinlist " + instanceIterator + " Coords for point " + pointIterator + ": " + check.getCenterLocation().getX() + " " + check.getCenterLocation().getY() + " " + check.getCenterLocation().getZ());
                            pointIterator++;
                        }
                        instanceIterator++;
                    }
                    //initializePathFindingSecondary(dungeon, grid);
                    connectRemainingRooms(dungeon);
                    dungeon.getDungeonIntegrity().setBloodPathfinding(DungeonIntegrityStatus.COMPLETED);
                    break;
                } else {
                    bloodRushPath.add(roomInstance);
                }
            }
        }

    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void connectRemainingRooms(Dungeon dungeon) {
        for (DungeonRoomInstance instance : getBloodRushPath()) {
            instance.setConnected(true);
        }
        for (DungeonRoomInstance instance : dungeon.getDungeonRooms()) {
            if (!instance.isConnected()) {
                if (!connectRoom(dungeon, instance, true)) {
                    if (connectRoom(dungeon, instance, false)) {
                        Common.log("Successful room connection");
                    }
                }
            }
        }
        verifyAllReachable(dungeon);
    }

    public void verifyAllReachable(Dungeon dungeon) {
        ArrayList<DungeonRoomInstance> unreachable = new ArrayList<>();
        if (!bloodRushAddedNetwork) {
            for (DungeonRoomInstance instance : getBloodRushPath()) {
                addRoomToNetwork(instance);
            }
            this.bloodRushAddedNetwork = true;
        }
        for (DungeonRoomInstance instance : getBloodRushPath()) {
            if (!instance.equals(DungeonRoomInstance.getRoomFromLocation(dungeon, bloodCenter)) && !instance.equals(DungeonRoomInstance.getRoomFromLocation(dungeon, lobbyCenter))) {
                for (DungeonRoomInstance i : instance.getRoomsConnected()) {
                    addRoomToNetwork(i);
                    makePathAddToNetwork(i);
                }
            }
        }
        for (DungeonRoomInstance instance : dungeon.getDungeonRooms()) {
            if (!getRoomNetwork().contains(instance)) {
                unreachable.add(instance);
                Common.log("Fixing unreachable room " + instance.getDungeonRoomPoints().get(0).getCenterLocation().getX() + " " + instance.getDungeonRoomPoints().get(0).getCenterLocation().getZ());
            }
        }
        if (!unreachable.isEmpty()) {
            makeReachable(dungeon, unreachable);
        } else {
            // GENERATION ENDS BEGIN DUNGEON LOGIC //
            Common.log("Beginning Dungeon logic");
            dungeon.getDungeonSecretInstance().initializeAllDungeonSecrets();
            registerPuzzles(dungeon);
            //dungeon.initializeMobsAllRooms(dungeon);
            for (DungeonRoomInstance instance : this.bloodRushPath) {
                if (instance.getType() != DungeonRoomType.BLOOD && instance.getType() != DungeonRoomType.LOBBY) {
                    instance.setKeyRoom(true);
                }
            }
            dungeon.setGenerationComplete(true);
            DungeonScore score = dungeon.getDungeonScore();
            score.setSecretRequirement(dungeon);
            dungeon.initializeDungeonLobby();
        }

    }

    public void registerPuzzles(Dungeon dungeon) {
        int puzzles = 0;
        for (DungeonRoomInstance instance : dungeon.getDungeonRooms()) {
            if (instance.getRoomIdentifier().equalsIgnoreCase("1x1_Square")) {
                Common.log("Register puzzles passed identifier");
                Common.log("RoomType is " + instance.getType());
                if (instance.getType() != DungeonRoomType.LOBBY && instance.getType() != DungeonRoomType.BLOOD) {
                    Common.log("Register puzzles passed not lobbyblood");
                    Common.log("Register puzzles connected size " + instance.getRoomsConnected().size());
                    if (instance.getRoomsConnected().size() == 1) {
                        if (puzzles < 5) {
                            instance.setRoomType("puzzle");
                            instance.setType(DungeonRoomType.PUZZLE);
                            puzzles++;
                            Common.log("Added " + instance.getRoomCenter().getX() + ", " + instance.getRoomCenter().getZ() + " as a puzzle room during generation");
                        }
                    }
                }
            }
        }
    }


    public void registerAndReplacePuzzles() {

    }

    public void makeReachable(Dungeon dungeon, ArrayList<DungeonRoomInstance> unreachable) {
        DungeonRoomInstance checkingRoom;
        boolean allReachable = false;
        Location location1;
        Location location2;
        Location location3;
        Location location4;
        for (DungeonRoomInstance room : unreachable) {
            for (DungeonRoomPoint point : room.getDungeonRoomPoints()) {
                location1 = point.getCenterLocation().clone().add(-34.0, 0.0, 0.0);
                location2 = point.getCenterLocation().clone().add(34.0, 0.0, 0.0);
                location3 = point.getCenterLocation().clone().add(0.0, 0.0, -34.0);
                location4 = point.getCenterLocation().clone().add(0.0, 0.0, 34.0);
                if (getRoomNetwork().contains(DungeonRoomInstance.getRoomFromLocation(dungeon, location1))) {
                    checkingRoom = DungeonRoomInstance.getRoomFromLocation(dungeon, location1);
                    if (checkingRoom != null) {
                        if (!location1.equals(bloodCenter) && !location1.equals(lobbyCenter)) {
                            room.addConnectedRoom(DungeonRoomInstance.getRoomFromLocation(dungeon, location1));
                            checkingRoom.addConnectedRoom(room);
                            DungeonDoor.addDungeonDoor(dungeon, point, getPointFromLocation(dungeon, location1));
                            addRoomToNetwork(room);
                            makePathAddToNetwork(room);
                            verifyAllReachable(dungeon);
                            return;
                        }
                    }
                }
                if (getRoomNetwork().contains(DungeonRoomInstance.getRoomFromLocation(dungeon, location2))) {
                    checkingRoom = DungeonRoomInstance.getRoomFromLocation(dungeon, location2);
                    if (checkingRoom != null) {
                        if (!location2.equals(bloodCenter) && !location2.equals(lobbyCenter)) {
                            room.addConnectedRoom(DungeonRoomInstance.getRoomFromLocation(dungeon, location2));
                            checkingRoom.addConnectedRoom(room);
                            DungeonDoor.addDungeonDoor(dungeon, point, getPointFromLocation(dungeon, location2));
                            addRoomToNetwork(room);
                            makePathAddToNetwork(room);
                            verifyAllReachable(dungeon);
                            return;
                        }
                    }
                }
                if (getRoomNetwork().contains(DungeonRoomInstance.getRoomFromLocation(dungeon, location3))) {
                    checkingRoom = DungeonRoomInstance.getRoomFromLocation(dungeon, location3);
                    if (checkingRoom != null) {
                        if (!location3.equals(bloodCenter) && !location3.equals(lobbyCenter)) {
                            room.addConnectedRoom(DungeonRoomInstance.getRoomFromLocation(dungeon, location3));
                            checkingRoom.addConnectedRoom(room);
                            DungeonDoor.addDungeonDoor(dungeon, point, getPointFromLocation(dungeon, location3));
                            addRoomToNetwork(room);
                            makePathAddToNetwork(room);
                            verifyAllReachable(dungeon);
                            return;
                        }
                    }
                }
                if (getRoomNetwork().contains(DungeonRoomInstance.getRoomFromLocation(dungeon, location4))) {
                    checkingRoom = DungeonRoomInstance.getRoomFromLocation(dungeon, location4);
                    if (checkingRoom != null) {
                        if (!location4.equals(bloodCenter) && !location4.equals(lobbyCenter)) {
                            room.addConnectedRoom(DungeonRoomInstance.getRoomFromLocation(dungeon, location4));
                            checkingRoom.addConnectedRoom(room);
                            DungeonDoor.addDungeonDoor(dungeon, point, getPointFromLocation(dungeon, location4));
                            addRoomToNetwork(room);
                            makePathAddToNetwork(room);
                            verifyAllReachable(dungeon);
                            return;
                        }
                    }
                }
            }
        }
    }

    public void makePathAddToNetwork(DungeonRoomInstance baseRoom) {
        for (DungeonRoomInstance instance : baseRoom.getRoomsConnected()) {
            if (!bloodRushPath.contains(instance)) {
                if (!getRoomNetwork().contains(instance)) {
                    addRoomToNetwork(instance);
                    makePathAddToNetwork(instance);
                }
            }
        }
    }

    public boolean connectRoom(Dungeon dungeon, DungeonRoomInstance instance, boolean bloodRushOnly) {
        Location location1;
        Location location2;
        Location location3;
        Location location4;
        DungeonRoomInstance checkingRoom;
        for (DungeonRoomPoint point : instance.getDungeonRoomPoints()) {
            location1 = point.getCenterLocation().clone().add(-34.0, 0.0, 0.0);
            location2 = point.getCenterLocation().clone().add(34.0, 0.0, 0.0);
            location3 = point.getCenterLocation().clone().add(0.0, 0.0, -34.0);
            location4 = point.getCenterLocation().clone().add(0.0, 0.0, 34.0);
            if (!Objects.equals(DungeonRoomInstance.getRoomFromLocation(dungeon, location1), instance) && dungeon.isLocationPartOfDungeon(dungeon, location1) && !getBloodCenter().equals(location1) && !getLobbyCenter().equals(location1)) {
                checkingRoom = DungeonRoomInstance.getRoomFromLocation(dungeon, location1);
                if (checkingRoom != null) {
                    if (bloodRushOnly) {
                        if (getBloodRushPath().contains(checkingRoom)) {
                            instance.setConnected(true);
                            instance.addConnectedRoom(checkingRoom);
                            checkingRoom.addConnectedRoom(instance);
                            DungeonDoor.addDungeonDoor(dungeon, point, getPointFromLocation(dungeon, location1));
                            return true;
                        }
                    } else {
                        if (!getBloodRushPath().contains(checkingRoom)) {
                            instance.setConnected(true);
                            instance.addConnectedRoom(checkingRoom);
                            checkingRoom.addConnectedRoom(instance);
                            DungeonDoor.addDungeonDoor(dungeon, point, getPointFromLocation(dungeon, location1));
                            return true;
                        } else {
                            Common.log("Checked room was blood rush, this should not happen");
                        }
                    }
                }
            }
            if (!Objects.equals(DungeonRoomInstance.getRoomFromLocation(dungeon, location2), instance) && dungeon.isLocationPartOfDungeon(dungeon, location2) && !getBloodCenter().equals(location2) && !getLobbyCenter().equals(location2)) {
                checkingRoom = DungeonRoomInstance.getRoomFromLocation(dungeon, location2);
                if (checkingRoom != null) {
                    if (bloodRushOnly) {
                        if (getBloodRushPath().contains(checkingRoom)) {
                            instance.setConnected(true);
                            instance.addConnectedRoom(checkingRoom);
                            checkingRoom.addConnectedRoom(instance);
                            DungeonDoor.addDungeonDoor(dungeon, point, getPointFromLocation(dungeon, location2));
                            return true;
                        }
                    } else {
                        if (!getBloodRushPath().contains(checkingRoom)) {
                            instance.setConnected(true);
                            instance.addConnectedRoom(checkingRoom);
                            checkingRoom.addConnectedRoom(instance);
                            DungeonDoor.addDungeonDoor(dungeon, point, getPointFromLocation(dungeon, location2));
                            return true;
                        } else {
                            Common.log("Checked room was blood rush, this should not happen");
                        }
                    }
                }
            }
            if (!Objects.equals(DungeonRoomInstance.getRoomFromLocation(dungeon, location3), instance) && dungeon.isLocationPartOfDungeon(dungeon, location3) && !getBloodCenter().equals(location3) && !getLobbyCenter().equals(location3)) {
                checkingRoom = DungeonRoomInstance.getRoomFromLocation(dungeon, location3);
                if (checkingRoom != null) {
                    if (bloodRushOnly) {
                        if (getBloodRushPath().contains(checkingRoom)) {
                            instance.setConnected(true);
                            instance.addConnectedRoom(checkingRoom);
                            checkingRoom.addConnectedRoom(instance);
                            DungeonDoor.addDungeonDoor(dungeon, point, getPointFromLocation(dungeon, location3));
                            return true;
                        }
                    } else {
                        if (!getBloodRushPath().contains(checkingRoom)) {
                            instance.setConnected(true);
                            instance.addConnectedRoom(checkingRoom);
                            checkingRoom.addConnectedRoom(instance);
                            DungeonDoor.addDungeonDoor(dungeon, point, getPointFromLocation(dungeon, location3));
                            return true;
                        } else {
                            Common.log("Checked room was blood rush, this should not happen");
                        }
                    }
                }
            }
            if (!Objects.equals(DungeonRoomInstance.getRoomFromLocation(dungeon, location4), instance) && dungeon.isLocationPartOfDungeon(dungeon, location4) && !getBloodCenter().equals(location4) && !getLobbyCenter().equals(location4)) {
                checkingRoom = DungeonRoomInstance.getRoomFromLocation(dungeon, location4);
                if (checkingRoom != null) {
                    if (bloodRushOnly) {
                        if (getBloodRushPath().contains(checkingRoom)) {
                            instance.setConnected(true);
                            instance.addConnectedRoom(checkingRoom);
                            checkingRoom.addConnectedRoom(instance);
                            DungeonDoor.addDungeonDoor(dungeon, point, getPointFromLocation(dungeon, location4));
                            return true;
                        }
                    } else {
                        if (!getBloodRushPath().contains(checkingRoom)) {
                            instance.setConnected(true);
                            instance.addConnectedRoom(checkingRoom);
                            checkingRoom.addConnectedRoom(instance);
                            DungeonDoor.addDungeonDoor(dungeon, point, getPointFromLocation(dungeon, location4));
                            return true;
                        } else {
                            Common.log("Checked room was blood rush, this should not happen");
                        }
                    }
                }
            }
        }
        return false;
    }

    public void addRoomToNetwork(DungeonRoomInstance instance) {
        ArrayList<DungeonRoomInstance> network;
        network = getRoomNetwork();
        if (!network.contains(instance)) {
            network.add(instance);
        }
        setRoomNetwork(network);
    }

    public String reverseDirection(String direction) {
        if (direction.equalsIgnoreCase("Move left")) {
            return "Move right";
        } else if (direction.equalsIgnoreCase("Move right")) {
            return "Move left";
        } else if (direction.equalsIgnoreCase("Move top")) {
            return "Move bottom";
        } else if (direction.equalsIgnoreCase("Move bottom")) {
            return "Move top";
        }
        return "not found";
    }

    public void findFirstMove(Dungeon dungeon) {
        DungeonRoomInstance roomInstance;
        Location nextLocation = findNextDirectionalLocation(lobbyCenter, directionToMove);
        Location midPoint = DungeonGeneration.findMidPoint(lobbyCenter, nextLocation);

        midPoint.getBlock().setType(Material.REDSTONE_BLOCK);
        //addDungeonDoor(dungeon, midPoint);
        DungeonRoomPoint nextPoint = getPointFromLocation(dungeon, nextLocation);
        setCurrentEvaluationPoint(nextPoint);
        roomInstance = DungeonRoomInstance.getRoomFromLocation(dungeon, nextLocation);
        bloodRushPath.add(roomInstance);
        DungeonLobbyDoor.addLobbyDungeonDoor(dungeon, getPointFromLocation(dungeon, lobbyCenter), nextPoint);
        setCurrentRoom(roomInstance);
    }

    public DungeonRoomPoint findBestPoint(DungeonRoomInstance instance) {
        DungeonRoomPoint chosenPoint = getCurrentEvaluationPoint();
        double chosenX;
        double chosenZ;
        double x;
        double z;
            if (instance != null) {
            ArrayList<DungeonRoomPoint> points = instance.getDungeonRoomPoints();
            points.remove(getCurrentEvaluationPoint());
            String direction = getDirectionToMove();
            for (DungeonRoomPoint point : points) {
                x = point.getCenterLocation().getX();
                z = point.getCenterLocation().getZ();
                chosenX = chosenPoint.getCenterLocation().getX();
                chosenZ = chosenPoint.getCenterLocation().getZ();
                if (direction.equalsIgnoreCase("Move left")) {
                    //prioritize -x
                    if (x < chosenX && z == chosenZ) {
                        chosenPoint = point;
                    }
                } else if (direction.equalsIgnoreCase("Move right")) {
                    //prioritize X
                    if (x > chosenX && z == chosenZ) {
                        chosenPoint = point;
                    }
                } else if (direction.equalsIgnoreCase("Move top")) {
                    //prioritize -z
                    if (z < chosenZ && x == chosenX) {
                        chosenPoint = point;
                    }
                } else if (direction.equalsIgnoreCase("Move bottom")) {
                    //prioritize z
                    if (z > chosenZ && x == chosenX) {
                        chosenPoint = point;
                    }
                }
            }
        }
        return chosenPoint;
    }

    public DungeonRoomPoint findPriorityBestPoint(DungeonRoomInstance instance) {
        DungeonRoomPoint chosenPoint = getCurrentEvaluationPoint();
        double chosenX;
        double chosenZ;
        double x;
        double z;
        if (instance != null) {
            ArrayList<DungeonRoomPoint> points = instance.getDungeonRoomPoints();
            //added this check, idk?
            if (points.size() > 1) {
                points.remove(getCurrentEvaluationPoint());
            }
            String priority = getPriorityDirection();
            for (DungeonRoomPoint point : points) {
                x = point.getCenterLocation().getX();
                z = point.getCenterLocation().getZ();
                chosenX = chosenPoint.getCenterLocation().getX();
                chosenZ = chosenPoint.getCenterLocation().getZ();
                if (priority.equalsIgnoreCase("Move left")) {
                    //prioritize -x
                    if (x < chosenX && ensurePriorityNotPastBlood(point, priority)) {
                        chosenPoint = point;
                    }
                } else if (priority.equalsIgnoreCase("Move right")) {
                    //prioritize X
                    if (x > chosenX && ensurePriorityNotPastBlood(point, priority)) {
                        chosenPoint = point;
                    }
                } else if (priority.equalsIgnoreCase("Move top")) {
                    //prioritize -z
                    if (z < chosenZ && ensurePriorityNotPastBlood(point, priority)) {
                        chosenPoint = point;
                    }
                } else if (priority.equalsIgnoreCase("Move bottom")) {
                    //prioritize z
                    if (z > chosenZ && ensurePriorityNotPastBlood(point, priority)) {
                        chosenPoint = point;
                    }
                }
            }
            double chosenPointX = chosenPoint.getCenterLocation().getX();
            double chosenPointZ = chosenPoint.getCenterLocation().getZ();
            if (priority.equalsIgnoreCase("Move right") || priority.equalsIgnoreCase("Move left")) {
                for (DungeonRoomPoint p : points) {
                    if (p.getCenterLocation().getX() == chosenPointX) {
                        if (getDirectionToMove().equalsIgnoreCase("Move top")) {
                            if (p.getCenterLocation().getZ() < chosenPointZ) {
                                chosenPoint = p;
                            }
                        } else if (getDirectionToMove().equalsIgnoreCase("Move bottom")) {
                            if (p.getCenterLocation().getZ() > chosenPointZ) {
                                chosenPoint = p;
                            }
                        }
                    }
                }
            } else if (priority.equalsIgnoreCase("Move top") || priority.equalsIgnoreCase("Move bottom")) {
                for (DungeonRoomPoint p : points) {
                    if (p.getCenterLocation().getZ() == chosenPointZ) {
                        if (getDirectionToMove().equalsIgnoreCase("Move left")) {
                            if (p.getCenterLocation().getX() < chosenPointX) {
                                chosenPoint = p;
                            }
                        } else if (getDirectionToMove().equalsIgnoreCase("Move right")) {
                            if (p.getCenterLocation().getX() > chosenPointX) {
                                chosenPoint = p;
                            }
                        }
                    }
                }
            }
            if (chosenPoint.getCenterLocation().getX() == bloodX) {
                setAligned(true);
                setPriorityUnchangeable("x");
            } else if (chosenPoint.getCenterLocation().getZ() == bloodZ) {
                setAligned(true);
                setPriorityUnchangeable("z");
            }
            if (!isAligned) {
                for (DungeonRoomPoint po : points) {
                    if (po.getCenterLocation().getX() == bloodX || po.getCenterLocation().getZ() == bloodZ) {
                        Common.log("Should have been aligned. Point " + po.getCenterLocation().getX() + " " + po.getCenterLocation().getZ() + " is aligned but chosen point "
                                + chosenPointX + " " + chosenPointZ);
                        Common.log("Force Setting point for aligned");
                        chosenPoint = po;
                    }
                }
            }
            return chosenPoint;
        }
        return null;
    }

    //changed by adding or = to's
    // changed all from or = to bloodx 5/25/2024 to test
    // added back or = to because it's negated
    public boolean ensurePriorityNotPastBlood(DungeonRoomPoint point, String priorityDirection) {
        if (priorityDirection.equalsIgnoreCase("Move left")) {
            //prioritize -x
            return !(point.getCenterLocation().getX() <= getBloodX());
        } else if (priorityDirection.equalsIgnoreCase("Move right")) {
            //prioritize X
            return !(point.getCenterLocation().getX() >= getBloodX());
        } else if (priorityDirection.equalsIgnoreCase("Move top")) {
            //prioritize -z
            return !(point.getCenterLocation().getZ() <= getBloodZ());
        } else if (priorityDirection.equalsIgnoreCase("Move bottom")) {
            //prioritize z
            return !(point.getCenterLocation().getZ() >= getBloodZ());
        }
        return false;
    }

    public Location findNextDirectionalLocation(Location location, String direction) {
        Location locations;
        if (directionToMove.equalsIgnoreCase("Move left")) {
            locations = location.clone().add(-34.0, 0.0, 0.0);
        } else if (directionToMove.equalsIgnoreCase("Move right")) {
            locations = location.clone().add(34.0, 0.0, 0.0);
        } else if (directionToMove.equalsIgnoreCase("Move top")) {
            locations = location.clone().add(0.0, 0.0, -34.0);
        } else {
            locations = location.clone().add(0.0, 0.0, 34.0);
        }
        return locations;
    }

    public static String getPriorityDirection(Location lobbyLocation, Location bloodLocation, String directionToMove) {
        double lobbyX = lobbyLocation.getX();
        double lobbyZ = lobbyLocation.getZ();
        double bloodX = bloodLocation.getX();
        double bloodZ = bloodLocation.getZ();
        String prioritize = null;
        if (directionToMove.equalsIgnoreCase("Move left") || directionToMove.equalsIgnoreCase("Move right")) {
            if (lobbyZ == bloodZ) {
                prioritize = "none";
            } else if (lobbyZ > bloodZ) {
                prioritize = "Move top";
            } else if (lobbyZ < bloodZ) {
                prioritize = "Move bottom";
            }
        } else if (directionToMove.equalsIgnoreCase("Move top") || directionToMove.equalsIgnoreCase("Move bottom")) {
            if (lobbyX == bloodX) {
                prioritize = "none";
            } else if (lobbyX < bloodX) {
                prioritize = "Move right";
            } else if (lobbyX > bloodX) {
                prioritize = "Move left";
            }
        }
        return prioritize;
    }

    public DungeonRoomPoint findNextPointFromCurrentPoint(Dungeon dungeon, DungeonRoomPoint currentPoint, String direction) {
        Location toLocation;
        Location fromLocation = currentPoint.getCenterLocation();
        if (direction.equalsIgnoreCase("Move left")) {
            toLocation = fromLocation.clone().add(-34.0, 0.0, 0.0);
        } else if (direction.equalsIgnoreCase("Move right")) {
            toLocation = fromLocation.clone().add(34.0, 0.0, 0.0);
        } else if (direction.equalsIgnoreCase("Move top")) {
            toLocation = fromLocation.clone().add(0.0, 0.0, -34.0);
        } else {
            toLocation = fromLocation.clone().add(0.0, 0.0, 34.0);
        }
        if (getPointFromLocation(dungeon, toLocation) == null) {
            Common.log("INFINITE ERROR >> Tried to " + direction + " from " + fromLocation.getX() + ", " + fromLocation.getZ() + " to" + toLocation.getX() + ", " + toLocation.getZ());
            if (direction.equals(directionToMove)) {
                return findNextPointFromCurrentPoint(dungeon, currentPoint, priorityDirection);
            } else if (direction.equals(priorityDirection)) {
                return findNextPointFromCurrentPoint(dungeon, currentPoint, directionToMove);
            }
        }
        return getPointFromLocation(dungeon, toLocation);
    }

    public DungeonRoomPoint getPointFromLocation(Dungeon dungeon, Location location) {
        for (DungeonRoomPoint point : dungeon.getPointTracking()) {
            if (point.getCenterLocation().equals(location)) {
                return point;
            }
        }
        return null;
    }

}