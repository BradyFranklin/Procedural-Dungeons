package org.ninenetwork.infinitedungeons.mob;

import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.mineacademy.fo.Common;
import org.ninenetwork.infinitedungeons.dungeon.Dungeon;
import org.ninenetwork.infinitedungeons.dungeon.instance.DungeonRoomInstance;

import java.util.*;

public class DungeonMobRegistry {

    @Getter
    private static final DungeonMobRegistry instance = new DungeonMobRegistry();

    private Map<Dungeon,Map<DungeonRoomInstance,ArrayList<LivingEntity>>> mobTracking = new HashMap<>();

    @Getter
    private Map<Integer, List<Class<? extends AbstractDungeonEnemy>>> floorMobs = new HashMap<>();

    public DungeonMobRegistry() {

    }

    public void addDungeon(Dungeon dungeon) {
        if (!this.mobTracking.containsKey(dungeon)) {
            Map<DungeonRoomInstance,ArrayList<LivingEntity>> dungeonInstance = new HashMap<>();
            this.mobTracking.put(dungeon, dungeonInstance);
        }
    }

    public void addSingleMob(Dungeon dungeon, DungeonRoomInstance dungeonRoomInstance, LivingEntity entity) {
        if (!this.mobTracking.get(dungeon).containsKey(dungeonRoomInstance)) {
            ArrayList<LivingEntity> entities = new ArrayList<>(Arrays.asList(entity));
            this.mobTracking.get(dungeon).put(dungeonRoomInstance, entities);
        } else {
            this.mobTracking.get(dungeon).get(dungeonRoomInstance).add(entity);
            Common.log("Added mob to registry");
        }
    }

    public void addRoomAndMobs(Dungeon dungeon, DungeonRoomInstance dungeonRoomInstance, ArrayList<LivingEntity> entities) {
        if (!this.mobTracking.get(dungeon).containsKey(dungeonRoomInstance)) {
            this.mobTracking.get(dungeon).put(dungeonRoomInstance, entities);
        } else {
            if (!this.mobTracking.get(dungeon).get(dungeonRoomInstance).containsAll(entities)) {
                for (LivingEntity entity : entities) {
                    if (!this.mobTracking.get(dungeon).get(dungeonRoomInstance).contains(entity)) {
                        this.mobTracking.get(dungeon).get(dungeonRoomInstance).add(entity);
                    }
                }
            }
        }
    }

    public boolean checkMobIsRegistered(Entity entity) {
        boolean isRegistered = false;
        for (Map.Entry<Dungeon, Map<DungeonRoomInstance, ArrayList<LivingEntity>>> dungeonEntry : mobTracking.entrySet()) {
            Dungeon dungeon = dungeonEntry.getKey();
            Map<DungeonRoomInstance, ArrayList<LivingEntity>> roomInstanceMap = dungeonEntry.getValue();

            // Iterate through the room instances in the inner map
            for (Map.Entry<DungeonRoomInstance, ArrayList<LivingEntity>> roomEntry : roomInstanceMap.entrySet()) {
                DungeonRoomInstance roomInstance = roomEntry.getKey();
                if (roomEntry.getValue().contains(entity)) {
                    isRegistered = true;
                    break;
                }
            }
        }
        return isRegistered;
    }

    public boolean removeMob(Entity entity) {
        // Iterate through the dungeons in the outer map
        for (Map.Entry<Dungeon, Map<DungeonRoomInstance, ArrayList<LivingEntity>>> dungeonEntry : mobTracking.entrySet()) {
            Dungeon dungeon = dungeonEntry.getKey();
            Map<DungeonRoomInstance, ArrayList<LivingEntity>> roomInstanceMap = dungeonEntry.getValue();

            // Iterate through the room instances in the inner map
            for (Map.Entry<DungeonRoomInstance, ArrayList<LivingEntity>> roomEntry : roomInstanceMap.entrySet()) {
                DungeonRoomInstance roomInstance = roomEntry.getKey();
                if (roomEntry.getValue().contains(entity)) {
                    roomEntry.getValue().remove(entity);
                    if (!roomInstance.isCleared()) {
                        if (roomInstance.getRoomCompleteMobs().contains(entity)) {
                            roomInstance.getRoomCompleteMobs().remove(entity);
                            if (roomInstance.getRoomCompleteMobs().isEmpty()) {
                                roomInstance.runClearRoom(dungeon, entity.getLocation(), roomInstance);
                            }
                        }
                    }
                    Common.log("Removed mob from registry");
                    // If the ArrayList becomes empty after removal, remove the room instance entry
                    if (roomEntry.getValue().isEmpty()) {
                        //roomInstanceMap.remove(roomInstance);
                        dungeonEntry.getValue().remove(roomInstance);
                        Common.log("All mobs gone removed room from registry");
                    }
                    // If the room instance map becomes empty after removal, remove the dungeon entry
                    //if (roomInstanceMap.isEmpty()) {
                    //    mobTracking.remove(dungeon);
                    //}
                    // Exit the loop after finding and removing the entity
                    return true;
                }
            }
        }
        return false;
    }

    public void removeAllMobsSpecificDungeon(Dungeon dungeon) {
        ArrayList<LivingEntity> dungeonMobs = new ArrayList<>();
        if (this.mobTracking.containsKey(dungeon)) {
            Map<DungeonRoomInstance, ArrayList<LivingEntity>> dungeonMobMap = this.mobTracking.get(dungeon);
            for (Map.Entry<DungeonRoomInstance, ArrayList<LivingEntity>> roomEntry : dungeonMobMap.entrySet()) {
                dungeonMobs.addAll(roomEntry.getValue());
            }
            for (LivingEntity entity : dungeonMobs) {
                if (!entity.isDead()) {
                    entity.remove();
                }
            }
            this.mobTracking.remove(dungeon);
        }
    }

    public void emergencyClearAllMobs() {
        ArrayList<LivingEntity> allMobs = new ArrayList<>();
        for (Map.Entry<Dungeon, Map<DungeonRoomInstance, ArrayList<LivingEntity>>> dungeonEntry : mobTracking.entrySet()) {
            Dungeon dungeon = dungeonEntry.getKey();
            Map<DungeonRoomInstance, ArrayList<LivingEntity>> roomInstanceMap = dungeonEntry.getValue();
            for (Map.Entry<DungeonRoomInstance, ArrayList<LivingEntity>> roomEntry : roomInstanceMap.entrySet()) {
                DungeonRoomInstance roomInstance = roomEntry.getKey();
                allMobs.addAll(roomEntry.getValue());
            }
        }
        for (LivingEntity entity : allMobs) {
            if (!entity.isDead()) {
                entity.remove();
            }
        }
    }

    public void initializeMobTypes() {
        for (int i = 0; i <= 7; i++) {
            this.floorMobs.put(i, new ArrayList<>());
        }
        Set<AbstractDungeonEnemy> allTypes = DungeonMobManager.getInstance().getMobRegistry();
        for (AbstractDungeonEnemy type : allTypes) {
            for (int i : type.getFloors()) {
                if (!this.floorMobs.get(i).contains(type.getClass())) {
                    this.floorMobs.get(i).add(type.getClass());
                }
            }
        }
    }

}