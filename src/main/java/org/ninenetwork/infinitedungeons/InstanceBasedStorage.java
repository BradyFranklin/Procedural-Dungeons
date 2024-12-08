package org.ninenetwork.infinitedungeons;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.mineacademy.fo.constants.FoConstants;
import org.mineacademy.fo.settings.ConfigItems;
import org.mineacademy.fo.settings.YamlConfig;
import org.ninenetwork.infinitedungeons.dungeon.DungeonLastInstanceStorage;

import javax.annotation.Nullable;
import java.util.*;

@Getter
public class InstanceBasedStorage extends YamlConfig {

    @Getter
    private static final ConfigItems<InstanceBasedStorage> loadedInstances = ConfigItems.fromFolder("DungeonStorage/ServerStorage", InstanceBasedStorage.class);

    private List<Location> activeDungeons = new ArrayList<>();

    /*
    private InstanceBasedStorage(String playerName, UUID uuid) {
        this.playerName = playerName;
        this.uniqueId = uuid;

        this.setPathPrefix("Players." + uniqueId.toString());
        this.loadConfiguration(NO_DEFAULT, FoConstants.File.DATA);
    }

    @Override
    protected void onLoad() {
        if (this.location != null) {
            this.save();
            return;
        }
        this.location = getLocation("Position");
    }

    @Override
    protected void onSave() {
        this.set("Position",this.location);
    }
    */


}