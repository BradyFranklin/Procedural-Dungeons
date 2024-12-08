package org.ninenetwork.infinitedungeons.item.tool;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.mineacademy.fo.Messenger;
import org.mineacademy.fo.visual.VisualTool;
import org.ninenetwork.infinitedungeons.PlayerCache;
import org.ninenetwork.infinitedungeons.dungeon.Dungeon;

import javax.annotation.Nullable;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class DungeonTool extends VisualTool {

    @Override
    public void handleBlockClick(Player player, ClickType click, Block block) {
        final Dungeon dungeon = PlayerCache.from(player).getCurrentDungeon();
        // Handle parent
        super.handleBlockClick(player, click, block);
        // Post to us
        this.onSuccessfulClick(player, dungeon, block, click);
        // Save data
    }

    @Override
    protected void handleAirClick(Player player, ClickType click) {
        Dungeon dungeon = this.getCurrentDungeon(player);

        if (dungeon != null && dungeon.isEdited())
            this.onSuccessfulAirClick(player, dungeon, click);
    }

    protected void onSuccessfulAirClick(Player player, Dungeon dungeon, ClickType click) {
    }

    protected void onSuccessfulClick(Player player, Dungeon dungeon, Block block, ClickType click) {
    }

    @Override
    protected List<Location> getVisualizedPoints(Player player) {
        final List<Location> points = super.getVisualizedPoints(player);
        final Dungeon dungeon = this.getCurrentDungeon(player);

        final Location point = this.getDungeonPoint(player, dungeon);

        if (point != null)
            points.add(point);

        final List<Location> additionalPoints = this.getDungeonPoints(player, dungeon);

        if (additionalPoints != null)
            points.addAll(additionalPoints);

        return points;
    }

    @Nullable
    protected Location getDungeonPoint(Player player, Dungeon dungeon) {
        return null;
    }

    @Nullable
    protected List<Location> getDungeonPoints(Player player, Dungeon dungeon) {
        return null;
    }

    protected Dungeon getCurrentDungeon(Player player) {
        return PlayerCache.from(player).getCurrentDungeon();
    }

}