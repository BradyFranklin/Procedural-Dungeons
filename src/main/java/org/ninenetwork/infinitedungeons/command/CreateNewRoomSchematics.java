package org.ninenetwork.infinitedungeons.command;

import org.bukkit.entity.Player;
import org.mineacademy.fo.MinecraftVersion;
import org.mineacademy.fo.command.SimpleCommand;
import org.mineacademy.fo.model.HookManager;
import org.ninenetwork.infinitedungeons.dungeon.DungeonRoom;
import org.ninenetwork.infinitedungeons.dungeon.DungeonRoomType;
import org.ninenetwork.infinitedungeons.dungeon.roomtools.DungeonRoomCreation;

public class CreateNewRoomSchematics extends SimpleCommand {

    public CreateNewRoomSchematics() {
        super("droomsave");
        setMinArguments(1);
    }

    @Override
    protected void onCommand() {
        checkConsole();
        Player player = (Player) sender;
        String name = args[0];

        DungeonRoom dungeonRoom = DungeonRoom.findByName(name);

        DungeonRoomCreation.setDungeonRoomSchematic(player, dungeonRoom);
    }

}