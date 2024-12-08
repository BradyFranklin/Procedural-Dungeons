package org.ninenetwork.infinitedungeons.mob.boss.mini;

import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.mcmonkey.sentinel.SentinelTrait;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.plugin.SimplePlugin;
import org.ninenetwork.infinitedungeons.dungeon.Dungeon;
import org.ninenetwork.infinitedungeons.dungeon.DungeonType;
import org.ninenetwork.infinitedungeons.mob.boss.DungeonBoss;
import org.ninenetwork.infinitedungeons.mob.trait.ShadowAssassinTeleportTrait;
import org.ninenetwork.infinitedungeons.util.DungeonMobUtil;

public class Assassin extends DungeonBoss {

    public Assassin(SimplePlugin plugin) {
        super(plugin, "Assassin", EntityType.PLAYER);
    }

    @Override
    protected LivingEntity generateMob(NPC npc, Dungeon dungeon) {

        int baseStartingHealth = 1000;
        LivingEntity entity = (LivingEntity) npc.getEntity();

        Common.log("SPAWNED MINI BOSS" + entity.getLocation().getX() + " " + entity.getLocation().getY() + " " +entity.getLocation().getZ());

        npc.getOrAddTrait(SkinTrait.class).setSkinPersistent("d3731e38-5dde-4878-859d-d0858c4b6793",
                "Zc+egPfvdkkutM0qR13oIUCXYuLIRkXGLuKutWxSUbW4H7jujEIQD+aKW+Yy9JekTbaqehvp+OArXMkjRs9h8o0ZGAJY/xlXF3OVzfBA7hIvrtx7cSaIRIr5pfjcBCUe0m1l8shByayaCtu/q11QZzZCX1+ZHKgghG9W95EnkmyAESHNjIXFBCMxPCElGfjEIsKwdt48NIlDiCmx3pUSCr3AnL8FvHrG4CMNZK+hhMStOV8nLq7l6MppsUUmRWkL0DVDTEh9BHzAWw3pBOvwP3r9Ax/5amBDrB1sN8vSa/bfuMxlxH11UGt3kb04SOuxYuMCCSCzKq0xSzlP5H5HfW3wSSk9T2zcpyEZgsIud28FZzBjcdgB+Umq0Cp7IybAi6xFbjC8zNgh+y24sNv6F4XJzv8v5eB1AwUZXStDrqrIpTb1XHIJurRNBbyXh3q8XuR2ECmpZAwupKtxWDo5og6IbigQEjKFjMrmvgnUd1dukcdro+w/p2IgmGHVXoR6jtN1YNnpldILDJiql8R097Nco3wU0crU5M1qfqkHHEvOOrf7iOZRF+psNaiJSZuBNmmTdS+13Q+nNwoTfGERFb8Em3YxKFs5j9l4a7HxbW2YvH93sGHCxuPgd9bXJ9KPh6Yp9Uch1cDB/uF4FfOwN7WMQ8ON7IhAHAegjLththc=",
                "ewogICJ0aW1lc3RhbXAiIDogMTU4OTEzNzY1ODgxOSwKICAicHJvZmlsZUlkIiA6ICJlM2I0NDVjODQ3ZjU0OGZiOGM4ZmEzZjFmN2VmYmE4ZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJNaW5pRGlnZ2VyVGVzdCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8zMzk5ZTAwZjQwNDQxMWU0NjVkNzQzODhkZjEzMmQ1MWZlODY4ZWNmODZmMWMwNzNmYWZmYTFkOTE3MmVjMGYzIgogICAgfQogIH0KfQ==");

        //entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(baseStartingHealth);
        //entity.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(4.0);

        SentinelTrait sentinel = npc.getOrAddTrait(SentinelTrait.class);

        sentinel.addIgnore("NPCS");
        sentinel.addIgnore("PASSIVE_MOBS");
        sentinel.addIgnore("MOBS");
        sentinel.addIgnore("MONSTERS");

        sentinel.addTarget("PLAYERS");
        sentinel.addTarget("OWNER");

        sentinel.setHealth(baseStartingHealth);
        sentinel.setInvincible(false);
        //sentinel.pathTo(entity.getLocation());
        sentinel.damage = 20;
        sentinel.chaseRange = 5.0;
        sentinel.range = 1.75;
        sentinel.spawnPoint = entity.getLocation();
        sentinel.attackRate = 1;
        sentinel.allowKnockback = false;
        sentinel.autoswitch = false;
        sentinel.speed = 2.0;
        sentinel.healRate = 0;
        sentinel.health = 1000;

        if (entity.getEquipment() != null) {
            //entity.getEquipment().setBoots(new ItemStack(Material.LEATHER_BOOTS, 1));
            entity.getEquipment().setItemInMainHand(new ItemStack(Material.IRON_SWORD, 1));
        }

        /*
        npc.data().set(NPC.Metadata.KNOCKBACK, false);
        npc.data().set(NPC.Metadata.USING_HELD_ITEM, true);
        npc.data().set(NPC.Metadata.ACTIVATION_RANGE, 15);
        npc.data().set(NPC.Metadata.GLOWING, true);
        npc.data().set(NPC.Metadata.DEATH_SOUND, "entity.wither_skeleton.death");

         */

        //SentinelTrait sentinelTrait = npc.getOrAddTrait(SentinelTrait.class);
        //npc.addTrait(new ShadowAssassinTeleportTrait());
        npc.getOrAddTrait(ShadowAssassinTeleportTrait.class);
        /*
        npc.getNavigator().getLocalParameters()
                .updatePathRate(2)
                .useNewPathfinder(false)
                .avoidWater(true)
                .attackDelayTicks(15)
                .baseSpeed(1.9F);

         */

        //CompMetadata.setMetadata(entity, "Citizens-Boss", "Assassin");

        //entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
        //DungeonMobUtil.createDyedArmorPieces(entity, Color.fromRGB(255, 102, 252));

        npc.setName(this.getBossName(dungeon, entity, this.getBossHealth(dungeon)));
        return entity;
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.PLAYER;
    }

    @Override
    public String getBossName(Dungeon dungeon, LivingEntity entity, double displayedHealth) {
        return Common.colorize(DungeonMobUtil.getBossNametagGradient(dungeon, entity, "Shadow Assassin", "#0083FF", "#93FFFF", displayedHealth));
    }

    @Override
    public double getBossHealth(Dungeon dungeon) {
        int floor = dungeon.getFloor();
        if (dungeon.getType() == DungeonType.CATACOMBS) {
            if (floor == 0) {
                return 600;
            } else if (floor == 1) {
                return 28000;
            } else if (floor == 2) {
                return 52000;
            } else if (floor == 3) {
                return 90000;
            } else if (floor == 4) {
                return 160000;
            } else if (floor == 5) {
                return 250000;
            } else if (floor == 6) {
                return 360000;
            } else if (floor == 7) {
                return 750000;
            }
        } else if (dungeon.getType() == DungeonType.MASTER) {
            if (floor == 1) {
                return 1600000;
            } else if (floor == 2) {
                return 3000000;
            } else if (floor == 3) {
                return 7200000;
            } else if (floor == 4) {
                return 8400000;
            } else if (floor == 5) {
                return 9600000;
            } else if (floor == 6) {
                return 14400000;
            } else if (floor == 7) {
                return 28000000;
            }
        }
        return 0;
    }

    @Override
    public double getBossDefense(Dungeon dungeon) {
        return 0.0;
    }

    @Override
    public double getDungeonBossBaseDamage(Dungeon dungeon) {
        int floor = dungeon.getFloor();
        if (dungeon.getType() == DungeonType.CATACOMBS) {
            if (floor == 0) {
                return 200;
            } else if (floor == 1) {
                return 720;
            } else if (floor == 2) {
                return 1120;
            } else if (floor == 3) {
                return 1920;
            } else if (floor == 4) {
                return 2640;
            } else if (floor == 5) {
                return 4160;
            } else if (floor == 6) {
                return 5600;
            } else if (floor == 7) {
                return 11600;
            }
        } else if (dungeon.getType() == DungeonType.MASTER) {
            if (floor == 1) {
                return 18000;
            } else if (floor == 2) {
                return 30000;
            } else if (floor == 3) {
                return 67500;
            } else if (floor == 4) {
                return 75000;
            } else if (floor == 5) {
                return 82500;
            } else if (floor == 6) {
                return 105000;
            } else if (floor == 7) {
                return 120000;
            }
        }
        return 0;
    }
}