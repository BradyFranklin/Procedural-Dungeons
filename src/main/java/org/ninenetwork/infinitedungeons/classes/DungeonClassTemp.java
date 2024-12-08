package org.ninenetwork.infinitedungeons.classes;

import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.model.ConfigSerializable;

import java.lang.reflect.Array;
import java.util.*;

public abstract class DungeonClass {

    private static final Map<String, DungeonClass> byName = new LinkedHashMap<>();

    public static DungeonClass MAGE = new Mage();
    public static DungeonClass HEALER = new Healer();
    public static DungeonClass ARCHER = new Archer();
    public static DungeonClass BERSERK = new Berserk();
    public static DungeonClass TANK = new Tank();

    private final String name;
    private final List<DungeonClassAbility> abilities;

    public DungeonClass(String name, ArrayList<DungeonClassAbility> abilities) {
        this.name = name;
        this.abilities = abilities;

        byName.put(name, this);
    }

    public static DungeonClass getByName(String className) {
        return byName.get(className);
    }

    public static Collection<DungeonClass> getClasses() {
        return byName.values();
    }

    public static Set<String> getClassNames() {
        return byName.keySet();
    }

}