package org.ninenetwork.infinitedungeons.enchant;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.mineacademy.fo.MathUtil;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.enchant.SimpleEnchantment;
import org.mineacademy.fo.enchant.SimpleEnchantmentTarget;
import org.ninenetwork.infinitedungeons.PlayerCache;
import org.ninenetwork.infinitedungeons.util.GeneralUtils;

@AutoRegister(hideIncompatibilityWarnings = true)
public final class Replenish extends SimpleEnchantment {

    @Getter
    private static final Replenish instance = new Replenish();

    private Replenish() {
        super("Replenish", 4);
    }

    private void applyEffects(Player player, int level) {
        PlayerCache cache = PlayerCache.from(player);
        cache.setActiveHealthRegen(cache.getActiveHealthRegen() + (level * 5));
    }

    private void removeEffects(Player player, int level) {
        PlayerCache cache = PlayerCache.from(player);
        cache.setActiveHealthRegen(cache.getActiveHealthRegen() - (level * 5));
    }

    @Override
    public String getLore(int level) {
        if (level == 5) {
            return GeneralUtils.formatStringWithRainbowGradient("Replenish V", false);
        } else {
            return "&1Replenish " + MathUtil.toRoman(level);
        }
    }

    @Override
    public SimpleEnchantmentTarget getTarget() {
            return SimpleEnchantmentTarget.ARMOR;
    }

}