package org.ninenetwork.infinitedungeons.classes;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.mineacademy.fo.Common;
import org.ninenetwork.infinitedungeons.InfiniteDungeonsPlugin;
import org.ninenetwork.infinitedungeons.PlayerCache;
import org.ninenetwork.infinitedungeons.dungeon.Dungeon;
import org.ninenetwork.infinitedungeons.mob.DungeonMobManager;
import org.ninenetwork.infinitedungeons.mob.boss.DungeonBossManager;
import org.ninenetwork.infinitedungeons.playerstats.damage.DamageType;
import org.ninenetwork.infinitedungeons.playerstats.damage.PlayerDamageHandler;
import org.ninenetwork.infinitedungeons.util.CooldownManager;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class DungeonClassUtil {

    public static void activatePassiveStats(Dungeon dungeon) {
        for (Player player : dungeon.getDungeonParty().getPlayers()) {
            PlayerCache cache = PlayerCache.from(player);
            if (cache.getCurrentDungeonClass().equalsIgnoreCase("Berserk")) {
                double percentageGain = .05 + (.1 * cache.getBerserkLevel());
                cache.setActiveDefense(cache.getActiveDefense() + (cache.getActiveStrength() * (1 + percentageGain)));
            }
        }
    }

    // PASSIVES //

    // Berserk - 20-40% increased dmg 5 seconds. 3% missing health heal every melee hit
    public static void performBloodlust() {

    }


    // ULTIMATES //

    public static void performMageUlt(Player player) {
        Random rand = new Random();
        UUID uuid = player.getUniqueId();
        CooldownManager manager = CooldownManager.getInstance();
        Duration timeLeft = manager.getRemainingCooldown(uuid, "Thunderstorm");
        if (timeLeft.isZero() || timeLeft.isNegative()) {
            manager.setCooldown(uuid, "Thunderstorm", Duration.ofSeconds(500L));
            List<Entity> entities = player.getNearbyEntities(10.0, 10.0, 10.0);
            for (Entity entity : entities) {
                if (entity instanceof LivingEntity) {
                    if (DungeonMobManager.getInstance().checkIsDungeonMob((LivingEntity) entity)) {
                        PlayerDamageHandler.handleNormalDamage(player, (LivingEntity) entity, DamageType.ABILITY);
                    } else if (DungeonBossManager.getInstance().checkIsDungeonBoss((LivingEntity) entity)) {

                    }
                }
            }
            new BukkitRunnable() {
                int iterations = 0;
                @Override
                public void run() {
                    iterations += 1;
                    if (iterations >= 15) {
                        cancel();
                    } else {
                        Entity entity = entities.get(rand.nextInt(entities.size()));
                        entity.getWorld().strikeLightning(entity.getLocation());
                    }
                }
            }.runTaskTimer(InfiniteDungeonsPlugin.getInstance(), 0L, 20L);

        } else {
            Common.tell(player, "Your ultimate ability is still on cooldown: " + timeLeft);
        }
    }

    public static void performBerserkUlt(Player player) {
        PlayerCache cache = PlayerCache.from(player);
        UUID uuid = player.getUniqueId();
        CooldownManager manager = CooldownManager.getInstance();
        Duration timeLeft = manager.getRemainingCooldown(uuid, "Ragnarok");
        if (timeLeft.isZero() || timeLeft.isNegative()) {
            manager.setCooldown(uuid, "Ragnarok", Duration.ofSeconds(500L));
            double bonusAttack = 100;
            double speed = 400;
            double damageBoost = cache.getActiveDamage() * 1.5;
            cache.setActiveAttackSpeed(cache.getActiveAttackSpeed() + bonusAttack);
            cache.setActiveSpeed(cache.getActiveSpeed() + speed);
            cache.setActiveDamage(cache.getActiveDamage() + damageBoost);
            new BukkitRunnable() {
                @Override
                public void run() {
                    normalizeBerserkUlt(player, bonusAttack, speed, damageBoost);
                }
            }.runTaskLater(InfiniteDungeonsPlugin.getInstance(), 300L);
        } else {
            Common.tell(player, "Your ultimate ability is still on cooldown: " + timeLeft);
        }
    }

    public static void normalizeBerserkUlt(Player player, double bonusAttack, double speed, double damageBoost) {
        PlayerCache cache = PlayerCache.from(player);
        cache.setActiveAttackSpeed(cache.getActiveAttackSpeed() - bonusAttack);
        cache.setActiveSpeed(cache.getActiveSpeed() - speed);
        cache.setActiveDamage(cache.getActiveDamage() - damageBoost);
    }

    public static double getMageCooldown(Player player, double baseCooldown) {
        PlayerCache cache = PlayerCache.from(player);
        double cooldownReduction = 25.0 + ((double) cache.getMageLevel() / 2);
        double endNumber = cooldownReduction / 100.0;
        return baseCooldown - (baseCooldown * endNumber);
    }

    public static double getBerserkMeleeBoost(Player player) {
        PlayerCache cache = PlayerCache.from(player);
        // .40 for dupe .80 for non dupe;
        return .4 + (.04 * cache.getBerserkLevel());
    }

}