package org.ninenetwork.infinitedungeons.command;

import org.mineacademy.fo.Common;
import org.mineacademy.fo.command.SimpleSubCommand;
import org.ninenetwork.infinitedungeons.PlayerCache;
import org.ninenetwork.infinitedungeons.dungeon.Dungeon;

abstract class DungeonSubCommand extends SimpleSubCommand {

    protected DungeonSubCommand(String sublabel) {
        super(sublabel);
    }

    protected final PlayerCache getCache() {
        return isPlayer() ? PlayerCache.from(getPlayer()) : null;
    }

    protected final Dungeon findDungeonFromLocationOrFirstArg() {
        Dungeon dungeon;
        if (this.args.length > 0)
            dungeon = this.findDungeon(this.joinArgs(0));
        else {
            dungeon = Dungeon.findByLocation(getPlayer().getLocation());
            this.checkNotNull(dungeon, "Unable to locate a dungeon. Type a dungeon name. Available: " + Common.join(Dungeon.getDungeonNames()));
        }
        return dungeon;
    }

    protected final Dungeon findDungeon(String name) {
        Dungeon dungeon = Dungeon.findByName(name);
        this.checkNotNull(dungeon, "No such dungeon: '" + name + "'. Available: " + Common.join(Dungeon.getDungeonNames()));
        return dungeon;
    }

    protected final void checkDungeonExists(String dungeonName) {
        this.checkBoolean(Dungeon.isDungeonLoaded(dungeonName),
                "No such dungeon: '" + dungeonName + "'. Available: " + Common.join(Dungeon.getDungeonNames()));
    }
}