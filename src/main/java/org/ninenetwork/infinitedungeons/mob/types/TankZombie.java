package org.ninenetwork.infinitedungeons.mob.types;

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

public class TankZombie extends AbstractDungeonEnemy {

    public TankZombie(SimplePlugin plugin) {
        super(plugin, "TankZombie", EntityType.ZOMBIE);
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
        DungeonMobUtil.createDyedArmorPieces(entity, Color.fromRGB(255, 102, 252));

        entity.setCustomNameVisible(true);
        entity.setCustomName(Common.colorize(this.getDungeonMobName(dungeon, entity, this.getDungeonMobBaseHealth(dungeon), this.getMobBaseName())));

        return entity;
    }

    @Override
    public String getMobBaseName() {
        return "Tank Zomb";
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.ZOMBIE;
    }

    @Override
    public String getDungeonMobName(Dungeon dungeon, LivingEntity entity, double displayedHealth, String name) {
        return Common.colorize(DungeonMobUtil.getMobNametagGradient(dungeon, false, entity, name, "#87CEEB", "#FFB6C1", displayedHealth));
    }

    @Override
    public List<Integer> getFloors() {
        return Arrays.asList(0,1,2,3);
    }

    @Override
    public double getDungeonMobBaseHealth(Dungeon dungeon) {
        int floor = dungeon.getFloor();
        if (dungeon.getType() == DungeonType.CATACOMBS) {
            if (floor == 0) {
                return 1500;
            } else if (floor == 1) {
                return 2500;
            } else if (floor == 2) {
                return 4500;
            } else if (floor == 3) {
                return 8000;
            }
        } else if (dungeon.getType() == DungeonType.MASTER) {
            if (floor == 1) {
                return 60000;
            } else if (floor == 2) {
                return 105000;
            } else if (floor == 3) {
                return 160000;
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
                return 464;
            } else if (floor == 2) {
                return 760;
            } else if (floor == 3) {
                return 1320;
            }
        } else if (dungeon.getType() == DungeonType.MASTER) {
            if (floor == 1) {
                return 11000;
            } else if (floor == 2) {
                return 18000;
            } else if (floor == 3) {
                return 25000;
            }
        }
        return 0;
    }

}