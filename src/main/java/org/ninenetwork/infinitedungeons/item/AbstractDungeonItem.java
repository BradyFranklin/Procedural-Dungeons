package org.ninenetwork.infinitedungeons.item;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.mineacademy.fo.plugin.SimplePlugin;
import org.ninenetwork.infinitedungeons.playerstats.PlayerStat;
import org.ninenetwork.infinitedungeons.util.SkullCreator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public abstract class AbstractDungeonItem implements Listener {

    protected final SimplePlugin plugin;
    public final String itemID;
    public final Material material;

    public AbstractDungeonItem(SimplePlugin plugin, String itemID, Material material) {
        this.plugin = plugin;
        this.itemID = itemID;
        this.material = material;
    }

    public boolean isApplicable(ItemStack item) {
        if (item == null) {
            return false;
        }
        if (!item.hasItemMeta()) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        String pdcId = pdc.get(new NamespacedKey(plugin, "ITEM_ID"), PersistentDataType.STRING);
        return itemID.equals(pdcId);
    }

    public ItemStack getItem(boolean skull, String url) {
        ItemStack item;
        ItemMeta meta;
        if (skull) {
            item = SkullCreator.createSkullItem(url);
        } else {
            item = new ItemStack(material);
        }
        meta = item.getItemMeta();

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(new NamespacedKey(plugin, "ITEM_ID"), PersistentDataType.STRING, itemID);

        item.setItemMeta(meta);
        return generateItem(item);
    }

    protected abstract ItemStack generateItem(ItemStack item);

    protected abstract boolean hasAbilityOrBonus();

    public abstract ItemType getItemType();

    protected abstract ArrayList<String> adaptiveAdditions();

    public LinkedHashMap<PlayerStat, Double> itemStatData() {
        return null;
    }

    public List<String> itemLoreDescription() {
        return Arrays.asList("");
    }

    public String itemRarityDescription() {
        return null;
    }

    public List<String> itemFullSetBonus() {
        return null;
    }

    public List<String> itemAbilityDescription() {
        return null;
    }

    public int cooldownTime() {
        return 0;
    }

    public int manaCost() {
        return 0;
    }

    public double abilityDamage() {
        return 0;
    }

    public double abilityScaling() {
        return 0;
    }

}