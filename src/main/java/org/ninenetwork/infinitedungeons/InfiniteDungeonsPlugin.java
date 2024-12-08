package org.ninenetwork.infinitedungeons;

import org.bukkit.Bukkit;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.menu.MenuTools;
import org.mineacademy.fo.plugin.SimplePlugin;
import org.ninenetwork.infinitedungeons.command.*;
import org.ninenetwork.infinitedungeons.command.tests.*;
import org.ninenetwork.infinitedungeons.dungeon.Dungeon;
import org.ninenetwork.infinitedungeons.listener.DungeonListener;
import org.ninenetwork.infinitedungeons.listener.MobListeners;
import org.ninenetwork.infinitedungeons.listener.NPCListener;

public class InfiniteDungeonsPlugin extends SimplePlugin {

    @Override
    protected void onPluginStart() {

        Common.log("Infinite Dungeons Framework Initialized");

        registerCommand(new TestCommand());
        registerCommand(new TestTwoCommand());
        registerCommand(new TestThreeCommand());
        registerCommand(new ReloadCommand());
        registerCommand(new DungeonFinderCommand());
        registerCommand(new SetDungeonLevelCommand());
        registerCommand(new SetBlockCommand());
        registerCommand(new TestDungeonGeneration());
        registerCommand(new DungeonMobCommand());
        registerCommand(new DungeonMobNonNpc());
        registerCommand(new TestDungeonStart());
        registerCommand(new CreateRoomCommand());
        registerCommand(new TestDefaultRoomCreator());

        registerEvents(new DungeonListener());
        registerEvents(new NPCListener());
        registerEvents(new MobListeners());
        registerAllCommands(NMSCommand.class);

    }

    @Override
    protected void onReloadablesStart() {

        Common.log("Infinite Dungeons Framework Restarting");

    }

    @Override
    protected void onPluginStop() {

        Common.log("Infinite Dungeons Framework Shutting Down");

    }

    public static InfiniteDungeonsPlugin getInstance() {
        return getPlugin(InfiniteDungeonsPlugin.class);
    }

    // Finish Main Menu Extensions

}