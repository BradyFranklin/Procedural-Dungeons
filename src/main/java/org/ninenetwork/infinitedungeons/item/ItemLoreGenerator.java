package org.ninenetwork.infinitedungeons.item;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Common;
import org.ninenetwork.infinitedungeons.enchant.DungeonEnchant;
import org.ninenetwork.infinitedungeons.playerstats.PlayerStat;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemLoreGenerator {

    public static ArrayList<String> dungeonItemBaseLoreGenerator(Class<? extends AbstractDungeonItem> clazz, ItemStack item) {
        ArrayList<String> lore = new ArrayList<>();
        LinkedHashMap<PlayerStat, Double> statsList;
        CustomDungeonItemManager itemManager = CustomDungeonItemManager.getInstance();
        ItemType itemType = itemManager.getHandler(clazz).getItemType();

        // Stats - Spacer - Enchants - Spacer - Description - Spacer - Bonuses - Spacer - RarityLevel
        // Before Stats - none
        // Before Enchants &f&2
        // Before Description &2
        //Stats
        if (clazz != null) {
            statsList = itemManager.getHandler(clazz).itemStatData();
            for (PlayerStat stat : statsList.keySet()) {
                lore.add(Common.colorize(getReplacedItemStat(stat, statsList.get(stat))));
            }
            //Spacer 1 and description
            lore.add(Common.colorize("&2"));
            for (String line : itemManager.getHandler(clazz).itemLoreDescription()) {
                lore.add(Common.colorize(line));
            }
            //Spacer 2 and bonus
            if (itemManager.getHandler(clazz).hasAbilityOrBonus()) {
                lore.add(Common.colorize("&2&2"));
                if (itemType == ItemType.ABILITY_WEAPON) {
                    for (String line : itemManager.getHandler(clazz).itemAbilityDescription()) {
                        lore.add(Common.colorize(line));
                    }
                    lore.add(Common.colorize("&8Mana Cost: &3" + itemManager.getHandler(clazz).manaCost()));
                    if (itemManager.getHandler(clazz).cooldownTime() != 0) {
                        lore.add(Common.colorize("&8Cooldown: &a" + itemManager.getHandler(clazz).cooldownTime()));
                    }
                } else if (itemType == ItemType.ARMOR) {
                    for (String line : itemManager.getHandler(clazz).itemFullSetBonus()) {
                        lore.add(Common.colorize(line));
                    }
                }
            }
            //Spacer 3 and adaptive additions
            if (itemManager.getHandler(clazz).adaptiveAdditions() != null) {
                lore.add(Common.colorize("&2&2&2"));
                for (String line : itemManager.getHandler(clazz).adaptiveAdditions()) {
                    lore.add(Common.colorize(line));
                }
            }
            // Spacer 4 and rarity level
            lore.add(Common.colorize("&2&2&2&2"));
            lore.add(Common.colorize(itemManager.getHandler(clazz).itemRarityDescription()));
        }
        return lore;
    }

    public static List<String> talismanItemBaseLoreGenerator(Class<? extends AbstractDungeonItem> clazz, ItemStack item) {
        ArrayList<String> lore = new ArrayList<>();
        LinkedHashMap<PlayerStat, Double> statsList;
        CustomDungeonItemManager itemManager = CustomDungeonItemManager.getInstance();
        ItemType itemType = itemManager.getHandler(clazz).getItemType();
        if (clazz != null) {
            statsList = itemManager.getHandler(clazz).itemStatData();
            for (PlayerStat stat : statsList.keySet()) {
                lore.add(Common.colorize(getReplacedItemStat(stat, statsList.get(stat))));
            }
            if (itemManager.getHandler(clazz).itemLoreDescription() != null) {
                lore.add(Common.colorize("&2"));
                for (String line : itemManager.getHandler(clazz).itemLoreDescription()) {
                    lore.add(Common.colorize(line));
                }
            }
            lore.add(Common.colorize("&2&2"));
            if (itemManager.getHandler(clazz).hasAbilityOrBonus()) {
                for (String line : itemManager.getHandler(clazz).itemAbilityDescription()) {
                    lore.add(Common.colorize(line));
                }
                lore.add(Common.colorize("&2&2&2"));
            }
            lore.add(Common.colorize(itemManager.getHandler(clazz).itemRarityDescription()));
        }
        return lore;
    }

    public static List<String> dungeonItemAddEnchantLoreGenerator(Player player, Class<? extends AbstractDungeonItem> clazz, ItemStack item, DungeonEnchant enchant) {
        CustomDungeonItemManager itemManager = CustomDungeonItemManager.getInstance();
        String descriptionStart = itemManager.getHandler(clazz).itemLoreDescription().get(0);
        List<String> currentLore = item.getItemMeta().getLore();
        int enchantStartIndex = 1;
        for (String s : currentLore) {
            if (s.contains(Common.colorize("&2"))) {
                enchantStartIndex = currentLore.indexOf(s) + 1;
                Common.tell(player, "Found &.2 marker at " + enchantStartIndex);
                break;
            }
        }
        if (currentLore.get(enchantStartIndex).equals(Common.colorize(itemManager.getHandler(clazz).itemLoreDescription().get(0)))) {
            currentLore.add(enchantStartIndex, Common.colorize(enchant.lore));
            currentLore.add(enchantStartIndex + 1, Common.colorize("&f&2"));
        } else {
            int descriptionStartIndex = 0;
            for (String s : currentLore) {
                if (s.equals(Common.colorize(descriptionStart))) {
                    descriptionStartIndex = currentLore.indexOf(Common.colorize(s));
                    break;
                }
            }
            String lineLengthTest = removeColorFormatting(Common.revertColorizing(currentLore.get(descriptionStartIndex - 2)) + ", " + enchant.label);
            String lineChange = currentLore.get(descriptionStartIndex - 2) + ", " + enchant.lore;
            Common.tell(player, lineLengthTest);
            //Common.tell(player, removeColorFormatting(lineChange));`
            if ((lineLengthTest.length() + 2) <= 40) {
                currentLore.set(descriptionStartIndex - 2, Common.colorize(lineChange));
            } else {
                currentLore.set(descriptionStartIndex - 2, Common.colorize(currentLore.get(descriptionStartIndex - 2) + ","));
                currentLore.add(descriptionStartIndex - 1, Common.colorize(enchant.lore));
            }
        }
        return currentLore;
    }

    public static String getReplacedItemStat(PlayerStat stat, double value) {
        String s = stat.statFormat;
        if (s.contains("%")) {
            int first = s.indexOf("%");
            int second = s.indexOf("%", first + 1);
            if (second != -1) {
                String placeholder = s.substring(first + 1, second);
                return s.replace("%" + placeholder + "%", "" + (int) value);
            }
        }
        return s;
    }

    public static ArrayList<String> getEnchants(String itemType) {
        ArrayList<String> enchants = new ArrayList<>();
        if (itemType.equalsIgnoreCase("Hyperion")) {
            enchants.add("&9First Strike IV, &6Critical VI, ");
            enchants.add("&9Cleave V, &9Cubism VI");
        }
        return enchants;
    }

    public static ArrayList<String> getScrolls(String type) {
        ArrayList<String> scrollLore = new ArrayList<>();
        if (type.equalsIgnoreCase("Implosion")) {
            scrollLore.add("&6Ability: Implosion &e&lRIGHT CLICK");
            scrollLore.add("&7Teleport &a10 blocks &7ahead of you");
            scrollLore.add("&7dealing &c100,000 &7damage to all");
            scrollLore.add("&7nearby enemies.");
        }
        if (type.equalsIgnoreCase("Wither Impact")) {
            scrollLore.add("&6Ability: Wither Impact &e&lRIGHT CLICK");
            scrollLore.add("&7Teleport &a10 blocks &7ahead of you");
            scrollLore.add("&7landing on your enemy dealing &c100,521 &7damage");
        }
        return scrollLore;
    }

    public static String removeColorFormatting(String input) {
        String hexPattern = "&#[A-Fa-f0-9]{6}";
        String mcPattern = "&[0-9a-fA-Fk-oK-OrR]";
        Pattern hexRegex = Pattern.compile(hexPattern);
        Pattern mcRegex = Pattern.compile(mcPattern);
        Matcher hexMatcher = hexRegex.matcher(input);
        Matcher mcMatcher = mcRegex.matcher(input);
        String result = hexMatcher.replaceAll("").trim();
        result = mcMatcher.replaceAll("").trim();
        return result;
    }

}