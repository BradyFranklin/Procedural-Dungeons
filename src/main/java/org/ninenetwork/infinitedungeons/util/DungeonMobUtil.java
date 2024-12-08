package org.ninenetwork.infinitedungeons.util;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.mineacademy.fo.MinecraftVersion;
import org.mineacademy.fo.remain.CompMetadata;
import org.ninenetwork.infinitedungeons.dungeon.Dungeon;
import org.ninenetwork.infinitedungeons.mob.AbstractDungeonEnemy;
import org.ninenetwork.infinitedungeons.mob.DungeonMobManager;
import org.ninenetwork.infinitedungeons.mob.boss.DungeonBoss;
import org.ninenetwork.infinitedungeons.mob.boss.DungeonBossManager;
import org.ninenetwork.infinitedungeons.playerstats.PlayerStat;
import org.ninenetwork.infinitedungeons.playerstats.health.MobHealthManager;

import java.util.Random;

public class DungeonMobUtil {

    public static void randomModifier(Dungeon dungeon, LivingEntity entity) {
        Random rand = new Random();
        int chance = rand.nextInt(100);
        if (chance <= 30) {
            if (DungeonMobManager.getInstance().checkIsDungeonMob(entity)) {
                AbstractDungeonEnemy enemy = DungeonMobManager.getInstance().findDungeonMob(entity);
                String modifier = getRandomModifier();
                CompMetadata.setMetadata(entity, "Modifier", modifier);
                MobHealthManager.updateMobNametag(dungeon, entity, enemy.getDungeonMobBaseHealth(dungeon), modifier);
                applyModifier(entity, modifier);
            }
        }
    }

    public static void applyModifier(LivingEntity entity, String modifier) {
        if (modifier.equalsIgnoreCase("Healthy")) {
            entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(1300);
            entity.setHealth(1300);
        } else if (modifier.equalsIgnoreCase("Foritified")) {
            CompMetadata.setMetadata(entity, "Fortified", "30");
        } else if (modifier.equalsIgnoreCase("Speedy")) {
            double baseSpeed = entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getValue();
            entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(baseSpeed * 1.2);
        } else if (modifier.equalsIgnoreCase("Stormy")) {
            CompMetadata.setMetadata(entity, "Stormy", "10");
        } else if (modifier.equalsIgnoreCase("Flaming")) {
            CompMetadata.setMetadata(entity, "Flaming", "80");
        }
    }

    public static String getRandomModifier() {
        Random rand = new Random();
        int choice = rand.nextInt(5);
        if (choice == 0) {
            return "Flaming";
        } else if (choice == 1) {
            return "Stormy";
        } else if (choice == 2) {
            return "Speedy";
        } else if (choice == 3) {
            return "Fortified";
        } else {
            return "Healthy";
        }
    }

    public static double mobHealthScalingExample(double currentHealth, double damage) {
        return (currentHealth * 100000) - (damage * 100000);
    }

    public static String updateMobNametag(LivingEntity entity, double damage, double currentHealth, String entityName, String hex1, String hex2) {
        String nameTag = " ";
        String temp = ("{" + hex1 + ">}" + entityName + "{" + hex2 + "<}");
        if (MinecraftVersion.atLeast(MinecraftVersion.V.v1_16)) {
            nameTag = ("&6✪ " + ColorUtils.colorizeGradient(temp) + " &f" + GeneralUtils.formatNumber(mobHealthScalingExample(currentHealth, damage)) + "&c" + GeneralUtils.getCustomSymbol("heart"));
        }
        return nameTag;
    }

    public static String getMobNametagGradient(Dungeon dungeon, boolean starred, Entity entity, String entityName, String hex1, String hex2, double dipslayedHealth) {
        LivingEntity livingEntity = (LivingEntity) entity;
        String nameTag = " ";
        String colorChar;
        if (MinecraftVersion.atLeast(MinecraftVersion.V.v1_16)) {
            AbstractDungeonEnemy dungeonEnemy = DungeonMobManager.getInstance().findDungeonMob(livingEntity);
            if (dipslayedHealth <= (.2 * dungeonEnemy.getDungeonMobBaseHealth(dungeon))) {
                colorChar = "&c";
            } else if (dipslayedHealth <= (.75 * dungeonEnemy.getDungeonMobBaseHealth(dungeon))) {
                colorChar = "&e";
            } else {
                colorChar = "&a";
            }
            if (starred) {
                nameTag = (hex1 + GeneralUtils.getCustomSymbol("star") + " " + GeneralUtils.formatStringWithGradient(entityName, hex1, hex2, false, false) + " " + colorChar + GeneralUtils.formatNumber(dipslayedHealth) + "&c" + GeneralUtils.getStatSymbol(PlayerStat.HEALTH));
            } else {
                nameTag = GeneralUtils.formatStringWithGradient(entityName, hex1, hex2, false, false) + " " + colorChar + GeneralUtils.formatNumber(dipslayedHealth) + "&c" + GeneralUtils.getStatSymbol(PlayerStat.HEALTH);
            }
        }
        return nameTag;
    }

    public static String getMobNametagSimple(Dungeon dungeon, boolean starred, Entity entity, String entityName, String hex1, String hex2, String hex3, double dipslayedHealth) {
        LivingEntity livingEntity = (LivingEntity) entity;
        String nametag;

        String[] words = entityName.split(" ");
        String[] hex = new String[5];
        hex[0] = hex1;
        hex[1] = hex2;
        hex[2] = hex3;
        hex[3] = hex1;
        hex[4] = hex2;
        StringBuilder build = new StringBuilder();
        int iterator = 0;
        for (String str : words) {
            build.append(hex[iterator]);
            build.append(str);
            build.append(" ");
            iterator++;
        }

        /*
        for (int i = 0; i < words.length; i++) {
            build.append(hex[i]).append(words[i]);
            build.append(" ");
        }
        */

        String colorChar;
        AbstractDungeonEnemy dungeonEnemy = DungeonMobManager.getInstance().findDungeonMob(livingEntity);
        if (dipslayedHealth <= (.2 * dungeonEnemy.getDungeonMobBaseHealth(dungeon))) {
            colorChar = "&c";
        } else if (dipslayedHealth <= (.75 * dungeonEnemy.getDungeonMobBaseHealth(dungeon))) {
            colorChar = "&e";
        } else {
            colorChar = "&a";
        }
        if (starred) {
            nametag = "&6" + GeneralUtils.getCustomSymbol("star") + " " + build + " " + colorChar + GeneralUtils.formatNumber(dipslayedHealth) + "&c" + GeneralUtils.getStatSymbol(PlayerStat.HEALTH);
        } else {
            nametag = build + " " + colorChar + GeneralUtils.formatNumber(dipslayedHealth) + "&c" + GeneralUtils.getStatSymbol(PlayerStat.HEALTH);
        }
        return nametag;
    }

    public static String getBossNametagGradient(Dungeon dungeon, Entity entity, String entityName, String hex1, String hex2, double dipslayedHealth) {
        LivingEntity livingEntity = (LivingEntity) entity;
        String nametag;
        int index = entityName.indexOf(" ");
        String first = entityName.substring(0, index);
        String second = entityName.substring(index);
        String colorChar;
        DungeonBoss boss = DungeonBossManager.getInstance().findDungeonBoss((LivingEntity) entity);
        if (dipslayedHealth <= (.2 * boss.getBossHealth(dungeon))) {
            colorChar = "&c";
        } else if (dipslayedHealth <= (.75 * boss.getBossHealth(dungeon))) {
            colorChar = "&e";
        } else {
            colorChar = "&a";
        }
        nametag = hex1 + GeneralUtils.getCustomSymbol("star") + " " + hex1 + first + hex2 + second + " " + colorChar + GeneralUtils.formatNumber(dipslayedHealth) + "&c" + GeneralUtils.getStatSymbol(PlayerStat.HEALTH);
        return nametag;
    }

    public static String getProcessedString(String string, String hex1, String hex2) {
        String nameTag = " ";
        String temp = ("{" + hex1 + ">}" + string + "{" + hex2 + "<}");
        return nameTag;
    }

    public static void createDyedJustBootsAndHelmet(Entity entity, Color color) {
        LivingEntity livingEntity = (LivingEntity) entity;
        livingEntity.getEquipment().setBoots(createArmorPieceDyed(Material.LEATHER_BOOTS, color));
        livingEntity.getEquipment().setHelmet(createArmorPieceDyed(Material.LEATHER_HELMET, color));
    }

    public static void createDyedArmorPieces(Entity entity, org.bukkit.Color color) {
        LivingEntity livingEntity = (LivingEntity) entity;
        livingEntity.getEquipment().setBoots(createArmorPieceDyed(Material.LEATHER_BOOTS, color));
        livingEntity.getEquipment().setLeggings(createArmorPieceDyed(Material.LEATHER_LEGGINGS, color));
        livingEntity.getEquipment().setChestplate(createArmorPieceDyed(Material.LEATHER_CHESTPLATE, color));
        livingEntity.getEquipment().setHelmet(createArmorPieceDyed(Material.LEATHER_HELMET, color));
    }

    public static ItemStack createArmorPieceDyed(Material material, Color color) {
        ItemStack item = new ItemStack(material, 1);
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
        meta.setColor(color);
        item.setItemMeta(meta);
        return item;
    }

    public static int chooseMobAmount(String roomIdentifier) {
        if (roomIdentifier.equalsIgnoreCase("1x1_Square")) {
            return 5;
        } else if (roomIdentifier.equalsIgnoreCase("2x2_Square")) {
            return 20;
        } else if (roomIdentifier.equalsIgnoreCase("1x2_Rectangle")) {
            return 10;
        } else if (roomIdentifier.equalsIgnoreCase("1x3_Rectangle")) {
            return 15;
        } else if (roomIdentifier.equalsIgnoreCase("1x4_Rectangle")) {
            return 20;
        } else {
            return 15;
        }
    }

    /*
    public static ArrayList<LivingEntity> spawnStarredMobs(DungeonRoomInstance instance, int amount) {
        ArrayList<LivingEntity> entities = new ArrayList<>();
        int upper = instance.getRoomRegion().getPrimary().getBlockX();
        int lower = instance.getRoomRegion().getSecondary().getBlockX();
        int upper2 = instance.getRoomRegion().getPrimary().getBlockZ();
        int lower2 = instance.getRoomRegion().getSecondary().getBlockZ();
        Location selectedLocation;
        Random rand = new Random();
        int choice;
        for (int i = 0; i < amount; i++) {
            choice = rand.nextInt(5);
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
            selectedLocation = new Location(instance.getRoomRegion().getWorld(), x, instance.getRoomRegion().getPrimary().getBlockY() + 3.0, z);
            if (choice == 1) {
                LastAncientArcher archer = new LastAncientArcher(selectedLocation);
                entities.add((LivingEntity) archer.getBukkitEntity());
            } else if (choice == 2) {
                NMSFossilizedTarantula tarantula = new NMSFossilizedTarantula(selectedLocation);
                entities.add((LivingEntity) tarantula.getBukkitEntity());
            } else if (choice == 3) {
                LastNeolithicNecromancer necromancer = new LastNeolithicNecromancer(selectedLocation);
                entities.add((LivingEntity) necromancer.getBukkitEntity());
            } else if (choice == 4) {
                LastWildBarbarian barbarian = new LastWildBarbarian(selectedLocation);
                entities.add((LivingEntity) barbarian.getBukkitEntity());
            } else {
                LastAncientArcher archer = new LastAncientArcher(selectedLocation);
                entities.add((LivingEntity) archer.getBukkitEntity());
            }
        }
        return entities;
    }

     */

}