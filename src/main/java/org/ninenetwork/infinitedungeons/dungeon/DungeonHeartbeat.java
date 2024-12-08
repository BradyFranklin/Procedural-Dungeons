package org.ninenetwork.infinitedungeons.dungeon;

import lombok.Getter;
import org.mineacademy.fo.TimeUtil;
import org.mineacademy.fo.model.Countdown;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;

public class DungeonHeartbeat extends Countdown {

    @Getter
    private final Dungeon dungeon;

    public DungeonHeartbeat(final Dungeon dungeon) {
        super(dungeon.getGameDuration());
        this.dungeon = dungeon;
    }

    @Override
    protected void onStart() {

    }

    @Override
    protected void onTick() {
        final List<Integer> broadcastTimes = Arrays.asList(20, 30, 60);

        // % == the remaining amount after a division
        // 120 / 120 = 1 . 00
        // 240 / 120 = 2 . 00
        // 239 % 120 = 1 . 99 === 99

        // Broadcast every 2 minutes, when there is less than 10 seconds away and on 20th, 30th and 60th second to end away
        if (this.getTimeLeft() % 120 == 0 || this.getTimeLeft() <= 10 || broadcastTimes.contains(this.getTimeLeft()))
            this.dungeon.broadcastWarn("Game ends in less than " + TimeUtil.formatTimeGeneric(this.getTimeLeft()) + ".");
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