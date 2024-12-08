package org.ninenetwork.infinitedungeons.dungeon;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Location;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.FileUtil;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.model.ConfigSerializable;
import org.mineacademy.fo.region.Region;
import org.mineacademy.fo.settings.ConfigItems;
import org.mineacademy.fo.settings.YamlConfig;
import org.ninenetwork.infinitedungeons.dungeon.instance.DungeonRoomPoint;

import java.io.File;
import java.util.*;

@Getter
public class DungeonRoom extends YamlConfig implements ConfigSerializable {

    private static final String FOLDER = "DungeonStorage/DungeonRooms";

    private static final String NBT_TAG = "InfiniteDungeonRoom";

    //private static ConfigItems<DungeonRoom> loadedDungeonRooms = ConfigItems.fromFolder(FOLDER, DungeonRoom.class);

    private static final ConfigItems<? extends DungeonRoom> loadedDungeonRooms = ConfigItems.fromFolder(FOLDER, fileName -> {
        final YamlConfig config = YamlConfig.fromFileFast(FileUtil.getFile(FOLDER + "/" + fileName + ".yml"));
        final DungeonRoomType type = config.get("Type", DungeonRoomType.class);

        Valid.checkNotNull(type, "Unrecognized DungeonType." + config.getObject("Type") + " in " + fileName + "! Available: " + Common.join(DungeonRoomType.values()));
        return type.getInstanceClass();
    });

    /* ------------------------------------------------------------------------------- */
    /* Stored properties saved to a file */
    /* ------------------------------------------------------------------------------- */

    private DungeonRoomType type;

    private List<String> schematics;

    private String roomIdentifier;

    private int roomRadius;

    private Region roomRegion;

    private String roomSizeTag;

    protected DungeonRoom (String name) {
        this(name, null);
    }

    public DungeonRoom (String name, DungeonRoomType type) {
        this.type = type;
        this.setHeader(
                Common.configLine(),
                "Stores Dungeon Room Information, DO NOT EDIT directly from file, only use in game editors if applicable",
                Common.configLine() + "\n");
        this.loadConfiguration(NO_DEFAULT, FOLDER + "/" + name + ".yml");
    }

    @Override
    protected void onLoad() {

        if (this.type == null) {
            Valid.checkBoolean(isSet("Type"), "Corrupted Dungeon Room File " + this.getFileName() + ", lacks the 'Type' key to determine Room Type for the Dungeon Room");
            this.type = get("Type", DungeonRoomType.class);
        }

        this.roomRadius = getInteger("Room_Radius", 25);
        this.roomSizeTag = getString("Size_Tag", "Tiny");
        this.roomRegion = get("Region", Region.class);
        this.roomIdentifier = getString("Room_Identifier");
        this.schematics = getStringList("Schematics");

        this.save();
    }

    @Override
    protected void onSave() {
        this.set("Type", this.type);
        this.set("Room_Radius", this.roomRadius);
        this.set("Size_Tag", this.roomSizeTag);
        this.set("Region", this.roomRegion);
        this.set("Room_Identifier", this.roomIdentifier);
        this.set("Schematics", this.schematics);
    }

    @Override
    public SerializedMap serialize() {
        return SerializedMap.ofArray(
                "Type", this.type,
                "Room_Radius", this.roomRadius,
                "Size_Tag", this.roomSizeTag,
                "Region", this.roomRegion,
                "Schematics", this.schematics,
                "Room_Identifier", this.roomIdentifier);
    }

    public static DungeonRoom deserialize(SerializedMap map, DungeonRoom instance) {
        DungeonRoom data = new DungeonRoom(instance.getName());

        data.type = map.get("Type", DungeonRoomType.class);
        data.roomRadius = map.getInteger("Room_Radius");
        data.roomSizeTag = map.getString("Size_Tag");
        data.roomRegion = map.get("Region", Region.class);
        data.roomIdentifier = map.getString("Room_Identifier");
        data.schematics = map.getStringList("Schematics");

        return data;
    }

    //public static DungeonRoom createDungeonRoom(@NonNull final String name, @NonNull final DungeonRoomType type) {
    //    return loadedDungeonRooms.loadOrCreateItem(name, () -> new DungeonRoom(name, type));
    //}

    public static DungeonRoom createDungeonRoom(@NonNull final String name, @NonNull final DungeonRoomType type) {
        return loadedDungeonRooms.loadOrCreateItem(name, () -> type.instantiate(name));
    }

    /*
    public static initializeAllBaseDungeonRooms() {
        for (File file : FileUtil.getFiles("DungeonStorage/Schematics/", ".schematic")) {
            if (file.getName().contains("1x1_Square")) {

            }
        }
    }
    */

    public void setSchematics(List<String> schematics) {
        this.schematics = schematics;
        this.save();
    }

    public void setRoomIdentifier(String roomIdentifier) {
        this.roomIdentifier = roomIdentifier;
        this.save();
    }

    public static void loadDungeonRooms() {
        loadedDungeonRooms.loadItems();
    }

    public static void removeDungeonRoom(final String dungeonName) {
        loadedDungeonRooms.removeItemByName(dungeonName);
    }

    public static boolean isDungeonRoomLoaded(final String name) {
        return loadedDungeonRooms.isItemLoaded(name);
    }

    /**
     * @param name
     * @return
     * @see ConfigItems#findItem(String)
     */
    public static DungeonRoom findByName(@NonNull final String name) {
        return loadedDungeonRooms.findItem(name);
    }

    /**
     *
     * @param type
     * @return
     */
    public static ArrayList<DungeonRoom> findByType(final DungeonRoomType type) {
        final ArrayList<DungeonRoom> items = new ArrayList<>();

        for (final DungeonRoom item : getDungeonRooms())
            if (item.getType() == type) {
                items.add(item);
            }

        return items;
    }

    public static ArrayList<DungeonRoom> findAllByIdentifier(final String roomIdentifier) {
        final ArrayList<DungeonRoom> items = new ArrayList<>();
        for (final DungeonRoom item : getDungeonRooms())
            if (item.getRoomIdentifier().equalsIgnoreCase(roomIdentifier)) {
                if (!item.getName().equalsIgnoreCase("Lobby") && !item.getName().equalsIgnoreCase("Blood")) {
                    items.add(item);
                }
            }
        return items;
    }

    /**
     * @return
     * @see ConfigItems#getItems()
     */
    public static Collection<? extends DungeonRoom> getDungeonRooms() {
        return loadedDungeonRooms.getItems();
    }

    /**
     * @return
     * @see ConfigItems#getItemNames()
     */
    public static Set<String> getDungeonRoomNames() {
        return loadedDungeonRooms.getItemNames();
    }

    /*
    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class DungeonRoomData implements ConfigSerializable {

        private final DungeonRoom instance;

        public void setPossibleDoorLocations(LocationList possibleDoorLocations) {
            this.possibleDoorLocations = possibleDoorLocations;

            this.instance.save();
        }

        @Override
        public SerializedMap serialize() {
            return SerializedMap.ofArray(
                    "Possible_Door_Locations", this.possibleDoorLocations);
        }

        public static DungeonRoomData deserialize(SerializedMap map, DungeonRoom instance) {
            DungeonRoomData data = new DungeonRoomData(instance);

            data.possibleDoorLocations = map.get("Possible_Door_Locations", LocationList.class);

            return data;
        }


    }

    */

}