package org.ninenetwork.infinitedungeons.command.admin;

import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.command.SimpleCommand;
import org.ninenetwork.infinitedungeons.PlayerCache;
import org.ninenetwork.infinitedungeons.dungeon.Dungeon;
import org.ninenetwork.infinitedungeons.dungeon.DungeonStopReason;
import org.ninenetwork.infinitedungeons.mob.DungeonMobRegistry;

public class ForceStopDungeonCommand extends SimpleCommand {

    public ForceStopDungeonCommand() {
        super("dstop");
        setMinArguments(1);
    }

    @Override
    protected void onCommand() {
        Player player = (Player) sender;
        Dungeon dungeon = Dungeon.findByName(args[0]);
        if (dungeon != null) {
            Common.tell(player, "&c&lDUNGEONS >> &fStopping dungeon...");
            DungeonMobRegistry.getInstance().emergencyClearAllMobs();
            dungeon.stop(DungeonStopReason.COMMAND);
        }

    }

}