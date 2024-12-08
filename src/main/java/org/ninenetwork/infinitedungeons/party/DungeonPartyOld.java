package org.ninenetwork.infinitedungeons.party;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.settings.ConfigItems;
import org.mineacademy.fo.settings.YamlConfig;
import org.ninenetwork.infinitedungeons.InfiniteDungeonsPlugin;
import org.ninenetwork.infinitedungeons.PlayerCache;

import java.util.*;

@Getter
@NoArgsConstructor
public class DungeonPartyOld extends YamlConfig {

    private static final ConfigItems<DungeonPartyOld> loadedPartys = ConfigItems.fromFolder("DungeonStorage/Partys", DungeonPartyOld.class);

    private Player leader;
    private String leaderName;

    private String type;
    private int floor;
    private int minimumRequiredLevel;

    private int partySize;
    private List<String> playerNames = new ArrayList<>();
    private List<Player> players = new ArrayList<>();
    public static final int MAX_PLAYERS = 5;

    private DungeonPartyOld(String name) {
        this(name, "CATACOMBS");
    }

    private DungeonPartyOld(String leaderName, String dungeonType) {
        this.leader = Bukkit.getPlayer(leaderName);
        this.leaderName = leaderName;
        this.type = dungeonType;
        setBasePartyInfo(leaderName);

        loadConfiguration(NO_DEFAULT, "DungeonStorage/Partys/" + leaderName + ".yml");
    }

    public void setBasePartyInfo(String leaderName) {
        Player player = Bukkit.getPlayer(leaderName);
        if (player != null) {
            PlayerCache cache = PlayerCache.from(player);
            this.floor = cache.getSelectedFloor();
            this.minimumRequiredLevel = cache.getDungeonLevelRequired();
            this.partySize = 1;
            this.playerNames = Arrays.asList(player.getName());
            this.players = Arrays.asList(player);
        }
    }

    @Override
    protected void onLoad() {
        if (this.leaderName != null) {
            return;
        }
        this.leader = get("Leader", Player.class);
        this.leaderName = getString("Leader_Name");
        this.type = getString("Type", "CATACOMBS");
        this.floor = getInteger("Floor", 1);
        this.minimumRequiredLevel = getInteger("Level_Requirement", 0);
        this.partySize = getInteger("Party_Size", 1);
        this.playerNames = getStringList("Players_Named");
        this.players = getList("Players", Player.class);
        this.save();
    }

    @Override
    protected void onSave() {
        this.set("Leader", this.leader);
        this.set("Leader_Name", this.leaderName);
        this.set("Type", this.type);
        this.set("Floor", this.floor);
        this.set("Level_Requirement", minimumRequiredLevel);
        this.set("Party_Size", this.partySize);
        this.set("Players_Named", this.playerNames);
        this.set("Players", this.players);
    }

    /**
     * Create And Delete Partys
     */
    public static DungeonPartyOld loadOrCreateDungeonParty(String leaderName, String dungeonType) {
        return loadedPartys.loadOrCreateItem(leaderName, () -> new DungeonPartyOld(leaderName, dungeonType));
    }

    public static void deleteDungeonParty(Player player) {
        Player p;
        PlayerCache cache;
        PlayerCache.from(player).setInParty(false);
        if (verifyPlayerPartyLeader(player)) {
            DungeonPartyOld party = findParty(player.getName());
            if (party != null && party.getCurrentMembers() != null) {
                for (String member : party.getCurrentMembers()) {
                    p = Bukkit.getPlayer(member);
                    if (p != null) {
                        cache = PlayerCache.from(p);
                        cache.setInParty(false);
                        Common.tellNoPrefix(p, "Your dungeon party was disbanded");
                    }
                }
            }
            loadedPartys.removeItem(Objects.requireNonNull(party));
        } else {
            Common.tellNoPrefix(player, "You do not currently have a dungeon party");
        }

    }

    public static void invitePlayerToParty(Player leader, Player guest) {
        PlayerCache cache = PlayerCache.from(guest);
        DungeonPartyOld party;
        if (verifyPlayerPartyLeader(leader)) {
            party = findParty(leader.getName());
            if (cache.getPartyInviter().equals(leader.getName())) {
                Common.tell(leader, "You have already invited this person. Please wait until the request times out.");
            } else {
                Common.tell(leader, "You invited " + guest.getName() + " to your party.");
                Common.tell(guest, leader.getName() + " has invited you to their dungeon party.");
                cache.setPartyInviter(leader.getName());
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!cache.getPartyInviter().equals("None")) {
                            Common.tell(leader, "Your dungeon invite to " + guest.getName() + " has timed out.");
                            Common.tell(guest, "Your dungeon invite from " + leader.getName() + " has timed out.");
                            if (cache.getPartyInviter().equals(leader.getName())) {
                                cache.setPartyInviter("None");
                            }
                        }
                    }
                }.runTaskLater(InfiniteDungeonsPlugin.getInstance(), 600L);
            }
        } else {
            Common.tell(leader, "You do not have a party. Please create a party queue before inviting.");
        }
    }

    public static void joinPlayerToParty(Player leader, Player guest) {
        PlayerCache cache = PlayerCache.from(guest);
        DungeonPartyOld party;
        if (verifyPlayerPartyLeader(leader)) {
            party = findParty(leader.getName());
            if (!cache.isInParty()) {
                if (cache.getPartyInviter().equals(leader.getName())) {
                    if (!(party.getPartySize() >= 5)) {
                        addPlayerToParty(leader.getName(), guest);
                        Common.tell(guest, "You have successfully joined " + leader.getName() + " 's party.");
                        for (Player player : party.getPlayers()) {
                            Common.tell(player, guest.getName() + "Has joined your dungeon party!");
                        }
                    } else {
                        Common.tell(guest, "This party is full.");
                    }
                } else {
                    Common.tell(guest, "You do not have a valid invite from " + leader.getName() + ". Please ask them for a new one.");
                }
            } else {
                Common.tell(guest, "You are already in a party.");
            }
        }
    }

    /**
     * Party Searches
     */

    public static Collection<DungeonPartyOld> getDungeonPartys() {
        return loadedPartys.getItems();
    }

    public static List<DungeonPartyOld> getDungeonPartysByFloor(int floor) {
        List<DungeonPartyOld> floorPartys = new ArrayList<>();
        for (DungeonPartyOld party : getDungeonPartys()) {
            if (party.getFloor() == floor) {
                floorPartys.add(party);
            }
        }
        return floorPartys;

    }

    public static DungeonPartyOld findParty(@NonNull String leaderName) {
        for (final DungeonPartyOld dungeonParty : getDungeonPartys()) {
            if (Valid.colorlessEquals(dungeonParty.getLeaderName(),leaderName)) {
                return dungeonParty;
            }
        }
        return null;
    }

    public static boolean verifyPlayerPartyLeader(Player player) {
        return findParty(player.getName()) != null;
    }

    public static boolean verifyPlayerInParty(Player player) {
        boolean playerPartied = false;
        for (DungeonPartyOld party : getDungeonPartys()) {
            if (party.getCurrentMembers().contains(player.getName())) {
                playerPartied = true;
            }
        }
        return playerPartied;
    }

    public static String findPartyMembersLeader(Player player) {
        String leader = null;
        if (verifyPlayerInParty(player)) {
            for (DungeonPartyOld party : getDungeonPartys()) {
                if (party.getCurrentMembers().contains(player.getName())) {
                    leader = party.getLeaderName();
                    return leader;
                }
            }
        }
        return null;
    }

    /**
     * Party Member Manipulation
     */
    public static void addPlayerToParty(String leaderName, Player addedPlayer) {
        PlayerCache cache = PlayerCache.from(addedPlayer);
        Player player = Bukkit.getPlayer(leaderName);
        if (player != null) {
            PlayerCache leaderCache = PlayerCache.from(player);
            if (!verifyPlayerInParty(addedPlayer)) {
                DungeonPartyOld party = findParty(leaderName);
                if (party.getPartySize() >= 5) {
                    Common.tell(addedPlayer, "Party is full and has been delisted");
                    return;
                }
                int requirement = leaderCache.getDungeonLevelRequired();
                if (cache.getDungeonLevel() >= requirement) {
                    List<String> members = party.getCurrentMembers();
                    members.add(addedPlayer.getName());
                    party.setPlayerNames(members);
                    List<Player> players = party.getPlayers();
                    players.add(addedPlayer);
                    party.setPlayers(players);
                    cache.setInParty(true);
                    Common.tellNoPrefix(addedPlayer, "Successfully Joined " + leaderCache + "'s dungeon party");
                } else {
                    Common.tellNoPrefix(addedPlayer, "Your dungeon level is not high enough to join this party");
                }
            } else {
                Common.tellNoPrefix(addedPlayer, "You are already in a dungeon party");
            }
        }
    }

    public static void removePlayerFromParty(Player player) {
        if (verifyPlayerInParty(player)) {
            DungeonPartyOld party = findParty(findPartyMembersLeader(player));
            List<String> members = party.getCurrentMembers();
            assert members != null;
            members.remove(player.getName());
            party.setPlayerNames(members);
            Common.tellNoPrefix(player, "You were removed from the dungeon party");

        }
    }

    public int getMinimum() {
        return this.minimumRequiredLevel;
    }
    public List<String> getCurrentMembers() {
        return this.playerNames;
    }

    public void setPlayerNames(List<String> playerNames) {
        this.playerNames = playerNames;
        this.save();
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
        this.save();
    }

}