package org.ninenetwork.infinitedungeons;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.collection.StrictMap;
import org.mineacademy.fo.constants.FoConstants;
import org.mineacademy.fo.exception.FoException;
import org.mineacademy.fo.remain.Remain;
import org.mineacademy.fo.settings.YamlConfig;
import org.mineacademy.fo.visual.VisualizedRegion;
import org.ninenetwork.infinitedungeons.dungeon.Dungeon;
import org.ninenetwork.infinitedungeons.dungeon.DungeonJoinMode;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Getter
public class PlayerCache extends YamlConfig {

    @Getter
    private static final Map<UUID, PlayerCache> cacheMap = new HashMap<>();

    @Setter
    private DungeonJoinMode currentDungeonMode;

    @Setter
    private String currentDungeonName;

    @Setter
    private boolean joining;

    @Setter
    private boolean leaving;

    private final VisualizedRegion region = new VisualizedRegion();

    private final StrictMap<String, Object> tags = new StrictMap<>();

    private final String playerName;
    private final UUID uniqueId;

    private boolean autoReady;
    private String currentViewing;

    private String dungeonClass;

    private int dungeonLevel;
    private double dungeonLevelingExp;

    //Party//
    private boolean isInParty;
    private int selectedFloor;
    private String groupNote;
    private String classLevelRequired;
    private String dungeonLevelRequired;

    //DungeonClassStats//

    private int healerLevel;
    private int mageLevel;
    private int berserkLevel;
    private int archerLevel;
    private int tankLevel;

    private double healerExp;
    private double mageExp;
    private double berserkExp;
    private double archerExp;
    private double tankExp;

    //PlayerDungeonStats//

    private double health;
    private double defense;
    private int speed;
    private double strength;
    private double mana;
    private int critChance;
    private double critDamage;
    private double abilityDamage;
    private double dungeonLuck;

    private PlayerCache(String playerName, UUID uuid) {
        this.playerName = playerName;
        this.uniqueId = uuid;

        this.setPathPrefix("Players." + uniqueId.toString());
        this.loadConfiguration(NO_DEFAULT, FoConstants.File.DATA);
    }

    @Override
    protected void onLoad() {
        this.dungeonClass = this.getString("Dungeon_Class", "Mage");

        this.dungeonLevel = this.getInteger("Dungeon_Level", 0);
        this.dungeonLevelingExp = this.getDouble("Dungeon_Exp", 0.0);

        this.healerLevel = this.getInteger("Healer_Level", 0);
        this.mageLevel = this.getInteger("Mage_Level", 0);
        this.berserkLevel = this.getInteger("Berserk_Level", 0);
        this.archerLevel = this.getInteger("Archer_Level", 0);
        this.tankLevel = this.getInteger("Tank_Level", 0);

        this.healerExp = this.getDouble("Healer_Exp", 0.0);
        this.mageExp = this.getDouble("Mage_Exp", 0.0);
        this.berserkExp = this.getDouble("Berserk_Exp", 0.0);
        this.archerExp = this.getDouble("Archer_Exp", 0.0);
        this.tankExp = this.getDouble("Tank_Exp", 0.0);

        this.autoReady = this.getBoolean("Auto_Ready", false);
        this.currentViewing = this.getString("Currently_Viewing", "Catacombs");

        this.isInParty = this.getBoolean("Is_Partied", false);
        this.selectedFloor = this.getInteger("Selected_Floor", 1);
        this.groupNote = this.getString("Group_Note", " ");
        this.classLevelRequired = this.getString("Class_Level_Required", "None!");
        this.dungeonLevelRequired = this.getString("Dungeon_Level_Required", "None!");
    }

    @Override
    protected void onSave() {
        this.set("Dungeon_Class", this.dungeonClass);

        this.set("Dungeon_Level", this.dungeonLevel);
        this.set("Dungeon_Exp", this.dungeonLevelingExp);

        this.set("Healer_Level", this.healerLevel);
        this.set("Mage_Level", this.mageLevel);
        this.set("Berserk_Level", this.berserkLevel);
        this.set("Archer_Level", this.archerLevel);
        this.set("Tank_Level", this.tankLevel);

        this.set("Healer_Exp", this.healerExp);
        this.set("Mage_Exp", this.mageExp);
        this.set("Berserk_Exp", this.berserkExp);
        this.set("Archer_Exp", this.archerExp);
        this.set("Tank_Exp", this.tankExp);

        this.set("Auto_Ready", this.autoReady);
        this.set("Currently_Viewing", this.currentViewing);

        this.set("Is_Partied", this.isInParty);
        this.set("Selected_Floor", this.selectedFloor);
        this.set("Group_Note", this.groupNote);
        this.set("Class_Level_Required", this.classLevelRequired);
        this.set("Dungeon_Level_Required", this.dungeonLevelRequired);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof PlayerCache && ((PlayerCache) obj).getUniqueId().equals(this.uniqueId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.uniqueId);
    }

    public Dungeon getCurrentDungeon() {
        if (this.hasDungeon()) {
            final Dungeon dungeon = Dungeon.findByName(this.currentDungeonName);
            Valid.checkNotNull(dungeon, "Found player " + this.playerName + " having unloaded dungeon " + this.currentDungeonName);
            return dungeon;
        }
        return null;
    }

    public boolean hasDungeon() {
        // Integrity check
        if ((this.currentDungeonName != null && this.currentDungeonMode == null) || (this.currentDungeonName == null && this.currentDungeonMode != null)) {
            throw new FoException("Current dungeon and current dungeon mode must both be set or both be null, " + this.getPlayerName() + " had dungeon" +
                    " " + this.currentDungeonName + " and mode " + this.currentDungeonMode);
        }
        return this.currentDungeonName != null;
    }

    public boolean hasPlayerTag(final String key) {
        return getPlayerTag(key);
    }

    public <T> T getPlayerTag(final String key) {
        final Object value = this.tags.get(key);

        return value != null ? (T) value : null;
    }

    public void setPlayerTag(final String key, final Object value) {
        this.tags.override(key, value);
    }

    public void removePlayerTag(final String key) {
        this.tags.remove(key);
    }

    public void clearTags() {
        this.tags.clear();
    }

    @Nullable
    public Player toPlayer() {
        final Player player = Remain.getPlayerByUUID(this.uniqueId);
        return player != null && player.isOnline() ? player : null;
    }

    public void removeFromMemory() {
        synchronized (cacheMap) {
            cacheMap.remove(this.uniqueId);
        }
    }

    @Override
    public String toString() {
        return "PlayerCache{" + this.playerName + ", " + this.uniqueId + "}";
    }

    ///////////////////////////////////////////////////////

    public void setDungeonClass(final String dungeonClass) {
        this.dungeonClass = dungeonClass;
        this.save();
    }

    public void setDungeonLevel(int dungeonLevel) {
        this.dungeonLevel = dungeonLevel;
        this.save();
    }

    public void setHealerLevel(int healerLevel) {
        this.healerLevel = healerLevel;
        this.save();
    }

    public void setMageLevel(int mageLevel) {
        this.mageLevel = mageLevel;
        this.save();
    }

    public void setBerserkLevel(int berserkLevel) {
        this.berserkLevel = berserkLevel;
        this.save();
    }

    public void setArcherLevel(int archerLevel) {
        this.archerLevel = archerLevel;
        this.save();
    }

    public void setTankLevel(int tankLevel) {
        this.tankLevel = tankLevel;
        this.save();
    }

    public void setHealerExp(double healerExp) {
        this.healerExp = healerExp;
        this.save();
    }

    public void setMageExp(double mageExp) {
        this.mageExp = mageExp;
        this.save();
    }

    public void setBerserkExp(double berserkExp) {
        this.berserkExp = berserkExp;
        this.save();
    }

    public void setArcherExp(double archerExp) {
        this.archerExp = archerExp;
        this.save();
    }

    public void setTankExp(double tankExp) {
        this.tankExp = tankExp;
        this.save();
    }

    public void setAutoReady(boolean autoReady) {
        this.autoReady = autoReady;
        this.save();
    }

    public void setCurrentViewing(String currentViewing) {
        this.currentViewing = currentViewing;
        this.save();
    }

    public void setInParty(boolean inParty) {
        this.isInParty = inParty;
        this.save();
    }

    public void setSelectedFloor(int selectedFloor) {
        this.selectedFloor = selectedFloor;
        this.save();
    }

    public void setGroupNote(String groupNote) {
        this.groupNote = groupNote;
        this.save();
    }

    public void setClassLevelRequired(String classLevelRequired) {
        this.classLevelRequired = classLevelRequired;
        this.save();
    }

    public void setDungeonLevelRequired(String dungeonLevelRequired) {
        this.dungeonLevelRequired = dungeonLevelRequired;
        this.save();
    }

    public static PlayerCache from(Player player) {
        synchronized (cacheMap) {
            final UUID uniqueId = player.getUniqueId();
            final String playerName = player.getName();
            PlayerCache cache = cacheMap.get(uniqueId);
            if (cache == null) {
                cache = new PlayerCache(playerName, uniqueId);
                cacheMap.put(uniqueId, cache);
            }
            return cache;
        }
    }

    public static PlayerCache from(final UUID uuid) {
        Player player = (Player) Bukkit.getOfflinePlayer(uuid);
        PlayerCache cache = cacheMap.get(uuid);
        if (cache == null) {
            cache = new PlayerCache(player.getName(), uuid);
            cacheMap.put(uuid, cache);
        }
        return cache;
    }

    public static void remove(Player player) {
        cacheMap.remove(player.getUniqueId());
    }

    public static void clearCaches() {
        synchronized (cacheMap) {
            cacheMap.clear();
        }
    }

}