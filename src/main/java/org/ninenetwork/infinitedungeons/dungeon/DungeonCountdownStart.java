package org.ninenetwork.infinitedungeons.dungeon;

import lombok.Getter;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.model.Countdown;

public class DungeonCountdownStart extends Countdown {

    @Getter
    private final Dungeon dungeon;

    protected DungeonCountdownStart(final Dungeon dungeon) {
        super(dungeon.getLobbyDuration());
        this.dungeon = dungeon;
    }

    @Override
    protected void onTick() {
        // Broadcast every fifth second or every second when there are 5 or less seconds left
        if (this.getTimeLeft() <= 5 || this.getTimeLeft() % 10 == 0)
            this.dungeon.broadcastWarn("Dungeon starts in less than " + Common.plural(this.getTimeLeft(), "second"));
    }

    @Override
    protected void onTickError(final Throwable t) {
        this.dungeon.stop(DungeonStopReason.ERROR);
    }

    @Override
    protected void onEnd() {
        this.dungeon.start();
    }

}