package org.ninenetwork.infinitedungeons.dungeon;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.mineacademy.fo.Common;
import org.ninenetwork.infinitedungeons.dungeon.instance.DungeonRoomInstance;
import org.ninenetwork.infinitedungeons.dungeon.instance.DungeonRoomPoint;

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
    protected ArrayList<DungeonRoomConnection> allConnections = new ArrayList<>();
    protected DungeonRoomPoint currentEvaluationPoint;
    protected ArrayList<DungeonRoomInstance> roomNetwork = new ArrayList<>();

    protected String directionToMove;
    protected String priorityDirection;
    protected String priorityUnchangeable;

    protected boolean bloodRushPathingDone;
    protected boolean isAligned;

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
    }

    public void initializePathfinding(Dungeon dungeon, DungeonGrid grid) {
        setDirectionToMove("Move " + grid.getBloodRoom(dungeon).getShape().getDirection());
        this.setAligned(lobbyX == bloodX || lobbyZ == bloodZ);
        setPriorityDirection(getPriorityDirection(lobbyCenter, bloodCenter, directionToMove));
        bloodRushPath.add(grid.getLobbyRoom(dungeon));
        boolean generateAlignedNewPoint = false;

        findFirstMove(dungeon);
        while (!bloodRushPathingDone) {
            if (!this.isAligned) {
                DungeonRoomPoint point = findPriorityBestPoint(getCurrentRoom());
                if  (point != null) {
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

    // possible implement
    public void verifyAllReachable(Dungeon dungeon) {
        ArrayList<DungeonRoomInstance> unreachable = new ArrayList<>();
        for (DungeonRoomInstance instance : getBloodRushPath()) {
            addRoomToNetwork(instance);
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
                Common.log("Unreachable room " + instance.getDungeonRoomPoints().get(0).getCenterLocation().getX() + " " + instance.getDungeonRoomPoints().get(0).getCenterLocation().getZ());
            }
        }
        if (!unreachable.isEmpty()) {
            makeReachable(dungeon, unreachable);
        } else {
            //dungeon.setGenerationComplete(true);
            //dungeon.initializeDungeonLobby();
        }

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
                        if (location1 != bloodCenter && location1 != lobbyCenter) {
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
                        if (location2 != bloodCenter && location2 != lobbyCenter) {
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
                        if (location3 != bloodCenter && location3 != lobbyCenter) {
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
                        if (location4 != bloodCenter && location4 != lobbyCenter) {
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

    public boolean checkHasPathToBloodRushPath(DungeonRoomInstance instance, ArrayList<DungeonRoomInstance> ignoredRooms) {
        ArrayList<DungeonRoomInstance> nextCheckRooms = new ArrayList<>();
        for (DungeonRoomInstance connection : instance.getRoomsConnected()) {
            if (!(connection.getRoomsConnected().size() <= 1)) {
                nextCheckRooms.add(connection);
                ignoredRooms.add(instance);
            }
        }
        return false;
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



    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /*
    public void initializePathFindingSecondary(Dungeon dungeon, DungeonGrid grid) {
        ArrayList<DungeonRoomPoint> points = getAllPointsInDungeon();
        ArrayList<DungeonRoomInstance> usedRooms = getBloodRushPath();
        for (DungeonRoomInstance instance : usedRooms) {
            addRoomToNetwork(instance);
        }
        attemptCornerPathing(dungeon, grid);
    }

    public void attemptCornerPathing(Dungeon dungeon, DungeonGrid grid) {
        DungeonRoomInstance selectedRoom;
        Location location = grid.getLocationGrid()[0][0];
        if (!getRoomNetwork().contains(DungeonRoomInstance.getRoomFromLocation(dungeon, location))) {
            selectedRoom = DungeonRoomInstance.getRoomFromLocation(dungeon, location);
            assert selectedRoom != null;
            connectNextRooms(dungeon, selectedRoom, getBloodRushPath());
        }
        location = grid.getLocationGrid()[0][5];
        if (!getRoomNetwork().contains(DungeonRoomInstance.getRoomFromLocation(dungeon, location))) {
            selectedRoom = DungeonRoomInstance.getRoomFromLocation(dungeon, location);
            assert selectedRoom != null;
            connectNextRooms(dungeon, selectedRoom, getBloodRushPath());
        }
        location = grid.getLocationGrid()[5][0];
        if (!getRoomNetwork().contains(DungeonRoomInstance.getRoomFromLocation(dungeon, location))) {
            selectedRoom = DungeonRoomInstance.getRoomFromLocation(dungeon, location);
            assert selectedRoom != null;
            connectNextRooms(dungeon, selectedRoom, getBloodRushPath());
        }
        location = grid.getLocationGrid()[5][5];
        if (!getRoomNetwork().contains(DungeonRoomInstance.getRoomFromLocation(dungeon, location))) {
            selectedRoom = DungeonRoomInstance.getRoomFromLocation(dungeon, location);
            assert selectedRoom != null;
            connectNextRooms(dungeon, selectedRoom, getBloodRushPath());
        }
    }

    public void connectToBloodPath(Dungeon dungeon, ArrayList<DungeonRoomInstance> path) {
        path.removeAll(null);
        if (!path.isEmpty()) {
            DungeonRoomInstance instance = path.get(path.size() - 1);
            ArrayList<DungeonRoomPoint> validPoints = checkRoomHasBloodRoomPathTouching(dungeon, instance);
            if (validPoints == null) {
                path.remove(instance);
                connectToBloodPath(dungeon, path);
            } else {
                DungeonDoor.addDungeonDoor(dungeon, validPoints.get(0), validPoints.get(1));
            }
        } else {
            // implement this logic not even attempted yet
            Common.log("No way to connect path to blood rush path");
        }
    }

    public void connectNextRooms(Dungeon dungeon, DungeonRoomInstance instance, ArrayList<DungeonRoomInstance> used) {
        ArrayList<DungeonRoomInstance> possibilities = new ArrayList<>();
        ArrayList<DungeonRoomInstance> path = new ArrayList<>();
        ArrayList<DungeonRoomInstance> usedRooms;
        usedRooms = used;
        DungeonRoomInstance currentCheck = null;
        DungeonRoomPoint currentCheckPoint;
        Location location;
        boolean stopMain = false;
        for (DungeonRoomPoint point : instance.getDungeonRoomPoints()) {
            for (int i = 0; i < 4; i++) {
                if (i == 0) {
                    location = point.getCenterLocation().clone().add(-34.0, 0.0, 0.0);
                } else if (i == 1) {
                    location = point.getCenterLocation().clone().add(34.0, 0.0, 0.0);
                } else if (i == 2) {
                    location = point.getCenterLocation().clone().add(0.0, 0.0, -34.0);
                } else {
                    location = point.getCenterLocation().clone().add(0.0, 0.0, 34.0);
                }
                if (dungeon.isLocationPartOfDungeon(dungeon, location)) {
                    currentCheck = DungeonRoomInstance.getRoomFromLocation(dungeon, location);
                    currentCheckPoint = getPointFromLocation(dungeon, location);
                    Common.log("Checking room at " + location.getX() + " " + location.getZ());
                    if (!usedRooms.contains(currentCheck) && !instance.getDungeonRoomPoints().contains(currentCheckPoint) && !possibilities.contains(currentCheck) && !getBloodCenter().equals(location) && !getLobbyCenter().equals(location)) {
                        Common.log("Checking room passed");
                        possibilities.add(currentCheck);
                        usedRooms.add(currentCheck);
                        DungeonDoor.addDungeonDoor(dungeon, point, getPointFromLocation(dungeon, location));
                        if (!getRoomNetwork().contains(DungeonRoomInstance.getRoomFromPoint(dungeon, point))) {
                            addRoomToNetwork(DungeonRoomInstance.getRoomFromPoint(dungeon, point));
                        }
                        if (!getRoomNetwork().contains(currentCheck)) {
                            addRoomToNetwork(currentCheck);
                        }
                        if (!path.contains(DungeonRoomInstance.getRoomFromPoint(dungeon, point))) {
                            path.add(DungeonRoomInstance.getRoomFromPoint(dungeon, point));
                        }
                        if (!path.contains(currentCheck)) {
                            path.add(currentCheck);
                        }
                        stopMain = true;
                        break;
                    }
                }
            }
            //last thing I added 3/31
            if (stopMain) {
                break;
            }
        }
        if (possibilities.isEmpty() || currentCheck == null) {
            connectToBloodPath(dungeon, path);
        } else {
            connectNextRooms(dungeon, currentCheck, usedRooms);
        }

    }

    public ArrayList<DungeonRoomPoint> checkRoomHasBloodRoomPathTouching(Dungeon dungeon, DungeonRoomInstance instance) {
        Location side1;
        Location side2;
        Location side3;
        Location side4;
        ArrayList<DungeonRoomPoint> points = new ArrayList<>();
        for (DungeonRoomPoint point : instance.getDungeonRoomPoints()) {
            side1 = point.getCenterLocation().clone().add(34.0, 0.0, 0.0);
            side2 = point.getCenterLocation().clone().add(-34.0, 0.0, 0.0);
            side3 = point.getCenterLocation().clone().add(0.0, 0.0, 34.0);
            side4 = point.getCenterLocation().clone().add(0.0, 0.0, -34.0);
            if (getBloodRushPath().contains(DungeonRoomInstance.getRoomFromLocation(dungeon, side1))) {
                points.add(point);
                points.add(getPointFromLocation(dungeon, side1));
                return points;
            }
            if (getBloodRushPath().contains(DungeonRoomInstance.getRoomFromLocation(dungeon, side2))) {
                points.add(point);
                points.add(getPointFromLocation(dungeon, side2));
                return points;
            }
            if (getBloodRushPath().contains(DungeonRoomInstance.getRoomFromLocation(dungeon, side3))) {
                points.add(point);
                points.add(getPointFromLocation(dungeon, side3));
                return points;
            }
            if (getBloodRushPath().contains(DungeonRoomInstance.getRoomFromLocation(dungeon, side4))) {
                points.add(point);
                points.add(getPointFromLocation(dungeon, side4));
                return points;
            }
        }
        return null;
    }
    */

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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

        setCurrentEvaluationPoint(getPointFromLocation(dungeon, nextLocation));
        roomInstance = DungeonRoomInstance.getRoomFromLocation(dungeon, nextLocation);
        bloodRushPath.add(roomInstance);
        setCurrentRoom(roomInstance);
    }

    public void runBloodRushPath(Dungeon dungeon, DungeonGrid grid) {

    }

    public DungeonRoomPoint findBestPoint(DungeonRoomInstance instance) {
        DungeonRoomPoint chosenPoint = getCurrentEvaluationPoint();
        double chosenX;
        double chosenZ;
        double x;
        double z;
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

    public void runBloodRushPathing(Dungeon dungeon, DungeonGrid grid) {
        int iterator = 0;
        boolean atBlood = false;
        String alignBloodDirection;
        // top = -z, right = +x, bottom = +z, left = -x
        this.directionToMove = "Move " + grid.getBloodRoom(dungeon).getShape().getDirection();
        this.priorityDirection = getPriorityDirection(lobbyCenter, bloodCenter, getDirectionToMove());
        addBloodRushPath(this.currentRoom);
        while (!atBlood) {
            if (iterator != 0) {
                if (getCurrentRoom().equals(DungeonRoomInstance.getRoomFromLocation(dungeon, getBloodCenter()))) {
                    atBlood = true;
                } else {
                    this.currentRoom = findNextBloodRushRoom(dungeon, grid, iterator);
                    iterator++;
                }
            } else {
                this.currentRoom = findNextBloodRushRoom(dungeon, grid, iterator);
                iterator++;
            }
        }
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

    public DungeonRoomInstance findNextBloodRushRoom(Dungeon dungeon, DungeonGrid grid, int iterator) {
        Location findNext;
        DungeonRoomInstance roomInstance = null;
        if (iterator == 0) {
            this.currentRoom = grid.getLobbyRoom(dungeon);
            if (directionToMove.equalsIgnoreCase("Move left")) {
                findNext = dungeon.getLobbyLocation().clone().add(-34.0, 0.0, 0.0);
            } else if (directionToMove.equalsIgnoreCase("Move right")) {
                findNext = dungeon.getLobbyLocation().clone().add(34.0, 0.0, 0.0);
            } else if (directionToMove.equalsIgnoreCase("Move top")) {
                findNext = dungeon.getLobbyLocation().clone().add(0.0, 0.0, -34.0);
            } else {
                findNext = dungeon.getLobbyLocation().clone().add(0.0, 0.0, 34.0);
            }
            Location mid = DungeonGeneration.findMidPoint(dungeon.getLobbyLocation(), findNext);
            mid.getBlock().setType(Material.REDSTONE_BLOCK);
            roomInstance = DungeonRoomInstance.getRoomFromLocation(dungeon, findNext);
        } else {
            //Start testing logic here.
            /*
            DungeonRoomPoint point = furthestDirectionalPoint(currentRoom.getDungeonRoomPoints(), directionToMove);
            DungeonRoomPoint nextPoint;
            if (this.priorityDirection.equalsIgnoreCase("none") || (point.getCenterLocation().getX() == bloodX || point.getCenterLocation().getZ() == bloodCenter.getZ())) {
                nextPoint = findNextPointFromCurrentPoint(dungeon, point, this.directionToMove);
            } else {
                nextPoint = findNextPointFromCurrentPoint(dungeon, point, this.priorityDirection);
            }
            Location mid = DungeonGeneration.findMidPoint(point.getCenterLocation(), nextPoint.getCenterLocation());
            mid.getBlock().setType(Material.REDSTONE_BLOCK);
            roomInstance = DungeonRoomInstance.getRoomFromPoint(dungeon, nextPoint);

             */
        }
        return roomInstance;
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

    /*
    public DungeonRoomPoint furthestDirectionalPoint(ArrayList<DungeonRoomPoint> points, String direction) {
        DungeonRoomPoint closestPoint = points.get(0);
        double closestCoord;
        double lastPoint = 0.0;
        double trackerX;
        double trackerZ;
        for (DungeonRoomPoint point : points) {
            trackerX = point.getCenterLocation().getX();
            trackerZ = point.getCenterLocation().getZ();
            if (!(points.indexOf(point) == 0)) {
                if (direction.equalsIgnoreCase("Move left")) {
                    if (trackerX < lastPoint && getCurrentPoint().getCenterLocation().getZ() == trackerZ) {
                        closestPoint = point;
                    }
                    lastPoint = trackerX;
                } else if (direction.equalsIgnoreCase("Move right")) {
                    if (trackerX > lastPoint && getCurrentPoint().getCenterLocation().getZ() == trackerZ) {
                        closestPoint = point;
                    }
                    lastPoint = trackerX;
                } else if (direction.equalsIgnoreCase("Move top")) {
                    if (trackerZ < lastPoint && getCurrentPoint().getCenterLocation().getX() == trackerX) {
                        closestPoint = point;
                    }
                    lastPoint = trackerZ;
                } else {
                    if (trackerZ > lastPoint && getCurrentPoint().getCenterLocation().getX() == trackerX) {
                        closestPoint = point;
                    }
                    lastPoint = trackerZ;
                }
            } else {
                if (direction.equalsIgnoreCase("Move left")) {
                    closestCoord = point.getCenterLocation().getX();
                } else if (direction.equalsIgnoreCase("Move right")) {
                    closestCoord = point.getCenterLocation().getX();
                } else if (direction.equalsIgnoreCase("Move top")) {
                    closestCoord = point.getCenterLocation().getZ();
                } else {
                    closestCoord = point.getCenterLocation().getZ();
                }
                lastPoint = closestCoord;
            }
        }
        return closestPoint;
    }
    */

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

    public void addBloodRushPath(DungeonRoomInstance instance) {
        ArrayList<DungeonRoomInstance> temp = getBloodRushPath();
        temp.add(instance);
        setBloodRushPath(temp);
    }

}