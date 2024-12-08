package org.ninenetwork.infinitedungeons.item.talisman;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.mineacademy.fo.plugin.SimplePlugin;
import org.mineacademy.fo.remain.CompMetadata;
import org.ninenetwork.infinitedungeons.item.AbstractDungeonItem;
import org.ninenetwork.infinitedungeons.item.ItemLoreGenerator;
import org.ninenetwork.infinitedungeons.item.ItemType;
import org.ninenetwork.infinitedungeons.playerstats.PlayerStat;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class SpeedTalisman extends AbstractDungeonItem {

    public SpeedTalisman(SimplePlugin plugin) {
        super(plugin, "SpeedTalisman", Material.PLAYER_HEAD);
    }

    @Override
    protected ItemStack generateItem(ItemStack item) {
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        String name = ChatColor.translateAlternateColorCodes('&', "&fSpeed Talisman");
        meta.setDisplayName(name);

        meta.setLore(ItemLoreGenerator.talismanItemBaseLoreGenerator(SpeedTalisman.class, item));

        item.setItemMeta(meta);
        CompMetadata.setMetadata(item, "DungeonItem", "SpeedTalisman");
        return item;
    }

    @Override
    public LinkedHashMap<PlayerStat, Double> itemStatData() {
        LinkedHashMap<PlayerStat, Double> statData = new LinkedHashMap<>();
        statData.put(PlayerStat.SPEED, 5.0);
        return statData;
    }

    @Override
    public List<String> itemLoreDescription() {
        return null;
    }

    @Override
    public String itemRarityDescription() {
        return "&f&lCOMMON ACCESSORY";
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