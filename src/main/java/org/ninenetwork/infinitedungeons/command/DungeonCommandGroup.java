package org.ninenetwork.infinitedungeons.command;

import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.command.DebugCommand;
import org.mineacademy.fo.command.ReloadCommand;
import org.mineacademy.fo.command.SimpleCommandGroup;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AutoRegister
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DungeonCommandGroup extends SimpleCommandGroup {

    @Getter(value = AccessLevel.PRIVATE)
    private static final DungeonCommandGroup instance = new DungeonCommandGroup();

    @Override
    protected String getCredits() {
        return "Brought to you by Infinite Dungeons";
    }

    /**
     * @see SimpleCommandGroup#registerSubcommands()
     */
    @Override
    protected void registerSubcommands() {

        // Register all /game sub commands automatically
        registerSubcommand(DungeonSubCommand.class);

        // Register the premade commands from Foundation
        registerSubcommand(new DebugCommand("dungeon.command.debug"));
        registerSubcommand(new ReloadCommand("dungeon.command.reload"));
    }
}