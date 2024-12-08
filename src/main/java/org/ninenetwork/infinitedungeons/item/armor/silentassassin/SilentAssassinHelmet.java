package org.ninenetwork.infinitedungeons.item.armor.silentassassin;

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
import org.ninenetwork.infinitedungeons.util.GeneralUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public class SilentAssassinHelmet extends AbstractDungeonItem {

    public SilentAssassinHelmet(SimplePlugin plugin) {
        super(plugin, "SilentAssassinHelmet", Material.LEATHER_HELMET);
    }

    @Override
    protected ItemStack generateItem(ItemStack item) {
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        String name = ChatColor.translateAlternateColorCodes('&', "&5Silent Assassin Helmet");
        meta.setDisplayName(name);

        meta.setLore(ItemLoreGenerator.dungeonItemBaseLoreGenerator(SilentAssassinHelmet.class, item));

        item.setItemMeta(meta);
        CompMetadata.setMetadata(item, "DungeonItem", "SilentAssassinHelmet");
        return item;
    }

    @Override
    public LinkedHashMap<PlayerStat, Double> itemStatData() {
        LinkedHashMap<PlayerStat, Double> statData = new LinkedHashMap<>();
        statData.put(PlayerStat.HEALTH, 160.0);
        statData.put(PlayerStat.DEFENSE, 70.0);
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
        return "&5&l&k% &5&lEPIC DUNGEON HELMET &5&l&k%";
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