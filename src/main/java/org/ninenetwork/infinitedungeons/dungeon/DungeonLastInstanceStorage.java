package org.ninenetwork.infinitedungeons.dungeon;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.constants.FoConstants;
import org.mineacademy.fo.settings.ConfigItems;
import org.mineacademy.fo.settings.YamlConfig;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
public final class DungeonLastInstanceStorage extends YamlConfig {

    private static final ConfigItems<DungeonLastInstanceStorage> loadedInstances = ConfigItems.fromFolder("DungeonStorage/InstanceTracker",DungeonLastInstanceStorage.class);

    private final String name;

    private Location location;

    private DungeonLastInstanceStorage(Dungeon dungeon, @Nullable Location location) {
        this.name = dungeon.getName();
        this.location = location;
        this.setPathPrefix("Dungeons." + dungeon.getName());
        this.loadConfiguration(NO_DEFAULT, FoConstants.File.DATA);
    }

    @Override
    protected void onLoad() {
        if (this.location != null) {
            this.save();
            return;
        }
        this.location = getLocation("Instance_Location");
    }

    @Override
    protected void onSave() {
        this.set("Instance_Location", this.location);
    }

    @Nullable
    public Location getLocation() {
        return this.location.clone();
    }

    public static DungeonLastInstanceStorage addDungeonInstanceCenter(@NonNull final Dungeon dungeon, @NonNull final Location location) {
        return loadedInstances.loadOrCreateItem(dungeon.getName(), () -> new DungeonLastInstanceStorage(dungeon,location));
    }

    public static void loadLocations() {
        loadedInstances.loadItems();
    }

    public static void removeDungeonInstanceCenter(final DungeonLastInstanceStorage location) {
        loadedInstances.removeItem(location);
    }

    public static boolean isLocationLoaded(final String name) {
        return loadedInstances.isItemLoaded(name);
    }

    public static DungeonLastInstanceStorage findByLocation(@NonNull final Location location) {
        for (final DungeonLastInstanceStorage dungeonLastInstanceStorage : getLocations()) {
            if (Valid.locationEquals(dungeonLastInstanceStorage.getLocation(),location)) {
                return dungeonLastInstanceStorage;
            }
        }
        return null;
    }

    public static List<Location> getBukkitLocations() {
        return Common.convert(getLocations(),DungeonLastInstanceStorage::getLocation);
    }

    public static Collection<DungeonLastInstanceStorage> getLocations() {
        return loadedInstances.getItems();
    }

    public static Set<String> getLocationNames() {
        return loadedInstances.getItemNames();
    }

    public static Location findNextInstanceLocation() {
        double highestX = 0.0;
        double nextLocationXModifier = 150;
        int dungeonSpacingFromLastCenter = 150;
        if (!DungeonLastInstanceStorage.getLocations().isEmpty()) {
            for (DungeonLastInstanceStorage storage : getLocations()) {
                if (storage.getLocation() != null) {
                    if (storage.getLocation().getX() > highestX) {
                        highestX = storage.getLocation().getX();
                    }
                }
            }
        }
        if (highestX != 0.0) {
            return new Location(Bukkit.getWorld("Dungeon"), (highestX + nextLocationXModifier), 5, 0);
        } else {
            return new Location(Bukkit.getWorld("Dungeon"), 0, 5, 0);
        }
    }

}