package org.ninenetwork.infinitedungeons.command.tests;

import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.command.SimpleCommand;
import org.ninenetwork.infinitedungeons.party.DungeonParty;

public class TestThreeCommand extends SimpleCommand {

    public TestThreeCommand() {
        super("tester");
    }

    @Override
    protected void onCommand() {
        checkConsole();
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Common.tell(player,"Deleting Party");
            DungeonParty.deleteDungeonParty(player);
        }
    }

}