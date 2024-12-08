package org.ninenetwork.infinitedungeons.dungeon;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.mineacademy.fo.Common;
import org.ninenetwork.infinitedungeons.PlayerCache;
import org.ninenetwork.infinitedungeons.item.CustomDungeonItemManager;
import org.ninenetwork.infinitedungeons.enchant.DungeonEnchant;
import org.ninenetwork.infinitedungeons.item.ItemLoreGenerator;
import org.ninenetwork.infinitedungeons.party.DungeonParty;
import org.ninenetwork.infinitedungeons.party.DungeonQueue;
import org.ninenetwork.infinitedungeons.playerstats.PlayerStatSource;
import org.ninenetwork.infinitedungeons.playerstats.PlayerStatSourceDatabase;

import java.util.ArrayList;
import java.util.Objects;

public class DungeonFunctions {

    public static void refreshPlayer(Player player) {
        PlayerStatSourceDatabase.getInstance().removeAllStatsFromSource(player, PlayerStatSource.BLESSING);
    }

    public static void initializeStatMapping(Player player) {
        PlayerStatSourceDatabase manager = PlayerStatSourceDatabase.getInstance();

    }

    public static void addSecretToRoom(String roomName, Location secretLocation) {
        DungeonRoom dungeonRoom = DungeonRoom.findByName(roomName);
        if (dungeonRoom != null) {
            if (dungeonRoom.getSecrets().contains(secretLocation)) {
                dungeonRoom.removeSecret(secretLocation);
            }
            dungeonRoom.addSecret(secretLocation);
        }
    }

    public static void removeAllSecretsFromRoom(String roomName) {
        DungeonRoom dungeonRoom = DungeonRoom.findByName(roomName);
        if (dungeonRoom != null) {
            ArrayList<Location> secrets = new ArrayList<>();
            dungeonRoom.setSecrets(secrets);
            Common.log("Removed all secrets from room " + dungeonRoom.getName());
        }
    }

    public static void quickEnchantLoreChecker(Player player, String enchantName) {
        ItemStack item = Objects.requireNonNull(player.getEquipment()).getItemInMainHand();
        ItemMeta meta = item.getItemMeta();
        CustomDungeonItemManager itemManager = CustomDungeonItemManager.getInstance();
        if (itemManager.checkIsDungeonItem(item)) {
            Common.tell(player, "Class" + itemManager.findDungeonItem(item).getClass());
            for (String s : meta.getLore()) {
                Common.tell(player, s);
            }
            meta.setLore(ItemLoreGenerator.dungeonItemAddEnchantLoreGenerator(player, itemManager.findDungeonItem(item).getClass(), item, DungeonEnchant.valueOf(enchantName)));
            item.setItemMeta(meta);
        }
    }

    public static void startDungeon(Player player, DungeonType dungeonType, int floor) {
        PlayerCache cache = PlayerCache.from(player);
        if (DungeonParty.hasParty(player)) {
            DungeonParty party = DungeonParty.findPartyByPlayer(player);
            if (party != null) {
                if (DungeonParty.isLeader(player, party)) {
                    int iterationProtection = 0;
                    while (Dungeon.findByName(player.getName() + cache.getDungeonsCreated()) != null) {
                        cache.setDungeonsCreated(cache.getDungeonsCreated() + 1);
                        iterationProtection++;
                        if (iterationProtection > 10) {
                            break;
                        }
                    }
                    Dungeon dungeon = Dungeon.createDungeon(player.getName() + cache.getDungeonsCreated(), dungeonType);
                    dungeon.setFloor(floor);
                    cache.setDungeonsCreated(cache.getDungeonsCreated() + 1);
                    dungeon.initializeDungeonGeneration(dungeon, party, dungeonType, floor);
                    if (DungeonQueue.getDungeonQueue().getPartiesInQueue().contains(party)) {
                        DungeonQueue.getDungeonQueue().removePartyFromQueue(party);
                    }
                } else {
                    Common.tell(player, "You must be leader in order to start the dungeon!");
                }
            } else {
                Common.tell(player, "Something went wrong, please try again!");
            }
        } else {
            DungeonParty party = new DungeonParty(player, dungeonType);
            Dungeon dungeon = Dungeon.createDungeon(player.getName() + cache.getDungeonsCreated(), dungeonType);
            dungeon.setFloor(floor);
            cache.setDungeonsCreated(cache.getDungeonsCreated() + 1);
            dungeon.initializeDungeonGeneration(dungeon, party, dungeonType, floor);
        }
    }

}