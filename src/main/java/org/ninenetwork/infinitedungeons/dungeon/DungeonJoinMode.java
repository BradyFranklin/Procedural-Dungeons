package org.ninenetwork.infinitedungeons.dungeon;

public enum DungeonJoinMode {

    PLAYING,

    EDITING,

    SPECTATING;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }

}