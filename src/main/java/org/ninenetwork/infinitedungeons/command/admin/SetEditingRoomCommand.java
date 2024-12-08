package org.ninenetwork.infinitedungeons.command.admin;

import org.bukkit.entity.Player;
import org.mineacademy.fo.command.SimpleCommand;
import org.ninenetwork.infinitedungeons.PlayerCache;

public class SetEditingRoomCommand extends SimpleCommand {

    public SetEditingRoomCommand() {
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
        }
    }

}