package org.ninenetwork.infinitedungeons.task;

import org.bukkit.scheduler.BukkitRunnable;
import org.ninenetwork.infinitedungeons.dungeon.Dungeon;

public class DungeonScoringTask extends BukkitRunnable {

    @Override
    public void run() {
        for (Dungeon dungeon : Dungeon.getDungeons()) {
            dungeon.getDungeonScore().recalculateDungeonScore();
        }
    }

}