package org.ninenetwork.infinitedungeons.classes;

import org.bukkit.entity.Player;
import org.ninenetwork.infinitedungeons.PlayerCache;

public class Mage extends DungeonClassTemp {

    Mage() {
        super("Mage");
    }

    @Override
    public int getLevel(Player player) {
        return PlayerCache.from(player).getMageLevel();
    }

}