package org.ninenetwork.infinitedungeons.dungeon;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.*;
import org.bukkit.event.Cancellable;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;
import org.mineacademy.fo.*;
import org.mineacademy.fo.collection.StrictList;
import org.mineacademy.fo.exception.EventHandledException;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.model.*;
import org.mineacademy.fo.region.Region;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.remain.CompMetadata;
import org.mineacademy.fo.remain.Remain;
import org.mineacademy.fo.settings.ConfigItems;
import org.mineacademy.fo.settings.SimpleSettings;
import org.mineacademy.fo.settings.YamlConfig;
import org.mineacademy.fo.visual.VisualizedRegion;
import org.ninenetwork.infinitedungeons.PlayerCache;
import org.ninenetwork.infinitedungeons.dungeon.instance.DungeonRoomInstance;
import org.ninenetwork.infinitedungeons.dungeon.instance.DungeonRoomPoint;
import org.ninenetwork.infinitedungeons.party.DungeonParty;

import javax.annotation.Nullable;
import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Consumer;

@Getter
public abstract class Dungeon extends YamlConfig {

    private static final String FOLDER = "DungeonStorage/Dungeons";

    public static final String TAG_TELEPORTING = "Game_Teleporting";

    private static final ConfigItems<? extends Dungeon> loadedFiles = ConfigItems.fromFolder(FOLDER, fileName -> {
        final YamlConfig config = YamlConfig.fromFileFast(FileUtil.getFile(FOLDER + "/" + fileName + ".yml"));
        final DungeonType type = config.get("Type", DungeonType.class);

        Valid.checkNotNull(type, "Unrecognized DungeonType " + config.getObject("Type") + " in " + fileName + "! Available: " + Common.join(DungeonType.values()));
        return type.getInstanceClass();
    });

    private DungeonType type;
    private Region region;
    private Location lobbyLocation;
    private Location bloodLocation;
    private Location returnBackLocation;
    private List<DungeonRoomInstance> dungeonRooms;
    private SimpleTime lobbyDuration;
    private SimpleTime gameDuration;

    /* ------------------------------------------------------------------------------- */
    /* Local properties which are not saved to the dungeon settings file */
    /* ------------------------------------------------------------------------------- */

    private final StrictList<PlayerCache> players = new StrictList<>();
    private DungeonParty dungeonParty;
    private Countdown startCountdown;
    private Countdown heartbeat;
    private DungeonScoreboard scoreboard;
    private DungeonState state = DungeonState.STOPPED;
    private boolean stopping;
    private boolean starting;
    ArrayList<DungeonRoomPoint> pointTracking = new ArrayList<>();
    @Setter
    public boolean playersReady;
    @Setter
    public boolean generationComplete = false;

    int max11Square = 20;
    int max22Square = 2;
    int max111L = 2;
    int max12Rectangle = 3;
    int max13Rectangle = 3;
    int max14Rectangle = 1;

    @Setter
    ArrayList<Location> pointLocations = new ArrayList<>();

    @Setter
    private int dungeonScore;

    @Setter
    private int floor;

    Dungeon (String name) {
        this(name, null);
    }

    Dungeon (String name, @Nullable DungeonType type) {
        this.type = type;

        this.setHeader(
                Common.configLine(),
                "This file stores information about a single entry, such as an arena or a mob by its name.",
                Common.configLine() + "\n");

        this.loadConfiguration(NO_DEFAULT, FOLDER + "/" + name + ".yml");

        this.startCountdown = this.compileStartCountdown();
        this.heartbeat = this.compileHeartbeat();
        this.scoreboard = this.compileScoreboard();
    }

    protected Countdown compileStartCountdown() {
        return new DungeonCountdownStart(this);
    }

    protected DungeonHeartbeat compileHeartbeat() {
        return new DungeonHeartbeat(this);
    }

    protected DungeonScoreboard compileScoreboard() {
        return new DungeonScoreboard(this);
    }

    @Override
    protected void onLoad() {
        this.region = get("Region", Region.class, new Region(new Location(Bukkit.getWorld("Dungeon"), 0, 0, 0), new Location(Bukkit.getWorld("Dungeon"), 0, 0, 1))); // never null
        this.lobbyLocation = getLocation("Lobby_Location");
        this.bloodLocation = getLocation("Blood_Location");
        this.returnBackLocation = getLocation("Return_Back_Location");
        this.dungeonRooms = getList("Dungeon_Rooms", DungeonRoomInstance.class);
        this.lobbyDuration = getTime("Lobby_Duration", SimpleTime.from("15 seconds"));
        this.gameDuration = getTime("Game_Duration", SimpleTime.from("5 minutes"));

        if (this.type != null) {
            this.save();
        } else {
            this.type = get("Type", DungeonType.class);
        }
    }

    @Override
    protected void onSave() {
        this.set("Type", this.type);
        this.set("Region", this.region);
        this.set("Lobby_Location", this.lobbyLocation);
        this.set("Blood_Location", this.bloodLocation);
        this.set("Return_Back_Location", this.returnBackLocation);
        this.set("Dungeon_Rooms", this.dungeonRooms);
        this.set("Lobby_Duration", this.lobbyDuration);
        this.set("Game_Duration", this.gameDuration);
    }

    public void initializeDungeonPoints(ArrayList<Location> points) {
        ArrayList<DungeonRoomPoint> pointTracking = new ArrayList<>();
        int size = points.size();
        for (int i = 0; i < size; i++) {
            pointTracking.add(new DungeonRoomPoint(i, points.get(i), false));
        }
        this.pointTracking = pointTracking;
    }

    public boolean isLocationPartOfDungeon(Dungeon dungeon, Location location) {
        for (DungeonRoomPoint point : dungeon.getPointTracking()) {
            if (point.getCenterLocation().equals(location)) {
                return true;
            }
        }
        return false;
    }

    public DungeonRoomPoint findPointFromLocation(Location location) {
        for (DungeonRoomPoint point : this.pointTracking) {
            if (point.getCenterLocation().equals(location)) {
                return point;
            }
        }
        return null;
    }

    public final DungeonType getType() {
        return type;
    }

    public final Region getRegion() {
        return region;
    }

    public final void setRegion(Region region) {
        this.region = region;
        this.save();
    }

    public final void setDungeonRooms(List<DungeonRoomInstance> dungeonRooms) {
        this.dungeonRooms = dungeonRooms;
        this.save();
    }

    public final void addDungeonRooms(DungeonRoomInstance dungeonRoomInstance) {
        List<DungeonRoomInstance> dungeonRoomInstances = this.dungeonRooms;
        dungeonRoomInstances.add(dungeonRoomInstance);
        this.dungeonRooms = dungeonRoomInstances;
        this.save();;
    }

    public final Location getLobbyLocation() {
        return this.lobbyLocation;
    }

    public void setLobbyLocation(final Location location) {
        this.lobbyLocation = location;
        this.save();
    }

    public final Location getBloodLocation() {
        return this.bloodLocation;
    }

    public void setBloodLocation(Location bloodLocation) {
        this.bloodLocation = bloodLocation;
        this.save();
    }

    public final Location getReturnBackLocation() {
        return returnBackLocation;
    }

    public void setReturnBackLocation(Location returnBackLocation) {
        this.returnBackLocation = returnBackLocation;
        this.save();
    }

    public final SimpleTime getLobbyDuration() {
        return lobbyDuration;
    }

    public final SimpleTime getGameDuration() {
        return this.gameDuration;
    }

    public boolean isSetup() {
        return this.region.isWhole() && this.lobbyLocation != null;
    }

    public final Countdown getStartCountdown() {
        return this.startCountdown;
    }

    public final Countdown getHeartbeat() {
        return this.heartbeat;
    }

    public final DungeonScoreboard getScoreboard() {
        return this.scoreboard;
    }

    public final DungeonState getState() {
        return this.state;
    }

    public final boolean isStarting() {
        return this.starting;
    }

    public final boolean isStopping() {
        return this.stopping;
    }

    public abstract Location getRespawnLocation(Player player);

    public boolean canRestoreRegion() {
        return HookManager.isWorldEditLoaded() && MinecraftVersion.atLeast(MinecraftVersion.V.v1_13);
    }

    public final boolean isStopped() {
        return this.state == DungeonState.STOPPED;
    }

    public final boolean isEdited() {
        return this.state == DungeonState.EDITED;
    }

    public final boolean isPlayed() {
        return this.state == DungeonState.PLAYED;
    }

    public final boolean isBoss() {
        return this.state == DungeonState.BOSS;
    }

    public final boolean isLobby() {
        return this.state == DungeonState.LOBBY;
    }

    @Override
    public final String getName() {
        return super.getName();
    }

    /* ------------------------------------------------------------------------------- */
    /* Dungeon logic */
    /* ------------------------------------------------------------------------------- */

    public void initializeDungeonGeneration(DungeonParty party, DungeonType type, int dungeonFloor) {
        String name = party.getLeaderName();
        if (name != null) {
            Dungeon dungeon = Dungeon.createDungeon(name, type);
            DungeonGeneration generator = new DungeonGeneration();
            generator.preDungeonInitialization(dungeon, 7);
            generator.dungeonInitialization(dungeon, 7);
        } else {
            Common.log("Failed to initialize Dungeon");
        }
    }

    public void initializeDungeonLobby() {
        for (Player player : this.dungeonParty.getPlayers()) {
            player.teleport(this.lobbyLocation);
        }
    }

    public void start() {
        Valid.checkBoolean(this.state == DungeonState.LOBBY, "Cannot start dungeon " + this.getName() + " while in the " + this.state + " mode");

        this.state = DungeonState.PLAYED;
        this.starting = true;

        try {

            if (this.players.isEmpty()) {
                this.stop(DungeonStopReason.NOT_ENOUGH_PLAYERS);
                return;
            }

            this.cleanEntities();
            this.heartbeat.launch();
            this.scoreboard.onGameStart();

            // /game forcestart --> bypass lobby waiting
            if (this.startCountdown.isRunning())
                this.startCountdown.cancel();

            try {
                this.onDungeonStart();

            } catch (final Throwable t) {
                Common.error(t, "Failed to start dungeon " + this.getName() + ", stopping for safety");

                this.stop(DungeonStopReason.ERROR);
            }

            this.forEachInAllModes(cache -> {
                Player player = cache.toPlayer();
                // Close all players inventories
                player.closeInventory();
                this.onDungeonStartFor(player, cache);
            });

            try {
                this.onDungeonPostStart();
            } catch (final Throwable t) {
                Common.error(t, "Failed to start dungeon " + this.getName() + ", stopping for safety");
                this.stop(DungeonStopReason.ERROR);
            }

            this.broadcastInfo("Dungeon " + this.getName() + " starts now! Players: " + this.players.size());
            Common.log("Started dungeon " + this.getName());

        } finally {
            this.starting = false;
        }
    }

    public void stop(DungeonStopReason stopReason) {
        Valid.checkBoolean(this.state != DungeonState.STOPPED, "Cannot stop stopped dungeon " + this.getName());

        // Wrap in a try-finally block to properly clean the dungeon and set it back to stopped even on error
        try {
            this.stopping = true;

            if (this.startCountdown.isRunning())
                this.startCountdown.cancel();

            if (this.heartbeat.isRunning())
                this.heartbeat.cancel();

            final int playingPlayersCount = this.getPlayers(DungeonJoinMode.PLAYING).size();

            this.forEachPlayerInAllModes(player -> {

                String stopMessage = stopReason.getMessage();

                if (stopMessage != null) {
                    BoxedMessage.tell(player, "<center>&c&lDUNGEON OVER\n\n<center>&7"
                            + Replacer.replaceArray(stopMessage,
                            "game", this.getName(),
                            "players", playingPlayersCount));
                }

                else if (PlayerCache.from(player).getCurrentDungeonMode() == DungeonJoinMode.SPECTATING)
                    BoxedMessage.tell(player, "<center>&c&lDUNGEON OVER\n\n" +
                            "<center>&7This game has been ended.\n" +
                            "<center>&7Thank you for spectating.");

                this.leavePlayer(player, DungeonLeaveReason.GAME_STOP, false);
            });

            this.scoreboard.onGameStop();
            this.cleanEntities();

            try {
                this.onDungeonStop();
            } catch (final Throwable t) {
                Common.error(t, "Failed to properly stop dungeon " + this.getName());
            }

            /*
            if (this.destruction) {
                if (this.restoreWorld)
                    DungeonWorldManager.restoreWorld(this);
                else if (this.canRestoreRegion())
                    DungeonWorldManager.restoreRegion(this);
            }

             */

        } finally {
            this.state = DungeonState.STOPPED;
            this.players.clear();
            this.stopping = false;

            Common.log("Stopped dungeon " + this.getName());
        }
    }

    private void cleanEntities() {
        if (!this.region.isWhole()) {
            return;
        }
        final Set<String> ignoredEntities = Common.newSet("PLAYER", "ITEM_FRAME", "PAINTING", "ARMOR_STAND", "LEASH_HITCH", "ENDER_CRYSTAL");

        for (final Entity entity : this.region.getEntities()) {
            if (!ignoredEntities.contains(entity.getType().toString())) {
                //Common.log("Removing " + entity.getType() + " from game " + this.getName() + "[" + Common.shortLocation(entity.getLocation()) + "]");
                entity.remove();
            }
        }
    }

    public final boolean joinPlayer(final Player player, final DungeonJoinMode mode) {
        final PlayerCache cache = PlayerCache.from(player);

        if (!this.canJoin(player, mode)) {
            return false;
        }

        cache.setJoining(true);

        try {

            cache.clearTags();

            if (mode != DungeonJoinMode.EDITING) {

                cache.setPlayerTag("PreviousLocation", player.getLocation());

                PlayerUtil.storeState(player);

                this.teleport(player, this.lobbyLocation);

                cache.setPlayerTag("AllowGamemodeChange", true);
                PlayerUtil.normalize(player, true);
                cache.removePlayerTag("AllowGamemodeChange");
            }

            try {
                this.onDungeonJoin(player, mode);

            } catch (final Throwable t) {
                Common.error(t, "Failed to properly handle " + player.getName() + " joining the dungeon " + this.getName() + ", aborting");

                return false;
            }

            cache.setCurrentDungeonMode(mode);
            cache.setCurrentDungeonName(this.getName());

            this.players.add(cache);

            // Start countdown and change game mode
            if (this.state == DungeonState.STOPPED)
                if (mode == DungeonJoinMode.EDITING) {
                    Valid.checkBoolean(!this.startCountdown.isRunning(), "Dungeon start countdown already running for " + getName());

                    this.state = DungeonState.EDITED;
                    this.scoreboard.onEditStart();

                    this.onDungeonEditStart();

                } else {
                    Valid.checkBoolean(!this.startCountdown.isRunning(), "Dungeon start countdown already running for " + this.getName());

                    this.state = DungeonState.LOBBY;
                    this.scoreboard.onLobbyStart();

                    /*
                    if (!Settings.AutoMode.ENABLED)
                        this.startCountdown.launch();

                    this.onDungeonLobbyStart();

                    if (this.destruction) {
                        if (this.restoreWorld)
                            DungeonWorldManager.disableAutoSave(this);

                        else if (this.canRestoreRegion())
                            DungeonWorldManager.saveRegion(this);
                    }

                     */
                }

            Messenger.success(player, "You are now " + mode.toString().toLowerCase() + " dungeon '" + this.getName() + "'!");

            if (this.isLobby()) {
                this.broadcast("&6" + player.getName() + " &7has joined the dungeon! (" + this.players.size() + "/" + 5 + ")");
/*
                if (this.getPlayers(DungeonJoinMode.PLAYING).size() >= 0 && Settings.AutoMode.ENABLED) {
                    this.startCountdown.launch();

                    this.forEachPlayerInAllModes(otherPlayer -> Remain.sendTitle(otherPlayer,
                            "",
                            "&eDungeon Starts In " + Common.plural(this.startCountdown.getTimeLeft(), "second")));
                }

 */
            }

            this.scoreboard.onPlayerJoin(player);

            if (mode == DungeonJoinMode.SPECTATING)
                this.transformToSpectate(player);

            this.checkIntegrity();

        } finally {
            cache.setJoining(false);
        }
        return true;
    }

    public final boolean canJoin(Player player, DungeonJoinMode mode) {
        final PlayerCache cache = PlayerCache.from(player);

        if (cache.getCurrentDungeon() != null) {
            Messenger.error(player, "You are already joined in dungeon '" + cache.getCurrentDungeonName() + "'.");

            return false;
        }

        /*
        // Perhaps admins joining another player into an game?
        if (player.isDead()) {
            if (Settings.AutoMode.ENABLED) {
                Remain.respawn(player);
            }
            else {
                Messenger.error(player, "You cannot join dungeon '" + this.getName() + "' while you are dead.");
                return false;
            }
        }

        if (!Settings.AutoMode.ENABLED) {
            if (mode != DungeonJoinMode.EDITING && (player.isFlying() || player.getFallDistance() > 0)) {
                Messenger.error(player, "You cannot join dungeon '" + this.getName() + "' while you are flying.");
                return false;
            }

            if (mode != DungeonJoinMode.EDITING && player.getFireTicks() > 0) {
                Messenger.error(player, "You cannot join dungeon '" + this.getName() + "' while you are burning.");
                return false;
            }
        }

         */

        if (this.state == DungeonState.EDITED && mode != DungeonJoinMode.EDITING) {
            Messenger.error(player, "You cannot join dungeon '" + this.getName() + "' for play while it is being edited.");
            return false;
        }

        if ((this.state == DungeonState.LOBBY || this.state == DungeonState.PLAYED) && mode == DungeonJoinMode.EDITING) {
            Messenger.error(player, "You edit dungeon '" + this.getName() + "' for play while it is being played.");
            return false;
        }

        if (this.state != DungeonState.PLAYED && mode == DungeonJoinMode.SPECTATING) {
            Messenger.error(player, "Only dungeons that are being played may be spectated.");
            return false;
        }

        if (this.state == DungeonState.PLAYED && mode == DungeonJoinMode.PLAYING) {
            Messenger.error(player, "This dungeon has already started. Type '/dungeon spectate " + this.getName() + "' to observe.");
            return false;
        }

        if (!this.isSetup() && mode != DungeonJoinMode.EDITING) {
            Messenger.error(player, "Dungeon '" + this.getName() + "' is not yet configured. If you are an admin, run '/dungeon edit " + this.getName() + "' to see what's missing.");
            return false;
        }

        if (mode == DungeonJoinMode.PLAYING && this.players.size() >= 5) {
            Messenger.error(player, "Dungeon '" + this.getName() + "' is full (" + 5 + " players)!");
            return false;
        }

        /*
        if (DungeonWorldManager.isWorldBeingProcessed(this.region.getWorld())) {
            Messenger.error(player, "This dungeon is being processed right now.");
            return false;
        }

         */
        return true;
    }

    public final void leavePlayer(Player player, DungeonLeaveReason leaveReason) {
        this.leavePlayer(player, leaveReason, true);
    }

    private void leavePlayer(Player player, DungeonLeaveReason leaveReason, boolean stopIfLast) {
        final PlayerCache cache = PlayerCache.from(player);

        Valid.checkBoolean(!this.isStopped(), "Cannot leave player " + player.getName() + " from stopped dungeon!");
        Valid.checkBoolean(cache.hasDungeon() && cache.getCurrentDungeon().equals(this), "Player " + player.getName() + " is not joined in dungeon " + this.getName());

        cache.setLeaving(true);

        if (!this.getPlayers(DungeonJoinMode.PLAYING).isEmpty() && leaveReason.autoSpectateOnLeave() && this.canSpectateOnLeave(player)) {
            cache.setLeaving(false);

            this.transformToSpectate(player);
            this.onDungeonSpectate(player);
            this.broadcast("&6" + player.getName() + " &7has lost the dungeon and is now spectating!");

        } else {

            try {
                this.scoreboard.onPlayerLeave(player);

                // If onLeave uses players then move it below because this player will be removed at the point of calling onLeave!
                this.players.remove(cache);

                try {
                    this.onDungeonLeave(player);

                } catch (final Throwable t) {
                    Common.error(t, "Failed to properly handle " + player.getName() + " leaving dungeon " + this.getName() + ", stopping for safety");

                    if (!this.isStopped()) {
                        stop(DungeonStopReason.ERROR);

                        return;
                    }
                }

                if (!this.isEdited()) {
                    final Location previousLocation = cache.getPlayerTag("PreviousLocation");
                    Valid.checkNotNull(previousLocation, "Unable to locate previous location for player " + player.getName());

                    PlayerUtil.normalize(player, true);

                    Location respawnLocation = previousLocation;

                    if (Dungeon.findByLocation(previousLocation) != null && this.returnBackLocation != null)
                        respawnLocation = this.returnBackLocation;

                    this.teleportToReturnLocation(player, respawnLocation);

                    Common.runLater(2, () -> {
                        cache.setPlayerTag("AllowGamemodeChange", true);
                        PlayerUtil.restoreState(player);
                        cache.removePlayerTag("AllowGamemodeChange");
                    });
                }

                // If we are not stopping, remove from the map automatically
                if (this.getPlayers(DungeonJoinMode.PLAYING).isEmpty() && stopIfLast)
                    this.stop(DungeonStopReason.LAST_PLAYER_LEFT);

                if (!this.stopping)
                    Messenger.success(player, "You've left " + cache.getCurrentDungeonMode().toString().toLowerCase() + " the dungeon '" + this.getName() + "'!");

                if (cache.getCurrentDungeonMode() != DungeonJoinMode.SPECTATING)
                    this.broadcast("&6" + player.getName() + " &7has left the dungeon! (" + this.getPlayers(DungeonJoinMode.PLAYING).size() + "/" + 5 + ")");

            } finally {
                cache.setLeaving(false);
                cache.setCurrentDungeonMode(null);
                cache.setCurrentDungeonName(null);
                cache.clearTags();
            }
        }
    }

    public final void teleportToReturnLocation(Player player) {
        this.teleportToReturnLocation(player, Common.getOrDefault(this.returnBackLocation, player.getWorld().getSpawnLocation()));
    }

    private void teleportToReturnLocation(Player player, @NonNull Location location) {
        PlayerCache cache = PlayerCache.from(player);

        if (cache.hasPlayerTag("SwitchToEditing")) {
            cache.removePlayerTag("SwitchToEditing");

            return;
        }

        /*
        if (Settings.AutoMode.ENABLED)
            BungeeUtil.connect(player, Settings.AutoMode.RETURN_BACK_SERVER);
        else
            this.teleport(player, location);

         */
    }

    protected boolean canSpectateOnLeave(Player player) {
        return this.getPlayers(DungeonJoinMode.PLAYING).size() > 1;
    }

    protected void transformToSpectate(Player player) {
        PlayerCache cache = PlayerCache.from(player);

        cache.setCurrentDungeonMode(DungeonJoinMode.SPECTATING);

        // Normalize once again, clearing all items
        PlayerUtil.normalize(player, true);

        // Set invisibility
        forEachPlayerInAllModes(other -> other.hidePlayer(player));

        // Set adventure and flying
        cache.setPlayerTag("AllowGamemodeChange", true);
        player.setGameMode(GameMode.ADVENTURE);
        cache.removePlayerTag("AllowGamemodeChange");

        player.setAllowFlight(true);
        player.setFlying(true);

        // Teleport to the first living player
        final List<Player> playingPlayers = this.getBukkitPlayers(DungeonJoinMode.PLAYING);
        Valid.checkBoolean(!playingPlayers.isEmpty(), "Cannot spectate dungeon where there are no playing players! Found: " + playingPlayers);
        final Player randomPlayer = RandomUtil.nextItem(playingPlayers);

        this.teleport(player, randomPlayer.getPlayer().getLocation().add(0.5, 1, 0.5));
        player.setCompassTarget(randomPlayer.getLocation());

        /*
        // Give a special compass that opens a menu to select players to teleport to
        SpectatePlayersTool.getInstance().give(player, 4);

         */

        this.scoreboard.onSpectateStart();
    }

    /* ------------------------------------------------------------------------------- */
    /* Overridable methods */
    /* ------------------------------------------------------------------------------- */

    protected void onDungeonJoin(Player player, DungeonJoinMode mode) {
    }

    protected void onDungeonSpectate(Player player) {
    }

    protected void onDungeonLeave(Player player) {
    }

    protected void onDungeonEditStart() {
    }

    protected void onDungeonLobbyStart() {
    }

    protected void onDungeonStart() {
    }

    protected void onDungeonPostStart() {
    }

    protected void onDungeonStartFor(Player player, PlayerCache cache) {
    }

    protected void onDungeonStop() {
    }

    public void onPlayerChat(PlayerCache cache, AsyncPlayerChatEvent event) throws EventHandledException {
        final List<Player> dungeonPlayers = cache.getCurrentDungeon().getBukkitPlayersInAllModes();

        if (cache.getCurrentDungeonMode() == DungeonJoinMode.EDITING)
            this.returnHandled();

        event.getRecipients().removeIf(recipient -> !dungeonPlayers.contains(recipient));
        event.setFormat(Common.colorize("&8[&6" + cache.getCurrentDungeonName() + "&8] &f" + cache.getPlayerName() + "&8: &f" + event.getMessage()));
    }

    public void onPlayerDeath(PlayerCache cache, PlayerDeathEvent event) {
        Player player = event.getEntity();

        // Instantly skip Death Screen and respawn the player
        if (this.isLobby() || cache.getCurrentDungeonMode() == DungeonJoinMode.SPECTATING || this.canRespawn(player, cache))
            Remain.respawn(player);

        event.setDeathMessage(null);

        try {
            event.setKeepInventory(true);

        } catch (NoSuchMethodError err) {
            // Ancient MC versions
        }
    }

    protected boolean canRespawn(Player player, PlayerCache cache) {
        return true;
    }

    public void onPlayerRespawn(PlayerCache cache, PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        if (this.state == DungeonState.LOBBY) {
            event.setRespawnLocation(this.getLobbyLocation());
            return;
        }

        if (cache.getCurrentDungeonMode() == DungeonJoinMode.SPECTATING)
            this.transformToSpectate(player);

        Location respawnLocation = getRespawnLocation(player);
        Valid.checkNotNull(respawnLocation, "Unable to find respawn location for player " + player.getName());

        event.setRespawnLocation(respawnLocation);
    }

    public void onPlayerCommand(PlayerCache cache, PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        DungeonJoinMode mode = cache.getCurrentDungeonMode();

        if (mode == DungeonJoinMode.EDITING || player.isOp())
            returnHandled();

        final String label = event.getMessage().split(" ")[0];
        this.checkBoolean(label.equals("/#flp")
                || Valid.isInList(label, SimpleSettings.MAIN_COMMAND_ALIASES), player, "You cannot execute this command while playing.");
    }

    public void onPlayerInteract(PlayerCache cache, PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final DungeonJoinMode mode = cache.getCurrentDungeonMode();

        if (mode == DungeonJoinMode.EDITING)
            this.returnHandled();

        if (mode == DungeonJoinMode.SPECTATING)
            this.cancelEvent();

        final boolean isBoat = event.hasItem() && CompMaterial.isBoat(event.getItem().getType());
        final boolean isSoil = event.hasBlock() && event.getClickedBlock().getType() == CompMaterial.FARMLAND.getMaterial();

        if (isBoat || isSoil) {
            player.updateInventory();
            this.cancelEvent();
        }
    }

    public void onPvP(Player attacker, Player victim, EntityDamageByEntityEvent event) {

    }

    public void onPlayerDamage(Player attacker, Entity victim, EntityDamageByEntityEvent event) {

    }

    public void onDamage(Entity attacker, Entity victim, EntityDamageByEntityEvent event) {

    }

    public void onDamage(Entity victim, EntityDamageEvent event) {

    }

    public void onExplosion(Location centerLocation, List<Block> blocks, Cancellable event) {
        if (!this.isPlayed())
            cancelEvent();

        if (event instanceof EntityExplodeEvent)
            ((EntityExplodeEvent) event).setYield(0F);

        try {
            if (event instanceof BlockExplodeEvent) // 1.8.8
                ((BlockExplodeEvent) event).setYield(0F);

        } catch (final Throwable t) {
            // Old MC
        }
    }

    public void onPlayerKill(Player killer, LivingEntity victim, EntityDeathEvent event) {

    }

    public void onPlayerPickupItem(Player player, PlayerCache cache, Item item) {

    }

    public void onBlockPlace(Player player, Block block, BlockPlaceEvent event) {
        this.cancelEvent();
    }

    public void onBlockBreak(Player player, Block block, BlockBreakEvent event) {
        this.cancelEvent();
    }

    public void onEntityClick(Player player, Entity entity, PlayerInteractEntityEvent event) {
        if (entity instanceof ItemFrame)
            this.cancelEvent();
    }

    public void onItemSpawn(Item item, ItemSpawnEvent event) {

    }

    public void onBedEnter(PlayerCache cache, PlayerBedEnterEvent event) {
        this.cancelEvent();
    }

    public void onPlayerInventoryClick(PlayerCache cache, InventoryClickEvent event) {
        // TODO add exception for clicking your own custom items
        if (!this.isPlayed()) {
            this.cancelEvent();
        }
    }

    public void onPlayerInventoryDrag(PlayerCache cache, InventoryDragEvent event) {
        // TODO add exception for clicking your own custom items
        if (!this.isPlayed()) {
            this.cancelEvent();
        }
    }

    /* ------------------------------------------------------------------------------- */
    /* Messaging */
    /* ------------------------------------------------------------------------------- */

    public final void broadcastInfo(final String message) {
        this.checkIntegrity();
        this.forEachPlayerInAllModes(player -> Messenger.info(player, message));
    }

    public final void broadcastWarn(final String message) {
        this.checkIntegrity();
        this.forEachPlayerInAllModes(player -> Messenger.warn(player, message));
    }

    public final void broadcast(final String message) {
        this.checkIntegrity();
        this.forEachPlayerInAllModes(player -> Common.tellNoPrefix(player, message));
    }

    public final void preventLeave(FallingBlock falling) {
        final Runnable tracker = () -> {
            final Location newLocation = falling.getLocation();
            final Dungeon newDungeon = Dungeon.findByLocation(newLocation);

            if (newDungeon == null || !newDungeon.equals(this)) {
                falling.remove();
            }
        };

        EntityUtil.track(falling, 10 * 20, tracker, tracker);
    }

    /**
     * Teleport the player to the given location, or to the fallback location if failed
     */
    public final void teleport(final Player player, @NonNull final Location location) {
        Valid.checkBoolean(player != null && player.isOnline(), "Cannot teleport offline players!");

        final Location topOfTheBlock = location.getBlock().getLocation().add(0.5, 1, 0.5);
        final PlayerCache cache = PlayerCache.from(player);

        // Support teleporting when the player is dead and respawning
        if (cache.hasPlayerTag("Respawning")) {
            PlayerRespawnEvent event = cache.getPlayerTag("Respawning");

            event.setRespawnLocation(topOfTheBlock);

        } else {
            Valid.checkBoolean(!player.isDead(), "Cannot teleport dead player " + player.getName());

            // Since we prevent players escaping the game, add a special invisible tag
            // that we use to check if we can actually enable the teleportation
            CompMetadata.setTempMetadata(player, TAG_TELEPORTING);

            final boolean success = player.teleport(topOfTheBlock, PlayerTeleportEvent.TeleportCause.PLUGIN);
            Valid.checkBoolean(success, "Failed to teleport " + player.getName() + " to both primary and fallback location, they may get stuck in the dungeon!");

            // Remove the tag after the teleport. Also remove in case of failure to clear up
            CompMetadata.removeTempMetadata(player, TAG_TELEPORTING);
        }
    }

    /**
     * Run a function for all players in the game regardless of their mode
     */
    protected final void forEachPlayerInAllModes(final Consumer<Player> consumer) {
        this.forEachPlayer(consumer, null);
    }

    /**
     * Run a function for each players having the given mode
     */
    protected final void forEachPlayer(final Consumer<Player> consumer, final DungeonJoinMode mode) {
        for (final PlayerCache player : this.getPlayers(mode))
            consumer.accept(player.toPlayer());
    }

    protected final void forEachInAllModes(final Consumer<PlayerCache> consumer) {
        this.forEach(consumer, null);
    }

    protected final void forEach(final Consumer<PlayerCache> consumer, final DungeonJoinMode mode) {
        for (final PlayerCache player : this.getPlayers(mode))
            consumer.accept(player);
    }

    public final boolean isJoined(Player player) {
        for (final PlayerCache otherCache : this.players)
            if (otherCache.getUniqueId().equals(player.getUniqueId())) {
                return true;
            }
        return false;
    }

    public final boolean isJoined(PlayerCache cache) {
        return this.players.contains(cache);
    }

    public final List<Player> getBukkitPlayersInAllModes() {
        return this.getBukkitPlayers(null);
    }

    public final List<Player> getBukkitPlayers(final DungeonJoinMode mode) {
        return Common.convert(this.getPlayers(mode), PlayerCache::toPlayer);
    }

    public final List<PlayerCache> getPlayersInAllModes() {
        return Collections.unmodifiableList(this.players.getSource());
    }

    public final List<PlayerCache> getPlayers(@Nullable final DungeonJoinMode mode) {
        final List<PlayerCache> foundPlayers = new ArrayList<>();
        for (final PlayerCache otherCache : this.players)
            if (!otherCache.isLeaving() && (mode == null || (otherCache.hasDungeon() && otherCache.getCurrentDungeonMode() == mode))) {
                foundPlayers.add(otherCache);
            }
        return Collections.unmodifiableList(foundPlayers);
    }

    public final PlayerCache findPlayer(final Player player) {
        this.checkIntegrity();
        for (final PlayerCache otherCache : this.players) {
            if (otherCache.hasDungeon() && otherCache.getCurrentDungeon().equals(this) && otherCache.getUniqueId().equals(player.getUniqueId()))
                return otherCache;
        }
        return null;
    }

    protected final void checkBoolean(boolean value, Player player, String errorMessage) {
        if (!value) {
            Messenger.error(player, errorMessage);
            this.cancelEvent();
        }
    }

    protected final void cancelEvent() {
        throw new EventHandledException(true);
    }

    protected final void returnHandled() {
        throw new EventHandledException(false);
    }

    private void checkIntegrity() {

        if (this.state == DungeonState.STOPPED)
            Valid.checkBoolean(this.players.isEmpty(), "Found players in stopped " + this.getName() + " dungeon: " + this.players);
        int playing = 0, editing = 0, spectating = 0;

        for (final PlayerCache cache : this.players) {
            final Player player = cache.toPlayer();
            final DungeonJoinMode mode = cache.getCurrentDungeonMode();

            Valid.checkBoolean(player != null && player.isOnline(), "Found a disconnected player " + player + " in dungeon " + this.getName());

            if (mode == DungeonJoinMode.PLAYING)
                playing++;

            else if (mode == DungeonJoinMode.EDITING)
                editing++;

            else if (mode == DungeonJoinMode.SPECTATING)
                spectating++;
        }

        if (editing > 0) {
            Valid.checkBoolean(this.state == DungeonState.EDITED, "Dungeon " + this.getName() + " must be in EDIT mode not " + this.state + " while there are " + editing + " editing players!");
            Valid.checkBoolean(playing == 0 && spectating == 0, "Found " + playing + " and " + spectating + " players in edited dungeon " + this.getName());
        }
    }

    /* ------------------------------------------------------------------------------- */
    /* Dungeon Retrieval Methods */
    /* ------------------------------------------------------------------------------- */

    public static Dungeon createDungeon(@NonNull final String name, @NonNull final DungeonType type) {
        return loadedFiles.loadOrCreateItem(name, () -> type.instantiate(name));
    }

    public static void loadDungeons() {
        loadedFiles.loadItems();
    }

    public static void removeDungeon(final String dungeonName) {
        loadedFiles.removeItemByName(dungeonName);
    }

    public static boolean isDungeonLoaded(final String name) {
        return loadedFiles.isItemLoaded(name);
    }

    public static Dungeon findByName(@NonNull final String name) {
        return loadedFiles.findItem(name);
    }

    public static List<Dungeon> findByType(final DungeonType type) {
        final List<Dungeon> items = new ArrayList<>();

        for (final Dungeon item : getDungeons())
            if (item.getType() == type) {
                items.add(item);
            }

        return items;
    }

    public static Dungeon findByLocation(final Location location) {
        for (final Dungeon dungeon : getDungeons())
            if (dungeon.getRegion().isWhole() && dungeon.getRegion().isWithin(location))
                return dungeon;

        return null;
    }

    public static Dungeon findByPlayer(final Player player) {
        return PlayerCache.from(player).getCurrentDungeon();
    }

    public static Collection<? extends Dungeon> getDungeons() {
        return loadedFiles.getItems();
    }

    public static Set<String> getDungeonNames() {
        return loadedFiles.getItemNames();
    }

}