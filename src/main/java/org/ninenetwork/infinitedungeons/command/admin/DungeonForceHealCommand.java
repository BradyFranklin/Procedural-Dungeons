package org.ninenetwork.infinitedungeons.command.admin;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.command.SimpleCommand;
import org.ninenetwork.infinitedungeons.PlayerCache;

public class DungeonForceHealCommand extends SimpleCommand {

    public DungeonForceHealCommand() {
        super("dheal");
        setMinArguments(1);
    }

    @Override
    protected void onCommand() {
        Player player = (Player) sender;
        PlayerCache cache = PlayerCache.from(player);
        Player target = Bukkit.getPlayer(args[0]);
        if (target != null) {
            PlayerCache cache2 = PlayerCache.from(target);
            cache.setActiveHealth(cache.getActiveMaxHealth());
            Common.tell(player, "Health scale returns " + player.getHealthScale());
            player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        }
    }

}