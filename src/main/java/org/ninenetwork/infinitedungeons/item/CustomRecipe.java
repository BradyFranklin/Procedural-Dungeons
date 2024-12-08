package org.ninenetwork.infinitedungeons.item;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ShapedRecipe;
import org.ninenetwork.infinitedungeons.InfiniteDungeonsPlugin;
import org.ninenetwork.infinitedungeons.item.talisman.SpeedTalisman;

import java.util.ArrayList;
import java.util.List;

public class CustomRecipe {

    public static List<ShapedRecipe> getRecipes() {
        List<ShapedRecipe> recipes = new ArrayList<>();
        NamespacedKey key = new NamespacedKey(InfiniteDungeonsPlugin.getInstance(), "InfiniteDungeons");
        ShapedRecipe speedTalisman = new ShapedRecipe(key, CustomDungeonItemManager.getInstance().getHandler(SpeedTalisman.class).getItem(true, "http://textures.minecraft.net/texture/f06706eecb2d558ace27abda0b0b7b801d36d17dd7a890a9520dbe522374f8a6"));
        speedTalisman.shape("***", "***", "***");
        speedTalisman.setIngredient('*', Material.SUGAR_CANE);
        recipes.add(speedTalisman);
        return recipes;
    }

}