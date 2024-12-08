package org.ninenetwork.infinitedungeons.party;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.ninenetwork.infinitedungeons.PlayerCache;
import org.ninenetwork.infinitedungeons.classes.DungeonClass;
import org.ninenetwork.infinitedungeons.classes.DungeonClassManager;
import org.ninenetwork.infinitedungeons.dungeon.DungeonType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
public class DungeonParty {

    private static final int MAX_PARTY_SIZE = 5;

    private Player leader;
    private List<Player> players;
    private DungeonType dungeonType;
    private int dungeonFloor;
    private DungeonPartyStatus status;
    private int dungeonLevelRequirement;
    private int classLevelRequirement;

    public DungeonParty(Player leader, DungeonType type) {
        PlayerCache cache = PlayerCache.from(leader);
        this.leader = leader;
        this.players = new ArrayList<>(Collections.singletonList(leader));
        this.dungeonType = type;
        this.dungeonFloor = cache.getSelectedFloor();
        this.status = DungeonPartyStatus.QUEUED;
        this.dungeonLevelRequirement = cache.getDungeonLevelRequired();
        this.classLevelRequirement = cache.getClassLevelRequired();
        DungeonQueue.getDungeonQueue().addPartyToQueue(this);
        DungeonQueue.getDungeonQueue().addParty(this);
    }

    public void joinPlayer(Player player) {
        if (canPlayerJoin(this, player)) {
            this.players.add(player);
            PlayerCache cache = PlayerCache.from(player);
            int level = DungeonClassManager.retrieveClassValue(player, DungeonClass.findClassByLabel(cache.getCurrentDungeonClass()), "Level");
            for (Player p : this.getPlayers()) {
                Common.tell(p, "&b" + player.getName() + " &fhas joined your dungeon party! &6" + cache.getCurrentDungeonClass() + " &8[&e" + level + "&8]");
            }
            if (this.players.size() >= 5) {
                this.setStatus(DungeonPartyStatus.FULL);
                DungeonQueue.getDungeonQueue().removeParty(this);
                for (Player p : this.getPlayers()) {
                    Common.tell(p, "Your dungeon party is full");
                }
            }
        }
    }

    public void removePlayer(DungeonParty party, Player player) {
        PlayerCache cache = PlayerCache.from(player);
        if (!player.getName().equals(party.getLeader().getName())) {
            if (party.getPlayers().contains(player)) {
                party.players.remove(player);
            }
            if (party.getStatus() == DungeonPartyStatus.FULL) {
                party.setStatus(DungeonPartyStatus.QUEUED);
                DungeonQueue.getDungeonQueue().addPartyToQueue(party);
            }
        }
    }

    public static boolean areSameParty(Player player, Player compare) {
        return findPartyByPlayer(player).getPlayers().contains(compare);
    }

    public void disbandParty(DungeonParty party) {
        for (Player player : party.getPlayers()) {
            Common.tell(player, party.getLeader().getName() + " has disbanded the dungeon party");
        }
        DungeonQueue.getDungeonQueue().removePartyFromQueue(party);
        DungeonQueue.getDungeonQueue().removeParty(party);
        party.setPlayers(null);
    }

    public boolean canPlayerJoin(DungeonParty party, Player player) {
        PlayerCache cache = PlayerCache.from(player);
        Player leader = party.getLeader();
        PlayerCache leaderCache = PlayerCache.from(leader);
        int level = DungeonClassManager.retrieveClassValue(player, DungeonClass.findClassByLabel(cache.getCurrentDungeonClass()), "Level");
        if (hasParty(player)) {
            if (party.getStatus() == DungeonPartyStatus.QUEUED) {
                if (cache.getDungeonLevel() >= leaderCache.getDungeonLevelRequired() && level >= leaderCache.getClassLevelRequired()) {
                    if (!(party.players.size() >= 5)) {
                        return true;
                    }
                } else {
                    Common.tell(player, "You do not meet the requirements to join this party.");
                }
            } else {
                Common.tell(player, "Parties must be queued in order to join.");
            }
        } else {
            Common.tell(player, "You are already in a party.");
        }
        return false;
    }

    public static boolean isLeader(Player player, DungeonParty party) {
        return party.getLeader() == player;
    }

    public static boolean hasParty(Player player) {
        for (DungeonParty party : DungeonQueue.getDungeonQueue().getAllParties()) {
            if (party.getPlayers().contains(player)) {
                return true;
            }
        }
        return false;
    }

    public static DungeonParty findPartyByPlayer(Player player) {
        for (DungeonParty party : DungeonQueue.getDungeonQueue().getAllParties()) {
            if (party.getPlayers().contains(player)) {
                return party;
            }
        }
        return null;
    }

    public static DungeonParty findPartyByLeader(Player leader) {
        for (DungeonParty party : DungeonQueue.getDungeonQueue().getAllParties()) {
            if (party.getLeader().getName().equals(leader.getName())) {
                return party;
            }
        }
        return null;
    }

}