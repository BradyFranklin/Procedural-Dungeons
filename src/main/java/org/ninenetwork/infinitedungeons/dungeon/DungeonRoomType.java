package org.ninenetwork.infinitedungeons.dungeon;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.mineacademy.fo.ReflectionUtil;

import java.lang.reflect.Constructor;

@RequiredArgsConstructor
public enum DungeonRoomType {

    LOBBY("Lobby", DungeonRoomLobby.class),
    BLOODRUSH("BloodRush", DungeonRoomBloodRush.class),
    PUZZLE("Puzzle", DungeonRoomPuzzle.class),
    BLOOD("Blood", DungeonRoomBlood.class);

    @Getter
    private final String name;

    @Getter
    private final Class<? extends DungeonRoom> instanceClass;

    protected <T extends DungeonRoom> T instantiate(String name) {
        final Constructor<?> constructor = ReflectionUtil.getConstructor(this.instanceClass, String.class, DungeonRoomType.class);
        return (T) ReflectionUtil.instantiate(constructor, name, this);
    }

}