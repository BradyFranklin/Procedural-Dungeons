package org.ninenetwork.infinitedungeons.item.tool;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.Messenger;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.menu.tool.Tool;
import org.mineacademy.fo.remain.CompMaterial;
import org.ninenetwork.infinitedungeons.PlayerCache;
import org.ninenetwork.infinitedungeons.dungeon.Dungeon;
import org.ninenetwork.infinitedungeons.dungeon.DungeonRoom;
import org.ninenetwork.infinitedungeons.settings.Settings;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SecretTool extends DungeonTool {

    @Getter
    private static final SecretTool instance = new SecretTool();

    @Override
    public ItemStack getItem() {
        return ItemCreator.of(CompMaterial.CHEST,
                        "&6SecretCreator")
                .lore("&7Click on a chest to add",
                        "&7a secret to the room.")
                .glow(true)
                .make();
    }

    @Override
    protected CompMaterial getBlockMask(Block block, Player player) {
        return CompMaterial.CHEST;
    }

    @Override
    protected void onSuccessfulClick(Player player, Dungeon dungeon, Block block, ClickType click) {

        if (!(player.getWorld().getName().equals(Settings.PluginServerSettings.DUNGEON_WORLD_NAME))) {
            Messenger.error(player, "You can only use this in the dungeons world.");
        }

        final Location location = block.getLocation();
        PlayerCache cache = PlayerCache.from(player);
        DungeonRoom dungeonRoom = DungeonRoom.findByName(cache.getDungeonRoomEditing());
        if (!dungeonRoom.getSecrets().contains(location)) {
            location.setZ(location.getZ());
            location.setY(location.getY() + 1);
            location.getBlock().setType(Material.CHEST);
            dungeonRoom.addSecret(location);
            Messenger.success(player, "Successfully added secret to dungeon room: " + dungeonRoom.getName());
        } else {
            Messenger.error(player, "There is already a secret here for this room");
        }
    }

}