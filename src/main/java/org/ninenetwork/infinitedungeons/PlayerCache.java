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
import org.ninenetwork.infinitedungeons.playerstats.PlayerStat;

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

    private int dungeonsCreated;

    private final VisualizedRegion region = new VisualizedRegion();

    private final StrictMap<String, Object> tags = new StrictMap<>();

    private Map<PlayerStat, Double> talismanStats = new HashMap<>();

    private Map<PlayerStat, Double> statMapper = new HashMap<>();

    private final String playerName;
    private final UUID uniqueId;

    private String dungeonRoomEditing;

    private boolean autoReady;
    private String currentViewing;

    private int dungeonLevel;
    private double dungeonLevelingExp;
    private double totalDungeonLevelExp;

    //Admin//
    private String roomCreatorSizeIdentifier;
    private String roomCreatorTypeIdentifier;

    //Party//
    private boolean isInParty;
    private int selectedFloor;
    private String groupNote;
    private int classLevelRequired;
    private int dungeonLevelRequired;
    private String partyInviter;
    private boolean isReady;

    //DungeonClassStats//

    private String currentDungeonClass;

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

    private double lastStoredHealth;

    private int forbiddenBlessing;

    private double baseDamage;
    private double baseHealth;
    private double baseDefense;
    private double baseSpeed;
    private double baseStrength;
    private double baseIntelligence;
    private double baseCritChance;
    private double baseCritDamage;
    private double baseAbilityDamage;
    private double baseMagicFind;
    private double baseTrueDefense;
    private double baseHealthRegen;
    private double baseAttackSpeed;
    private double baseFerocity;
    private double baseVitality;
    private double baseMending;

    private double activeDamage;
    private double activeHealth;
    private double activeMaxHealth;
    private double activeDefense;
    private double activeSpeed;
    private double activeStrength;
    private double activeIntelligence;
    private double activeMaxIntelligence;
    private double activeCritChance;
    private double activeCritDamage;
    private double activeAbilityDamage;
    private double activeMagicFind;
    private double activeTrueDefense;
    private double activeHealthRegen;
    private double activeAttackSpeed;
    private double activeFerocity;
    private double activeVitality;
    private double activeMending;
    private double activeManaRegen;

    private int witherEssence;
    private int undeadEssence;
    private int dragonEssence;
    private int iceEssence;

    private PlayerCache(String playerName, UUID uuid) {
        this.playerName = playerName;
        this.uniqueId = uuid;

        this.setPathPrefix("Players." + uniqueId.toString());
        this.loadConfiguration(NO_DEFAULT, FoConstants.File.DATA);
    }

    @Override
    protected void onLoad() {
        this.currentDungeonClass = this.getString("Dungeon_Class", "Mage");

        this.dungeonLevel = this.getInteger("Dungeon_Level", 0);
        this.dungeonLevelingExp = this.getDouble("Dungeon_Exp", 0.0);
        this.totalDungeonLevelExp = this.getDouble("Total_Dungeon_Exp", 0.0);
        this.dungeonsCreated = this.getInteger("Dungeons_Created", 0);
        this.dungeonRoomEditing = this.getString("Dungeon_Room_Editing", "none");

        this.talismanStats = this.getMap("Talisman_Stats", PlayerStat.class, Double.class);

        this.statMapper = this.getMap("Statistic_Mapping", PlayerStat.class, Double.class);

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
        this.classLevelRequired = this.getInteger("Class_Level_Required", 0);
        this.dungeonLevelRequired = this.getInteger("Dungeon_Level_Required", 0);
        this.partyInviter = this.getString("Party_Inviter", "None!");
        this.isReady = this.getBoolean("Is_Ready", false);

        this.roomCreatorSizeIdentifier = this.getString("Room_Creator_Size");
        this.roomCreatorTypeIdentifier = this.getString("Room_Creator_Type");

        this.lastStoredHealth = this.getDouble("Stored_Health", 0.0);

        this.forbiddenBlessing = this.getInteger("Forbidden_Blessing", 0);

        this.baseDamage = this.getDouble("Base_Damage", 0.0);
        this.activeDamage = this.getDouble("Active_Damage", 0.0);
        this.baseHealth = this.getDouble("Base_Health", 100.0);
        this.activeHealth = this.getDouble("Active_Health", 0.0);
        this.activeMaxHealth = this.getDouble("Active_Max_Health", 100.0);
        this.baseDefense = this.getDouble("Base_Defense", 0.0);
        this.activeDefense = this.getDouble("Active_Defense", 0.0);
        this.baseSpeed = this.getDouble("Base_Speed", 0.0);
        this.activeSpeed = this.getDouble("Active_Speed", 0.0);
        this.baseStrength = this.getDouble("Base_Strength", 0.0);
        this.activeStrength = this.getDouble("Active_Strength", 0.0);
        this.baseIntelligence = this.getDouble("Base_Intelligence", 0.0);
        this.activeIntelligence = this.getDouble("Active_Intelligence", 0.0);
        this.activeMaxIntelligence = this.getDouble("Active_Max_Intelligence", 0.0);
        this.baseCritChance = this.getDouble("Base_Crit_Chance", 30.0);
        this.activeCritChance = this.getDouble("Active_Crit_Chance", 0.0);
        this.baseCritDamage = this.getDouble("Base_Crit_Damage", 50.0);
        this.activeCritDamage = this.getDouble("Active_Crit_Damage", 0.0);
        this.baseAbilityDamage = this.getDouble("Base_Ability_Damage", 0.0);
        this.activeAbilityDamage = this.getDouble("Active_Ability_Damage", 0.0);
        this.baseMagicFind = this.getDouble("Base_Magic_Find", 0.0);
        this.activeMagicFind = this.getDouble("Active_Magic_Find", 0.0);
        this.baseTrueDefense = this.getDouble("Base_True_Defense", 0.0);
        this.activeTrueDefense = this.getDouble("Active_True_Defense", 0.0);
        this.baseHealthRegen = this.getDouble("Base_Health_Regen", 100.0);
        this.activeHealthRegen = this.getDouble("Active_Health_Regen", 100.0);
        this.baseAttackSpeed = this.getDouble("Base_Attack_Speed", 30.0);
        this.activeAttackSpeed = this.getDouble("Active_Attack_Speed", 30.0);
        this.baseFerocity = this.getDouble("Base_Ferocity", 0.0);
        this.activeFerocity = this.getDouble("Active_Ferocity", 0.0);
        this.baseVitality = this.getDouble("Base_Vitality", 100.0);
        this.activeVitality = this.getDouble("Active_Vitality", 0.0);
        this.baseMending = this.getDouble("Base_Mending", 100.0);
        this.activeMending = this.getDouble("Active_Mending", 0.0);
        this.activeManaRegen = this.getDouble("Active_Mana_Regen", 0.0);

        this.witherEssence = this.getInteger("Wither_Essence", 0);
        this.undeadEssence = this.getInteger("Undead_Essence", 0);
        this.dragonEssence = this.getInteger("Dragon_Essence", 0);
        this.iceEssence = this.getInteger("Ice_Essence", 0);
    }

    @Override
    protected void onSave() {
        this.set("Dungeon_Class", this.currentDungeonClass);

        this.set("Dungeon_Level", this.dungeonLevel);
        this.set("Dungeon_Exp", this.dungeonLevelingExp);
        this.set("Total_Dungeon_Exp", this.totalDungeonLevelExp);
        this.set("Dungeons_Created", this.dungeonsCreated);
        this.set("Dungeon_Room_Editing", this.dungeonRoomEditing);

        this.set("Talisman_Stats", this.talismanStats);
        this.set("Statistic_Mapping", this.statMapper);

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
        this.set("Party_Inviter", this.partyInviter);
        this.set("Is_Ready", this.isReady);

        this.set("Room_Creator_Size", this.roomCreatorSizeIdentifier);
        this.set("Room_Creator_Type", this.roomCreatorTypeIdentifier);

        this.set("Stored_Health", this.lastStoredHealth);

        this.set("Forbidden_Blessing", this.forbiddenBlessing);

        this.set("Base_Damage", this.baseDamage);
        this.set("Active_Damage", this.activeDamage);
        this.set("Base_Health", this.baseHealth);
        this.set("Active_Health", this.activeHealth);
        this.set("Active_Max_Health", this.activeMaxHealth);
        this.set("Base_Defense", this.baseDefense);
        this.set("Active_Defense", this.activeDefense);
        this.set("Base_Speed", this.baseSpeed);
        this.set("Active_Speed", this.activeSpeed);
        this.set("Base_Strength", this.baseStrength);
        this.set("Active_Strength", this.activeStrength);
        this.set("Base_Intelligence", this.baseIntelligence);
        this.set("Active_Intelligence", this.activeIntelligence);
        this.set("Active_Max_Intelligence", this.activeMaxIntelligence);
        this.set("Base_Crit_Chance", this.baseCritChance);
        this.set("Active_Crit_Chance", this.activeCritChance);
        this.set("Base_Crit_Damage", this.baseCritDamage);
        this.set("Active_Crit_Damage", this.activeCritDamage);
        this.set("Base_Ability_Damage", this.baseAbilityDamage);
        this.set("Active_Ability_Damage", this.activeAbilityDamage);
        this.set("Base_Magic_Find", this.baseMagicFind);
        this.set("Active_Magic_Find", this.activeMagicFind);
        this.set("Base_True_Defense", this.baseTrueDefense);
        this.set("Active_True_Defense", this.activeTrueDefense);
        this.set("Base_Health_Regen", this.baseHealthRegen);
        this.set("Active_Health_Regen", this.activeHealthRegen);
        this.set("Base_Attack_Speed", this.baseAttackSpeed);
        this.set("Active_Attack_Speed", this.activeAttackSpeed);
        this.set("Base_Ferocity", this.baseFerocity);
        this.set("Active_Ferocity", this.activeFerocity);
        this.set("Base_Vitality", this.baseVitality);
        this.set("Active_Vitality", this.activeVitality);
        this.set("Base_Mending", this.baseMending);
        this.set("Active_Mending", this.activeMending);
        this.set("Active_Mana_Regen", this.activeManaRegen);

        this.set("Wither_Essence", this.witherEssence);
        this.set("Dragon_Essence", this.dragonEssence);
        this.set("Undead_Essence", this.undeadEssence);
        this.set("Ice_Essence", this.iceEssence);
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
        return getPlayerTag(key) != null;
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

    public void setStatMapper(Map<PlayerStat, Double> stats) {
        this.statMapper = stats;
        this.save();
    }
    public void setCurrentDungeonClass(final String currentDungeonClass) {
        this.currentDungeonClass = currentDungeonClass;
        this.save();
    }
    public void setDungeonLevel(int dungeonLevel) {
        this.dungeonLevel = dungeonLevel;
        this.save();
    }
    public void setDungeonLevelingExp(double dungeonLevelingExp) {
        this.dungeonLevelingExp = dungeonLevelingExp;
        this.save();
    }
    public void setTotalDungeonLevelExp(double totalDungeonLevelExp) {
        this.totalDungeonLevelExp = totalDungeonLevelExp;
        this.save();
    }
    public void setDungeonsCreated(int dungeonsCreated) {
        this.dungeonsCreated = dungeonsCreated;
        this.save();
    }
    public void setDungeonRoomEditing(String dungeonRoomEditing) {
        this.dungeonRoomEditing = dungeonRoomEditing;
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
    public void setRoomCreatorSizeIdentifier(String roomCreatorSizeIdentifier) {
        this.roomCreatorSizeIdentifier = roomCreatorSizeIdentifier;
        this.save();
    }
    public void setRoomCreatorTypeIdentifier(String roomCreatorTypeIdentifier) {
        this.roomCreatorTypeIdentifier = roomCreatorTypeIdentifier;
        this.save();
    }
    public void setClassLevelRequired(int classLevelRequired) {
        this.classLevelRequired = classLevelRequired;
        this.save();
    }
    public void setDungeonLevelRequired(int dungeonLevelRequired) {
        this.dungeonLevelRequired = dungeonLevelRequired;
        this.save();
    }
    public void setPartyInviter(String partyInviter) {
        this.partyInviter = partyInviter;
        this.save();
    }
    public void setReady(boolean ready) {
        this.isReady = ready;
        this.save();
    }
    public void setLastStoredHealth(double lastStoredHealth) {
        this.lastStoredHealth = lastStoredHealth;
        this.save();
    }
    public void setForbiddenBlessing(int forbiddenBlessing) {
        this.forbiddenBlessing = forbiddenBlessing;
        this.save();
    }
    public void setBaseDamage(double baseDamage) {
        this.baseDamage = baseDamage;
        this.save();
    }
    public void setActiveDamage(double activeDamage) {
        this.activeDamage = activeDamage;
        this.save();
    }
    public void setBaseHealth(double baseHealth) {
        this.baseHealth = baseHealth;
        this.save();
    }
    public void setActiveHealth(double activeHealth) {
        this.activeHealth = activeHealth;
        this.save();
    }
    public void setActiveMaxHealth(double activeMaxHealth) {
        this.activeMaxHealth = activeMaxHealth;
        this.save();
    }
    public void setBaseDefense(double baseDefense) {
        this.baseDefense = baseDefense;
        this.save();
    }
    public void setActiveDefense(double activeDefense) {
        this.activeDefense = activeDefense;
        this.save();
    }
    public void setBaseSpeed(double baseSpeed) {
        this.baseSpeed = baseSpeed;
        this.save();
    }
    public void setActiveSpeed(double activeSpeed) {
        this.activeSpeed = activeSpeed;
        this.save();
    }
    public void setBaseStrength(double baseStrength) {
        this.baseStrength = baseStrength;
        this.save();
    }
    public void setActiveStrength(double activeStrength) {
        this.activeStrength = activeStrength;
        this.save();
    }
    public void setBaseIntelligence(double baseIntelligence) {
        this.baseIntelligence = baseIntelligence;
        this.save();
    }
    public void setActiveIntelligence(double activeIntelligence) {
        this.activeIntelligence = activeIntelligence;
        this.save();
    }

    public void setActiveMaxIntelligence(double activeMaxIntelligence) {
        this.activeMaxIntelligence = activeMaxIntelligence;
        this.save();
    }

    public void setBaseCritChance(double baseCritChance) {
        this.baseCritChance = baseCritChance;
        this.save();
    }
    public void setActiveCritChance(double activeCritChance) {
        this.activeCritChance = activeCritChance;
        this.save();
    }
    public void setBaseCritDamage(double baseCritDamage) {
        this.baseCritDamage = baseCritDamage;
        this.save();
    }
    public void setActiveCritDamage(double activeCritDamage) {
        this.activeCritDamage = activeCritDamage;
        this.save();
    }
    public void setBaseAbilityDamage(double baseAbilityDamage) {
        this.baseAbilityDamage = baseAbilityDamage;
        this.save();
    }
    public void setActiveAbilityDamage(double activeAbilityDamage) {
        this.activeAbilityDamage = activeAbilityDamage;
        this.save();
    }
    public void setBaseMagicFind(double baseMagicFind) {
        this.baseMagicFind = baseMagicFind;
        this.save();
    }
    public void setActiveMagicFind(double activeMagicFind) {
        this.activeMagicFind = activeMagicFind;
        this.save();
    }
    public void setBaseTrueDefense(double baseTrueDefense) {
        this.baseTrueDefense = baseTrueDefense;
        this.save();
    }
    public void setActiveTrueDefense(double activeTrueDefense) {
        this.activeTrueDefense = activeTrueDefense;
        this.save();
    }
    public void setBaseHealthRegen(double baseHealthRegen) {
        this.baseHealthRegen = baseHealthRegen;
        this.save();
    }
    public void setActiveHealthRegen(double activeHealthRegen) {
        this.activeHealthRegen = activeHealthRegen;
        this.save();
    }
    public void setBaseAttackSpeed(double baseAttackSpeed) {
        this.baseAttackSpeed = baseAttackSpeed;
        this.save();
    }
    public void setActiveAttackSpeed(double activeAttackSpeed) {
        this.activeAttackSpeed = activeAttackSpeed;
        this.save();
    }
    public void setBaseFerocity(double baseFerocity) {
        this.baseFerocity = baseFerocity;
        this.save();
    }
    public void setActiveFerocity(double activeFerocity) {
        this.activeFerocity = activeFerocity;
        this.save();
    }
    public void setBaseVitality(double baseVitality) {
        this.baseVitality = baseVitality;
        this.save();
    }
    public void setActiveVitality(double activeVitality) {
        this.activeVitality = activeVitality;
        this.save();
    }
    public void setBaseMending(double baseMending) {
        this.baseMending = baseMending;
        this.save();
    }
    public void setActiveMending(double activeMending) {
        this.activeMending = activeMending;
        this.save();
    }
    public void setActiveManaRegen(double activeManaRegen) {
        this.activeManaRegen = activeManaRegen;
        this.save();
    }
    public void setWitherEssence(int witherEssence) {
        this.witherEssence = witherEssence;
        this.save();
    }
    public void setUndeadEssence(int undeadEssence) {
        this.undeadEssence = undeadEssence;
        this.save();
    }
    public void setDragonEssence(int dragonEssence) {
        this.dragonEssence = dragonEssence;
        this.save();
    }
    public void setIceEssence(int iceEssence) {
        this.iceEssence = iceEssence;
        this.save();
    }
    public void clearTalismanStats() {
        this.talismanStats = new HashMap<>();
        this.save();
    }
    public void setTalismanStats(Map<PlayerStat, Double> taliStats) {
        this.talismanStats = taliStats;
        this.save();
    }
    public void addTalismanStat(PlayerStat stat, Double value) {
        boolean alreadyHas = false;
        for (Map.Entry<PlayerStat, Double> entry : this.getTalismanStats().entrySet()) {
            PlayerStat key = entry.getKey();
            if (key == stat) {
                alreadyHas = true;
                this.getTalismanStats().replace(entry.getKey(), entry.getValue() + value);
            }
        }
        if (!alreadyHas) {
            this.getTalismanStats().put(stat, value);
        }
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