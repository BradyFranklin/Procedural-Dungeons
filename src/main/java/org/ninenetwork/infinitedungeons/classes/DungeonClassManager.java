package org.ninenetwork.infinitedungeons.classes;

import org.bukkit.entity.Player;
import org.ninenetwork.infinitedungeons.PlayerCache;

import java.lang.reflect.Method;

public class DungeonClassManager {

    public static DungeonClass retrieveClass(Player player) {
        PlayerCache cache = PlayerCache.from(player);
        String currentSelection = cache.getCurrentDungeonClass();
        return DungeonClass.findClassByLabel(cache.getCurrentDungeonClass());
    }

    public static int retrieveClassValue(Player player, DungeonClass dungeonClass, String levelOrExp) {
        try {
            PlayerCache cache = PlayerCache.from(player);
            String classNameLabel = dungeonClass.label;
            String methodName = "get" + classNameLabel.substring(0, 1).toUpperCase() + classNameLabel.substring(1) + levelOrExp;
            Method method = PlayerCache.class.getMethod(methodName, int.class);
            return (int) method.invoke(cache);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

}