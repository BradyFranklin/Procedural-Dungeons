package org.ninenetwork.infinitedungeons.playerstats.health;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.ninenetwork.infinitedungeons.PlayerCache;
import org.ninenetwork.infinitedungeons.playerstats.PlayerStat;
import org.ninenetwork.infinitedungeons.playerstats.PlayerStatManager;

import java.util.HashMap;
import java.util.Map;

public class PlayerHealthHandler {

    public static void mainHealthSequence(Player player, double damage) {
        PlayerCache cache = PlayerCache.from(player);
        double currentHealth = cache.getActiveHealth();
        cache.setActiveHealth(((currentHealth - damage) < 0.0) ? 0.0 : (currentHealth - damage));
        if (cache.getActiveHealth() <= 0.0) {
            //player died, set spectate etc.
        } else {
            player.setHealth(getPlayerHearts(cache.getActiveHealth() - damage, cache.getActiveMaxHealth()));
        }
        //player.setHealth(getPlayerHearts(cache.getActiveHealth() - damage, cache.getActiveMaxHealth()));
    }

    //PlayerStatManager.getTotalOfSpecificStatsDungeonItems(player, Arrays.asList(PlayerStat.HEALTH, PlayerStat.DEFENSE, PlayerStat.TRUE_DEFENSE, PlayerStat.HEALTH_REGEN));

    public static double getEffectiveHp(PlayerCache cache) {
        return cache.getActiveHealth() * (1 + (cache.getActiveDefense()/100.0));
    }

    public static void updatePlayersHealthPercentage(Player player) {
        PlayerCache cache = PlayerCache.from(player);
        double playerHealthPercentage = cache.getActiveHealth() / cache.getActiveMaxHealth();
        cache.setLastStoredHealth(playerHealthPercentage);
    }

    public static void updatePlayerDungeonMaxHealth(Player player, boolean isStart) {
        PlayerCache cache = PlayerCache.from(player);
        Map<PlayerStat, Double> healthStats = new HashMap<>(PlayerStatManager.evaluateEquipmentStats(player, "none"));
        double currentHealthPercentage = (cache.getActiveHealth() / cache.getActiveMaxHealth()) * 100;
        double effectiveHP = (cache.getBaseHealth() + (healthStats.containsKey(PlayerStat.HEALTH) ? healthStats.get(PlayerStat.HEALTH) : 0)) * (1 + (cache.getBaseDefense() + (healthStats.containsKey(PlayerStat.DEFENSE) ? healthStats.get(PlayerStat.DEFENSE)/100 : 0)));
        cache.setActiveMaxHealth(effectiveHP);
        if (cache.getActiveHealth() <= 0) {
            cache.setActiveHealth(effectiveHP);
        } else {
            if (currentHealthPercentage != 0) {
                cache.setActiveHealth(effectiveHP * (currentHealthPercentage / 100));
            } else {
                cache.setActiveHealth(effectiveHP * 1);
            }
        }
        Common.tell(player, currentHealthPercentage + " health percentage, " + effectiveHP + " effective HP, " + getPlayerHearts(cache.getActiveHealth(), cache.getActiveMaxHealth()) + " player hearts");
        if (isStart) {
            player.setHealth(getPlayerHearts(effectiveHP, effectiveHP));
        } else {
            player.setHealth(getPlayerHearts(cache.getActiveHealth(), effectiveHP));
        }
    }

    public static void runHealthRegen(PlayerCache cache, double regenHealth) {
        double total = cache.getActiveHealth() + regenHealth;
        if (total > cache.getActiveMaxHealth()) {
            cache.setActiveHealth(cache.getActiveMaxHealth());
            cache.toPlayer().setHealth(getPlayerHearts(cache.getActiveHealth(), cache.getActiveMaxHealth()));
        } else {
            cache.setActiveHealth(total);
            cache.toPlayer().setHealth(getPlayerHearts(cache.getActiveHealth(), cache.getActiveMaxHealth()));
        }
        PlayerHealthHandler.updatePlayersHealthPercentage(cache.toPlayer());
    }

    public static void worldChangeHealthGenerator(Player player, int value) {
        player.setHealthScale(value);
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(value);
        player.setHealthScaled(value != 20);
        player.setHealth(value);
    }

    public static void runArmorEventChange(Player player) {
        PlayerCache cache = PlayerCache.from(player);
        updatePlayerDungeonMaxHealth(player, false);
    }

    public static double getPlayerHearts(double newHealth, double currentMaxHealth) {
        double percentage = (newHealth / currentMaxHealth) * 100.0;
        if (newHealth < 0.0) {
            return 0;
        } else if (percentage < 2.5) {
            return 0.5;
        } else {
            if (roundToHalf(percentage / 2.5) > 40) {
                return 40;
            }
            return roundToHalf(percentage / 2.5);
        }
    }

    public static double roundToHalf(double d) {
        return Math.round(d * 2) / 2.0;
    }

}