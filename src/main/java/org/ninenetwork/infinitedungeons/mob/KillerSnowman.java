package org.ninenetwork.infinitedungeons.mob;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Snowball;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.ninenetwork.infinitedungeons.InfiniteDungeonsPlugin;

import javax.annotation.Nullable;

public class KillerSnowman extends SnowGolem implements NMSEntity {

    public KillerSnowman(Location loc) {
        super(EntityType.SNOW_GOLEM, ((CraftWorld) loc.getWorld()).getHandle());

        this.setPosRaw(loc.getX(), loc.getY(), loc.getZ());

        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0, true));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));

        this.setPumpkin(false);

        this.getBukkitEntity().setMetadata("DeadlySnowman", new FixedMetadataValue(InfiniteDungeonsPlugin.getInstance(), true));

        this.persist = false;

        ((CraftWorld) loc.getWorld()).getHandle().addFreshEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);

    }

    @Override
    public void performRangedAttack(LivingEntity entityliving, float f) {
        Snowball entitysnowball = new Snowball(this.level(), this);
        double d0 = entityliving.getEyeY() - 1.100000023841858;
        double d1 = entityliving.getX() - this.getX();
        double d2 = d0 - entitysnowball.getY();
        double d3 = entityliving.getZ() - this.getZ();
        double d4 = Math.sqrt(d1 * d1 + d3 * d3) * 0.20000000298023224;
        entitysnowball.shoot(d1, d2 + d4, d3, 1.6F, 12.0F);
        this.playSound(SoundEvents.SHULKER_SHOOT, 1.0F, 0.4F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level().addFreshEntity(entitysnowball);
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return SoundEvents.WITHER_AMBIENT;
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource damagesource) {
        return SoundEvents.WITHER_SKELETON_HURT;
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return SoundEvents.WITHER_SKELETON_DEATH;
    }

    @Override
    public org.bukkit.entity.LivingEntity spawn(Location location) {
        this.setPosRaw(location.getX(), location.getY(), location.getZ());
        CraftWorld world = ((CraftWorld) this.getBukkitEntity().getWorld());
        world.addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
        return (org.bukkit.entity.LivingEntity) this.getBukkitEntity();
    }

    //EntityZombie a;

    /*
    @Override
    public void a(final EntityLiving entityliving, final float f) {
        final EntitySnowball snowball = new EntitySnowball(this.cK().getMinecraftWorld(), this);

        final double posX = entityliving.X - this.X;
        final double posY = (entityliving.Y + entityliving.getBukkitEntity().getHeight() - 1.100000023841858) - snowball.Y;
        final double posZ = entityliving.Z - this.Z;

        snowball.projectileSource(posX, posY + f2, posZ, 1.6f, 12.0f);

        // Customize the code here to customize shooting sound
        this.playSound(SoundEffects.qE, 1.0f, 1.0f / (this.getRandom().nextFloat() * 0.4f + 0.8f));
        this.t.addEntity(snowball);

        // We inject our snowball tracking to damage entities which are hit
        EntityUtil.trackHit((Projectile) snowball.getBukkitEntity(), hitEvent -> {
            final Entity hitEntity = hitEvent.getHitEntity();

            if (hitEntity instanceof LivingEntity) {
                final LivingEntity living = (LivingEntity) hitEntity;

                living.setHealth(MathUtil.range(living.getHealth() - 4, 0, living.getHealth()));
            }
        });

    }

    @Override
    public void g(final Vec3D vec3d) {

        final double motionSpeed = 0.2;
        final double speed = 1.0;
        final double jumpVelocity = 0.7;

        // Do not call for dead entities
        if (!this.isAlive())
            return;

        // If there's not passenger, we'll default to NMS behavior
        if (!this.isVehicle()) {
            this.bb = 0.02F;
            super.g(vec3d);

            return;
        }

        final EntityLiving passenger = (EntityLiving) this.getPassengers().get(0);

        // Reset fall distance when back on ground
        if (this.isOnGround())
            this.K = 0;

        // Float up, when in water
        if (this.a(TagsFluid.b))
            this.setMot(this.getMot().add(0, 0.5, 0));

        // Make this mob look where the rider is looking
        this.setYawPitch(passenger.getBukkitYaw(), passenger.getBukkitEntity().getLocation().getPitch() * 0.5F);

        this.aP = this.getBukkitYaw();
        this.bt = this.getBukkitYaw();

        final double sideMotion = passenger.bo * motionSpeed;
        double forwardMotion = passenger.bq * motionSpeed;

        // If we are going backwards, make backwards walk slower
        if (forwardMotion <= 0F)
            forwardMotion *= 0.25F;

        this.ride(sideMotion, forwardMotion, vec3d.getY(), (float) speed);

        // Handle jumping
        if (passenger.bn && this.isOnGround()) // prevent infinite jump
            this.setMot(this.getMot().getX(), jumpVelocity, this.getMot().getZ());

        this.a(this, false);
    }

    // Inspired from pig, horse classes and
    // https://www.spigotmc.org/threads/1-15-entity-wasd-control.431302/
    // Updated and reworked for Minecraft 1.16 by MineAcademy
    private void ride(final double motionSideways, final double motionForward, final double motionUpwards, final float speedModifier) {

        double locY;
        float f2;
        final float speed;
        final float swimSpeed;

        // Behavior in water
        if (this.a(TagsFluid.b)) {
            locY = this.locY();
            speed = 0.8F;
            swimSpeed = 0.02F;

            this.a(swimSpeed, new Vec3D(motionSideways, motionUpwards, motionForward));
            this.move(EnumMoveType.a, this.getMot());

            final double motX = this.getMot().getX() * speed;
            double motY = this.getMot().getY() * 0.800000011920929D;
            final double motZ = this.getMot().getZ() * speed;
            motY -= 0.02D;

            if (this.A && this.f(this.getMot().getX(), this.getMot().getY() + 0.6000000238418579D - this.locY() + locY, this.getMot().getZ()))
                motY = 0.30000001192092896D;

            this.setMot(motX, motY, motZ);

            // Behavior in lava
        } else if (this.a(TagsFluid.c)) {
            locY = this.locY();

            this.a(2F, new Vec3D(motionSideways, motionUpwards, motionForward));
            this.move(EnumMoveType.a, this.getMot());

            final double motX = this.getMot().getX() * 0.5D;
            double motY = this.getMot().getY() * 0.5D;
            final double motZ = this.getMot().getZ() * 0.5D;

            motY -= 0.02D;

            if (this.A && this.f(this.getMot().getX(), this.getMot().getY() + 0.6000000238418579D - this.locY() + locY, this.getMot().getZ()))
                motY = 0.30000001192092896D;

            this.setMot(motX, motY, motZ);

            // Behavior on land
        } else {
            final float friction = 0.91F;

            speed = speedModifier * (0.16277136F / (friction * friction * friction));

            this.a(speed, new Vec3D(motionSideways, motionUpwards, motionForward));

            double motX = this.getMot().getX();
            double motY = this.getMot().getY();
            double motZ = this.getMot().getZ();

            if (this.isClimbing()) {
                swimSpeed = 0.15F;
                motX = MathHelper.a(motX, -swimSpeed, swimSpeed);
                motZ = MathHelper.a(motZ, -swimSpeed, swimSpeed);
                this.K = 0.0F;

                if (motY < -0.15D)
                    motY = -0.15D;
            }

            final Vec3D mot = new Vec3D(motX, motY, motZ);

            this.move(EnumMoveType.a, mot);

            if (this.A && this.isClimbing())
                motY = 0.2D;

            motY -= 0.08D;

            motY *= 0.9800000190734863D;
            motX *= friction;
            motZ *= friction;

            this.setMot(motX, motY, motZ);
        }

        this.aP = this.ba;
        locY = this.locX() - this.u;
        final double d1 = this.locZ() - this.w;
        f2 = (float) Math.sqrt(locY * locY + d1 * d1) * 4.0F;

        if (f2 > 1.0F)
            f2 = 1.0F;

        this.ba += (f2 - this.ba) * 0.4F;
        this.bb += this.ba;
    }

    @Override
    public LivingEntity spawn(final Location location) {
        //this.setPosition(location.getX(), location.getY(), location.getZ());
        ((CraftWorld)this.getBukkitEntity().getWorld()).addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);

        return (LivingEntity) this.getBukkitEntity();
    }


    @Override
    protected SoundEffect getSoundFall(final int fallHeight) {
        return SoundEffects.uO;
    }


    @Override
    protected SoundEffect getSoundAmbient() {
        return SoundEffects.uA; // laughter
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.uM;
    }
   */


}