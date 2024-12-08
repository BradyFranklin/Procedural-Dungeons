package org.ninenetwork.infinitedungeons.mob.trait;

import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.exception.EventHandledException;
import org.mineacademy.fo.model.SimpleTrait;
import org.ninenetwork.infinitedungeons.InfiniteDungeonsPlugin;

import java.util.UUID;

public class MiniBossBowSwitchTrait extends SimpleTrait {

    public MiniBossBowSwitchTrait() {
        super("bow-switch");

        // Only call onTick every 10. second
        this.setTickThreshold(20 * 10);
    }

    @Override
    protected void onTick() {
        final NPC npc = this.getNPC();
        final Entity npcEntity = npc.getEntity();
        final int playersNearbyRange = 5;

        if (npcEntity == null)
            return;

        for (final Entity nearby : npcEntity.getWorld().getNearbyEntities(npcEntity.getLocation(), playersNearbyRange, playersNearbyRange, playersNearbyRange)) {
            if (nearby instanceof Player) {
                final UUID uniqueId = nearby.getUniqueId();
                final Player player = (Player) nearby;

                npc.setProtected(true);
                LivingEntity livingEntity = (LivingEntity) npc.getEntity();
                livingEntity.getEquipment().setItemInMainHand(new ItemStack(Material.BOW, 1));
                Common.tell(player, "Attempted bow switch shit");
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        npc.setProtected(false);
                        livingEntity.getEquipment().setItemInMainHand(new ItemStack(Material.IRON_SWORD));
                    }
                }.runTaskLater(InfiniteDungeonsPlugin.getInstance(), 20L);
                break;
            }
        }
    }

    @Override
    public void onClick(final Player player, final ClickType clickType) throws EventHandledException {
        if (clickType != ClickType.RIGHT) {
            Common.tell(player, "Right click");
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