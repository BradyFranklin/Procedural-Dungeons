package org.ninenetwork.infinitedungeons.item.tool;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.Messenger;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.menu.model.SkullCreator;
import org.mineacademy.fo.remain.CompMaterial;
import org.ninenetwork.infinitedungeons.PlayerCache;
import org.ninenetwork.infinitedungeons.dungeon.Dungeon;
import org.ninenetwork.infinitedungeons.dungeon.DungeonRoom;
import org.ninenetwork.infinitedungeons.settings.Settings;

import java.util.Arrays;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EssenceSecretTool extends DungeonTool {

    @Getter
    private static final EssenceSecretTool instance = new EssenceSecretTool();

    @Override
    public ItemStack getItem() {
        ItemStack item = SkullCreator.itemFromUrl("http://textures.minecraft.net/texture/d742f0a79672be1490889e730cfe1f5c42b6862727ff1815824681e45eb1f6da");
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setDisplayName("&6SecretCreator - Essence");
        meta.setLore(Arrays.asList(Common.colorize("&7Click to add an essence"), Common.colorize("&7secret to the room.")));
        item.setItemMeta(meta);
        return item;
    }

    @Override
    protected CompMaterial getBlockMask(Block block, Player player) {
        return CompMaterial.PLAYER_HEAD;
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
            location.getBlock().setType(this.getItem().getType());
            dungeonRoom.addSecret(location);
            Messenger.success(player, "Successfully added essence secret to dungeon room: " + dungeonRoom.getName());
        } else {
            Messenger.error(player, "There is already a secret here for this room");
        }
    }
}