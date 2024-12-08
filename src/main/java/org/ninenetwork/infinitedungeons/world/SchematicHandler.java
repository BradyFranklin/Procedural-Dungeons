package org.ninenetwork.infinitedungeons.map;

import org.mineacademy.fo.FileUtil;

import java.io.File;
import java.util.*;

import static java.util.Collections.addAll;

public class SchematicHandler {

    public SchematicHandler() {

    }

    public static boolean validateSchematicsExist() {
        return schematicCheck("1x1_Square", 5) && schematicCheck("2x2_Square", 2) && schematicCheck("1x1x1_L", 2) &&
                schematicCheck("1x2_Rectangle", 3) && schematicCheck("1x3_Rectangle", 3) && schematicCheck("1x4_Rectangle", 2);
    }

    public static boolean schematicCheck(String definition, int minimum) {
        int amount = 0;
        String directory = "DungeonStorage/Schematics/" + definition + "/";
        for (File file : FileUtil.getFiles(directory, ".schematic")) {
            amount++;
        }
        return amount >= minimum;
    }

    public static File[] getSchematicsByRoom(String roomType) {
        String directory = "DungeonStorage/Schematics/" + roomType + "/";
        return FileUtil.getFiles(directory, ".schematic");
    }

    public static File retrieveRandomSchematic(String roomType) {
        String directory = "DungeonStorage/Schematics/" + roomType + "/";
        ArrayList<File> schematics = new ArrayList<>(Arrays.asList(FileUtil.getFiles(directory, ".schematic")));
        Collections.shuffle(schematics);
        Random rand = new Random();
        int selection = rand.nextInt(schematics.size());
        return schematics.get(selection);
    }

}