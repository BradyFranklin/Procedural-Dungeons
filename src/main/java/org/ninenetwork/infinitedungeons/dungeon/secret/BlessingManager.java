package org.ninenetwork.infinitedungeons.dungeon.secret;

import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.ninenetwork.infinitedungeons.PlayerCache;
import org.ninenetwork.infinitedungeons.dungeon.Dungeon;
import org.ninenetwork.infinitedungeons.playerstats.PlayerStat;
import org.ninenetwork.infinitedungeons.playerstats.PlayerStatManager;
import org.ninenetwork.infinitedungeons.playerstats.PlayerStatSource;
import org.ninenetwork.infinitedungeons.playerstats.PlayerStatSourceDatabase;
import org.ninenetwork.infinitedungeons.util.GeneralUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlessingManager {

    private Dungeon dungeon;
    private List<Player> playersReceive;
    private int lifeBlessing;
    private int powerBlessing;
    private int stoneBlessing;
    private int wisdomBlessing;

    public BlessingManager(Dungeon dungeon) {
        this.dungeon = dungeon;
        this.playersReceive = dungeon.getDungeonParty().getPlayers();
    }

    public void applyBlessing(BlessingType type, int level) {
        if (type == BlessingType.LIFE) {
            applyLifeBlessing(level);
        } else if (type == BlessingType.POWER) {
            applyPowerBlessing(level);
        } else if (type == BlessingType.STONE) {
            applyStoneBlessing(level);
        } else if (type == BlessingType.WISDOM) {
            applyWisdomBlessing(level);
        }
        /*
        switch(type) {
            case LIFE: applyLifeBlessing(level);
            case POWER: applyPowerBlessing(level);
            case STONE: applyStoneBlessing(level);
            case WISDOM: applyWisdomBlessing(level);
        }

         */
    }

    public void applyLifeBlessing(int level) {
        this.lifeBlessing += level;
        for (Player player : this.playersReceive) {
            PlayerCache cache = PlayerCache.from(player);
            double percentageIncrease = getPercentageIncrease(cache, this.lifeBlessing, 3.0);
            double flatIncrease = 0.0;
            double totalHealth = PlayerStatSourceDatabase.getInstance().statTotalWithExclusions(player, PlayerStat.HEALTH, Collections.singletonList(PlayerStatSource.BLESSING));
            double totalHealthRegen = PlayerStatSourceDatabase.getInstance().statTotalWithExclusions(player, PlayerStat.HEALTH_REGEN, Collections.singletonList(PlayerStatSource.BLESSING));
            double healthStatPostBlessing = (totalHealth + flatIncrease) * (1 + percentageIncrease);
            double healthRegenStatPostBlessing = (totalHealthRegen + flatIncrease) * (1 + percentageIncrease);
            Map<PlayerStat, Double> map = new HashMap<>();
            map.put(PlayerStat.HEALTH, GeneralUtils.round(healthStatPostBlessing - totalHealth, 2));
            map.put(PlayerStat.HEALTH_REGEN, GeneralUtils.round(healthRegenStatPostBlessing - totalHealthRegen, 2));
            PlayerStatSourceDatabase.getInstance().addBlessingValues(player, map);
            PlayerStatManager.setAllPlayerStats(player);
            Common.tell(player, "Health Blessing increased: " + GeneralUtils.round((healthStatPostBlessing - totalHealth), 2));
            Common.tell(player, "Health Regen Blessing increased: " + GeneralUtils.round((healthRegenStatPostBlessing - totalHealthRegen), 2));
        }
    }

    public void applyPowerBlessing(int level) {
        this.powerBlessing += level;
        for (Player player : this.playersReceive) {
            PlayerCache cache = PlayerCache.from(player);
            double percentageIncrease = getPercentageIncrease(cache, this.powerBlessing, 2.0);
            double flatIncrease = getFlatIncrease(cache, this.powerBlessing, 4.0);
            double totalStrength = PlayerStatSourceDatabase.getInstance().statTotalWithExclusions(player, PlayerStat.STRENGTH, Collections.singletonList(PlayerStatSource.BLESSING));
            double totalCritDamage = PlayerStatSourceDatabase.getInstance().statTotalWithExclusions(player, PlayerStat.CRIT_DAMAGE, Collections.singletonList(PlayerStatSource.BLESSING));
            double strengthStatPostBlessing = (totalStrength + flatIncrease) * (1 + percentageIncrease);
            double critDamageStatPostBlessing = (totalCritDamage + flatIncrease) * (1 + percentageIncrease);
            Map<PlayerStat, Double> map = new HashMap<>();
            map.put(PlayerStat.STRENGTH, GeneralUtils.round(strengthStatPostBlessing - totalStrength, 2));
            map.put(PlayerStat.CRIT_DAMAGE, GeneralUtils.round(critDamageStatPostBlessing - totalCritDamage, 2));
            PlayerStatSourceDatabase.getInstance().addBlessingValues(player, map);
            PlayerStatManager.setAllPlayerStats(player);
            Common.tell(player, "Strength Blessing increased: " + GeneralUtils.round((strengthStatPostBlessing - totalStrength), 2));
            Common.tell(player, "Crit Damage Blessing increased: " + GeneralUtils.round((critDamageStatPostBlessing - totalCritDamage), 2));
        }
    }

    public void applyStoneBlessing(int level) {
        this.stoneBlessing += level;
        for (Player player : this.playersReceive) {
            PlayerCache cache = PlayerCache.from(player);
            double percentageIncrease = getPercentageIncrease(cache, this.stoneBlessing, 2.0);
            double defenseFlatIncrease = getFlatIncrease(cache, this.stoneBlessing, 4.0);
            double damageFlatIncrease = getFlatIncrease(cache, this.stoneBlessing, 6.0);
            double totalDefense = PlayerStatSourceDatabase.getInstance().statTotalWithExclusions(player, PlayerStat.DEFENSE, Collections.singletonList(PlayerStatSource.BLESSING));
            double totalDamage = PlayerStatSourceDatabase.getInstance().statTotalWithExclusions(player, PlayerStat.DAMAGE, Collections.singletonList(PlayerStatSource.BLESSING));
            double defenseStatPostBlessing = (totalDefense + defenseFlatIncrease) * (1 + percentageIncrease);
            double damageStatPostBlessing = (totalDamage + damageFlatIncrease);
            Map<PlayerStat, Double> map = new HashMap<>();
            map.put(PlayerStat.DEFENSE, GeneralUtils.round(defenseStatPostBlessing - totalDefense, 2));
            map.put(PlayerStat.DAMAGE, GeneralUtils.round(damageStatPostBlessing - totalDamage, 2));
            PlayerStatSourceDatabase.getInstance().addBlessingValues(player, map);
            PlayerStatManager.setAllPlayerStats(player);
            Common.tell(player, "Defense Blessing increased: " + GeneralUtils.round((defenseStatPostBlessing - totalDefense), 2));
            Common.tell(player, "Damage Blessing increased: " + GeneralUtils.round((damageStatPostBlessing - totalDamage), 2));
        }
    }

    public void applyWisdomBlessing(int level) {
        this.wisdomBlessing += level;
        for (Player player : this.playersReceive) {
            PlayerCache cache = PlayerCache.from(player);
            double percentageIncrease = getPercentageIncrease(cache, this.wisdomBlessing, 2.0);
            double flatIncrease = getFlatIncrease(cache, this.wisdomBlessing, 4.0);
            double totalIntelligence = PlayerStatSourceDatabase.getInstance().statTotalWithExclusions(player, PlayerStat.INTELLIGENCE, Collections.singletonList(PlayerStatSource.BLESSING));
            double totalSpeed = PlayerStatSourceDatabase.getInstance().statTotalWithExclusions(player, PlayerStat.SPEED, Collections.singletonList(PlayerStatSource.BLESSING));
            double intelligenceStatPostBlessing = (totalIntelligence + flatIncrease) * (1 + percentageIncrease);
            double speedStatPostBlessing = (totalSpeed + flatIncrease);
            Map<PlayerStat, Double> map = new HashMap<>();
            map.put(PlayerStat.INTELLIGENCE, GeneralUtils.round(intelligenceStatPostBlessing - totalIntelligence, 2));
            map.put(PlayerStat.SPEED, GeneralUtils.round(speedStatPostBlessing - totalSpeed, 2));
            PlayerStatSourceDatabase.getInstance().addBlessingValues(player, map);
            PlayerStatManager.setAllPlayerStats(player);
            Common.tell(player, "Intelligence Blessing increased: " + GeneralUtils.round((intelligenceStatPostBlessing - totalIntelligence), 2));
            Common.tell(player, "Speed Blessing increased: " + GeneralUtils.round((speedStatPostBlessing - totalSpeed), 2));
        }
    }

    public double getPercentageIncrease(PlayerCache cache, int buffLevel, double percentage) {
        double aboveF3 = this.dungeon.getFloor() >= 3 ? 1.2 : 1.0;
        double forbiddenBlessing = 1 + ((double) cache.getForbiddenBlessing() / 100.0);
        double percentageIncrease = forbiddenBlessing * aboveF3 * (buffLevel * percentage / 100.0);
        return percentageIncrease;
    }

    public double getFlatIncrease(PlayerCache cache, int buffLevel, double increase) {
        double aboveF3 = this.dungeon.getFloor() >= 3 ? 1.2 : 1.0;
        double forbiddenBlessing = 1 + ((double) cache.getForbiddenBlessing() / 100.0);
        double flatIncrease = forbiddenBlessing * aboveF3 * (buffLevel * increase);
        return flatIncrease;
    }

    public int getBlessingAmount(BlessingType type) {
        switch(type) {
            case LIFE: return this.lifeBlessing;
            case POWER: return this.powerBlessing;
            case STONE: return this.stoneBlessing;
            case WISDOM: return this.wisdomBlessing;
        }
        return 0;
    }

}