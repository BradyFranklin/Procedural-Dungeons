package org.ninenetwork.infinitedungeons.mob.types.nms;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.mineacademy.fo.Common;
import org.ninenetwork.infinitedungeons.InfiniteDungeonsPlugin;
import org.ninenetwork.infinitedungeons.util.DungeonMobUtil;

public class LastNeolithicNecromancer extends WitherSkeleton {

    public LastNeolithicNecromancer(Location loc) {
        super(EntityType.WITHER_SKELETON, ((CraftWorld)loc.getWorld()).getHandle());

        this.setPosRaw(loc.getX(), loc.getY(), loc.getZ());

        this.setGlowingTag(true);
        this.setCanPickUpLoot(false);
        this.skipDropExperience();
        this.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.STONE_SWORD));

        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 8.0F));

        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));

        final Entity bukkitEntity = getBukkitEntity();
        //bukkitEntity.setCustomName(Common.colorize(DungeonMobUtil.getEntityDefaultNametag(bukkitEntity, "Neo Necro", "#A38C8C", "#3D3D3D", 100)));
        bukkitEntity.setCustomNameVisible(true);

        DungeonMobUtil.createDyedArmorPieces(bukkitEntity, Color.fromRGB(192, 192, 192));

        this.persist = true;

        this.getBukkitEntity().setMetadata("NeolithicNecromancer", new FixedMetadataValue(InfiniteDungeonsPlugin.getInstance(), true));

        ((CraftWorld) loc.getWorld()).getHandle().addFreshEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }

    @Override
    public AttributeMap getAttributes() {
        return new AttributeMap(Skeleton.createMonsterAttributes().add(Attributes.FOLLOW_RANGE, 35.0).add(Attributes.MOVEMENT_SPEED, 0.25).add(Attributes.ATTACK_DAMAGE, 6.0).add(Attributes.ARMOR, 2.0).add(Attributes.MAX_HEALTH, 40.0).add(Attributes.KNOCKBACK_RESISTANCE, 4.0).build());
    }

    @Override
    protected boolean shouldDropLoot() {
        return false;
    }

    @Override
    protected boolean isSunBurnTick() {
        return false;
    }

    @Override
    public boolean canFreeze() {
        return false;
    }

    @Override
    public void handleDamageEvent(DamageSource damagesource) {
        super.handleDamageEvent(damagesource);
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity entity) {
        if (!super.doHurtTarget(entity)) {
            return false;
        } else {
            if (entity instanceof LivingEntity) {
                ((LivingEntity)entity).addEffect(new MobEffectInstance(MobEffects.WITHER, 40), this, EntityPotionEffectEvent.Cause.ATTACK);
            }

            return true;
        }
    }

}