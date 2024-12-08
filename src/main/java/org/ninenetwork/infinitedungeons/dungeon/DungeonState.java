package org.ninenetwork.infinitedungeons.dungeon;

public enum DungeonState {

    STOPPED,

    LOBBY,

    PLAYED,

    BOSS,

    EDITED;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }

}