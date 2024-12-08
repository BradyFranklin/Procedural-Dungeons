package org.ninenetwork.infinitedungeons.dungeon;

import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.ItemUtil;
import org.mineacademy.fo.remain.Remain;
import org.mineacademy.fo.settings.SimpleSettings;
import org.ninenetwork.infinitedungeons.PlayerCache;

import java.util.List;

import static org.mineacademy.fo.Valid.checkBoolean;
import static org.mineacademy.fo.Valid.checkNotNull;

public class DungeonFunctions {

    public Dungeon createDungeon(String dungeonName, DungeonType type) {
        checkBoolean(!Dungeon.isDungeonLoaded(dungeonName), "Dungeon: '" + dungeonName + "' already exists!");
        Dungeon dungeon = Dungeon.createDungeon(dungeonName, type);
        Common.log("Created " + type.getName() + " dungeon '" + dungeonName + "'!");
        return dungeon;
    }

    public void removeDungeon(String dungeonName) {
        this.checkDungeonExists(dungeonName);
        Dungeon dungeon = Dungeon.findByName(dungeonName);
        if (!dungeon.isStopped()) {
            dungeon.stop(DungeonStopReason.COMMAND);
        }
        Dungeon.removeDungeon(dungeonName);
        Common.log("Removed dungeon '" + dungeonName + "'!");
    }

    public void dungeonJoinPlayers(Dungeon dungeon, List<Player> dungeonParty) {
        checkBoolean(dungeonParty.size() <= 5 && !dungeonParty.isEmpty());
        for (Player player : dungeonParty) {
            dungeon.joinPlayer(player, DungeonJoinMode.PLAYING);
        }
        dungeon.start();
    }

    public void generateDungeon(Dungeon dungeon) {
        DungeonGeneration generator = new DungeonGeneration();
        generator.preDungeonInitialization(dungeon, 7);
        generator.dungeonInitialization(dungeon, 7);
    }

    public void dungeonPlayerLeave(Player player) {
        final PlayerCache cache = PlayerCache.from(player);
        checkBoolean(cache.hasDungeon(), "You are not playing any dungeon right now.");
        cache.getCurrentDungeon().leavePlayer(player, DungeonLeaveReason.COMMAND);
    }

    public void dungeonTeleport(Player player, Dungeon dungeon) {
        dungeon.teleport(player, dungeon.getLobbyLocation());
    }

    public void startDungeon(Dungeon dungeon) {
        checkBoolean(dungeon.isLobby(), "Can only start dungeons in lobby! "
                + dungeon.getName() + " is " + ItemUtil.bountifyCapitalized(dungeon.getState()).toLowerCase() + ".");
        dungeon.start();
    }

    protected final Dungeon findDungeon(String name) {
        Dungeon dungeon = Dungeon.findByName(name);
        checkNotNull(dungeon, "No such dungeon: '" + name + "'. Available: " + Common.join(Dungeon.getDungeonNames()));
        return dungeon;
    }

    protected final void checkDungeonExists(String dungeonName) {
        checkBoolean(Dungeon.isDungeonLoaded(dungeonName),
                "No such dungeon: '" + dungeonName + "'. Available: " + Common.join(Dungeon.getDungeonNames()));
    }

}