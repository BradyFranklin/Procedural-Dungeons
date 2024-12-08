package org.ninenetwork.infinitedungeons.dungeon.secret;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Messenger;
import org.ninenetwork.infinitedungeons.dungeon.Dungeon;
import org.ninenetwork.infinitedungeons.dungeon.instance.DungeonRoomInstance;

import java.util.List;
import java.util.Set;

public class SecretManager {

    public static boolean handleSecretClickAttempt(Dungeon dungeon, Player player, DungeonRoomInstance dungeonRoomInstance, Block block) {
        if (dungeonRoomInstance != null) {
            //List<Location> locations = dungeonRoomInstance.getSecrets();
            Set<Location> locations = dungeonRoomInstance.getSecretMap().keySet();
            if (!(locations == null) && !locations.isEmpty()) {
                if (locations.contains(block.getLocation())) {
                    if (dungeonRoomInstance.getSecretMap().containsKey(block.getLocation())) {
                        if (!dungeonRoomInstance.getSecretMap().get(block.getLocation())) {
                            dungeon.getDungeonSecretInstance().handleSecret(player, block);
                            dungeonRoomInstance.setSecretFound(block.getLocation());
                            return true;
                        } else {
                            Messenger.error(player, "Secret already found.");
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

}