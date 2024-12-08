package org.ninenetwork.infinitedungeons.dungeon;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.ninenetwork.infinitedungeons.PlayerCache;

@Getter
@Setter
public class DungeonIntegrity {

    private Dungeon dungeon;

    private boolean integrityMaintained;

    private int retry;

    private DungeonIntegrityStatus lobbyBloodInitialization;
    private DungeonIntegrityStatus gridGeneration;
    private DungeonIntegrityStatus dungeonRoomInstancing;
    private DungeonIntegrityStatus bloodPathfinding;
    private DungeonIntegrityStatus dungeonPathfinding;
    private DungeonIntegrityStatus schematicPasting;
    private DungeonIntegrityStatus mobsInitialized;

    public DungeonIntegrity(Dungeon dungeon) {
        this.dungeon = dungeon;
        this.integrityMaintained = true;
        this.retry = 0;
        this.lobbyBloodInitialization = DungeonIntegrityStatus.PRE;
        this.gridGeneration = DungeonIntegrityStatus.PRE;
        this.dungeonRoomInstancing = DungeonIntegrityStatus.PRE;
        this.bloodPathfinding = DungeonIntegrityStatus.PRE;
        this.schematicPasting = DungeonIntegrityStatus.PRE;
        this.dungeonPathfinding = DungeonIntegrityStatus.PRE;
        this.mobsInitialized = DungeonIntegrityStatus.PRE;
    }

    public void changeIntegrity(boolean integrity, String phase) {
        this.integrityMaintained = integrity;
        if (!this.integrityMaintained) {
            if (this.retry == 0) {
                Common.log("The dungeon " + this.dungeon.getName() + "'s generation failed at the " + phase + " stage... Retrying");
                restartDungeonGeneration(this.dungeon);
                retry++;
            } else {
                Common.log("The dungeon " + this.dungeon.getName() + "'s generation failed at the " + phase + " stage... Stopping (2nd Try)");
                stopDungeonGeneration(this.dungeon);
                for (PlayerCache cache : this.dungeon.getPlayerCaches()) {
                    Common.tell(cache.toPlayer(), "&c&lDUNGEONS >> &fDungeon generation &4failed&f, please try again. If problem persists, please contact an administrator.");
                }
            }
        }
    }

    private void restartDungeonGeneration(Dungeon dungeon) {

    }

    private void stopDungeonGeneration(Dungeon dungeon) {
        dungeon.stop(DungeonStopReason.COMMAND);
        for (PlayerCache cache : dungeon.getPlayerCaches()) {
            cache.setCurrentDungeonMode(null);
            cache.setCurrentDungeonName(null);
        }
    }

    private void clearFailedGeneration(Dungeon dungeon) {

    }

}