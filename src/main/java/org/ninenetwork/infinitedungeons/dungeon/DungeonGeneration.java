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
import org.ninenetwork.infinitedungeons.dungeon.instance.*;
import org.ninenetwork.infinitedungeons.dungeon.secret.DungeonSecretInstance;
import org.ninenetwork.infinitedungeons.world.SchematicManager;
import org.ninenetwork.infinitedungeons.mob.DungeonMob;
import org.ninenetwork.infinitedungeons.mob.DungeonMobRegistry;
import org.ninenetwork.infinitedungeons.util.GeneralUtils;

import java.io.File;
import java.util.*;

import static org.mineacademy.fo.Valid.checkBoolean;

@Getter
@Setter
public final class DungeonGeneration {

    private Location startingPosition;
    private int floor;

    private Location spawnpoint;

    private ArrayList<Location> gridCentersRemaining;

    private ArrayList<DungeonRoom> rooms1x1 = new ArrayList<>();
    private ArrayList<DungeonRoom> rooms1x2 = new ArrayList<>();
    private ArrayList<DungeonRoom> rooms1x3 = new ArrayList<>();
    private ArrayList<DungeonRoom> rooms1x4 = new ArrayList<>();
    private ArrayList<DungeonRoom> rooms2x2 = new ArrayList<>();
    private ArrayList<DungeonRoom> rooms1x1x1 = new ArrayList<>();

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
        if (!dungeon.getDungeonIntegrity().isIntegrityMaintained()) {
            return;
        }
        this.floor = floor;
        checkBoolean(HookManager.isWorldEditLoaded());
        checkBoolean(MinecraftVersion.atLeast(MinecraftVersion.V.v1_13));
        Location location = DungeonLastInstanceStorage.findNextInstanceLocation();
        DungeonLastInstanceStorage.addDungeonInstanceCenter(dungeon, location);
        dungeon.setLobbyLocation(location);
        ArrayList<Location> locations = generatePossibleRoomCenterLocations(location, floor);
        dungeon.initializeDungeonPoints(locations);
        dungeon.setPointLocations(locations);
        DungeonMobRegistry.getInstance().addDungeon(dungeon);
        if (!(locations.size() < (GeneralUtils.findRowsForFloor(floor, "h") * GeneralUtils.findRowsForFloor(floor, "v")))) {
            int lastPointIndex = GeneralUtils.findRowsForFloor(floor, "h") * GeneralUtils.findRowsForFloor(floor, "v") - 1;
            dungeon.setRegion(new Region(locations.get(0).clone().add(-15.0, 0.0, -15.0), locations.get(lastPointIndex).clone().add(15.0, 65.0, 15.0)));
        }
    }

    public void dungeonInitialization(Dungeon dungeon, int floor) {
        if (!dungeon.getDungeonIntegrity().isIntegrityMaintained()) {
            return;
        }
        DungeonRoom.loadDungeonRooms();
        DungeonGrid grid = new DungeonGrid(dungeon.getName(), floor);
        grid.initializePointsGrid(dungeon);
        grid.initializeLocationGrid(dungeon);
        grid.setPointsStillEmpty(dungeon.getPointTracking());
        //createDungeonRooms(dungeon, 1, grid);
        this.rooms1x1 = DungeonRoom.findAllByIdentifier("1x1_Square");
        this.rooms1x2 = DungeonRoom.findAllByIdentifier("1x2_Rectangle");
        this.rooms1x3 = DungeonRoom.findAllByIdentifier("1x3_Rectangle");
        this.rooms1x4 = DungeonRoom.findAllByIdentifier("1x4_Rectangle");
        this.rooms2x2 = DungeonRoom.findAllByIdentifier("2x2_Square");
        this.rooms1x1x1 = DungeonRoom.findAllByIdentifier("1x1x1_L");
        addLobbyBlood(dungeon, grid);
        createDungeonRoomGrid(dungeon, 1, grid);
        DungeonSecretInstance secretInstance = new DungeonSecretInstance(dungeon);
        dungeon.setDungeonSecretInstance(secretInstance);
    }

    public void gridGenMiddleMan(Dungeon dungeon, DungeonGrid grid, int generationRounds) {
        Common.runLater(10, new BukkitRunnable() {
            @Override
            public void run() {
                if (generationRounds < 3) {
                    createDungeonRoomGrid(dungeon,generationRounds + 1, grid);
                } else if (generationRounds == 3) {
                    if (grid.checkGridValid(dungeon, grid)) {
                        dungeon.getDungeonIntegrity().setGridGeneration(DungeonIntegrityStatus.COMPLETED);
                        initializeDungeonRoomInstances(dungeon, grid);
                    } else {
                        // CHANGE THIS LATER, RESTART DUNGEON GENERATION IF FOR COMMENTED CONDITIONS
                        dungeon.getDungeonIntegrity().setGridGeneration(DungeonIntegrityStatus.COMPLETED); // failed case
                        initializeDungeonRoomInstances(dungeon, grid);
                        //grid.clearShapeGrid(grid);
                        //createDungeonRoomGrid(dungeon, 1, grid);
                    }
                }
            }
        });
    }

    public void createDungeonRoomGrid(Dungeon dungeon, int generationRound, DungeonGrid grid) {
        if (!dungeon.getDungeonIntegrity().isIntegrityMaintained()) {
            return;
        }
        dungeon.getDungeonIntegrity().setGridGeneration(DungeonIntegrityStatus.RUNNING);
        ArrayList<DungeonRoomPoint> dungeonRoomPoints = dungeon.getPointTracking();
        new BukkitRunnable() {
            int dungeonPointsIterator = 0;
            DungeonRoomPoint focusedPoint;
            final int generationRounds = generationRound;
            @Override
            public void run() {
                if (dungeonPointsIterator < dungeonRoomPoints.size()) {
                    int integrity = 0;
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
                        if (integrity > 100) {
                            Common.log("Issue generating dungeon, iterations over 100 while initializing grid");
                            dungeon.getDungeonIntegrity().changeIntegrity(false, "GridGeneration");
                            this.cancel();
                            break;
                        }
                        integrity++;
                    }
                    if (!focusedPoint.isAlreadyUsed()) {
                        DungeonRoomShape shape = shapeGenerator(dungeon, focusedPoint, generationRounds, false);
                        if (shape != null) {
                            fillGrids(shape, grid);
                        }
                    }
                } else {
                    gridGenMiddleMan(dungeon, grid, generationRounds);
                    this.cancel();
                    /*
                    if (generationRounds < 3) {
                        createDungeonRoomGrid(dungeon,generationRounds + 1, grid);
                    } else if (generationRound== 3) {
                        if (grid.checkGridValid(dungeon, grid)) {
                            initializeDungeonRoomInstances(dungeon, grid);
                        } else {
                            grid.clearShapeGrid(grid);
                            createDungeonRoomGrid(dungeon, 1, grid);
                        }
                    }
                    this.cancel();
                    */
                }
                this.dungeonPointsIterator++;
            }
        }.runTaskTimerAsynchronously(InfiniteDungeonsPlugin.getInstance(), 0L, 2L);
    }

    public void addLobbyBlood(Dungeon dungeon, DungeonGrid grid) {
        if (!dungeon.getDungeonIntegrity().isIntegrityMaintained()) {
            return;
        }
        dungeon.getDungeonIntegrity().setLobbyBloodInitialization(DungeonIntegrityStatus.RUNNING);
        boolean lobbySuccess = false;
        boolean bloodSuccess = false;
        Random rand = new Random();
        ArrayList<String> choices = new ArrayList<>(Arrays.asList("top", "left", "right"));
        String choice = choices.get(rand.nextInt(3));
        int lobbySlot = DungeonGrid.chooseRandomOutsideSlotExceptBottom(choice, this.floor);
        String opposite = DungeonGrid.directionNeeded(lobbySlot, this.floor);
        ArrayList<Integer> slotsPossible = DungeonGrid.outsidePointsSpecificSide(opposite, this.floor);
        int bloodSlot = slotsPossible.get(rand.nextInt(slotsPossible.size()));
        DungeonRoomPoint point = grid.getPointGrid()[DungeonGrid.convertToRow(lobbySlot, this.floor)][DungeonGrid.convertToColumn(lobbySlot, this.floor)];
        DungeonRoomShape lobbyShape = shapeGenerator(dungeon, point, 3, true);
        if (lobbyShape != null) {
            lobbyShape.setRoomType("lobby");
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
            lobbySuccess = true;
        }
        DungeonRoomPoint point2 = grid.getPointGrid()[DungeonGrid.convertToRow(bloodSlot, this.floor)][DungeonGrid.convertToColumn(bloodSlot, this.floor)];
        DungeonRoomShape bloodShape = shapeGenerator(dungeon, point2, 3, true);
        if (bloodShape != null) {
            bloodShape.setRoomType("blood");
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
            bloodSuccess = true;
        }
        if (lobbySuccess && bloodSuccess) {
            dungeon.getDungeonIntegrity().setLobbyBloodInitialization(DungeonIntegrityStatus.COMPLETED);
        } else {
            dungeon.getDungeonIntegrity().setLobbyBloodInitialization(DungeonIntegrityStatus.FAILED);
            dungeon.getDungeonIntegrity().changeIntegrity(false, "LobbyBloodInitialization");
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

    public ArrayList<DungeonRoom> getSpecificRooms(String identifier) {
        if (identifier.equalsIgnoreCase("1x1_Square")) {
            return this.rooms1x1;
        } else if (identifier.equalsIgnoreCase("2x2_Square")) {
            return this.rooms2x2;
        } else if (identifier.equalsIgnoreCase("1x1x1_L")) {
            return this.rooms1x1x1;
        } else if (identifier.equalsIgnoreCase("1x2_Rectangle")) {
            return this.rooms1x2;
        } else if (identifier.equalsIgnoreCase("1x3_Rectangle")) {
            return this.rooms1x3;
        } else if (identifier.equalsIgnoreCase("1x4_Rectangle")) {
            return this.rooms1x4;
        }
        return null;
    }

    public void initializeDungeonRoomInstances(Dungeon dungeon, DungeonGrid grid) {
        if (!dungeon.getDungeonIntegrity().isIntegrityMaintained()) {
            return;
        }
        dungeon.getDungeonIntegrity().setDungeonRoomInstancing(DungeonIntegrityStatus.RUNNING);
        ArrayList<DungeonRoom> dungeonRooms;
        ArrayList<DungeonRoomInstance> instances = new ArrayList<>();
        DungeonRoom dungeonRoom = null;
        String roomType = "bloodrush";
        Common.log("Shapes in grid for generating " + grid.getShapes().size());
        int iterator = 0;
        for (DungeonRoomShape shape : grid.getShapes()) {
            //added null check
            Common.log("Running for shape number " + iterator);
            iterator++;
            if (shape != null) {
                if (shape.getRoomType() == null) {
                    dungeonRooms = getSpecificRooms(shape.getRoomIdentifier());
                    if (!dungeonRooms.isEmpty()) {
                        if (dungeonRooms.size() > 1) {
                            Collections.shuffle(dungeonRooms);
                        }
                        dungeonRoom = dungeonRooms.get(0);
                    }
                } else if (shape.getRoomType().equalsIgnoreCase("lobby")) {
                    dungeonRoom = DungeonRoom.findByName("lobby");
                    roomType = "lobby";
                } else if (shape.getRoomType().equalsIgnoreCase("blood")) {
                    dungeonRoom = DungeonRoom.findByName("blood");
                    roomType = "blood";
                } else {
                    dungeonRooms = getSpecificRooms(shape.getRoomIdentifier());
                    if (dungeonRooms.size() > 1) {
                        Collections.shuffle(dungeonRooms);
                    }
                    dungeonRoom = dungeonRooms.get(0);
                }
                if (dungeonRoom != null) {
                    int secretCount = 0;
                    DungeonRoomInstance roomInstance = new DungeonRoomInstance(dungeon, dungeonRoom);
                    roomInstance.setRoomType(roomType);
                    roomInstance.setOrientation(shape.getOrientation());
                    roomInstance.setDungeonRoomPoints(shape.getGridConsumption());
                    roomInstance.setShape(shape);
                    roomInstance.setDungeonRoomRegions();
                    roomInstance.setSecretAmount(secretCount);
                    roomInstance.setRoomCenter(roomInstance.getShape().getRoomCenterLocation());
                    DungeonMobSpawnpointInstance.initializeDungeonMobSpawnpoints(roomInstance);
                    instances.add(roomInstance);
                    Common.log("Added room instance");
                } else {
                    dungeon.getDungeonIntegrity().setDungeonRoomInstancing(DungeonIntegrityStatus.FAILED);
                    dungeon.getDungeonIntegrity().changeIntegrity(false, "DungeonRoomInstancing");
                    Common.log("Null room found");
                }
            }
        }
        dungeon.setDungeonRooms(instances);
        dungeon.getDungeonScore().setTotalRooms(instances.size() - 1);
        dungeon.getDungeonIntegrity().setDungeonRoomInstancing(DungeonIntegrityStatus.COMPLETED);
        dungeonSchematicHandle(dungeon, grid);
    }

    public void dungeonSchematicHandle(Dungeon dungeon, DungeonGrid grid) {
        if (!dungeon.getDungeonIntegrity().isIntegrityMaintained()) {
            return;
        }
        dungeon.getDungeonIntegrity().setSchematicPasting(DungeonIntegrityStatus.RUNNING);

        List<DungeonRoomInstance> rooms = dungeon.getDungeonRooms();
        double centerOffset = 15.0;

        new BukkitRunnable() {

            int iterator = 0;
            DungeonRoomInstance currentRoom;
            int rotation;
            DungeonRoomShape shape;
            Location centerLocation;
            int schemCounter;
            int floorHeightDifference;

            @Override
            public void run() {
                if (iterator > 100) {
                    dungeon.getDungeonIntegrity().setSchematicPasting(DungeonIntegrityStatus.FAILED);
                    dungeon.getDungeonIntegrity().changeIntegrity(false, "SchematicPasting");
                    this.cancel();
                }
                if (iterator < rooms.size()) {
                    schemCounter = 1;
                    currentRoom = rooms.get(iterator);
                    shape = currentRoom.getShape();
                    rotation = shape.getOrientation();
                    floorHeightDifference = currentRoom.getFloorDifference();
                    for (DungeonRoomPoint point : shape.getGridConsumption()) {
                        centerLocation = point.getCenterLocation();
                        Location loc = new Location(centerLocation.getWorld(), centerLocation.getX() - centerOffset, centerLocation.getY() - floorHeightDifference, centerLocation.getZ() - centerOffset);
                        SchematicManager.paste(loc, locateSchematic(currentRoom.getDungeonRoom(), currentRoom.getDungeonRoom().getName().toLowerCase() + schemCounter), rotation);
                        schemCounter++;
                    }
                    //DungeonMob.initializeStarredMobs(dungeon, currentRoom);
                    DungeonMob.initializeCompletionMobs(dungeon, currentRoom);
                } else {
                    Common.log("Finished pasting dungeon " + dungeon.getName());
                    DungeonPathfinding pathfinding = new DungeonPathfinding(dungeon, grid);
                    dungeon.setDungeonPathfinding(pathfinding);
                    pathfinding.initializePathfinding(dungeon, grid);
                    this.cancel();
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

    public void pasteAndConnect(Dungeon dungeon, DungeonGrid grid, ArrayList<DungeonRoomPoint> gridConsumption, DungeonRoomShape shape) {
        if (!dungeon.getDungeonIntegrity().isIntegrityMaintained()) {
            return;
        }

        //DungeonRoomInstance dungeonRoomInstance = new DungeonRoomInstance(dungeon, dungeonRoom);
        //dungeonRoomInstance.setDungeonRoomPoints(gridConsumption);
        // Finishing this logic and assigning possible door locations to dungeonroominstance
        // with orientation and rotation taken into accountability

        Location location;
        Location previousPoint = null;
        Location centerLocation;
        int rotation = shape.getOrientation();
        int schemCounter = 1;
        ArrayList<DungeonRoom> rooms = getSpecificRooms(shape.getRoomIdentifier());
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
            SchematicManager.paste(loc, locateSchematic(dungeonRoom, dungeonRoom.getName().toLowerCase() + schemCounter), rotation);
            schemCounter++;
            grid.addDungeonRoomShape(shape);
        }

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
        shape.setRoomCenterLocation(pointCenter);
        return shape;
    }

    private int getMaxCount(Dungeon dungeon, String choice) {
        switch (choice) {
            case "2x2_Square":
                return dungeon.max22Square;
            case "1x1x1_L":
                return dungeon.max111L;
            case "1x2_Rectangle":
                return dungeon.max12Rectangle;
            case "1x3_Rectangle":
                return dungeon.max13Rectangle;
            case "1x4_Rectangle":
                return dungeon.max14Rectangle;
            default:
                return 0;
        }
    }

    public int checkShapeValid(Dungeon dungeon, String choice, Location position, int generationRound) {
        int orientation = 4;
        switch (choice) {
            case "1x1_Square":
                Random rand = new Random();
                orientation = rand.nextInt(4);
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

    public boolean checkMinimumSchematicsExist() {
        return true;
    }

    public static File locateSchematic(DungeonRoom dungeonRoom, String name) {
        File schematic = FileUtil.getFile("DungeonStorage/Schematics/" + folderClassification(dungeonRoom.getRoomIdentifier()) + "/" + name.toLowerCase() + ".schematic");
        checkBoolean(schematic.exists());
        return schematic;
    }

    public static ArrayList<Location> generatePossibleRoomCenterLocations(Location startingCenter, int floor) {
        Common.log("Generating Dungeon Room Possible Center Locations");
        ArrayList<Location> allCenters = new ArrayList<>();
        allCenters.add(startingCenter);
        Location lastLocation = startingCenter;
        int baseRoomDiameter = 31;
        int gapSize = 3;
        int dimensionsH = GeneralUtils.findRowsForFloor(floor, "h");
        int dimensionsV = GeneralUtils.findRowsForFloor(floor, "z");
        for (int i = 0; i < dimensionsV; i++) {
            for (int b = 0; b <= (dimensionsH - 2); b++) {
                lastLocation = lastLocation.clone().add(baseRoomDiameter + gapSize, 0, 0);
                allCenters.add(lastLocation);
                Common.log("Added possible location: " + lastLocation.getX() + " " + lastLocation.getZ());
            }
            if (i < (dimensionsV - 1)) {
                lastLocation = startingCenter.clone().add(0, 0, (baseRoomDiameter + gapSize) * (i + 1));
                allCenters.add(lastLocation);
                Common.log("Added possible location: " + lastLocation.getX() + " " + lastLocation.getZ());
            }
        }
        Common.log("Generated " + allCenters.size() + " Possible Points");
        return allCenters;
    }

    public static String folderClassification(String roomSize) {
        switch (roomSize) {
            case "1x1_Square":
                return "1x1";
            case "2x2_Square":
                return "2x2";
            case "1x1x1_L":
                return "1x1x1";
            case "1x2_Rectangle":
                return "1x2";
            case "1x3_Rectangle":
                return "1x3";
            case "1x4_Rectangle":
                return "1x4";
        }
        return null;
    }

}