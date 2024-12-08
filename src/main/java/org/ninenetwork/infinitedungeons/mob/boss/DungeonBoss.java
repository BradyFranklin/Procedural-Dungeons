package org.ninenetwork.infinitedungeons.mob.boss;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.mcmonkey.sentinel.SentinelTrait;
import org.mineacademy.fo.model.HookManager;
import org.mineacademy.fo.plugin.SimplePlugin;
import org.ninenetwork.infinitedungeons.dungeon.Dungeon;
import org.ninenetwork.infinitedungeons.dungeon.instance.DungeonRoomInstance;

import static org.mineacademy.fo.Valid.checkBoolean;

public abstract class DungeonBoss {

    // silencer
    // sentinel - hacker boss hacks players

    protected final SimplePlugin plugin;
    public final String bossID;
    public final EntityType entityType;
    public NPC npc;

    public DungeonBoss(SimplePlugin plugin, String bossID, EntityType entityType) {
        this.plugin = plugin;
        this.bossID = bossID;
        this.entityType = entityType;
    }

    public boolean isApplicable(LivingEntity entity) {
        if (entity == null) {
            return false;
        }
        PersistentDataContainer pdc = entity.getPersistentDataContainer();
        String pdcId = pdc.get(new NamespacedKey(plugin, "BOSS_ID"), PersistentDataType.STRING);
        return bossID.equals(pdcId);
    }

    public boolean isApplicable(NPC npc) {
        if (npc == null) {
            return false;
        }
        PersistentDataContainer pdc = npc.getEntity().getPersistentDataContainer();
        String pdcId = pdc.get(new NamespacedKey(plugin, "BOSS_ID"), PersistentDataType.STRING);
        return bossID.equals(pdcId);
    }

    public LivingEntity spawnBoss(Dungeon dungeon, DungeonRoomInstance instance, EntityType mobType, Location location) {
        checkBoolean(HookManager.isCitizensLoaded(), "This command requires Citizens plugin to be installed.");
        final NPCRegistry registry = CitizensAPI.getNPCRegistry();
        final NPC npc = registry.createNPC(mobType, dungeon.getName() + dungeon.getNpcSpawns());
        dungeon.setNpcSpawns(dungeon.getNpcSpawns() + 1);
        npc.spawn(location);
        npc.getOrAddTrait(SentinelTrait.class);

        LivingEntity entity = (LivingEntity) npc.getEntity();

        PersistentDataContainer pdc = entity.getPersistentDataContainer();
        pdc.set(new NamespacedKey(plugin, "BOSS_ID"), PersistentDataType.STRING, bossID);
        this.npc = npc;

        return generateMob(npc, dungeon);
    }

    protected abstract LivingEntity generateMob(NPC npc, Dungeon dungeon);

    public abstract EntityType getEntityType();

    public abstract String getBossName(Dungeon dungeon, LivingEntity entity, double displayedHealth);

    public abstract double getDungeonBossBaseDamage(Dungeon dungeon);

    public abstract double getBossHealth(Dungeon dungeon);

    public abstract double getBossDefense(Dungeon dungeon);

}