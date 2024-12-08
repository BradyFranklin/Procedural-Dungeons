package org.ninenetwork.infinitedungeons.classes;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.ninenetwork.infinitedungeons.InfiniteDungeonsPlugin;
import org.ninenetwork.infinitedungeons.PlayerCache;
import org.ninenetwork.infinitedungeons.dungeon.Dungeon;
import org.ninenetwork.infinitedungeons.party.DungeonParty;

public class DungeonClassTask extends BukkitRunnable {

    private Dungeon dungeon;

    public DungeonClassTask(Dungeon dungeon) {
        this.dungeon = dungeon;
    }

    @Override
    public void run() {
        for (PlayerCache cache : this.dungeon.getPlayerCaches()) {
            DungeonClass dungeonClass = DungeonClass.findClassByLabel(cache.getCurrentDungeonClass());
            if (dungeonClass == DungeonClass.HEALER) {
                runHealerBonuses(cache);
            } else if (dungeonClass == DungeonClass.MAGE) {
                runMageBonuses(cache);
            } else if (dungeonClass == DungeonClass.ARCHER) {
                runArcherBonuses(cache);
            } else if (dungeonClass == DungeonClass.TANK) {
                runTankBonuses(cache);
            } else if (dungeonClass == DungeonClass.BERSERK) {
                runBerserkBonuses(cache);
            }
            if (this.dungeon.isStopped()) {
                cancel();
            }
        }
    }

    private static void runHealerBonuses(PlayerCache cache) {
        if (cache.hasDungeon()) {
            Dungeon dungeon = cache.getCurrentDungeon();
            Player player = cache.toPlayer();
            // HEALING AURA //
            for (Entity entity : player.getNearbyEntities(8.0, 8.0, 8.0)) {
                if (entity instanceof Player) {
                    if (DungeonParty.areSameParty(player, (Player) entity)) {
                        Player p = (Player) entity;
                        int classLevel = DungeonClassManager.retrieveClassValue(player, DungeonClass.findClassByLabel(cache.getCurrentDungeonClass()), "Level");
                        double amount = 1 + (dungeon.getFloor() * (classLevel / 5.0) * .1);
                        PlayerCache.from(p).setActiveHealthRegen(cache.getActiveHealthRegen() + amount);
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                PlayerCache.from(p).setActiveHealthRegen(cache.getActiveHealthRegen() - amount);
                            }
                        }.runTaskLaterAsynchronously(InfiniteDungeonsPlugin.getInstance(), 19L);
                    }
                }
            }
        }
    }

    private static void runMageBonuses(PlayerCache cache) {

    }

    private static void runArcherBonuses(PlayerCache cache) {

    }

    private static void runTankBonuses(PlayerCache cache) {

    }

    private static void runBerserkBonuses(PlayerCache cache) {

    }

}