package org.ninenetwork.infinitedungeons.playerstats.damage;

import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.mcmonkey.sentinel.SentinelTrait;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.remain.CompMetadata;
import org.ninenetwork.infinitedungeons.PlayerCache;
import org.ninenetwork.infinitedungeons.dungeon.Dungeon;
import org.ninenetwork.infinitedungeons.item.AbstractDungeonItem;
import org.ninenetwork.infinitedungeons.item.CustomDungeonItemManager;
import org.ninenetwork.infinitedungeons.mob.DungeonMobManager;
import org.ninenetwork.infinitedungeons.mob.boss.DungeonBossManager;
import org.ninenetwork.infinitedungeons.playerstats.PlayerStat;
import org.ninenetwork.infinitedungeons.playerstats.PlayerStatSource;
import org.ninenetwork.infinitedungeons.playerstats.PlayerStatSourceDatabase;
import org.ninenetwork.infinitedungeons.playerstats.health.MobHealthManager;
import org.ninenetwork.infinitedungeons.util.CooldownManager;
import org.ninenetwork.infinitedungeons.util.GeneralUtils;
import org.ninenetwork.infinitedungeons.util.HologramUtil;

import java.util.Map;
import java.util.Random;

public class PlayerDamageHandler {

    public static double handleNormalDamage(Player player, LivingEntity entity, DamageType damageType) {
        if (player.getEquipment().getItemInMainHand() != null) {
            PlayerCache cache = PlayerCache.from(player);
            if (cache.hasDungeon()) {
                Dungeon dungeon = cache.getCurrentDungeon();
                if (CustomDungeonItemManager.getInstance().checkIsDungeonItem(player.getEquipment().getItemInMainHand())) {
                    Class<? extends AbstractDungeonItem> clazz = CustomDungeonItemManager.getInstance().getHandler(CustomDungeonItemManager.getInstance().findDungeonItem(player.getEquipment().getItemInMainHand()).itemID).getClass();
                    double damage = mainDamageSequence(damageType, player, entity, player.getEquipment().getItemInMainHand(), clazz);
                    //Common.tell(player, damage + " damage after handling");
                    applyDamageScaleHealth(player, entity, damage, dungeon);
                    return damage;
                }
                if (cache.getCurrentDungeonClass().equalsIgnoreCase("Berserk")) {
                    // Blood lust passive healing
                    double missingHealth = cache.getActiveMaxHealth() - cache.getActiveHealth();
                    if (missingHealth > 0) {
                        double additionalHealth = missingHealth * .03;
                        double newHealth = cache.getActiveHealth() + additionalHealth;
                        cache.setActiveHealth(newHealth);
                    }
                }
            }
        }
        return 0.0;
    }

    public static void handleNormalDamageNPC(Player player, NPC npc, DamageType damageType) {
        if (player.getEquipment().getItemInMainHand() != null) {
            PlayerCache cache = PlayerCache.from(player);
            if (cache.hasDungeon()) {
                Dungeon dungeon = cache.getCurrentDungeon();
                if (CustomDungeonItemManager.getInstance().checkIsDungeonItem(player.getEquipment().getItemInMainHand())) {
                    Class<? extends AbstractDungeonItem> clazz = CustomDungeonItemManager.getInstance().getHandler(CustomDungeonItemManager.getInstance().findDungeonItem(player.getEquipment().getItemInMainHand()).itemID).getClass();
                    double damage = mainDamageSequenceNPC(damageType, player, npc, player.getEquipment().getItemInMainHand(), clazz);
                    //Common.tell(player, damage + " npc damage after handling");
                    applyDamageScaleHealthNPC(player, npc, damage, dungeon);
                }
            }
        }
    }

    public static double mainDamageSequence(DamageType damageType, Player player, LivingEntity enemy, ItemStack item, Class<? extends AbstractDungeonItem> clazz) {
        double enemyDefense = getEnemyDefense(enemy);
        double postDefenseDamage = 0;
        postDefenseDamage = getPrimaryDamage(enemy, damageType, player, item, clazz) * (1 - enemyDefense / (enemyDefense + 100));
        return postDefenseDamage;
    }

    public static double mainDamageSequenceNPC(DamageType damageType, Player player, NPC npc, ItemStack item, Class<? extends AbstractDungeonItem> clazz) {
        double enemyDefense = getEnemyDefenseNPC(npc);
        double postDefenseDamage = 0;
        postDefenseDamage = getPrimaryDamage((LivingEntity) npc.getEntity(), damageType, player, item, clazz) * (1 - enemyDefense / (enemyDefense + 100));
        return postDefenseDamage;
    }

    private static double getPrimaryDamage(LivingEntity entity, DamageType damageType, Player player, ItemStack item, Class<? extends AbstractDungeonItem> clazz) {
        PlayerCache cache = PlayerCache.from(player);
        PlayerStatSourceDatabase manager = PlayerStatSourceDatabase.getInstance();
        Map<PlayerStat, Double> itemStats = manager.getAllValuesOfStatSource(player, PlayerStatSource.WEAPON);

        double baseDamage = itemStats.containsKey(PlayerStat.DAMAGE) ? itemStats.get(PlayerStat.DAMAGE) : 1;
        double strength = cache.getActiveStrength() > 0 ? cache.getActiveStrength() : 1.0;
        double critDamage = cache.getActiveCritDamage();
        double additiveMultiplier = getPlayerAdditive(player);
        double multiplicativeMultiplier = getPlayerMultiplicative(player);
        double bonusModifiers = getPlayerBonus(player);
        double finalDamage = baseDamage;

        if (CooldownManager.getInstance().checkReverseCooldown(player, "Bloodlust")) {
            double finalPercentage = .2 + (.04 * cache.getBerserkLevel() / 10);
            finalDamage += baseDamage * (1 + finalPercentage);
        }

        if (cache.getCurrentDungeonClass().equalsIgnoreCase("Berserk")) {
            int playerHits = MobDamagerDatabase.getInstance().checkPlayersHits(entity, player);
            double increase = ((.15 + (.006 * cache.getBerserkLevel())) * playerHits) + 1;
            finalDamage += baseDamage * increase;
            Common.tell(player, "Hits: " + playerHits);
        }

        MobDamagerDatabase.getInstance().addPlayerDamager(entity, player);

        if (damageType == DamageType.MELEE) {
            if (chooseIfCrit(player)) {
                return (5 + finalDamage) * (1 + strength / 100) * (1 + critDamage / 100) * additiveMultiplier * multiplicativeMultiplier + bonusModifiers;
            }
            return (5 + finalDamage) * (1 + strength / 100) * additiveMultiplier * multiplicativeMultiplier + bonusModifiers;
        } else if (damageType == DamageType.MAGIC) {
            CustomDungeonItemManager.getInstance().getHandler(clazz).abilityDamage();
            double baseAbilityDamage = CustomDungeonItemManager.getInstance().getHandler(clazz).abilityDamage();
            double abilityDamage = 1 + cache.getActiveAbilityDamage() / 100;
            multiplicativeMultiplier = multiplicativeMultiplier * abilityDamage;
            double intelligence = cache.getActiveMaxIntelligence();
            double abilityScaling = CustomDungeonItemManager.getInstance().getHandler(clazz).abilityScaling();
            return baseAbilityDamage * (1 + intelligence/100 * abilityScaling) * additiveMultiplier * multiplicativeMultiplier + bonusModifiers;
        } else if (damageType == DamageType.PROJECTILE) {
            if (chooseIfCrit(player)) {
                return (5 + finalDamage) * (1 + strength / 100) * (1 + critDamage / 100) * additiveMultiplier * multiplicativeMultiplier + bonusModifiers;
            }
            return (5 + finalDamage) * (1 + strength / 100) * additiveMultiplier * multiplicativeMultiplier + bonusModifiers;
        }
        return 0;
    }

    public static void applyDamageScaleHealth(Player player, LivingEntity entity, double damage, Dungeon dungeon) {
        double maxHealth = 1000;
        double currentHealth = 0;
        double maxDisplayedHealth = 0;
        double currentDisplayedHealth = 0;
        double displayedHealthAfterDamage = 0;
        double currentHealthAfterDamage = 0;
        if (entity.getHealth() >= 1000) {
            if (!CompMetadata.hasMetadata(entity, "Modifier")) {
                currentHealth = 1000;
            } else if (!CompMetadata.getMetadata(entity, "Modifier").equals("Healthy")) {
                currentHealth = 1000;
            } else {
                currentHealth = entity.getHealth();
            }
        } else {
            currentHealth = entity.getHealth();
        }
        if (DungeonMobManager.getInstance().checkIsDungeonMob(entity)) {
            maxDisplayedHealth = DungeonMobManager.getInstance().findDungeonMob(entity).getDungeonMobBaseHealth(dungeon);
        } else if (DungeonBossManager.getInstance().checkIsDungeonBoss(entity)) {
            maxDisplayedHealth = DungeonBossManager.getInstance().findDungeonBoss(entity).getBossHealth(dungeon);
        } else {
            return;
        }
        currentDisplayedHealth = scaleUp(currentHealth, maxHealth, maxDisplayedHealth);
        displayedHealthAfterDamage = currentDisplayedHealth - damage;
        currentHealthAfterDamage = scaleDown(displayedHealthAfterDamage, maxHealth, maxDisplayedHealth);
        boolean hasModifier = CompMetadata.hasMetadata(entity, "Modifier");
        String modifierName = "";
        if (displayedHealthAfterDamage <= 0) {
            if (hasModifier) {
                modifierName = CompMetadata.getMetadata(entity, "Modifier");
            }
            MobHealthManager.updateMobNametag(dungeon, entity, displayedHealthAfterDamage, modifierName);
            MobHealthManager.killMob(player, entity);
        } else {
            MobHealthManager.updateMobNametag(dungeon, entity, displayedHealthAfterDamage, modifierName);
            entity.setHealth(currentHealthAfterDamage);
        }
        if (Common.doesPluginExist("HolographicDisplays")) {
            HologramUtil.createHitHologram(player, entity, damage);
        }
        //Common.tell(player, "Damaged mob for " + GeneralUtils.formatNumber(damage) + " Health went from " + GeneralUtils.formatNumber(currentDisplayedHealth) + " to " + GeneralUtils.formatNumber(displayedHealthAfterDamage));
    }

    public static void applyDamageScaleHealthNPC(Player player, NPC npc, double damage, Dungeon dungeon) {
        double maxHealth = 1000;
        double currentHealth = 0;
        double maxDisplayedHealth = 0;
        double currentDisplayedHealth = 0;
        double displayedHealthAfterDamage = 0;
        double currentHealthAfterDamage = 0;
        SentinelTrait sent = npc.getOrAddTrait(SentinelTrait.class);
        if (sent.health >= 1000) {
            sent.setHealth(1000);
        } else {
            if (sent.health != ((LivingEntity)npc.getEntity()).getHealth()) {
                if (sent.health > ((LivingEntity)npc.getEntity()).getHealth()) {
                    currentHealth = sent.health;
                } else {
                    currentHealth = ((LivingEntity) npc.getEntity()).getHealth();
                }
            }
        }
        if (DungeonBossManager.getInstance().checkIsDungeonBoss(npc)) {
            Common.log("Passed check in scaled damage for npcs");
            maxDisplayedHealth = DungeonBossManager.getInstance().findDungeonBoss(npc).getBossHealth(dungeon);
        } else {
            return;
        }
        currentDisplayedHealth = scaleUp(currentHealth, maxHealth, maxDisplayedHealth);
        displayedHealthAfterDamage = currentDisplayedHealth - damage;
        currentHealthAfterDamage = scaleDown(displayedHealthAfterDamage, maxHealth, maxDisplayedHealth);
        if (displayedHealthAfterDamage <= 0) {
            MobHealthManager.updateNpcNametag(dungeon, npc, displayedHealthAfterDamage);
            MobHealthManager.killMob(player, (LivingEntity) npc.getEntity());
        } else {
            MobHealthManager.updateNpcNametag(dungeon, npc, displayedHealthAfterDamage);
            sent.setHealth(currentHealthAfterDamage);
            ((LivingEntity) npc.getEntity()).setHealth(currentHealthAfterDamage);
        }
        if (Common.doesPluginExist("HolographicDisplays")) {
            HologramUtil.createHitHologram(player, (LivingEntity) npc.getEntity(), damage);
        }
        Common.tell(player, "Damaged boss for " + GeneralUtils.formatNumber(damage) + " Health went from " + GeneralUtils.formatNumber(currentDisplayedHealth) + " to " + GeneralUtils.formatNumber(displayedHealthAfterDamage));
    }

    public static boolean chooseIfCrit(Player player) {
        double cc = PlayerCache.from(player).getActiveCritChance();
        Random rand = new Random();
        int choice = rand.nextInt(100);
        return choice <= (int) cc;
    }

    public static double scaleDown(double displayedHealth, double literalMaxHealth, double mobMaxHealth) {
        return displayedHealth * (literalMaxHealth / mobMaxHealth);
    }

    // Scale up actual health to displayed health
    public static double scaleUp(double actualHealth, double literalMaxHealth, double mobMaxHealth) {
        return actualHealth * (mobMaxHealth / literalMaxHealth);
    }

    private static double getPlayerAdditive(Player player) {
        //enchants
        double additives = 1;
        return additives;
    }

    private static double getPlayerMultiplicative(Player player) {
        double multiplicative = 1;
        return multiplicative;
    }

    private static double getPlayerBonus(Player player) {
        double bonus = 1;
        return bonus;
    }

    private static double getEnemyDefense(LivingEntity enemy) {
        double enemyDefense = 1;
        return enemyDefense;
    }

    private static double getEnemyDefenseNPC(NPC npc) {
        double npcDefense = 1;
        return npcDefense;
    }

    private static double getAbilityScaling(ItemStack item) {
        return 1;
    }

    private double maxBerserkBoost(PlayerCache cache, int floor) {
        return 2.5 + (.7 * (floor * ((double) cache.getBerserkLevel() / 5.0)));
    }

}