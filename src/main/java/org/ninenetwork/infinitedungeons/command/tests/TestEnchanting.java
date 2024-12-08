package org.ninenetwork.infinitedungeons.command.tests;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.command.SimpleCommand;
import org.mineacademy.fo.enchant.SimpleEnchantment;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.ninenetwork.infinitedungeons.PlayerCache;
import org.ninenetwork.infinitedungeons.enchant.DungeonEnchant;
import org.ninenetwork.infinitedungeons.enchant.QuickStrike;
import org.ninenetwork.infinitedungeons.item.CustomDungeonItemManager;
import org.ninenetwork.infinitedungeons.item.ItemLoreGenerator;

import java.util.Map;

public class TestEnchanting extends SimpleCommand {

    public TestEnchanting() {
        super("denchant");
        setMinArguments(1);
    }

    @Override
    protected void onCommand() {
        checkConsole();
        if (sender instanceof Player) {
            Player player = (Player) sender;
            PlayerCache cache = PlayerCache.from(player);
            DungeonEnchant ench = DungeonEnchant.findEnchantByLabel(args[0]);
            if (ench != null) {
                ItemStack item = player.getEquipment().getItemInMainHand();

                if (item != null) {
                    for (Map.Entry<SimpleEnchantment, Integer> entry : SimpleEnchantment.findEnchantments(item).entrySet()) {
                        Common.tell(player, entry.getKey() + " Enchant at level " + entry.getValue());
                    }
                }

                ItemCreator.of(item).enchant(QuickStrike.getInstance(), 4);

                ItemMeta meta = item.getItemMeta();
                meta.setLore(ItemLoreGenerator.dungeonItemAddEnchantLoreGenerator(player, CustomDungeonItemManager.getInstance().findDungeonItem(item).getClass(), item, ench));
                item.setItemMeta(meta);
                
                /*
                if (ench == DungeonEnchant.QUICK_STRIKE4) {
                    item.addEnchantment(QuickStrike.getInstance().toBukkit(), 4);
                    ItemMeta meta = item.getItemMeta();
                    meta.setLore(ItemLoreGenerator.dungeonItemAddEnchantLoreGenerator(player, CustomDungeonItemManager.getInstance().findDungeonItem(item).getClass(), item, DungeonEnchant.QUICK_STRIKE4));
                    item.setItemMeta(meta);
                } else {
                    item.addEnchantment(Replenish.getInstance().toBukkit(), 5);
                    ItemMeta meta = item.getItemMeta();
                    meta.setLore(ItemLoreGenerator.dungeonItemAddEnchantLoreGenerator(player, CustomDungeonItemManager.getInstance().findDungeonItem(item).getClass(), item, ench));
                    item.setItemMeta(meta);
                }

                */
            }
        }
    }

}