package org.ninenetwork.infinitedungeons.task;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.mineacademy.fo.remain.Remain;
import org.ninenetwork.infinitedungeons.PlayerCache;
import org.ninenetwork.infinitedungeons.settings.Settings;

public class ManaRegenerationTask extends BukkitRunnable {

    @Override
    public void run() {
        for (Player player : Remain.getOnlinePlayers()) {
            if (player.getWorld().getName().equals(Settings.PluginServerSettings.DUNGEON_WORLD_NAME)) {
                PlayerCache cache = PlayerCache.from(player);
                if (cache.getActiveIntelligence() != cache.getActiveMaxIntelligence()) {
                    double percentageRegen = (cache.getActiveManaRegen() + 2) / 100;
                    if (percentageRegen > 0.4) {
                        percentageRegen = 0.4;
                    }
                    double amountToAdd = cache.getActiveMaxIntelligence() * percentageRegen;
                    if (cache.getActiveIntelligence() + amountToAdd > cache.getActiveMaxIntelligence()) {
                        cache.setActiveIntelligence(cache.getActiveMaxIntelligence());
                    } else {
                        cache.setActiveIntelligence(cache.getActiveIntelligence() + amountToAdd);
                    }
                }
            }
        }
    }

}