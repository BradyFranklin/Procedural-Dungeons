package org.ninenetwork.infinitedungeons.playerstats;

import org.bukkit.entity.Player;
import org.ninenetwork.infinitedungeons.PlayerCache;

public class ManaManager {

    public static boolean useMana(Player player, double amount) {
        PlayerCache cache = PlayerCache.from(player);
        if (cache.getActiveIntelligence() >= amount) {
            cache.setActiveIntelligence(cache.getActiveIntelligence() - amount);
            return true;
        } else {
            return false;
        }
    }

    public static void manaChange(Player player) {
        PlayerCache cache = PlayerCache.from(player);
        if (cache.getActiveMaxIntelligence() < cache.getActiveIntelligence()) {
            cache.setActiveIntelligence(cache.getActiveMaxIntelligence());
        }
    }

}