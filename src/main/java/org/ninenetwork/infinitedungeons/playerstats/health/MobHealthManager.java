package org.ninenetwork.infinitedungeons.playerstats.health;

import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.mineacademy.fo.remain.CompMetadata;
import org.ninenetwork.infinitedungeons.PlayerCache;
import org.ninenetwork.infinitedungeons.dungeon.Dungeon;
import org.ninenetwork.infinitedungeons.mob.AbstractDungeonEnemy;
import org.ninenetwork.infinitedungeons.mob.DungeonMobManager;
import org.ninenetwork.infinitedungeons.mob.DungeonMobRegistry;
import org.ninenetwork.infinitedungeons.mob.boss.DungeonBoss;
import org.ninenetwork.infinitedungeons.mob.boss.DungeonBossManager;
import org.ninenetwork.infinitedungeons.util.CooldownManager;

import java.time.Duration;
import java.util.UUID;

public class MobHealthManager {

    // add npc's to mob registry
    public static void killMob(Player player, LivingEntity entity) {
        PlayerCache cache = PlayerCache.from(player);
        if (DungeonMobManager.getInstance().checkIsDungeonMob(entity)) {
            boolean work = DungeonMobRegistry.getInstance().removeMob(entity);
            entity.setHealth(0);
            if (DungeonMobRegistry.getInstance().checkMobIsRegistered(entity)) {
                DungeonMobRegistry.getInstance().removeMob(entity);
            }
        } else if (DungeonBossManager.getInstance().checkIsDungeonBoss(entity)) {
            DungeonBoss dungeonBoss = DungeonBossManager.getInstance().findDungeonBoss(entity);
            dungeonBoss.npc.destroy();
        }
        if (cache.getCurrentDungeonClass().equalsIgnoreCase("Berserk")) {
            UUID uuid = player.getUniqueId();
            CooldownManager manager = CooldownManager.getInstance();
            Duration timeLeft = manager.getRemainingCooldown(uuid, "Bloodlust");
            if (timeLeft.isZero() || timeLeft.isNegative()) {
                manager.setCooldown(uuid, "Bloodlust", Duration.ofSeconds(5L));
            }
        }

    }

    public static void updateMobNametag(Dungeon dungeon, LivingEntity entity, double newHealth, String modifierName) {
        if (DungeonMobManager.getInstance().checkIsDungeonMob(entity)) {
            AbstractDungeonEnemy mob = DungeonMobManager.getInstance().findDungeonMob(entity);
            if (CompMetadata.hasMetadata(entity, "Modifier")) {
                entity.setCustomName(mob.getDungeonMobName(dungeon, entity, newHealth, CompMetadata.getMetadata(entity, "Modifier") + " " + mob.getMobBaseName()));
            } else {
                entity.setCustomName(mob.getDungeonMobName(dungeon, entity, newHealth, mob.getMobBaseName()));
            }
            /*
            if (!modifierName.isEmpty()) {
                entity.setCustomName(mob.getDungeonMobName(dungeon, entity, newHealth, modifierName + " " + mob.getMobBaseName()));
            } else {
                entity.setCustomName(mob.getDungeonMobName(dungeon, entity, newHealth, mob.getMobBaseName()));
            }
             */
        } else if (DungeonBossManager.getInstance().checkIsDungeonBoss(entity)) {
            DungeonBoss boss = DungeonBossManager.getInstance().findDungeonBoss(entity);
            entity.setCustomName(boss.getBossName(dungeon, entity, newHealth));
        }
    }

    public static void updateNpcNametag(Dungeon dungeon, NPC npc, double newHealth) {
        if (DungeonBossManager.getInstance().checkIsDungeonBoss((LivingEntity) npc.getEntity())) {
            DungeonBoss boss = DungeonBossManager.getInstance().findDungeonBoss((LivingEntity) npc.getEntity());
            npc.setName(boss.getBossName(dungeon, (LivingEntity) npc.getEntity(), newHealth));
            npc.getEntity().setCustomName(boss.getBossName(dungeon, (LivingEntity) npc.getEntity(), newHealth));
        }
    }

}