package org.ninenetwork.infinitedungeons.playerstats.damage;

import lombok.Getter;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.ninenetwork.infinitedungeons.mob.DungeonMobManager;

import java.util.HashMap;
import java.util.Map;

public class MobDamagerDatabase {

    @Getter
    public static final MobDamagerDatabase instance = new MobDamagerDatabase();

    private Map<LivingEntity,Map<Player,Integer>> damageMap = new HashMap<>();

    public MobDamagerDatabase() {

    }

    public void addMobToDamageMap(LivingEntity entity) {
        if (!this.damageMap.containsKey(entity)) {
            Map<Player,Integer> map = new HashMap<>();
            this.damageMap.put(entity, map);
        }
    }

    public void addPlayerDamager(LivingEntity entity, Player player) {
        if (DungeonMobManager.getInstance().checkIsDungeonMob(entity)) {
            if (this.damageMap.containsKey(entity)) {
                if (this.damageMap.get(entity).containsKey(player)) {
                    this.damageMap.get(entity).replace(player, this.damageMap.get(entity).get(player) + 1);
                } else {
                    this.damageMap.get(entity).put(player, 1);
                }
            } else {
                Map<Player, Integer> map = new HashMap<>();
                map.put(player, 1);
                this.damageMap.put(entity, map);
            }
        }
    }

    public int checkPlayersHits(LivingEntity entity, Player player) {
        if (DungeonMobManager.getInstance().checkIsDungeonMob(entity)) {
            if (this.damageMap.containsKey(entity)) {
                if (this.damageMap.get(entity).containsKey(player)) {
                    return this.damageMap.get(entity).get(player);
                }
            }
        }
        return 0;
    }

    public void removeMobOnDeath(LivingEntity entity) {
        if (this.damageMap.containsKey(entity)) {
            this.damageMap.remove(entity);
        }
    }

}