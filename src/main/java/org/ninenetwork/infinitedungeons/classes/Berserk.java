package org.ninenetwork.infinitedungeons.classes;

import org.bukkit.entity.Player;
import org.ninenetwork.infinitedungeons.PlayerCache;

public class Berserk extends DungeonClassTemp {

    Berserk() {
        super("Berserk");
    }

    @Override
    public int getLevel(Player player) {
        return PlayerCache.from(player).getBerserkLevel();
    }

}