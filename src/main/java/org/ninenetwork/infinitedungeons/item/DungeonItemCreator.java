package org.ninenetwork.infinitedungeons.item;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.mineacademy.fo.remain.CompMetadata;
import org.ninenetwork.infinitedungeons.settings.Settings;

import java.util.ArrayList;
import java.util.List;

public class DungeonItemCreator {

    public static ItemStack createWeapon(String name) {
        ItemStack item = null;
        ItemMeta meta = null;
        ArrayList<String> lore = new ArrayList<>();
        if (name.equalsIgnoreCase("Hyperion")) {
            item = new ItemStack(Material.IRON_SWORD, 1);
            meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Settings.Items.HYPERIONNAME));
            for (String s : createLore(name, Settings.Items.HYPERIONLORE)) {
                lore.add(ChatColor.translateAlternateColorCodes('&', s));
            }
            CompMetadata.setMetadata(item,"WitherScroll","false");
            CompMetadata.setMetadata(item,"ImpactScroll","false");
            CompMetadata.setMetadata(item,"TestScroll","false");
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public static List<String> createLore(String itemType, List<String> lore) {
        for (String s : lore) {
            if (s.contains("%")) {
                int first = s.indexOf("%");
                if (s.length() >= first++) {
                    int second = s.indexOf("%", first);
                    //s = replaceSettingPlaceholder(itemType, s, s.substring(first - 1, second + 1));
                    lore.addAll(replaceSettingPlaceholder(itemType, s, s.substring(first - 1, second + 1)));
                } else {
                    lore.add(s);
                }
            } else {
                lore.add(s);
            }
        }
        return lore;
    }

    public static ArrayList<String> replaceSettingPlaceholder(String itemType, String loreLine, String placeholder) {
        ArrayList<String> lore = new ArrayList<>();
        if (placeholder.equalsIgnoreCase("%scrolls%")) {
            return getScrolls("Wither Impact");
        } else if (placeholder.equalsIgnoreCase("%enchants%")) {
            return getEnchants("Test");
        } else {
            lore.add(loreLine.replace(placeholder, "+" + getItemStatBaseValue(itemType, placeholder)));
        }
        return lore;
    }

    public static int getItemStatBaseValue(String itemType, String stat) {
        if (itemType.equalsIgnoreCase("Hyperion")) {
            if (stat.equalsIgnoreCase("%damage%")) {
                return 260;
            } else if (stat.equalsIgnoreCase("%strength%")) {
                return 150;
            } else if (stat.equalsIgnoreCase("%intelligence%")) {
                return 350;
            } else if (stat.equalsIgnoreCase("%ferocity%")) {
                return 30;
            } else if (stat.equalsIgnoreCase("%manacost%")) {
                return 210;
            }
        }
        return 0;
    }

    public static ArrayList<String> getEnchants(String itemType) {
        ArrayList<String> enchants = new ArrayList<>();
        if (itemType.equalsIgnoreCase("Test")) {
            enchants.add("&1First Strike IV, &6Critical VI, ");
            enchants.add("&1Cleave V, &1Cubism VI, ");
        }
        return enchants;
    }

    public static ArrayList<String> getScrolls(String type) {
        ArrayList<String> scrollLore = new ArrayList<>();
        if (type.equalsIgnoreCase("Wither Impact")) {
            scrollLore.add("&6Ability: Wither Impact &e&lRIGHT CLICK");
            scrollLore.add("&7Teleport &a10 blocks &7ahead of you");
            scrollLore.add("&7landing on your enemy dealing &c100,521 &7damage");
        }
        return scrollLore;
    }

}