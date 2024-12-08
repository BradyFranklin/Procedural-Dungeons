package org.ninenetwork.infinitedungeons.util;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.MinecraftVersion;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DungeonMobUtil {

    //Mob Health Registry//

    public static double createMobBeginningHealth(String type, int floor, double mobBaseHealth) {
        return 0.0;
    }

    public static double updateMobHealth(double currentHealth, double damageAmount) {
        return 0.0;
    }

    public static double mobHealthScalingExample(double currentHealth, double damage) {
        return (currentHealth * 100000) - (damage * 100000);
    }

    //Mob Naming//

    public static String updateMobNametag(LivingEntity entity, double damage, double currentHealth, String entityName, String hex1, String hex2) {
        String nameTag = " ";
        String temp = ("{" + hex1 + ">}" + entityName + "{" + hex2 + "<}");
        if (MinecraftVersion.atLeast(MinecraftVersion.V.v1_16)) {
            nameTag = ("&6✪ " + ColorUtils.colorizeGradient(temp) + " &f" + GeneralUtils.formatNumber(mobHealthScalingExample(currentHealth, damage)) + "&c❤");
        }
        return nameTag;
    }

    public static String getEntityDefaultNametag(Entity entity, String entityName, String hex1, String hex2) {
        LivingEntity livingEntity = (LivingEntity) entity;
        String nameTag = " ";
        String temp = ("{" + hex1 + ">}" + entityName + "{" + hex2 + "<}");
        if (MinecraftVersion.atLeast(MinecraftVersion.V.v1_16)) {
            nameTag = ("&6✪ " + ColorUtils.colorizeGradient(temp) + " &f" + GeneralUtils.formatNumber(livingEntity.getHealth() * 100000) + "&c❤");
        }
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

}