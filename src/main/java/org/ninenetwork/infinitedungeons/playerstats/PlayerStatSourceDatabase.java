package org.ninenetwork.infinitedungeons.playerstats;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.ninenetwork.infinitedungeons.PlayerCache;
import org.ninenetwork.infinitedungeons.dungeon.DungeonLeveling;

import java.util.*;

public class PlayerStatSourceDatabase {

    @Getter
    private static final PlayerStatSourceDatabase instance = new PlayerStatSourceDatabase();

    private Map<UUID,Map<PlayerStatSource,Map<PlayerStat,Double>>> statMap = new HashMap<>();

    public PlayerStatSourceDatabase() {

    }

    /*
    Add a specific value of a specific stat
    */
    private void addSingleStatToMapping(Player player, PlayerStatSource source, PlayerStat stat, double value) {
        UUID uuid = player.getUniqueId();
        if (this.statMap.containsKey(uuid)) {
            if (this.statMap.get(uuid).containsKey(source)) {
                if (this.statMap.get(uuid).get(source).containsKey(stat)) {
                    this.statMap.get(uuid).get(source).replace(stat, value);
                } else {
                    this.statMap.get(uuid).get(source).put(stat, value);
                }
            } else {
                Map<PlayerStat,Double> map = new HashMap<>();
                map.put(stat, value);
                this.statMap.get(uuid).put(source, map);
            }
        } else {
            Map<PlayerStat, Double> map = new HashMap<>();
            Map<PlayerStatSource, Map<PlayerStat, Double>> map1 = new HashMap<>();
            map.put(stat, value);
            map1.put(source, map);
            this.statMap.put(uuid, map1);
        }
    }

    /*
    Takes all values passed in from a specific stat source and either adds them into current values or clears then sets them.
    */
    public void addFullStatsFromSpecificSourceToMapping(Player player, PlayerStatSource source, Map<PlayerStat, Double> values, boolean addOnto) {
        UUID uuid = player.getUniqueId();
        List<PlayerStat> garbageCollector = new ArrayList<>();
        if (source == PlayerStatSource.WEAPON || source == PlayerStatSource.ARMOR_HELMET || source == PlayerStatSource.ARMOR_CHESTPLATE || source == PlayerStatSource.ARMOR_LEGGINGS || source == PlayerStatSource.ARMOR_BOOTS) {
            for (Map.Entry<PlayerStat, Double> entry : values.entrySet()) {
                entry.setValue(entry.getValue() * ((100 + DungeonLeveling.getPlayerLevelBaseBoost(player)) / 100));
            }
        }
        if (this.statMap.containsKey(uuid)) {
            if (this.statMap.get(uuid).containsKey(source)) {
                if (this.statMap.get(uuid).get(source).isEmpty()) {
                    this.statMap.get(uuid).get(source).putAll(values);
                } else {
                    if (addOnto) {
                        for (Map.Entry<PlayerStat, Double> entry : this.statMap.get(uuid).get(source).entrySet()) {
                            if (values.containsKey(entry.getKey())) {
                                this.statMap.get(uuid).get(source).replace(entry.getKey(), entry.getValue() + values.get(entry.getKey()));
                                garbageCollector.add(entry.getKey());
                            }
                        }
                        for (PlayerStat stat : garbageCollector) {
                            values.remove(stat);
                        }
                        this.statMap.get(uuid).get(source).putAll(values);
                    } else {
                        this.statMap.get(uuid).get(source).clear();
                        this.statMap.get(uuid).get(source).putAll(values);
                    }
                }
            } else {
                this.statMap.get(uuid).put(source, values);
            }
        } else {
            Map<PlayerStatSource, Map<PlayerStat, Double>> map1 = new HashMap<>();
            map1.put(source, values);
            this.statMap.put(uuid, map1);
        }
        PlayerCache.from(player).setStatMapper(mergeAllStatsAllSourcesToMap(player));
        //updateAllPlayerValues(player);
    }

    public void addBlessingValues(Player player, Map<PlayerStat, Double> values) {
        UUID uuid = player.getUniqueId();
        if (this.statMap.containsKey(uuid)) {
            if (this.statMap.get(uuid).containsKey(PlayerStatSource.BLESSING)) {
                for (Map.Entry<PlayerStat, Double> entry : values.entrySet()) {
                    if (this.statMap.get(uuid).get(PlayerStatSource.BLESSING).containsKey(entry.getKey())) {
                        this.statMap.get(uuid).get(PlayerStatSource.BLESSING).replace(entry.getKey(), values.get(entry.getKey()));
                    } else {
                        this.statMap.get(uuid).get(PlayerStatSource.BLESSING).put(entry.getKey(), entry.getValue());
                    }
                }
            } else {
                this.statMap.get(uuid).put(PlayerStatSource.BLESSING, values);
            }
        } else {
            Map<PlayerStatSource, Map<PlayerStat, Double>> map = new HashMap<>();
            map.put(PlayerStatSource.BLESSING, values);
            this.statMap.put(uuid, map);
        }
        PlayerCache.from(player).setStatMapper(mergeAllStatsAllSourcesToMap(player));
    }

    /*
    Remove a specific stat from a specific source
    */
    public void removeStatFromSource(Player player, PlayerStatSource source, PlayerStat stat) {
        UUID uuid = player.getUniqueId();
        if (this.statMap.containsKey(uuid) && this.statMap.get(uuid).containsKey(source) && this.statMap.get(uuid).get(source).containsKey(stat)) {
            this.statMap.get(uuid).get(source).remove(stat);
        }
    }

    public void removeAllStatsFromSource(Player player, PlayerStatSource source) {
        UUID uuid = player.getUniqueId();
        if (this.statMap.containsKey(uuid)) {
            if (this.statMap.get(uuid).containsKey(source)) {
                this.statMap.get(uuid).get(source).clear();
            }
        }
        PlayerCache.from(player).setStatMapper(mergeAllStatsAllSourcesToMap(player));
        PlayerStatManager.setAllPlayerStats(player);
    }

    //////////////////////////////////////////////////////////////////////////////////////////

    private void updateAllPlayerValues(Player player) {
        Map<PlayerStat, Double> statMap = PlayerCache.from(player).getStatMapper();
        for (Map.Entry<PlayerStat, Double> entry : statMap.entrySet()) {
            PlayerStatManager.resetUpdatePlayerSpecificStat(player, entry.getKey(), entry.getValue());
        }
    }

    private Map<PlayerStat, Double> mergeAllStatsAllSourcesToMap(Player player) {
        UUID uuid = player.getUniqueId();
        Map<PlayerStat, Double> allStats = new HashMap<>();
        for (Map.Entry<PlayerStatSource, Map<PlayerStat, Double>> entry : this.statMap.get(uuid).entrySet()) {
            for (Map.Entry<PlayerStat, Double> ent : this.statMap.get(uuid).get(entry.getKey()).entrySet()) {
                if (!allStats.containsKey(ent.getKey())) {
                    allStats.put(ent.getKey(), ent.getValue());
                } else {
                    allStats.replace(ent.getKey(), allStats.get(ent.getKey()) + ent.getValue());
                }
            }
        }
        return allStats;
    }

    public double statTotalWithExclusions(Player player, PlayerStat stat, List<PlayerStatSource> exclusions) {
        UUID uuid = player.getUniqueId();
        double statValue = 0;
        for (Map.Entry<PlayerStatSource, Map<PlayerStat, Double>> entry : this.statMap.get(uuid).entrySet()) {
            if (!exclusions.contains(entry.getKey())) {
                for (Map.Entry<PlayerStat, Double> ent : this.statMap.get(uuid).get(entry.getKey()).entrySet()) {
                    if (ent.getKey() == stat) {
                        statValue += ent.getValue();
                    }
                }
            }
        }
        return statValue;
    }

    //////////////////////////////////////////////////////////////////////////////////////////

    public void clearStatValue(Player player, PlayerStatSource source, PlayerStat stat) {
        UUID uuid = player.getUniqueId();
        if (this.statMap.containsKey(uuid) && this.statMap.get(uuid).containsKey(source) && this.statMap.get(uuid).get(source).containsKey(stat)) {
            this.statMap.get(uuid).get(source).clear();
        }
    }

    public void clearAllStatValuesByPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        this.statMap.get(uuid).clear();
    }

    public double getAllValuesOfStatType(Player player, PlayerStat stat) {
        double totalStat = 0.0;
        UUID uuid = player.getUniqueId();
        for (Map.Entry<PlayerStatSource, Map<PlayerStat, Double>> entry : this.statMap.get(uuid).entrySet()) {
            if (entry.getValue().containsKey(stat)) {
                totalStat += entry.getValue().get(stat);
            }
        }
        return totalStat;
    }

    public Map<PlayerStat, Double> getAllValuesOfStatSource(Player player, PlayerStatSource source) {
        UUID uuid = player.getUniqueId();
        if (this.statMap.containsKey(uuid)) {
            if (this.statMap.get(uuid).containsKey(source)) {
                return this.statMap.get(uuid).get(source);
            }
        }
        return new HashMap<>();
    }

    public Map<PlayerStat, Double> getAllValuesOfAllSources(Player player) {
        UUID uuid = player.getUniqueId();
        Map<PlayerStat, Double> allData = new HashMap<>();
        for (Map.Entry<PlayerStatSource, Map<PlayerStat, Double>> entry : this.statMap.get(uuid).entrySet()) {
            for (Map.Entry<PlayerStat, Double> ent : this.statMap.get(uuid).get(entry.getKey()).entrySet()) {
                if (allData.containsKey(ent.getKey())) {
                    allData.replace(ent.getKey(), allData.get(ent.getKey()) + ent.getValue());
                } else {
                    allData.put(ent.getKey(), ent.getValue());
                }
            }
        }
        return allData;
    }

}