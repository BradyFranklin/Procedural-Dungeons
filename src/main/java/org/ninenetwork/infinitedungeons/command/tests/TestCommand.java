package org.ninenetwork.infinitedungeons.command.tests;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.ReflectionUtil;
import org.mineacademy.fo.command.SimpleCommand;
import org.ninenetwork.infinitedungeons.PlayerCache;
import org.ninenetwork.infinitedungeons.dungeon.*;
import org.ninenetwork.infinitedungeons.party.DungeonParty;

import java.util.ArrayList;
import java.util.List;

public class TestCommand extends SimpleCommand {

    public TestCommand() {
        super("test");
    }

    @Override
    protected void onCommand() {
        checkConsole();
        if (sender instanceof Player) {
            Player player = (Player) sender;
            PlayerCache cache = PlayerCache.from(player);
            Common.tell(player,"Creating Party");
            ArrayList<String> players = new ArrayList<>();
            players.add(player.getName());
            ArrayList<String> playerss = new ArrayList<>();
            playerss.add("InfinitySB");
            playerss.add("creeperdown02");
            DungeonParty party2 = DungeonParty.loadOrCreateDungeonParty("InfinitySB", "CATACOMBS",7, "34");
            party2.setCurrentMembers(playerss);
            new ToolsMenu().displayTo(player);
            if (cache.isInParty()) {
                DungeonParty.deleteDungeonParty(player);
                cache.setInParty(false);
            }

            //dungeon.setDungeonRooms(rooms);

        }
    }

}
