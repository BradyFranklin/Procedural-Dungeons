package org.ninenetwork.infinitedungeons.item.armor.necron;

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
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public class NecronHelmet extends AbstractDungeonItem {

    public NecronHelmet(SimplePlugin plugin) {
        super(plugin, "NecronHelmet", Material.LEATHER_HELMET);
    }

    @Override
    protected ItemStack generateItem(ItemStack item) {
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        String name = ChatColor.translateAlternateColorCodes('&', "&dNecron's Helmet");
        meta.setDisplayName(name);

        meta.setLore(ItemLoreGenerator.dungeonItemBaseLoreGenerator(NecronHelmet.class, item));

        item.setItemMeta(meta);
        CompMetadata.setMetadata(item, "DungeonItem", "NecronHelmet");
        return item;
    }

    @Override
    public LinkedHashMap<PlayerStat, Double> itemStatData() {
        LinkedHashMap<PlayerStat, Double> statData = new LinkedHashMap<>();
        statData.put(PlayerStat.HEALTH, 180.0);
        statData.put(PlayerStat.DEFENSE, 100.0);
        statData.put(PlayerStat.STRENGTH, 40.0);
        statData.put(PlayerStat.CRIT_DAMAGE, 30.0);
        statData.put(PlayerStat.INTELLIGENCE, 10.0);
        return statData;
    }

    @Override
    public List<String> itemLoreDescription() {
        return Arrays.asList("&7Reduces the damage you take from", "&7withers by &c10%&7.");
    }

    @Override
    public String itemRarityDescription() {
        return "&d&l&k% &d&lMYTHIC DUNGEON HELMET &d&l&k%";
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