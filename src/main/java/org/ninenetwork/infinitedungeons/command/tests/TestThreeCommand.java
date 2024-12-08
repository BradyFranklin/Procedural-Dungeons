package org.ninenetwork.infinitedungeons.command.tests;

import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.command.SimpleCommand;
import org.ninenetwork.infinitedungeons.PlayerCache;
import org.ninenetwork.infinitedungeons.playerstats.PlayerStat;
import org.ninenetwork.infinitedungeons.playerstats.PlayerStatManager;
import org.ninenetwork.infinitedungeons.playerstats.PlayerStatSource;
import org.ninenetwork.infinitedungeons.playerstats.PlayerStatSourceDatabase;

import java.util.HashMap;
import java.util.Map;

public class TestThreeCommand extends SimpleCommand {

    public TestThreeCommand() {
        super("testers");
    }

    @Override
    protected void onCommand() {
        checkConsole();
        if (sender instanceof Player) {
            Player player = (Player) sender;
            //Common.tell(player,"Deleting Party");
            //DungeonParty.deleteDungeonParty(player);
            //DungeonBossManager.getInstance().getHandler(Sentinel.class).spawnBoss(player, EntityType.PLAYER);
            PlayerCache cache = PlayerCache.from(player);
            //Common.tell(player, "Health " + cache.getActiveHealth());
            //Common.tell(player, "Defense " + cache.getActiveDefense());
            //Common.tell(player, "Strength " + cache.getActiveStrength());
            //Common.tell(player, "Damage " + cache.getActiveDamage());
            //Common.tell(player, "Intelligence " + cache.getActiveIntelligence());
            //PlayerStatManager.evaluateEquipmentStats(player,"none");
            Common.tell(player, "All Sources");
            Map<PlayerStat, Double> map = new HashMap<>();
            map.put(PlayerStat.CRIT_CHANCE, 100.0);
            map.put(PlayerStat.CRIT_DAMAGE, cache.getActiveCritDamage() + 50.0);
            PlayerStatSourceDatabase.getInstance().addFullStatsFromSpecificSourceToMapping(player, PlayerStatSource.FAILSAFE, map, false);
            PlayerStatManager.setAllPlayerStats(player);
            for (Map.Entry<PlayerStat, Double> entry : PlayerStatSourceDatabase.getInstance().getAllValuesOfAllSources(player).entrySet()) {
                Common.tell(player, entry.getKey() + ": " + entry.getValue());
            }
            Common.tell(player, " ");
            Common.tell(player, "All From Cache");
            for (Map.Entry<PlayerStat, Double> ent : cache.getStatMapper().entrySet()) {
                Common.tell(player, ent.getKey() + ": " + ent.getValue());
            }
        }
    }

}