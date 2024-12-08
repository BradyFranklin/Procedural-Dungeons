package org.ninenetwork.infinitedungeons.mob.types;

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
import java.util.Random;

public class SkeletonSoldier extends AbstractDungeonEnemy {

    public SkeletonSoldier(SimplePlugin plugin) {
        super(plugin, "SkeletonSoldier", EntityType.SKELETON);
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
        Random rand = new Random();
        if (rand.nextInt(2) != 0) {
            DungeonMobUtil.createDyedArmorPieces(entity, chooseArmorColor());
        }
        entity.setCustomNameVisible(true);
        entity.setCustomName(Common.colorize(this.getDungeonMobName(dungeon, entity, this.getDungeonMobBaseHealth(dungeon), getMobBaseName())));

        return entity;
    }

    private static Color chooseArmorColor() {
        Random rand = new Random();
        int choice = rand.nextInt(3);
        if (choice == 0) {
            return Color.fromRGB(255, 239, 0);
        } else if (choice == 1) {
            return Color.fromRGB(255, 154, 0);
        } else {
            return Color.fromRGB(255, 43, 0);
        }
    }

    @Override
    public String getMobBaseName() {
        return "Skeleton Soldier";
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.SKELETON;
    }

    @Override
    public String getDungeonMobName(Dungeon dungeon, LivingEntity entity, double displayedHealth, String name) {
        return Common.colorize(DungeonMobUtil.getMobNametagSimple(dungeon, false, entity, name, "#FFC300", "#FF7F00", "#FF0000", displayedHealth));
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
                return 600;
            } else if (floor == 1) {
                return 28000;
            } else if (floor == 2) {
                return 52000;
            } else if (floor == 3) {
                return 90000;
            } else if (floor == 4) {
                return 160000;
            } else if (floor == 5) {
                return 250000;
            } else if (floor == 6) {
                return 360000;
            } else if (floor == 7) {
                return 750000;
            }
        } else if (dungeon.getType() == DungeonType.MASTER) {
            if (floor == 1) {
                return 1600000;
            } else if (floor == 2) {
                return 3000000;
            } else if (floor == 3) {
                return 7200000;
            } else if (floor == 4) {
                return 8400000;
            } else if (floor == 5) {
                return 9600000;
            } else if (floor == 6) {
                return 14400000;
            } else if (floor == 7) {
                return 28000000;
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
                return 200;
            } else if (floor == 1) {
                return 720;
            } else if (floor == 2) {
                return 1120;
            } else if (floor == 3) {
                return 1920;
            } else if (floor == 4) {
                return 2640;
            } else if (floor == 5) {
                return 4160;
            } else if (floor == 6) {
                return 5600;
            } else if (floor == 7) {
                return 11600;
            }
        } else if (dungeon.getType() == DungeonType.MASTER) {
            if (floor == 1) {
                return 18000;
            } else if (floor == 2) {
                return 30000;
            } else if (floor == 3) {
                return 67500;
            } else if (floor == 4) {
                return 75000;
            } else if (floor == 5) {
                return 82500;
            } else if (floor == 6) {
                return 105000;
            } else if (floor == 7) {
                return 120000;
            }
        }
        return 0;
    }

}