package org.ninenetwork.infinitedungeons.item.armor.goldor;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.mineacademy.fo.plugin.SimplePlugin;
import org.mineacademy.fo.remain.CompMetadata;
import org.ninenetwork.infinitedungeons.item.AbstractDungeonItem;
import org.ninenetwork.infinitedungeons.item.ItemLoreGenerator;
import org.ninenetwork.infinitedungeons.item.ItemType;
import org.ninenetwork.infinitedungeons.playerstats.PlayerStat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public class GoldorLeggings extends AbstractDungeonItem {

    public GoldorLeggings(SimplePlugin plugin) {
        super(plugin, "GoldorLeggings", Material.LEATHER_LEGGINGS);
    }

    @Override
    protected ItemStack generateItem(ItemStack item) {
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
        meta.setColor(Color.fromRGB(101, 96, 90));
        String name = ChatColor.translateAlternateColorCodes('&', "&dGoldor's Leggings");
        meta.setDisplayName(name);

        meta.setLore(ItemLoreGenerator.dungeonItemBaseLoreGenerator(GoldorLeggings.class, item));
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_DYE, ItemFlag.HIDE_ARMOR_TRIM, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);

        item.setItemMeta(meta);
        CompMetadata.setMetadata(item, "DungeonItem", "GoldorLeggings");
        return item;
    }

    @Override
    public LinkedHashMap<PlayerStat, Double> itemStatData() {
        LinkedHashMap<PlayerStat, Double> statData = new LinkedHashMap<>();
        statData.put(PlayerStat.HEALTH, 250.0);
        statData.put(PlayerStat.DEFENSE, 220.0);
        statData.put(PlayerStat.INTELLIGENCE, 20.0);
        return statData;
    }

    @Override
    public List<String> itemLoreDescription() {
        return Arrays.asList("&7Reduces the damage you take from", "&7withers by &c10%&7.");
    }

    @Override
    public String itemRarityDescription() {
        return "&d&l&k% &d&lMYTHIC DUNGEON LEGGINGS &d&l&k%";
    }

    @Override
    protected boolean hasAbilityOrBonus() {
        return true;
    }

    @Override
    public ItemType getItemType() {
        return ItemType.ARMOR;
    }

    @Override
    protected ArrayList<String> adaptiveAdditions() {
        return null;
    }

    @Override
    public List<String> itemFullSetBonus() {
        return Arrays.asList("&6Full Set Bonus: Witherborn", "&7Spawns a wither minion every", "&e30 &7seconds up to a maximum", "&a1 &7wither. Your withers will",
                "&7travel to and explode on nearby", "&7enemies");
    }

}