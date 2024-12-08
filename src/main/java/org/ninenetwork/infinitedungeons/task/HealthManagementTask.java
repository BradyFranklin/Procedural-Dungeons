package org.ninenetwork.infinitedungeons.task;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.mineacademy.fo.remain.Remain;
import org.ninenetwork.infinitedungeons.PlayerCache;
import org.ninenetwork.infinitedungeons.playerstats.health.PlayerHealthHandler;
import org.ninenetwork.infinitedungeons.settings.Settings;

public class HealthManagementTask extends BukkitRunnable {

    @Override
    public void run() {
        for (Player player : Remain.getOnlinePlayers()) {
            if (player.getWorld().getName().equals(Settings.PluginServerSettings.DUNGEON_WORLD_NAME)) {
                PlayerCache cache = PlayerCache.from(player);
                if (cache.getActiveHealth() != cache.getActiveMaxHealth()) {
                    double regenHealth = ((cache.getActiveMaxHealth() / 100) + 1.5) * (cache.getActiveHealthRegen() / 100);
                    PlayerHealthHandler.runHealthRegen(cache, regenHealth);
                }
            }
        }
    }

}