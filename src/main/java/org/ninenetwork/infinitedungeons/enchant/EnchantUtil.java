package org.ninenetwork.infinitedungeons.enchant;

public class EnchantUtil {

    public static Object getEnchantInstance(String name) {
        if (name.equalsIgnoreCase("QuickStrike")) {
            return QuickStrike.getInstance();
        }
        return null;
    }

}