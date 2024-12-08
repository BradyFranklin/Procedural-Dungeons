package org.ninenetwork.infinitedungeons.dungeon;

import lombok.Getter;

import javax.annotation.Nullable;
import javax.xml.stream.Location;
import java.util.List;

@Getter
public class DungeonRoomPuzzle extends DungeonRoom {

    private DungeonRoom instance;

    private List<Location> trackedPoints;
    private String puzzleType;

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
