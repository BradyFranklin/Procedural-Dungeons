package org.ninenetwork.infinitedungeons.mob.types;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.plugin.SimplePlugin;
import org.ninenetwork.infinitedungeons.dungeon.DungeonType;
import org.ninenetwork.infinitedungeons.mob.AbstractDungeonEnemy;
import org.ninenetwork.infinitedungeons.util.DungeonMobUtil;

public class AncientArcher extends AbstractDungeonEnemy {

    public AncientArcher(SimplePlugin plugin) {
        super(plugin, "AncientArcher", EntityType.SKELETON);
    }

    @Override
    protected LivingEntity generateMob(LivingEntity entity) {

        int baseStartingHealth = 100;

        entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(baseStartingHealth);
        entity.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(4.0);

        entity.setPersistent(false);
        entity.setAI(true);
        entity.setHealth(baseStartingHealth);
        entity.setRemoveWhenFarAway(false);
        entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
        DungeonMobUtil.createDyedArmorPieces(entity, Color.fromRGB(255, 102, 252));

        entity.setCustomNameVisible(true);
        entity.setCustomName(Common.colorize(DungeonMobUtil.getEntityDefaultNametag(entity, "Ancient Archer", "#F9D1F9", "#D800D5")));

        return entity;
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.SKELETON;
    }

    @Override
    public String getDungeonMobName(LivingEntity entity) {
        return Common.colorize(DungeonMobUtil.getEntityDefaultNametag(entity, "Ancient Archer", "#F9D1F9", "#D800D5"));
    }

    @Override
    public double getDungeonMobBaseHealth(DungeonType dungeonType) {
        return 100.0;
    }

    @Override
    public double getDungeonMobBaseDefense(DungeonType dungeonType) {
        return 0.0;
    }

}