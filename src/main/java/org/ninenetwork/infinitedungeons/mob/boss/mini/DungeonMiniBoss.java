package org.ninenetwork.infinitedungeons.mob.boss.mini;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.ai.AttackStrategy;
import net.citizensnpcs.api.ai.Goal;
import net.citizensnpcs.api.ai.Navigator;
import net.citizensnpcs.api.ai.goals.TargetNearbyEntityGoal;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.mcmonkey.sentinel.SentinelTrait;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.model.HookManager;
import org.mineacademy.fo.remain.CompMetadata;
import org.ninenetwork.infinitedungeons.dungeon.Dungeon;
import org.ninenetwork.infinitedungeons.dungeon.instance.DungeonRoomInstance;
import org.ninenetwork.infinitedungeons.mob.trait.ShadowAssassinTeleportTrait;

import java.util.ArrayList;
import java.util.function.Function;

import static org.mineacademy.fo.Valid.checkBoolean;

public class DungeonMiniBoss {

    Dungeon dungeon;
    DungeonRoomInstance roomInstance;

    String type;
    ArrayList<String> skills = new ArrayList<>();

    public DungeonMiniBoss() {

    }

    public void spawnMiniBoss(Dungeon dungeon, DungeonRoomInstance instance) {

    }

    public static void createNPC(Player player) {
        checkBoolean(HookManager.isCitizensLoaded(), "Citizens not loaded");
        final NPCRegistry registry = CitizensAPI.getNPCRegistry();

        final Location playerLocation = player.getLocation();

        final NPC npc = registry.createNPC(EntityType.PLAYER, "Shadow Assassin");

        npc.spawn(playerLocation);

        final LivingEntity entity = (LivingEntity) npc.getEntity();

        npc.setProtected(false);
        entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(1000);
        npc.setName("&c&lShadow Assassin");
        if (entity.getEquipment() != null) {
            //entity.getEquipment().setBoots(new ItemStack(Material.LEATHER_BOOTS, 1));
            entity.getEquipment().setItemInMainHand(new ItemStack(Material.IRON_SWORD, 1));
        }
        npc.data().set(NPC.Metadata.KNOCKBACK, false);
        npc.data().set(NPC.Metadata.USING_HELD_ITEM, true);
        npc.data().set(NPC.Metadata.ACTIVATION_RANGE, 15);
        npc.data().set(NPC.Metadata.GLOWING, true);
        npc.data().set(NPC.Metadata.DEATH_SOUND, "entity.wither_skeleton.death");

        //final WanderGoal goal = WanderGoal.builder(npc).xrange(20).yrange(10).build();
        //npc.getDefaultGoalController().addGoal(goal, 1);

        /*
        final Goal attackGoal = TargetNearbyEntityGoal.builder(npc)
                .aggressive(true)
                .radius(5)
                .targets(Common.newSet(EntityType.PLAYER))
                .build();
        npc.getDefaultGoalController().addGoal(attackGoal, 1);

        npc.getNavigator().getLocalParameters()
                .attackDelayTicks(15)
                .attackRange(2)
                .baseSpeed(1.8F)
                .useNewPathfinder(true)
                .avoidWater(true)
                .updatePathRate(5);

         */

        npc.getOrAddTrait(SkinTrait.class).setSkinPersistent("d3731e38-5dde-4878-859d-d0858c4b6793", "Zc+egPfvdkkutM0qR13oIUCXYuLIRkXGLuKutWxSUbW4H7jujEIQD+aKW+Yy9JekTbaqehvp+OArXMkjRs9h8o0ZGAJY/xlXF3OVzfBA7hIvrtx7cSaIRIr5pfjcBCUe0m1l8shByayaCtu/q11QZzZCX1+ZHKgghG9W95EnkmyAESHNjIXFBCMxPCElGfjEIsKwdt48NIlDiCmx3pUSCr3AnL8FvHrG4CMNZK+hhMStOV8nLq7l6MppsUUmRWkL0DVDTEh9BHzAWw3pBOvwP3r9Ax/5amBDrB1sN8vSa/bfuMxlxH11UGt3kb04SOuxYuMCCSCzKq0xSzlP5H5HfW3wSSk9T2zcpyEZgsIud28FZzBjcdgB+Umq0Cp7IybAi6xFbjC8zNgh+y24sNv6F4XJzv8v5eB1AwUZXStDrqrIpTb1XHIJurRNBbyXh3q8XuR2ECmpZAwupKtxWDo5og6IbigQEjKFjMrmvgnUd1dukcdro+w/p2IgmGHVXoR6jtN1YNnpldILDJiql8R097Nco3wU0crU5M1qfqkHHEvOOrf7iOZRF+psNaiJSZuBNmmTdS+13Q+nNwoTfGERFb8Em3YxKFs5j9l4a7HxbW2YvH93sGHCxuPgd9bXJ9KPh6Yp9Uch1cDB/uF4FfOwN7WMQ8ON7IhAHAegjLththc=", "ewogICJ0aW1lc3RhbXAiIDogMTU4OTEzNzY1ODgxOSwKICAicHJvZmlsZUlkIiA6ICJlM2I0NDVjODQ3ZjU0OGZiOGM4ZmEzZjFmN2VmYmE4ZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJNaW5pRGlnZ2VyVGVzdCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8zMzk5ZTAwZjQwNDQxMWU0NjVkNzQzODhkZjEzMmQ1MWZlODY4ZWNmODZmMWMwNzNmYWZmYTFkOTE3MmVjMGYzIgogICAgfQogIH0KfQ==");
        SentinelTrait sentinelTrait = npc.getOrAddTrait(SentinelTrait.class);
        npc.addTrait(new ShadowAssassinTeleportTrait());
        sentinelTrait.setHealth(100.0);
        npc.getNavigator().getLocalParameters()
                .updatePathRate(2)
                .useNewPathfinder(false)
                .avoidWater(true)
                .attackDelayTicks(15)
                .baseSpeed(1.8F);

        CompMetadata.setMetadata(entity, "Citizens-Boss", "MiniBoss");

        //npc.getOrAddTrait(SkinTrait.class).setSkinPersistent("", "", "");

    }

    public EntityType getEntityType() {
        return null;
    }

    public String getEntityName() {
        return null;
    }

    public double getMiniBossHealth() {
        return 1000.0;
    }

    public double getMiniBossDefense() {
        return 0.0;
    }

}