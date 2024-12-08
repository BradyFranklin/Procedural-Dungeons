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

public class NeolithicNecromancer extends AbstractDungeonEnemy {

    public NeolithicNecromancer(SimplePlugin plugin) {
        super(plugin, "NeolithicNecromancer", EntityType.WITHER_SKELETON);
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
        DungeonMobUtil.createDyedArmorPieces(entity, Color.fromRGB(192, 192, 192));

        entity.setCustomNameVisible(true);
        entity.setCustomName(Common.colorize(this.getDungeonMobName(dungeon, entity, this.getDungeonMobBaseHealth(dungeon), getMobBaseName())));

        return entity;
    }

    @Override
    public String getMobBaseName() {
        return "Neolithic Necromancer";
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.WITHER_SKELETON;
    }

    @Override
    public String getDungeonMobName(Dungeon dungeon, LivingEntity entity, double displayedHealth, String name) {
        return Common.colorize(DungeonMobUtil.getMobNametagSimple(dungeon, true, entity, name, "#999999", "#666666","#333333", displayedHealth));
    }

    @Override
    public List<Integer> getFloors() {
        return Arrays.asList(4,5,6,7);
    }

    @Override
    public double getDungeonMobBaseHealth(Dungeon dungeon) {
        int floor = dungeon.getFloor();
        if (dungeon.getType() == DungeonType.CATACOMBS) {
            if (floor == 4) {
                return 300000;
            } else if (floor == 5) {
                return 470000;
            } else if (floor == 6) {
                return 600000;
            } else if (floor == 7) {
                return 1250000;
            }
        } else if (dungeon.getType() == DungeonType.MASTER) {
            if (floor == 4) {
                return 31250000;
            } else if (floor == 5) {
                return 48000000;
            } else if (floor == 6) {
                return 88000000;
            } else if (floor == 7) {
                return 150000000;
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
            if (floor == 4) {
                return 4640;
            } else if (floor == 5) {
                return 6400;
            } else if (floor == 6) {
                return 8480;
            } else if (floor == 7) {
                return 17600;
            }
        } else if (dungeon.getType() == DungeonType.MASTER) {
            if (floor == 4) {
                return 100000;
            } else if (floor == 5) {
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