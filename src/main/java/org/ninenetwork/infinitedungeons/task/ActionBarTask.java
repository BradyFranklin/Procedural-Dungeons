package org.ninenetwork.infinitedungeons.task;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.mineacademy.fo.remain.Remain;
import org.ninenetwork.infinitedungeons.PlayerCache;
import org.ninenetwork.infinitedungeons.playerstats.PlayerStat;
import org.ninenetwork.infinitedungeons.settings.Settings;
import org.ninenetwork.infinitedungeons.util.GeneralUtils;

public class ActionBarTask extends BukkitRunnable {

    @Override
    public void run() {
        for (Player player : Remain.getOnlinePlayers()) {
            if (player.getWorld().getName().equals(Settings.PluginServerSettings.DUNGEON_WORLD_NAME)) {
                PlayerCache cache = PlayerCache.from(player);
                String health = GeneralUtils.formatStringWithGradient((int) cache.getActiveHealth() + "/" + (int) cache.getActiveMaxHealth() + GeneralUtils.getStatSymbol(PlayerStat.HEALTH), "#FFA500", "#FF3535", false, true);
                String defense = GeneralUtils.formatStringWithGradient((int) cache.getActiveDefense() + " Defense " + GeneralUtils.getStatSymbol(PlayerStat.DEFENSE), "#64FF64", "#00A400", false, true);
                String intelligence = GeneralUtils.formatStringWithGradient((int) cache.getActiveIntelligence() + "/" + (int) cache.getActiveMaxIntelligence() + " " + GeneralUtils.getStatSymbol(PlayerStat.INTELLIGENCE), "#ADD8E6", "#5151DD", false, true);

                Remain.sendActionBar(player, health + "     " + defense + "     " + intelligence);

                /*
                Remain.sendActionBar(player, "&c" + (int) cache.getActiveHealth() + "/" + (int) cache.getActiveMaxHealth() + "     "
                        + "&a" + (int) cache.getActiveDefense() + " Defense" + "     "
                        + "&b" + (int) cache.getActiveIntelligence() + "/" + (int) cache.getActiveMaxIntelligence());
                */
            }
        }
    }

}