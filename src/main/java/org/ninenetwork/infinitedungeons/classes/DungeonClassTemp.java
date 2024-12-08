package org.ninenetwork.infinitedungeons.classes;

import org.bukkit.entity.Player;

import java.util.*;

public abstract class DungeonClassTemp {

    private static final Map<String, DungeonClassTemp> byName = new LinkedHashMap<>();

    public static DungeonClassTemp MAGE = new Mage();
    public static DungeonClassTemp HEALER = new Healer();
    public static DungeonClassTemp ARCHER = new Archer();
    public static DungeonClassTemp BERSERK = new Berserk();
    public static DungeonClassTemp TANK = new Tank();

    private final String name;

    public DungeonClassTemp(String name) {
        this.name = name;

        byName.put(name, this);
    }

    public int getLevel(Player player) {
        return 0;
    }

    public static DungeonClassTemp getByName(String className) {
        return byName.get(className);
    }

    public static Collection<DungeonClassTemp> getClasses() {
        return byName.values();
    }

    public static Set<String> getClassNames() {
        return byName.keySet();
    }

}