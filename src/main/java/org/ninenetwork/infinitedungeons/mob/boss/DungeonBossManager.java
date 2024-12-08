package org.ninenetwork.infinitedungeons.mob.boss;

import lombok.Getter;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.LivingEntity;
import org.ninenetwork.infinitedungeons.InfiniteDungeonsPlugin;
import org.ninenetwork.infinitedungeons.mob.boss.mini.Assassin;

import java.util.HashSet;
import java.util.Set;

public class DungeonBossManager {

    private final InfiniteDungeonsPlugin plugin;
    private final Set<DungeonBoss> bossRegistry = new HashSet<>();

    @Getter
    private static final DungeonBossManager instance = new DungeonBossManager(InfiniteDungeonsPlugin.getInstance());

    public DungeonBossManager(InfiniteDungeonsPlugin plugin) {
        this.plugin = plugin;

        registerHandler(new Sentinel(plugin));

        registerHandler(new Assassin(plugin));

    }

    public void registerHandler(DungeonBoss... handlers) {
        for (DungeonBoss handler : handlers) {
            this.registerHandler(handler);
        }
    }

    public void registerHandler(DungeonBoss handler) {
        bossRegistry.add(handler);
        //plugin.registerListener(handler);
    }

    public <T> T getHandler(Class<? extends T> clazz) {
        for (DungeonBoss handler : bossRegistry) {
            if (handler.getClass().equals(clazz)) {
                return (T) handler;
            }
        }
        return null;
    }

    public DungeonBoss getHandler(String bossID) {
        for (DungeonBoss handler : bossRegistry) {
            if (handler.bossID.equals(bossID)) {
                return handler;
            }
        }
        return null;

    }

    public boolean checkIsDungeonBoss(LivingEntity entity) {
        for (DungeonBoss dungeonBoss : this.bossRegistry) {
            if (dungeonBoss.isApplicable(entity)) {
                return true;
            }
        }
        return false;
    }

    public boolean checkIsDungeonBoss(NPC npc) {
        for (DungeonBoss dungeonBoss : this.bossRegistry) {
            if (dungeonBoss.isApplicable(npc)) {
                return true;
            }
        }
        return false;
    }

    public DungeonBoss findDungeonBoss(LivingEntity entity) {
        for (DungeonBoss dungeonBoss : this.bossRegistry) {
            if (dungeonBoss.isApplicable(entity)) {
                return dungeonBoss;
            }
        }
        return null;
    }

    public DungeonBoss findDungeonBoss(NPC npc) {
        for (DungeonBoss dungeonBoss : this.bossRegistry) {
            if (dungeonBoss.isApplicable(npc)) {
                return dungeonBoss;
            }
        }
        return null;
    }

}