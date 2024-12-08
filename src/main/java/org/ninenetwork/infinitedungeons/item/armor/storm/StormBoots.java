package org.ninenetwork.infinitedungeons.item.armor;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.mineacademy.fo.plugin.SimplePlugin;
import org.mineacademy.fo.remain.CompMetadata;
import org.ninenetwork.infinitedungeons.item.AbstractDungeonItem;
import org.ninenetwork.infinitedungeons.item.ItemLoreGenerator;
import org.ninenetwork.infinitedungeons.item.ItemType;
import org.ninenetwork.infinitedungeons.playerstats.PlayerStat;

import java.util.*;

@Getter
public class StormBoots extends AbstractDungeonItem {

    public StormBoots(SimplePlugin plugin) {
            super(plugin, "StormBoots", Material.LEATHER_BOOTS);
    }

    @Override
    protected ItemStack generateItem(ItemStack item, Player player) {
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
        meta.setColor(Color.fromRGB(28, 212, 228));
        String name = ChatColor.translateAlternateColorCodes('&', "&bStorm Boots &c✪✪✪✪&6✪");
        meta.setDisplayName(name);

        meta.setLore(ItemLoreGenerator.dungeonItemBaseLoreGenerator(StormBoots.class, item));
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_DYE, ItemFlag.HIDE_ARMOR_TRIM, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);

        item.setItemMeta(meta);
        CompMetadata.setMetadata(item, "DungeonItem", "StormBoots");
        return item;
    }

    @Override
    public LinkedHashMap<PlayerStat, Double> itemStatData() {
        LinkedHashMap<PlayerStat, Double> statData = new LinkedHashMap<>();
        statData.put(PlayerStat.HEALTH, 145.0);
        statData.put(PlayerStat.DEFENSE, 65.0);
        statData.put(PlayerStat.INTELLIGENCE, 250.0);
        return statData;
    }

    @Override
    public List<String> itemLoreDescription() {
        return Arrays.asList("&7Reduces the damage you take from", "&7withers by &c10%&7.");
    }

    @Override
    public String itemRarityDescription() {
        return "&d&l&k% &d&lMYTHIC DUNGEON BOOTS &d&l&k%";
    }

    @Override
    protected boolean hasAbilityOrBonus() {
        return true;
    }

    @Override
    protected ItemType getItemType() {
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