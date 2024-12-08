package org.ninenetwork.infinitedungeons.command.tests;

import org.bukkit.entity.Player;
import org.mineacademy.fo.FileUtil;
import org.mineacademy.fo.command.SimpleCommand;
import org.ninenetwork.infinitedungeons.PlayerCache;
import org.ninenetwork.infinitedungeons.dungeon.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class TestDefaultRoomCreator extends SimpleCommand {

    public TestDefaultRoomCreator() {
        super("makerooms");
    }

    @Override
    protected void onCommand() {
        checkConsole();
        if (sender instanceof Player) {
            Player player = (Player) sender;
            PlayerCache cache = PlayerCache.from(player);
            ArrayList<String> iterators = new ArrayList<>(Arrays.asList("1x1_Example", "1x2_Example", "1x3_Example", "1x4_Example", "1x1x1_Example", "2x2_Example"));
            DungeonRoomType type = DungeonRoomType.BLOODRUSH;
            ArrayList<String> schems = new ArrayList<>();
            for (String s : iterators) {
                DungeonRoom dungeonRoom = DungeonRoom.createDungeonRoom(s, type);
                if (s.equals("1x1_Example")) {
                    dungeonRoom.setRoomIdentifier("1x1_Square");
                } else if (s.equals("2x2_Example")) {
                    dungeonRoom.setRoomIdentifier("2x2_Square");
                } else if (s.equals("1x1x1_Example")) {
                    dungeonRoom.setRoomIdentifier("1x1x1_L");
                } else if (s.equals("1x2_Example")) {
                    dungeonRoom.setRoomIdentifier("1x2_Rectangle");
                } else if (s.equals("1x3_Example")) {
                    dungeonRoom.setRoomIdentifier("1x3_Rectangle");
                } else if (s.equals("1x4_Example")) {
                    dungeonRoom.setRoomIdentifier("1x4_Rectangle");
                }
                String path1 = "DungeonStorage/Schematics/" + (s + "1" + ".schematic");
                String path2 = "DungeonStorage/Schematics/" + (s + "2" + ".schematic");
                String path3 = "DungeonStorage/Schematics/" + (s + "3" + ".schematic");
                String path4 = "DungeonStorage/Schematics/" + (s + "4" + ".schematic");
                File schematic1 = FileUtil.getOrMakeFile(path1);
                schems.add(schematic1.getName());
                if (!s.equals("1x1_Example")) {
                    File schematic2 = FileUtil.getOrMakeFile(path2);
                    schems.add(schematic2.getName());
                    if (s.equals("1x3_Example") || s.equals("1x4_Example") || s.equals("1x1x1_Example") || s.equals("2x2_Example")) {
                        File schematic3 = FileUtil.getOrMakeFile(path3);
                        schems.add(schematic3.getName());
                        if (s.equals("1x4_Example") || s.equals("2x2_Example")) {
                            File schematic4 = FileUtil.getOrMakeFile(path4);
                            schems.add(schematic4.getName());
                        }
                    }
                }
                dungeonRoom.setSchematics(schems);
            }

        }
    }

}
