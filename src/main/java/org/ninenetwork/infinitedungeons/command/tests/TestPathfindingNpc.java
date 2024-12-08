package org.ninenetwork.infinitedungeons.command.tests;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.mcmonkey.sentinel.SentinelTrait;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.command.SimpleCommand;
import org.mineacademy.fo.model.HookManager;
import org.ninenetwork.infinitedungeons.PlayerCache;

public class TestPathfindingNpc extends SimpleCommand {

    public TestPathfindingNpc() {
        super("pathing");
        setMinArguments(2);
    }

    @Override
    protected void onCommand() {
        checkConsole();
        if (sender instanceof Player) {
            Player player = (Player) sender;
            PlayerCache cache = PlayerCache.from(player);
            checkBoolean(HookManager.isCitizensLoaded(), "This command requires Citizens plugin to be installed.");
            String param = args[0];
            if (param.equalsIgnoreCase("boss")) {
                final EntityType type = findEnum(EntityType.class, args[1], "No such entity type '{1}', available: " + Common.join(EntityType.values()));
                final NPCRegistry registry = CitizensAPI.getNPCRegistry();
                final NPC npc = registry.createNPC(type, "Pathfinder");
                npc.spawn(player.getLocation());
                npc.setProtected(false);
                LivingEntity en = (LivingEntity) npc.getEntity();
                en.setHealth(20);
                npc.setFlyable(true);
                SentinelTrait trait = npc.getOrAddTrait(SentinelTrait.class);
                trait.pathTo(player.getLocation().clone().add(100.0, 0.0, 0.0));
            }
        }
    }

}