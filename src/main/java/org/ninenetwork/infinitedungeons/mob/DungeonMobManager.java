package org.ninenetwork.infinitedungeons.mob;

import lombok.Getter;
import org.bukkit.entity.LivingEntity;
import org.ninenetwork.infinitedungeons.InfiniteDungeonsPlugin;
import org.ninenetwork.infinitedungeons.mob.types.SkeletonSoldier;
import org.ninenetwork.infinitedungeons.mob.types.TankZombie;
import org.ninenetwork.infinitedungeons.mob.types.starred.*;

import java.util.HashSet;
import java.util.Set;

public class DungeonMobManager {

    private final InfiniteDungeonsPlugin plugin;

    @Getter
    private final Set<AbstractDungeonEnemy> mobRegistry = new HashSet<>();

    @Getter
    private static final DungeonMobManager instance = new DungeonMobManager(InfiniteDungeonsPlugin.getInstance());

    public DungeonMobManager(InfiniteDungeonsPlugin plugin) {
        this.plugin = plugin;

        registerHandler(new AncientArcher(plugin));
        registerHandler(new FossilizedTarantula(plugin));
        registerHandler(new NeolithicNecromancer(plugin));
        registerHandler(new WildBarbarian(plugin));
        registerHandler(new Fel(plugin));

        registerHandler(new SkeletonSoldier(plugin));
        registerHandler(new TankZombie(plugin));

    }

    public void registerHandler(AbstractDungeonEnemy... handlers) {
        for (AbstractDungeonEnemy handler : handlers) {
            this.registerHandler(handler);
        }
    }

    public void registerHandler(AbstractDungeonEnemy handler) {
        mobRegistry.add(handler);
        //plugin.registerListener(handler);
    }

    public <T> T getHandler(Class<? extends T> clazz) {
        for (AbstractDungeonEnemy handler : mobRegistry) {
            if (handler.getClass().equals(clazz)) {
                return (T) handler;
            }
        }
        return null;
    }

    public AbstractDungeonEnemy getHandler(String itemId) {
        for (AbstractDungeonEnemy handler : mobRegistry) {
            if (handler.mobID.equals(itemId)) {
                return handler;
            }
        }
        return null;

    }

    public boolean checkIsDungeonMob(LivingEntity entity) {
        for (AbstractDungeonEnemy dungeonMob : this.mobRegistry) {
            if (dungeonMob.isApplicable(entity)) {
                return true;
            }
        }
        return false;
    }

    public AbstractDungeonEnemy findDungeonMob(LivingEntity entity) {
        for (AbstractDungeonEnemy dungeonMob : this.mobRegistry) {
            if (dungeonMob.isApplicable(entity)) {
                return dungeonMob;
            }
        }
        return null;
    }

}