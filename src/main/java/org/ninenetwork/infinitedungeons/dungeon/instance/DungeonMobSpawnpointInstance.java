package org.ninenetwork.infinitedungeons.dungeon.instance;

import org.bukkit.Location;
import org.bukkit.Material;
import org.mineacademy.fo.Common;
import org.ninenetwork.infinitedungeons.dungeon.DungeonRoomType;
import org.ninenetwork.infinitedungeons.dungeon.secret.SecretType;

import java.util.HashMap;
import java.util.Map;

public class DungeonMobSpawnpointHandler {

    public void initializeAllDungeonSecrets() {
        Location roomCenter;
        Location instanceCenter;
        Location secretLocation = null;
        int orientation;
        double x;
        double y;
        double z;
        double xx;
        double yy;
        double zz;
        double xDifferenceFrom0 = 0;
        double yDifferenceFrom0 = 0;
        double zDifferenceFrom0 = 0;
        double yfinal = 0;
        String xBaseType = "none";
        String yBaseType = "none";
        String zBaseType = "none";
        for (DungeonRoomInstance instance : dungeon.getDungeonRooms()) {
            Common.log("Attempting to search a room for secrets");
            if (instance.getType() == DungeonRoomType.BLOODRUSH && !instance.getDungeonRoom().getSecrets().isEmpty()) {
                Common.log("room has secrets");
                if (!secretInstances.containsKey(instance)) {
                    Map<Location, SecretType> m = new HashMap<>();
                    secretInstances.put(instance, m);
                }
                orientation = instance.getOrientation();
                roomCenter = instance.getDungeonRoom().getRoomCenter();
                instanceCenter = instance.getShape().getRoomCenterLocation();
                xx = instanceCenter.getX();
                yy = instanceCenter.getY();
                zz = instanceCenter.getZ();
                x = roomCenter.getX();
                y = roomCenter.getY();
                z = roomCenter.getZ();
                for (Location location : instance.getDungeonRoom().getSecrets()) {
                    if (location.getX() < x) {
                        xDifferenceFrom0 = Math.floor(x) - Math.floor(location.getX());
                        xBaseType = "subtract";
                    } else if (location.getX() == x) {
                        xDifferenceFrom0 = 0;
                        xBaseType = "none";
                    } else if (location.getX() > x) {
                        xDifferenceFrom0 = Math.floor(location.getX()) - Math.floor(x);
                        xBaseType = "add";
                    }

                    if (location.getY() < y) {
                        yDifferenceFrom0 = Math.floor(y) - Math.floor(location.getY());
                        yfinal = -yDifferenceFrom0;
                    } else if (location.getY() == y) {
                        yDifferenceFrom0 = 0;
                        yfinal = 0;
                    } else if (location.getY() > y) {
                        yDifferenceFrom0 = Math.floor(location.getY()) - Math.floor(y);
                        yfinal = yDifferenceFrom0;
                    }

                    if (location.getZ() < z) {
                        zDifferenceFrom0 = Math.floor(z) - Math.floor(location.getZ());
                        zBaseType = "subtract";
                    } else if (location.getZ() == z) {
                        zDifferenceFrom0 = 0;
                        zBaseType = "none";
                    } else if (location.getZ() > z) {
                        zDifferenceFrom0 = Math.floor(location.getZ()) - Math.floor(z);
                        zBaseType = "add";
                    }

                    if (orientation == 0) {
                        if (xBaseType.equalsIgnoreCase("subtract") && zBaseType.equalsIgnoreCase("subtract")) {
                            secretLocation = new Location(location.getWorld(), xx - xDifferenceFrom0, yy + yfinal, zz - zDifferenceFrom0);
                        } else if (xBaseType.equalsIgnoreCase("subtract") && zBaseType.equalsIgnoreCase("add")) {
                            secretLocation = new Location(location.getWorld(), xx - xDifferenceFrom0, yy + yfinal, zz + zDifferenceFrom0);
                        } else if (xBaseType.equalsIgnoreCase("add") && zBaseType.equalsIgnoreCase("subtract")) {
                            secretLocation = new Location(location.getWorld(), xx + xDifferenceFrom0, yy + yfinal, zz - zDifferenceFrom0);
                        } else if (xBaseType.equalsIgnoreCase("add") && zBaseType.equalsIgnoreCase("add")) {
                            secretLocation = new Location(location.getWorld(), xx + xDifferenceFrom0, yy + yfinal, zz + zDifferenceFrom0);
                        } else if (xBaseType.equalsIgnoreCase("none") && zBaseType.equalsIgnoreCase("add")) {

                        } else if (xBaseType.equalsIgnoreCase("none") && zBaseType.equalsIgnoreCase("subtract")) {

                        } else if (xBaseType.equalsIgnoreCase("add") && zBaseType.equalsIgnoreCase("none")) {

                        } else if (xBaseType.equalsIgnoreCase("subtract") && zBaseType.equalsIgnoreCase("none")) {

                        } else if (xBaseType.equalsIgnoreCase("none") && zBaseType.equalsIgnoreCase("none")) {

                        }
                    } else if (orientation == 1) {
                        if (xBaseType.equalsIgnoreCase("subtract") && zBaseType.equalsIgnoreCase("subtract")) {
                            secretLocation = new Location(location.getWorld(), xx + zDifferenceFrom0, yy + yfinal, zz - xDifferenceFrom0);
                        } else if (xBaseType.equalsIgnoreCase("subtract") && zBaseType.equalsIgnoreCase("add")) {
                            secretLocation = new Location(location.getWorld(), xx - zDifferenceFrom0, yy + yfinal, zz - xDifferenceFrom0);
                        } else if (xBaseType.equalsIgnoreCase("add") && zBaseType.equalsIgnoreCase("subtract")) {
                            secretLocation = new Location(location.getWorld(), xx + zDifferenceFrom0, yy + yfinal, zz + xDifferenceFrom0);
                        } else if (xBaseType.equalsIgnoreCase("add") && zBaseType.equalsIgnoreCase("add")) {
                            secretLocation = new Location(location.getWorld(), xx - zDifferenceFrom0, yy + yfinal, zz + xDifferenceFrom0);
                        }
                    } else if (orientation == 2) {
                        if (xBaseType.equalsIgnoreCase("subtract") && zBaseType.equalsIgnoreCase("subtract")) {
                            secretLocation = new Location(location.getWorld(), xx + xDifferenceFrom0, yy + yfinal, zz + zDifferenceFrom0);
                        } else if (xBaseType.equalsIgnoreCase("subtract") && zBaseType.equalsIgnoreCase("add")) {
                            secretLocation = new Location(location.getWorld(), xx + xDifferenceFrom0, yy + yfinal, zz - zDifferenceFrom0);
                        } else if (xBaseType.equalsIgnoreCase("add") && zBaseType.equalsIgnoreCase("subtract")) {
                            secretLocation = new Location(location.getWorld(), xx - xDifferenceFrom0, yy + yfinal, zz + zDifferenceFrom0);
                        } else if (xBaseType.equalsIgnoreCase("add") && zBaseType.equalsIgnoreCase("add")) {
                            secretLocation = new Location(location.getWorld(), xx - xDifferenceFrom0, yy + yfinal, zz - zDifferenceFrom0);
                        }
                    } else if (orientation == 3) {
                        if (xBaseType.equalsIgnoreCase("subtract") && zBaseType.equalsIgnoreCase("subtract")) {
                            secretLocation = new Location(location.getWorld(), xx - zDifferenceFrom0, yy + yfinal, zz + xDifferenceFrom0);
                        } else if (xBaseType.equalsIgnoreCase("subtract") && zBaseType.equalsIgnoreCase("add")) {
                            secretLocation = new Location(location.getWorld(), xx + zDifferenceFrom0, yy + yfinal, zz + xDifferenceFrom0);
                        } else if (xBaseType.equalsIgnoreCase("add") && zBaseType.equalsIgnoreCase("subtract")) {
                            secretLocation = new Location(location.getWorld(), xx - zDifferenceFrom0, yy + yfinal, zz - xDifferenceFrom0);
                        } else if (xBaseType.equalsIgnoreCase("add") && zBaseType.equalsIgnoreCase("add")) {
                            secretLocation = new Location(location.getWorld(), xx + zDifferenceFrom0, yy + yfinal, zz - xDifferenceFrom0);
                        }
                    }
                    Common.log("Adding secret to " + secretLocation.getX() + "," + secretLocation.getY() + "," + secretLocation.getZ());
                    instance.addSecret(secretLocation);
                    instance.addSecretToMap(secretLocation);
                    if (secretLocation.getBlock().getType() == Material.PLAYER_HEAD) {
                        this.secretInstances.get(instance).put(secretLocation, SecretType.ESSENCE);
                    } else if (secretLocation.getBlock().getType() == Material.CHEST || secretLocation.getBlock().getType() == Material.TRAPPED_CHEST) {
                        this.secretInstances.get(instance).put(secretLocation, SecretType.BLESSING);
                    }
                }
            }
        }
    }

}