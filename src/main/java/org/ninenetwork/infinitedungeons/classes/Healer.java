package org.ninenetwork.infinitedungeons.classes;

import org.bukkit.entity.Player;
import org.ninenetwork.infinitedungeons.PlayerCache;

public class Healer extends DungeonClassTemp {

    Healer() {
        super("Healer");
    }

    @Override
    public int getLevel(Player player) {
        return PlayerCache.from(player).getHealerLevel();
    }

}