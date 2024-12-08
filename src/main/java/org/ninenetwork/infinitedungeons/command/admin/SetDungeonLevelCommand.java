package org.ninenetwork.infinitedungeons.command.admin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.command.SimpleCommand;
import org.ninenetwork.infinitedungeons.PlayerCache;
import org.ninenetwork.infinitedungeons.dungeon.DungeonLeveling;

public class SetDungeonLevelCommand extends SimpleCommand {

    public SetDungeonLevelCommand() {
        super("dsetlevel");
        setMinArguments(3);
        setUsage("<player> <level> <keepExp>");
    }

    @Override
    protected void onCommand() {
        checkConsole();
        if (sender instanceof Player) {
            Player admin = (Player) sender;
            Player player = Bukkit.getPlayer(args[0]);
            int level = Integer.parseInt(args[1]);
            boolean keepExp = Boolean.parseBoolean(args[2]);
            PlayerCache cache = PlayerCache.from(player);
            cache.setDungeonLevel(level);
            if (!keepExp) {
                cache.setDungeonLevelingExp(0);
            } else {
                if (cache.getDungeonLevelingExp() >= DungeonLeveling.getRequiredExpToLevel(level + 1)) {
                    cache.setDungeonLevelingExp(DungeonLeveling.getRequiredExpToLevel(level + 1) - 1);
                }
            }
            Common.tell(admin,"Set" + player.getName() + "'s Dungeon Level To " + level);
        }
    }

}