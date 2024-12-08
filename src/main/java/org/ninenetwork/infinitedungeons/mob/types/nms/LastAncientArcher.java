package org.ninenetwork.infinitedungeons.mob.types;

import io.lumine.mythic.core.mobs.ai.Pathfinder;
import io.lumine.mythic.core.mobs.ai.PathfindingGoal;
import io.lumine.mythic.core.volatilecode.v1_20_R1.ai.goals.MeleeAttackGoal;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.mineacademy.fo.Common;
import org.ninenetwork.infinitedungeons.InfiniteDungeonsPlugin;
import org.ninenetwork.infinitedungeons.util.DungeonMobUtil;

public class LastAncientArcher extends Skeleton {

    LivingEntity livingEntity;

    public LastAncientArcher(Location loc) {
        super(EntityType.SKELETON, ((CraftWorld)loc.getWorld()).getHandle());

        this.setPosRaw(loc.getX(), loc.getY(), loc.getZ());

        this.setGlowingTag(true);
        this.setCanPickUpLoot(false);
        this.skipDropExperience();
        this.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.BOW));
        this.drops = null; // idk

        this.goalSelector.addGoal(1, new RangedBowAttackGoal<>(this,0.3, 20, 20.0F));
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 8.0F));

        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));

        final Entity bukkitEntity = getBukkitEntity();
        bukkitEntity.setCustomName(Common.colorize(DungeonMobUtil.getEntityDefaultNametag(bukkitEntity, "Ancient Archer", "#F9D1F9", "#D800D5")));
        bukkitEntity.setCustomNameVisible(true);

        DungeonMobUtil.createDyedArmorPieces(bukkitEntity, Color.fromRGB(255, 102, 252));

        this.persist = true;

        this.getBukkitEntity().setMetadata("AncientArcher", new FixedMetadataValue(InfiniteDungeonsPlugin.getInstance(), true));

        ((CraftWorld) loc.getWorld()).getHandle().addFreshEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
        this.livingEntity = (LivingEntity) this.getBukkitEntity();
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

}