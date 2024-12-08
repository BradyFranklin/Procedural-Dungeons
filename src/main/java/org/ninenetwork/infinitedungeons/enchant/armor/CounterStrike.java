package org.ninenetwork.infinitedungeons.enchant.armor;

import lombok.Getter;
import org.mineacademy.fo.MathUtil;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.enchant.SimpleEnchantment;
import org.mineacademy.fo.enchant.SimpleEnchantmentTarget;

@AutoRegister(hideIncompatibilityWarnings = true)
public final class CounterStrike extends SimpleEnchantment {

    @Getter
    private static final CounterStrike instance = new CounterStrike();

    private CounterStrike() {
        super("Counter Strike", 5);
    }

    @Override
    public String getLore(int level) {
        if (level == 5) {
            return "&6Quick Strike V";
        } else {
            return "&1Quick Strike " + MathUtil.toRoman(level);
        }
    }

    @Override
    public SimpleEnchantmentTarget getTarget() {
        return SimpleEnchantmentTarget.ARMOR_CHEST;
    }

}