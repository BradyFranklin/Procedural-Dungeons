package org.ninenetwork.infinitedungeons.util;

import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

public class DungeonPlayerUtil {

    double baseHealth = 1;
    double baseDefense;
    int baseSpeed;
    double baseStrength;
    double baseMana;
    int baseCritChance;
    double baseCritDamage;
    double baseAbilityDamage;
    double baseDungeonLuck;

    public static void dungeonInitializePlayerStats(Player player, World world) {

    }

    public static void setPlayerHealthScale(Player player) {
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(40);
        player.setHealth(40.0);
        player.setHealthScaled(true);
        player.setHealthScale(40.0);
    }

    public static void setPlayerHealthScaleRevert(Player player) {
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
        player.setHealth(20.0);
        player.setHealthScale(20.0);
        player.setHealthScaled(false);
    }

}