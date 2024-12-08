package org.ninenetwork.infinitedungeons.command.tests;

import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.command.SimpleCommand;
import org.ninenetwork.infinitedungeons.PlayerCache;
import org.ninenetwork.infinitedungeons.dungeon.DungeonScore;

public class TestDungeonScoreCommand extends SimpleCommand {

    public TestDungeonScoreCommand() {
        super("dscore");
    }

    @Override
    protected void onCommand() {
        checkConsole();
        if (sender instanceof Player) {
            Player player = (Player) sender;
            PlayerCache cache = PlayerCache.from(player);
            DungeonScore score = cache.getCurrentDungeon().getDungeonScore();
            Common.tell(player, "Total score: " + score.getDungeonScore());
            Common.tell(player, "Explore score: " + score.getExploreScore());
            Common.tell(player, "Speed score: " + score.getSpeedScore());
            Common.tell(player, "Skill score: " + score.getSkillScore());
            Common.tell(player, "Cleared Rooms: " + score.getClearedRooms());
            Common.tell(player, "Secrets Found: " + score.getSecretsFound());
            Common.tell(player, "Total Secrets: " + score.getTotalSecrets());
        }
    }

}