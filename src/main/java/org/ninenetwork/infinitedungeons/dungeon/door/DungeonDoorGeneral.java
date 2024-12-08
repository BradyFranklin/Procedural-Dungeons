package org.ninenetwork.infinitedungeons.dungeon.door;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.MinecraftVersion;
import org.mineacademy.fo.menu.model.SkullCreator;
import org.mineacademy.fo.model.SimpleHologramStand;
import org.ninenetwork.infinitedungeons.PlayerCache;
import org.ninenetwork.infinitedungeons.animation.SimpleAnimatedHologram;
import org.ninenetwork.infinitedungeons.dungeon.Dungeon;

import java.util.concurrent.atomic.AtomicBoolean;

public class DungeonDoorGeneral {

    public static DungeonDoorLock checkIsBloodRushDoor(Dungeon dungeon, Block block) {
        for (DungeonDoorLock door : dungeon.getBloodRushDoors()) {
            if (door.getFallBlocks().contains(block)) {
                return door;
            }
        }
        return null;
    }

    public static void runDungeonDoorOpenAttempt(Dungeon dungeon, PlayerCache cache, Block block) {
        DungeonDoorLock door = DungeonDoorGeneral.checkIsBloodRushDoor(dungeon, block);
        if (door != null) {
            if (dungeon.isPlayerHasKey()) {
                door.makeBlocksFall();
                for (PlayerCache caches : dungeon.getPlayerCaches()) {
                    Common.tell(caches.toPlayer(), "&e" + cache.getName() + " &fhas opened a &8Wither &7Door&f!");
                }
                dungeon.setPlayerHasKey(false);
            }
        }
    }

}