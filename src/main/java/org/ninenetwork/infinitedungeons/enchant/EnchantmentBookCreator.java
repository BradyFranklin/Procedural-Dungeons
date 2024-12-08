package org.ninenetwork.infinitedungeons.enchant;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.MathUtil;
import org.mineacademy.fo.enchant.SimpleEnchantment;

public class EnchantmentBookCreator {

    public static ItemStack createEnchantmentBook(SimpleEnchantment enchant, int level, boolean isMax) {
        ItemStack item = new ItemStack(Material.BOOK);
        ItemMeta meta = item.getItemMeta();
        String color = isMax ? "&a" : "&f";
        meta.setDisplayName(Common.colorize(color + enchant.getName() + " " + MathUtil.toRoman(level)));
        item.setItemMeta(meta);
        return item;
    }

}