package org.ninenetwork.infinitedungeons.playerstats.damage;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.ninenetwork.infinitedungeons.PlayerCache;
import org.ninenetwork.infinitedungeons.dungeon.Dungeon;
import org.ninenetwork.infinitedungeons.mob.AbstractDungeonEnemy;
import org.ninenetwork.infinitedungeons.mob.DungeonMobManager;
import org.ninenetwork.infinitedungeons.mob.boss.DungeonBoss;
import org.ninenetwork.infinitedungeons.mob.boss.DungeonBossManager;
import org.ninenetwork.infinitedungeons.playerstats.health.PlayerHealthHandler;
import org.ninenetwork.infinitedungeons.util.GeneralUtils;

public class MobDamageHandler {

    public static void handleNormalDamage(Player player, LivingEntity entity, DamageType damageType) {
        PlayerCache cache = PlayerCache.from(player);
        if (cache.hasDungeon()) {
            Dungeon dungeon = cache.getCurrentDungeon();
            double baseDamage = 0;
            double damageReduction = cache.getActiveDefense() / (cache.getActiveDefense() + 100.0);
            double endDamage = 0;
            Common.tell(player, "Damage reduction is " + GeneralUtils.formatNumber(damageReduction));
            if (DungeonMobManager.getInstance().checkIsDungeonMob(entity)) {
                AbstractDungeonEnemy mob = DungeonMobManager.getInstance().findDungeonMob(entity);
                baseDamage = mob.getDungeonMobBaseDamage(dungeon);
                endDamage = GeneralUtils.scatterNumber(baseDamage - (baseDamage * damageReduction));
            } else if (DungeonBossManager.getInstance().checkIsDungeonBoss(entity)) {
                DungeonBoss boss = DungeonBossManager.getInstance().findDungeonBoss(entity);
                baseDamage = boss.getDungeonBossBaseDamage(dungeon);
                endDamage = GeneralUtils.scatterNumber(baseDamage - (baseDamage * damageReduction));
            } else {
                endDamage = 1.0;
            }
            //applyDamageScaling(player, endDamage);
            scaleHealthEffectiveToDisplayed(player, endDamage);
        }
    }

    public static void scaleHealthEffectiveToDisplayed(Player player, double damage) {
        PlayerCache cache = PlayerCache.from(player);
        double effectiveHp = PlayerHealthHandler.getEffectiveHp(cache);
        if (damage <= effectiveHp) {
            double percentageOfEffective = damage / effectiveHp;
            double displayEquivalent = cache.getActiveMaxHealth() * percentageOfEffective;
            applyDamageScaling(player, displayEquivalent);
        } else {
            applyDamageScaling(player, cache.getActiveMaxHealth());
        }
    }

    public static void applyDamageScaling(Player player, double endDamage) {
        PlayerCache cache = PlayerCache.from(player);
        double maxDisplayedHealth = cache.getActiveMaxHealth();
        double displayedHealthAfterDamage = cache.getActiveHealth() - endDamage;
        Common.tell(player, "Mob damaged you for " + GeneralUtils.formatNumber(endDamage) + " and your health beforehand was " + GeneralUtils.formatNumber(cache.getActiveHealth()));
        if (displayedHealthAfterDamage <= 0) {
            playerDeath(cache.getCurrentDungeon(), player);
        } else {
            double currentHealthAfterDamage = PlayerHealthHandler.getPlayerHearts(displayedHealthAfterDamage, maxDisplayedHealth);
            cache.setActiveHealth(displayedHealthAfterDamage);
            player.setHealth(currentHealthAfterDamage);
        }
        PlayerHealthHandler.updatePlayersHealthPercentage(player);
    }

    public static void playerDeath(Dungeon dungeon, Player player) {
        Common.tell(player, "You died");
    }

}