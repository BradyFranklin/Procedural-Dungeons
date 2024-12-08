package org.ninenetwork.infinitedungeons.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.ninenetwork.infinitedungeons.PlayerCache;

public class DungeonListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        PlayerCache.from(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final PlayerCache cache = PlayerCache.from(player);
        cache.save();
        cache.removeFromMemory();
    }

}