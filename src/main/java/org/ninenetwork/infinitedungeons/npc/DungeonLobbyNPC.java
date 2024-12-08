package org.ninenetwork.infinitedungeons.npc;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.ninenetwork.infinitedungeons.dungeon.Dungeon;

public class DungeonLobbyNPC {

    public static void createDungeonLobbyNPC(Dungeon dungeon) {

        final NPCRegistry registry = CitizensAPI.getNPCRegistry();

        double addx = 0.0;
        double addz = 0.0;

        String direction = dungeon.getDungeonPathfinding().getDirectionToMove();

        if (direction.equalsIgnoreCase("Move right")) {
            addx = 5.0;
        } else if (direction.equalsIgnoreCase("Move left")) {
            addx = 5.0;
        } else if (direction.equalsIgnoreCase("Move top")) {
            addz = -5.0;
        } else if (direction.equalsIgnoreCase("Move bottom")) {
            addz = 5.0;
        }

        final Location npcLocation = dungeon.getLobbyLocation().clone().add(addx, 1.0, addz);
        final NPC npc = registry.createNPC(EntityType.PLAYER, dungeon.getName());

        npc.spawn(npcLocation);
        npc.getNavigator().setPaused(true);
        npc.faceLocation(dungeon.getLobbyLocation());
        dungeon.setLobbyNPC(npc);

    }

}