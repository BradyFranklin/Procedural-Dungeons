package org.ninenetwork.infinitedungeons.mob.crypt;

import net.citizensnpcs.api.trait.trait.Equipment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.mineacademy.fo.Common;
import org.ninenetwork.infinitedungeons.InfiniteDungeonsPlugin;
import org.ninenetwork.infinitedungeons.util.ColorUtils;
import org.ninenetwork.infinitedungeons.util.DungeonMobUtil;

public class CryptZombies extends Zombie {

    public CryptZombies(Location loc) {
        super(EntityType.ZOMBIE, ((CraftWorld)loc.getWorld()).getHandle());

        this.setPosRaw(loc.getX(), loc.getY(), loc.getZ());
        //this.setItemSlot(EquipmentSlot.FEET, new ItemStack(Items.LEATHER_BOOTS));
        //this.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.NETHERITE_SWORD));

        this.setGlowingTag(true);
        this.setCanPickUpLoot(false);
        this.skipDropExperience();

        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 8.0F));
        //this.goalSelector.addGoal(3, new MoveTowardsTargetGoal(this, 1.0, range));
        //this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));

        final Entity bukkitEntity = getBukkitEntity();
        LivingEntity livingEntity = (LivingEntity) bukkitEntity;
        //bukkitEntity.setCustomName(Common.colorize(DungeonMobUtil.getEntityDefaultNametag(livingEntity, "Crypt Zombie", "#F1FC01", "#FDBA40", 100)));
        bukkitEntity.setCustomNameVisible(true);

        this.setItemSlot(EquipmentSlot.FEET, new net.minecraft.world.item.ItemStack(Items.LEATHER_BOOTS));
        createDyedArmorPieces(livingEntity, Color.YELLOW);

        this.persist = true;

        this.getBukkitEntity().setMetadata("CrapZambier", new FixedMetadataValue(InfiniteDungeonsPlugin.getInstance(), true));

        ((CraftWorld) loc.getWorld()).getHandle().addFreshEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }

    @Override
    public AttributeMap getAttributes() {
        return new AttributeMap(Zombie.createMonsterAttributes().add(Attributes.FOLLOW_RANGE, 35.0).add(Attributes.MOVEMENT_SPEED, 0.25).add(Attributes.ATTACK_DAMAGE, 6.0).add(Attributes.ARMOR, 2.0).add(Attributes.MAX_HEALTH, 40.0).add(Attributes.KNOCKBACK_RESISTANCE, 4.0).build());
    }

    @Override
    protected boolean shouldDropLoot() {
        return false;
    }

    @Override
    protected boolean isSunSensitive() {
        return false;
    }

    private static void createDyedArmorPieces(LivingEntity livingEntity, Color color) {
        livingEntity.getEquipment().setBoots(createArmorPieceDyed(Material.LEATHER_BOOTS, color));
        livingEntity.getEquipment().setLeggings(createArmorPieceDyed(Material.LEATHER_LEGGINGS, color));
        livingEntity.getEquipment().setChestplate(createArmorPieceDyed(Material.LEATHER_CHESTPLATE, color));
        livingEntity.getEquipment().setHelmet(createArmorPieceDyed(Material.LEATHER_HELMET, color));
    }

    private static ItemStack createArmorPieceDyed(Material material, Color color) {
        ItemStack item = new ItemStack(material, 1);
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
        meta.setColor(Color.LIME);
        item.setItemMeta(meta);
        return item;
    }

}