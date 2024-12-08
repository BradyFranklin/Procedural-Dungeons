package org.ninenetwork.infinitedungeons.util;

import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import me.filoghost.holographicdisplays.api.hologram.VisibilitySettings;
import me.filoghost.holographicdisplays.api.hologram.line.ItemHologramLine;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Common;
import org.ninenetwork.infinitedungeons.InfiniteDungeonsPlugin;
import org.ninenetwork.infinitedungeons.util.GeneralUtils;

import java.text.NumberFormat;
import java.util.Random;

public class HitHologram {

    public static void createHitHologram(Player player, LivingEntity mob, double damage) {
        HolographicDisplaysAPI api = HolographicDisplaysAPI.get(InfiniteDungeonsPlugin.getInstance());
        double heightModifier = ((double) new Random().nextInt(7) / 10);
        Hologram hologram = api.createHologram(mob.getLocation().clone().add(1, 1.5 + heightModifier, 0));
        //String text = ChatColor.translateAlternateColorCodes('&', GeneralUtils.rainbow(String.format("%,d", damageDone)));
        String text = Common.colorize(GeneralUtils.rainbow(String.format("%,d", (int) damage)));
        //ItemHologramLine itemLine = hologram.getLines().insertItem(0, new ItemStack(Material.BEDROCK));
        //itemLine.setItemStack(player.getEquipment().getItemInMainHand());
        hologram.getLines().appendText(text);
        VisibilitySettings visibilitySettings = hologram.getVisibilitySettings();
        visibilitySettings.setGlobalVisibility(VisibilitySettings.Visibility.HIDDEN);
        visibilitySettings.setIndividualVisibility(player, VisibilitySettings.Visibility.VISIBLE);
        Common.runLater(8, hologram::delete);
    }

}