package org.ninenetwork.infinitedungeons.dungeon;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.FileUtil;
import org.mineacademy.fo.MinecraftVersion;
import org.mineacademy.fo.model.ChunkedTask;
import org.mineacademy.fo.model.HookManager;
import org.mineacademy.fo.region.Region;
import org.ninenetwork.infinitedungeons.InfiniteDungeonsPlugin;
import org.ninenetwork.infinitedungeons.dungeon.instance.DungeonRoomInstance;
import org.ninenetwork.infinitedungeons.dungeon.instance.DungeonRoomPoint;
import org.ninenetwork.infinitedungeons.dungeon.instance.DungeonRoomShape;
import org.ninenetwork.infinitedungeons.dungeon.instance.DungeonRoomShapeOrientation;
import org.ninenetwork.infinitedungeons.map.SchematicManager;

import java.io.File;
import java.util.*;

import static org.mineacademy.fo.Valid.checkBoolean;

@Getter
@Setter
public final class DungeonGeneration {

    private Location startingPosition;

    private Location spawnpoint;

    private ArrayList<Location> gridCentersRemaining;

    int current11Square = 0;
    int current22Square = 0;
    int current111L = 0;
    int current12Rectangle = 0;
    int current13Rectangle = 0;
    int current14Rectangle = 0;
    //private Location lobbyMiddle;

    private final Map<UUID, ChunkedTask> dungeonsGenerating = new HashMap<>();

    public DungeonGeneration() {

    }

    public void preDungeonInitialization(Dungeon dungeon, int floor) {
        checkBoolean(HookManager.isWorldEditLoaded());
        checkBoolean(MinecraftVersion.atLeast(MinecraftVersion.V.v1_13));
        Location location = DungeonLastInstanceStorage.findNextInstanceLocation();
        DungeonLastInstanceStorage.addDungeonInstanceCenter(dungeon.getName(), location);
        dungeon.setLobbyLocation(location);
        ArrayList<Location> locations = generatePossibleRoomCenterLocations(location, floor);
        dungeon.initializeDungeonPoints(locations);
        dungeon.setPointLocations(locations);
        dungeon.setDungeonScore(0);
    }

    public void dungeonInitialization(Dungeon dungeon, int floor) {
        DungeonGrid grid = new DungeonGrid(dungeon.getName());
        grid.initializePointsGrid(dungeon);
        grid.initializeLocationGrid(dungeon);
        grid.setPointsStillEmpty(dungeon.getPointTracking());
        //createDungeonRooms(dungeon, 1, grid);
        DungeonRoom.loadDungeonRooms();
        addLobbyBlood(dungeon, grid);
        createDungeonRoomGrid(dungeon, 1, grid);
    }

    public void createDungeonRoomGrid(Dungeon dungeon, int generationRound, DungeonGrid grid) {
        ArrayList<DungeonRoomPoint> dungeonRoomPoints = dungeon.getPointTracking();

        new BukkitRunnable() {

            int dungeonPointsIterator = 0;
            DungeonRoomPoint focusedPoint;
            final int generationRounds = generationRound;
            //boolean lobbyCreated = false;

            @Override
            public void run() {
                if (dungeonPointsIterator < dungeonRoomPoints.size()) {
                    boolean pointUnfilled = false;
                    focusedPoint = dungeonRoomPoints.get(dungeonPointsIterator);
                    while (!pointUnfilled) {
                        if (focusedPoint.isAlreadyUsed()) {
                            if (!(dungeonPointsIterator < dungeonRoomPoints.size() - 1)) {
                                pointUnfilled = true;
                            } else {
                                dungeonPointsIterator++;
                                focusedPoint = dungeonRoomPoints.get(dungeonPointsIterator);
                            }
                        } else {
                            pointUnfilled = true;
                        }
                    }
                    if (!focusedPoint.isAlreadyUsed()) {
                        DungeonRoomShape shape = shapeGenerator(dungeon, focusedPoint, generationRounds, false);
                        if (shape != null) {
                            fillGrids(shape, grid);
                        }
                    }
                } else {
                    if (generationRounds < 3) {
                        createDungeonRoomGrid(dungeon,generationRounds + 1, grid);
                    } else if (generationRound == 3) {
                        if (grid.checkGridValid(dungeon, grid)) {
                            initializeDungeonRoomInstances(dungeon, grid);
                        } else {
                            grid.clearShapeGrid(grid);
                            createDungeonRoomGrid(dungeon, 1, grid);
                        }
                    }
                    cancel();
                }
                this.dungeonPointsIterator++;
            }
        }.runTaskTimerAsynchronously(InfiniteDungeonsPlugin.getInstance(), 0L, 1L);
    }

    public void addLobbyBlood(Dungeon dungeon, DungeonGrid grid) {
        Random rand = new Random();
        ArrayList<String> choices = new ArrayList<>(Arrays.asList("top", "left", "right"));
        String choice = choices.get(rand.nextInt(3));
        int lobbySlot = DungeonGrid.chooseRandomOutsideSlotExceptBottom(choice);
        String opposite = DungeonGrid.directionNeeded(lobbySlot);
        ArrayList<Integer> slotsPossible = DungeonGrid.outsidePointsSpecificSide(opposite);
        int bloodSlot = slotsPossible.get(rand.nextInt(slotsPossible.size()));
        DungeonRoomPoint point = grid.getPointGrid()[DungeonGrid.convertToRow(lobbySlot)][DungeonGrid.convertToColumn(lobbySlot)];
        DungeonRoomShape lobbyShape = shapeGenerator(dungeon, point, 3, true);
        if (lobbyShape != null) {
            lobbyShape.setRoomType("Lobby");
            lobbyShape.setDirection(choice);
            lobbyShape.setRoomIdentifier("1x1_Square");
            if (choice.equalsIgnoreCase("right")) {
                lobbyShape.setOrientation(3);
            } else if (choice.equalsIgnoreCase("left")) {
                lobbyShape.setOrientation(1);
            } else if (choice.equalsIgnoreCase("top")) {
                lobbyShape.setOrientation(2);
            }
            fillGrids(lobbyShape, grid);
            dungeon.setLobbyLocation(point.getCenterLocation());
        }
        DungeonRoomPoint point2 = grid.getPointGrid()[DungeonGrid.convertToRow(bloodSlot)][DungeonGrid.convertToColumn(bloodSlot)];
        DungeonRoomShape bloodShape = shapeGenerator(dungeon, point2, 3, true);
        if (bloodShape != null) {
            bloodShape.setRoomType("Blood");
            bloodShape.setDirection(opposite);
            bloodShape.setRoomIdentifier("1x1_Square");
            if (opposite.equalsIgnoreCase("right")) {
                bloodShape.setOrientation(3);
            } else if (opposite.equalsIgnoreCase("left")) {
                bloodShape.setOrientation(1);
            } else if (opposite.equalsIgnoreCase("top")) {
                bloodShape.setOrientation(2);
            } else if (opposite.equalsIgnoreCase("bottom")) {
                bloodShape.setOrientation(0);
            }
            fillGrids(bloodShape, grid);
            dungeon.setBloodLocation(point2.getCenterLocation());
        }
    }

    public void fillGrids(DungeonRoomShape shape, DungeonGrid grid) {
        if (shape != null) {
            grid.addShape(shape);
            for (DungeonRoomPoint ignored : shape.getGridConsumption()) {
                grid.addDungeonRoomShape(shape);
            }
        }
    }

    public void initializeDungeonRoomInstances(Dungeon dungeon, DungeonGrid grid) {
        ArrayList<DungeonRoom> dungeonRooms;
        DungeonRoom dungeonRoom;
        String roomType = "BloodRush";
        for (DungeonRoomShape shape : grid.getShapes()) {
            //added null check
            if (shape != null) {
                if (shape.getRoomType() == null) {
                    dungeonRooms = DungeonRoom.findAllByIdentifier(shape.getRoomIdentifier());
                    Collections.shuffle(dungeonRooms);
                    dungeonRoom = dungeonRooms.get(0);
                } else if (shape.getRoomType().equalsIgnoreCase("Lobby")) {
                    dungeonRoom = DungeonRoom.findByName("Lobby");
                    roomType = "Lobby";
                } else if (shape.getRoomType().equalsIgnoreCase("Blood")) {
                    dungeonRoom = DungeonRoom.findByName("Blood");
                    roomType = "Blood";
                } else {
                    dungeonRooms = DungeonRoom.findAllByIdentifier(shape.getRoomIdentifier());
                    Collections.shuffle(dungeonRooms);
                    dungeonRoom = dungeonRooms.get(0);
                }
                DungeonRoomInstance roomInstance = new DungeonRoomInstance(dungeon, dungeonRoom);
                roomInstance.setRoomType(roomType);
                roomInstance.setOrientation(shape.getOrientation());
                roomInstance.setDungeonRoomPoints(shape.getGridConsumption());
                roomInstance.setShape(shape);
                dungeon.addDungeonRooms(roomInstance);
                Common.log("Added room instance");
            }
        }
        dungeonSchematicHandle(dungeon, grid);
    }

    public void dungeonSchematicHandle(Dungeon dungeon, DungeonGrid grid) {

        List<DungeonRoomInstance> rooms = dungeon.getDungeonRooms();
        double centerOffset = 15.0;

        new BukkitRunnable() {

            int iterator = 0;
            DungeonRoomInstance currentRoom;
            int rotation;
            DungeonRoomShape shape;
            Location centerLocation;
            int schemCounter;

            @Override
            public void run() {
                if (iterator < rooms.size()) {
                    schemCounter = 1;
                    currentRoom = rooms.get(iterator);
                    shape = currentRoom.getShape();
                    rotation = shape.getOrientation();
                    for (DungeonRoomPoint point : shape.getGridConsumption()) {
                        centerLocation = point.getCenterLocation();
                        Location loc = new Location(centerLocation.getWorld(), centerLocation.getX() - centerOffset, centerLocation.getY(), centerLocation.getZ() - centerOffset);
                        SchematicManager.paste(loc, locateSchematic(currentRoom.getDungeonRoom().getName() + schemCounter), rotation);
                        schemCounter++;
                    }
                    currentRoom.initializePossibleDoors(dungeon, currentRoom, shape.getGridConsumption());
                } else {
                    Common.log("Finished pasting dungeon " + dungeon.getName());
                    DungeonPathfinding pathfinding = new DungeonPathfinding(dungeon, grid);
                    pathfinding.initializePathfinding(dungeon, grid);
                    cancel();
                }
                iterator++;
            }

        }.runTaskTimer(InfiniteDungeonsPlugin.getInstance(), 0L, 10L);

    }

    public void createDungeonRooms(Dungeon dungeon, int generationRound, DungeonGrid grid) {
        ArrayList<DungeonRoomPoint> dungeonRoomPoints = dungeon.getPointTracking();
        DungeonRoom.loadDungeonRooms();

        new BukkitRunnable() {

            int dungeonPointsIterator = 0;
            DungeonRoomPoint focusedPoint;
            final int generationRounds = generationRound;

            @Override
            public void run() {
                if (dungeonPointsIterator < dungeonRoomPoints.size()) {
                    boolean pointUnfilled = false;
                    Common.log("Points Recognized in dungeon: " + dungeonRoomPoints.size());
                    focusedPoint = dungeonRoomPoints.get(dungeonPointsIterator);
                    Common.log("Focused point now at block " + dungeonPointsIterator);
                    while (!pointUnfilled) {
                        if (focusedPoint.isAlreadyUsed()) {
                            if (!(dungeonPointsIterator < dungeonRoomPoints.size() - 1)) {
                                pointUnfilled = true;
                            } else {
                                dungeonPointsIterator++;
                                focusedPoint = dungeonRoomPoints.get(dungeonPointsIterator);
                            }
                        } else {
                            pointUnfilled = true;
                        }
                    }
                    if (!focusedPoint.isAlreadyUsed()) {
                        DungeonRoomShape shape = shapeGenerator(dungeon, focusedPoint, generationRounds, false);
                        if (shape != null) {
                            //Common.log("Added room " + dungeonRoom.getName() + " with a shape of " + shape.getRoomIdentifier());
                            pasteAndConnect(dungeon, grid, shape.getGridConsumption(), shape);
                        } else {
                            Common.log("Shape null found");
                        }
                    } else {
                        Common.log("Focused dungeon point already used");
                    }
                } else {
                    Common.log("All points iterated. cancelling runnable");
                    Common.log("Finished generation round " + generationRounds);
                    if (generationRounds < 3) {
                        createDungeonRooms(dungeon,generationRounds + 1, grid);
                    } else {
                        Common.log("1x1_Square " + current11Square);
                        Common.log("2x2_Square " + current22Square);
                        Common.log("1x1x1_L " + current111L);
                        Common.log("1x2_Rectangle " + current12Rectangle);
                        Common.log("1x3_Rectangle " + current13Rectangle);
                        Common.log("1x4_Rectangle " + current14Rectangle);
                    }
                    cancel();
                }
                this.dungeonPointsIterator++;
            }
        }.runTaskTimer(InfiniteDungeonsPlugin.getInstance(), 0L, 15L);

    }

    public static void pasteAndConnect(Dungeon dungeon, DungeonGrid grid, ArrayList<DungeonRoomPoint> gridConsumption, DungeonRoomShape shape) {

        //DungeonRoomInstance dungeonRoomInstance = new DungeonRoomInstance(dungeon, dungeonRoom);
        //dungeonRoomInstance.setDungeonRoomPoints(gridConsumption);
        // Finishing this logic and assigning possible door locations to dungeonroominstance
        // with orientation and rotation taken into accountability

        Location location;
        Location previousPoint = null;
        Location centerLocation;
        boolean isFirstRound = true;
        int rotation = shape.getOrientation();
        int schemCounter = 1;
        ArrayList<DungeonRoom> rooms = DungeonRoom.findAllByIdentifier(shape.getRoomIdentifier());
        Random rand = new Random();
        DungeonRoom dungeonRoom;
        //if (rooms.size() > 1) {
            //dungeonRoom = rooms.get(rand.nextInt(rooms.size()));
        //} else {
        dungeonRoom = rooms.get(0);
        Common.log("Schematics possibly empty/not found");

        for (DungeonRoomPoint point : gridConsumption) {
            centerLocation = point.getCenterLocation();
            Location loc = new Location(centerLocation.getWorld(), centerLocation.getX() - 15.0, centerLocation.getY(), centerLocation.getZ() - 15.0);
            SchematicManager.paste(loc, locateSchematic(dungeonRoom.getName() + schemCounter), rotation);
            schemCounter++;
            grid.addDungeonRoomShape(shape);
            /*
            if (!isFirstRound) {
                if (!shape.getRoomIdentifier().equals("1x1_Square")) {
                    if (!(centerLocation.getX() == previousPoint.getX() && centerLocation.getZ() == previousPoint.getZ())) {
                        if ((int) centerLocation.getX() == (int) previousPoint.getX()) {
                            location = findMidPoint(centerLocation, previousPoint);
                            location.getBlock().setType(Material.SEA_LANTERN);
                            Common.log("Connector origins: " + previousPoint.getX() + "," + centerLocation.getX());
                            Common.log("Connector origins: " + previousPoint.getZ() + "," + centerLocation.getZ());
                            Common.log("Pasting stone connector at " + location.getX() + " " + location.getY() + " " + location.getZ());
                        } else if ((int) centerLocation.getZ() == (int) previousPoint.getZ()) {
                            location = findMidPoint(centerLocation, previousPoint);
                            location.getBlock().setType(Material.SEA_LANTERN);
                            Common.log("Connector origins: " + previousPoint.getX() + "," + centerLocation.getX());
                            Common.log("Connector origins: " + previousPoint.getZ() + "," + centerLocation.getZ());
                            Common.log("Pasting stone connector at " + location.getX() + " " + location.getY() + " " + location.getZ());
                        } else {
                            Common.log("InfiniteError >> Centerpoint location not found due to neither x or z being the same as previousPoint");
                        }
                    } else {
                        Common.log("Skipping connection process due to locations already equal");
                    }
                } else {
                    Common.log("Skipping connection process due to shape identifier " + shape.getRoomIdentifier());
                }
            } else {
                Common.log("Skipping connection process due to first round");
            }
            */
            //previousPoint = centerLocation;
            //isFirstRound = false;

        }
        //dungeon.addDungeonRooms(dungeonRoomInstance);
    }

    public static Location findMidPoint(Location location1, Location location2) {
        double finalX;
        double finalY;
        double finalZ;
        finalX = (((location1.getX()) + (location2.getX())) / 2);
        finalY = (((location1.getY()) + (location2.getY())) / 2);
        finalZ = (((location1.getZ()) + (location2.getZ())) / 2);
        return new Location(location1.getWorld(), finalX, finalY, finalZ);
    }

    public DungeonRoomShape shapeGenerator(Dungeon dungeon, DungeonRoomPoint point, int generationRound, boolean forcedOne) {

        ArrayList<String> notUsable = new ArrayList<>();
        Location pointCenter = point.getCenterLocation();
        String choice = "Failed";
        int orientation = 4;
        boolean validShapeFound = false;
        if (generationRound == 3 || forcedOne) {
            choice = "1x1_Square";
            orientation = checkShapeValid(dungeon, choice, pointCenter, generationRound);
        } else if (generationRound == 2) {
            while (!validShapeFound) {
                choice = chooseRandomRoomShapeExclusionsNoSingles(notUsable);
                orientation = checkShapeValid(dungeon, choice, pointCenter, generationRound);
                if (choice.equals("Failed")) {
                    Common.log("Dungeon Room Shape Generation Failed");
                    return null;
                } else {
                    if (orientation != 4) {
                        validShapeFound = true;
                    } else {
                        notUsable.add(choice);
                    }
                }
            }
        } else if (generationRound == 1) {
            choice = chooseRandomRoomShape();
            orientation = checkShapeValid(dungeon, choice, pointCenter, generationRound);
            if (orientation == 4) {
                return null;
            }
        }
        DungeonRoomShape shape = new DungeonRoomShape(choice);
        shape.setOrientation(orientation);
        shape.setRoomIdentifier(choice);
        shape.setGridConsumption(DungeonRoomPoint.getPointsFromLocations(dungeon, DungeonRoomShapeOrientation.calculateLocationsNeededOrientation(choice, pointCenter, orientation)));
        return shape;
    }

    public int checkShapeValid(Dungeon dungeon, String choice, Location position, int generationRound) {
        int orientation = 4;
        switch (choice) {
            case "1x1_Square":
                orientation = 0;
                if (this.current11Square < dungeon.max11Square) {
                    this.current11Square++;
                    return orientation;
                }
                break;
            case "2x2_Square":
                if (generationRound == 1) {
                    orientation = shapeFitsAtLocationRandomOrientation(dungeon, choice, position);
                } else {
                    orientation = shapeFitsAtLocationAnyOrientation(dungeon, choice, position);
                }
                if (this.current22Square < dungeon.max22Square && orientation != 4) {
                    this.current22Square++;
                    return orientation;
                }
                break;
            case "1x1x1_L":
                if (generationRound == 1) {
                    orientation = shapeFitsAtLocationRandomOrientation(dungeon, choice, position);
                } else {
                    orientation = shapeFitsAtLocationAnyOrientation(dungeon, choice, position);
                }
                if (this.current111L < dungeon.max111L && orientation != 4) {
                    this.current111L++;
                    return orientation;
                }
                break;
            case "1x2_Rectangle":
                if (generationRound == 1) {
                    orientation = shapeFitsAtLocationRandomOrientation(dungeon, choice, position);
                } else {
                    orientation = shapeFitsAtLocationAnyOrientation(dungeon, choice, position);
                }
                if (this.current12Rectangle < dungeon.max12Rectangle && orientation != 4) {
                    this.current12Rectangle++;
                    return orientation;
                }
                break;
            case "1x3_Rectangle":
                if (generationRound == 1) {
                    orientation = shapeFitsAtLocationRandomOrientation(dungeon, choice, position);
                } else {
                    orientation = shapeFitsAtLocationAnyOrientation(dungeon, choice, position);
                }
                if (this.current13Rectangle < dungeon.max13Rectangle && orientation != 4) {
                    this.current13Rectangle++;
                    return orientation;
                }
                break;
            case "1x4_Rectangle":
                if (generationRound == 1) {
                    orientation = shapeFitsAtLocationRandomOrientation(dungeon, choice, position);
                } else {
                    orientation = shapeFitsAtLocationAnyOrientation(dungeon, choice, position);
                }
                if (this.current14Rectangle < dungeon.max14Rectangle && orientation != 4) {
                    this.current14Rectangle++;
                    return orientation;
                }
                break;
        }
        return 4;
    }

    public static int shapeFitsAtLocationRandomOrientation(Dungeon dungeon, String shape, Location startingPosition) {
        if (shape.equals("1x1_Square")) {
            return 0;
        }
        ArrayList<Integer> orientations = new ArrayList<>(Arrays.asList(0,1,2,3));
        Collections.shuffle(orientations);
        Random rand = new Random();
        int orientation = rand.nextInt(4);
        //Common.log("Checking singular orientaton " + orientation);
        if (specificOrientationWorks(dungeon, shape, startingPosition, orientation)) {
            return orientation;
        }
        return 4;
    }

    public static int shapeFitsAtLocationAnyOrientation(Dungeon dungeon, String shape, Location startingPosition) {
        if (shape.equals("1x1_Square")) {
            return 0;
        }
        ArrayList<Integer> orientations = new ArrayList<>(Arrays.asList(0,1,2,3));
        Collections.shuffle(orientations);
        for (Integer orientation : orientations) {
            //Common.log("Checking orientaton " + orientation);
            if (specificOrientationWorks(dungeon, shape, startingPosition, orientation)) {
                return orientation;
            }
        }
        return 4;
    }

    public static boolean specificOrientationWorks(Dungeon dungeon, String shape, Location checkedPoint, int orientation) {
        ArrayList<Location> locationsMustFit = DungeonRoomShapeOrientation.calculateLocationsNeededOrientation(shape, checkedPoint, orientation);
        for (Location location : locationsMustFit) {
            if (!dungeon.getPointLocations().contains(location) || dungeon.findPointFromLocation(location).isAlreadyUsed()) {
                return false;
            }
        }
        return true;
    }

    public static String chooseRandomRoomShape() {
        final Random random = new Random();
        int shapeChoice = random.nextInt(6);
        switch (shapeChoice) {
            case 0:
                return "1x1_Square";
            case 1:
                return "2x2_Square";
            case 2:
                return "1x1x1_L";
            case 3:
                return "1x2_Rectangle";
            case 4:
                return "1x3_Rectangle";
            case 5:
                return "1x4_Rectangle";
        }
        return "Failed";
    }

    public static String chooseRandomRoomShapeExclusionsNoSingles(ArrayList<String> exclusions) {
        ArrayList<String> types = new ArrayList<>();
        types.add("2x2_Square");
        types.add("1x1x1_L");
        types.add("1x2_Rectangle");
        types.add("1x3_Rectangle");
        types.add("1x4_Rectangle");
        if (!exclusions.isEmpty()) {
            for (String excludes : exclusions) {
                types.remove(excludes);
            }
        }
        final Random random = new Random();
        if (!types.isEmpty()) {
            int shapeChoice = random.nextInt(types.size());
            return types.get(shapeChoice);
        }
        return "Failed";
    }

    public void additiveRoomCurrentValues(String shape) {
        switch (shape) {
            case "1x1_Square":
                this.current11Square++;
            case "2x2_Square":
                this.current22Square++;
            case "1x1x1_L":
                this.current111L++;
            case "1x2_Rectangle":
                this.current12Rectangle++;
            case "1x3_Rectangle":
                this.current13Rectangle++;
            case "1x4_Rectangle":
                this.current14Rectangle++;
        }
    }

    public boolean checkMinimumSchematicsExist() {
        return true;
    }

    public static File locateSchematic(String name) {
        File schematic = FileUtil.getFile("DungeonStorage/Schematics/" + name + ".schematic");
        checkBoolean(schematic.exists());
        return schematic;
    }

    public static Region createDungeonRegion(Dungeon dungeon) {
        Location lobby = DungeonLastInstanceStorage.findNextInstanceLocation();
        dungeon.setLobbyLocation(lobby);
        double maxDungeonHeight = 150;
        double roomRadius = 16;
        Location proceduralOne = lobby.clone().add(lobby.getX() - (69 + roomRadius), lobby.getY() - 1, lobby.getZ() + roomRadius);
        Location proceduralTwo = lobby.clone().add(lobby.getX() + (67 + roomRadius), lobby.getY() + maxDungeonHeight, lobby.getZ() - (136 + roomRadius));
        return new Region(dungeon.getName(), proceduralOne, proceduralTwo);
    }

    public static void createDungeonRoomRegion() {

    }

    public static ArrayList<Location> generatePossibleRoomCenterLocations(Location startingCenter, int floor) {
        Common.log("Generating Dungeon Room Possible Center Locations");
        ArrayList<Location> allCenters = new ArrayList<>();
        allCenters.add(startingCenter);
        Location lastLocation = startingCenter;
        int baseRoomDiameter = 31;
        int gapSize = 3;
        int dimensions = 6;
        for (int i = 0; i < dimensions; i++) {
            for (int b = 0; b <= (dimensions - 2); b++) {
                lastLocation = lastLocation.clone().add(baseRoomDiameter + gapSize, 0, 0);
                allCenters.add(lastLocation);
                Common.log("Added possible location: " + lastLocation.getX() + " " + lastLocation.getZ());
            }
            if (i < (dimensions - 1)) {
                lastLocation = startingCenter.clone().add(0, 0, (baseRoomDiameter + gapSize) * (i + 1));
                allCenters.add(lastLocation);
                Common.log("Added possible location: " + lastLocation.getX() + " " + lastLocation.getZ());
            }
        }
        Common.log("Generated " + allCenters.size() + " Possible Points");
        return allCenters;
    }

    public static int findRowsForFloor(int floor) {
        int temp;
        if (floor == 7) {
            temp = 6;
        } else if (floor >= 5) {
            temp = 5;
        } else if (floor >= 3) {
            temp = 3;
        } else if (floor > 0) {
            temp = 2;
        } else {
            temp = 1;
        }
        return temp * 34;
    }

}