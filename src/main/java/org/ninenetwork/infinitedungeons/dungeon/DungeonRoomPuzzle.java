package org.ninenetwork.infinitedungeons.dungeon;

import lombok.Getter;

import javax.annotation.Nullable;

@Getter
public class DungeonRoomPuzzle extends DungeonRoom {

    private DungeonRoom instance;

    protected DungeonRoomPuzzle(String name) {
        super(name);
    }

    protected DungeonRoomPuzzle(String name, @Nullable DungeonRoomType type) {
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

}
