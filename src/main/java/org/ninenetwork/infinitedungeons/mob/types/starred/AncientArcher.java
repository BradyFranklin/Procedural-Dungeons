package org.ninenetwork.infinitedungeons.mob.types.starred;

import org.bukkit.Color;
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

public class AncientArcher extends AbstractDungeonEnemy {

    public AncientArcher(SimplePlugin plugin) {
        super(plugin, "AncientArcher", EntityType.SKELETON);
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
        DungeonMobUtil.createDyedArmorPieces(entity, Color.fromRGB(255, 102, 252));

        entity.setCustomNameVisible(true);
        entity.setCustomName(Common.colorize(this.getDungeonMobName(dungeon, entity, this.getDungeonMobBaseHealth(dungeon), getMobBaseName())));

        return entity;
    }

    @Override
    public String getMobBaseName() {
        return "Ancient Archer";
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.SKELETON;
    }

    @Override
    public String getDungeonMobName(Dungeon dungeon, LivingEntity entity, double displayedHealth, String name) {
        return Common.colorize(DungeonMobUtil.getMobNametagSimple(dungeon, true, entity, name, "#FFFAF0", "#FFB6C1", "#FFD700", displayedHealth));
    }

    @Override
    public List<Integer> getFloors() {
        return Arrays.asList(2,3,4,5,6,7);
    }

    @Override
    public double getDungeonMobBaseHealth(Dungeon dungeon) {
        int floor = dungeon.getFloor();
        if (dungeon.getType() == DungeonType.CATACOMBS) {
            if (floor == 2) {
                return 59000;
            } else if (floor == 3) {
                return 99000;
            } else if (floor == 4) {
                return 180000;
            } else if (floor == 5) {
                return 280000;
            } else if (floor == 6) {
                return 400000;
            } else if (floor == 7) {
                return 900000;
            }
        } else if (dungeon.getType() == DungeonType.MASTER) {
            if (floor == 2) {
                return 3450000;
            } else if (floor == 3) {
                return 5400000;
            } else if (floor == 4) {
                return 8000000;
            } else if (floor == 5) {
                return 11100000;
            } else if (floor == 6) {
                return 21000000;
            } else if (floor == 7) {
                return 42000000;
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
            if (floor == 2) {
                return 2625;
            } else if (floor == 3) {
                return 4050;
            } else if (floor == 4) {
                return 5700;
            } else if (floor == 5) {
                return 8700;
            } else if (floor == 6) {
                return 12000;
            } else if (floor == 7) {
                return 24000;
            }
        } else if (dungeon.getType() == DungeonType.MASTER) {
            if (floor == 2) {
                return 45000;
            } else if (floor == 3) {
                return 75000;
            } else if (floor == 4) {
                return 110000;
            } else if (floor == 5) {
                return 150000;
            } else if (floor == 6) {
                return 200000;
            } else if (floor == 7) {
                return 227500;
            }
        }
        return 0;
    }

}