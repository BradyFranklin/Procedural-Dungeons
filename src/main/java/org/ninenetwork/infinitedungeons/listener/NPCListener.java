package org.ninenetwork.infinitedungeons.listener;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.*;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.mcmonkey.sentinel.SentinelTrait;
import org.mcmonkey.sentinel.events.SentinelAttackEvent;
import org.mineacademy.fo.Common;
import org.ninenetwork.infinitedungeons.PlayerCache;
import org.ninenetwork.infinitedungeons.dungeon.Dungeon;
import org.ninenetwork.infinitedungeons.menu.LobbyNpcMenu;
import org.ninenetwork.infinitedungeons.settings.Settings;

public class NPCListener implements Listener {

    @EventHandler
    public void onNpcDamage(NPCDamageByEntityEvent event) {
        NPC npc = event.getNPC();
        if (event.getDamager().getWorld().getName().equals(Settings.PluginServerSettings.DUNGEON_WORLD_NAME)) {
            if (event.getDamager() instanceof Player) {
                Common.log("NPC damage called from npc damage event");
                SentinelTrait trait = npc.getOrAddTrait(SentinelTrait.class);
                double newHealth = trait.health - 10;
                if (newHealth <= 0) {
                    trait.health = 1;
                    npc.destroy();
                } else {
                    trait.health = newHealth;
                }
                //npc.setName("&c&lScarf &8[&7Level 100&8]" + " &7" + GeneralUtils.formatNumber(trait.health) + "&c❤");
                //trait.setHealth(newHealth);
                //npc.setName("&cShadow Assassin" + GeneralUtils.formatNumber(trait.health));
                //PlayerDamageHandler.handleNormalDamageNPC((Player) event.getDamager(), npc, DamageType.MELEE);
            }
            //npc.setName("&6✪ &eCrypt Zombie " + (int) ((LivingEntity) npc.getEntity()).getHealth());
            //npc.updateCustomName();
        }
    }

    @EventHandler
    public void onSentinelAttack(SentinelAttackEvent event) {
        Common.log("Sentinel Attack event run");
        NPC npc = event.getNPC();
        if (event.getTarget() instanceof Player) {
            Player player = (Player) event.getTarget();
            player.setHealth(player.getHealth() - 1);
            Common.tell(player, "Hit");
            //MobDamageHandler.handleNormalDamage(player, (LivingEntity) npc.getEntity(), DamageType.MELEE);
        }
    }

    @EventHandler
    public void onNpcKnockback(NPCKnockbackEvent event) {

    }

    @EventHandler
    public void onNpcFindPlayer(NPCSeenByPlayerEvent event) {
        //event.getNPC().getNavigator().setTarget(event.getPlayer(), true);
    }

    @EventHandler
    public void onNpcRightClick(NPCRightClickEvent event) {
        Player player = event.getClicker();
        NPC npc = event.getNPC();
        PlayerCache cache = PlayerCache.from(player);
        if (cache.hasDungeon()) {
            Dungeon dungeon = cache.getCurrentDungeon();
            if (npc.getName().equals(dungeon.getName())) {
                new LobbyNpcMenu(dungeon, player).displayTo(player);
            }
        }
    }

    @EventHandler
    public void onNpcDeath(NPCDeathEvent event) {
        NPC npc = event.getNPC();
        npc.destroy();
        CitizensAPI.getNPCRegistry().deregister(npc);
    }

}