package org.ninenetwork.infinitedungeons.dungeon;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.model.Countdown;
import org.mineacademy.fo.model.SimpleTime;
import org.ninenetwork.infinitedungeons.PlayerCache;

public class DungeonCountdownStart extends Countdown {

    @Getter
    private final Dungeon dungeon;

    protected DungeonCountdownStart(final Dungeon dungeon) {
        super(SimpleTime.from("10 seconds"));
        this.dungeon = dungeon;
    }

    @Override
    protected void onTick() {
        for (PlayerCache cache : this.dungeon.getPlayerCaches()) {
            if (!cache.isReady()) {
                this.dungeon.broadcastWarn(cache.toPlayer().getName() + " is no longer ready!");
                this.dungeon.checkBeginCountdown(this.dungeon, this.dungeon.getStartCountdown());
                this.cancel();
            }
        }
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