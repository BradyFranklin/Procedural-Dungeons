package org.ninenetwork.infinitedungeons.listener;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.ReflectionUtil;
import org.mineacademy.fo.remain.CompMetadata;
import org.ninenetwork.infinitedungeons.NMSSpecific.HitHologram;
import org.ninenetwork.infinitedungeons.PlayerCache;
import org.ninenetwork.infinitedungeons.dungeon.Dungeon;
import org.ninenetwork.infinitedungeons.mob.DungeonMobRegistry;
import org.ninenetwork.infinitedungeons.playerstats.health.PlayerHealthHandler;
import org.ninenetwork.infinitedungeons.settings.Settings;
import org.ninenetwork.infinitedungeons.util.DungeonMobUtil;

import java.awt.*;

public class MobListeners implements Listener {

    @EventHandler
    public void onMobDamage(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        if (event.getEntity().getWorld().getName().equals(Settings.PluginServerSettings.DUNGEON_WORLD_NAME)) {
            if (entity instanceof ArmorStand) {
                event.setCancelled(true);
            }

            //EntityDamaged//
            if (event.getDamager() instanceof Player && entity instanceof LivingEntity && entity.getCustomName() != null) {
                Player player = (Player) event.getDamager();
                PlayerCache cache = PlayerCache.from(player);
                LivingEntity mob = (LivingEntity) entity;
                double damageAmount = 5.0;
                double health = mob.getHealth();
            /*
            if (CompMetadata.hasMetadata(entity, "WildBarbarian")) {
                mob.setCustomName(Common.colorize(DungeonMobUtil.updateMobNametag(mob, damageAmount, health, "Wild Barbarian", "#F1FC01", "#FDBA40")));
            } else if (CompMetadata.hasMetadata(entity, "AncientArcher")) {
                mob.setCustomName(Common.colorize(DungeonMobUtil.updateMobNametag(mob, damageAmount, health, "Ancient Archer", "#F9D1F9", "#D800D5")));
            } else if (CompMetadata.hasMetadata(entity, "NeolithicNecromancer")) {
                mob.setCustomName(Common.colorize(DungeonMobUtil.updateMobNametag(mob, damageAmount, health, "Neo Necro", "#A38C8C", "#3D3D3D")));
            } else if (CompMetadata.hasMetadata(entity, "FossilizedTarantula")) {
                mob.setCustomName(Common.colorize(DungeonMobUtil.updateMobNametag(mob, damageAmount, health, "Fossil Tara", "#A38C8C", "#3D3D3D")));
            }
            */

                if (entity.getCustomName().contains("WildBarbarian")) {
                    mob.setCustomName(Common.colorize(DungeonMobUtil.updateMobNametag(mob, damageAmount, health, "Wild Barbarian", "#F1FC01", "#FDBA40")));
                } else if (entity.getCustomName().contains("AncientArcher")) {
                    mob.setCustomName(Common.colorize(DungeonMobUtil.updateMobNametag(mob, damageAmount, health, "Ancient Archer", "#F9D1F9", "#D800D5")));
                } else if (entity.getCustomName().contains("NeolithicNecromancer")) {
                    mob.setCustomName(Common.colorize(DungeonMobUtil.updateMobNametag(mob, damageAmount, health, "Neo Necro", "#A38C8C", "#3D3D3D")));
                } else if (entity.getCustomName().contains("FossilizedTarantula")) {
                    mob.setCustomName(Common.colorize(DungeonMobUtil.updateMobNametag(mob, damageAmount, health, "Fossil Tara", "#A38C8C", "#3D3D3D")));
                }
                event.setDamage(damageAmount);
                HitHologram.createHitHologram(player, mob, damageAmount);
                if (cache.getCurrentDungeon() != null) {
                    Dungeon dungeon = cache.getCurrentDungeon();
                    if (dungeon.getMobTracking().containsKey(entity)) {
                        Common.tell(player, "Entity found in a room of type " + dungeon.getMobTracking().get(entity).getRoomIdentifier());
                    }
                }
                //PlayerDamaged//
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
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity().getWorld().getName().equalsIgnoreCase(Settings.PluginServerSettings.DUNGEON_WORLD_NAME)) {
            if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player) && entity.getWorld().getName().equalsIgnoreCase("Dungeon")) {
            boolean work = DungeonMobRegistry.getInstance().removeMob(entity);
            if (event.getEntity().getKiller() != null) {
                Common.tell(event.getEntity().getKiller(), "Removal of mob was " + work);
            }
            event.getDrops().clear();
        }

    }

    // Add world check to onMobDamage or region

}