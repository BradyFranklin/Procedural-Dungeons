package org.ninenetwork.infinitedungeons.classes;

import org.bukkit.entity.Player;
import org.ninenetwork.infinitedungeons.PlayerCache;

public class Tank extends DungeonClassTemp {

    Tank() {
        super("Tank");
    }

    @Override
    public int getLevel(Player player) {
        return PlayerCache.from(player).getTankLevel();
    }

    private double protectiveBarrier(Player player, int defenseAddition) {
        // Increases defense 25-35%
        PlayerCache cache = PlayerCache.from(player);
        double defenseIncrease = 25 + (0.2 * cache.getTankLevel());
        return (defenseIncrease + 100) / 100 * defenseAddition;
    }

}