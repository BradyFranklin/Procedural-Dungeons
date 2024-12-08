package org.ninenetwork.infinitedungeons.command.tests;

import org.bukkit.entity.Player;
import org.mineacademy.fo.command.SimpleCommand;
import org.ninenetwork.infinitedungeons.PlayerCache;

public class SetEditingRoom extends SimpleCommand {

    public SetEditingRoom() {
        super("setroom");
        setMinArguments(1);
    }

    @Override
    protected void onCommand() {
        checkConsole();
        if (sender instanceof Player) {
            Player player = (Player) sender;
            PlayerCache cache = PlayerCache.from(player);
            String name = args[0];
            cache.setDungeonRoomEditing(name);
            //dungeon.setDungeonRooms(rooms);

        }
    }

}