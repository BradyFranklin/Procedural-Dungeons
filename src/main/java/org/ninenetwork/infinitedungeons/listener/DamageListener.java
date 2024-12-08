package org.ninenetwork.infinitedungeons.listener;

import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.mineacademy.fo.Common;
import org.ninenetwork.infinitedungeons.mob.DungeonMobManager;
import org.ninenetwork.infinitedungeons.mob.DungeonMobRegistry;
import org.ninenetwork.infinitedungeons.playerstats.damage.DamageType;
import org.ninenetwork.infinitedungeons.playerstats.damage.MobDamageHandler;
import org.ninenetwork.infinitedungeons.playerstats.damage.PlayerDamageHandler;
import org.ninenetwork.infinitedungeons.settings.Settings;

public class DamageListener implements Listener {

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();

        if (event.getEntity().getWorld().getName().equals(Settings.PluginServerSettings.DUNGEON_WORLD_NAME)) {
            if (entity instanceof ArmorStand) {
                //event.setCancelled(true);
            }
            if (CitizensAPI.getNPCRegistry().isNPC(event.getEntity()) || CitizensAPI.getNPCRegistry().isNPC(event.getDamager())) {
                Common.log("Cancelled normal damage event for npc");
                //event.setCancelled(true);
            }

            if (event.getDamager() instanceof LivingEntity && event.getEntity() instanceof LivingEntity) {
                if (DungeonMobManager.getInstance().checkIsDungeonMob((LivingEntity) event.getEntity())) {
                    if (event.getDamager() instanceof Player) {
                        PlayerDamageHandler.handleNormalDamage((Player) event.getDamager(), (LivingEntity) event.getEntity(), DamageType.MELEE);
                    }
                    event.setCancelled(true);
                } else if (DungeonMobManager.getInstance().checkIsDungeonMob((LivingEntity) event.getDamager())) {
                    if (event.getEntity() instanceof Player) {
                        MobDamageHandler.handleNormalDamage((Player) event.getEntity(), (LivingEntity) event.getDamager(), DamageType.MELEE);
                    }
                    event.setCancelled(true);
                } else if (!(event.getDamager() instanceof Player) && !(event.getEntity() instanceof Player)) {
                    event.setCancelled(true);
                }
            }

            /*
            if (event.getDamager() instanceof Player && (entity instanceof LivingEntity && !(entity instanceof org.bukkit.entity.NPC)) && entity.getCustomName() != null) {
                if (event.getEntity() instanceof NPC) {
                    Common.log("Regular damage event for player hitting an npc");
                } else {
                    PlayerDamageHandler.handleNormalDamage((Player) event.getDamager(), (LivingEntity) event.getEntity(), DamageType.MELEE);
                    Common.tell(event.getDamager(), "Reached damage handle");
                }
            } else if ((event.getDamager() instanceof LivingEntity || event.getDamager() instanceof NPC) && event.getEntity() instanceof Player) {
                if (event.getDamager() instanceof NPC) {
                    Common.log("Regular damage event ran for npc hitting player");
                } else {
                    MobDamageHandler.handleNormalDamage((Player) event.getEntity(),(LivingEntity) event.getDamager(),DamageType.MELEE);
                }
                //add param's here and finish
            }
            if (!(event.getDamager() instanceof NPC) && !(event.getEntity() instanceof NPC)) {
                //event.setCancelled(true);
            }

                //PlayerDamaged// havent handled player beingg damaged yet new way
            } else if (event.getDamager() instanceof LivingEntity && event.getEntity() instanceof Player) {
                Player player = (Player) event.getEntity();
                LivingEntity mob = (LivingEntity) event.getDamager();
                Common.tell(player, "Hit Debug");
                PlayerHealthHandler.mainHealthSequence(player, 5.0);
                if (CompMetadata.hasMetadata(entity, "NeolithicNecromancer")) {
                    if (player.isVisualFire()) {
                        player.setVisualFire(false);
                        Common.tell(player, "Removed visual fire");
                    }
                }
            }
            */
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity().getWorld().getName().equals(Settings.PluginServerSettings.DUNGEON_WORLD_NAME)) {
            if (event.getHitEntity() instanceof Player) {
                if (event.getEntity() instanceof Arrow) {
                    Arrow arrow = (Arrow) event.getEntity();
                    if (DungeonMobManager.getInstance().checkIsDungeonMob((LivingEntity) arrow.getShooter())) {
                        MobDamageHandler.handleNormalDamage((Player) event.getHitEntity(), (LivingEntity) arrow.getShooter(), DamageType.PROJECTILE);
                    }
                    event.setCancelled(true);
                }
            } else if (!(event.getHitEntity() instanceof Player) && !(event.getEntity().getShooter() instanceof Player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onFireTick(EntityCombustByBlockEvent event) {
        if (event.getEntity().getWorld().getName().equals(Settings.PluginServerSettings.DUNGEON_WORLD_NAME)) {
            if (!(event.getEntity() instanceof Player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity().getWorld().getName().equalsIgnoreCase(Settings.PluginServerSettings.DUNGEON_WORLD_NAME)) {
            EntityDamageEvent.DamageCause damageCause = event.getCause();
            if (damageCause == EntityDamageEvent.DamageCause.FALL || damageCause == EntityDamageEvent.DamageCause.CRAMMING || damageCause == EntityDamageEvent.DamageCause.FIRE ||
            damageCause == EntityDamageEvent.DamageCause.FIRE_TICK || damageCause == EntityDamageEvent.DamageCause.LAVA || damageCause == EntityDamageEvent.DamageCause.SUFFOCATION) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player) && entity.getWorld().getName().equals(Settings.PluginServerSettings.DUNGEON_WORLD_NAME)) {
            boolean work = DungeonMobRegistry.getInstance().removeMob(entity);
            if (event.getEntity().getKiller() != null) {
                Common.tell(event.getEntity().getKiller(), "Removal of mob was " + work);
            }
            event.getDrops().clear();
        }

    }

    // Add world check to onMobDamage or region

}