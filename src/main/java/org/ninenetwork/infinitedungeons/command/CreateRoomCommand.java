package org.ninenetwork.infinitedungeons.command;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.mineacademy.fo.FileUtil;
import org.mineacademy.fo.MinecraftVersion;
import org.mineacademy.fo.command.SimpleCommand;
import org.mineacademy.fo.model.HookManager;
import org.mineacademy.fo.region.Region;
import org.ninenetwork.infinitedungeons.PlayerCache;
import org.ninenetwork.infinitedungeons.dungeon.*;
import org.ninenetwork.infinitedungeons.map.SchematicManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class CreateRoomCommand extends SimpleCommand {

    public CreateRoomCommand() {
        super("createroom");
        setMinArguments(3);
    }

    @Override
    protected void onCommand() {
        checkConsole();
        checkBoolean(HookManager.isWorldEditLoaded(), "Loading or saving schematic requires WorldEdit.");
        checkBoolean(MinecraftVersion.atLeast(MinecraftVersion.V.v1_13), "Loading or saving schematic requires Minecraft 1.13 or greater.");
        Player player = (Player) sender;
        String param = args[0];
        String name = args[1];
        String sizeIdentifier = args[2];

        PlayerCache data = PlayerCache.from(player);
        Region region = data.getRegion();
        checkBoolean(region.isWhole(), "Use /tools to set both primary/secondary region points.");

        final DungeonRoomType type = this.findEnum(DungeonRoomType.class, param.toUpperCase(), "No such room type '{0}'. Available: {available}");
        DungeonRoom dungeonRoom = DungeonRoom.createDungeonRoom(name, type);

        String path1 = "DungeonStorage/Schematics/" + (name + "1" + ".schematic");
        String path2 = "DungeonStorage/Schematics/" + (name + "2" + ".schematic");
        String path3 = "DungeonStorage/Schematics/" + (name + "3" + ".schematic");
        String path4 = "DungeonStorage/Schematics/" + (name + "4" + ".schematic");
        Location firstLocationChange;
        Location secondLocationChange;
        Location thirdLocationChange;
        Location fourthLocationChange;
        Location fifthLocationChange;
        Location sixthLocationChange;

        if (sizeIdentifier.equals("1x1_Square")) {
            File schematic1 = FileUtil.getOrMakeFile(path1);
            Region region1 = new Region(region.getPrimary(), region.getSecondary());
            SchematicManager.save(region1, schematic1);
            dungeonRoom.setSchematics(new ArrayList<>(Arrays.asList(schematic1.getName())));
        } else if (sizeIdentifier.equals("2x2_Square")) {
            File schematic1 = FileUtil.getOrMakeFile(path1);
            File schematic2 = FileUtil.getOrMakeFile(path2);
            File schematic3 = FileUtil.getOrMakeFile(path3);
            File schematic4 = FileUtil.getOrMakeFile(path4);
            firstLocationChange = region.getSecondary().clone().add(-31.0, 0.0, -31.0);
            Region region1 = new Region(region.getPrimary(), firstLocationChange);
            SchematicManager.save(region1, schematic1);
            secondLocationChange = region.getPrimary().clone().add(0.0, 0.0, 34.0);
            thirdLocationChange = region.getSecondary().clone().add(-31.0, 0.0, 0.0);
            Region region2 = new Region(secondLocationChange, thirdLocationChange);
            SchematicManager.save(region2, schematic2);
            fourthLocationChange = region.getPrimary().clone().add(34.0, 0.0, 34.0);
            Region region3 = new Region(fourthLocationChange, region.getSecondary());
            SchematicManager.save(region3, schematic3);
            fifthLocationChange = region.getPrimary().clone().add(34.0, 0.0, 0.0);
            sixthLocationChange = region.getSecondary().clone().add(0.0, 0.0, -31.0);
            Region region4 = new Region(fifthLocationChange, sixthLocationChange);
            SchematicManager.save(region4, schematic4);
            dungeonRoom.setSchematics(new ArrayList<>(Arrays.asList(schematic1.getName(), schematic2.getName(), schematic3.getName(), schematic4.getName())));
        } else if (sizeIdentifier.equals("1x1x1_L")) {
            File schematic1 = FileUtil.getOrMakeFile(path1);
            File schematic2 = FileUtil.getOrMakeFile(path2);
            File schematic3 = FileUtil.getOrMakeFile(path3);
            firstLocationChange = region.getPrimary().clone().add(0.0, 0.0, 34.0);
            secondLocationChange = region.getSecondary().clone().add(-34.0, 0.0, 0.0);
            Region region1 = new Region(firstLocationChange, secondLocationChange);
            SchematicManager.save(region1, schematic1);
            thirdLocationChange = region.getSecondary().clone().add(-31.0, 0.0, -31.0);
            Region region2 = new Region(region.getPrimary(), thirdLocationChange);
            SchematicManager.save(region2, schematic2);
            fourthLocationChange = region.getPrimary().clone().add(34.0, 0.0, 0.0);
            fifthLocationChange = region.getSecondary().clone().add(0.0, 0.0, -34.0);
            Region region3 = new Region(fourthLocationChange, fifthLocationChange);
            SchematicManager.save(region3, schematic3);
            dungeonRoom.setSchematics(new ArrayList<>(Arrays.asList(schematic1.getName(), schematic2.getName(), schematic3.getName())));
        } else if (sizeIdentifier.equals("1x2_Rectangle")) {
            File schematic1 = FileUtil.getOrMakeFile(path1);
            File schematic2 = FileUtil.getOrMakeFile(path2);
            firstLocationChange = region.getSecondary().clone().add(0.0, 0.0, -31.0);
            secondLocationChange = region.getPrimary().clone().add(0.0, 0.0, 34.0);
            Region region1 = new Region(region.getPrimary(), firstLocationChange);
            Region region2 = new Region(secondLocationChange, region.getSecondary());
            SchematicManager.save(region1, schematic1);
            SchematicManager.save(region2, schematic2);
            dungeonRoom.setSchematics(new ArrayList<>(Arrays.asList(schematic1.getName(), schematic2.getName())));
        } else if (sizeIdentifier.equals("1x3_Rectangle")) {
            File schematic1 = FileUtil.getOrMakeFile(path1);
            File schematic2 = FileUtil.getOrMakeFile(path2);
            File schematic3 = FileUtil.getOrMakeFile(path3);
            firstLocationChange = region.getSecondary().clone().add(0.0, 0.0, -64.0);
            Region region1 = new Region(region.getPrimary(), firstLocationChange);
            SchematicManager.save(region1, schematic1);
            secondLocationChange = region.getPrimary().clone().add(0.0, 0.0, 34.0);
            thirdLocationChange = region.getSecondary().clone().add(0.0, 0.0, -31.0);
            Region region2 = new Region(secondLocationChange, thirdLocationChange);
            SchematicManager.save(region2, schematic2);
            fourthLocationChange = region.getPrimary().clone().add(0.0, 0.0, 68.0);
            Region region3 = new Region(fourthLocationChange, region.getSecondary());
            SchematicManager.save(region3, schematic3);
            dungeonRoom.setSchematics(new ArrayList<>(Arrays.asList(schematic1.getName(), schematic2.getName(), schematic3.getName())));
        } else if (sizeIdentifier.equals("1x4_Rectangle")) {
            File schematic1 = FileUtil.getOrMakeFile(path1);
            File schematic2 = FileUtil.getOrMakeFile(path2);
            File schematic3 = FileUtil.getOrMakeFile(path3);
            File schematic4 = FileUtil.getOrMakeFile(path4);
            firstLocationChange = region.getSecondary().clone().add(0.0, 0.0, -97.0);
            Region region1 = new Region(region.getPrimary(), firstLocationChange);
            SchematicManager.save(region1, schematic1);
            secondLocationChange = region.getPrimary().clone().add(0.0, 0.0, 34);
            thirdLocationChange = region.getSecondary().clone().add(0.0, 0.0, -64.0);
            Region region2 = new Region(secondLocationChange, thirdLocationChange);
            SchematicManager.save(region2, schematic2);
            fourthLocationChange = region.getPrimary().clone().add(0.0, 0.0, 68.0);
            fifthLocationChange = region.getSecondary().clone().add(0.0, 0.0, -31);
            Region region3 = new Region(fourthLocationChange, fifthLocationChange);
            SchematicManager.save(region3, schematic3);
            sixthLocationChange = region.getPrimary().clone().add(0.0, 0.0, 102.0);
            Region region4 = new Region(sixthLocationChange, region.getSecondary());
            SchematicManager.save(region4, schematic4);
            dungeonRoom.setSchematics(new ArrayList<>(Arrays.asList(schematic1.getName(), schematic2.getName(), schematic3.getName(), schematic4.getName())));
        }
        dungeonRoom.setRoomIdentifier(sizeIdentifier);
    }

}