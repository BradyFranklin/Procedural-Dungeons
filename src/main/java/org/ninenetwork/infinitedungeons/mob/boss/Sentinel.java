package org.ninenetwork.infinitedungeons.mob.boss;

import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.effect.GridEffect;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.Gravity;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.mcmonkey.sentinel.SentinelTrait;
import org.mineacademy.fo.plugin.SimplePlugin;
import org.ninenetwork.infinitedungeons.InfiniteDungeonsPlugin;
import org.ninenetwork.infinitedungeons.animation.Effects;
import org.ninenetwork.infinitedungeons.dungeon.Dungeon;
import org.ninenetwork.infinitedungeons.util.GeneralUtils;

public class Sentinel extends DungeonBoss {

    public Sentinel(SimplePlugin plugin) {
        super(plugin, "Sentinel", EntityType.PLAYER);
    }

    @Override
    protected LivingEntity generateMob(NPC npc, Dungeon dungeon) {
        npc.setProtected(true);
        npc.setAlwaysUseNameHologram(false);
        npc.getOrAddTrait(SkinTrait.class).setSkinPersistent("d3731e38-5dde-4878-859d-d0858c4b6793", "Zc+egPfvdkkutM0qR13oIUCXYuLIRkXGLuKutWxSUbW4H7jujEIQD+aKW+Yy9JekTbaqehvp+OArXMkjRs9h8o0ZGAJY/xlXF3OVzfBA7hIvrtx7cSaIRIr5pfjcBCUe0m1l8shByayaCtu/q11QZzZCX1+ZHKgghG9W95EnkmyAESHNjIXFBCMxPCElGfjEIsKwdt48NIlDiCmx3pUSCr3AnL8FvHrG4CMNZK+hhMStOV8nLq7l6MppsUUmRWkL0DVDTEh9BHzAWw3pBOvwP3r9Ax/5amBDrB1sN8vSa/bfuMxlxH11UGt3kb04SOuxYuMCCSCzKq0xSzlP5H5HfW3wSSk9T2zcpyEZgsIud28FZzBjcdgB+Umq0Cp7IybAi6xFbjC8zNgh+y24sNv6F4XJzv8v5eB1AwUZXStDrqrIpTb1XHIJurRNBbyXh3q8XuR2ECmpZAwupKtxWDo5og6IbigQEjKFjMrmvgnUd1dukcdro+w/p2IgmGHVXoR6jtN1YNnpldILDJiql8R097Nco3wU0crU5M1qfqkHHEvOOrf7iOZRF+psNaiJSZuBNmmTdS+13Q+nNwoTfGERFb8Em3YxKFs5j9l4a7HxbW2YvH93sGHCxuPgd9bXJ9KPh6Yp9Uch1cDB/uF4FfOwN7WMQ8ON7IhAHAegjLththc=", "ewogICJ0aW1lc3RhbXAiIDogMTU4OTEzNzY1ODgxOSwKICAicHJvZmlsZUlkIiA6ICJlM2I0NDVjODQ3ZjU0OGZiOGM4ZmEzZjFmN2VmYmE4ZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJNaW5pRGlnZ2VyVGVzdCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8zMzk5ZTAwZjQwNDQxMWU0NjVkNzQzODhkZjEzMmQ1MWZlODY4ZWNmODZmMWMwNzNmYWZmYTFkOTE3MmVjMGYzIgogICAgfQogIH0KfQ==");
        npc.getOrAddTrait(SentinelTrait.class).setHealth(1000);
        npc.getOrAddTrait(Gravity.class).setEnabled(true);

        npc.data().set(NPC.Metadata.KNOCKBACK, false);
        npc.data().set(NPC.Metadata.ACTIVATION_RANGE, 15);

        npc.getNavigator().getLocalParameters()
                .updatePathRate(4)
                .useNewPathfinder(true)
                .avoidWater(true)
                .attackDelayTicks(15)
                .baseSpeed(2.0F);

        npc.getNavigator().setPaused(true);
        this.sentinelFirstStage(npc);
        npc.setName(this.getBossName(dungeon, (LivingEntity) npc.getEntity(), this.getBossHealth(dungeon)));

        return (LivingEntity) npc.getEntity();
    }

    @Override
    public EntityType getEntityType() {
        return null;
    }

    @Override
    public String getBossName(Dungeon dungeon, LivingEntity entity, double displayedHealth) {
        return null;
    }

    @Override
    public double getBossHealth(Dungeon dungeon) {
        return 0;
    }

    @Override
    public double getBossDefense(Dungeon dungeon) {
        return 0;
    }

    @Override
    public double getDungeonBossBaseDamage(Dungeon dungeon) {
        return 0;
    }

    private void sentinelFirstStage(NPC npc) {
        EffectManager effectManager = Effects.getEffectManager();
        final Location startingLocation = npc.getEntity().getLocation();

        GridEffect gridEffect = new GridEffect(effectManager);
        gridEffect.setLocation(npc.getEntity().getLocation().add(0.0, 5.0, 10.0));
        //gridEffect.particle = Particle.SPELL_MOB;
        gridEffect.particle = Particle.VILLAGER_ANGRY;
        gridEffect.period = 1;
        gridEffect.iterations = 50;
        gridEffect.start();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (npc.getEntity() == null) {
                    cancel();
                }
                Location location = npc.getEntity().getLocation();
                if (location.getY() >= startingLocation.getY() + 3.0) {
                    npc.setName("&c&lScarf &8[&7Level 100&8]" + " &7" + GeneralUtils.formatNumber(npc.getOrAddTrait(SentinelTrait.class).health) + "&c❤");
                    //npc.getNavigator().setPaused(false);
                    cancel();
                } else {
                    npc.teleport(location.add(0.0, .2, 0.0), PlayerTeleportEvent.TeleportCause.PLUGIN);
                }
            }
        }.runTaskTimer(InfiniteDungeonsPlugin.getInstance(), 0L, 4L);
    }

    /*
    public void runBossStage(Player player, NPC npc) {
        EffectManager effectManager = Effects.getEffectManager();
        final Location startingLocation = npc.getEntity().getLocation();

        Effect gridEffect = new GridEffect(effectManager);
        gridEffect.setLocation(npc.getEntity().getLocation().add(10.0, 5.0, 0.0));
        gridEffect.particle = Particle.BLOCK_CRACK;
        gridEffect.period = 1;
        gridEffect.duration = 10;
        gridEffect.setMaxIterations(200);
        gridEffect.start();



        Effect effect = new DnaEffect((Effects.getEffectManager()));
        Effect effect1 = new AnimatedBallEffect(Effects.getEffectManager());
        Effect effect2 = new TurnEffect(Effects.getEffectManager());
        final Location startingLocation = npc.getEntity().getLocation();
        //effect1.iterations = 20;
        effect1.iterations = 40;
        effect1.setEntity(npc.getEntity());
        effect2.iterations = 40;
        effect2.setEntity(npc.getEntity());
        effect1.start();
        effect2.start();

        Gravity trait = npc.getOrAddTrait(Gravity.class);
        trait.setEnabled(true);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (npc.getEntity() == null) {
                    cancel();
                }
                Location location = npc.getEntity().getLocation();
                if (location.getY() >= startingLocation.getY() + 3.0) {
                    cancel();
                } else {
                    npc.teleport(location.add(0.0, .2, 0.0), PlayerTeleportEvent.TeleportCause.PLUGIN);
                }
            }
        }.runTaskTimer(InfiniteDungeonsPlugin.getInstance(), 0L, 2L);
    }
    */

}