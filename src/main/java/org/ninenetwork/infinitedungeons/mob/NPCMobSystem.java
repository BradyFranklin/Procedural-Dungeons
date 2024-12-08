package org.ninenetwork.infinitedungeons.mob;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.ai.Goal;
import net.citizensnpcs.api.ai.goals.TargetNearbyEntityGoal;
import net.citizensnpcs.api.ai.goals.WanderGoal;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.model.HookManager;
import org.mineacademy.fo.remain.CompMetadata;

import static org.mineacademy.fo.Valid.checkBoolean;

public class NPCMobSystem {

    public void createNPC(Player player) {
        checkBoolean(HookManager.isCitizensLoaded(), "Citizens not loaded");
        final NPCRegistry registry = CitizensAPI.getNPCRegistry();

        final Location playerLocation = player.getLocation();

        final NPC npc = registry.createNPC(EntityType.ZOMBIE, "&c&lCrypt Zombie");
        npc.spawn(playerLocation);

        final LivingEntity entity = (LivingEntity) npc.getEntity();

        npc.setProtected(false);
        entity.setHealth(10);

        npc.data().set(NPC.Metadata.GLOWING, true);
        npc.data().set(NPC.Metadata.DEATH_SOUND, "entity.wither_skeleton.death");

        final WanderGoal goal = WanderGoal.builder(npc).xrange(20).yrange(10).build();
        npc.getDefaultGoalController().addGoal(goal, 1);

        final Goal attackGoal = TargetNearbyEntityGoal.builder(npc)
                .aggressive(true)
                .radius(5)
                .targets(Common.newSet(EntityType.PLAYER))
                .build();
        npc.getDefaultGoalController().addGoal(attackGoal, 2);

        npc.getNavigator().getLocalParameters()
                .attackDelayTicks(20)
                .attackRange(25)
                .baseSpeed(1.5F)
                .updatePathRate(4 * 20);

        CompMetadata.setMetadata(entity, "Citizens-Boss");

    }

}
