package org.ninenetwork.infinitedungeons.classes;

import org.bukkit.entity.Player;
import org.ninenetwork.infinitedungeons.PlayerCache;

public class Archer extends DungeonClassTemp {

    Archer() {
        super("Archer");
    }

    @Override
    public int getLevel(Player player) {
        return PlayerCache.from(player).getArcherLevel();
    }

}