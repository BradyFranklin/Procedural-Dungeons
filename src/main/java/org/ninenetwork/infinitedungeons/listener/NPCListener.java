package org.ninenetwork.infinitedungeons.listener;

import net.citizensnpcs.api.event.NPCDamageByEntityEvent;
import net.citizensnpcs.api.event.NPCKnockbackEvent;
import net.citizensnpcs.api.event.NPCSeenByPlayerEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class NPCListener implements Listener {

    @EventHandler
    public void onNpcDamage(NPCDamageByEntityEvent event) {
        NPC npc = event.getNPC();
        npc.setName("&6✪ &eCrypt Zombie " + (int) ((LivingEntity) npc.getEntity()).getHealth());
        npc.updateCustomName();
    }

    @EventHandler
    public void onNpcKnockback(NPCKnockbackEvent event) {
        if (event.getNPC().getName().contains("Crypt Zombie")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onNpcFindPlayer(NPCSeenByPlayerEvent event) {
        event.getNPC().getNavigator().setTarget(event.getPlayer(), true);
    }

}