package org.ninenetwork.infinitedungeons.command.tests;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.AnimationUtil;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.command.SimpleCommand;
import org.ninenetwork.infinitedungeons.PlayerCache;

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
            Common.tell(player, "Strength " + cache.getActiveStrength());
            Common.tell(player, "Damage " + cache.getActiveDamage());
            Common.tell(player, "CC " + cache.getActiveCritChance());
            Common.tell(player, "CD " + cache.getActiveCritDamage());
            Common.tell(player, "Regen " + cache.getActiveHealthRegen());
            Common.tell(player, "Speed " + cache.getActiveSpeed());
            ItemStack item = player.getEquipment().getItemInMainHand();
            List<String> animatedFrames = AnimationUtil.leftToRightFull("Quick Strike IV", ChatColor.GREEN, ChatColor.AQUA, ChatColor.LIGHT_PURPLE);
            AnimationUtil.animateItemLore(item, 5, animatedFrames, 20, 20);
        }
    }

}
