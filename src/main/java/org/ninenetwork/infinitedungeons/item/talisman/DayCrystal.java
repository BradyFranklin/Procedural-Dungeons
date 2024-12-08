package org.ninenetwork.infinitedungeons.item.talisman;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.mineacademy.fo.plugin.SimplePlugin;
import org.mineacademy.fo.remain.CompMetadata;
import org.ninenetwork.infinitedungeons.item.AbstractDungeonItem;
import org.ninenetwork.infinitedungeons.item.ItemLoreGenerator;
import org.ninenetwork.infinitedungeons.item.ItemType;
import org.ninenetwork.infinitedungeons.playerstats.PlayerStat;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class DayCrystal extends AbstractDungeonItem {

    public DayCrystal(SimplePlugin plugin) {
        super(plugin, "DayCrystal", Material.NETHER_STAR);
    }

    @Override
    protected ItemStack generateItem(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        String name = ChatColor.translateAlternateColorCodes('&', "&5Day Crystal");
        meta.setDisplayName(name);
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ENCHANTS);

        meta.setLore(ItemLoreGenerator.talismanItemBaseLoreGenerator(DayCrystal.class, item));

        item.setItemMeta(meta);
        CompMetadata.setMetadata(item, "DungeonItem", "DayCrystal");
        return item;
    }

    @Override
    public LinkedHashMap<PlayerStat, Double> itemStatData() {
        LinkedHashMap<PlayerStat, Double> statData = new LinkedHashMap<>();
        statData.put(PlayerStat.STRENGTH, 5.0);
        statData.put(PlayerStat.DEFENSE, 5.0);
        return statData;
    }

    @Override
    public List<String> itemLoreDescription() {
        return null;
    }

    @Override
    public String itemRarityDescription() {
        return "&5&lRARE ACCESSORY";
    }

    @Override
    protected boolean hasAbilityOrBonus() {
        return false;
    }

    @Override
    public ItemType getItemType() {
        return ItemType.TALISMAN;
    }

    @Override
    protected ArrayList<String> adaptiveAdditions() {
        return null;
    }

}