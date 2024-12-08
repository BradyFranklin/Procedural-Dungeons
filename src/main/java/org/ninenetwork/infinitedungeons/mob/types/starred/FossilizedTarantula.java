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

public class FossilizedTarantula extends AbstractDungeonEnemy {

    public FossilizedTarantula(SimplePlugin plugin) {
        super(plugin, "FossilizedTarantula", EntityType.SPIDER);
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
        return "Fossil Tara";
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.SPIDER;
    }

    @Override
    public String getDungeonMobName(Dungeon dungeon, LivingEntity entity, double displayedHealth, String name) {
        return Common.colorize(DungeonMobUtil.getMobNametagSimple(dungeon, true, entity, name, "#8BC34A", "#4CAF50", "#009688", displayedHealth));
    }

    @Override
    public List<Integer> getFloors() {
        return Arrays.asList(0,1,2,3,4,5,6,7);
    }

    @Override
    public double getDungeonMobBaseHealth(Dungeon dungeon) {
        int floor = dungeon.getFloor();
        if (dungeon.getType() == DungeonType.CATACOMBS) {
            if (floor == 0) {
                return 22000;
            } else if (floor == 1) {
                return 30000;
            } else if (floor == 2) {
                return 45000;
            } else if (floor == 3) {
                return 65000;
            } else if (floor == 4) {
                return 95000;
            } else if (floor == 5) {
                return 125000;
            } else if (floor == 6) {
                return 200000;
            } else if (floor == 7) {
                return 420000;
            }
        } else if (dungeon.getType() == DungeonType.MASTER) {
            if (floor == 1) {
                return 500000;
            } else if (floor == 2) {
                return 900000;
            } else if (floor == 3) {
                return 1500000;
            } else if (floor == 4) {
                return 2250000;
            } else if (floor == 5) {
                return 3150000;
            } else if (floor == 6) {
                return 4800000;
            } else if (floor == 7) {
                return 9500000;
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
                return 560;
            } else if (floor == 1) {
                return 1120;
            } else if (floor == 2) {
                return 1680;
            } else if (floor == 3) {
                return 2400;
            } else if (floor == 4) {
                return 3520;
            } else if (floor == 5) {
                return 4400;
            } else if (floor == 6) {
                return 5680;
            } else if (floor == 7) {
                return 12400;
            }
        } else if (dungeon.getType() == DungeonType.MASTER) {
            if (floor == 1) {
                return 17000;
            } else if (floor == 2) {
                return 28500;
            } else if (floor == 3) {
                return 42000;
            } else if (floor == 4) {
                return 57500;
            } else if (floor == 5) {
                return 75000;
            } else if (floor == 6) {
                return 100000;
            } else if (floor == 7) {
                return 108000;
            }
        }
        return 0;
    }

}