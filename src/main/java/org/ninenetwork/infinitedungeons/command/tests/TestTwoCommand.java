package org.ninenetwork.infinitedungeons.command.tests;

import org.bukkit.entity.Player;
import org.mineacademy.fo.command.SimpleCommand;
import org.ninenetwork.infinitedungeons.PlayerCache;
import org.ninenetwork.infinitedungeons.menu.ToolsMenu;

public class TestTwoCommand extends SimpleCommand {

    public TestTwoCommand() {
        super("testing");
    }

    @Override
    protected void onCommand() {
        checkConsole();
        if (sender instanceof Player) {
            Player player = (Player) sender;
            PlayerCache cache = PlayerCache.from(player);
            new ToolsMenu().displayTo(player);
            //player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(40);
            //player.setHealth(40.0);
            //player.setHealthScaled(true);
            //player.setHealthScale(40.0);
            //DungeonMiniBoss.createNPC(player);
            //DungeonPartyOld.deleteDungeonParty(player);
            //cache.setInParty(false);
            //cache.setCurrentDungeonMode(null);
            //cache.setCurrentDungeonName(null);
        }
    }

}