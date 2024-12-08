package org.ninenetwork.infinitedungeons.mob;

import org.bukkit.scheduler.BukkitRunnable;

public class DungeonMobTask extends BukkitRunnable {

    @Override
    public void run() {
        for () {
            Location location = player.getLocation();

            if (player.isGliding() && location.getPitch() < -30)
                player.setVelocity(location.getDirection().multiply(1.5));
        }
    }

}