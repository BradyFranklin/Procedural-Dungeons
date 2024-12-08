package org.ninenetwork.infinitedungeons.classes;

import org.ninenetwork.infinitedungeons.playerstats.PlayerStat;

import java.util.Map;

public class DungeonClassAbility {

    private DungeonClassTemp classIntended;

    private Map<PlayerStat, Double> playerStatBonuses;

    public DungeonClassAbility(DungeonClassTemp classIntended) {
        this.classIntended = classIntended;
    }

}