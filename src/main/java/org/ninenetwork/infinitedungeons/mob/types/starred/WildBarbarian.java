package org.ninenetwork.infinitedungeons.mob.types.starred;

import org.bukkit.Color;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
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

public class WildBarbarian extends AbstractDungeonEnemy {

    public WildBarbarian(SimplePlugin plugin) {
        super(plugin, "WildBarbarian", EntityType.ZOMBIE);
    }

    @Override
    protected LivingEntity generateMob(LivingEntity entity, Dungeon dungeon) {

        int baseStartingHealth = 1000;

        ((Zombie) entity).setAdult();

        entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(baseStartingHealth);
        entity.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(4.0);

        entity.setPersistent(false);
        entity.setAI(true);
        entity.setHealth(baseStartingHealth);
        entity.setRemoveWhenFarAway(false);
        entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
        DungeonMobUtil.createDyedArmorPieces(entity, Color.YELLOW);

        entity.setCustomNameVisible(true);
        entity.setCustomName(Common.colorize(this.getDungeonMobName(dungeon, entity, this.getDungeonMobBaseHealth(dungeon), getMobBaseName())));

        return entity;
    }

    @Override
    public String getMobBaseName() {
        return "Wild Barbarian";
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.ZOMBIE;
    }

    @Override
    public String getDungeonMobName(Dungeon dungeon, LivingEntity entity, double displayedHealth, String name) {
        return Common.colorize(DungeonMobUtil.getMobNametagSimple(dungeon, true, entity, name, "#1E371E", "#33691E", "#556B2F", displayedHealth));
    }

    @Override
    public List<Integer> getFloors() {
        return Arrays.asList(0,1,2);
    }

    @Override
    public double getDungeonMobBaseHealth(Dungeon dungeon) {
        int floor = dungeon.getFloor();
        if (dungeon.getType() == DungeonType.CATACOMBS) {
            if (floor == 0) {
                return 22000;
            } else if (floor == 1) {
                return 36000;
            } else if (floor == 2) {
                return 62000;
            }
        } else if (dungeon.getType() == DungeonType.MASTER) {
            if (floor == 1) {
                return 1600000;
            } else if (floor == 2) {
                return 3000000;
            }
        }
        return 0;
    }

    @Override
    public double getDungeonMobBaseDefense(Dungeon dungeon) {
        return 0.0;
    }

    @Override
    public double getDungeonMobBaseDamage(Dungeon dungeon) {
        int floor = dungeon.getFloor();
        if (dungeon.getType() == DungeonType.CATACOMBS) {
            if (floor == 0) {
                return 320;
            } else if (floor == 1) {
                return 544;
            } else if (floor == 2) {
                return 832;
            }
        } else if (dungeon.getType() == DungeonType.MASTER) {
            if (floor == 1) {
                return 14000;
            } else if (floor == 2) {
                return 24000;
            }
        }
        return 0;
    }

}