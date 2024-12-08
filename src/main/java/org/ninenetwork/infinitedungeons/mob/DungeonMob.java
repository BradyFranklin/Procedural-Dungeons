package org.ninenetwork.infinitedungeons.mob;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Zombie;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.ninenetwork.infinitedungeons.dungeon.Dungeon;
import org.ninenetwork.infinitedungeons.dungeon.DungeonRoomType;
import org.ninenetwork.infinitedungeons.dungeon.instance.DungeonRoomInstance;
import org.ninenetwork.infinitedungeons.mob.boss.DungeonBoss;
import org.ninenetwork.infinitedungeons.mob.boss.DungeonBossManager;
import org.ninenetwork.infinitedungeons.mob.boss.mini.Assassin;
import org.ninenetwork.infinitedungeons.util.DungeonMobUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DungeonMob {

    public static void initializeCompletionMobs(Dungeon dungeon, DungeonRoomInstance instance) {
        if (!(instance.getType() == DungeonRoomType.BLOOD) && !(instance.getType() == DungeonRoomType.LOBBY)) {
            Random rand = new Random();
            ArrayList<LivingEntity> entities = new ArrayList<>();
            boolean miniSpawned = false;
            Location spawnpoint;
            if (rand.nextInt(4) == 0) {
                //Mini boss case
                for (Location location : instance.getMobSpawnpoints()) {
                    spawnpoint = getSpawnableLocation(location);
                    if (!miniSpawned) {
                        entities.add(spawnMiniBoss(dungeon, instance, spawnpoint));
                        miniSpawned = true;
                    } else {
                        spawnRegularDungeonMob(dungeon, instance, spawnpoint);
                    }
                }
            } else {
                //Starred mobs case
                int maxStarredMobs = DungeonMobUtil.chooseMobAmount(instance.getRoomIdentifier());
                int starredMobsRegistered = 0;
                for (Location location : instance.getMobSpawnpoints()) {
                    spawnpoint = getSpawnableLocation(location);
                    if (starredMobsRegistered < maxStarredMobs) {
                        entities.add(spawnStarredDungeonMob(dungeon, instance, spawnpoint));
                        starredMobsRegistered++;
                    } else {
                        spawnRegularDungeonMob(dungeon, instance, spawnpoint);
                    }
                }
            }
            /*
            int maxStarredMobs = DungeonMobUtil.chooseMobAmount(instance.getRoomIdentifier());
            int starredMobsRegistered = 0;
            for (Location location : instance.getMobSpawnpoints()) {
                spawnpoint = getSpawnableLocation(location);
                if (starredMobsRegistered < maxStarredMobs) {
                    entities.add(spawnStarredDungeonMob(dungeon, instance, spawnpoint));
                    starredMobsRegistered++;
                } else {
                    spawnRegularDungeonMob(dungeon, instance, spawnpoint);
                }
            }
            */
            for (LivingEntity entity : entities) {
                DungeonMobUtil.randomModifier(dungeon, entity);
            }
            instance.setRoomCompleteMobs(entities);
        }
    }

    private static Location getSpawnableLocation(Location location) {
        boolean isAir = location.getBlock().getType() == Material.AIR;
        int iterationProtection = 0;
        while (!isAir) {
            location = location.clone().add(0.0, 1.0, 0.0);
            if (location.getBlock().getType() == Material.AIR) {
                isAir = true;
                break;
            }
            iterationProtection += 1;
            if (iterationProtection > 20) {
                break;
            }
        }
        return location;
    }

    private static LivingEntity spawnRegularDungeonMob(Dungeon dungeon, DungeonRoomInstance instance, Location location) {
        Random rand = new Random();
        List<Class<? extends AbstractDungeonEnemy>> mobs = DungeonMobRegistry.getInstance().getFloorMobs().get(dungeon.getFloor());
        Class<? extends AbstractDungeonEnemy> clazz = mobs.get(rand.nextInt(mobs.size()));
        DungeonMobManager mobManager = DungeonMobManager.getInstance();

        EntityType entityType = mobManager.getHandler(clazz).getEntityType();
        return mobManager.getHandler(clazz).spawnDungeonMob(dungeon, instance, entityType, location);
    }

    private static LivingEntity spawnStarredDungeonMob(Dungeon dungeon, DungeonRoomInstance instance, Location location) {
        Random rand = new Random();
        List<Class<? extends AbstractDungeonEnemy>> mobs = DungeonMobRegistry.getInstance().getFloorMobs().get(dungeon.getFloor());
        Class<? extends AbstractDungeonEnemy> clazz = mobs.get(rand.nextInt(mobs.size()));
        DungeonMobManager mobManager = DungeonMobManager.getInstance();

        EntityType entityType = mobManager.getHandler(clazz).getEntityType();
        return mobManager.getHandler(clazz).spawnDungeonMob(dungeon, instance, entityType, location);
    }

    private static LivingEntity spawnMiniBoss(Dungeon dungeon, DungeonRoomInstance instance, Location location) {
        Random rand = new Random();
        Class<? extends DungeonBoss> clazz = Assassin.class;
        DungeonBossManager bossManager = DungeonBossManager.getInstance();
        int miniBossSelection = rand.nextInt(3);
        if (miniBossSelection == 0) {

            // Assassin
        } else if (miniBossSelection == 1) {

        } else {
            
        }
        EntityType entityType = bossManager.getHandler(clazz).getEntityType();
        return bossManager.getHandler(clazz).spawnBoss(dungeon, instance, entityType, location);
    }

    /////////////////////////////

    public static void initializeStarredMobs(Dungeon dungeon, DungeonRoomInstance dungeonRoomInstance) {
        if (dungeonRoomInstance.getType() != DungeonRoomType.LOBBY && dungeonRoomInstance.getType() != DungeonRoomType.BLOOD) {
            int amount = DungeonMobUtil.chooseMobAmount(dungeonRoomInstance.getRoomIdentifier());
            ArrayList<LivingEntity> entities = new ArrayList<>();
            Random rand = new Random();
            int choice;
            for (int i = 0; i < amount; i++) {
                choice = rand.nextInt(5);
                if (choice == 1) {
                    entities.add(createStarredMob(dungeon, dungeonRoomInstance, "AncientArcher"));
                } else if (choice == 2) {
                    entities.add(createStarredMob(dungeon, dungeonRoomInstance, "FossilizedTarantula"));
                } else if (choice == 3) {
                    entities.add(createStarredMob(dungeon, dungeonRoomInstance, "NeolithicNecromancer"));
                } else if (choice == 4) {
                    entities.add(createStarredMob(dungeon, dungeonRoomInstance, "WildBarbarian"));
                } else {
                    entities.add(createStarredMob(dungeon, dungeonRoomInstance, "AncientArcher"));
                }
            }
            dungeonRoomInstance.getRoomCompleteMobs().addAll(entities);
        }
    }

    public static LivingEntity createStarredMob(Dungeon dungeon, DungeonRoomInstance dungeonRoomInstance, String mobName) {
        Location loc = getSpawnLocation(dungeonRoomInstance);
        LivingEntity entity = (LivingEntity) loc.getWorld().spawnEntity(loc, getEntityType(mobName));
        DungeonMobRegistry.getInstance().addSingleMob(dungeon, dungeonRoomInstance, entity);
        if (entity instanceof PigZombie) {
            ((PigZombie) entity).setAngry(true);
            ((PigZombie) entity).setAdult();
        } else if (entity instanceof Zombie) {
            ((Zombie) entity).setAdult();
        }
        int startHealth = getMobStartingHealth(mobName);
        entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(startHealth);
        entity.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(4.0);
        entity.setPersistent(false);
        entity.setAI(true);
        entity.setHealth(startHealth);
        entity.setRemoveWhenFarAway(false);
        entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
        if (entity.getType() != EntityType.CAVE_SPIDER) {
            DungeonMobUtil.createDyedArmorPieces(entity, getArmorColor(mobName));
        }
        //setName(entity, mobName);
        return entity;
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

    public static int getMobStartingHealth(String mobName) {
        return 20;
    }

    public static EntityType getEntityType(String mobName) {
        if (mobName.equalsIgnoreCase("AncientArcher")) {
            return EntityType.SKELETON;
        } else if (mobName.equalsIgnoreCase("FossilizedTarantula")) {
            return EntityType.CAVE_SPIDER;
        } else if (mobName.equalsIgnoreCase("NeolithicNecromancer")) {
            return EntityType.WITHER_SKELETON;
        } else if (mobName.equalsIgnoreCase("WildBarbarian")) {
            return EntityType.ZOMBIE;
        }
        return EntityType.ZOMBIE;
    }

    public static Color getArmorColor(String mobName) {
        if (mobName.equalsIgnoreCase("AncientArcher")) {
            return Color.fromRGB(255, 102, 252);
        } else if (mobName.equalsIgnoreCase("NeolithicNecromancer")) {
            return Color.fromRGB(192, 192, 192);
        } else if (mobName.equalsIgnoreCase("WildBarbarian")) {
            return Color.YELLOW;
        }
        return Color.WHITE;
    }

    /*
    public static void setName(LivingEntity entity, String mobName) {
        if (mobName.equalsIgnoreCase("AncientArcher")) {
            entity.setCustomName(Common.colorize(DungeonMobUtil.getEntityDefaultNametag(entity, "Ancient Archer", "#F9D1F9", "#D800D5")));
        } else if (mobName.equalsIgnoreCase("FossilizedTarantula")) {
            entity.setCustomName(Common.colorize(DungeonMobUtil.getEntityDefaultNametag(entity, "Fossil Tara", "#A38C8C", "#3D3D3D")));
        } else if (mobName.equalsIgnoreCase("NeolithicNecromancer")) {
            entity.setCustomName(Common.colorize(DungeonMobUtil.getEntityDefaultNametag(entity, "Neo Necro", "#A38C8C", "#3D3D3D")));
        } else if (mobName.equalsIgnoreCase("WildBarbarian")) {
            entity.setCustomName(Common.colorize(DungeonMobUtil.getEntityDefaultNametag(entity, "Wild Barbarian", "#F1FC01", "#FDBA40")));
        }
        entity.setCustomNameVisible(true);
    }

     */

}