package org.ninenetwork.infinitedungeons.mob;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.mineacademy.fo.plugin.SimplePlugin;
import org.ninenetwork.infinitedungeons.dungeon.Dungeon;
import org.ninenetwork.infinitedungeons.dungeon.instance.DungeonRoomInstance;

import java.util.List;

public abstract class AbstractDungeonEnemy implements Listener {

    protected final SimplePlugin plugin;
    public final String mobID;
    public final EntityType entityType;

    public AbstractDungeonEnemy(SimplePlugin plugin, String mobID, EntityType entityType) {
        this.plugin = plugin;
        this.mobID = mobID;
        this.entityType = entityType;
    }

    public boolean isApplicable(LivingEntity entity) {
        if (entity == null) {
            return false;
        }
        PersistentDataContainer pdc = entity.getPersistentDataContainer();
        String pdcId = pdc.get(new NamespacedKey(plugin, "MOB_ID"), PersistentDataType.STRING);
        return mobID.equals(pdcId);
    }

    public LivingEntity spawnDungeonMob(Dungeon dungeon, DungeonRoomInstance instance, EntityType mobType, Location location) {
        LivingEntity entity = (LivingEntity) location.getWorld().spawnEntity(location, mobType);

        PersistentDataContainer pdc = entity.getPersistentDataContainer();
        pdc.set(new NamespacedKey(plugin, "MOB_ID"), PersistentDataType.STRING, mobID);

        DungeonMobRegistry.getInstance().addSingleMob(dungeon, instance, entity);

        return generateMob(entity, dungeon);
    }

    public static Location getSpawnLocation(DungeonRoomInstance instance) {
        int upper = instance.getRoomRegion().getPrimary().getBlockX();
        int lower = instance.getRoomRegion().getSecondary().getBlockX();
        int upper2 = instance.getRoomRegion().getPrimary().getBlockZ();
        int lower2 = instance.getRoomRegion().getSecondary().getBlockZ();
        Location selectedLocation;
        int x;
        int z;
        if (upper > lower) {
            x = (int) (Math.random() * (upper - lower)) + lower;
        } else {
            x = (int) (Math.random() * (lower - upper)) + upper;
        }
        if (upper2 > lower) {
            z = (int) (Math.random() * (upper2 - lower2)) + lower2;
        } else {
            z = (int) (Math.random() * (lower2 - upper2)) + upper2;
        }
        selectedLocation = new Location(instance.getRoomRegion().getWorld(), x, instance.getRoomRegion().getPrimary().getBlockY() + 2.5, z);
        return selectedLocation;
    }

    protected abstract LivingEntity generateMob(LivingEntity entity, Dungeon dungeon);

    public abstract double getDungeonMobBaseHealth(Dungeon dungeon);

    public abstract double getDungeonMobBaseDefense(Dungeon dungeon);

    public abstract double getDungeonMobBaseDamage(Dungeon dungeon);

    public abstract String getMobBaseName();

    public abstract List<Integer> getFloors();

    protected EntityType getEntityType() {
        return null;
    }

    public String getDungeonMobName(Dungeon dungeon, LivingEntity entity, double displayedHealth, String name) {
        return null;
    }

}