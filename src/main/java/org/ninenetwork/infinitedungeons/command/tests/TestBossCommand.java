package org.ninenetwork.infinitedungeons.command.tests;

import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.effect.HelixEffect;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.trait.Gravity;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.mcmonkey.sentinel.SentinelTrait;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.command.SimpleCommand;
import org.mineacademy.fo.model.HookManager;
import org.mineacademy.fo.remain.Remain;
import org.ninenetwork.infinitedungeons.InfiniteDungeonsPlugin;
import org.ninenetwork.infinitedungeons.animation.Effects;

public class TestBossCommand extends SimpleCommand {

    public TestBossCommand() {
        super("dboss");
    }

    @Override
    protected void onCommand() {
        checkConsole();
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Remain.sendTitle(player, 20, 20, 20, "&c&lSentinels MicroCenter", "");
            checkBoolean(HookManager.isCitizensLoaded(), "This command requires Citizens plugin to be installed.");
            final NPCRegistry registry = CitizensAPI.getNPCRegistry();
            final NPC npc = registry.createNPC(EntityType.PLAYER, Common.colorize("&#BAB9FFS&#C4AEF4e&#CEA2EAn&#D897DFt&#E18BD4i&#EB80C9n&#F574BFe&#FF69B4l"));
            //npc.setName("&#BAB9FFS&#C4AEF4e&#CEA2EAn&#D897DFt&#E18BD4i&#EB80C9n&#F574BFe&#FF69B4l");
            npc.spawn(player.getLocation());
            npc.getOrAddTrait(SkinTrait.class).setSkinPersistent("d3731e38-5dde-4878-859d-d0858c4b6793", "Zc+egPfvdkkutM0qR13oIUCXYuLIRkXGLuKutWxSUbW4H7jujEIQD+aKW+Yy9JekTbaqehvp+OArXMkjRs9h8o0ZGAJY/xlXF3OVzfBA7hIvrtx7cSaIRIr5pfjcBCUe0m1l8shByayaCtu/q11QZzZCX1+ZHKgghG9W95EnkmyAESHNjIXFBCMxPCElGfjEIsKwdt48NIlDiCmx3pUSCr3AnL8FvHrG4CMNZK+hhMStOV8nLq7l6MppsUUmRWkL0DVDTEh9BHzAWw3pBOvwP3r9Ax/5amBDrB1sN8vSa/bfuMxlxH11UGt3kb04SOuxYuMCCSCzKq0xSzlP5H5HfW3wSSk9T2zcpyEZgsIud28FZzBjcdgB+Umq0Cp7IybAi6xFbjC8zNgh+y24sNv6F4XJzv8v5eB1AwUZXStDrqrIpTb1XHIJurRNBbyXh3q8XuR2ECmpZAwupKtxWDo5og6IbigQEjKFjMrmvgnUd1dukcdro+w/p2IgmGHVXoR6jtN1YNnpldILDJiql8R097Nco3wU0crU5M1qfqkHHEvOOrf7iOZRF+psNaiJSZuBNmmTdS+13Q+nNwoTfGERFb8Em3YxKFs5j9l4a7HxbW2YvH93sGHCxuPgd9bXJ9KPh6Yp9Uch1cDB/uF4FfOwN7WMQ8ON7IhAHAegjLththc=", "ewogICJ0aW1lc3RhbXAiIDogMTU4OTEzNzY1ODgxOSwKICAicHJvZmlsZUlkIiA6ICJlM2I0NDVjODQ3ZjU0OGZiOGM4ZmEzZjFmN2VmYmE4ZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJNaW5pRGlnZ2VyVGVzdCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8zMzk5ZTAwZjQwNDQxMWU0NjVkNzQzODhkZjEzMmQ1MWZlODY4ZWNmODZmMWMwNzNmYWZmYTFkOTE3MmVjMGYzIgogICAgfQogIH0KfQ==");
            SentinelTrait trait = npc.getOrAddTrait(SentinelTrait.class);
            LivingEntity entity = (LivingEntity) npc.getEntity();

            //npc.getNavigator().getDefaultParameters().attackRange(4.0);

            npc.setProtected(true);
            //npc.setAlwaysUseNameHologram(true);
            npc.getOrAddTrait(Gravity.class).setEnabled(true);
            //npc.getOrAddTrait(SentinelTrait.class).setHealth(1000);

            //npc.data().set(NPC.Metadata.KNOCKBACK, false);
            //npc.data().set(NPC.Metadata.ACTIVATION_RANGE, 15);
            //npc.data().set(NPC.Metadata.AGGRESSIVE, true);

            Common.tell(player, npc.getNavigator().getDefaultParameters().attackRange() + " ");

            npc.getNavigator().setPaused(true);

            Common.runLater(30, new Runnable() {
                @Override
                public void run() {
                    sentinelFirstStage(npc);
                }
            });
        }
    }

    private void sentinelFirstStage(NPC npc) {
        EffectManager effectManager = Effects.getEffectManager();
        final Location startingLocation = npc.getEntity().getLocation();

        //Effect a = new HelixEffect()


        HelixEffect gridEffect = new HelixEffect(effectManager);
        gridEffect.setLocation(npc.getEntity().getLocation().add(0.0, 10.0, 0.0));
        //gridEffect.particle = Particle.SPELL_MOB;
        gridEffect.particle = Particle.VILLAGER_ANGRY;
        gridEffect.period = 8;
        gridEffect.iterations = 20;
        gridEffect.start();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (npc.getEntity() == null) {
                    cancel();
                }
                Location location = npc.getEntity().getLocation();
                if (location.getY() >= startingLocation.getY() + 7.0) {
                    SentinelTrait trait = npc.getOrAddTrait(SentinelTrait.class);
                    npc.getNavigator().setPaused(false);
                    npc.getNavigator().getLocalParameters()
                            .updatePathRate(4 * 20)
                            .useNewPathfinder(true)
                            .attackRange(25)
                            .attackDelayTicks(20)
                            .baseSpeed(1.5F);
                    trait.health = 100;
                    //npc.setName("&c&lScarf &8[&7Level 100&8]" + " &7" + GeneralUtils.formatNumber(trait.health) + "&c❤");
                    npc.getOrAddTrait(Gravity.class).setEnabled(false);
                    npc.setProtected(false);
                    trait.attackRateRanged = 0;
                    trait.range = 1.0;
                    trait.targetingHelper.findQuickMeleeTarget();
                    trait.targetingHelper.findBestTarget();
                    cancel();
                } else {
                    npc.teleport(location.add(0.0, .2, 0.0), PlayerTeleportEvent.TeleportCause.PLUGIN);
                }
            }
        }.runTaskTimer(InfiniteDungeonsPlugin.getInstance(), 0L, 4L);
    }

}