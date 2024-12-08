package org.ninenetwork.infinitedungeons.party;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class DungeonQueue {

    @Getter
    private static final DungeonQueue dungeonQueue = new DungeonQueue();

    @Getter
    private List<DungeonParty> partiesInQueue = new ArrayList<>();

    @Getter
    private List<DungeonParty> allParties = new ArrayList<>();

    public DungeonQueue() {

    }

    public void disbandAllParties() {
        for (DungeonParty party : allParties) {

        }
    }

    public void addPartyToQueue(DungeonParty party) {
        if (!this.partiesInQueue.contains(party)) {
            if (!(party.getPlayers().size() >= 5)) {
                this.partiesInQueue.add(party);
            }
        }
    }

    public void addParty(DungeonParty party) {
        if (!this.allParties.contains(party)) {
            this.allParties.add(party);
        }
    }

    public void removePartyFromQueue(DungeonParty party) {
        if (this.partiesInQueue.contains(party)) {
            this.partiesInQueue.remove(party);
        }
    }

    public void removeParty(DungeonParty party) {
        if (this.allParties.contains(party)) {
            this.allParties.remove(party);
        }
    }

}