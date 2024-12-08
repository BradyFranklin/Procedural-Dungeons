package org.ninenetwork.infinitedungeons.command.admin;

import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.command.SimpleCommand;
import org.ninenetwork.infinitedungeons.PlayerCache;
import org.ninenetwork.infinitedungeons.dungeon.Dungeon;
import org.ninenetwork.infinitedungeons.dungeon.DungeonType;
import org.ninenetwork.infinitedungeons.party.DungeonParty;

public class CreateDungeonCommand extends SimpleCommand {

    public CreateDungeonCommand() {
        super("createdungeon");
        setMinArguments(1);
    }

    @Override
    protected void onCommand() {
        Player player = (Player) sender;
        PlayerCache cache = PlayerCache.from(player);
        if (DungeonParty.isLeader(player, DungeonParty.findPartyByPlayer(player))) {
            Dungeon dungeon = Dungeon.createDungeon(player.getName() + cache.getDungeonsCreated(), DungeonType.CATACOMBS);
            dungeon.initializeDungeonGeneration(dungeon, DungeonParty.findPartyByPlayer(player), DungeonType.CATACOMBS, Integer.parseInt(args[0]));
        } else {
            Common.tell(player, "You must have a party to play dungeons.");
        }
    }

}