package org.ninenetwork.infinitedungeons.command;

import org.bukkit.event.inventory.ClickType;
import org.ninenetwork.infinitedungeons.item.tool.RegionTool;

import java.util.List;

final class RoomRegionCommand extends DungeonSubCommand {

    public RoomRegionCommand() {
        super("setregionpoint/srp");

        this.setMinArguments(1);
        this.setUsage("<primary/secondary>");
        this.setDescription("Sets a region point in air while editing a game.");
    }

    @Override
    protected void onCommand() {
        this.checkConsole();

        final ClickType clickType;

        switch (args[0]) {
            case "primary":
                clickType = ClickType.LEFT;
                break;

            case "secondary":
                clickType = ClickType.RIGHT;
                break;

            default: {
                returnTell("Unrecognized click '{0}', either type 'primary' or 'secondary'.");

                return;
            }
        }

        RegionTool.getInstance().handleBlockClick(getPlayer(), clickType, getPlayer().getLocation().getBlock());
    }

    @Override
    protected List<String> tabComplete() {
        return this.args.length == 1 ? completeLastWord("primary", "secondary") : NO_COMPLETE;
    }

}