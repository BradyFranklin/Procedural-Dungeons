package org.ninenetwork.infinitedungeons.dungeon;

import lombok.Getter;
import org.mineacademy.fo.model.Countdown;
import org.mineacademy.fo.model.SimpleTime;

public class DungeonTimeTracker extends Countdown {

    @Getter
    private final Dungeon dungeon;

    protected DungeonTimeTracker(final Dungeon dungeon) {
        super(SimpleTime.from("30 minutes"));
        this.dungeon = dungeon;
    }

    @Override
    protected void onTick() {

    }

    @Override
    protected void onTickError(final Throwable t) {
        this.dungeon.stop(DungeonStopReason.ERROR);
    }

    @Override
    protected void onEnd() {
        this.dungeon.stop(DungeonStopReason.TIMEOUT);
    }

}