package org.ninenetwork.infinitedungeons.item.weapons;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.mineacademy.fo.plugin.SimplePlugin;

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

    public ItemStack getItem(Player player) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(new NamespacedKey(plugin, "ITEM_ID"), PersistentDataType.STRING, itemID);

        item.setItemMeta(meta);
        return generateItem(item, player);
    }

    protected abstract ItemStack generateItem(ItemStack item, Player player);

}