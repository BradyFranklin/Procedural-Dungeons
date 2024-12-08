package org.ninenetwork.infinitedungeons.dungeon;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.mineacademy.fo.ReflectionUtil;

import java.lang.reflect.Constructor;

@RequiredArgsConstructor
public enum DungeonType {

    CATACOMBS("Catacombs", DungeonCatacombs.class),
    MASTER("MasterMode", DungeonMaster.class);

    @Getter
    private final String name;

    @Getter
    private final Class<? extends Dungeon> instanceClass;

    protected <T extends Dungeon> T instantiate(String name) {
        final Constructor<?> constructor = ReflectionUtil.getConstructor(this.instanceClass, String.class, DungeonType.class);
        return (T) ReflectionUtil.instantiate(constructor, name, this);
    }

}