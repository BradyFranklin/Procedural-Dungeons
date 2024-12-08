package org.ninenetwork.infinitedungeons.command.tests;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.command.SimpleCommand;
import org.ninenetwork.infinitedungeons.PlayerCache;
import org.ninenetwork.infinitedungeons.dungeon.DungeonRoom;

public class TestSetFloorHeight extends SimpleCommand {

    public TestSetFloorHeight() {
        super("setfloor");
        setMinArguments(4);
    }

    @Override
    protected void onCommand() {
        checkConsole();
        if (sender instanceof Player) {
            Player player = (Player) sender;
            PlayerCache cache = PlayerCache.from(player);
            DungeonRoom dungeonRoom = DungeonRoom.findByName(args[0]);
            Location secretLocation = new Location(player.getWorld(), Double.parseDouble(args[1]), Double.parseDouble(args[2]), Double.parseDouble(args[3]));
            if (dungeonRoom != null) {
                dungeonRoom.setFloorHeight(secretLocation);

                Common.tell(player, "set floor height");
            }
        }
    }

}