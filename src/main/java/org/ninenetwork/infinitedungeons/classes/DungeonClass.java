package org.ninenetwork.infinitedungeons.classes;

import org.ninenetwork.infinitedungeons.enchant.DungeonEnchant;

public enum DungeonClass {

    MAGE("Mage"),
    HEALER("Healer"),
    BERSERK("Berserk"),
    ARCHER("Archer"),
    TANK("Tank");

    public final String label;

    private DungeonClass(String label) {
        this.label = label;
    }

    public static DungeonClass findClassByLabel(String label) {
        for (DungeonClass dungeonClass : DungeonClass.values()) {
            if (dungeonClass.label.equals(label)) {
                return dungeonClass;
            }
        }
        return null;
    }

}