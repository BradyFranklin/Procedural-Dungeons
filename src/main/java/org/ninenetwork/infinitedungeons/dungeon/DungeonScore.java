package org.ninenetwork.infinitedungeons.dungeon;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DungeonScore {

    private Dungeon dungeon;
    private double dungeonScore;
    private double dungeonScoreProjection;

    private double skillScore;
    private double exploreScore;
    private double speedScore;

    private int percentageCleared;

    private int clearedRooms;
    private int totalRooms;
    private int secretsFound;
    private int totalSecrets;

    private boolean mimic;
    private int cryptsCleared;

    private double secretRequirement;

    public DungeonScore(Dungeon dungeon) {
        this.dungeon = dungeon;
        this.dungeonScore = 0;
        this.skillScore = 0;
        this.exploreScore = 0;
        this.speedScore = 0;
        this.clearedRooms = 0;
        this.totalRooms = 0;
        this.secretsFound = 0;
        this.totalSecrets = 0;
        this.mimic = false;
        this.cryptsCleared = 0;
        this.secretRequirement = 0;
    }

    public void recalculateDungeonScore() {
        double skillScoreComplete = this.calculateSkillScore();
        int scoreProjection = (int) (this.calculateSkillScore() + this.calculateExploreScore() + this.calculateSpeedScore());
        int score = (int) (scoreProjection - skillScoreComplete);
        this.setDungeonScoreProjection(scoreProjection);
        this.setDungeonScore(score);
        this.setPercentageCleared((int) ((double) this.clearedRooms / (double) this.totalRooms * 100.0));
        //for (Player player : this.dungeon.getPlayersFromCaches(this.dungeon.getPlayerCaches())) {
        //}
    }

    private double calculateSkillScore() {
        // update on death/failed puzzle
        int deathPenalty = this.dungeon.getNumberDeaths() * 2;
        int puzzlePenalty = this.dungeon.getFailedPuzzles() * 14;
        if (deathPenalty != 0 || puzzlePenalty != 0) {
            return (100 - (this.dungeon.getNumberDeaths() * 2) - (this.dungeon.getFailedPuzzles() * 14));
        } else {
            return 100;
        }
    }

    private double calculateExploreScore() {
        // update on room clear, secret find
        double exploreRooms = (60.0 * this.clearedRooms) / this.totalRooms;
        double exploreSecrets = (40.0 * this.secretsFound) / (this.secretRequirement * totalSecrets);
        if (exploreSecrets > 40.0) {
            exploreSecrets = 40.0;
        }
        return exploreRooms + exploreSecrets;
    }

    private double calculateSpeedScore() {
        // update on time intervals
        double t = getTimeValue(this.dungeon.getType(), this.dungeon.getFloor(), this.dungeon.getDungeonTimeTracker().getElapsedTime());
        if (t < 480) {
            return 100.0;
        } else if (t >= 480 && t < 600) {
            return (140 - (1.0/12.0) * t);
        } else if (t >= 600 && t < 840) {
            return (115 - (1.0/24.0) * t);
        } else if (t >= 840 && t < 1140) {
            return (108 - (1.0/30.0) * t);
        } else if (t >= 1140 && t < 3940) {
            return (98.5 - (1.0/40.0) * t);
        } else {
            return 0.0;
        }
    }

    public void addClearedRoom() {
        this.clearedRooms = this.clearedRooms + 1;
    }

    public void addSecretFound() {
        this.secretsFound = this.secretsFound + 1;
    }

    public void setSecretRequirement(Dungeon dungeon) {
        this.secretRequirement = getSecretRequirement(dungeon.getType(), dungeon.getFloor(), this.totalSecrets);
    }

    private int getSecretRequirement(DungeonType dungeonType, int floor, int totalSecrets) {
        if (floor == 1 && dungeonType == DungeonType.CATACOMBS) {
            // 30%
            return (int) Math.floor(totalSecrets * .3);
        } else if (floor == 7 || dungeonType == DungeonType.MASTER) {
            return 1;
        } else if (floor > 1) {
            double diff = .3 + (floor * .1 - .1);
            if (floor == 6) {
                diff += .05;
            }
            return (int) Math.floor(totalSecrets * diff);
        }
        return 0;
    }

    private double getTimeValue(DungeonType dungeonType, int floor, int dungeonTimeSeconds) {
        if ((floor == 1 && dungeonType == DungeonType.CATACOMBS) || (floor == 2 && dungeonType == DungeonType.CATACOMBS) || (floor == 3 && dungeonType == DungeonType.CATACOMBS)
                || (floor == 5 && dungeonType == DungeonType.CATACOMBS) || (floor == 6 && dungeonType == DungeonType.MASTER)) {
            return (dungeonTimeSeconds - 120) < 0 ? 0 : (dungeonTimeSeconds - 120);
        } else if ((floor == 4 && dungeonType == DungeonType.CATACOMBS) || (floor == 6 && dungeonType == DungeonType.CATACOMBS)) {
            return (dungeonTimeSeconds - 240) < 0 ? 0 : (dungeonTimeSeconds - 240);
        } else if (floor == 7 ) {
            return (dungeonTimeSeconds - 360) < 0 ? 0 : (dungeonTimeSeconds - 360);
        } else if ((floor == 1 && dungeonType == DungeonType.MASTER) || (floor == 2 && dungeonType == DungeonType.MASTER) || (floor == 3 && dungeonType == DungeonType.MASTER)
                || (floor == 4 && dungeonType == DungeonType.MASTER) || (floor == 5 && dungeonType == DungeonType.MASTER)) {
            return dungeonTimeSeconds;
        }
        return 0.0;
    }


}