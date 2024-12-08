package org.ninenetwork.infinitedungeons.util;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {

    @Getter
    public static final CooldownManager instance = new CooldownManager();

    private final Map<UUID,Map<String,Instant>> map = new HashMap<>();

    // Set cooldown
    public void setCooldown(UUID uuid, String key, Duration duration) {
        if (map.containsKey(uuid)) {
            if (map.get(uuid).containsKey(key)) {
                map.get(uuid).replace(key, Instant.now().plus(duration));
            } else {
                map.get(uuid).put(key, Instant.now().plus(duration));
            }
        } else {
            Map<String, Instant> cooldowns = new HashMap<>();
            cooldowns.put(key, Instant.now().plus(duration));
            map.put(uuid,cooldowns);
        }
    }

    public boolean isUsable(Player player, String key, int seconds) {
        UUID uuid = player.getUniqueId();
        CooldownManager manager = CooldownManager.getInstance();
        Duration timeLeft = manager.getRemainingCooldown(uuid, key);
        if (timeLeft.isZero() || timeLeft.isNegative()) {
            manager.setCooldown(uuid, key, Duration.ofSeconds(5L));
            return true;
        } else {
            return false;
        }
    }

    public boolean checkReverseCooldown(Player player, String key) {
        UUID uuid = player.getUniqueId();
        CooldownManager manager = CooldownManager.getInstance();
        Duration timeLeft = manager.getRemainingCooldown(uuid, key);
        if (timeLeft.isZero() || timeLeft.isNegative()) {
            return false;
        } else {
            return true;
        }
    }

    // Check if cooldown has expired
    public boolean hasCooldown(UUID uuid, String type) {
        Instant cooldown = map.get(uuid).get(type);
        return cooldown != null && Instant.now().isBefore(cooldown);
    }

    // Remove cooldown
    public Instant removeCooldown(UUID uuid, String type) {
        return map.get(uuid).remove(type);
    }

    // Get remaining cooldown time
    public Duration getRemainingCooldown(UUID uuid, String type) {
        if (map.containsKey(uuid) && map.get(uuid).containsKey(type)) {
            Instant cooldown = map.get(uuid).get(type);
            Instant now = Instant.now();
            if (cooldown != null && now.isBefore(cooldown)) {
                return Duration.between(now, cooldown);
            } else {
                return Duration.ZERO;
            }
        } else {
            return Duration.ZERO;
        }
    }

}