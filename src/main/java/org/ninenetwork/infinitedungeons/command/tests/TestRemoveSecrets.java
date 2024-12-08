package org.ninenetwork.infinitedungeons.command.tests;

import org.bukkit.entity.Player;
import org.mineacademy.fo.command.SimpleCommand;
import org.ninenetwork.infinitedungeons.PlayerCache;
import org.ninenetwork.infinitedungeons.dungeon.DungeonFunctions;

public class TestRemoveSecrets extends SimpleCommand {

    public TestRemoveSecrets() {
        super("rsecrets");
        setMinArguments(1);
    }

    @Override
    protected void onCommand() {
        checkConsole();
        if (sender instanceof Player) {
            Player player = (Player) sender;
            PlayerCache cache = PlayerCache.from(player);
            DungeonFunctions.removeAllSecretsFromRoom(cache.getDungeonRoomEditing());
        }
    }

}