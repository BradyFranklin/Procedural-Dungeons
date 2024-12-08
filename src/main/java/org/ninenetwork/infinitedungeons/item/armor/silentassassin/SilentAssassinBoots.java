package org.ninenetwork.infinitedungeons.item.armor.silentassassin;

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
import org.ninenetwork.infinitedungeons.util.GeneralUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public class SilentAssassinBoots extends AbstractDungeonItem {

    public SilentAssassinBoots(SimplePlugin plugin) {
        super(plugin, "SilentAssassinBoots", Material.LEATHER_BOOTS);
    }

    @Override
    protected ItemStack generateItem(ItemStack item) {
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
        meta.setColor(Color.fromRGB(0,0,0));
        String name = ChatColor.translateAlternateColorCodes('&', "&5Silent Assassin Boots");
        meta.setDisplayName(name);

        meta.setLore(ItemLoreGenerator.dungeonItemBaseLoreGenerator(SilentAssassinBoots.class, item));
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_DYE, ItemFlag.HIDE_ARMOR_TRIM, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);

        item.setItemMeta(meta);
        CompMetadata.setMetadata(item, "DungeonItem", "SilentAssassinBoots");
        return item;
    }

    @Override
    public LinkedHashMap<PlayerStat, Double> itemStatData() {
        LinkedHashMap<PlayerStat, Double> statData = new LinkedHashMap<>();
        statData.put(PlayerStat.HEALTH, 125.0);
        statData.put(PlayerStat.DEFENSE, 55.0);
        statData.put(PlayerStat.STRENGTH, 25.0);
        statData.put(PlayerStat.SPEED, 7.0);
        statData.put(PlayerStat.CRIT_DAMAGE, 25.0);
        return statData;
    }

    @Override
    public List<String> itemLoreDescription() {
        return Arrays.asList("&7On teleport: Grants &c+10" + GeneralUtils.getStatSymbol(PlayerStat.STRENGTH), "&cStrength &7for &b10 &7seconds.");
    }

    @Override
    public String itemRarityDescription() {
        return "&5&l&k% &5&lEPIC DUNGEON BOOTS &5&l&k%";
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
        return Arrays.asList("&6Full Set Bonus: Silent Assassin", "&7Collect the souls of the", "&7enemies you kill increasing your", "&7damage for the rest of the",
                "&7dungeon while wearing this set.", "&c+1" + GeneralUtils.getStatSymbol(PlayerStat.STRENGTH) + " Strength &7every kill.");
    }

}