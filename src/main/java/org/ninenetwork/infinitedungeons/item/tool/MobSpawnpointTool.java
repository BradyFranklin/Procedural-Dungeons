package org.ninenetwork.infinitedungeons.item.tool;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.Messenger;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;
import org.ninenetwork.infinitedungeons.PlayerCache;
import org.ninenetwork.infinitedungeons.dungeon.Dungeon;
import org.ninenetwork.infinitedungeons.dungeon.DungeonRoom;

// Just finished this tool, now you gotta test it and add it to the menu so you can test it, then change the entire dungeons mob spawning system to work with this

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MobSpawnpointTool extends DungeonTool {

    @Getter
    private static final MobSpawnpointTool instance = new MobSpawnpointTool();

    @Override
    public ItemStack getItem() {
        return ItemCreator.of(CompMaterial.SPAWNER,
                        "Mob Spawnpoint",
                        "",
                        "&7Click to mark a mob",
                        "&7spawnpoint within a room")
                .glow(true)
                .make();
    }

    @Override
    protected CompMaterial getBlockMask(Block block, Player player) {
        return CompMaterial.SPAWNER;
    }

    @Override
    protected String getBlockName(Block block, Player player) {
        return "&8[&eMob Spawnpoint&8]";
    }

    @Override
    protected void onSuccessfulClick(Player player, Dungeon dungeon, Block block, ClickType click) {
        PlayerCache cache = PlayerCache.from(player);
        if (cache.getDungeonRoomEditing() == null) {
            Common.tell(player, "You must have a dungeon room selected to add spawnpoints");
            return;
        }
        DungeonRoom dungeonRoom = DungeonRoom.findByName(cache.getDungeonRoomEditing());
        final Location location = block.getLocation();

        if (!dungeonRoom.getMobSpawnpoints().contains(location)) {
            dungeonRoom.addMobSpawnpoint(location);
            Messenger.success(player, "Mob spawner was added to room: " + dungeonRoom.getName());
        } else {
            Common.tell(player, "This location is already added as a mob spawnpoint in dungeon room: " + dungeonRoom.getName());
        }
    }

}