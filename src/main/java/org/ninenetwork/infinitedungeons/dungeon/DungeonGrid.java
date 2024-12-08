package org.ninenetwork.infinitedungeons.dungeon;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.mineacademy.fo.Common;
import org.ninenetwork.infinitedungeons.dungeon.instance.DungeonRoomInstance;
import org.ninenetwork.infinitedungeons.dungeon.instance.DungeonRoomPoint;
import org.ninenetwork.infinitedungeons.dungeon.instance.DungeonRoomShape;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

@Getter
@Setter
public class DungeonGrid {

    String gridName;
    private static final int GRID_SIZE = 6;
    private ArrayList<DungeonRoomPoint> pointsStillEmpty;
    private ArrayList<DungeonRoomShape> shapes = new ArrayList<>();
    private DungeonRoomInstance[][] dungeonGrid = new DungeonRoomInstance[6][6];
    private DungeonRoomShape[][] shapeGrid = new DungeonRoomShape[6][6];
    private DungeonRoomPoint[][] pointGrid = new DungeonRoomPoint[6][6];
    private Location[][] locationGrid = new Location[6][6];
    private int lobbyPosition;
    private int bloodPosition;


    DungeonGrid(String name) {
        this.gridName = name;
    }

    public void addShape(DungeonRoomShape shape) {
        ArrayList<DungeonRoomShape> shapers = new ArrayList<>();
        if (!this.shapes.isEmpty()) {
            shapers = this.shapes;
        }
        shapers.add(shape);
        this.shapes = shapers;
    }

    public DungeonRoomInstance getLobbyRoom(Dungeon dungeon) {
        for (DungeonRoomInstance instance : dungeon.getDungeonRooms()) {
            if (instance.getRoomType().equalsIgnoreCase("Lobby")) {
                return instance;
            }
        }
        return null;
    }

    public DungeonRoomInstance getBloodRoom(Dungeon dungeon) {
        for (DungeonRoomInstance instance : dungeon.getDungeonRooms()) {
            if (instance.getRoomType().equalsIgnoreCase("Blood")) {
                return instance;
            }
        }
        return null;
    }

    public boolean checkGridValid(Dungeon dungeon, DungeonGrid grid) {
        DungeonRoomShape[][] shaperGrid = grid.getShapeGrid();
        DungeonRoomShape currentShape;
        ArrayList<DungeonRoomShape> oneByOnesOutside = new ArrayList<>();
        int outsideOneByOnes = 0;
        int oneByOnes = 0;
        for (int rows = 0; rows < shaperGrid.length; rows++) {
            for (int cols = 0; cols < shaperGrid[rows].length; cols++) {
                currentShape = shaperGrid[rows][cols];
                if (currentShape.getRoomIdentifier().equalsIgnoreCase("1x1_Square")) {
                    oneByOnes++;
                    if (isOutside(currentShape.getGridConsumption().get(0).getPointPosition())) {
                        outsideOneByOnes++;
                        oneByOnesOutside.add(currentShape);
                    }
                }
            }
        }
        /*
        boolean validLobbyAndBlood = false;
        DungeonRoomPoint point;
        DungeonRoomShape id;
        for (DungeonRoomShape shape : oneByOnesOutside) {
            point = shape.getGridConsumption().get(0);
            for (int i : outsidePointsSpecificSide(directionNeeded(point.getPointPosition()))) {
                this.lobbyPosition = point.getPointPosition();
                shape.setRoomType("Lobby");
                id = shapeGrid[convertToRow(i)][convertToColumn(i)];
                if (id.getRoomIdentifier().equals("1x1_Square")) {
                    validLobbyAndBlood = true;
                    this.bloodPosition = id.getGridConsumption().get(0).getPointPosition();
                    id.setRoomType("Blood");
                    break;
                }
                shape.setRoomType(null);
                id.setRoomType(null);
            }
        }
        */

        if (outsideOneByOnes >= 2 && oneByOnes >= 5) {
            return true;
        }
        return false;
    }

    public void clearShapeGrid(DungeonGrid grid) {
        DungeonRoomShape[][] clearGrid = new DungeonRoomShape[6][6];
        this.shapeGrid = clearGrid;
    }

    public void initializeLocationGrid(Dungeon dungeon) {
        ArrayList<Location> pointLocations = dungeon.getPointLocations();
        Location[][] locGrid = new Location[6][6];
        int iteration = 0;
        for (int rows = 0; rows < locGrid.length; rows++) {
            for (int cols = 0; cols < locGrid[rows].length; cols++) {
                locGrid[rows][cols] = pointLocations.get(iteration);
                iteration++;
                Common.log("Location grid init adding location to visualized grid slot " + iteration);
            }
        }
        this.locationGrid = locGrid;
    }

    public void initializePointsGrid(Dungeon dungeon) {
        ArrayList<DungeonRoomPoint> points = dungeon.getPointTracking();
        DungeonRoomPoint[][] pointsGrid = new DungeonRoomPoint[6][6];
        int iteration = 0;
        for (int rows = 0; rows < pointsGrid.length; rows++) {
            for (int cols = 0; cols < pointsGrid[rows].length; cols++) {
                pointsGrid[rows][cols] = points.get(iteration);
                iteration++;
                Common.log("Point grid init adding point to visualized grid slot " + iteration);
            }
        }
        this.pointGrid = pointsGrid;
    }

    public boolean gridIsComplete(Dungeon dungeon) {

        DungeonRoomShape[][] grids = this.shapeGrid.clone();

        for (int rows = 0; rows < grids.length; rows++) {
            for (int cols = 0; cols < grids[rows].length; cols++) {
                if (grids[rows][cols] == null) {
                    return false;
                }
            }
        }
        return true;
    }

    public void addDungeonRoomShape(DungeonRoomShape shape) {
        int val, row, col;
        for (DungeonRoomPoint point : shape.getGridConsumption()) {
            val = point.getPointPosition();
            row = convertToRow(val);
            col = convertToColumn(val);
            if (val <= 35) {
                DungeonRoomShape[][] grids = this.shapeGrid.clone();
                if (grids[row][col] == null) {
                    Common.log("Adding shape segment to grid");
                    grids[row][col] = shape;
                    point.setAlreadyUsed(true);
                    this.shapeGrid = grids;
                } else {
                    //Common.log("Registered shape already in grid position");
                }
            }
        }
    }

    public static int convertToRow(int slot) {
        if (slot > 29) {
            return 5;
        } else if (slot > 23) {
            return 4;
        } else if (slot > 17) {
            return 3;
        } else if (slot > 11) {
            return 2;
        } else if (slot > 5) {
            return 1;
        } else {
            return 0;
        }
    }

    public static int convertToColumn(int slot) {
        if (slot == 35 || slot == 29 || slot == 23 || slot == 17 || slot == 11 || slot == 5) {
            return 5;
        } else if (slot == 34 || slot == 28 || slot == 22 || slot == 16 || slot == 10 || slot == 4) {
            return 4;
        } else if (slot == 33 || slot == 27 || slot == 21 || slot == 15 || slot == 9 || slot == 3) {
            return 3;
        } else if (slot == 32 || slot == 26 || slot == 20 || slot == 14 || slot == 8 || slot == 2) {
            return 2;
        } else if (slot == 31 || slot == 25 || slot == 19 || slot == 13 || slot == 7 || slot == 1) {
            return 1;
        } else {
            return 0;
        }
    }

    public static int chooseRandomOutsideSlotExceptBottom(String side) {
        ArrayList<Integer> points = null;
        if (side.equalsIgnoreCase("top")) {
            points = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));
        } else if (side.equalsIgnoreCase("left")) {
            points = new ArrayList<>(Arrays.asList(6, 12, 18, 24));
        } else if (side.equalsIgnoreCase("right")) {
            points = new ArrayList<>(Arrays.asList(29,23,17,11));
        }
        Collections.shuffle(points);
        return points.get(0);
    }

    public static boolean isOutside(int slot) {
        if (slot > 29 || slot <= 5 || slot == 6 || slot == 12 || slot == 18 || slot == 24 || slot == 29 || slot == 23 || slot == 17 || slot == 11) {
            return true;
        } else {
            return false;
        }
    }

    public static ArrayList<Integer> outsidePointsSpecificSide(String side) {
        ArrayList<Integer> points = null;
        if (side.equalsIgnoreCase("top")) {
            points = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5));
        } else if (side.equalsIgnoreCase("bottom")) {
            points = new ArrayList<>(Arrays.asList(35,34,33,32,31,30));
        } else if (side.equalsIgnoreCase("left")) {
            points = new ArrayList<>(Arrays.asList(6, 12, 18, 24));
        } else if (side.equalsIgnoreCase("right")) {
            points = new ArrayList<>(Arrays.asList(29,23,17,11));
        }
        return points;
    }

    public static String directionNeeded(int slot) {
        // reversed to find needed direction
        if (slot > 0 && slot <= 5) {
            return "bottom";
        } else if (slot > 29 && slot <= 36) {
            return "top";
        } else if (slot == 29 || slot == 23 || slot == 17 || slot == 11) {
            return "left";
        } else if (slot == 6 || slot == 12 || slot == 18 || slot == 24) {
            return "right";
        }
        return null;
    }

}