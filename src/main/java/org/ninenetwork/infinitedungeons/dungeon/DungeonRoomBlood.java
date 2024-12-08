package org.ninenetwork.infinitedungeons.dungeon;

import lombok.Getter;

import javax.annotation.Nullable;

@Getter
public class DungeonRoomBlood extends DungeonRoom {

    private DungeonRoom instance;

    protected DungeonRoomBlood(String name) {
        super(name);
    }

    protected DungeonRoomBlood(String name, @Nullable DungeonRoomType type) {
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
