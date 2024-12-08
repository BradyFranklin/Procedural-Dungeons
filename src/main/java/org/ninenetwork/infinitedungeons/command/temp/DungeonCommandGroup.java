package org.ninenetwork.infinitedungeons.command.temp;

import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.command.ReloadCommand;
import org.mineacademy.fo.command.SimpleCommandGroup;

public class DungeonCommandGroup extends SimpleCommandGroup {

    public DungeonCommandGroup() {
        super("channel/ch");
    }

    @Override
    protected void registerSubcommands() {
        registerSubcommand(new ReloadCommand());
    }

}