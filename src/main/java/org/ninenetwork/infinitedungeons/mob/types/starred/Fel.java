package org.ninenetwork.infinitedungeons.mob.types.starred;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.plugin.SimplePlugin;
import org.ninenetwork.infinitedungeons.dungeon.Dungeon;
import org.ninenetwork.infinitedungeons.dungeon.DungeonType;
import org.ninenetwork.infinitedungeons.mob.AbstractDungeonEnemy;
import org.ninenetwork.infinitedungeons.util.DungeonMobUtil;

import java.util.Arrays;
import java.util.List;

public class Fel extends AbstractDungeonEnemy {

    public Fel(SimplePlugin plugin) {
        super(plugin, "Fel", EntityType.ENDERMAN);
    }

    @Override
    protected LivingEntity generateMob(LivingEntity entity, Dungeon dungeon) {

        int baseStartingHealth = 1000;

        entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(baseStartingHealth);
        entity.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(4.0);

        entity.setPersistent(false);
        entity.setAI(true);
        entity.setHealth(baseStartingHealth);
        entity.setRemoveWhenFarAway(false);
        entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));

        entity.setCustomNameVisible(true);
        entity.setCustomName(Common.colorize(this.getDungeonMobName(dungeon, entity, this.getDungeonMobBaseHealth(dungeon), getMobBaseName())));

        return entity;
    }

    @Override
    public String getMobBaseName() {
        return "Zoid";
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.ENDERMAN;
    }

    @Override
    public String getDungeonMobName(Dungeon dungeon, LivingEntity entity, double displayedHealth, String name) {
        return Common.colorize(DungeonMobUtil.getMobNametagGradient(dungeon, true, entity, name, "#FFDAB9", "#FF69B4", displayedHealth));
    }

    @Override
    public double getDungeonMobBaseHealth(Dungeon dungeon) {
        int floor = dungeon.getFloor();
        if (dungeon.getType() == DungeonType.CATACOMBS) {
            if (floor == 5) {
                return 600000;
            } else if (floor == 6) {
                return 700000;
            } else if (floor == 7) {
                return 1500000;
            }
        } else if (dungeon.getType() == DungeonType.MASTER) {
            if (floor == 5) {
                return 45000000;
            } else if (floor == 6) {
                return 80000000;
            } else if (floor == 7) {
                return 160000000;
            }
        }
        return 0;
    }

    @Override
    public double getDungeonMobBaseDefense(Dungeon dungeon) {
        return 0.0;
    }

    @Override
    public List<Integer> getFloors() {
        return Arrays.asList(5,6,7);
    }

    @Override
    public double getDungeonMobBaseDamage(Dungeon dungeon) {
        int floor = dungeon.getFloor();
        if (dungeon.getType() == DungeonType.CATACOMBS) {
            if (floor == 5) {
                return 8000;
            } else if (floor == 6) {
                return 9600;
            } else if (floor == 7) {
                return 20000;
            }
        } else if (dungeon.getType() == DungeonType.MASTER) {
            if (floor == 5) {
                return 150000;
            } else if (floor == 6) {
                return 200000;
            } else if (floor == 7) {
                return 240000;
            }
        }
        return 0;
    }

}