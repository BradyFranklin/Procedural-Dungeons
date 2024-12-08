package org.ninenetwork.infinitedungeons.task;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.mineacademy.fo.remain.Remain;
import org.ninenetwork.infinitedungeons.PlayerCache;
import org.ninenetwork.infinitedungeons.settings.Settings;

public class ActionBar extends BukkitRunnable {

    @Override
    public void run() {
        for (Player player : Remain.getOnlinePlayers()) {

            if (player.getWorld().getName().equals(Settings.PluginServerSettings.DUNGEON_WORLD_NAME)) {
                PlayerCache cache = PlayerCache.from(player);
                Remain.sendActionBar(player, "&c" + (int) cache.getActiveHealth() + "/" + (int) cache.getActiveMaxHealth() + "     "
                        + "&a" + (int) cache.getActiveDefense() + " Defense" + "     "
                        + (int) cache.getActiveIntelligence() + "/" + (int) cache.getActiveMaxIntelligence());
            }

        }
    }

}