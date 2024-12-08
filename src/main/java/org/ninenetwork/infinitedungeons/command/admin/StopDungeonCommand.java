package org.ninenetwork.infinitedungeons.command.admin;

import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.FileUtil;
import org.mineacademy.fo.command.SimpleCommand;
import org.ninenetwork.infinitedungeons.dungeon.Dungeon;
import org.ninenetwork.infinitedungeons.dungeon.DungeonStopReason;

public class StopDungeonCommand extends SimpleCommand {

    public StopDungeonCommand() {
        super("dstop");
        setMinArguments(1);
        setUsage("<DungeonName>");
    }

    @Override
    protected void onCommand() {
        checkConsole();
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Dungeon dungeon = null;
            //Dungeon dungeon = Dungeon.findByName(args[0]);
            for (Dungeon d : Dungeon.getDungeons()) {
                if (d.getName().contains(args[0])) {
                    dungeon = d;
                }
            }
            if (dungeon != null) {
                if (dungeon.getDungeonParty() != null) {
                    dungeon.getDungeonParty().disbandParty(dungeon.getDungeonParty());
                }
                dungeon.stop(DungeonStopReason.COMMAND);
                String dungeonName = dungeon.getName();
                Dungeon.removeDungeon(dungeon.getName());
                Common.tell(player, dungeonName + " checking for files");
                if (FileUtil.getFile("DungeonStorage/Dungeons/" + dungeonName + ".yml").exists()) {
                    Common.tell(player, "File found");
                    boolean test = FileUtil.getFile("DungeonStorage/Dungeons/" + dungeonName + ".yml").delete();
                    Common.tell(player, "Deletion: " + test);
                }
            } else {
                Common.tell(player, "Unable to find that dungeon");
            }

        }
    }

}