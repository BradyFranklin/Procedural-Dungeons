package org.ninenetwork.infinitedungeons.dungeon;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.mineacademy.fo.Common;
import org.ninenetwork.infinitedungeons.dungeon.instance.DungeonRoomInstance;
import org.ninenetwork.infinitedungeons.dungeon.instance.DungeonRoomPoint;
import org.ninenetwork.infinitedungeons.dungeon.instance.DungeonRoomShape;
import org.ninenetwork.infinitedungeons.settings.Settings;
import org.ninenetwork.infinitedungeons.util.GeneralUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

@Getter
@Setter
public class DungeonGrid {

    public String gridName;
    private int floor;
    private ArrayList<DungeonRoomPoint> pointsStillEmpty;
    private ArrayList<DungeonRoomShape> shapes = new ArrayList<>();
    private int dimensionsH;
    private int dimensionsV;
    private DungeonRoomInstance[][] dungeonGrid;
    private DungeonRoomShape[][] shapeGrid;
    private DungeonRoomPoint[][] pointGrid;
    private Location[][] locationGrid;
    private int lobbyPosition;
    private int bloodPosition;


    DungeonGrid(String name, int floor) {
        this.gridName = name;
        this.floor = floor;
        this.dimensionsH = GeneralUtils.findRowsForFloor(floor, "h");
        this.dimensionsV = GeneralUtils.findRowsForFloor(floor, "v");
        dungeonGrid = new DungeonRoomInstance[this.dimensionsH][this.dimensionsV];
        shapeGrid = new DungeonRoomShape[this.dimensionsH][this.dimensionsV];
        pointGrid = new DungeonRoomPoint[this.dimensionsH][this.dimensionsV];
        locationGrid = new Location[this.dimensionsH][this.dimensionsV];
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
                    if (isOutside(currentShape.getGridConsumption().get(0).getPointPosition(), this.floor)) {
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
        DungeonRoomShape[][] clearGrid = new DungeonRoomShape[this.dimensionsH][this.dimensionsV];
        this.shapeGrid = clearGrid;
    }

    public void initializeLocationGrid(Dungeon dungeon) {
        ArrayList<Location> pointLocations = dungeon.getPointLocations();
        Location[][] locGrid = new Location[this.dimensionsH][this.dimensionsV];
        int iteration = 0;
        for (int rows = 0; rows < locGrid.length; rows++) {
            for (int cols = 0; cols < locGrid[rows].length; cols++) {
                locGrid[rows][cols] = pointLocations.get(iteration);
                iteration++;
                if (Settings.PluginServerSettings.DEBUG_MODE) {
                    Common.log("Location grid init adding location to visualized grid slot " + iteration);
                }
            }
        }
        this.locationGrid = locGrid;
    }

    public void initializePointsGrid(Dungeon dungeon) {
        ArrayList<DungeonRoomPoint> points = dungeon.getPointTracking();
        DungeonRoomPoint[][] pointsGrid = new DungeonRoomPoint[this.dimensionsH][this.dimensionsV];
        int iteration = 0;
        for (int rows = 0; rows < pointsGrid.length; rows++) {
            for (int cols = 0; cols < pointsGrid[rows].length; cols++) {
                pointsGrid[rows][cols] = points.get(iteration);
                iteration++;
                if (Settings.PluginServerSettings.DEBUG_MODE) {
                    Common.log("Point grid init adding point to visualized grid slot " + iteration);
                }
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
            row = convertToRow(val, this.floor);
            col = convertToColumn(val, this.floor);
            if (val < (this.dimensionsH * this.dimensionsV)) {
                DungeonRoomShape[][] grids = this.shapeGrid.clone();
                if (grids[row][col] == null) {
                    if (Settings.PluginServerSettings.DEBUG_MODE) {
                        Common.log("Adding shape segment to grid");
                    }
                    grids[row][col] = shape;
                    point.setAlreadyUsed(true);
                    this.shapeGrid = grids;
                } else {
                    //Common.log("Registered shape already in grid position");
                }
            }
        }
    }

    public static int convertToRow(int slot, int floor) {
        if (floor == 7 || floor == 6 || floor == 5) {
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
        } else if (floor == 4 || floor == 3) {
            if (slot >= 20) {
                return 4;
            } else if (slot >= 15) {
                return 3;
            } else if (slot >= 10) {
                return 2;
            } else if (slot >= 5) {
                return 1;
            } else {
                return 0;
            }
        } else if (floor == 2 || floor == 1) {
            if (slot >= 12) {
                return 3;
            } else if (slot >= 8) {
                return 2;
            } else if (slot >= 4) {
                return 1;
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    public static int convertToColumn(int slot, int floor) {
        if (floor == 7 || floor == 6 || floor == 5) {
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
        } else if (floor == 4 || floor == 3) {
            if (slot == 24 || slot == 19 || slot == 14 || slot == 9 || slot == 4) {
                return 4;
            } else if (slot == 23 || slot == 18 || slot == 13 || slot == 8 || slot == 3) {
                return 3;
            } else if (slot == 22 || slot == 17 || slot == 12 || slot == 7 || slot == 2) {
                return 2;
            } else if (slot == 21 || slot == 16 || slot == 11 || slot == 6 || slot == 1) {
                return 1;
            } else {
                return 0;
            }
        } else if (floor == 2 || floor == 1) {
            if (slot == 15 || slot == 11 || slot == 7 || slot == 3) {
                return 3;
            } else if (slot == 14 || slot == 10 || slot == 6 || slot == 2) {
                return 2;
            } else if (slot == 13 || slot == 9 || slot == 5 || slot == 1) {
                return 1;
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    public static int chooseRandomOutsideSlotExceptBottom(String side, int floor) {
        ArrayList<Integer> points = null;
        if (floor == 7 || floor == 6 || floor == 5) {
            if (side.equalsIgnoreCase("top")) {
                points = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));
            } else if (side.equalsIgnoreCase("left")) {
                points = new ArrayList<>(Arrays.asList(6, 12, 18));
                if (floor == 7)
                    points.add(24);
            } else if (side.equalsIgnoreCase("right")) {
                points = new ArrayList<>(Arrays.asList(23, 17, 11));
                if (floor == 7)
                    points.add(29);
            }
        } else if (floor == 4 || floor == 3) {
            if (side.equalsIgnoreCase("top")) {
                points = new ArrayList<>(Arrays.asList(1, 2, 3, 4));
            } else if (side.equalsIgnoreCase("left")) {
                points = new ArrayList<>(Arrays.asList(5, 10, 15));
            } else if (side.equalsIgnoreCase("right")) {
                points = new ArrayList<>(Arrays.asList(9, 14, 19));
            }
        } else if (floor == 2 || floor == 1) {
            if (side.equalsIgnoreCase("top")) {
                points = new ArrayList<>(Arrays.asList(1, 2, 3));
            } else if (side.equalsIgnoreCase("left")) {
                points = new ArrayList<>(Arrays.asList(4, 8));
            } else if (side.equalsIgnoreCase("right")) {
                points = new ArrayList<>(Arrays.asList(7, 11));
            }
        }
        Collections.shuffle(points);
        return points.get(0);
    }

    public static boolean isOutside(int slot, int floor) {
        if (floor == 7) {
            return slot > 29 || slot <= 5 || slot == 6 || slot == 12 || slot == 18 || slot == 24 || slot == 29 || slot == 23 || slot == 17 || slot == 11;
        } else if (floor == 6 || floor == 5) {
            return slot > 23 || slot <= 5 || slot == 6 || slot == 12 || slot == 18 || slot == 23 || slot == 17 || slot == 11;
        } else if (floor == 4 || floor == 3) {
            return slot > 19 || slot <= 4 || slot == 5 || slot == 10 || slot == 15 || slot == 19 || slot == 14 || slot == 9;
        } else if (floor == 2 || floor == 1) {
            return slot > 11 || slot <= 3 || slot == 4 || slot == 8 || slot == 11 || slot == 7;
        } else {
            return false;
        }
    }

    public static ArrayList<Integer> outsidePointsSpecificSide(String side, int floor) {
        ArrayList<Integer> points = new ArrayList<>();
        if (floor == 7) {
            if (side.equalsIgnoreCase("top")) {
                points = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5));
            } else if (side.equalsIgnoreCase("bottom")) {
                points = new ArrayList<>(Arrays.asList(35, 34, 33, 32, 31, 30));
            } else if (side.equalsIgnoreCase("left")) {
                points = new ArrayList<>(Arrays.asList(6, 12, 18, 24));
            } else if (side.equalsIgnoreCase("right")) {
                points = new ArrayList<>(Arrays.asList(29, 23, 17, 11));
            }
        } else if (floor == 6 || floor == 5) {
            if (side.equalsIgnoreCase("top")) {
                points = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5));
            } else if (side.equalsIgnoreCase("bottom")) {
                points = new ArrayList<>(Arrays.asList(29, 28, 27, 26, 25, 24));
            } else if (side.equalsIgnoreCase("left")) {
                points = new ArrayList<>(Arrays.asList(6, 12, 18));
            } else if (side.equalsIgnoreCase("right")) {
                points = new ArrayList<>(Arrays.asList(23, 17, 11));
            }
        } else if (floor == 4 || floor == 3) {
            if (side.equalsIgnoreCase("top")) {
                points = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4));
            } else if (side.equalsIgnoreCase("bottom")) {
                points = new ArrayList<>(Arrays.asList(24, 23, 22, 21, 20));
            } else if (side.equalsIgnoreCase("left")) {
                points = new ArrayList<>(Arrays.asList(5, 10, 15));
            } else if (side.equalsIgnoreCase("right")) {
                points = new ArrayList<>(Arrays.asList(9, 14, 19));
            }
        } else if (floor == 2 || floor == 1) {
            if (side.equalsIgnoreCase("top")) {
                points = new ArrayList<>(Arrays.asList(0, 1, 2, 3));
            } else if (side.equalsIgnoreCase("bottom")) {
                points = new ArrayList<>(Arrays.asList(15, 14, 13, 12));
            } else if (side.equalsIgnoreCase("left")) {
                points = new ArrayList<>(Arrays.asList(4, 8));
            } else if (side.equalsIgnoreCase("right")) {
                points = new ArrayList<>(Arrays.asList(7, 11));
            }
        }
        return points;
    }

    public static String directionNeeded(int slot, int floor) {
        // reversed to find needed direction
        if (floor == 7) {
            if (slot <= 5) {
                return "bottom";
            } else if (slot > 29 && slot <= 36) {
                return "top";
            } else if (slot == 29 || slot == 23 || slot == 17 || slot == 11) {
                return "left";
            } else if (slot == 6 || slot == 12 || slot == 18 || slot == 24) {
                return "right";
            }
        } else if (floor == 6 || floor == 5) {
            if (slot <= 5) {
                return "bottom";
            } else if (slot > 23 && slot < 30) {
                return "top";
            } else if (slot == 23 || slot == 17 || slot == 11) {
                return "left";
            } else if (slot == 6 || slot == 12 || slot == 18) {
                return "right";
            }
        } else if (floor == 4 || floor == 3) {
            if (slot <= 4) {
                return "bottom";
            } else if (slot > 19 && slot < 25) {
                return "top";
            } else if (slot == 9 || slot == 14 || slot == 19) {
                return "left";
            } else if (slot == 5 || slot == 10 || slot == 15) {
                return "right";
            }
        } else if (floor == 2 || floor == 1) {
            if (slot <= 3) {
                return "bottom";
            } else if (slot > 11 && slot < 16) {
                return "top";
            } else if (slot == 7 || slot == 11) {
                return "left";
            } else if (slot == 4 || slot == 8) {
                return "right";
            }
        }
        return "none";
    }

}