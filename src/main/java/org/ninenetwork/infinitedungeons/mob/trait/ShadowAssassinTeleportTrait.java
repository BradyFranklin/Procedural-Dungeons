package org.ninenetwork.infinitedungeons.mob.trait;

import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.mcmonkey.sentinel.SentinelTrait;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.model.SimpleTrait;

import java.util.UUID;

public class ShadowAssassinTeleportTrait extends SimpleTrait {

    public ShadowAssassinTeleportTrait() {
        super("shadow-teleport");

        // Only call onTick every 7. second
        this.setTickThreshold(20 * 7);
    }

    @Override
    protected void onTick() {
        // called automatically DURING the time this NPC has this trait,
        // it is called as much as you want

        // Send chat messages/bubbles to nearby players depending on their interaction phase

        final NPC npc = this.getNPC();
        final Entity npcEntity = npc.getEntity();
        final int playersNearbyRange = 5;

        if (npcEntity == null)
            return;

        for (final Entity nearby : npcEntity.getWorld().getNearbyEntities(npcEntity.getLocation(), playersNearbyRange, playersNearbyRange, playersNearbyRange)) {
            if (nearby instanceof Player) {
                final UUID uniqueId = nearby.getUniqueId();
                final Player player = (Player) nearby;

                npc.teleport(player.getLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                player.damage(5);
                if (npc.hasTrait(SentinelTrait.class)) {
                    npc.getOrAddTrait(SentinelTrait.class).faceLocation(player.getLocation());
                }
                Common.tell(player, "Shadow Assassin hit you for 10 Damage!");
                break;
            }
        }
    }

    @Override
    protected void load(final SerializedMap serializedMap) {
        // NPC-specific

    }

    @Override
    protected void save(final SerializedMap serializedMap) {

    }

}