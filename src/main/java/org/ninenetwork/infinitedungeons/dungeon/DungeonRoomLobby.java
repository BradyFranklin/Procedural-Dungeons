package org.ninenetwork.infinitedungeons.dungeon;

import lombok.Getter;
import org.bukkit.Location;

import javax.annotation.Nullable;

@Getter
public class DungeonRoomLobby extends DungeonRoom {

    private DungeonRoom instance;

    private Location spawnpoint;

    protected DungeonRoomLobby(String name) {
        super(name);
    }

    protected DungeonRoomLobby(String name, @Nullable DungeonRoomType type) {
        super(name, type);
    }

    DungeonRoomLobby(String name, @Nullable DungeonRoomType type, Location spawnpoint) {
        super(name, type);
    }

    @Override
    protected void onLoad() {
        this.spawnpoint = this.getLocation("Spawnpoint");

        super.onLoad();
    }

    @Override
    protected void onSave() {
        super.onSave();

        this.set("Spawnpoint", this.spawnpoint);
    }

    public void setSpawnpoint(Location location) {
        this.spawnpoint = location;
        this.save();
    }

}