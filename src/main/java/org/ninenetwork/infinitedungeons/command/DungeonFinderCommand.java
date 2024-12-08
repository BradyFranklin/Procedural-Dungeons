package org.ninenetwork.infinitedungeons.command;

import org.bukkit.entity.Player;
import org.mineacademy.fo.command.SimpleCommand;
import org.ninenetwork.infinitedungeons.menu.DungeonMainMenu;


public class DungeonFinderCommand extends SimpleCommand {

    public DungeonFinderCommand() {
        super("dungeons");
    }

    @Override
    protected void onCommand() {
        checkConsole();
        if (sender instanceof Player) {
            Player player = (Player) sender;
            new DungeonMainMenu(player).displayTo(player);
        }
    }

}