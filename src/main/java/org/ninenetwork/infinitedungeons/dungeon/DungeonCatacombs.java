package org.ninenetwork.infinitedungeons.dungeon;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public class DungeonCatacombs extends Dungeon {

    protected DungeonCatacombs(String name) {
        super(name);
    }

    protected DungeonCatacombs(String name, @Nullable DungeonType type) {
        super(name, type);
    }

    @Override
    protected void onLoad() {
        super.onLoad();
    }

    @Override
    protected void onSave() {
        super.onSave();
    }

    @Override
    public Location getRespawnLocation(Player player) {
        return null;
    }

}