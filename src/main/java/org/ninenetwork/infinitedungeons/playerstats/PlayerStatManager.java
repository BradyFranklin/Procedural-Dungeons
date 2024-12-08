package org.ninenetwork.infinitedungeons.playerstats;

import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.remain.CompMetadata;
import org.ninenetwork.infinitedungeons.PlayerCache;
import org.ninenetwork.infinitedungeons.dungeon.DungeonLeveling;
import org.ninenetwork.infinitedungeons.item.AbstractDungeonItem;
import org.ninenetwork.infinitedungeons.item.CustomDungeonItemManager;
import org.ninenetwork.infinitedungeons.util.AccessoryUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerStatManager {

    public static void onDungeonStartStatEvaluation(Player player) {
        PlayerCache cache = PlayerCache.from(player);
        PlayerStatSourceDatabase manager = PlayerStatSourceDatabase.getInstance();
        evaluateEquipment(player);
        evaluateTalismanStats(player, AccessoryUtil.getItems(player));
    }

    public static void updateItemStats(Player player, ItemStack oldItem, ItemStack newItem) {
        CustomDungeonItemManager manager = CustomDungeonItemManager.getInstance();
        Map<PlayerStat, Double> removalStats = new HashMap<>();
        Map<PlayerStat, Double> additionStats = new HashMap<>();

        if (manager.checkIsDungeonItem(oldItem)) {
            removalStats = manager.findDungeonItem(oldItem).itemStatData();
        }
        if (manager.checkIsDungeonItem(newItem)) {
            additionStats = manager.findDungeonItem(newItem).itemStatData();
        }

        List<PlayerStat> processedStats = new ArrayList<>();

        if (!removalStats.isEmpty()) {
            for (Map.Entry<PlayerStat, Double> entry : removalStats.entrySet()) {
                Common.tell(player, entry.getKey().label + " " + entry.getValue());
                double additionValue = 0;
                if (additionStats.containsKey(entry.getKey())) {
                    additionValue = additionStats.get(entry.getKey());
                }
                changeSpecificStat(player, entry.getKey(), entry.getValue(), additionValue);
                processedStats.add(entry.getKey());
                //additionStats.remove(entry.getKey());
                //removalStats.remove(entry.getKey());
            }
        }
        for (PlayerStat stat : processedStats) {
            additionStats.remove(stat);
        }
        if (!additionStats.isEmpty()) {
            for (Map.Entry<PlayerStat, Double> entry : additionStats.entrySet()) {
                changeSpecificStat(player, entry.getKey(), 0, entry.getValue());
            }
        }
    }

    public static void evaluateEquipment(Player player) {
        PlayerStatSourceDatabase manager = PlayerStatSourceDatabase.getInstance();
        EntityEquipment equipment = player.getEquipment();
        if (CustomDungeonItemManager.getInstance().checkIsDungeonItem(equipment.getHelmet())) {
            manager.addFullStatsFromSpecificSourceToMapping(player, PlayerStatSource.ARMOR_HELMET, CustomDungeonItemManager.getInstance().findDungeonItem(equipment.getHelmet()).itemStatData(), false);
        }
        if (CustomDungeonItemManager.getInstance().checkIsDungeonItem(equipment.getChestplate())) {
            manager.addFullStatsFromSpecificSourceToMapping(player, PlayerStatSource.ARMOR_CHESTPLATE, CustomDungeonItemManager.getInstance().findDungeonItem(equipment.getChestplate()).itemStatData(), false);
        }
        if (CustomDungeonItemManager.getInstance().checkIsDungeonItem(equipment.getLeggings())) {
            manager.addFullStatsFromSpecificSourceToMapping(player, PlayerStatSource.ARMOR_LEGGINGS, CustomDungeonItemManager.getInstance().findDungeonItem(equipment.getLeggings()).itemStatData(), false);
        }
        if (CustomDungeonItemManager.getInstance().checkIsDungeonItem(equipment.getBoots())) {
            manager.addFullStatsFromSpecificSourceToMapping(player, PlayerStatSource.ARMOR_BOOTS, CustomDungeonItemManager.getInstance().findDungeonItem(equipment.getBoots()).itemStatData(), false);
        }
        if (CustomDungeonItemManager.getInstance().checkIsDungeonItem(equipment.getItemInMainHand())) {
            manager.addFullStatsFromSpecificSourceToMapping(player, PlayerStatSource.WEAPON, CustomDungeonItemManager.getInstance().findDungeonItem(equipment.getItemInMainHand()).itemStatData(), false);
        }
        PlayerStatManager.setAllPlayerStats(player);
    }

    public static Map<PlayerStat, Double> evaluateEquipmentStats(Player player, String exclusively) {
        Map<PlayerStat, Double> totals = new HashMap<>();
        if (player.getEquipment() != null) {
            ArrayList<ItemStack> items = new ArrayList<>();
            if (!exclusively.equalsIgnoreCase("item")) {
                items.add(player.getEquipment().getHelmet());
                items.add(player.getEquipment().getChestplate());
                items.add(player.getEquipment().getLeggings());
                items.add(player.getEquipment().getBoots());
            }
            if (!exclusively.equalsIgnoreCase("armor")) {
                items.add(player.getEquipment().getItemInMainHand());
            }
            CustomDungeonItemManager itemManager = CustomDungeonItemManager.getInstance();
            for (ItemStack item : items) {
                if (item != null) {
                    if (itemManager.checkIsDungeonItem(item)) {
                        for (Map.Entry<PlayerStat, Double> entry : itemManager.findDungeonItem(item).itemStatData().entrySet()) {
                            if (totals.containsKey(entry.getKey())) {
                                totals.replace(entry.getKey(), totals.get(entry.getKey()) + entry.getValue());
                            } else {
                                totals.put(entry.getKey(), entry.getValue());
                            }
                        }
                    }
                }
            }
            for (Map.Entry<PlayerStat, Double> entry : totals.entrySet()) {
                totals.replace(entry.getKey(), entry.getValue() * ((100 + DungeonLeveling.getPlayerLevelBaseBoost(player)) / 100));
                PlayerStat key = entry.getKey();
                double value = entry.getValue();
                resetUpdatePlayerSpecificStat(player, key, value);
            }
        }
        return totals;
    }

    public static void resetUpdatePlayerSpecificStat(Player player, PlayerStat stat, double addedValue) {
        PlayerCache cache = PlayerCache.from(player);
        try {
            String statName = stat.label.replace("_", "");
            String methodName = "getBase" + statName;
            Method getBaseMethod = PlayerCache.class.getMethod(methodName);

            methodName = "setActive" + statName.substring(0, 1).toUpperCase() + statName.substring(1);
            Method setActiveMethod = PlayerCache.class.getMethod(methodName, double.class);

            double baseValue = (double) getBaseMethod.invoke(cache);
            double totalStatValue = baseValue + addedValue;
            setActiveMethod.invoke(cache, totalStatValue);
            if (stat == PlayerStat.HEALTH || stat == PlayerStat.INTELLIGENCE) {
                String methodName2 = "setActiveMax" + statName.substring(0, 1).toUpperCase() + statName.substring(1);
                Method setMaxMethod = PlayerCache.class.getMethod(methodName2, double.class);
                setMaxMethod.invoke(cache, totalStatValue);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setAllPlayerStats(Player player) {
        PlayerCache cache = PlayerCache.from(player);
        try {
            for (Map.Entry<PlayerStat, Double> entry : cache.getStatMapper().entrySet()) {
                String statName = entry.getKey().label.replace("_", "");
                if (entry.getKey() == PlayerStat.HEALTH || entry.getKey() == PlayerStat.INTELLIGENCE) {
                    String setMaxMethodName = "setActiveMax" + statName;
                    Method setMaxMethod = PlayerCache.class.getMethod(setMaxMethodName, double.class);
                    setMaxMethod.invoke(cache, entry.getValue());
                    if (entry.getKey() == PlayerStat.HEALTH) {
                        double nextHealth = entry.getValue() * cache.getLastStoredHealth();
                        if (!(nextHealth <= entry.getValue())) {
                            nextHealth = entry.getValue();
                        }
                        cache.setActiveHealth(nextHealth);
                    }
                } else {
                    String setMethodName = "setActive" + statName;
                    Method setActiveMethod = PlayerCache.class.getMethod(setMethodName, double.class);
                    setActiveMethod.invoke(cache, entry.getValue());
                    if (entry.getKey() == PlayerStat.SPEED) {
                        cache.getActiveSpeed();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void changeSpecificStat(Player player, PlayerStat stat, double removalValue, double addedValue) {
        PlayerCache cache = PlayerCache.from(player);
        try {
            String statName = stat.label.replace("_", "");
            double nextValue;

            if (removalValue <= addedValue) {
                nextValue = addedValue - removalValue;
                String methodName = "setActive" + statName.substring(0, 1).toUpperCase() + statName.substring(1);
                Method setActiveMethod = PlayerCache.class.getMethod(methodName, double.class);
                String getterMethod = "getActive" + statName.substring(0, 1).toUpperCase() + statName.substring(1);
                Method getActiveMethod = PlayerCache.class.getMethod(getterMethod);
                double current = (double) getActiveMethod.invoke(cache);

                setActiveMethod.invoke(cache, current + nextValue);
                if (stat == PlayerStat.HEALTH || stat == PlayerStat.INTELLIGENCE) {
                    String methodName2 = "setActiveMax" + statName.substring(0, 1).toUpperCase() + statName.substring(1);
                    Method setMaxMethod = PlayerCache.class.getMethod(methodName2, double.class);
                    String getterMethod2 = "getActiveMax" + statName.substring(0, 1).toUpperCase() + statName.substring(1);
                    Method getMaxMethod = PlayerCache.class.getMethod(getterMethod2);
                    double currentMax = (double) getMaxMethod.invoke(cache);
                    setMaxMethod.invoke(cache, currentMax + nextValue);
                }
            } else {
                String getterMethod = "getActive" + statName.substring(0, 1).toUpperCase() + statName.substring(1);
                Method getActiveMethod = PlayerCache.class.getMethod(getterMethod);
                String methodName = "setActive" + statName.substring(0, 1).toUpperCase() + statName.substring(1);
                Method setActiveMethod = PlayerCache.class.getMethod(methodName, double.class);
                double current = (double) getActiveMethod.invoke(cache);
                setActiveMethod.invoke(cache, current + addedValue);
                current = (double) getActiveMethod.invoke(cache);
                if ((current - removalValue) <= 0) {
                    setActiveMethod.invoke(cache, 0.0);
                } else {
                    setActiveMethod.invoke(cache, current - removalValue);
                }
                if (stat == PlayerStat.HEALTH || stat == PlayerStat.INTELLIGENCE) {
                    String methodName2 = "setActiveMax" + statName.substring(0, 1).toUpperCase() + statName.substring(1);
                    Method setMaxMethod = PlayerCache.class.getMethod(methodName2, double.class);
                    String getterMethod2 = "getActiveMax" + statName.substring(0, 1).toUpperCase() + statName.substring(1);
                    Method getMaxMethod = PlayerCache.class.getMethod(getterMethod2);
                    double currentMax = (double) getMaxMethod.invoke(cache);
                    setMaxMethod.invoke(cache, currentMax + addedValue);
                    currentMax = (double) getMaxMethod.invoke(cache);
                    if ((currentMax - removalValue) <= 0) {
                        setMaxMethod.invoke(cache, 0.0);
                    } else {
                        setMaxMethod.invoke(cache, currentMax - removalValue);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void playerTalismanStats(Player player, ArrayList<ItemStack> talismans) {
        PlayerCache cache = PlayerCache.from(player);
        ArrayList<AbstractDungeonItem> talis = new ArrayList<>();
        for (ItemStack item : talismans) {
            if (CustomDungeonItemManager.getInstance().checkIsDungeonItem(item)) {
                talis.add(CustomDungeonItemManager.getInstance().findDungeonItem(item));
            }
        }
        for (AbstractDungeonItem i : talis) {
            for (Map.Entry<PlayerStat, Double> entry : i.itemStatData().entrySet()) {
                cache.addTalismanStat(entry.getKey(), entry.getValue());
            }
        }
    }

    public static void evaluateTalismanStats(Player player, ArrayList<ItemStack> talismans) {
        ArrayList<AbstractDungeonItem> talis = new ArrayList<>();
        Map<PlayerStat, Double> allTalismanStats = new HashMap<>();
        for (ItemStack item : talismans) {
            if (CustomDungeonItemManager.getInstance().checkIsDungeonItem(item)) {
                talis.add(CustomDungeonItemManager.getInstance().findDungeonItem(item));
            }
        }
        for (AbstractDungeonItem i : talis) {
            for (Map.Entry<PlayerStat, Double> entry : i.itemStatData().entrySet()) {
                if (allTalismanStats.containsKey(entry.getKey())) {
                    allTalismanStats.replace(entry.getKey(), allTalismanStats.get(entry.getKey()) + entry.getValue());
                } else {
                    allTalismanStats.put(entry.getKey(), entry.getValue());
                }
            }
        }
        PlayerStatSourceDatabase.getInstance().addFullStatsFromSpecificSourceToMapping(player, PlayerStatSource.ACCESSORY, allTalismanStats, false);
        PlayerStatManager.setAllPlayerStats(player);
        /*
        PlayerCache cache = PlayerCache.from(player);
        Map<PlayerStat, Double> currentTalismanStats = cache.getTalismanStats();
        for (Map.Entry<PlayerStat, Double> entry : allTalismanStats.entrySet()) {
            changeSpecificStat(player, entry.getKey(), currentTalismanStats.getOrDefault(entry.getKey(), 0.0), entry.getValue());
        }
        for (Map.Entry<PlayerStat, Double> entry : currentTalismanStats.entrySet()) {
            if (!allTalismanStats.containsKey(entry.getKey())) {
                changeSpecificStat(player, entry.getKey(), entry.getValue(), 0.0);
            }
        }
        cache.clearTalismanStats();
        cache.setTalismanStats(allTalismanStats);
        */
    }

    /*
    private static void resetUpdatePlayerSpecificStat(Player player, PlayerStat stat, double addedValue) {
        PlayerCache cache = PlayerCache.from(player);
        try {
            String statName = stat.name().toLowerCase().replace("_", "");
            String methodName = "getBase" + statName.substring(0, 1).toUpperCase() + statName.substring(1);
            Method getBaseMethod = PlayerCache.class.getMethod(methodName);

            methodName = "setActive" + statName.substring(0, 1).toUpperCase() + statName.substring(1);
            Method setActiveMethod = PlayerCache.class.getMethod(methodName, double.class);

            double baseValue = (double) getBaseMethod.invoke(cache);
            double totalStatValue = baseValue + addedValue;
            setActiveMethod.invoke(cache, totalStatValue);
            if (stat == PlayerStat.HEALTH || stat == PlayerStat.INTELLIGENCE) {
                String methodName2 = "setActiveMax" + statName.substring(0, 1).toUpperCase() + statName.substring(1);
                Method setMaxMethod = PlayerCache.class.getMethod(methodName2, double.class);
                setMaxMethod.invoke(cache, totalStatValue);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    */

    /*
    private static void resetUpdatePlayerSpecificStat(Player player, PlayerStat stat, double addedValue) {
        PlayerCache cache = PlayerCache.from(player);
        try {
            String og = stat.name().toLowerCase().replace("_", "");
            String next = og.substring(0, 1).toUpperCase() + og.substring(1);
            String methodName = "setActive" + next;
            Method method = PlayerCache.class.getMethod(methodName, double.class);
            method.invoke(cache, cache.getBaseDamage() + addedValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    */

    private void updateAllStatsOnConnect(Player player) {
        PlayerCache cache = PlayerCache.from(player);
        double activeHealth;
        double activeDefense;
        double activeSpeed;
        double activeStrength;
        double activeMana;
        double activeCritChance;
        double activeCritDamage;
        double activeAbilityDamage;
        double activeMagicFind;
        double activeTrueDefense;
        double activeDungeonLuck;
        double activeHealthRegen;

        if (player.getEquipment() != null) {
            ArrayList<ItemStack> armor = new ArrayList<>();
            armor.add(player.getEquipment().getHelmet());
            armor.add(player.getEquipment().getChestplate());
            armor.add(player.getEquipment().getLeggings());
            armor.add(player.getEquipment().getBoots());

            CustomDungeonItemManager itemManager = CustomDungeonItemManager.getInstance();

            for (ItemStack item : armor) {
                if (item != null) {
                    if (itemManager.checkIsDungeonItem(item)) {
                        Map<PlayerStat, Double> stats = itemManager.findDungeonItem(item).itemStatData();
                        for (Map.Entry<PlayerStat,Double> entry : stats.entrySet()) {

                        }
                    }
                }
            }
        }

    }

    public static Map<PlayerStat, Double> getTotalOfSpecificStatsDungeonItems(Player player, List<PlayerStat> statTypes) {
        Map<PlayerStat, Double> stats = new HashMap<>();
        Map<PlayerStat, Double> comparable;
        ArrayList<AbstractDungeonItem> dungeonItems = new ArrayList<>();
        CustomDungeonItemManager itemManager = CustomDungeonItemManager.getInstance();
        EntityEquipment equipment = player.getEquipment();
        if (equipment != null) {
            if (equipment.getHelmet() != null) {
                if (itemManager.checkIsDungeonItem(equipment.getHelmet())) {
                    dungeonItems.add(itemManager.findDungeonItem(equipment.getHelmet()));
                }
            }
            if (equipment.getChestplate() != null) {
                if (itemManager.checkIsDungeonItem(equipment.getChestplate())) {
                    dungeonItems.add(itemManager.findDungeonItem(equipment.getChestplate()));
                }
            }
            if (equipment.getLeggings() != null) {
                if (itemManager.checkIsDungeonItem(equipment.getLeggings())) {
                    dungeonItems.add(itemManager.findDungeonItem(equipment.getLeggings()));
                }
            }
            if (equipment.getBoots() != null) {
                if (itemManager.checkIsDungeonItem(equipment.getBoots())) {
                    dungeonItems.add(itemManager.findDungeonItem(equipment.getBoots()));
                }
            }
        }
        if (itemManager.checkIsDungeonItem(player.getEquipment().getItemInMainHand())) {
            dungeonItems.add(itemManager.findDungeonItem(player.getEquipment().getItemInMainHand()));
        }
        for (PlayerStat statType : statTypes) {
            for (AbstractDungeonItem item : dungeonItems) {
                comparable = item.itemStatData();
                if (!stats.containsKey(statType)) {
                    stats.put(statType, comparable.getOrDefault(statType, 0.0));
                } else {
                    if (comparable.containsKey(statType)) {
                        stats.replace(statType, stats.get(statType) + comparable.get(statType));
                    }
                }
            }
        }
        return stats;
    }

    public static void setPlayerSpeed(Player player, double speed) {

    }

    private Class<?> getItemClass(String className, String itemType) {
        try {
            return Class.forName("org.ninenetwork.infinitedungeons.item." + itemType.toLowerCase() + "." + className);
        }

        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public HashMap getAllStatsFromItem(ItemStack item, Class<?> clazz) {
        Map<String,Double> statData = new HashMap<>();
        if (CompMetadata.hasMetadata(item, "Health")) {
            statData.put("Health", Double.parseDouble(CompMetadata.getMetadata(item, "Health")));
        }
        if (CompMetadata.hasMetadata(item, "Health")) {
            statData.put("Health", Double.parseDouble(CompMetadata.getMetadata(item, "Health")));
        }
        return null;
    }

}