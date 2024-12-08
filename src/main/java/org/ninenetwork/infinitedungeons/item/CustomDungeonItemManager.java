package org.ninenetwork.infinitedungeons.item;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.remain.nbt.NBT;
import org.mineacademy.fo.remain.nbt.ReadWriteNBT;
import org.mineacademy.fo.remain.nbt.ReadableItemNBT;
import org.ninenetwork.infinitedungeons.InfiniteDungeonsPlugin;
import org.ninenetwork.infinitedungeons.item.armor.goldor.GoldorBoots;
import org.ninenetwork.infinitedungeons.item.armor.goldor.GoldorChestplate;
import org.ninenetwork.infinitedungeons.item.armor.goldor.GoldorHelmet;
import org.ninenetwork.infinitedungeons.item.armor.goldor.GoldorLeggings;
import org.ninenetwork.infinitedungeons.item.armor.necron.NecronBoots;
import org.ninenetwork.infinitedungeons.item.armor.necron.NecronChestplate;
import org.ninenetwork.infinitedungeons.item.armor.necron.NecronHelmet;
import org.ninenetwork.infinitedungeons.item.armor.necron.NecronLeggings;
import org.ninenetwork.infinitedungeons.item.armor.silentassassin.SilentAssassinBoots;
import org.ninenetwork.infinitedungeons.item.armor.silentassassin.SilentAssassinChestplate;
import org.ninenetwork.infinitedungeons.item.armor.silentassassin.SilentAssassinHelmet;
import org.ninenetwork.infinitedungeons.item.armor.silentassassin.SilentAssassinLeggings;
import org.ninenetwork.infinitedungeons.item.armor.storm.StormBoots;
import org.ninenetwork.infinitedungeons.item.armor.storm.StormChestplate;
import org.ninenetwork.infinitedungeons.item.armor.storm.StormHelmet;
import org.ninenetwork.infinitedungeons.item.armor.storm.StormLeggings;
import org.ninenetwork.infinitedungeons.item.talisman.DayCrystal;
import org.ninenetwork.infinitedungeons.item.talisman.SpeedTalisman;
import org.ninenetwork.infinitedungeons.item.weapons.Hyperion;
import org.ninenetwork.infinitedungeons.item.weapons.Terminator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class CustomDungeonItemManager {

    private final InfiniteDungeonsPlugin plugin;
    private final Set<AbstractDungeonItem> itemRegistry = new HashSet<>();

    @Getter
    private static final CustomDungeonItemManager instance = new CustomDungeonItemManager(InfiniteDungeonsPlugin.getInstance());

    public CustomDungeonItemManager(InfiniteDungeonsPlugin plugin) {
        this.plugin = plugin;

        registerHandler(new Hyperion(plugin));
        registerHandler(new Terminator(plugin));

        registerHandler(new SpeedTalisman(plugin));
        registerHandler(new DayCrystal(plugin));

        registerHandler(new StormHelmet(plugin));
        registerHandler(new StormChestplate(plugin));
        registerHandler(new StormLeggings(plugin));
        registerHandler(new StormBoots(plugin));

        registerHandler(new NecronHelmet(plugin));
        registerHandler(new NecronChestplate(plugin));
        registerHandler(new NecronLeggings(plugin));
        registerHandler(new NecronBoots(plugin));

        registerHandler(new GoldorHelmet(plugin));
        registerHandler(new GoldorChestplate(plugin));
        registerHandler(new GoldorLeggings(plugin));
        registerHandler(new GoldorBoots(plugin));

        registerHandler(new SilentAssassinHelmet(plugin));
        registerHandler(new SilentAssassinChestplate(plugin));
        registerHandler(new SilentAssassinLeggings(plugin));
        registerHandler(new SilentAssassinBoots(plugin));
    }

    public void registerHandler(AbstractDungeonItem... handlers) {
        for (AbstractDungeonItem handler : handlers) {
            this.registerHandler(handler);
        }
    }

    public void registerHandler(AbstractDungeonItem handler) {
        itemRegistry.add(handler);
        //plugin.registerListener(handler);
    }

    public <T> T getHandler(Class<? extends T> clazz) {
        for (AbstractDungeonItem handler : itemRegistry) {
            if (handler.getClass().equals(clazz)) {
                return (T) handler;
            }
        }
        return null;
    }

    public AbstractDungeonItem getHandler(String itemId) {
        for (AbstractDungeonItem handler : itemRegistry) {
            if (handler.itemID.equals(itemId)) {
                return handler;
            }
        }
        return null;

    }

    public boolean checkIsDungeonItem(ItemStack item) {
        for (AbstractDungeonItem dungeonItem : this.itemRegistry) {
            if (dungeonItem.isApplicable(item)) {
                return true;
            }
        }
        return false;
    }

    public AbstractDungeonItem findDungeonItem(ItemStack item) {
        for (AbstractDungeonItem dungeonItem : this.itemRegistry) {
            if (dungeonItem.isApplicable(item)) {
                return dungeonItem;
            }
        }
        return null;
    }

    public HashMap<String, Double> breakDownNBTStats(ItemStack item, List<String> statTypes, Player player) {
        ReadWriteNBT nbtItem = NBT.itemStackToNBT(item);
        if (statTypes.contains("DungeonStrength")) {
            String strength = NBT.get(item, (Function<ReadableItemNBT, String>) nbt -> nbt.getString("DungeonStrength"));
            Common.tell(player, "Strength " + strength);
            Common.tell(player, "Strength2 " + nbtItem.getDouble("DungeonStrength"));
        }
        if (statTypes.contains("DungeonDamage")) {
            String damage = NBT.get(item, (Function<ReadableItemNBT, String>) nbt -> nbt.getString("DungeonDamage"));
            Common.tell(player, "Damage " + damage);
            Common.tell(player, "Damage2 " + nbtItem.getDouble("DungeonDamage"));
        }
        return null;
    }

}